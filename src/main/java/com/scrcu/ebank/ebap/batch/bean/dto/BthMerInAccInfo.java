package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 只针对商户入账保留的信息实体
 */
@Data
public class BthMerInAccInfo {
    /**
     * 交易金额(不包含银行出资)
     */
    private BigDecimal txnAmt;
    /**
     * 手续费金额(扣商户手续费)
     */
    private BigDecimal feeAmt;

    /**
     * 交易笔数
     */
    private int txnCount;
}
