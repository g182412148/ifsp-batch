package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class BthEnterAccountResult extends CommonDTO {
    private String id;

    private String tranOrg;

    private String feeCatalog;

    private String borrowFlag;

    private String outAccoutOrg;

    private String outAccountNo;

    private String lendFlag;

    private String inAccoutOrg;

    private String inAccountNo;

    private String transAmount;

    private String transCur;

    private String billFlag;

    private String summaryCode;

    private String summary;

    private String reserved;

    private String dealResultCode;

    private String dealResultRemark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getTranOrg() {
        return tranOrg;
    }

    public void setTranOrg(String tranOrg) {
        this.tranOrg = tranOrg == null ? null : tranOrg.trim();
    }

    public String getFeeCatalog() {
        return feeCatalog;
    }

    public void setFeeCatalog(String feeCatalog) {
        this.feeCatalog = feeCatalog == null ? null : feeCatalog.trim();
    }

    public String getBorrowFlag() {
        return borrowFlag;
    }

    public void setBorrowFlag(String borrowFlag) {
        this.borrowFlag = borrowFlag == null ? null : borrowFlag.trim();
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

    public String getLendFlag() {
        return lendFlag;
    }

    public void setLendFlag(String lendFlag) {
        this.lendFlag = lendFlag == null ? null : lendFlag.trim();
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

    public String getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(String transAmount) {
        this.transAmount = transAmount == null ? null : transAmount.trim();
    }

    public String getTransCur() {
        return transCur;
    }

    public void setTransCur(String transCur) {
        this.transCur = transCur == null ? null : transCur.trim();
    }

    public String getBillFlag() {
        return billFlag;
    }

    public void setBillFlag(String billFlag) {
        this.billFlag = billFlag == null ? null : billFlag.trim();
    }

    public String getSummaryCode() {
        return summaryCode;
    }

    public void setSummaryCode(String summaryCode) {
        this.summaryCode = summaryCode == null ? null : summaryCode.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved == null ? null : reserved.trim();
    }

    public String getDealResultCode() {
        return dealResultCode;
    }

    public void setDealResultCode(String dealResultCode) {
        this.dealResultCode = dealResultCode == null ? null : dealResultCode.trim();
    }

    public String getDealResultRemark() {
        return dealResultRemark;
    }

    public void setDealResultRemark(String dealResultRemark) {
        this.dealResultRemark = dealResultRemark == null ? null : dealResultRemark.trim();
    }
}