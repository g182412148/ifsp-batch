package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class BackupTableConfig extends CommonDTO {
    private String tableName;

    private String origTableOwner;

    private String destTable;

    private String destTableOwner;

    private String archCon;

    private Short pageSize;

    private String backupType;

    private String cleanFlag;

    private String partType;

    private String parts;

    private String autoClearFlag;

    public BackupTableConfig() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public String getOrigTableOwner() {
        return origTableOwner;
    }

    public void setOrigTableOwner(String origTableOwner) {
        this.origTableOwner = origTableOwner == null ? null : origTableOwner.trim();
    }

    public String getDestTable() {
        return destTable;
    }

    public void setDestTable(String destTable) {
        this.destTable = destTable == null ? null : destTable.trim();
    }

    public String getDestTableOwner() {
        return destTableOwner;
    }

    public void setDestTableOwner(String destTableOwner) {
        this.destTableOwner = destTableOwner == null ? null : destTableOwner.trim();
    }

    public String getArchCon() {
        return archCon;
    }

    public void setArchCon(String archCon) {
        this.archCon = archCon == null ? null : archCon.trim();
    }

    public Short getPageSize() {
        return pageSize;
    }

    public void setPageSize(Short pageSize) {
        this.pageSize = pageSize;
    }

    public String getBackupType() {
        return backupType;
    }

    public void setBackupType(String backupType) {
        this.backupType = backupType == null ? null : backupType.trim();
    }

    public String getCleanFlag() {
        return cleanFlag;
    }

    public void setCleanFlag(String cleanFlag) {
        this.cleanFlag = cleanFlag == null ? null : cleanFlag.trim();
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType == null ? null : partType.trim();
    }

    public String getParts() {
        return parts;
    }

    public void setParts(String parts) {
        this.parts = parts == null ? null : parts.trim();
    }

    public String getAutoClearFlag() {
        return autoClearFlag;
    }

    public void setAutoClearFlag(String autoClearFlag) {
        this.autoClearFlag = autoClearFlag;
    }
}