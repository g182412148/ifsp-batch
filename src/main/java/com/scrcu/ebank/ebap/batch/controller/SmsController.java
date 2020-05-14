package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.CheckSmsReq;
import com.scrcu.ebank.ebap.batch.bean.request.NormalSmsSendReq;
import com.scrcu.ebank.ebap.batch.bean.request.SendSmsReq;
import com.scrcu.ebank.ebap.batch.bean.response.CheckSmsResp;
import com.scrcu.ebank.ebap.batch.bean.response.NormalSmsSendResp;
import com.scrcu.ebank.ebap.batch.bean.response.SendSmsResp;
import com.scrcu.ebank.ebap.batch.message.SmsService;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p>名称 : 短信发送与验证控制器 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/6/15  10:55 </p>
 */
//@Controller  内不使用，暂不对外提供
public class SmsController {
    /**
     * 本行发送短信入口
     */
    @Resource
    private SmsService smsService;

    /**
     * 本行短信发送
     * @param req 短信发送请求报文
     * @return 短信发送响应报文
     */
    @SOA(name = "001.sendOTP", version = "1.0.0", group = "batch")
    @Explain(name = "本行短信发送", logLv = LogLevel.INFO)
    @ResponseBody
    public SendSmsResp ibankSendSmsCode(SendSmsReq req){
        return  smsService.sendOTP(req);
    }

    /**
     * 本行普通短信发送[初始化密码]
     * @param req 短信发送请求报文
     * @return 短信发送响应报文
     */
    @SOA(name = "001.sendNormalOTP", version = "1.0.0", group = "batch")
    @Explain(name = "本行短信发送", logLv = LogLevel.INFO)
    @ResponseBody
    public NormalSmsSendResp ibankSendSmsCode(NormalSmsSendReq req){
        return  smsService.sendNormalOTP(req);
    }

    /**
     * 本行短信验证
     * @param req 短信验证请求报文
     * @return 短信验证响应报文
     */
    @SOA(name = "001.checkOTP", version = "1.0.0", group = "batch")
    @Explain(name = "本行短信验证", logLv = LogLevel.INFO)
    @ResponseBody
    public CheckSmsResp ibankCheckSmsCode(CheckSmsReq req){
        return  smsService.checkOTP(req);
    }

}
