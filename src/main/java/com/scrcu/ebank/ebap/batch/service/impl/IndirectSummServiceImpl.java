package com.scrcu.ebank.ebap.batch.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalDetail;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.batch.dao.BthSetCapitalDetailDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.service.IndirectSummService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IndirectSummServiceImpl implements IndirectSummService 
{
	@Resource
	private BthMerInAccDao bthMerInAccDao;
	
	@Resource
	private BthSetCapitalDetailDao bthSetCapitalDetailDao;
	
	@Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           // 商户基本信息
    
    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息
	
	private String batchNo;
	
	@Override
	public CommonResponse indirectOrderSumm(BatchRequest request) throws Exception 
	{
		batchNo = IfspId.getUUID(32);
		int count = 0;
		count = this.getTotalResult();
		List<BthSetCapitalDetail> summInfoList = this.getDataList(1,count);
		for(BthSetCapitalDetail inAccDtl : summInfoList)
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
		params.put("dealResult", Constans.DEAL_RESULT_NOT);
		params.put("entryType", Constans.ENTRY_TYPE_SUBMER_IN);     //分录流水类型： 20-二级商户隔日间接入账
		count = bthSetCapitalDetailDao.count("countSummaryInfo", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>transfer complete count is " + count);
		return count;
	}
	
	private List<BthSetCapitalDetail> getDataList(int beginIndex,int endIndex)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		
		params.put("startIdx", beginIndex);
		params.put("endIdx", endIndex);
		params.put("dealResult", Constans.DEAL_RESULT_NOT);
		params.put("entryType", Constans.ENTRY_TYPE_SUBMER_IN);     //分录流水类型： 20-二级商户隔日间接入账
		
		List<BthSetCapitalDetail> summaryList = this.bthSetCapitalDetailDao.selectList("selectSummaryInfo", params);
		
		return summaryList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(BthSetCapitalDetail detail)
	{
		//初始化清分信息
		BthMerInAcc merInAcc = this.initMerInAccInfo(detail);
		bthMerInAccDao.insert(merInAcc);
		
	}
	
	public void updateObject()
	{
		//更新二级商户结算状态
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dealResult", Constans.DEAL_RESULT_HANDING);
		params.put("dealStatus", Constans.DEAL_RESULT_NOT);
		
		params.put("entryType", Constans.ENTRY_TYPE_SUBMER_IN);
		params.put("batchNo", batchNo);
		
		bthSetCapitalDetailDao.update("updateSubmerDealStsOfDtl", params);
		log.info(">>>>>>>>>>>>>>>>>>>update submer settle status complete...");
	}
	
	/**
	 * 初始化本金资金明细
	 * 
	 * @param orderInfo
	 * @param cur
	 * @return
	 */
	private BthMerInAcc initMerInAccInfo(BthSetCapitalDetail detail)
	{
		//查询商户信息、商户结算信息
		MchtBaseInfo merInfo = this.getMerBaseInfo(detail.getMerId());
		MchtContInfo merStlInfo = this.getMerStlInfo(detail.getMerId());
		Date d = new Date();
		String curDate = DateUtil.format(d, "yyyyMMdd");
		BthMerInAcc merInAcc = new BthMerInAcc();
		
		merInAcc.setDateStlm(curDate);
		merInAcc.setChlMerId(detail.getMerId());
		merInAcc.setChlMerName(merInfo.getMchtName());
		
		merInAcc.setInAcctType(Constans.IN_ACCT_TYPE_SUBMER);    //入账类型：08 - 二级商户间接结算
		merInAcc.setTxnCount(0);
		merInAcc.setInAcctAmt(detail.getTranAmount().doubleValue()+"");
		merInAcc.setHandleState(Constans.HANDLE_STATE_PRE);     //未处理
		merInAcc.setInAcctStat(Constans.IN_ACC_STAT_PRE);
		merInAcc.setBrno(detail.getAccountType());      //设置结算账户类型 0 - 本行, 1 - 他行
		
		//账户信息
		merInAcc.setOutAcctNo(detail.getOutAccountNo());
		merInAcc.setOutAcctName(merStlInfo.getLiqAcctName());
		merInAcc.setOutAcctNoOrg(detail.getOutAccoutOrg());
		
		merInAcc.setInAcctNo(detail.getInAccountNo());
		merInAcc.setInAcctName(merStlInfo.getSettlAcctName());
		merInAcc.setInAcctNoOrg(detail.getInAccoutOrg());
		
		merInAcc.setLendFlag(Constans.LEND_ALLOCATED_ACCT);      //指定账户
		merInAcc.setBorrowFlag(Constans.BORROW_ALLOCATED_ACCT);  //指定账户
		
		 // 入账流水号
		merInAcc.setTxnSsn(IfspId.getUUID(32));
		
		// 设置批次号
		merInAcc.setBatchNo(batchNo);
		return merInAcc;
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


	public String getBatchNo() {
		return batchNo;
	}


	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	
	
}
