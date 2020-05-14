package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.ExcelData;

/**
 * @author ljy
 * @date 2018-12-18 16:18
 */
public class TxnCountVo {
    @ExcelData(name = "支付渠道",cellIndex = 0 )
    private String fundChannel;
    @ExcelData(name = "笔数",cellIndex = 1 )
    private String txnCount;
    @ExcelData(name = "金额",cellIndex = 2 )
    private String amt;


    public TxnCountVo(String fundChannel, String txnCount, String amt) {
        this.fundChannel = fundChannel;
        this.txnCount = txnCount;
        this.amt = amt;
    }

    public TxnCountVo() {
    }

    public String getFundChannel() {
        return fundChannel;
    }

    public void setFundChannel(String fundChannel) {
        this.fundChannel = fundChannel;
    }

    public String getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(String txnCount) {
        this.txnCount = txnCount;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
}

