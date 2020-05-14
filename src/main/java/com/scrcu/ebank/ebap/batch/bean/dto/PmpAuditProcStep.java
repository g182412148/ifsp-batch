package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class PmpAuditProcStep extends CommonDTO {
    private String id;

    private String auditProcId;

    private Short stepNo;

    private String stepName;

    private String stepDesc;

    private String auditRoleId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAuditProcId() {
        return auditProcId;
    }

    public void setAuditProcId(String auditProcId) {
        this.auditProcId = auditProcId == null ? null : auditProcId.trim();
    }

    public Short getStepNo() {
        return stepNo;
    }

    public void setStepNo(Short stepNo) {
        this.stepNo = stepNo;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName == null ? null : stepName.trim();
    }

    public String getStepDesc() {
        return stepDesc;
    }

    public void setStepDesc(String stepDesc) {
        this.stepDesc = stepDesc == null ? null : stepDesc.trim();
    }

    public String getAuditRoleId() {
        return auditRoleId;
    }

    public void setAuditRoleId(String auditRoleId) {
        this.auditRoleId = auditRoleId == null ? null : auditRoleId.trim();
    }
}