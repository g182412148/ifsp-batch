package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

import java.util.Date;

public class ErrMerInAcc extends CommonDTO {
    private String dateStlm;

    private String txnSsn;

    private String chlMerId;

    private String chlMerName;

    private String chlSubMerId;

    private String chlSubMerName;

    private int txnCount;

    private String txnAmt;

    private String feeAmt;

    private String outAcctNo;

    private String outAcctName;

    private String inAcctName;

    private String inAcctNo;

    private String inAcctAmt;

    private String inAcctTime;

    private String inAcctStat;

    private String statMark;

    private String misc1;

    private String misc2;

    private String vchrno;

    private String inAcctSsn;

    private String brno;

    private String t0StlmAmt;

    private String workDate;

    private String agentSerialNo;

    private String bankDate;

    private String bankId;

    private String agFee;

    private String handleMark;

    private String handleState;

    private String inAcctType;

    private String inAcctNoOrg;

    private String outAcctNoOrg;

    private String borrowFlag;

    private String lendFlag;

    private String batchNo;

    private String tranOrg;

    private String feeCatalog;

    private String summaryCode;

    private String summary;

    private String transCur;

    private String billFlag;

    private String dealResultCode;

    private String dealResultRemark;

    private String coreRespCode;

    private String coreRespMsg;

    private String antiFraudData;

    private String antiFraudRespSsn;

    private String antiFraudFinalDecision;

    private String antiFraudFinalDealType;

    private String antiFraudFinalControl;

    private String antiFraudResData;

    private Date updateTime;

    private Date antiFraudSendTime;

    private String entryType;

    private String otherSetlFee;

    private String parternCode;


    public String getDateStlm() {
        return dateStlm;
    }

    public void setDateStlm(String dateStlm) {
        this.dateStlm = dateStlm == null ? null : dateStlm.trim();
    }

    public String getTxnSsn() {
        return txnSsn;
    }

    public void setTxnSsn(String txnSsn) {
        this.txnSsn = txnSsn == null ? null : txnSsn.trim();
    }

    public String getChlMerId() {
        return chlMerId;
    }

    public void setChlMerId(String chlMerId) {
        this.chlMerId = chlMerId == null ? null : chlMerId.trim();
    }

    public String getChlMerName() {
        return chlMerName;
    }

    public void setChlMerName(String chlMerName) {
        this.chlMerName = chlMerName == null ? null : chlMerName.trim();
    }

    public String getChlSubMerId() {
        return chlSubMerId;
    }

    public void setChlSubMerId(String chlSubMerId) {
        this.chlSubMerId = chlSubMerId == null ? null : chlSubMerId.trim();
    }

    public String getChlSubMerName() {
        return chlSubMerName;
    }

    public void setChlSubMerName(String chlSubMerName) {
        this.chlSubMerName = chlSubMerName == null ? null : chlSubMerName.trim();
    }

    public int getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(int txnCount) {
        this.txnCount = txnCount;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt == null ? null : txnAmt.trim();
    }

    public String getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(String feeAmt) {
        this.feeAmt = feeAmt == null ? null : feeAmt.trim();
    }

    public String getOutAcctNo() {
        return outAcctNo;
    }

    public void setOutAcctNo(String outAcctNo) {
        this.outAcctNo = outAcctNo == null ? null : outAcctNo.trim();
    }

    public String getOutAcctName() {
        return outAcctName;
    }

    public void setOutAcctName(String outAcctName) {
        this.outAcctName = outAcctName == null ? null : outAcctName.trim();
    }

    public String getInAcctName() {
        return inAcctName;
    }

    public void setInAcctName(String inAcctName) {
        this.inAcctName = inAcctName == null ? null : inAcctName.trim();
    }

    public String getInAcctNo() {
        return inAcctNo;
    }

    public void setInAcctNo(String inAcctNo) {
        this.inAcctNo = inAcctNo == null ? null : inAcctNo.trim();
    }

