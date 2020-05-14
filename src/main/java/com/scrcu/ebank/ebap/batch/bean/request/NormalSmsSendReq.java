package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>名称 : 普通短信请求报文 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/7/23 </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NormalSmsSendReq extends CommonRequest {
    /**
     * 模板编号
     */
    private String tempCode;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 法人姓名
     */
    private String legalNm;
    /**
     * 商户名
     */
    private String mchtNm;
    /**
     * 密码
     */
    private String pwd;
    /**
     * 付费机构号
     */
    private String brNo;


    @Override
    public void valid() throws IfspValidException {
        if (IfspDataVerifyUtil.isEmpty(tempCode)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "模板编号(tempCode)不允许为空");
        } else if (IfspDataVerifyUtil.isEmpty(phone)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "手机号(phone)不允许为空");
        } else if (IfspDataVerifyUtil.isEmpty(legalNm)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "法人姓名(legalNm)不允许为空");
        } else if (IfspDataVerifyUtil.isEmpty(mchtNm)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "商户名(mchtNm)不允许为空");
        } else if (IfspDataVerifyUtil.isEmpty(pwd)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "初始密码(pwd)不允许为空");
        } else if (IfspDataVerifyUtil.isEmpty(brNo)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "付费机构号(brNo)不允许为空");
        }
    }
}
