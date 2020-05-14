package com.scrcu.ebank.ebap.batch.service;

import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.request.PagyServiceChargeRequest;
import com.scrcu.ebank.ebap.batch.bean.request.PagyTxnSsnRequest;
import com.scrcu.ebank.ebap.batch.bean.response.TpamTxnSsnResponse;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 名称：〈计算第三方通道手续费〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 说明：<br>
 */
public interface PagyServiceChargeService {

    CommonResponse wXCalculateServiceCharge(PagyServiceChargeRequest request) throws Exception;

    CommonResponse aLICalculateServiceCharge(PagyServiceChargeRequest request) throws Exception;

//    CommonResponse yLCalculateServiceCharge(PagyServiceChargeRequest request) throws Exception;
TpamTxnSsnResponse queryPaygTxnSsn(PagyTxnSsnRequest reqData);

}
