package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeepAccRevRequest extends CommonRequest {
    private String orderSsn;

    private String orderTm;

    private String verNo;

    public String getVerNo() {
        return verNo;
    }

    public void setVerNo(String verNo) {
        this.verNo = verNo;
    }

    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn;
    }

    public String getOrderTm() {
        return orderTm;
    }

    public void setOrderTm(String orderTm) {
        this.orderTm = orderTm;
    }

    @Override
    public void valid() throws IfspValidException {
        if(IfspDataVerifyUtil.isBlank(orderSsn)){
            log.warn("请求流水号orderSsn不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[请求流水号orderSsn不能为空]");
        }else{
            if(orderSsn.length()>32){
                log.warn("请求流水号orderSsn长度("+orderSsn.length()+")超长,最大长度32");
                throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[请求流水号orderSsn长度("+orderSsn.length()+")超长,最大长度32]");
            }
        }

        if(IfspDataVerifyUtil.isBlank(orderTm)){
            log.warn("请求订单时间orderTm不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[请求订单时间orderTm不能为空]");
        }else{
            if (!DateUtil.checkDateTime(orderTm, IfspDateTime.YYYYMMDDHHMMSS)) {
                log.warn("请求订单时间orderTm格式(yyyyMMddHHmmss)错误:[" + orderTm + "]");
                throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[请求订单时间orderTm格式(yyyyMMddHHmmss)错误:[" + orderTm + "]]");
            }
        }

    }
}
