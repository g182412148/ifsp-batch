package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class KeepAcctInfo extends CommonDTO {
    private String orderSsn;

    private String subOrderSsn;

    private String orderSeq;

    private String keepType;

    private String complexId;

    private String mchtId;

    private Date orderTm;

    private String txnTypeNo;

    private String syncFlag;

    private String debtAcctNo;

    private String debtAcctName;

    private String debtAcctType;

    private String debtAcctTypeName;

    private String credAcctNo;

    private String credAcctName;

    private String credAcctTypeName;

    private String credAcctType;

    private String respCode;

    private String respMsg;

    private String txnAmt;

    private String txnDesc;

    private String txnState;

    private String txnCcy;

    private Date crtTm;

    private Date upTm;

    private String pagyNo;

    private String misc1;

    private String misc2;

    private String misc3;

    private String uniqueSsn;

    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn == null ? null : orderSsn.trim();
    }

    public String getSubOrderSsn() {
        return subOrderSsn;
    }

    public void setSubOrderSsn(String subOrderSsn) {
        this.subOrderSsn = subOrderSsn == null ? null : subOrderSsn.trim();
    }

    public String getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq == null ? null : orderSeq.trim();
    }

    public String getKeepType() {
        return keepType;
    }

    public void setKeepType(String keepType) {
        this.keepType = keepType == null ? null : keepType.trim();
    }

    public String getComplexId() {
        return complexId;
    }

    public void setComplexId(String complexId) {
        this.complexId = complexId == null ? null : complexId.trim();
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId == null ? null : mchtId.trim();
    }

    public Date getOrderTm() {
        return orderTm;
    }

    public void setOrderTm(Date orderTm) {
        this.orderTm = orderTm;
    }

    public String getTxnTypeNo() {
        return txnTypeNo;
    }

    public void setTxnTypeNo(String txnTypeNo) {
        this.txnTypeNo = txnTypeNo == null ? null : txnTypeNo.trim();
    }

    public String getSyncFlag() {
        return syncFlag;
    }

    public void setSyncFlag(String syncFlag) {
        this.syncFlag = syncFlag == null ? null : syncFlag.trim();
    }

    public String getDebtAcctNo() {
        return debtAcctNo;
    }

    public void setDebtAcctNo(String debtAcctNo) {
        this.debtAcctNo = debtAcctNo == null ? null : debtAcctNo.trim();
    }

    public String getDebtAcctName() {
        return debtAcctName;
    }

    public void setDebtAcctName(String debtAcctName) {
        this.debtAcctName = debtAcctName == null ? null : debtAcctName.trim();
    }

    public String getDebtAcctType() {
        return debtAcctType;
    }

    public void setDebtAcctType(String debtAcctType) {
        this.debtAcctType = debtAcctType == null ? null : debtAcctType.trim();
    }

    public String getDebtAcctTypeName() {
        return debtAcctTypeName;
    }

    public void setDebtAcctTypeName(String debtAcctTypeName) {
        this.debtAcctTypeName = debtAcctTypeName == null ? null : debtAcctTypeName.trim();
    }

    public String getCredAcctNo() {
        return credAcctNo;
    }

    public void setCredAcctNo(String credAcctNo) {
        this.credAcctNo = credAcctNo == null ? null : credAcctNo.trim();
    }

    public String getCredAcctName() {
        return credAcctName;
    }

    public void setCredAcctName(String credAcctName) {
        this.credAcctName = credAcctName == null ? null : credAcctName.trim();
    }

    public String getCredAcctTypeName() {
        return credAcctTypeName;
    }

    public void setCredAcctTypeName(String credAcctTypeName) {
        this.credAcctTypeName = credAcctTypeName == null ? null : credAcctTypeName.trim();
    }

    public String getCredAcctType() {
        return credAcctType;
    }

    public void setCredAcctType(String credAcctType) {
        this.credAcctType = credAcctType == null ? null : credAcctType.trim();
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode == null ? null : respCode.trim();
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg == null ? null : respMsg.trim();
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt == null ? null : txnAmt.trim();
    }

    public String getTxnDesc() {
        return txnDesc;
    }

    public void setTxnDesc(String txnDesc) {
        this.txnDesc = txnDesc == null ? null : txnDesc.trim();
    }

    public String getTxnState() {
        return txnState;
    }

    public void setTxnState(String txnState) {
        this.txnState = txnState == null ? null : txnState.trim();
    }

    public String getTxnCcy() {
        return txnCcy;
    }

    public void setTxnCcy(String txnCcy) {
        this.txnCcy = txnCcy == null ? null : txnCcy.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Date getUpTm() {
        return upTm;
    }

    public void setUpTm(Date upTm) {
        this.upTm = upTm;
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getMisc1() {
        return misc1;
    }

    public void setMisc1(String misc1) {
        this.misc1 = misc1 == null ? null : misc1.trim();
    }

    public String getMisc2() {
        return misc2;
    }

    public void setMisc2(String misc2) {
        this.misc2 = misc2 == null ? null : misc2.trim();
    }

    public String getMisc3() {
        return misc3;
    }

    public void setMisc3(String misc3) {
        this.misc3 = misc3 == null ? null : misc3.trim();
    }

    public String getUniqueSsn() {
        return uniqueSsn;
    }

    public void setUniqueSsn(String uniqueSsn) {
        this.uniqueSsn = uniqueSsn == null ? null : uniqueSsn.trim();
    }
}