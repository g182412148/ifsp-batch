package com.scrcu.ebank.ebap.batch.bean.dto;

public class BthMerInAccDtlKey {
    private String stlmDate;

    private String inAcctDate;

    private String chlMerId;

    private String txnSeqId;

    public String getStlmDate() {
        return stlmDate;
    }

    public void setStlmDate(String stlmDate) {
        this.stlmDate = stlmDate;
    }

    public String getInAcctDate() {
        return inAcctDate;
    }

    public void setInAcctDate(String inAcctDate) {
        this.inAcctDate = inAcctDate;
    }

    public String getChlMerId() {
        return chlMerId;
    }

    public void setChlMerId(String chlMerId) {
        this.chlMerId = chlMerId;
    }

    public String getTxnSeqId() {
        return txnSeqId;
    }

    public void setTxnSeqId(String txnSeqId) {
        this.txnSeqId = txnSeqId;
    }

    @Override
    public String toString() {
        return "BthMerInAccDtlKey{" +
                "stlmDate='" + stlmDate + '\'' +
                ", inAcctDate='" + inAcctDate + '\'' +
                ", chlMerId='" + chlMerId + '\'' +
                ", txnSeqId='" + txnSeqId + '\'' +
                '}';
    }
}