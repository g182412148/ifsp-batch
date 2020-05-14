package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class AreaInfo extends CommonDTO {
    private String areaCode;

    private String areaName;

    private String parAreaCode;

    private String areaType;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode == null ? null : areaCode.trim();
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName == null ? null : areaName.trim();
    }

    public String getParAreaCode() {
        return parAreaCode;
    }

    public void setParAreaCode(String parAreaCode) {
        this.parAreaCode = parAreaCode == null ? null : parAreaCode.trim();
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType == null ? null : areaType.trim();
    }
}