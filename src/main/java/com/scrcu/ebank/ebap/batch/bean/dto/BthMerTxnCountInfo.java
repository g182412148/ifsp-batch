package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: ljy
 * @create: 2018-09-06 16:13
 */
@Data
public class BthMerTxnCountInfo {

    /**
     * 商户号
     */
    private String chlMerId;

    /**
     * 支付渠道
     */
    private String fundChannel;

    /**
     * 交易笔数
     */
    private int txnCount;

    /**
     * 交易金额
     */
    private String txnAmt;


}
