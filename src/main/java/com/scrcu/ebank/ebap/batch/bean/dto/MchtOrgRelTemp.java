package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class MchtOrgRelTemp extends CommonDTO {
    private String mchtNo;

    private String orgId;

    private String orgType;

    private String orgName;

    public String getMchtNo() {
        return mchtNo;
    }

    public void setMchtNo(String mchtNo) {
        this.mchtNo = mchtNo == null ? null : mchtNo.trim();
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType == null ? null : orgType.trim();
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }
}