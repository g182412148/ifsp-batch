package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;

public class MchtSettlRateCfg {
    private String rowId;

    private String mchtId;

    private String prodId;

    private String accChnlNo;

    private String acctType;

    private String txnType;

    private String payChnlNo;

    private String rateCalType;

    private BigDecimal rateCalParam;

    private BigDecimal maxParam;

    private BigDecimal minParam;

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getAccChnlNo() {
        return accChnlNo;
    }

    public void setAccChnlNo(String accChnlNo) {
        this.accChnlNo = accChnlNo;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getPayChnlNo() {
        return payChnlNo;
    }

    public void setPayChnlNo(String payChnlNo) {
        this.payChnlNo = payChnlNo;
    }

    public String getRateCalType() {
        return rateCalType;
    }

    public void setRateCalType(String rateCalType) {
        this.rateCalType = rateCalType;
    }

    public BigDecimal getRateCalParam() {
        return rateCalParam;
    }

    public void setRateCalParam(BigDecimal rateCalParam) {
        this.rateCalParam = rateCalParam;
    }

    public BigDecimal getMaxParam() {
        return maxParam;
    }

    public void setMaxParam(BigDecimal maxParam) {
        this.maxParam = maxParam;
    }

    public BigDecimal getMinParam() {
        return minParam;
    }

    public void setMinParam(BigDecimal minParam) {
        this.minParam = minParam;
    }
}