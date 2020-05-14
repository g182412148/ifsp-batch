package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class PmpAuditStepInfo extends CommonDTO {
    private String seqId;

    private String auditId;

    private Short stepNo;

    private String auditState;

    private String roleId;

    private String auditOprNo;

    private String auditDatetIme;

    private String auditView;

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId == null ? null : seqId.trim();
    }

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId == null ? null : auditId.trim();
    }

    public Short getStepNo() {
        return stepNo;
    }

    public void setStepNo(Short stepNo) {
        this.stepNo = stepNo;
    }

    public String getAuditState() {
        return auditState;
    }

    public void setAuditState(String auditState) {
        this.auditState = auditState == null ? null : auditState.trim();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getAuditOprNo() {
        return auditOprNo;
    }

    public void setAuditOprNo(String auditOprNo) {
        this.auditOprNo = auditOprNo == null ? null : auditOprNo.trim();
    }

    public String getAuditDatetIme() {
        return auditDatetIme;
    }

    public void setAuditDatetIme(String auditDatetIme) {
        this.auditDatetIme = auditDatetIme == null ? null : auditDatetIme.trim();
    }

    public String getAuditView() {
        return auditView;
    }

    public void setAuditView(String auditView) {
        this.auditView = auditView == null ? null : auditView.trim();
    }
}