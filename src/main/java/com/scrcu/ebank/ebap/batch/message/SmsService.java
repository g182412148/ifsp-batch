package com.scrcu.ebank.ebap.batch.message;


import com.scrcu.ebank.ebap.batch.bean.request.CheckSmsReq;
import com.scrcu.ebank.ebap.batch.bean.request.NormalSmsSendReq;
import com.scrcu.ebank.ebap.batch.bean.request.SendSmsReq;
import com.scrcu.ebank.ebap.batch.bean.response.CheckSmsResp;
import com.scrcu.ebank.ebap.batch.bean.response.NormalSmsSendResp;
import com.scrcu.ebank.ebap.batch.bean.response.SendSmsResp;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.util.Map;

/**
 * 短信
 */
public interface SmsService {


    /**
     * 发送OTP
     * @param sendSmsReq
     * @return
     */
    SendSmsResp sendOTP(SendSmsReq sendSmsReq);

    /**
     * 校验OTP
     * @param checkSmsReq
     * @return
     */
    CheckSmsResp checkOTP(CheckSmsReq checkSmsReq);

    /**
     * 发送普通OTP
     * @param smsSendReq
     * @return
     */
    NormalSmsSendResp sendNormalOTP(NormalSmsSendReq smsSendReq);


    /**
     * 发送短信通用方法，需要指定参数及短信模板编号及模板文件名称
     * @param params
     * @param templateFileName : 短信模板ftl文件名称，不带后缀名
     * @return
     */
    CommonResponse sendMsg(Map<String,Object> params,String templateFileName);
}
