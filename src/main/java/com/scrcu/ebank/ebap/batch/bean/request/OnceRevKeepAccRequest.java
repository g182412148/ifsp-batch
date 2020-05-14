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
public class OnceRevKeepAccRequest extends CommonRequest {
    /**
     * 通道支付请求流水号
     */
    private String pagyPayTxnSsn;
    /**
     * 通道支付请求流水时间
     */
    private String pagyPayTxnTm;

    /**
     * 原通道支付请求流水号
     */
    private String origPagyPayTxnSsn;

    /**
     * 抹账标志 S-自动，M-手工
     */
    private String erasPayInd;

    public String getErasPayInd() {
        return erasPayInd;
    }

    public void setErasPayInd(String erasPayInd) {
        this.erasPayInd = erasPayInd;
    }

    public String getPagyPayTxnSsn() {
        return pagyPayTxnSsn;
    }

    public void setPagyPayTxnSsn(String pagyPayTxnSsn) {
        this.pagyPayTxnSsn = pagyPayTxnSsn;
    }

    public String getPagyPayTxnTm() {
        return pagyPayTxnTm;
    }

    public void setPagyPayTxnTm(String pagyPayTxnTm) {
        this.pagyPayTxnTm = pagyPayTxnTm;
    }

    public String getOrigPagyPayTxnSsn() {
        return origPagyPayTxnSsn;
    }

    public void setOrigPagyPayTxnSsn(String origPagyPayTxnSsn) {
        this.origPagyPayTxnSsn = origPagyPayTxnSsn;
    }


    @Override
    public void valid() throws IfspValidException {
        if(IfspDataVerifyUtil.isBlank(pagyPayTxnSsn)){
            log.warn("请求流水号pagyPayTxnSsn不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[请求流水号pagyPayTxnSsn不能为空]");
        }else{
            if(pagyPayTxnSsn.length()!=20){
                log.warn("请求流水号pagyPayTxnSsn长度("+pagyPayTxnSsn.length()+")错误,长度只能为20位");
                throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[请求流水号pagyPayTxnSsn长度("+pagyPayTxnSsn.length()+")错误,长度只能为20位]");
            }
        }

        if(IfspDataVerifyUtil.isBlank(pagyPayTxnTm)){
            log.warn("请求订单时间pagyPayTxnTm不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[请求订单时间pagyPayTxnTm不能为空]");
        }else{
            if (!DateUtil.checkDateTime(pagyPayTxnTm, IfspDateTime.YYYYMMDDHHMMSS)) {
                log.warn("请求订单时间pagyPayTxnTm格式(yyyyMMddHHmmss)错误:[" + pagyPayTxnTm + "]");
                throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[请求订单时间pagyPayTxnTm格式(yyyyMMddHHmmss)错误:[" + pagyPayTxnTm + "]]");
            }
        }


        if(IfspDataVerifyUtil.isBlank(origPagyPayTxnSsn)){
            log.warn("原交易流水号origPagyPayTxnSsn不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[原交易流水号origPagyPayTxnSsn不能为空]");
        }else{
            if(origPagyPayTxnSsn.length()!=20){
                log.warn("原交易流水号origPagyPayTxnSsn长度("+origPagyPayTxnSsn.length()+")错误,长度只能为20位");
                throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[原交易流水号origPagyPayTxnSsn长度("+origPagyPayTxnSsn.length()+")错误,长度只能为20位]");
            }
        }

        if (IfspDataVerifyUtil.isBlank(erasPayInd)){
            log.warn("抹账标志erasPayInd不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[抹账标志erasPayInd不能为空]");
        }



    }
}
