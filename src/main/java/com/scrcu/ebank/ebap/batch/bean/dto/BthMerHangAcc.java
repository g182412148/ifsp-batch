package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class BthMerHangAcc extends CommonDTO {
    private String pagyPayTxnSsn;

    private Date pagyPayTxnTm;

    private String pagyTxnSsn;

    private Date pagyTxnTm;

    private String origPagyTxnSsn;

    private Date origPagyTxnTm;

    private String pagySysNo;

    private String pagySysSoaNo;

    private String pagySysSoaVersion;

    private String pagyNo;

    private String pagyMchtNo;

    private String tpamTxnSsn;

    private String tpamTxnTm;

    private String tpamOrigTxnSsn;

    private String tpamTxnTypeNo;

    private Long tpamTxnAmt;

    private Long tpamTxnFeeAmt;

    private Long tpamSetAmt;

    private String pagyProdId;

    private String pagyProdTxnId;

    private String txnReqSsn;

    private Date txnReqTm;

    private String origTxnReqSsn;

    private Date origTxnReqTm;

    private String txnChlAction;

    private String txnChlNo;

    private String txnChlMchtNo;

    private String cardType;

    private String acctType;

    private String acctSubType;

    private String acctNo;

    private Long txnAmt;

    private BigDecimal feeRate;

    private Long txnFeeAmt;

    private Long txnSetAmt;

    private String orderSsn;

    private String hangSt;

    private Date crtTm;

    private Date lstUpdTm;

    private String reserved1;

    private String reserved2;

    private String settleDateIn;

    private String settleDateOut;

    public String getPagyPayTxnSsn() {
        return pagyPayTxnSsn;
    }

    public void setPagyPayTxnSsn(String pagyPayTxnSsn) {
        this.pagyPayTxnSsn = pagyPayTxnSsn == null ? null : pagyPayTxnSsn.trim();
    }

    public Date getPagyPayTxnTm() {
        return pagyPayTxnTm;
    }

    public void setPagyPayTxnTm(Date pagyPayTxnTm) {
        this.pagyPayTxnTm = pagyPayTxnTm;
    }

    public String getPagyTxnSsn() {
        return pagyTxnSsn;
    }

    public void setPagyTxnSsn(String pagyTxnSsn) {
        this.pagyTxnSsn = pagyTxnSsn == null ? null : pagyTxnSsn.trim();
    }

    public Date getPagyTxnTm() {
        return pagyTxnTm;
    }

    public void setPagyTxnTm(Date pagyTxnTm) {
        this.pagyTxnTm = pagyTxnTm;
    }

    public String getOrigPagyTxnSsn() {
        return origPagyTxnSsn;
    }

    public void setOrigPagyTxnSsn(String origPagyTxnSsn) {
        this.origPagyTxnSsn = origPagyTxnSsn == null ? null : origPagyTxnSsn.trim();
    }

    public Date getOrigPagyTxnTm() {
        return origPagyTxnTm;
    }

    public void setOrigPagyTxnTm(Date origPagyTxnTm) {
        this.origPagyTxnTm = origPagyTxnTm;
    }

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public String getPagySysSoaNo() {
        return pagySysSoaNo;
    }

    public void setPagySysSoaNo(String pagySysSoaNo) {
        this.pagySysSoaNo = pagySysSoaNo == null ? null : pagySysSoaNo.trim();
    }

    public String getPagySysSoaVersion() {
        return pagySysSoaVersion;
    }

    public void setPagySysSoaVersion(String pagySysSoaVersion) {
        this.pagySysSoaVersion = pagySysSoaVersion == null ? null : pagySysSoaVersion.trim();
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getPagyMchtNo() {
        return pagyMchtNo;
    }

    public void setPagyMchtNo(String pagyMchtNo) {
        this.pagyMchtNo = pagyMchtNo == null ? null : pagyMchtNo.trim();
    }

    public String getTpamTxnSsn() {
        return tpamTxnSsn;
    }

    public void setTpamTxnSsn(String tpamTxnSsn) {
        this.tpamTxnSsn = tpamTxnSsn == null ? null : tpamTxnSsn.trim();
    }

    public String getTpamTxnTm() {
        return tpamTxnTm;
    }

    public void setTpamTxnTm(String tpamTxnTm) {
        this.tpamTxnTm = tpamTxnTm == null ? null : tpamTxnTm.trim();
    }

    public String getTpamOrigTxnSsn() {
        return tpamOrigTxnSsn;
    }

    public void setTpamOrigTxnSsn(String tpamOrigTxnSsn) {
        this.tpamOrigTxnSsn = tpamOrigTxnSsn == null ? null : tpamOrigTxnSsn.trim();
    }

    public String getTpamTxnTypeNo() {
        return tpamTxnTypeNo;
    }

    public void setTpamTxnTypeNo(String tpamTxnTypeNo) {
        this.tpamTxnTypeNo = tpamTxnTypeNo == null ? null : tpamTxnTypeNo.trim();
    }

    public Long getTpamTxnAmt() {
        return tpamTxnAmt;
    }

    public void setTpamTxnAmt(Long tpamTxnAmt) {
        this.tpamTxnAmt = tpamTxnAmt;
    }

    public Long getTpamTxnFeeAmt() {
        return tpamTxnFeeAmt;
    }

    public void setTpamTxnFeeAmt(Long tpamTxnFeeAmt) {
        this.tpamTxnFeeAmt = tpamTxnFeeAmt;
    }

    public Long getTpamSetAmt() {
        return tpamSetAmt;
    }

    public void setTpamSetAmt(Long tpamSetAmt) {
        this.tpamSetAmt = tpamSetAmt;
    }

    public String getPagyProdId() {
        return pagyProdId;
    }

    public void setPagyProdId(String pagyProdId) {
        this.pagyProdId = pagyProdId == null ? null : pagyProdId.trim();
    }

    public String getPagyProdTxnId() {
        return pagyProdTxnId;
    }

    public void setPagyProdTxnId(String pagyProdTxnId) {
        this.pagyProdTxnId = pagyProdTxnId == null ? null : pagyProdTxnId.trim();
    }

    public String getTxnReqSsn() {
        return txnReqSsn;
    }

    public void setTxnReqSsn(String txnReqSsn) {
        this.txnReqSsn = txnReqSsn == null ? null : txnReqSsn.trim();
    }

    public Date getTxnReqTm() {
        return txnReqTm;
    }

    public void setTxnReqTm(Date txnReqTm) {
        this.txnReqTm = txnReqTm;
    }

    public String getOrigTxnReqSsn() {
        return origTxnReqSsn;
    }

    public void setOrigTxnReqSsn(String origTxnReqSsn) {
        this.origTxnReqSsn = origTxnReqSsn == null ? null : origTxnReqSsn.trim();
    }

    public Date getOrigTxnReqTm() {
        return origTxnReqTm;
    }

    public void setOrigTxnReqTm(Date origTxnReqTm) {
        this.origTxnReqTm = origTxnReqTm;
    }

    public String getTxnChlAction() {
        return txnChlAction;
    }

    public void setTxnChlAction(String txnChlAction) {
        this.txnChlAction = txnChlAction == null ? null : txnChlAction.trim();
    }

    public String getTxnChlNo() {
        return txnChlNo;
    }

    public void setTxnChlNo(String txnChlNo) {
        this.txnChlNo = txnChlNo == null ? null : txnChlNo.trim();
    }

    public String getTxnChlMchtNo() {
        return txnChlMchtNo;
    }

    public void setTxnChlMchtNo(String txnChlMchtNo) {
        this.txnChlMchtNo = txnChlMchtNo == null ? null : txnChlMchtNo.trim();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType == null ? null : acctType.trim();
    }

    public String getAcctSubType() {
        return acctSubType;
    }

    public void setAcctSubType(String acctSubType) {
        this.acctSubType = acctSubType == null ? null : acctSubType.trim();
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo == null ? null : acctNo.trim();
    }

    public Long getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(Long txnAmt) {
        this.txnAmt = txnAmt;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public Long getTxnFeeAmt() {
        return txnFeeAmt;
    }

    public void setTxnFeeAmt(Long txnFeeAmt) {
        this.txnFeeAmt = txnFeeAmt;
    }

    public Long getTxnSetAmt() {
        return txnSetAmt;
    }

    public void setTxnSetAmt(Long txnSetAmt) {
        this.txnSetAmt = txnSetAmt;
    }

    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn == null ? null : orderSsn.trim();
    }

    public String getHangSt() {
        return hangSt;
    }

    public void setHangSt(String hangSt) {
        this.hangSt = hangSt == null ? null : hangSt.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Date getLstUpdTm() {
        return lstUpdTm;
    }

    public void setLstUpdTm(Date lstUpdTm) {
        this.lstUpdTm = lstUpdTm;
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

    public String getSettleDateIn() {
        return settleDateIn;
    }

    public void setSettleDateIn(String settleDateIn) {
        this.settleDateIn = settleDateIn == null ? null : settleDateIn.trim();
    }

    public String getSettleDateOut() {
        return settleDateOut;
    }

    public void setSettleDateOut(String settleDateOut) {
        this.settleDateOut = settleDateOut == null ? null : settleDateOut.trim();
    }
}