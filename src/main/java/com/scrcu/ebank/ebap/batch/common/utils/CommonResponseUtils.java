package com.scrcu.ebank.ebap.batch.common.utils;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.bean.request.CommRegiRequest;
import com.scrcu.ebank.ebap.batch.bean.response.CommRegiResponse;

/**
 * <p>名称 : 公共返回方法 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/6/14  15:40 </p>
 */
public class CommonResponseUtils {

    public static void setCommonResp(CommRegiRequest request, CommRegiResponse response){
        response.setReqSsn(request.getReqSsn());
        response.setReqTm(request.getReqTm());
        response.setRespSsn(IfspId.getUUID32());
        response.setRespTm(IfspDateTime.getYYYYMMDDHHMMSS());
    }
}
