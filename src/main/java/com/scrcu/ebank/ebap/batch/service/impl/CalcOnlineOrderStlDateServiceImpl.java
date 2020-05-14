package com.scrcu.ebank.ebap.batch.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.CalcOnlineOrderStlDateService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalcOnlineOrderStlDateServiceImpl implements CalcOnlineOrderStlDateService 
{
	private String batchDate;
	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;
	
	@Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           // 商户基本信息
    
    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息
    @Resource
    private PaySubOrderInfoDao paySubOrderInfoDao;     //子订单表
    
	@Override
	public void calcStlDate(BatchRequest request) 
	{
		//是否需要在子订单表加一个通知结算日期（要不然需要全表扫描订单明细表，如果有订单一致没通知结算，入账明细表查询的结果会越来越大）
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>calcStlDate service executing");
		String batchDate = request.getSettleDate();
    	if(IfspDataVerifyUtil.isBlank(batchDate))
    	{
    		batchDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
    	}
    	log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>batch date : " + batchDate);
    	
    	this.setBatchDate(batchDate);
    	int count = this.getTotalResult();
    	
    	List<PaySubOrderInfo> subOrderList = this.getDataList();
    	
		for(PaySubOrderInfo subOrder : subOrderList)
		{
			this.execute(subOrder);
		}
		
		this.updateObject();
	}
	
	public int getTotalResult()
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("noticeTime", this.batchDate);
		params.put("noticeTimeY", DateUtil.format(DateUtil.getBeforeDate(this.batchDate,1), "yyyyMMdd"));
		params.put("autoSettleFlag", Constans.NOTICE_FLAG_YES);    
		count = paySubOrderInfoDao.count("countNoticeOrderOfBatchDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>noticed order count is " + count);
		return count;
	}
	
	public List<PaySubOrderInfo> getDataList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("noticeTime", this.batchDate);
		params.put("autoSettleFlag", Constans.NOTICE_FLAG_YES);
		params.put("noticeTimeY", DateUtil.format(DateUtil.getBeforeDate(this.batchDate,1), "yyyyMMdd"));
		List<PaySubOrderInfo> subOrderList = paySubOrderInfoDao.selectList("selectNoticeOrderOfBatchDate", params);
		return subOrderList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(PaySubOrderInfo subOrder)
	{
		MchtContInfo merStlInfo = this.getMerStlInfo(subOrder.getSubMchtId());
		String stlDate = this.calcStlDate(merStlInfo,subOrder.getNoticeTime());
		//判断计算出的时间是否小于当前时间
		Date noticeDate = DateUtil.parse(stlDate, "yyyyMMdd");
		Date sysDate = DateUtil.parse(DateUtils.getCurrentDate(), "yyyyMMdd");
		if(noticeDate.getTime()<sysDate.getTime()){
			stlDate = DateUtils.getCurrentDate();
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("stlmDate", stlDate);
		params.put("inAcctDate", stlDate);
		params.put("txnSeqId", subOrder.getSubOrderSsn());
		
		int a = bthMerInAccDtlDao.update("updateStlDateByTxnSeqId", params);

		//设置子订单表结算日期
		Map<String,Object> params2 = new HashMap<String,Object>();
		params2.put("settleStatus", Constans.SETTLE_STATUS_NOT_CLEARING);
		params2.put("subOrderSsn", subOrder.getSubOrderSsn());
		params2.put("settleDate", stlDate);

		if(a==1){
			paySubOrderInfoDao.update("updateSubOrderStlStatusBySubOrderId", params2);
		}
	}
	
	public void updateObject()
	{
		log.info(">>>>>>>>>>>>>>>>>>>calc submer settle date complete...");
	}
	
	
	/**
	 * 查询商户结算信息 ：
	 * @param mchtId ： 商户号
	 * @return
	 */
	public MchtContInfo getMerStlInfo(String mchtId) {
		
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("mchtId", mchtId);
		MchtContInfo merStlInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);
		
		return merStlInfo;
	}

	public String getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}
	
	/**
	 * 计算订单结算日期
	 * @param merStlInfo
	 * @return
	 */
	private String calcStlDate(MchtContInfo merStlInfo,String noticeDateStr)
	{
		Date noticeDate = DateUtil.parse(noticeDateStr, "yyyyMMdd");
		Date stlDate = null;
		if(Constans.STL_TYPE_DN.equals(merStlInfo.getSettlCycleType()))
		{
			//TODO:当前都是根据T+N进行结算，后续如果考虑（D+N）节假日不进行结算，需维护节日表
			//但由于收单现有模式是结算失败的会在第二天继续结算，所以非节假日计算很难保证
			stlDate = DateUtil.add(noticeDate, Calendar.DAY_OF_MONTH, merStlInfo.getSettlCycleParam());
		}
		else if (Constans.STL_TYPE_TN.equals(merStlInfo.getSettlCycleType()))
		{
			stlDate = DateUtil.add(noticeDate, Calendar.DAY_OF_MONTH, merStlInfo.getSettlCycleParam());
		}
		else if (Constans.STL_TYPE_BY_MONTH.equals(merStlInfo.getSettlCycleType()))
		{
			int day = merStlInfo.getSettlCycleParam();
			if(day > 30)
			{
				day = 30;
			}
			
			String stlDate2 = DateUtil.getNextMonthDay(day);
			
			return stlDate2;
		}
		else
		{
			stlDate = noticeDate;
		}
		
		return DateUtil.format(stlDate, "yyyyMMdd");
	}

	/**
	 * 检查线上交易是否已通知结算 ：解决因为对账原因导致通知结算后无法更新订单结算日期问题
	 */
	public void check99991230Order()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("stlmDate","99991230");

		bthMerInAccDtlDao.update("updateStlmDateByNoticeStatus",params);
	}

}