    public String getInAcctAmt() {
        return inAcctAmt;
    }

    public void setInAcctAmt(String inAcctAmt) {
        this.inAcctAmt = inAcctAmt == null ? null : inAcctAmt.trim();
    }

    public String getInAcctTime() {
        return inAcctTime;
    }

    public void setInAcctTime(String inAcctTime) {
        this.inAcctTime = inAcctTime == null ? null : inAcctTime.trim();
    }

    public String getInAcctStat() {
        return inAcctStat;
    }

    public void setInAcctStat(String inAcctStat) {
        this.inAcctStat = inAcctStat == null ? null : inAcctStat.trim();
    }

    public String getStatMark() {
        return statMark;
    }

    public void setStatMark(String statMark) {
        this.statMark = statMark == null ? null : statMark.trim();
    }

    public String getMisc1() {
        return misc1;
    }

    public void setMisc1(String misc1) {
        this.misc1 = misc1 == null ? null : misc1.trim();
    }

    public String getMisc2() {
        return misc2;
    }

    public void setMisc2(String misc2) {
        this.misc2 = misc2 == null ? null : misc2.trim();
    }

    public String getVchrno() {
        return vchrno;
    }

    public void setVchrno(String vchrno) {
        this.vchrno = vchrno == null ? null : vchrno.trim();
    }

    public String getInAcctSsn() {
        return inAcctSsn;
    }

    public void setInAcctSsn(String inAcctSsn) {
        this.inAcctSsn = inAcctSsn == null ? null : inAcctSsn.trim();
    }

    public String getBrno() {
        return brno;
    }

    public void setBrno(String brno) {
        this.brno = brno == null ? null : brno.trim();
    }

    public String getT0StlmAmt() {
        return t0StlmAmt;
    }

    public void setT0StlmAmt(String t0StlmAmt) {
        this.t0StlmAmt = t0StlmAmt == null ? null : t0StlmAmt.trim();
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate == null ? null : workDate.trim();
    }

    public String getAgentSerialNo() {
        return agentSerialNo;
    }

    public void setAgentSerialNo(String agentSerialNo) {
        this.agentSerialNo = agentSerialNo == null ? null : agentSerialNo.trim();
    }

    public String getBankDate() {
        return bankDate;
    }

    public void setBankDate(String bankDate) {
        this.bankDate = bankDate == null ? null : bankDate.trim();
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId == null ? null : bankId.trim();
    }

    public String getAgFee() {
        return agFee;
    }

    public void setAgFee(String agFee) {
        this.agFee = agFee == null ? null : agFee.trim();
    }

    public String getHandleMark() {
        return handleMark;
    }

    public void setHandleMark(String handleMark) {
        this.handleMark = handleMark == null ? null : handleMark.trim();
    }

    public String getHandleState() {
        return handleState;
    }

    public void setHandleState(String handleState) {
        this.handleState = handleState == null ? null : handleState.trim();
    }

    public String getInAcctType() {
        return inAcctType;
    }

    public void setInAcctType(String inAcctType) {
        this.inAcctType = inAcctType == null ? null : inAcctType.trim();
    }

    public String getInAcctNoOrg() {
        return inAcctNoOrg;
    }

    public void setInAcctNoOrg(String inAcctNoOrg) {
        this.inAcctNoOrg = inAcctNoOrg == null ? null : inAcctNoOrg.trim();
    }

    public String getOutAcctNoOrg() {
        return outAcctNoOrg;
    }

    public void setOutAcctNoOrg(String outAcctNoOrg) {
        this.outAcctNoOrg = outAcctNoOrg == null ? null : outAcctNoOrg.trim();
    }

    public String getBorrowFlag() {
        return borrowFlag;
    }

    public void setBorrowFlag(String borrowFlag) {
        this.borrowFlag = borrowFlag == null ? null : borrowFlag.trim();
    }

    public String getLendFlag() {
        return lendFlag;
    }

