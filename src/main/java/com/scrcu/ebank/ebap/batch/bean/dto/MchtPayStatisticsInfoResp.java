package com.scrcu.ebank.ebap.batch.bean.dto;


public class MchtPayStatisticsInfoResp {

    private String chlMchtNo;

    private String transactionCount;

    private String amtCount;

    private String startTime;

    private String endTime;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
