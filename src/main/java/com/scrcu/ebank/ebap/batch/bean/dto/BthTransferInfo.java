package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;

public class BthTransferInfo {
    private String id;

    private String accountTime;

    private String merId;
    
    private String merName;

    private String settleType;

    private String fileName;

    private String inAccountNo;

    private String inAccountName;

    private String inAccoutOrg;

    private BigDecimal accountAmount;

    private BigDecimal acutualAmount;

    private BigDecimal remainAmonut;

    private String outAccountNo;

    private String outAccountName;

    private String outAccoutOrg;

    private String transCur;

    private String handleFlag;

    private Short handleCount;

    private String handDate;

    private String dealStatus;

    private String dealSuccessTime;

    private String dealResultRemark;

    private String createDate;

    private String updateDate;

    private String reserved1;

    private String reserved2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(String accountTime) {
        this.accountTime = accountTime;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }
    
    public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getInAccountNo() {
        return inAccountNo;
    }

    public void setInAccountNo(String inAccountNo) {
        this.inAccountNo = inAccountNo;
    }

    public String getInAccountName() {
        return inAccountName;
    }

    public void setInAccountName(String inAccountName) {
        this.inAccountName = inAccountName;
    }

    public String getInAccoutOrg() {
        return inAccoutOrg;
    }

    public void setInAccoutOrg(String inAccoutOrg) {
        this.inAccoutOrg = inAccoutOrg;
    }

    public BigDecimal getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(BigDecimal accountAmount) {
        this.accountAmount = accountAmount;
    }

    public BigDecimal getAcutualAmount() {
        return acutualAmount;
    }

    public void setAcutualAmount(BigDecimal acutualAmount) {
        this.acutualAmount = acutualAmount;
    }

    public BigDecimal getRemainAmonut() {
        return remainAmonut;
    }

    public void setRemainAmonut(BigDecimal remainAmonut) {
        this.remainAmonut = remainAmonut;
    }

    public String getOutAccountNo() {
        return outAccountNo;
    }

    public void setOutAccountNo(String outAccountNo) {
        this.outAccountNo = outAccountNo;
    }

    public String getOutAccountName() {
        return outAccountName;
    }

    public void setOutAccountName(String outAccountName) {
        this.outAccountName = outAccountName;
    }

    public String getOutAccoutOrg() {
        return outAccoutOrg;
    }

    public void setOutAccoutOrg(String outAccoutOrg) {
        this.outAccoutOrg = outAccoutOrg;
    }

    public String getTransCur() {
        return transCur;
    }

    public void setTransCur(String transCur) {
        this.transCur = transCur;
    }

    public String getHandleFlag() {
        return handleFlag;
    }

    public void setHandleFlag(String handleFlag) {
        this.handleFlag = handleFlag;
    }

    public Short getHandleCount() {
        return handleCount;
    }

    public void setHandleCount(Short handleCount) {
        this.handleCount = handleCount;
    }

    public String getHandDate() {
        return handDate;
    }

    public void setHandDate(String handDate) {
        this.handDate = handDate;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getDealSuccessTime() {
        return dealSuccessTime;
    }

    public void setDealSuccessTime(String dealSuccessTime) {
        this.dealSuccessTime = dealSuccessTime;
    }

    public String getDealResultRemark() {
        return dealResultRemark;
    }

    public void setDealResultRemark(String dealResultRemark) {
        this.dealResultRemark = dealResultRemark;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }
}