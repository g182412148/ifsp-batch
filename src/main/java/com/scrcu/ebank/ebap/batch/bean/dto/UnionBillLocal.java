package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class UnionBillLocal extends CommonDTO {
    private Date recoDate;

    private String txnSsn;

    private String settleKey;

    private Date txnTime;

    private String pagyNo;

    private String txnType;

    private String txnState;

    private BigDecimal txnAmt;

    private String recoState;

    private String orderId;

    private Date orderDate;

    private Date updDate;

    private String dubiousFlag;

    public UnionBillLocal() {
    }

    public UnionBillLocal(String txnSsn, String recoState) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
    }

    public UnionBillLocal(String txnSsn, String recoState, Date recoDate) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
        this.recoDate = recoDate;
    }

    public UnionBillLocal(String txnSsn, String recoState, Date recoDate, Date updDate) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
        this.recoDate = recoDate;
        this.updDate = updDate;
    }

    public UnionBillLocal(String txnSsn, String settleKey,  String recoState, Date recoDate, Date updDate) {
        this.txnSsn = txnSsn;
        this.settleKey = settleKey;
        this.recoState = recoState;
        this.recoDate = recoDate;
        this.updDate = updDate;
    }

    public UnionBillLocal(String txnSsn, String settleKey,  String recoState, Date recoDate, Date updDate, String dubiousFlag) {
        this.txnSsn = txnSsn;
        this.settleKey = settleKey;
        this.recoState = recoState;
        this.recoDate = recoDate;
        this.updDate = updDate;
        this.dubiousFlag = dubiousFlag;
    }

    public String getDubiousFlag() {
        return dubiousFlag;
    }

    public void setDubiousFlag(String dubiousFlag) {
        this.dubiousFlag = dubiousFlag;
    }

    public Date getRecoDate() {
        return recoDate;
    }

    public void setRecoDate(Date recoDate) {
        this.recoDate = recoDate;
    }

    public String getTxnSsn() {
        return txnSsn;
    }

    public void setTxnSsn(String txnSsn) {
        this.txnSsn = txnSsn == null ? null : txnSsn.trim();
    }

    public String getSettleKey() {
        return settleKey;
    }

    public void setSettleKey(String settleKey) {
        this.settleKey = settleKey == null ? null : settleKey.trim();
    }

    public Date getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(Date txnTime) {
        this.txnTime = txnTime;
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType == null ? null : txnType.trim();
    }

    public String getTxnState() {
        return txnState;
    }

    public void setTxnState(String txnState) {
        this.txnState = txnState == null ? null : txnState.trim();
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getRecoState() {
        return recoState;
    }

    public void setRecoState(String recoState) {
        this.recoState = recoState == null ? null : recoState.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate == null ? null : updDate;
    }
}