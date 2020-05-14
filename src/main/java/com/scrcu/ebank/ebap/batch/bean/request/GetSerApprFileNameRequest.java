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
public class GetSerApprFileNameRequest extends CommonRequest {
    private String clientId;
        private String reqSsn;
    private String reqTime;
    private String serviceId;
    private String serviceType;
    private String queryDate;
    private String reqChnl;
    @Override
    public void valid() throws IfspValidException {
        if(IfspDataVerifyUtil.isBlank(queryDate)){
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc()+"queryDate is null");
        }
        if(IfspDataVerifyUtil.isBlank(serviceId)){
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc()+"serviceId is null");
        }
        if(IfspDataVerifyUtil.isBlank(clientId)){
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc()+"clientId is null");
        }
        if(IfspDataVerifyUtil.isBlank(reqChnl)){
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc()+"reqChnl is null");
        }
    }
}
