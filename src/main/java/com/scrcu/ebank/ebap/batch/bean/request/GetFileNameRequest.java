package com.scrcu.ebank.ebap.batch.bean.request;/**
 * Created by Administrator on 2019-04-30.
 */

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-04-30 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Data
public class GetFileNameRequest extends CommonRequest {
    private String settleDate;

    private String mchtNo;

    private String clientId;
    @Override
    public void valid() throws IfspValidException {
        if(IfspDataVerifyUtil.isBlank(settleDate)){
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc()+"settleDate is nll");
        }
        if(IfspDataVerifyUtil.isBlank(mchtNo)){
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc()+"mchtNo is nll");
        }
        if(IfspDataVerifyUtil.isBlank(clientId)){
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc()+"clientId is nll");
        }
    }
}
