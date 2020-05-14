package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.ExcelData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ljy
 * @date 2018-12-11 17:00
 */
public class InAccMerVo {

    @ExcelData(name = "商户号",cellIndex = 0 )
    private String mchtId;

    @ExcelData(name = "商户名称",cellIndex = 1 )
    private String mchtNm;

    @ExcelData(name = "通道",cellIndex = 2 )
    private String pagyNm;

    @ExcelData(name = "入账失败日期",cellIndex = 3 )
    private String inAccFailDate;

    @ExcelData(name = "支付笔数",cellIndex = 4 )
    private String txnCount;

    @ExcelData(name = "支付金额",cellIndex = 5 )
    private String txnAmt;

    @ExcelData(name = "退款笔数",cellIndex = 6 )
    private String rtnCount;

    @ExcelData(name = "退款金额",cellIndex = 7 )
    private String rtnAmt;

    @ExcelData(name = "支付手续费",cellIndex = 8 )
    private String txnFee;

    @ExcelData(name = "退款返还手续费",cellIndex = 9 )
    private String rtnFee;

    @ExcelData(name = "返佣",cellIndex = 10 )
    private String commisonAmt;

    @ExcelData(name = "应入金额",cellIndex = 11 )
    private String planInAccAmt;

    @ExcelData(name = "实入金额",cellIndex = 12 )
    private String realInAccAmt;

    @ExcelData(name = "入账结果",cellIndex = 13 )
    private String inAccRst;

    @ExcelData(name = "失败原因",cellIndex = 14 )
    private String failReason;

    @ExcelData(name = "备注",cellIndex = 15 )
    private String remark;

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getMchtNm() {
        return mchtNm;
    }

    public void setMchtNm(String mchtNm) {
        this.mchtNm = mchtNm;
    }

    public String getPagyNm() {
        return pagyNm;
    }

    public void setPagyNm(String pagyNm) {
        this.pagyNm = pagyNm;
    }

    public String getInAccFailDate() {
        return inAccFailDate;
    }

    public void setInAccFailDate(String inAccFailDate) {
        this.inAccFailDate = inAccFailDate;
    }

    public String getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(String txnCount) {
        this.txnCount = txnCount;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getRtnCount() {
        return rtnCount;
    }

    public void setRtnCount(String rtnCount) {
        this.rtnCount = rtnCount;
    }

    public String getRtnAmt() {
        return rtnAmt;
    }

    public void setRtnAmt(String rtnAmt) {
        this.rtnAmt = rtnAmt;
    }

    public String getTxnFee() {
        return txnFee;
    }

    public void setTxnFee(String txnFee) {
        this.txnFee = txnFee;
    }

    public String getRtnFee() {
        return rtnFee;
    }

    public void setRtnFee(String rtnFee) {
        this.rtnFee = rtnFee;
    }

    public String getCommisonAmt() {
        return commisonAmt;
    }

    public void setCommisonAmt(String commisonAmt) {
        this.commisonAmt = commisonAmt;
    }

    public String getPlanInAccAmt() {
        return planInAccAmt;
    }

    public void setPlanInAccAmt(String planInAccAmt) {
        this.planInAccAmt = planInAccAmt;
    }

    public String getRealInAccAmt() {
        return realInAccAmt;
    }

    public void setRealInAccAmt(String realInAccAmt) {
        this.realInAccAmt = realInAccAmt;
    }

    public String getInAccRst() {
        return inAccRst;
    }

    public void setInAccRst(String inAccRst) {
        this.inAccRst = inAccRst;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
