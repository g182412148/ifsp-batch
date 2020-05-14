package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class CoreBillInfo extends CommonDTO {

    public CoreBillInfo() {
    }

    public CoreBillInfo(String txnSsn, String recoState) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
    }

    public CoreBillInfo(String txnSsn,Date recoDate, String recoState) {
        this.txnSsn = txnSsn;
        this.recoDate = recoDate;
        this.recoState = recoState;
    }
    public CoreBillInfo(String txnSsn,Date recoDate,Date updTm, String recoState) {
        this.txnSsn = txnSsn;
        this.recoDate = recoDate;
        this.updTm = updTm;
        this.recoState = recoState;
    }
    public CoreBillInfo(String txnSsn,Date recoDate,Date updTm, String recoState, String dubiousFlag) {
        this.txnSsn = txnSsn;
        this.recoDate = recoDate;
        this.updTm = updTm;
        this.recoState = recoState;
        this.dubiousFlag = dubiousFlag;
    }

    private String txnSsn;

    private Date recoDate;

    private String txnState;

    private String recoState;

    private String channelNo;

    private String channelDate;

    private String channelSeq;

    private String txnDate;

    private String tellerSeq;

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

    private String chkRst;

    private Date updTm;
    //对账日期前一天日期，不入库
    private Date dubiousDate;

    private String dubiousFlag;

    public String getDubiousFlag() {
        return dubiousFlag;
    }

    public void setDubiousFlag(String dubiousFlag) {
        this.dubiousFlag = dubiousFlag;
    }

    public String getTxnSsn() {
        return txnSsn;
    }

    public void setTxnSsn(String txnSsn) {
        this.txnSsn = txnSsn == null ? null : txnSsn.trim();
    }

    public Date getRecoDate() {
        return recoDate;
    }

    public void setRecoDate(Date recoDate) {
        this.recoDate = recoDate;
    }

    public String getTxnState() {
        return txnState;
    }

    public void setTxnState(String txnState) {
        this.txnState = txnState == null ? null : txnState.trim();
    }

    public String getRecoState() {
        return recoState;
    }

    public void setRecoState(String recoState) {
        this.recoState = recoState == null ? null : recoState.trim();
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

    public String getChannelSeq() {
        return channelSeq;
    }

    public void setChannelSeq(String channelSeq) {
        this.channelSeq = channelSeq == null ? null : channelSeq.trim();
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

    public String getChkRst() {
        return chkRst;
    }

    public void setChkRst(String chkRst) {
        this.chkRst = chkRst == null ? null : chkRst.trim();
    }

    public Date getUpdTm() {
        return updTm;
    }

    public void setUpdTm(Date updTm) {
        this.updTm = updTm;
    }

    public Date getDubiousDate() {
        return dubiousDate;
    }

    public void setDubiousDate(Date dubiousDate) {
        this.dubiousDate = dubiousDate;
    }
}