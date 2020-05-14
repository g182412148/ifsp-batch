package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.NewDayTimeKeepAcctVo;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * 日间记账请求报文
 * @author ljy
 */

@Slf4j
@Data
public class NewDayTimeKeepAcctRequest extends CommonRequest {

    /**
     * 请求流水号
     */
    private String reqSsn;

    /**
     * 请求时间
     */
    private String reqTm;

    /**
     * 订单号
     */
    private String orderSsn;

    /**
     * 订单时间
     */
    private String orderTm;

    /**
     * 批次号
     */
    private String verNo;

    private List<NewDayTimeKeepAcctVo> keepAcctList ;


    @Override
    public void valid() throws IfspValidException {

        if(IfspDataVerifyUtil.isBlank(reqSsn)){
            log.warn("请求流水号reqSsn不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[请求流水号reqSsn不能为空]");
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


        if(IfspDataVerifyUtil.isBlank(orderSsn)){
            log.warn("请求流水号orderSsn不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[请求订单号orderSsn不能为空]");
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


        if(IfspDataVerifyUtil.isBlank(verNo)){
            log.warn("订单请求批次号verNo不能为空" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[订单请求批次号verNo不能为空]");
        }



        if (IfspDataVerifyUtil.isEmptyList(keepAcctList)){
            log.warn("记账信息不能为空: [keepAcctList]" );
            throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ":[记账信息keepAcctList不能为空]");
        }else {
            for (NewDayTimeKeepAcctVo dayTimeKeepAcctVo : keepAcctList) {
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getOutAccNo())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[借方账户outAccNo不能为空]");
                }
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getOutAccType())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[借方账户类型outAccType不能为空]");
                }
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getInAccNo())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[贷方账户inAccNo不能为空]");
                }
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getInAccType())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[贷方账户类型inAccType不能为空]");
                }
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getTxnDesc())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[交易描述txnDesc不能为空]");
                }
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getTransAmt())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[交易金额transAmt不能为空]");
                }else{
                    try {
                        BigDecimal bigDecimal = new BigDecimal(dayTimeKeepAcctVo.getTransAmt());
                        if (BigDecimal.ZERO.compareTo(bigDecimal) > 0){
                            throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[交易金额transAmt不能小于0]");
                        }
                    } catch (NumberFormatException e) {
                        throw new IfspBizException(IfspRespCodeEnum.RESP_1005.getCode(), IfspRespCodeEnum.RESP_1005.getDesc() + ":[交易金额transAmt格式错误]:["+dayTimeKeepAcctVo.getTransAmt()+"]");
                    }
                }
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getTxnCcyType())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[交易币种txnCcyType不能为空]");
                }
                /*************** 记账表额外需要字段 ************/
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getSeq())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[划账序号seq不能为空]");
                }
                /*******************唯一索引不能为空*********************/
                if(IfspDataVerifyUtil.isBlank(dayTimeKeepAcctVo.getUniqueSsn())){
                    throw new IfspBizException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() +":[记账唯一标志uniqueSsn不能为空]");
                }

            }

        }

    }
}
