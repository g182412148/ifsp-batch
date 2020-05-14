package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class SynchronizeOrgInfo extends CommonDTO {
    private String id;

    private String brId;

    private String synchrState;

    private Date synchrTime;

    private String bakInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getBrId() {
        return brId;
    }

    public void setBrId(String brId) {
        this.brId = brId == null ? null : brId.trim();
    }

    public String getSynchrState() {
        return synchrState;
    }

    public void setSynchrState(String synchrState) {
        this.synchrState = synchrState == null ? null : synchrState.trim();
    }

    public Date getSynchrTime() {
        return synchrTime;
    }

    public void setSynchrTime(Date synchrTime) {
        this.synchrTime = synchrTime;
    }

    public String getBakInfo() {
        return bakInfo;
    }

    public void setBakInfo(String bakInfo) {
        this.bakInfo = bakInfo == null ? null : bakInfo.trim();
    }
}