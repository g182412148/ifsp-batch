package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;

public class BthMerDayAmtSum extends CommonDTO {
    private String id;

    private String mchtId;

    private String txnDate;

    private String fundChannel;

    private String fundChannelNm;

    private Long leftAmt;

    private Long rightAmt;

    private Long txnCount;

    private BigDecimal txnAmt;

    private String reserve1;

    private String reserve2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId == null ? null : mchtId.trim();
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate == null ? null : txnDate.trim();
    }

    public String getFundChannel() {
        return fundChannel;
    }

    public void setFundChannel(String fundChannel) {
        this.fundChannel = fundChannel == null ? null : fundChannel.trim();
    }

    public String getFundChannelNm() {
        return fundChannelNm;
    }

    public void setFundChannelNm(String fundChannelNm) {
        this.fundChannelNm = fundChannelNm == null ? null : fundChannelNm.trim();
    }

    public Long getLeftAmt() {
        return leftAmt;
    }

    public void setLeftAmt(Long leftAmt) {
        this.leftAmt = leftAmt;
    }

    public Long getRightAmt() {
        return rightAmt;
    }

    public void setRightAmt(Long rightAmt) {
        this.rightAmt = rightAmt;
    }

    public Long getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(Long txnCount) {
        this.txnCount = txnCount;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }
}