    public void setLendFlag(String lendFlag) {
        this.lendFlag = lendFlag == null ? null : lendFlag.trim();
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo == null ? null : batchNo.trim();
    }

    public String getTranOrg() {
        return tranOrg;
    }

    public void setTranOrg(String tranOrg) {
        this.tranOrg = tranOrg;
    }

    public String getFeeCatalog() {
        return feeCatalog;
    }

    public void setFeeCatalog(String feeCatalog) {
        this.feeCatalog = feeCatalog;
    }

    public String getSummaryCode() {
        return summaryCode;
    }

    public void setSummaryCode(String summaryCode) {
        this.summaryCode = summaryCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTransCur() {
        return transCur;
    }

    public void setTransCur(String transCur) {
        this.transCur = transCur;
    }

    public String getBillFlag() {
        return billFlag;
    }

    public void setBillFlag(String billFlag) {
        this.billFlag = billFlag;
    }

    public String getDealResultCode() {
        return dealResultCode;
    }

    public void setDealResultCode(String dealResultCode) {
        this.dealResultCode = dealResultCode;
    }

    public String getDealResultRemark() {
        return dealResultRemark;
    }

    public void setDealResultRemark(String dealResultRemark) {
        this.dealResultRemark = dealResultRemark;
    }

    public String getCoreRespCode() {
        return coreRespCode;
    }

    public void setCoreRespCode(String coreRespCode) {
        this.coreRespCode = coreRespCode == null ? null : coreRespCode.trim();
    }

    public String getCoreRespMsg() {
        return coreRespMsg;
    }

    public void setCoreRespMsg(String coreRespMsg) {
        this.coreRespMsg = coreRespMsg == null ? null : coreRespMsg.trim();
    }

    public String getAntiFraudData() {
        return antiFraudData;
    }

    public void setAntiFraudData(String antiFraudData) {
        this.antiFraudData = antiFraudData == null ? null : antiFraudData.trim();
    }

    public String getAntiFraudRespSsn() {
        return antiFraudRespSsn;
    }

    public void setAntiFraudRespSsn(String antiFraudRespSsn) {
        this.antiFraudRespSsn = antiFraudRespSsn == null ? null : antiFraudRespSsn.trim();
    }

    public String getAntiFraudFinalDecision() {
        return antiFraudFinalDecision;
    }

    public void setAntiFraudFinalDecision(String antiFraudFinalDecision) {
        this.antiFraudFinalDecision = antiFraudFinalDecision == null ? null : antiFraudFinalDecision.trim();
    }

    public String getAntiFraudFinalDealType() {
        return antiFraudFinalDealType;
    }

    public void setAntiFraudFinalDealType(String antiFraudFinalDealType) {
        this.antiFraudFinalDealType = antiFraudFinalDealType == null ? null : antiFraudFinalDealType.trim();
    }

    public String getAntiFraudFinalControl() {
        return antiFraudFinalControl;
    }

    public void setAntiFraudFinalControl(String antiFraudFinalControl) {
        this.antiFraudFinalControl = antiFraudFinalControl == null ? null : antiFraudFinalControl.trim();
    }

    public String getAntiFraudResData() {
        return antiFraudResData;
    }

    public void setAntiFraudResData(String antiFraudResData) {
        this.antiFraudResData = antiFraudResData == null ? null : antiFraudResData.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getAntiFraudSendTime() {
        return antiFraudSendTime;
    }

    public void setAntiFraudSendTime(Date antiFraudSendTime) {
        this.antiFraudSendTime = antiFraudSendTime;
    }

    public String getEntryType()
    {
        return entryType;
    }

    public void setEntryType(String entryType)
    {
        this.entryType = entryType;
    }

    public String getOtherSetlFee() {
        return otherSetlFee;
    }

    public void setOtherSetlFee(String otherSetlFee) {
        this.otherSetlFee = otherSetlFee;
    }

    public String getParternCode() {
        return parternCode;
    }

    public void setParternCode(String parternCode) {
        this.parternCode = parternCode;
    }
}