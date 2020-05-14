package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.DayTimeKeepAcctVo;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Iterator;
import java.util.List;

/**
 * 日间记账请求报文
 * @author ljy
 */

@Data
public class DayTimeKeepAcctRequest extends CommonRequest {

    /***********      同步记账还是异步     **********/
    @NotEmpty(message = "记账标志不能为空:  [isSync]")
    private  String isSync;

    /**
     * 异步通知地址
     */
    private String noticeAddr;

    @NotEmpty(message = "记账信息不能为空: [keepAcctList]")
    private List<DayTimeKeepAcctVo> keepAcctList ;


    @Override
    public void valid() throws IfspValidException {
        Iterator<DayTimeKeepAcctVo> iterator = keepAcctList.iterator();
        while (iterator.hasNext()) {
            DayTimeKeepAcctVo next = iterator.next();

            /****************  调本行通道所需字段 ***********/
            if(IfspDataVerifyUtil.isBlank(next.getOrderSsn())){
                throw new IfspBizException("9999","订单号不能为空: [orderSsn]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getOrderTm())){
                throw new IfspBizException("9999","订单时间不能为空: [orderTm]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getOutAccNo())){
                throw new IfspBizException("9999","借方账户不能为空: [outAccNo]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getOutAccType())){
                throw new IfspBizException("9999","借方账户类型不能为空: [outAccType]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getInAccNo())){
                throw new IfspBizException("9999","贷方账户不能为空: [inAccNo]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getInAccType())){
                throw new IfspBizException("9999","贷方账户类型不能为空: [inAccType]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getTxnDesc())){
                throw new IfspBizException("9999","交易描述不能为空: [txnDesc]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getTransAmt())){
                throw new IfspBizException("9999","支付金额不能为空: [transAmt]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getTxnCcyType())){
                throw new IfspBizException("9999","交易币种不能为空: [txnCcyType]");
            }
            /*************** 记账表额外需要字段 ************/
            if(IfspDataVerifyUtil.isBlank(next.getSeq())){
                throw new IfspBizException("9999","划账序号不能为空: [seq]");
            }
            if(IfspDataVerifyUtil.isBlank(next.getPagySysNo())){
                throw new IfspBizException("9999","通道系统编号不能为空: [pagySysNo]");
            }
            /*******************唯一索引不能为空*********************/
            if(IfspDataVerifyUtil.isBlank(next.getUniqueSsn())){
                throw new IfspBizException("9999","记账唯一标志不能为空: [uniqueSsn]");
            }


        }
    }
}
