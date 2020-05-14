package com.scrcu.ebank.ebap.batch.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthChkRsltInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.PointpayOrderChkService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;


/**
 * 授信支付对账
 * @author ydl
 *问题描述：
 *授信支付的支付记录未对账线上收单之前都是对明细账，但扫码支付系统的明细账依赖于总账（授信支付无法对总账）
 *解决方案：
 *将支付成功的授信支付订单插入对账成功表：
 */
@Service
@Slf4j
public class PointpayOrderChkServiceImpl implements PointpayOrderChkService 
{
	@Resource
    private PayOrderInfoDao payOrderInfoDao;                 // 订单信息
    
    @Resource
    private PaySubOrderInfoDao paySubOrderInfoDao;           //子订单信息
    
    @Resource
	private BthChkRsltInfoDao bthChkRsltInfoDao;             //对账成功表
    
    private String batchDate;
    
	@Override
	public CommonResponse pointpayOrderChk(BatchRequest request) throws Exception 
	{
		log.info(">>>>>>>>>>>>>>>>>>>>>pointpayOrderChk executing..." );
		
		String batchDate = request.getSettleDate();
    	if(IfspDataVerifyUtil.isBlank(batchDate))
    	{
    		batchDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
    		
    	}
    	this.setBatchDate(batchDate);
    	
    	//删除当日数据以便重跑
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("pagySysNo", Constans.LOAN_SYS_POINT+" ");
		params.put("chkSuccDt", DateUtil.format(new Date(), "yyyyMMdd"));     
		params.put("stlmSt", Constans.SETTLE_STATUS_NOT_CLEARING);   
		
    	bthChkRsltInfoDao.delete("deleteByChkSuccDtAndPagySysNo", params);
    	
    	int count = this.getTotalResult();
    	
    	List<PayOrderInfo> orderList = this.getDataList(0, count);
    	
    	for(PayOrderInfo order : orderList)
    	{
    		this.execute(order);
    	}
    	
		
		//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
        return commonResponse;
	}
	
	private int getTotalResult()
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("transFactTime", this.getBatchDate());
		params.put("fundChannel", Constans.GAINS_CHANNEL_POINT);     //支付渠道： 10-积分支付
		params.put("orderState", Constans.ORDER_STATUS_SUCC);          
		count = paySubOrderInfoDao.count("countPointpayDataByDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>pointpay order count is : " + count);
		return count;
	}
	
	private List<PayOrderInfo> getDataList(int beginIndex,int endIndex)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("transFactTime", this.getBatchDate());
		params.put("fundChannel", Constans.GAINS_CHANNEL_POINT);     //支付渠道： 10-积分支付
		params.put("orderState", Constans.ORDER_STATUS_SUCC);          
		
		List<PayOrderInfo> orderList = payOrderInfoDao.selectList("selectPointpayDataByDate", params);
		
		return orderList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(PayOrderInfo order)
	{
		//初始化清分信息
		BthChkRsltInfo chkRst = this.initChkRsltInfo(order);
		bthChkRsltInfoDao.insert(chkRst);
		
	}
	
	private BthChkRsltInfo initChkRsltInfo(PayOrderInfo order)
	{
		 BthChkRsltInfo record=new BthChkRsltInfo();
		 record.setPagyTxnSsn(order.getOrderSsn());
		 record.setOrderSsn(order.getOrderSsn());
		 record.setPagyPayTxnSsn(order.getOrderSsn());
		 record.setPagyPayTxnTm(order.getOrderTm());
		 record.setPagyTxnTm(order.getOrderTm());
		 
		 
         record.setStlmSt(Constans.STLM_ST_00);
         record.setTxnAmt(Long.parseLong(order.getPayAmt()));
         record.setTpamTxnAmt(Long.parseLong(order.getPayAmt()));
         record.setTxnReqSsn(order.getOrderSsn());
         
         record.setPagySysNo(Constans.LOAN_SYS_POINT);
         record.setPagyNo("604000000000001");
         record.setPagySysSoaNo("6040100003");
         record.setPagySysSoaVersion("1.0.0");
         
         
         try 
         {
			record.setChkSuccDt(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYY_MM_DD));
		 } 
         catch (ParseException e) 
         {
			e.printStackTrace();
		 }
         record.setChkSt(Constans.CHK_STATE_00);
         record.setChkRst("");
         record.setCrtTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
         record.setLstUpdTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
         
         return record;
	}
	
	public void updateObject()
	{
		log.info(">>>>>>>>>>>>>>>>>>>loanpayOrderChk complete...");
	}

	public String getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}

}
