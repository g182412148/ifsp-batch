package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.batch.common.dict.DubisFlagDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;

import java.math.BigDecimal;
import java.util.Date;

public class AliBillLocal extends CommonDTO {
    private String txnSsn;

    private Date recoDate;

    private Date txnTime;

    private String txnType;

    private String txnState;

    private BigDecimal txnAmt;

    private String recoState;

    private String orderId;

    private Date orderDate;

    private Date updDate;

    private String dubisFlag;

    public AliBillLocal(){

    }

    public AliBillLocal(String txnSsn, RecoStatusDict recoStateDict, Date updDate) {
        this.txnSsn = txnSsn;
        this.recoState = recoStateDict.getCode();
        this.updDate = updDate;
        //对账状态为可疑时, 设置可疑标志为1
        if(RecoStatusDict.DUBIOUS == recoStateDict){
            dubisFlag = DubisFlagDict.TRUE.getCode();
        }
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

    public Date getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(Date txnTime) {
        this.txnTime = txnTime;
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
        this.updDate = updDate;
    }

    public String getDubisFlag() {
        return dubisFlag;
    }

    public void setDubisFlag(String dubisFlag) {
        this.dubisFlag = dubisFlag == null ? null : dubisFlag.trim();
    }
}