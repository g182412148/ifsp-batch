package com.scrcu.ebank.ebap.batch.message;


import com.scrcu.ebank.ebap.batch.bean.request.NormalSmsSendReq;
import com.scrcu.ebank.ebap.batch.bean.response.NormalSmsSendResp;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.util.Map;

/**
 * <p>名称 : 非核心交易本行通道通讯请求 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/7/11 </p>
 */
public interface BaseBizService {

    /**
     * 发送普通OTP
     * @param smsSendReq
     * @return
     */
    NormalSmsSendResp sendNormalOTP(NormalSmsSendReq smsSendReq);

    /**
     * 发送短信通用方法
     * @param datas ： 短信模板参数
     * @param templateName ： 短信模板名称
     * @return
     */
    CommonResponse sendMsg(Map<String,Object> datas, String templateName);

}
