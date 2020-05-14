package com.scrcu.ebank.ebap.batch.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.request.PagyTxnSsnRequest;
import com.scrcu.ebank.ebap.batch.bean.response.TpamTxnSsnResponse;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.PagyServiceChargeRequest;
import com.scrcu.ebank.ebap.batch.service.PagyServiceChargeService;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;

/**
 * 名称：〈计算第三方通道手续费〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 说明：<br>
 */
@Controller
public class PagyServiceChargeController {

	@Resource
	private PagyServiceChargeService pagyServiceChargeService;

	@SOA("001.WXCalculateServiceCharge")
	@Explain(name = "计算微信手续费", logLv = LogLevel.DEBUG)
	public CommonResponse WXCalculateServiceCharge(@IfspValid PagyServiceChargeRequest request) throws Exception {
        CommonResponse resultMap=pagyServiceChargeService.wXCalculateServiceCharge(request);

		return resultMap;

	}
	
	
	@SOA("002.ALICalculateServiceCharge")
	@Explain(name = "计算支付宝手续费", logLv = LogLevel.DEBUG)
	public CommonResponse ALICalculateServiceCharge(@IfspValid PagyServiceChargeRequest request) throws Exception {
        CommonResponse resultMap=pagyServiceChargeService.aLICalculateServiceCharge(request);

		return resultMap;

	}
	
	@SOA("003.YLCalculateServiceCharge")
	@Explain(name = "计算银联二维码手续费", logLv = LogLevel.DEBUG)
	public CommonResponse YLCalculateServiceCharge(@IfspValid PagyServiceChargeRequest request) throws Exception {
        // todo 默认成功
//		Map<String, Object> resultMap=pagyServiceChargeService.yLCalculateServiceCharge(request);
        CommonResponse response= new CommonResponse();
		return response;
	}
	/**
	 * 受理通道信息表查询
	 *
	 * @param request 请求报文
	 * @return
	 */
	@SOA("004.queryPaygTxnSsn")
	@Explain(name = "受理queryPaygTxnSsn查询请求", logLv = LogLevel.DEBUG)
	public TpamTxnSsnResponse queryPaygTxnSsn(@IfspValid PagyTxnSsnRequest request) throws Exception {
		TpamTxnSsnResponse map = pagyServiceChargeService.queryPaygTxnSsn(request);
		return map;
	}

}
