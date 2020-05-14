package com.scrcu.ebank.ebap.batch.bean.dto;

public class MchtOrgRel extends MchtOrgRelKey {
    private String orgName;

    private String orgType;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }
}