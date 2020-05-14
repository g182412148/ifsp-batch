package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * <p>名称 : 本行短信验证请求报文 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/6/11  16:05 </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TpamCheckSmsReq extends CommonRequest {
    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    private String mobile;
    /**
     * 短信序号
     */
    @NotNull(message = "短信序列不能为空")
    private String authInfo;
    /**
     * 短信验证码
     */
    @NotNull(message = "短信验证码不能为空")
    private String cert;

    @Override
    public void valid() throws IfspValidException {

    }
}
