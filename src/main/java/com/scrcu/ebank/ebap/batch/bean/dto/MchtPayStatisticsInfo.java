package com.scrcu.ebank.ebap.batch.bean.dto;

import java.util.Date;

public class MchtPayStatisticsInfo {
    private String chlMchtNo;

    private String transactionCount;

    private String amtCount;

    private Date startTime;

    private Date endTime;

    public String getChlMchtNo() {
        return chlMchtNo;
    }

    public void setChlMchtNo(String chlMchtNo) {
        this.chlMchtNo = chlMchtNo;
    }

    public String getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(String transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getAmtCount() {
        return amtCount;
    }

    public void setAmtCount(String amtCount) {
        this.amtCount = amtCount;
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
}