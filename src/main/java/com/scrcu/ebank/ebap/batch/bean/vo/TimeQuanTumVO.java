package com.scrcu.ebank.ebap.batch.bean.vo;

import java.math.BigDecimal;

public class TimeQuanTumVO {
    private String startTime;

    private String endTime;

    private String chlMchtNo;

    private BigDecimal amtCount;

    private Integer transactionCount;

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

    public String getChlMchtNo() {
        return chlMchtNo;
    }

    public void setChlMchtNo(String chlMchtNo) {
        this.chlMchtNo = chlMchtNo;
    }

    public BigDecimal getAmtCount() {
        return amtCount;
    }

    public void setAmtCount(BigDecimal amtCount) {
        this.amtCount = amtCount;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
}
