package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class IfsStaffOrgRel extends CommonDTO {
    private String tlrId;

    private String brId;

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId == null ? null : tlrId.trim();
    }

    public String getBrId() {
        return brId;
    }

    public void setBrId(String brId) {
        this.brId = brId == null ? null : brId.trim();
    }
}