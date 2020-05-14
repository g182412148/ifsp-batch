package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class BillRecoErr extends CommonDTO {
    private String txnSsn;

    private Date recoDate;

    private String chnlNo;

    private Date txnTime;

    private String localTxnType;

    private String outerTxnType;

    private String localTxnState;

    private String outerTxnState;

    private BigDecimal localTxnAmt;

    private BigDecimal outerTxnAmt;

    private String recoRest;

    private String orderId;

    private Date orderDate;

    private String procState;

    private String procDesc;

    private Date updDate;

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

    public String getChnlNo() {
        return chnlNo;
    }

    public void setChnlNo(String chnlNo) {
        this.chnlNo = chnlNo == null ? null : chnlNo.trim();
    }

    public Date getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(Date txnTime) {
        this.txnTime = txnTime;
    }

    public String getLocalTxnType() {
        return localTxnType;
    }

    public void setLocalTxnType(String localTxnType) {
        this.localTxnType = localTxnType == null ? null : localTxnType.trim();
    }

    public String getOuterTxnType() {
        return outerTxnType;
    }

    public void setOuterTxnType(String outerTxnType) {
        this.outerTxnType = outerTxnType == null ? null : outerTxnType.trim();
    }

    public String getLocalTxnState() {
        return localTxnState;
    }

    public void setLocalTxnState(String localTxnState) {
        this.localTxnState = localTxnState == null ? null : localTxnState.trim();
    }

    public String getOuterTxnState() {
        return outerTxnState;
    }

    public void setOuterTxnState(String outerTxnState) {
        this.outerTxnState = outerTxnState == null ? null : outerTxnState.trim();
    }

    public BigDecimal getLocalTxnAmt() {
        return localTxnAmt;
    }

    public void setLocalTxnAmt(BigDecimal localTxnAmt) {
        this.localTxnAmt = localTxnAmt;
    }

    public BigDecimal getOuterTxnAmt() {
        return outerTxnAmt;
    }

    public void setOuterTxnAmt(BigDecimal outerTxnAmt) {
        this.outerTxnAmt = outerTxnAmt;
    }

    public String getRecoRest() {
        return recoRest;
    }

    public void setRecoRest(String recoRest) {
        this.recoRest = recoRest == null ? null : recoRest.trim();
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

    public String getProcState() {
        return procState;
    }

    public void setProcState(String procState) {
        this.procState = procState == null ? null : procState.trim();
    }

    public String getProcDesc() {
        return procDesc;
    }

    public void setProcDesc(String procDesc) {
        this.procDesc = procDesc == null ? null : procDesc.trim();
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }
}