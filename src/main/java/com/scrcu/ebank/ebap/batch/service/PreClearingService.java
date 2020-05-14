package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 清分前的预处理，根据对账成功表当日对账成功订单取订单信息及商户信息
 * @author ydl
 *
 */
public interface PreClearingService 
{
	void dataGathering(BthChkRsltInfo chkSuccOrd, String batchDate) throws Exception;

	void dataGathering(List<BthChkRsltInfo> chkSuccOrdList, String batchDate) throws Exception;
	
	CommonResponse prepare(BatchRequest request) throws Exception ;

	void calcMerFee4SubOrder(PayOrderInfo orderInfo, PaySubOrderInfo subOrder);

	BigDecimal calMerFee4$Order(PayOrderInfo orderInfo);
}
