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
import com.scrcu.ebank.ebap.batch.bean.dto.BthTransferInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.UUIDCreator;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.BthTransferInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.TranferBackAmtCountService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransferBackAmtCountServiceImpl implements TranferBackAmtCountService 
{
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	private static BigDecimal ZERO = new BigDecimal(0);
	
	
	@Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;       // 入账明细信息
	
	@Resource
    private BthTransferInfoDao bthTransferInfoDao;       // 转账信息
    
    @Resource
    private PayOrderInfoDao payOrderInfoDao;           // 订单信息
    
    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           // 商户基本信息
    
    @Resource
    private MchtContInfoDao mchtContInfoDao;           // 商户合同信息

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public CommonResponse getTransferAmtInfo() 
	{
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>getTransferAmtInfo begin");
		String subStlDate = DateUtil.format(new Date(), "yyyyMMdd");
		List<BthMerInAccDtl> transferSumList = this.getDataList(subStlDate);
		for(BthMerInAccDtl dtl : transferSumList)
		{
			this.execute(dtl);
			this.updateOrderStlStatus(dtl,subStlDate);
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
	//@Transactional(propagation = Propagation.REQUIRED)
	/*private int getTotalResult(String channel,String date)
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("subStlStatus", Constans.JS2_STATUS_UNHANDLE);  //03-记账失败
		params.put("stlStatus", Constans.SETTLE_STATUS_SUCCESS_CLEARING);             //03 - 已成功结算给上级商户
		count = bthMerInAccDtlDao.count("countAccFailedData", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>account fail data count of  " + date + " is : " + count);
		return count;
	}*/
	
	/**
	 * 根据渠道抽取待清算数据
	 * @param channel 支付渠道
	 * @param date    清算日期
	 * @return
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	public List<BthMerInAccDtl> getDataList(String subStlDate)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("subStlStatus", Constans.JS2_STATUS_UNHANDLE);                     //00 - 未处理
		params.put("stlStatus", Constans.SETTLE_STATUS_SUCCESS_CLEARING);             //03 - 已成功结算给上级商户
		params.put("subStlDate", subStlDate);                                         //00 - 二级商户结算日期
		
		List<BthMerInAccDtl> accountList = bthMerInAccDtlDao.selectList("selectTransferInfoByMer", params);
		return accountList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(BthMerInAccDtl accDtl)
	{
		//将失败的记账记录插入清分明细表
		BthTransferInfo transfer = initTransferInfo(accDtl);
		try 
		{
			bthTransferInfoDao.insertSelective(transfer);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new RuntimeException("转账数据插入异常!");
		}
	}
	
	/**
	 * 更新二级商户结算状态为划账中
	 * @param accountInfo
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateOrderStlStatus(BthMerInAccDtl accDtl,String subStlDate)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("chlMerId", accDtl.getChlMerId()+"%");
		params.put("subStlStatus", Constans.JS2_STATUS_UNHANDLE);       //00-未处理
		params.put("subStlResult", Constans.JS2_STATUS_TRANSFERING);    //01-划账中
		params.put("subStlDate", subStlDate);
		
		try
		{
			return bthMerInAccDtlDao.update("updateJs2StatusByMer", params);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new RuntimeException("数据库操作异常!");
		}
	}
	
	/**
	 * 根据商户号,结算build转账记录
	 * @param accDtl
	 * @return
	 */
	public BthTransferInfo initTransferInfo(BthMerInAccDtl accDtl)
	{
		BthTransferInfo transferInfo = new BthTransferInfo();
		
		//查询商户基本信息，商户结算信息
		MchtBaseInfo baseInfo = this.getMerBaseInfo(accDtl.getChlMerId());
		MchtContInfo stlInfo = this.getMerStlInfo(accDtl.getChlMerId());
		
		transferInfo.setId(UUIDCreator.randomUUID().toString());
		
		transferInfo.setMerId(accDtl.getChlMerId());   //一级商户商户号
		transferInfo.setMerName(baseInfo.getMchtName());
		
		//转出账户：一级商户结算账户
		transferInfo.setOutAccountNo(stlInfo.getSettlAcctNo());
		transferInfo.setOutAccountName(stlInfo.getSettlAcctName());
		transferInfo.setOutAccoutOrg(stlInfo.getSettlAcctOrgId());
		
		//转入账户：一级商户待清算账户
		transferInfo.setInAccountNo(stlInfo.getLiqAcctNo());
		transferInfo.setInAccountName(stlInfo.getLiqAcctName());
		transferInfo.setInAccoutOrg(stlInfo.getLiqAcctOrgId());
		
		transferInfo.setAccountAmount(new BigDecimal(accDtl.getTxnAmt()).divide(ONE_HUNDRED));
		transferInfo.setAcutualAmount(ZERO);
		transferInfo.setRemainAmonut(ZERO);
		transferInfo.setDealStatus(Constans.TRANSFER_STATUS_UNHANDLE);
		transferInfo.setHandleFlag(Constans.HANDLE_FLAG_UNHANDLE);        //后续处理标志，未处理
		
		transferInfo.setTransCur("01");   //币种：默认01-人民币
		
		String currTime = DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		transferInfo.setAccountTime(currTime);
		transferInfo.setHandDate(currTime.substring(0, 8));
		transferInfo.setCreateDate(currTime);
		transferInfo.setUpdateDate(currTime);
		
		return transferInfo;
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
