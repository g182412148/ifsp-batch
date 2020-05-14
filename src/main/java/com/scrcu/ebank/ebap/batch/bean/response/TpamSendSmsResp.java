package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>名称 : 本行短信发送响应报文 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/6/11  15:38 </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TpamSendSmsResp extends CommonResponse {
    /**
     * 返回内容 短信动态码的序号、密码为真随机数、安全问题序号及问题
     */
    private String rtnCont;
    /**
     * 认证流水号
     */
    private String authSeqNo;
    /**
     * 短信序号
     */
    private String smsSeq;
}
