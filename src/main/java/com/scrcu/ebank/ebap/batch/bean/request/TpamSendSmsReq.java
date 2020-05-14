package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * <p>名称 : 本行短信发送请求报文 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/6/11  15:28 </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TpamSendSmsReq extends CommonRequest {
    /**
     * 交易场景码
     */
    private String behavCode;
    /**
     * 手机号码
     */
    @NotNull(message = "手机号码不能为空")
    private String mobile;
    /**
     * 业务内容
     */
    private String businessContent;
    /**
     * 短信付费机构号
     */
    private String openBrc;

    @Override
    public void valid() throws IfspValidException {

    }
}
