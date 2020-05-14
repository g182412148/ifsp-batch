package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;

public class DebitTranInfo extends CommonDTO {
    private String channelSeq;

    private String channelNo;

    private String channelDate;

    private String txnDate;

    private String tellerSeq;

    private String txnStatus;

    private String platformDate;

    private String platformSeq;

    private String txnCode;

    private String txnOrg;

    private String txnTeller;

    private String txnCur;

    private String payAccount;

    private String receiveAccount;

    private BigDecimal txnAmount;

    private String reserved1;

    private String pagySysNo;

    private String pagyNo;

    private String chkDataDt;

    private String chkAcctSt;

    private String chkRst;

    private String lstUpdTm;
    
    private String dubiousFlag;// 用于还原可疑数据状态
    
    public String getDubiousFlag() {
		return dubiousFlag;
	}

	public void setDubiousFlag(String dubiousFlag) {
		this.dubiousFlag = dubiousFlag;
	}

	public String getChannelSeq() {
        return channelSeq;
    }

    public void setChannelSeq(String channelSeq) {
        this.channelSeq = channelSeq == null ? null : channelSeq.trim();
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo == null ? null : channelNo.trim();
    }

    public String getChannelDate() {
        return channelDate;
    }

    public void setChannelDate(String channelDate) {
        this.channelDate = channelDate == null ? null : channelDate.trim();
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate == null ? null : txnDate.trim();
    }

    public String getTellerSeq() {
        return tellerSeq;
    }

    public void setTellerSeq(String tellerSeq) {
        this.tellerSeq = tellerSeq == null ? null : tellerSeq.trim();
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus == null ? null : txnStatus.trim();
    }

    public String getPlatformDate() {
        return platformDate;
    }

    public void setPlatformDate(String platformDate) {
        this.platformDate = platformDate == null ? null : platformDate.trim();
    }

    public String getPlatformSeq() {
        return platformSeq;
    }

    public void setPlatformSeq(String platformSeq) {
        this.platformSeq = platformSeq == null ? null : platformSeq.trim();
    }

    public String getTxnCode() {
        return txnCode;
    }

    public void setTxnCode(String txnCode) {
        this.txnCode = txnCode == null ? null : txnCode.trim();
    }

    public String getTxnOrg() {
        return txnOrg;
    }

    public void setTxnOrg(String txnOrg) {
        this.txnOrg = txnOrg == null ? null : txnOrg.trim();
    }

    public String getTxnTeller() {
        return txnTeller;
    }

    public void setTxnTeller(String txnTeller) {
        this.txnTeller = txnTeller == null ? null : txnTeller.trim();
    }

    public String getTxnCur() {
        return txnCur;
    }

    public void setTxnCur(String txnCur) {
        this.txnCur = txnCur == null ? null : txnCur.trim();
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount == null ? null : payAccount.trim();
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount == null ? null : receiveAccount.trim();
    }

    public BigDecimal getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(BigDecimal txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1 == null ? null : reserved1.trim();
    }

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getChkDataDt() {
        return chkDataDt;
    }

    public void setChkDataDt(String chkDataDt) {
        this.chkDataDt = chkDataDt == null ? null : chkDataDt.trim();
    }

    public String getChkAcctSt() {
        return chkAcctSt;
    }

    public void setChkAcctSt(String chkAcctSt) {
        this.chkAcctSt = chkAcctSt == null ? null : chkAcctSt.trim();
    }

    public String getChkRst() {
        return chkRst;
    }

    public void setChkRst(String chkRst) {
        this.chkRst = chkRst == null ? null : chkRst.trim();
    }

    public String getLstUpdTm() {
        return lstUpdTm;
    }

    public void setLstUpdTm(String lstUpdTm) {
        this.lstUpdTm = lstUpdTm == null ? null : lstUpdTm.trim();
    }
}