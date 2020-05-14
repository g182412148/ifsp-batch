package com.scrcu.ebank.ebap.batch.bean.request;/**
 * Created by Administrator on 2019-08-01.
 */

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import lombok.extern.log4j.Log4j;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-08-01 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Data
@Log4j
public class KeepAcctCommonRequest extends CommonRequest {

    protected String reqSsn;

    protected String reqTm;

    protected String orderSsn;

    protected String orderTm;

    @Override
    public void valid() throws IfspValidException {
        if(IfspDataVerifyUtil.isBlank(reqSsn)){
            log.warn("请求流水号reqSsn不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[请求流水号reqSsn不能为空]");
        }else{
            if(reqSsn.length()>32){
                log.warn("请求流水号reqSsn长度("+reqSsn.length()+")超长,最大长度32");
                throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[请求流水号reqSsn长度("+reqSsn.length()+")超长,最大长度32]");
            }
        }

        if(IfspDataVerifyUtil.isBlank(reqTm)){
            log.warn("请求订单时间reqTm不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[请求订单时间reqTm不能为空]");
        }else{
            if (!DateUtil.checkDateTime(reqTm, IfspDateTime.YYYYMMDDHHMMSS)) {
                log.warn("请求订单时间reqTm格式(yyyyMMddHHmmss)错误:[" + reqTm + "]");
                throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[请求订单时间reqTm格式(yyyyMMddHHmmss)错误:[" + reqTm + "]]");
            }
        }
    }
}
