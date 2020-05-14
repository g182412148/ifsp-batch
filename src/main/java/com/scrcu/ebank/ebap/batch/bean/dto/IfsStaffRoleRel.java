package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class IfsStaffRoleRel extends CommonDTO {
    private String tlrId;

    private String roleId;

    private String brId;

    private String flag;

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId == null ? null : tlrId.trim();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public String getBrId() {
        return brId;
    }

    public void setBrId(String brId) {
        this.brId = brId == null ? null : brId.trim();
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}