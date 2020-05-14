package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class BthScheduleCluster extends CommonDTO {
	
    private String taskId;

    private String taskName;

    private String taskDesc;

    private String taskClass;

    private String serverIp;

    private String executeStat;

    private Date updateTm;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId == null ? null : taskId.trim();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc == null ? null : taskDesc.trim();
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass == null ? null : taskClass.trim();
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp == null ? null : serverIp.trim();
    }

    public String getExecuteStat() {
        return executeStat;
    }

    public void setExecuteStat(String executeStat) {
        this.executeStat = executeStat == null ? null : executeStat.trim();
    }

    public Date getUpdateTm() {
        return updateTm;
    }

    public void setUpdateTm(Date updateTm) {
        this.updateTm = updateTm;
    }
}