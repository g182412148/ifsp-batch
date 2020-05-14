package com.scrcu.ebank.ebap.batch.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthTransferInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.batch.dao.BthTransferInfoDao;
import com.scrcu.ebank.ebap.batch.service.TransferInfoSummService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransferInfoSummServiceImpl implements TransferInfoSummService 
{
	@Resource
    private BthTransferInfoDao bthTransferInfoDao;       // 转账信息
	@Resource
	private BthMerInAccDao bthMerInAccDao;               // 商户入账信息
	
	@Override
	/**
	 * 将转账信息表数据写到商户入账表
	 */
	public CommonResponse summTransferInfo() 
	{
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>transfer service begin");
		this.getTotalResult();
		List<BthTransferInfo> transferList = this.getDataList();
		
		for(BthTransferInfo transferInfo : transferList)
		{
			this.execute(transferInfo);
			this.updateTransferStauts(transferInfo);
		}
		
		//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
		return commonResponse;
	}
	
	/**
	 * 统计当前渠道对账成功数据量
	 * @param channel 支付渠道
	 * @param date    对账成功日期
	 * @return
	 */
	private int getTotalResult()
	{
		
		int count = 0;
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("dealStatus", Constans.TRANSFER_STATUS_UNHANDLE);              //未处理
		count = bthTransferInfoDao.count("countUnhandleTransferInfo", m);
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>transfer  txn count : " + count);
		return count;
	}
	/**
	 * 抽取订单信息
	 * @param channel 支付渠道
	 * @param date    对账成功日期
	 * @return
	 */
	private List<BthTransferInfo> getDataList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dealStatus", Constans.TRANSFER_STATUS_UNHANDLE);              //未处理
		
		List<BthTransferInfo> transferList = bthTransferInfoDao.selectList("selectUnhandleTransferInfo", params);
		return transferList;
	}

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	private void execute(BthTransferInfo transferInfo)
	{
		BthMerInAcc merAccInfo = this.buildMerAccInfo(transferInfo);
		bthMerInAccDao.insert(merAccInfo);
	}
	/**
	 * 
	 * @param transferInfo
	 */
	public void updateTransferStauts(BthTransferInfo transferInfo)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dealStatus", Constans.TRANSFER_STATUS_DEALING);
		params.put("id", transferInfo.getId());
		
		bthTransferInfoDao.update("updateTransferStatusById", params);
	}
	
	
	public BthMerInAcc buildMerAccInfo(BthTransferInfo transferInfo)
	{
		BthMerInAcc merAccInfo = new BthMerInAcc();
		
		merAccInfo.setInAcctType(Constans.IN_ACCT_TYPE_TRANSFER);    //入账类型：转账
		
		merAccInfo.setChlMerId(transferInfo.getMerId());
		merAccInfo.setChlMerName(transferInfo.getMerName());
		
		String currDate = DateUtil.format(new Date(), "yyyyMMdd");
		
		merAccInfo.setDateStlm(currDate);
		
		merAccInfo.setBorrowFlag(Constans.BORROW_ALLOCATED_ACCT);    //指定账户
		merAccInfo.setLendFlag(Constans.LEND_ALLOCATED_ACCT);
		
		//转入转出账户信息
		merAccInfo.setOutAcctName(transferInfo.getOutAccountName());
		merAccInfo.setOutAcctNo(transferInfo.getOutAccountNo());
		merAccInfo.setOutAcctNoOrg(transferInfo.getOutAccoutOrg());
		
		merAccInfo.setInAcctNo(transferInfo.getInAccountNo());
		merAccInfo.setInAcctName(transferInfo.getInAccountName());
		merAccInfo.setInAcctNoOrg(transferInfo.getInAccoutOrg());
		
		//merAccInfo.setTxnAmt(transferInfo.getAccountAmount().doubleValue()+"");
		merAccInfo.setInAcctAmt(transferInfo.getAccountAmount().doubleValue()+"");
		merAccInfo.setBrno("0");
		merAccInfo.setHandleState(Constans.HANDLE_STATE_PRE);     //未处理
		merAccInfo.setInAcctStat(Constans.IN_ACC_STAT_PRE);       //入账状态 ： 0-未入账
		
		merAccInfo.setTxnSsn(transferInfo.getId());
		merAccInfo.setBatchNo(transferInfo.getId());
		
		merAccInfo.setTxnCount(0);
		
		
		return merAccInfo;
	}

}
