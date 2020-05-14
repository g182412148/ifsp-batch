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
import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalDetail;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.BthSetCapitalDetailDao;
import com.scrcu.ebank.ebap.batch.service.UpdateStlStatusService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UpdateStlStatusServiceImpl implements UpdateStlStatusService 
{
	private String batchDate;
	
	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;
	
	@Resource
	private BthSetCapitalDetailDao bthSetCapitalDetailDao;
	
	@Override
	public void updateStlStatus(BatchRequest request) 
	{
		String batchDate = request.getSettleDate();
    	if(IfspDataVerifyUtil.isBlank(batchDate))
    	{
    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
    	}
    	
		this.setBatchDate(batchDate);
		//根据清分表二级商户隔日间接订单处理结果更新入账明细表二级商户结算状态
		this.updateSubMerStlStatus();
	}
	
	
	public void updateSubMerStlStatus()
	{
		int count = this.getTotalResult();
		List<BthSetCapitalDetail> subMerStlList = this.getDataList();
		for(BthSetCapitalDetail subMerCapDtl : subMerStlList)
		{
			this.execute(subMerCapDtl);
		}
		this.updateObject();
	}
	
	private int getTotalResult()
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("entryType", Constans.ENTRY_TYPE_SUBMER_IN);
		params.put("updateDate", batchDate+"%");
		count = bthSetCapitalDetailDao.count("countSubMerStlDataByDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>transfer complete count is " + count);
		return count;
	}
	
	private List<BthSetCapitalDetail> getDataList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("entryType", Constans.ENTRY_TYPE_SUBMER_IN);
		params.put("updateDate", batchDate+"%");
		List<BthSetCapitalDetail> subMerStlList = bthSetCapitalDetailDao.selectList("selectSubMerStlDataByDate", params);
		return subMerStlList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(BthSetCapitalDetail subMerCapDtl)
	{
		//根据转账表转账状态更新入账明细表转账状态
		Map<String,Object> params = new HashMap<String,Object>();
		
		if(Constans.DEAL_RESULT_SUCCESS.equals(subMerCapDtl.getDealResult()))
		{
			params.put("subStlResult", Constans.JS2_STATUS_SUCC);
		}
		else if(Constans.DEAL_RESULT_FAILE.equals(subMerCapDtl.getDealResult()))
		{
			params.put("subStlResult", Constans.JS2_STATUS_FAIL);
		}
		
		params.put("updateDate", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		params.put("txnSeqId", subMerCapDtl.getOrderId());
		
		bthMerInAccDtlDao.update("updateMerInAccDtlByOrderId", params);
			
	}
	
	public void updateObject()
	{
		log.info(">>>>>>>>>>>>>>>>>>>update submer settle status complete...");
	}


	public String getBatchDate() {
		return batchDate;
	}


	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}
	
	
}
