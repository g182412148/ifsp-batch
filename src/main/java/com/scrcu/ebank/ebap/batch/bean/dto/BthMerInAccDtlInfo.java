package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ljy
 */
@Data
public class BthMerInAccDtlInfo {
    /**
     * 通道系统编号
     */
    private String pagySysNo;
    /**
     * 交易类型
     */
    private String txnType;
    /**
     * 交易时间
     */
    private String orderTm;
    /**
     * 入账金额
     */
    private BigDecimal setlAmt;
    /**
     * 订单金额
     */
    private BigDecimal txnAmt;
    /**
     * 优惠金额银行
     */
    private BigDecimal bankCouponAmt;
    /**
     * 补贴金额商户
     */
    private BigDecimal mchtCouponAmt;
    /**
     * 商户手续费
     */
    private BigDecimal setlFeeAmt;
    /**
     * 渠道流水号
     */
    private String txnSeqId;
    /**
     * 状态
     */
    private String inAcctStat;
    /**
     * 原因
     */
    private String statMark;
}
