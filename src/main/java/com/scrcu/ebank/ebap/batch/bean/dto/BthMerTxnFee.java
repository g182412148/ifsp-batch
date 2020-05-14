package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

import java.math.BigDecimal;

public class BthMerTxnFee extends CommonDTO {
    private String chlMerId;

    private String batchNo;

    private BigDecimal txnAmt;

    private BigDecimal feeAmt;

    private BigDecimal txnCount;

    public String getChlMerId() {
        return chlMerId;
    }

    public void setChlMerId(String chlMerId) {
        this.chlMerId = chlMerId == null ? null : chlMerId.trim();
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo == null ? null : batchNo.trim();
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public BigDecimal getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(BigDecimal txnCount) {
        this.txnCount = txnCount;
    }
}