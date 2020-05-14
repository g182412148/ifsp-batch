package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: ljy
 * @create: 2018-08-25 13:23
 */
@Data
public class BthMerInAccMchts {

    /**
     * 商户号
     */
    private String merId;
    /**
     * 清算金额
     */
    private BigDecimal setlAmt;
    /**
     * 交易笔数
     */
    private int txnCount;
    /**
     * 交易金额
     */
    private BigDecimal txnAmt;
    /**
     * 商户营销金额
     */
    private BigDecimal mchtCouponAmt;
    /**
     * 银行出资营销金额
     */
    private BigDecimal bankCouponAmt;
    /**
     * 退款金额
     */
    private BigDecimal returnAmt;
    /**
     * 手续费
     */
    private BigDecimal setlFeeAmt;
    /**
     * 入账状态
     */
    private String inAcctStat;

    /**
     * 向上级返佣金额
     */
    private BigDecimal upCommissionAmt;

    /**
     * 收到下级返佣金额
     */
    private BigDecimal recvCommissionAmt;
}
