package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class BthSetCapitalDetail  extends CommonDTO {
	
	private static final long serialVersionUID = 1L;

	private String id;

    private String cleaTime;

    private String merOrderId;

    private String orderId;

    private String tranTime;

    private String paySeqId;

    private String paySendTime;

    private String accountId;

    private String merId;

    private String merName;

    private String subMerId;

    private String subMerName;

    private String outAccountNo;

    private String outAccountName;

    private String outAccoutOrg;

    private String inAccountNo;

    private String inAccountName;

    private String inAccoutOrg;

    private String transCur;

    private BigDecimal tranAmount;

    private String entryType;

    private String accountType;

    private String fundChannel;

    private String tranType;

    private String dealResult;

    private String accountStauts;

    private String dealRemark;

    private String createDate;

    private String updateDate;
    
    private String batchNo;

	private String reserved1;

    private String reserved2;

    private String reserved3;

    private String parternCode;

    public String getParternCode() {
        return parternCode;
    }

    public void setParternCode(String parternCode) {
        this.parternCode = parternCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCleaTime() {
        return cleaTime;
    }

    public void setCleaTime(String cleaTime) {
        this.cleaTime = cleaTime == null ? null : cleaTime.trim();
    }

    public String getMerOrderId() {
        return merOrderId;
    }

    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId == null ? null : merOrderId.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getTranTime() {
        return tranTime;
    }

    public void setTranTime(String tranTime) {
        this.tranTime = tranTime == null ? null : tranTime.trim();
    }

    public String getPaySeqId() {
        return paySeqId;
    }

    public void setPaySeqId(String paySeqId) {
        this.paySeqId = paySeqId == null ? null : paySeqId.trim();
    }

    public String getPaySendTime() {
        return paySendTime;
    }

    public void setPaySendTime(String paySendTime) {
        this.paySendTime = paySendTime == null ? null : paySendTime.trim();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId == null ? null : accountId.trim();
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId == null ? null : merId.trim();
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

    public String getOutAccountNo() {
        return outAccountNo;
    }

    public void setOutAccountNo(String outAccountNo) {
        this.outAccountNo = outAccountNo == null ? null : outAccountNo.trim();
    }

    public String getOutAccountName() {
        return outAccountName;
    }

    public void setOutAccountName(String outAccountName) {
        this.outAccountName = outAccountName == null ? null : outAccountName.trim();
    }

    public String getOutAccoutOrg() {
        return outAccoutOrg;
    }

    public void setOutAccoutOrg(String outAccoutOrg) {
        this.outAccoutOrg = outAccoutOrg == null ? null : outAccoutOrg.trim();
    }

    public String getInAccountNo() {
        return inAccountNo;
    }

    public void setInAccountNo(String inAccountNo) {
        this.inAccountNo = inAccountNo == null ? null : inAccountNo.trim();
    }

    public String getInAccountName() {
        return inAccountName;
    }

    public void setInAccountName(String inAccountName) {
        this.inAccountName = inAccountName == null ? null : inAccountName.trim();
    }

    public String getInAccoutOrg() {
        return inAccoutOrg;
    }

    public void setInAccoutOrg(String inAccoutOrg) {
        this.inAccoutOrg = inAccoutOrg == null ? null : inAccoutOrg.trim();
    }

    public String getTransCur() {
        return transCur;
    }

    public void setTransCur(String transCur) {
        this.transCur = transCur == null ? null : transCur.trim();
    }

    public BigDecimal getTranAmount() {
        return tranAmount;
    }

    public void setTranAmount(BigDecimal tranAmount) {
        this.tranAmount = tranAmount;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType == null ? null : entryType.trim();
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType == null ? null : accountType.trim();
    }

    public String getFundChannel() {
        return fundChannel;
    }

    public void setFundChannel(String fundChannel) {
        this.fundChannel = fundChannel == null ? null : fundChannel.trim();
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType == null ? null : tranType.trim();
    }

    public String getDealResult() {
        return dealResult;
    }

    public void setDealResult(String dealResult) {
        this.dealResult = dealResult == null ? null : dealResult.trim();
    }

    public String getAccountStauts() {
        return accountStauts;
    }

    public void setAccountStauts(String accountStauts) {
        this.accountStauts = accountStauts == null ? null : accountStauts.trim();
    }

    public String getDealRemark() {
        return dealRemark;
    }

    public void setDealRemark(String dealRemark) {
        this.dealRemark = dealRemark == null ? null : dealRemark.trim();
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate == null ? null : createDate.trim();
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate == null ? null : updateDate.trim();
    }
    
    public String getBatchNo()
	{
		return batchNo;
	}

	public void setBatchNo(String batchNo)
	{
		this.batchNo = batchNo;
	}

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1 == null ? null : reserved1.trim();
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2 == null ? null : reserved2.trim();
    }

    public String getReserved3() {
        return reserved3;
    }

    public void setReserved3(String reserved3) {
        this.reserved3 = reserved3 == null ? null : reserved3.trim();
    }
}