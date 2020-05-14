package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: ljy
 * @create: 2018-08-25 13:34
 */
@Data
public class BthMerInAccMchtsDtl {
    /**
     * 交易时间
     */
    private String orderTm;
    /**
     * 交易单号
     */
    private String orderSsn;
    /**
     * 交易类型
     */
    private String txnType;
    /**
     * 清算金额
     */
    private BigDecimal setlAmt;
    /**
     * 交易金额
     */
    private BigDecimal txnAmt;
    /**
     * 营销金额(商户)
     */
    private BigDecimal mchtCouponAmt;
    /**
     * 营销金额(银行)
     */
    private BigDecimal bankCouponAmt;
    /**
     * 退货金额
     */
    private BigDecimal returnAmt;
    /**
     * 手续费
     */
    private BigDecimal setlFeeAmt;

    /**
     * 原因
     */
    private String statMark;

    /**
     * 状态
     */
    private String inAcctStat;


}
