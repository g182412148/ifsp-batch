package com.scrcu.ebank.ebap.batch.message.impl;

import com.alibaba.fastjson.JSON;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.message.IfspXmlDataUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.NormalSmsSendReq;
import com.scrcu.ebank.ebap.batch.bean.response.NormalSmsSendResp;
import com.scrcu.ebank.ebap.batch.message.BaseBizService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.common.msg.IfspFreeMarkerUtils;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.template.IfspMessageCoreUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>名称 : 非核心交易本行通道通讯请求 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/7/11 </p>
 */
@Slf4j
@Service
public class BaseBizServiceImpl implements BaseBizService {

    /**
     * 发送普通OTP
     *
     * @param smsSendReq 请求报文
     * @return 响应报文
     */
    @Override
    public NormalSmsSendResp sendNormalOTP(NormalSmsSendReq smsSendReq) {
        // 响应报文
        NormalSmsSendResp smsSendResp = new NormalSmsSendResp();
        // ######################组装请求报文##########################
        // 请求报文转换map
        Map<String, Object> tpamReqMap = IfspFastJsonUtil.objectTomap(smsSendReq);
        //构建报文头的必输项,hosthead
        tpamReqMap.put("srcSystem", IfspMessageCoreUtil.getMessage("srcSystem")); //渠道号
        tpamReqMap.put("timeStamp", String.valueOf(System.currentTimeMillis())); //请求日期 时间戳

        log.info("#############请求Map###########" + tpamReqMap);

        // ##################本行交易通讯请求##########################
        // 通过freemarker生成xml TODO:这样每个短信模板都需要单独配置，考虑把模板名称作为参数传入
        String xml = IfspFreeMarkerUtils.getftlToMsg("smsSendNormal", tpamReqMap);
        log.info("short msg request ：" + xml);
        // 短信发送请求
        String resp = this.send(xml);
        log.info(resp);
        // 解析报文
        Map<String, Object> map = null;
        try {
            map = IfspXmlDataUtil.XMLToMapAll(resp);
        } catch (Exception e) {
            log.info("短信平台报文解析失败", e.getMessage());
        }
        log.info(JSON.toJSONString(map));
        Map<String, Object> respCodeMap = (Map<String, Object>) map.get("RESULTCODE");
        Map<String, Object> respMsgMap = (Map<String, Object>) map.get("RESULTINFO");
        String respCode = (String) respCodeMap.get("RESULTCODE");
        String respMsg = (String) respMsgMap.get("RESULTINFO");
        if (IfspDataVerifyUtil.equals(IfspMessageCoreUtil.getMessage("smsRespCode"), respCode)) {//短信响应成功
            smsSendResp.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
        } else {
            smsSendResp.setRespCode(respCode);
        }
        smsSendResp.setRespMsg(respMsg);

        return smsSendResp;
    }

    /**
     * 发送短信通用方法
     * @param datas ： 短信模板参数
     * @param templateName ： 短信模板名称
     * @return
     */
    @Override
    public CommonResponse sendMsg(Map<String, Object> datas, String templateFileName) {
        // 响应报文
        CommonResponse smsSendResp = new CommonResponse();
        // ######################组装请求报文##########################
        // 请求报文转换map
        Map<String, Object> tpamReqMap = datas;
        //构建报文头的必输项,hosthead
        tpamReqMap.put("srcSystem", IfspMessageCoreUtil.getMessage("srcSystem")); //渠道号
        tpamReqMap.put("timeStamp", String.valueOf(System.currentTimeMillis())); //请求日期 时间戳

        log.info("#############请求Map###########" + tpamReqMap);

        // ##################本行交易通讯请求##########################
        // 通过freemarker生成xml TODO:这样每个短信模板都需要单独配置，考虑把模板名称作为参数传入
        String xml = IfspFreeMarkerUtils.getftlToMsg(templateFileName, tpamReqMap);
        log.info("short msg request ：" + xml);
        // 短信发送请求
        String resp = this.send(xml);
        log.info(resp);
        // 解析报文
        Map<String, Object> map = null;
        try {
            map = IfspXmlDataUtil.XMLToMapAll(resp);
        } catch (Exception e) {
            log.info("短信平台报文解析失败", e.getMessage());
        }
        log.info(JSON.toJSONString(map));
        Map<String, Object> respCodeMap = (Map<String, Object>) map.get("RESULTCODE");
        Map<String, Object> respMsgMap = (Map<String, Object>) map.get("RESULTINFO");
        String respCode = (String) respCodeMap.get("RESULTCODE");
        String respMsg = (String) respMsgMap.get("RESULTINFO");
        if (IfspDataVerifyUtil.equals(IfspMessageCoreUtil.getMessage("smsRespCode"), respCode)) {//短信响应成功
            smsSendResp.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
        } else {
            smsSendResp.setRespCode(respCode);
        }
        smsSendResp.setRespMsg(respMsg);

        return smsSendResp;
    }

    /**
     * 发送
     *
     * @param xml 经过模板组装过的xml
     */
    protected String send(String xml) {
        //短信平台地址
        String ip = IfspMessageCoreUtil.getMessage("smsIp");
        String port = IfspMessageCoreUtil.getMessage("smsPort");
        PrintWriter writer = null;
        StringBuilder resultContent = new StringBuilder();
        char[] lenChar = new char[5];
        BufferedReader br = null;
        Socket socket = null;
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            int len = new ByteArrayInputStream(xml.getBytes("GBK")).available();
            String contentLen = String.format("%05d", len);

            log.debug("======================= SMS Request Start ======================\r\n".concat(contentLen + xml));
            log.debug("======================= SMS Request End ========================");

            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK"), true);
            writer.print(contentLen + xml);
            writer.flush();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
            br.read(lenChar, 0, 5);
            String contentLine = br.readLine();
            if (contentLine == null) {
                resultContent.append("failed!");
            }
            while ((contentLine = br.readLine()) != null) {
                resultContent.append(contentLine);
                if (contentLine.equals("</EMD>")) {
                    break;
                } else {
                    resultContent.append("\r\n");
                }
            }

            log.debug("======================= SMS Response Start ======================\r\n");
            log.debug("======================= SMS Response End ========================");
        } catch (IOException e) {
            log.error("发送短信出错：".concat(e.getMessage()));
        } finally {
            try {
                if (writer != null)
                    writer.close();
                if (socket != null)
                    socket.close();
                if (br != null)
                    br.close();
            } catch (Exception e) {
                log.error("关闭socket出错：".concat(e.getMessage()));
            }
        }

        return resultContent.toString();
    }
}
