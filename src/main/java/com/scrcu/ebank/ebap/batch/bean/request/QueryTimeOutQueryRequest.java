package com.scrcu.ebank.ebap.batch.bean.request;

public class QueryTimeOutQueryRequest extends BatchRequest {

    private String txnStartTime;//订单查询开始时间

    private String txnEndTime;//订单查询结束时间

    public String getTxnStartTime() {
        return txnStartTime;
    }

    public void setTxnStartTime(String txnStartTime) {
        this.txnStartTime = txnStartTime;
    }

    public String getTxnEndTime() {
        return txnEndTime;
    }

    public void setTxnEndTime(String txnEndTime) {
        this.txnEndTime = txnEndTime;
    }
}
