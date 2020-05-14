package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class WxBillResult extends CommonDTO {
    private String txnSsnLocal;

    private String txnSsnOuter;

    private String isDubiousLocal;

    private String isDubiousOuter;

    private String dubisLocalOriginal;

    private String dubisOuterOriginal;

    private String compareResult;

    public String getTxnSsnLocal() {
        return txnSsnLocal;
    }

    public void setTxnSsnLocal(String txnSsnLocal) {
        this.txnSsnLocal = txnSsnLocal == null ? null : txnSsnLocal.trim();
    }

    public String getTxnSsnOuter() {
        return txnSsnOuter;
    }

    public void setTxnSsnOuter(String txnSsnOuter) {
        this.txnSsnOuter = txnSsnOuter == null ? null : txnSsnOuter.trim();
    }

    public String getIsDubiousLocal() {
        return isDubiousLocal;
    }

    public void setIsDubiousLocal(String isDubiousLocal) {
        this.isDubiousLocal = isDubiousLocal == null ? null : isDubiousLocal.trim();
    }

    public String getIsDubiousOuter() {
        return isDubiousOuter;
    }

    public void setIsDubiousOuter(String isDubiousOuter) {
        this.isDubiousOuter = isDubiousOuter == null ? null : isDubiousOuter.trim();
    }

    public String getDubisLocalOriginal() {
        return dubisLocalOriginal;
    }

    public void setDubisLocalOriginal(String dubisLocalOriginal) {
        this.dubisLocalOriginal = dubisLocalOriginal == null ? null : dubisLocalOriginal.trim();
    }

    public String getDubisOuterOriginal() {
        return dubisOuterOriginal;
    }

    public void setDubisOuterOriginal(String dubisOuterOriginal) {
        this.dubisOuterOriginal = dubisOuterOriginal == null ? null : dubisOuterOriginal.trim();
    }

    public String getCompareResult() {
        return compareResult;
    }

    public void setCompareResult(String compareResult) {
        this.compareResult = compareResult == null ? null : compareResult.trim();
    }
}