package com.scrcu.ebank.ebap.batch.message.impl;

import com.alibaba.fastjson.JSON;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.common.client.IServiceInvoker;
import com.scrcu.ebank.common.service.ServiceRequest;
import com.scrcu.ebank.common.service.ServiceResponse;
import com.scrcu.ebank.ebap.batch.bean.request.*;
import com.scrcu.ebank.ebap.batch.bean.response.*;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.message.BaseBizService;
import com.scrcu.ebank.ebap.batch.message.SmsService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBaseException;
import com.scrcu.ebank.ebap.exception.template.IfspMessageCoreUtil;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {
    
    Logger log = IfspLoggerFactory.getLogger(SmsServiceImpl.class);

    @Resource
    private BaseBizService baseBizService;
    @Resource
    private IServiceInvoker serviceInvoker;

    @Override
    public SendSmsResp sendOTP(SendSmsReq request) {
        //校验报文
        request.valid();
        //响应报文
        SendSmsResp sendSmsResp = new SendSmsResp();
        //================调用认证中心接口=====================
        //组装请求报文
        TpamSendSmsReq tpamSendSmsReq = new TpamSendSmsReq();
        tpamSendSmsReq.setBehavCode(request.getBehavCode());
        tpamSendSmsReq.setBusinessContent(request.getBusinessContent());
        tpamSendSmsReq.setMobile(request.getMobile());
        tpamSendSmsReq.setOpenBrc(request.getOpenBrc());
        //调用行内dubbo
        TpamSendSmsResp resp = new TpamSendSmsResp();
        try {
            //设置请求参数
            ServiceRequest serviceRequest = new ServiceRequest();
            //设置head
            serviceRequest.setServiceId("sendOTP");
            serviceRequest.setAppId(IfspMessageCoreUtil.getMessage("appId"));
            log.info("======================="+request.getMobile());
            //设置body
            Map<String, Object> tpamReqMap = IfspFastJsonUtil.objectTomap(tpamSendSmsReq);
            for (Map.Entry<String,Object> entry :
                    tpamReqMap.entrySet()) {
                serviceRequest.setBodyValue(entry.getKey(),entry.getValue());
            }
            ServiceResponse rsp = serviceInvoker.call(serviceRequest);

            //响应信息获取
            resp.setRtnCont((String) rsp.getBodyValue("rtnCont"));
            resp.setAuthSeqNo((String) rsp.getBodyValue("authSeqNo"));
            resp.setSmsSeq((String) rsp.getBodyValue("smsSeq"));
            log.info("========本行响应码:[{}],本行响应信息[{}]!=========",rsp.getRespCode(),rsp.getRespMsg());
            resp.setRespCode(rsp.getRespCode());
            resp.setRespMsg(rsp.getRespMsg());

            log.info("===========结果========="+JSON.toJSONString(resp));

        } catch (Exception e){
            log.error("本行请求异常[" + e.getMessage() + "]", e);
            sendSmsResp.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            sendSmsResp.setRespMsg("本行请求异常[" + e.getMessage() + "]");
            return sendSmsResp;
        }
        //组装响应报文
        sendSmsResp.setAuthSeqNo(resp.getAuthSeqNo());//认证流水号
        sendSmsResp.setRtnCont(resp.getRtnCont());//返回内容 短信动态码的序号、密码为真随机数、安全问题序号及问题
        sendSmsResp.setSmsSeq(resp.getSmsSeq());//短信序号
        // 响应报文转换
        if (IfspDataVerifyUtil.equals(IfspMessageCoreUtil.getMessage("smsSucc"),resp.getRespCode())){
            log.info(resp.getRespCode()+"|"+IfspMessageCoreUtil.getMessage("smsSucc"));
            sendSmsResp.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            sendSmsResp.setRespMsg("短信发送成功");
        } else {
            log.info(resp.getRespCode()+"|"+IfspMessageCoreUtil.getMessage("smsSucc"));
            sendSmsResp.setRespCode(RespConstans.RESP_FAIL.getCode());
            sendSmsResp.setRespMsg("短信发送失败");
        }
        return sendSmsResp;
    }

    /**
     * 短信验证
     * @param request 请求参数
     * @return 响应报文
     */
    @Override
    public CheckSmsResp checkOTP(CheckSmsReq request) {
        //校验报文
        request.valid();
        //响应报文
        CheckSmsResp checkSmsResp = new CheckSmsResp();
        //====================调用认证中心接口=====================
        //组装请求报文
        TpamCheckSmsReq tpamCheckSmsReq = new TpamCheckSmsReq();
        tpamCheckSmsReq.setAuthInfo(request.getAuthInfo());
        tpamCheckSmsReq.setCert(request.getCert());
        tpamCheckSmsReq.setMobile(request.getMobile());
        //行内dubbo调用
        TpamCheckSmsResp tpamCheckSmsResp = new TpamCheckSmsResp();//本行通讯响应报文
        try {
            //设置请求参数
            ServiceRequest serviceRequest = new ServiceRequest();
            //设置head
            serviceRequest.setServiceId("checkOTP");
            serviceRequest.setAppId(IfspMessageCoreUtil.getMessage("appId"));
            log.info("======================="+request.getMobile());
            //设置body
            Map<String, Object> tpamReqMap = IfspFastJsonUtil.objectTomap(tpamCheckSmsReq);
            for (Map.Entry<String,Object> entry :
                    tpamReqMap.entrySet()) {
                serviceRequest.setBodyValue(entry.getKey(),entry.getValue());
            }

            ServiceResponse rsp = serviceInvoker.call(serviceRequest);
            log.info("==============报文接受=======");

            log.info("========本行响应码:[{}],本行响应信息[{}]!=========",rsp.getRespCode(),rsp.getRespMsg());
            tpamCheckSmsResp.setRespCode(rsp.getRespCode());
            tpamCheckSmsResp.setRespMsg(rsp.getRespMsg());
            log.info("===========响应结果========="+JSON.toJSONString(tpamCheckSmsResp));

        } catch (Exception e){
            log.error("本行请求异常[" + e.getMessage() + "]", e);
            checkSmsResp.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            checkSmsResp.setRespMsg("本行请求异常[" + e.getMessage() + "]");
            return checkSmsResp;
        }
        //组装响应报文
        checkSmsResp.setRespCode(tpamCheckSmsResp.getRespCode());
        checkSmsResp.setRespMsg(tpamCheckSmsResp.getRespMsg());
        // 响应报文转换
        if (IfspDataVerifyUtil.equals(IfspMessageCoreUtil.getMessage("smsSucc"),tpamCheckSmsResp.getRespCode())){
            checkSmsResp.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            checkSmsResp.setRespMsg("短信验证成功");
        } else if (IfspDataVerifyUtil.equals(IfspMessageCoreUtil.getMessage("smsOutTime"),tpamCheckSmsResp.getRespCode())){
            checkSmsResp.setRespCode(RespConstans.RESP_FAIL.getCode());
            checkSmsResp.setRespMsg("短信验证码超时");
        } else {
            checkSmsResp.setRespCode(RespConstans.RESP_FAIL.getCode());
            checkSmsResp.setRespMsg("短信验证失败");
        }

        return checkSmsResp;
    }

    /**
     * 发送普通OTP
     *
     * @param smsSendReq 请求报文
     * @return 响应报文
     */
    @Override
    public NormalSmsSendResp sendNormalOTP(NormalSmsSendReq smsSendReq) {
        // 校验报文
        smsSendReq.valid();
        // 响应报文
        NormalSmsSendResp normalSmsSendResp = new NormalSmsSendResp();
        // 短信发送请求
        try{
            normalSmsSendResp = baseBizService.sendNormalOTP(smsSendReq);
        }catch (IfspBaseException e) {
            log.error("处理失败:", e.getCode() + "|" + e.getMessage());
            normalSmsSendResp.setRespCode(e.getCode());
            normalSmsSendResp.setRespMsg(e.getMessage());
        } catch (Exception e) {
            log.error("未知错误:", e);
            normalSmsSendResp.setRespCode(SystemConfig.getSysErrorCode());
            normalSmsSendResp.setRespMsg(SystemConfig.getSysErrorMsg());
        }
        return normalSmsSendResp;
    }

    @Override
    public CommonResponse sendMsg(Map<String, Object> params,String templateFileName) {
        // 响应报文
        CommonResponse response = new CommonResponse();
        // 短信发送请求
        try{
            response = baseBizService.sendMsg(params,templateFileName);
        }catch (IfspBaseException e) {
            log.error("处理失败:", e.getCode() + "|" + e.getMessage());
            response.setRespCode(e.getCode());
            response.setRespMsg(e.getMessage());
        } catch (Exception e) {
            log.error("未知错误:", e);
            response.setRespCode(SystemConfig.getSysErrorCode());
            response.setRespMsg(SystemConfig.getSysErrorMsg());
        }
        return response;
    }
}
