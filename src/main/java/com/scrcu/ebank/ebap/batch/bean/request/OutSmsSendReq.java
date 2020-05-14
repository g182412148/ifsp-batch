package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 对外短信发送通用请求报文
 * M.chen
 * 2019/3/25 17:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OutSmsSendReq extends CommonRequest {
    /**
     * 模板编号
     */
    private String tempCode;
    /**
     * 付费机构号
     */
    private String brNo;

    /**
     * 模板参数
     */
    private Map<String,Object> tempData;

    @Override
    public void valid() throws IfspValidException {
        if (IfspDataVerifyUtil.isEmpty(tempCode)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "模板编号(tempCode)不允许为空");
        } else if (IfspDataVerifyUtil.isEmpty(brNo)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "付费机构号(brNo)不允许为空");
        }
    }
}
