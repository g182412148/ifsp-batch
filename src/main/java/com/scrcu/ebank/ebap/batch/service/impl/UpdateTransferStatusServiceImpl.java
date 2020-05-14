package com.scrcu.ebank.ebap.batch.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scrcu.ebank.ebap.batch.bean.dto.BthTransferInfo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.BthTransferInfoDao;
import com.scrcu.ebank.ebap.batch.service.UpdateTransferStatusService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UpdateTransferStatusServiceImpl implements UpdateTransferStatusService 
{

	@Resource
	private BthTransferInfoDao bthTransferInfoDao;
	
	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;
	
	@Override
	public void updateTransferStatus() 
	{
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>update updateTransferStatus executing...");
		int count = this.getTotalResult();
		List<BthTransferInfo> transferList = this.getDataList();
		for(BthTransferInfo transfer : transferList)
		{
			this.execute(transfer);
		}
		this.updateObject();
	}
	
	
	private int getTotalResult()
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("handleFlag", Constans.HANDLE_FLAG_UNHANDLE);    //handleFlag处理成功包括“转账成功”与“转账失败”两种状态
		count = bthTransferInfoDao.count("countTransferDataByHandleFlag", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>transfer complete count is " + count);
		return count;
	}
	
	private List<BthTransferInfo> getDataList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("handleFlag", Constans.HANDLE_FLAG_UNHANDLE);
		List<BthTransferInfo> transferList = bthTransferInfoDao.selectList("selectTransferDataByHandleFlag", params);
		return transferList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(BthTransferInfo transfer)
	{
		//根据转账表转账状态更新入账明细表转账状态
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("chlMerId", transfer.getMerId()+"%");
		if(Constans.TRANSFER_STATUS_SUCC.equals(transfer.getDealStatus()))
		{
			params.put("subStlResult", Constans.JS2_STATUS_TRANSFER_SUCC);
		}
		else if(Constans.JS2_STATUS_TRANSFER_FAIL.equals(transfer.getDealStatus()))
		{
			params.put("subStlResult", Constans.JS2_STATUS_TRANSFER_FAIL);
		}
		
		params.put("subStlStatus", Constans.JS2_STATUS_TRANSFERING);
		params.put("updateDate", Constans.JS2_STATUS_TRANSFERING);
		
		bthMerInAccDtlDao.update("updateMerInAccDtlTransferStatus", params);
			
	}
	
	public int updateObject()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("handleFlag", Constans.HANDLE_FLAG_UNHANDLE);
		params.put("handleFlagResult", Constans.HANDLE_FLAG_SUCC);           //更新后续处理标志为处理成功
		return bthTransferInfoDao.update("updateTransferHandleFlag", params);
	}

}
