package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.dao.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.UUIDCreator;
import com.scrcu.ebank.ebap.batch.service.AccFailDataClearingService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccFailDataClearingServiceImpl implements AccFailDataClearingService 
{
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
    @Resource
    private KeepAccInfoDao keepAccInfoDao;   // 入账明细信息
    @Resource
    private BthSetCapitalDetailDao bthSetCapitalDetailDao;     // 清分表
	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;   // 入账明细信息
	@Resource
	private PayOrderInfoDao payOrderInfoDao;           // 订单信息
	@Resource
	private MchtContInfoDao mchtContInfoDao;           //商户合同信息

	/**
	 * 商户入账汇总信息
	 */
	@Resource
	private BthMerInAccDao bthMerInAccDao;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public CommonResponse handleAccFailData(BatchRequest request) throws Exception {
		
		String batchDate = request.getSettleDate();
    	if(IfspDataVerifyUtil.isBlank(batchDate))
    	{
    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
    	}
		int count = this.getTotalResult(Constans.IBANK_SYS_NO, batchDate);
		
		
		//TODO:是否根据count数量进行分页..
		List<KeepAccInfo> accDataList = this.getDataList(Constans.IBANK_SYS_NO, batchDate);

		if(accDataList!=null && accDataList.size()>0) {
			for (KeepAccInfo accInfo : accDataList) {
				if(IfspDataVerifyUtil.isBlank(accInfo.getIsReaccount())) {
					this.execute(Constans.CHANNEL_TYPE_CORE, accInfo);
				}
				this.updateOrderStlStatus(accInfo);
			}
		}
		
		System.out.println("......................test2");
		
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
	private int getTotalResult(String channel,String date)
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accState", Constans.KEEP_ACCOUNT_STAT_FAIL);  //03-记账失败
		params.put("chkSt", Constans.CHK_STATE_01);               //01-已对账
		params.put("rerunFlag", Constans.RERUN_FLAG_YES);         //00-可重跑
		count = keepAccInfoDao.count("countAccFailedData", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>account fail data count of  " + date + " is : " + count);
		return count;
	}
	/**
	 * 根据渠道抽取待清算数据
	 * @param channel 支付渠道
	 * @param date    清算日期
	 * @return
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	private List<KeepAccInfo> getDataList(String channel,String date)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accState", Constans.KEEP_ACCOUNT_STAT_FAIL);  //03-记账失败
		params.put("chkSt", Constans.CHK_STATE_01);               //01-已对账
		params.put("rerunFlag", Constans.RERUN_FLAG_YES);         //00-可重跑
		
		List<KeepAccInfo> accountList = keepAccInfoDao.selectList("selectAccFailedData", params);
		return accountList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(String channel,KeepAccInfo accountInfo)
	{

		//将失败的记账记录插入清分明细表
		BthSetCapitalDetail detail = initCapitalDetail(accountInfo);
		//bthSetCapitalDetailDao.insert(detail);20190926
		//20190920将T+0失败的数据删除
		int a = bthSetCapitalDetailDao.updateFailureT0(detail);
		//20190920将T+0更新t+0汇总数据
		if(a>0) {
			String orderSsn = accountInfo.getOrderSsn();
			/*PayOrderInfo payOrderInfo = payOrderInfoDao.queryByTxnSeqId(accountInfo.getOrderSsn());
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("mchtId", payOrderInfo.getMchtId());
			MchtContInfo merInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);
			Map<String,String> map = new HashMap();
			map.put("cleaTime", accountInfo.getOrderTm().substring(0, 8));
			map.put("merId",payOrderInfo.getMchtId() );
			map.put("outAccountNo", accountInfo.getOutAccNo());
			map.put("outAccoutOrg", merInfo.getSettlAcctOrgId());
			map.put("inAccountNo",accountInfo.getInAccNo());
			map.put("inAccoutOrg", merInfo.getLiqAcctOrgId());
			String batchNo = bthMerInAccDao.getBatchNo(accountInfo.getOrderTm().substring(0, 8));
			map.put("batchNo", batchNo);
			BthSetCapitalDetail bthSetCapitalDetail =  bthSetCapitalDetailDao.selectClearSumInfoMchtInT0(map);
			Map<String ,Object> inAccDtlMap = new HashMap<>();
			// 查询参数
			inAccDtlMap.put("merId",payOrderInfo.getMchtId());
			inAccDtlMap.put("entryType","01");
			inAccDtlMap.put("cleaTime",accountInfo.getOrderTm().substring(0, 8));
			// 根据订单号汇总交易金额 , 商户手续费
			BthMerInAccInfo bthMerInAccInfo = bthMerInAccDtlDao.sumTxnAmtFeeAmtT0(inAccDtlMap);
			log.info("bthMerInAccInfo"+bthMerInAccInfo);
			log.info("bthSetCapitalDetail"+bthSetCapitalDetail);
			BthMerInAcc bthMerInAcc = new BthMerInAcc();
			if(IfspDataVerifyUtil.isEmpty(bthMerInAccInfo)){
				bthMerInAcc.setTxnAmt("0");
				bthMerInAcc.setFeeAmt("0");
				bthMerInAcc.setTxnCount(0);
			}else{
				bthMerInAcc.setTxnAmt(String.valueOf(bthMerInAccInfo.getTxnAmt()));
				bthMerInAcc.setFeeAmt(String.valueOf(bthMerInAccInfo.getFeeAmt()));
				bthMerInAcc.setTxnCount(bthMerInAccInfo.getTxnCount());
			}
			if(IfspDataVerifyUtil.isEmpty(bthSetCapitalDetail)){
				bthMerInAcc.setInAcctAmt("0");
			}else{
				bthMerInAcc.setInAcctStat(bthSetCapitalDetail.getAccountStauts());
				bthMerInAcc.setInAcctAmt(String.valueOf(bthSetCapitalDetail.getTranAmount()));
			}

			bthMerInAcc.setDateStlm(accountInfo.getOrderTm().substring(0, 8));
			bthMerInAcc.setChlMerId(payOrderInfo.getMchtId());
			bthMerInAcc.setInAcctNo(accountInfo.getInAccNo());
			bthMerInAcc.setOutAcctNo(accountInfo.getOutAccNo());
			Map<String ,String> map1 = new HashMap<>();
			map1.put("dateStlm", bthMerInAcc.getDateStlm());
			map1.put("outAcctNo", bthMerInAcc.getOutAcctNo());
			map1.put("inAcctNo", bthMerInAcc.getInAcctNo());
			map1.put("merId",payOrderInfo.getMchtId() );
			String txnSsn = bthMerInAccDao.getTxnSsn(map1);
			bthMerInAcc.setTxnSsn(txnSsn);
			bthMerInAccDao.updateByPrimaryKeySelectiveT0(bthMerInAcc);*/
		}else{
			bthSetCapitalDetailDao.insert(detail);
		}
	}
	
	/**
	 * 更新记账结果为记账成功
	 * @param accountInfo
	 * @return
	 */
	public int updateOrderStlStatus(KeepAccInfo accountInfo)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("coreSsn", accountInfo.getCoreSsn());
		params.put("accState", Constans.KEEP_ACCOUNT_STAT_SUCCESS);    //02-记账成功
		params.put("isReaccount","1");// 已补账标记位
		return keepAccInfoDao.update("updateAccFailedDataStsById", params);
	}

	
	/**
	 * 初始化本金资金明细
	 * 
	 * @param
	 * @param
	 * @return
	 */
	private BthSetCapitalDetail initCapitalDetail(KeepAccInfo settleInfo)
	{
		Date d = new Date();
		String curDate = DateUtil.format(d, "yyyyMMdd");
		BthSetCapitalDetail detail = new BthSetCapitalDetail();
		detail.setId(UUIDCreator.randomUUID().toString());
		detail.setCleaTime(curDate); // 清算日期
		detail.setOrderId(settleInfo.getOrderSsn());    //订单流水号(扫码为订单号，线上未子订单号) 
		//detail.setMerId(mgtMerInfo.getMchtId());
		//detail.setMerName(mgtMerInfo.getMchtName()); // 商户名称
		detail.setAccountType(Constans.SETTL_ACCT_TYPE_PLAT);             //入账账户类型 0-本行 1-他行,默认本行
		

		// 分录流水类型:97 - 日间记账
		detail.setEntryType(Constans.ENTRY_TYPE_FOR_ACCOUNT);

		detail.setTranType(Constans.ORDER_TYPE_CONSUME);    // 交易类型:01-消费 02-退货
		detail.setFundChannel(Constans.CHANNEL_TYPE_CORE);     // 资金通道:00-本行

		// 处理状态:00-未处理 01-处理中 02-处理成功 03-处理失败
		detail.setDealResult(Constans.DEAL_RESULT_NOT);
		// 入账状态：00 -未入账 01 - 入账成功 02 - 入账失败
		detail.setAccountStauts(Constans.ACCOUNT_STATUS_NOT);
		detail.setCreateDate(DateUtil.format(new Date(), "yyyyMMddHHmmss")); // 创建时间

		detail.setTransCur("01"); // 交易币种
		detail.setMerOrderId(settleInfo.getOrderSsn());
		
		//设置账户信息
		detail.setOutAccountNo(settleInfo.getOutAccNo());
		detail.setOutAccountName(settleInfo.getOutAccNoName());
		
		detail.setInAccountNo(settleInfo.getInAccNo());
		detail.setInAccountName(settleInfo.getInAccNoName());
		
		
		detail.setTranAmount(new BigDecimal(settleInfo.getTransAmt()).divide(ONE_HUNDRED));

		//初始化批次号：日期+转出账号
		String batchNo;
		if(settleInfo.getOutAccNo().length() > 24)
		{
			 batchNo = curDate + settleInfo.getOutAccNo().substring(0,24);
		}
		else
		{
			 batchNo = curDate + settleInfo.getOutAccNo();
		}
		detail.setBatchNo(batchNo);

		return detail;
	}
	
}
