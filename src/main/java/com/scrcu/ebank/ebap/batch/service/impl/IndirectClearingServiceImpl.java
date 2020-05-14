package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalDetail;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.UUIDCreator;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.BthSetCapitalDetailDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.service.IndirectClearingService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IndirectClearingServiceImpl implements IndirectClearingService 
{
	private static BigDecimal ZERO = new BigDecimal(0);
	private static BigDecimal MINUS_ONE = new BigDecimal(-1);
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;
	
	@Resource
	private BthSetCapitalDetailDao bthSetCapitalDetailDao;
	
	@Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           // 商户基本信息
    
    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息
	
	@Override
	public CommonResponse indirectStlClearing(BatchRequest request) throws Exception 
	{
		log.info(">>>>>>>>>>>>>>>>>indirectStlClearing executing...");
		int count = 0;
		count = this.getTotalResult();
		List<BthMerInAccDtl> subMerStlList = this.getDataList();
		for(BthMerInAccDtl inAccDtl : subMerStlList)
		{
			this.execute(inAccDtl);
		}
		this.updateObject();
		
		return null;
	}
	

	private int getTotalResult()
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("subStlResult", Constans.JS2_STATUS_TRANSFER_SUCC);
		count = bthMerInAccDtlDao.count("countMerAccInDtlByTransferStatus", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>transfer succ count is " + count);
		return count;
	}
	
	private List<BthMerInAccDtl> getDataList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("subStlResult", Constans.JS2_STATUS_TRANSFER_SUCC);
		List<BthMerInAccDtl> subMerStlList = bthMerInAccDtlDao.selectList("selectMerAccInDtlByTransferStatus", params);
		return subMerStlList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(BthMerInAccDtl inAccDtl)
	{
		//初始化清分信息
		BthSetCapitalDetail detail = this.initCapitalDetail(inAccDtl, "01");
		//查询二级商户结算账户信息
		MchtContInfo merSettleInfo = this.getMerStlInfo(inAccDtl.getChlMerId());
		
		// ***转出账户(商户待结算账户)****
		detail.setOutAccountNo(merSettleInfo.getLiqAcctNo());    
		detail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());   
		detail.setOutAccountName(merSettleInfo.getLiqAcctName());

		// ***转入账户(商户结算账户)****
		detail.setInAccountNo(merSettleInfo.getSettlAcctNo()); // 转入账户(商户结算账户)
		detail.setInAccountName(merSettleInfo.getSettlAcctName()); // 转入商户名
		detail.setInAccoutOrg(merSettleInfo.getSettlAcctOrgId()); // 转入商户机构号
		
		//(txn_amt - setl_fee_amt - commission_amt - logis_fee)  as txn_amt
		//结算金额
		BigDecimal tranAmt = 
				new BigDecimal(inAccDtl.getTxnAmt()).subtract(new BigDecimal(inAccDtl.getSetlFeeAmt()))
				.subtract(new BigDecimal(inAccDtl.getCommissionAmt())).subtract(new BigDecimal(inAccDtl.getLogisFee()));
		
		if(Constans.ORDER_TYPE_RETURN.equals(inAccDtl.getOrderType()))
		{
			tranAmt = tranAmt.multiply(MINUS_ONE);    //退款，金额记负数
		}
		detail.setTranAmount(tranAmt.divide(ONE_HUNDRED));
			
		bthSetCapitalDetailDao.insertSelective(detail);
	}
	
	public void updateObject()
	{
		//更新二级商户结算状态
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("subStlResult", Constans.JS2_STATUS_CLEARING);
		params.put("subStlStatus", Constans.JS2_STATUS_TRANSFER_SUCC);
		
		bthMerInAccDtlDao.update("updateMerInAccDtlBySubStlStatus", params);
		log.info(">>>>>>>>>>>>>>>>>>>update submer settle status complete...");
	}

	/**
	 * 初始化本金资金明细
	 * 
	 * @param orderInfo
	 * @param cur
	 * @return
	 */
	private BthSetCapitalDetail initCapitalDetail(BthMerInAccDtl settleInfo, String cur)
	{
		Date d = new Date();
		String curDate = DateUtil.format(d, "yyyyMMdd");
		BthSetCapitalDetail detail = new BthSetCapitalDetail();
		detail.setId(UUIDCreator.randomUUID().toString());
		detail.setCleaTime(curDate); // 清算日期
		detail.setOrderId(settleInfo.getTxnSeqId());    //订单流水号(扫码为订单号，线上未子订单号) 
		detail.setMerId(settleInfo.getChlMerId());
		detail.setMerName(settleInfo.getChlMerName()); // 商户名称
		detail.setAccountType(Constans.SETTL_ACCT_TYPE_PLAT); 
		detail.setTranAmount(ZERO);

		detail.setTransCur(cur); // 交易币种
		// 分录流水类型:20-二级商户隔日间接入账
		detail.setEntryType(Constans.ENTRY_TYPE_SUBMER_IN);

		detail.setTranType(settleInfo.getOrderType());    // 交易类型:01-消费 02-退货
		detail.setFundChannel(Constans.CHANNEL_TYPE_WX);     // 资金通道:01-微信记账

		// 处理状态:00-未处理 01-处理中 02-处理成功 03-处理失败
		detail.setDealResult(Constans.DEAL_RESULT_NOT);
		// 入账状态：00 -未入账 01 - 入账成功 02 - 入账失败
		detail.setAccountStauts(Constans.ACCOUNT_STATUS_NOT);
		detail.setCreateDate(DateUtil.format(new Date(), "yyyyMMddHHmmss")); // 创建时间

		detail.setTransCur("01"); // 交易币种
		detail.setMerOrderId(settleInfo.getTxnSeqId());

		return detail;
	}
	
	/**
	 * 查询商户基本信息 ：
	 * @param mchtId ： 商户号
	 * @return
	 */
	public MchtBaseInfo getMerBaseInfo(String mchtId) {
		
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("mchtId", mchtId);
		MchtBaseInfo merInfo = mchtBaseInfoDao.selectOne("selectMerInfoByMchtId", m);
		
		return merInfo;
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
	
}
