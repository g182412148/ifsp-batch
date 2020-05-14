package com.scrcu.ebank.ebap.batch.bean.dto;

import java.util.Date;

public class BthBatchJobExecution {
    private String jobExeId;

    private String jobName;

    private String jobDesc;

    private String batchNo;

    private String ipAddr;

    private Date startTime;

    private Date endTime;

    private String status;

    private String description;

    private byte[] params;

    public String getJobExeId() {
        return jobExeId;
    }

    public void setJobExeId(String jobExeId) {
        this.jobExeId = jobExeId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getParams() {
        return params;
    }

    public void setParams(byte[] params) {
        this.params = params;
    }
}