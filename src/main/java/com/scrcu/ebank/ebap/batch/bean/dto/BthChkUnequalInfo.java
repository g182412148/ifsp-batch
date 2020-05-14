package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class BthChkUnequalInfo extends CommonDTO {
    private String pagyPayTxnSsn;

    private Date pagyPayTxnTm;

    private String pagySysNo;

    private Long txnAmt;

    private String txnType;

    private String txnStat;

    private String tpamTxnSsn;

    private String tpamTxnTm;

    private Long tpamTxnAmt;

    private String tpamTxnType;

    private String tpamTxnStat;

    private String orderSsn;

    private String procSt;

    private String procDesc;

    private Date chkUeqDate;

    private Date crtTm;

    private Date lstUpdTm;

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

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public Long getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(Long txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType == null ? null : txnType.trim();
    }

    public String getTxnStat() {
        return txnStat;
    }

    public void setTxnStat(String txnStat) {
        this.txnStat = txnStat == null ? null : txnStat.trim();
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

    public Long getTpamTxnAmt() {
        return tpamTxnAmt;
    }

    public void setTpamTxnAmt(Long tpamTxnAmt) {
        this.tpamTxnAmt = tpamTxnAmt;
    }

    public String getTpamTxnType() {
        return tpamTxnType;
    }

    public void setTpamTxnType(String tpamTxnType) {
        this.tpamTxnType = tpamTxnType == null ? null : tpamTxnType.trim();
    }

    public String getTpamTxnStat() {
        return tpamTxnStat;
    }

    public void setTpamTxnStat(String tpamTxnStat) {
        this.tpamTxnStat = tpamTxnStat == null ? null : tpamTxnStat.trim();
    }

    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn == null ? null : orderSsn.trim();
    }

    public String getProcSt() {
        return procSt;
    }

    public void setProcSt(String procSt) {
        this.procSt = procSt == null ? null : procSt.trim();
    }

    public String getProcDesc() {
        return procDesc;
    }

    public void setProcDesc(String procDesc) {
        this.procDesc = procDesc == null ? null : procDesc.trim();
    }

    public Date getChkUeqDate() {
        return chkUeqDate;
    }

    public void setChkUeqDate(Date chkUeqDate) {
        this.chkUeqDate = chkUeqDate;
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
}