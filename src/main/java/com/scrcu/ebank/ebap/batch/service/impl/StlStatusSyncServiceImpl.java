package com.scrcu.ebank.ebap.batch.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.StlStatusSyncService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StlStatusSyncServiceImpl implements StlStatusSyncService 
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
	public void syncStlStatus(BatchRequest request) 
	{
		
		String batchDate = request.getSettleDate();
    	if(IfspDataVerifyUtil.isBlank(batchDate))
    	{
    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
    	}
    	
		this.setBatchDate(batchDate);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>syncStlStatus service of "+this.batchDate+" executing ");
		
		int count = this.getTotalResult();
		
		List<BthMerInAccDtl> inAccDtlList = this.getDataList();
		
		for(BthMerInAccDtl inAccDtl : inAccDtlList)
		{
			this.execute(inAccDtl);
		}
		
	}
	
	public int getTotalResult()
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("stlResult", Constans.SETTLE_STATUS_SUCCESS_CLEARING);
		params.put("updateDate", batchDate+'%');
		count = bthMerInAccDtlDao.count("countStlSuccOrderOfCurrDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>order-count-num  update @"+this.getBatchDate()+ " is : " +count);
		return count;
	}
	
	public List<BthMerInAccDtl> getDataList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("stlResult", Constans.SETTLE_STATUS_SUCCESS_CLEARING);
		params.put("updateDate", batchDate+'%');
		List<BthMerInAccDtl> inAccDtlList = bthMerInAccDtlDao.selectList("selectStlSuccOrderOfCurrDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>inAccDtlList  size of current thread is :" + inAccDtlList.size());
		return inAccDtlList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(BthMerInAccDtl inAccDtl)
	{
		//更新子订单表结算状态
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("settleStatus", inAccDtl.getStlStatus());
		params.put("subOrderSsn", inAccDtl.getTxnSeqId());
		params.put("settleDate", inAccDtl.getInAcctDate());
		
		paySubOrderInfoDao.update("updateSubOrderStlStatusBySubOrderId", params);
	}
	
	public void updateObject()
	{
		//更新二级商户结算状态
		log.info(">>>>>>>>>>>>>>>>>>>syncStlStatus @"+this.getBatchDate() + "completed..");
	}

	public String getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}

}
