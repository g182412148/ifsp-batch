package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class OrgRepeMergCfg extends CommonDTO {
    private String tableName;

    private String param;

    private String updType;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param == null ? null : param.trim();
    }

    public String getUpdType() {
        return updType;
    }

    public void setUpdType(String updType) {
        this.updType = updType == null ? null : updType.trim();
    }
}