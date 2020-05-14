package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.service.ClearingExecutor;
import com.scrcu.ebank.ebap.batch.service.OrderClearingService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class OrderClearingServiceImpl implements OrderClearingService
{
    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;   // 入账明细信息

    @Resource(name="coreClearingExecutor")
    private ClearingExecutor coreClearingExecutor;

    @Resource(name="wxClearingExecutor")
    private ClearingExecutor wxClearingExecutor;

    @Resource(name="aliClearingExecutor")
    private ClearingExecutor aliClearingExecutor;

    @Resource(name="unionpayClearingExecutor")
    private ClearingExecutor unionpayClearingExecutor;

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public CommonResponse coreChannelClearing(BatchRequest request) throws Exception
	{

//		String batchDate = request.getSettleDate();
//    	if(IfspDataVerifyUtil.isBlank(batchDate))
//    	{
//    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
//    	}
//		int count = this.getTotalResult(Constans.IBANK_SYS_NO, batchDate);
//
//		log.info("order count of channel 604 @date"+ batchDate +" is "+count);
//
//		//TODO:是否根据count数量进行分页..
//		List<BthMerInAccDtl> clearDataList = this.getDataList(Constans.IBANK_SYS_NO, batchDate);
//
//		for(BthMerInAccDtl clearData : clearDataList)
//		{
//			this.execute(Constans.CHANNEL_TYPE_CORE, clearData);
//			this.updateOrderStlStatus(clearData);
//		}
		this.execute(Constans.CHANNEL_TYPE_CORE, request);

		System.out.println("......................test2");

		//应答
		CommonResponse commonResponse = new CommonResponse();

		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

		return commonResponse;

	}

	@Override
	public CommonResponse wxChannelClearing(BatchRequest request) throws Exception
	{
//		String batchDate = request.getSettleDate();
//    	if(IfspDataVerifyUtil.isBlank(batchDate))
//    	{
//    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
//    	}
//		int count = this.getTotalResult(Constans.WX_SYS_NO, batchDate);
//
//		log.info("order count of channel 605 @date"+ batchDate +" is "+count);
//
//		//TODO:是否根据count数量进行分页,分线程..
//		List<BthMerInAccDtl> clearDataList = this.getDataList(Constans.WX_SYS_NO, batchDate);

//		for(BthMerInAccDtl clearData : clearDataList)
//		{
//			this.execute(Constans.CHANNEL_TYPE_WX, clearData);
//			this.updateOrderStlStatus(clearData);
//		}
		this.execute(Constans.CHANNEL_TYPE_WX, request);
		//应答
		CommonResponse commonResponse = new CommonResponse();

		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

		return commonResponse;
	}

	@Override
	public CommonResponse aliChannelClearing(BatchRequest request) throws Exception
	{
//		String batchDate = request.getSettleDate();
//    	if(IfspDataVerifyUtil.isBlank(batchDate))
//    	{
//    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
//    	}
//		int count = this.getTotalResult(Constans.ALI_SYS_NO, batchDate);
//
//		log.info("order count of channel 606 @date"+ batchDate +" is "+count);
//
//		//TODO:是否根据count数量进行分页,分线程..
//		List<BthMerInAccDtl> clearDataList = this.getDataList(Constans.ALI_SYS_NO, batchDate);
//
//		for(BthMerInAccDtl clearData : clearDataList)
//		{
//			this.execute(Constans.CHANNEL_TYPE_ALI, clearData);
//			this.updateOrderStlStatus(clearData);
//		}
		this.execute(Constans.CHANNEL_TYPE_ALI, request);

		//应答
		CommonResponse commonResponse = new CommonResponse();

		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

		return commonResponse;
	}

	@Override
	public CommonResponse unionpayChannelClearing(BatchRequest request) throws Exception
	{
		//应答
		CommonResponse commonResponse = new CommonResponse();

		//扫码支付订单
		commonResponse = this.unionSmClearing(request);
		//全渠道支付订单
		commonResponse = this.unionOnlineClearing(request);

		return commonResponse;
	}

	//银联全渠道支付订单清分
	public CommonResponse unionOnlineClearing(BatchRequest request) throws Exception
	{
//		String batchDate = request.getSettleDate();
//    	if(IfspDataVerifyUtil.isBlank(batchDate))
//    	{
//    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
//    	}
//		int count = this.getTotalResult(Constans.ALL_CHNL_UNION_SYS_NO, batchDate);
//
//		log.info("order count of channel 608 @date"+ batchDate +" is "+count);
//
//		//TODO:是否根据count数量进行分页,分线程..
//		List<BthMerInAccDtl> clearDataList = this.getDataList(Constans.ALL_CHNL_UNION_SYS_NO, batchDate);
//
//		for(BthMerInAccDtl clearData : clearDataList)
//		{
//			this.execute(Constans.CHANNEL_TYPE_UNIONPAY, clearData);
//			this.updateOrderStlStatus(clearData);
//		}
		request.setPagyNo(Constans.ALL_CHNL_UNION_SYS_NO);
		this.execute(Constans.CHANNEL_TYPE_UNIONPAY, request);

		//应答
		CommonResponse commonResponse = new CommonResponse();

		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

		return commonResponse;
	}

	//银联扫码支付订单清分
	public CommonResponse unionSmClearing(BatchRequest request) throws Exception
	{
//		String batchDate = request.getSettleDate();
//    	if(IfspDataVerifyUtil.isBlank(batchDate))
//    	{
//    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
//    	}
//		int count = this.getTotalResult(Constans.UNION_SYS_NO, batchDate);
//
//		log.info("order count of channel 605 @date"+ batchDate +" is "+count);
//
//		//TODO:是否根据count数量进行分页,分线程..
//		List<BthMerInAccDtl> clearDataList = this.getDataList(Constans.UNION_SYS_NO, batchDate);
//
//		for(BthMerInAccDtl clearData : clearDataList)
//		{
//			this.execute(Constans.CHANNEL_TYPE_UNIONPAY, request);
//			this.updateOrderStlStatus(clearData);
//		}
		request.setPagyNo(Constans.UNION_SYS_NO);
		this.execute(Constans.CHANNEL_TYPE_UNIONPAY, request);
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
		params.put("channel", channel);
		params.put("stlDate", date);
		params.put("stlStatus", Constans.SETTLE_STATUS_NOT_CLEARING);   //00-未清分
		count = bthMerInAccDtlDao.count("countClearingDataByChnlAndStldate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>check succuss orders count of  " + date + " is : " + count);
		return count;
	}
	/**
	 * 根据渠道抽取待清算数据
	 * @param channel 支付渠道
	 * @param date    清算日期
	 * @return
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	private List<BthMerInAccDtl> getDataList(String channel, String date)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("channel", channel);
		params.put("stlDate", date);
		params.put("stlStatus", Constans.SETTLE_STATUS_NOT_CLEARING);    //00-未清分
		List<BthMerInAccDtl> inAccDtlList = bthMerInAccDtlDao.selectList("selectClearingDataByChnlAndStldate", params);
		return inAccDtlList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(String channel, BatchRequest request) throws Exception
	{
		switch(channel)
		{
			case Constans.CHANNEL_TYPE_CORE:        //本行订单清分
				coreClearingExecutor.channelClearing(request);
				break;
			case Constans.CHANNEL_TYPE_WX:          //微信订单清分
				wxClearingExecutor.channelClearing(request);
				break;
			case Constans.CHANNEL_TYPE_ALI:         //支付宝订单清分
				aliClearingExecutor.channelClearing(request);
				break;
			case Constans.CHANNEL_TYPE_UNIONPAY:    //银联订单清分
				unionpayClearingExecutor.channelClearing(request);
				break;
		}
	}
	
	public int updateOrderStlStatus(BthMerInAccDtl inAccDtl)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("txnSeqId", inAccDtl.getTxnSeqId());
		params.put("stlStatus", Constans.SETTLE_STATUS_CLEARING);    //01-清分中
		return bthMerInAccDtlDao.update("updateOrderStlStatusByTxnSeqId", params);
	}
	
}
