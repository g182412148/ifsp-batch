package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class KeepRecoInfo extends CommonDTO {

    public KeepRecoInfo() {
    }

    public KeepRecoInfo(String txnSsn, String recoState) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
    }

    public KeepRecoInfo(String txnSsn,Date recoDate, String recoState) {
        this.txnSsn = txnSsn;
        this.recoDate = recoDate;
        this.recoState = recoState;
    }
    public KeepRecoInfo(String txnSsn,Date recoDate,Date updTm, String recoState) {
        this.txnSsn = txnSsn;
        this.recoDate = recoDate;
        this.updTm = updTm;
        this.recoState = recoState;
    }

    public KeepRecoInfo(String txnSsn,Date recoDate,Date updTm, String recoState, String dubiousFlag) {
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

    private String orderSsn;

    private String orderTm;

    private String subOrderSsn;

    private String keepAccTime;

    private String state;

    private String keepAccType;

    private Long transAmt;

    private String chkRst;

    private Date updTm;

    private String reserved1;

    private String reserved2;

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

    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn == null ? null : orderSsn.trim();
    }

    public String getOrderTm() {
        return orderTm;
    }

    public void setOrderTm(String orderTm) {
        this.orderTm = orderTm == null ? null : orderTm.trim();
    }

    public String getSubOrderSsn() {
        return subOrderSsn;
    }

    public void setSubOrderSsn(String subOrderSsn) {
        this.subOrderSsn = subOrderSsn == null ? null : subOrderSsn.trim();
    }

    public String getKeepAccTime() {
        return keepAccTime;
    }

    public void setKeepAccTime(String keepAccTime) {
        this.keepAccTime = keepAccTime == null ? null : keepAccTime.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getKeepAccType() {
        return keepAccType;
    }

    public void setKeepAccType(String keepAccType) {
        this.keepAccType = keepAccType == null ? null : keepAccType.trim();
    }

    public Long getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(Long transAmt) {
        this.transAmt = transAmt;
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
}