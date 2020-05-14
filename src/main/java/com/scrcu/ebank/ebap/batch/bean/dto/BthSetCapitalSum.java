package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

import java.math.BigDecimal;

public class BthSetCapitalSum extends CommonDTO {
    private String merId;

    private String batchNo;

    private String outAccoutOrg;

    private String outAccountNo;

    private String inAccoutOrg;

    private String inAccountNo;

    private String entryType;

    private BigDecimal tranAmount;

    private String merName;

    private String subMerId;

    private String subMerName;

    private String inAccountName;

    private String outAccountName;

    private String accountType;

    private String parternCode;

    public String getParternCode() {
        return parternCode;
    }

    public void setParternCode(String parternCode) {
        this.parternCode = parternCode;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId == null ? null : merId.trim();
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo == null ? null : batchNo.trim();
    }

    public String getOutAccoutOrg() {
        return outAccoutOrg;
    }

    public void setOutAccoutOrg(String outAccoutOrg) {
        this.outAccoutOrg = outAccoutOrg == null ? null : outAccoutOrg.trim();
    }

    public String getOutAccountNo() {
        return outAccountNo;
    }

    public void setOutAccountNo(String outAccountNo) {
        this.outAccountNo = outAccountNo == null ? null : outAccountNo.trim();
    }

    public String getInAccoutOrg() {
        return inAccoutOrg;
    }

    public void setInAccoutOrg(String inAccoutOrg) {
        this.inAccoutOrg = inAccoutOrg == null ? null : inAccoutOrg.trim();
    }

    public String getInAccountNo() {
        return inAccountNo;
    }

    public void setInAccountNo(String inAccountNo) {
        this.inAccountNo = inAccountNo == null ? null : inAccountNo.trim();
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType == null ? null : entryType.trim();
    }

    public BigDecimal getTranAmount() {
        return tranAmount;
    }

    public void setTranAmount(BigDecimal tranAmount) {
        this.tranAmount = tranAmount;
    }

    public String getMerName() {
        return merName;
    }

    public void setMerName(String merName) {
        this.merName = merName == null ? null : merName.trim();
    }

    public String getSubMerId() {
        return subMerId;
    }

    public void setSubMerId(String subMerId) {
        this.subMerId = subMerId == null ? null : subMerId.trim();
    }

    public String getSubMerName() {
        return subMerName;
    }

    public void setSubMerName(String subMerName) {
        this.subMerName = subMerName == null ? null : subMerName.trim();
    }

    public String getInAccountName() {
        return inAccountName;
    }

    public void setInAccountName(String inAccountName) {
        this.inAccountName = inAccountName == null ? null : inAccountName.trim();
    }

    public String getOutAccountName() {
        return outAccountName;
    }

    public void setOutAccountName(String outAccountName) {
        this.outAccountName = outAccountName == null ? null : outAccountName.trim();
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}