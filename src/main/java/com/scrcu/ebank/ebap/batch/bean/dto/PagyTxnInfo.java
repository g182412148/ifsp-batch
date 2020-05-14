package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class PagyTxnInfo extends CommonDTO {
    private String pagyTxnSsn;

    private Date pagyTxnTm;

    private String origPagyTxnSsn;

    private Date origPagyTxnTm;

    private String version;

    private String encoding;

    private String signMethodType;

    private String serType;

    private String prodId;

    private String txnType;

    private String txnActionFlag;

    private String txnAsynFlag;

    private String chlNo;

    private String chlMchtNo;

    private String chlAcsType;

    private String txnReqSsn;

    private Date txnReqTm;

    private String origTxnReqSsn;

    private Date origTxnReqTm;

    private String acctType;

    private String acctSubType;

    private String cardType;

    private String acctNo;

    private String acctNoEn;

    private String acctCustomerInfo;

    private String acctTokenBindId;

    private String acctInNo;

    private String acctInCustomerInfo;

    private String acctInTokenBindId;

    private String txnCustmerIp;

    private String txnProductInfo;

    private String txnDesc;

    private Long txnAmt;

    private Long txnFeeAmt;

    private Long txnRfdAmtSum;

    private Long txnRfdCountSum;

    private String txnCcyType;

    private String frontEndUrl;

    private String backEndUrl;

    private String txnAttach;

    private String pagySteDate;

    private String pagyTxnState;

    private String pagyRespCode;

    private String pagyRespMsg;

    private String sysTraceId;

    private String initLocalhostIp;

    private Date pagyCrtTm;

    private String updLocalhostIp;

    private Date pagyUpTm;

    public String getPagyTxnSsn() {
        return pagyTxnSsn;
    }

    public void setPagyTxnSsn(String pagyTxnSsn) {
        this.pagyTxnSsn = pagyTxnSsn == null ? null : pagyTxnSsn.trim();
    }

    public Date getPagyTxnTm() {
        return pagyTxnTm;
    }

    public void setPagyTxnTm(Date pagyTxnTm) {
        this.pagyTxnTm = pagyTxnTm;
    }

    public String getOrigPagyTxnSsn() {
        return origPagyTxnSsn;
    }

    public void setOrigPagyTxnSsn(String origPagyTxnSsn) {
        this.origPagyTxnSsn = origPagyTxnSsn == null ? null : origPagyTxnSsn.trim();
    }

    public Date getOrigPagyTxnTm() {
        return origPagyTxnTm;
    }

    public void setOrigPagyTxnTm(Date origPagyTxnTm) {
        this.origPagyTxnTm = origPagyTxnTm;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding == null ? null : encoding.trim();
    }

    public String getSignMethodType() {
        return signMethodType;
    }

    public void setSignMethodType(String signMethodType) {
        this.signMethodType = signMethodType == null ? null : signMethodType.trim();
    }

    public String getSerType() {
        return serType;
    }

    public void setSerType(String serType) {
        this.serType = serType == null ? null : serType.trim();
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId == null ? null : prodId.trim();
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType == null ? null : txnType.trim();
    }

    public String getTxnActionFlag() {
        return txnActionFlag;
    }

    public void setTxnActionFlag(String txnActionFlag) {
        this.txnActionFlag = txnActionFlag == null ? null : txnActionFlag.trim();
    }

    public String getTxnAsynFlag() {
        return txnAsynFlag;
    }

    public void setTxnAsynFlag(String txnAsynFlag) {
        this.txnAsynFlag = txnAsynFlag == null ? null : txnAsynFlag.trim();
    }

    public String getChlNo() {
        return chlNo;
    }

    public void setChlNo(String chlNo) {
        this.chlNo = chlNo == null ? null : chlNo.trim();
    }

    public String getChlMchtNo() {
        return chlMchtNo;
    }

    public void setChlMchtNo(String chlMchtNo) {
        this.chlMchtNo = chlMchtNo == null ? null : chlMchtNo.trim();
    }

    public String getChlAcsType() {
        return chlAcsType;
    }

    public void setChlAcsType(String chlAcsType) {
        this.chlAcsType = chlAcsType == null ? null : chlAcsType.trim();
    }

    public String getTxnReqSsn() {
        return txnReqSsn;
    }

    public void setTxnReqSsn(String txnReqSsn) {
        this.txnReqSsn = txnReqSsn == null ? null : txnReqSsn.trim();
    }

    public Date getTxnReqTm() {
        return txnReqTm;
    }

    public void setTxnReqTm(Date txnReqTm) {
        this.txnReqTm = txnReqTm;
    }

    public String getOrigTxnReqSsn() {
        return origTxnReqSsn;
    }

    public void setOrigTxnReqSsn(String origTxnReqSsn) {
        this.origTxnReqSsn = origTxnReqSsn == null ? null : origTxnReqSsn.trim();
    }

    public Date getOrigTxnReqTm() {
        return origTxnReqTm;
    }

    public void setOrigTxnReqTm(Date origTxnReqTm) {
        this.origTxnReqTm = origTxnReqTm;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType == null ? null : acctType.trim();
    }

    public String getAcctSubType() {
        return acctSubType;
    }

    public void setAcctSubType(String acctSubType) {
        this.acctSubType = acctSubType == null ? null : acctSubType.trim();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo == null ? null : acctNo.trim();
    }

    public String getAcctNoEn() {
        return acctNoEn;
    }

    public void setAcctNoEn(String acctNoEn) {
        this.acctNoEn = acctNoEn == null ? null : acctNoEn.trim();
    }

    public String getAcctCustomerInfo() {
        return acctCustomerInfo;
    }

    public void setAcctCustomerInfo(String acctCustomerInfo) {
        this.acctCustomerInfo = acctCustomerInfo == null ? null : acctCustomerInfo.trim();
    }

    public String getAcctTokenBindId() {
        return acctTokenBindId;
    }

    public void setAcctTokenBindId(String acctTokenBindId) {
        this.acctTokenBindId = acctTokenBindId == null ? null : acctTokenBindId.trim();
    }

    public String getAcctInNo() {
        return acctInNo;
    }

    public void setAcctInNo(String acctInNo) {
        this.acctInNo = acctInNo == null ? null : acctInNo.trim();
    }

    public String getAcctInCustomerInfo() {
        return acctInCustomerInfo;
    }

    public void setAcctInCustomerInfo(String acctInCustomerInfo) {
        this.acctInCustomerInfo = acctInCustomerInfo == null ? null : acctInCustomerInfo.trim();
    }

    public String getAcctInTokenBindId() {
        return acctInTokenBindId;
    }

    public void setAcctInTokenBindId(String acctInTokenBindId) {
        this.acctInTokenBindId = acctInTokenBindId == null ? null : acctInTokenBindId.trim();
    }

    public String getTxnCustmerIp() {
        return txnCustmerIp;
    }

    public void setTxnCustmerIp(String txnCustmerIp) {
        this.txnCustmerIp = txnCustmerIp == null ? null : txnCustmerIp.trim();
    }

    public String getTxnProductInfo() {
        return txnProductInfo;
    }

    public void setTxnProductInfo(String txnProductInfo) {
        this.txnProductInfo = txnProductInfo == null ? null : txnProductInfo.trim();
    }

    public String getTxnDesc() {
        return txnDesc;
    }

    public void setTxnDesc(String txnDesc) {
        this.txnDesc = txnDesc == null ? null : txnDesc.trim();
    }

    public Long getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(Long txnAmt) {
        this.txnAmt = txnAmt;
    }

    public Long getTxnFeeAmt() {
        return txnFeeAmt;
    }

    public void setTxnFeeAmt(Long txnFeeAmt) {
        this.txnFeeAmt = txnFeeAmt;
    }

    public Long getTxnRfdAmtSum() {
        return txnRfdAmtSum;
    }

    public void setTxnRfdAmtSum(Long txnRfdAmtSum) {
        this.txnRfdAmtSum = txnRfdAmtSum;
    }

    public Long getTxnRfdCountSum() {
        return txnRfdCountSum;
    }

    public void setTxnRfdCountSum(Long txnRfdCountSum) {
        this.txnRfdCountSum = txnRfdCountSum;
    }

    public String getTxnCcyType() {
        return txnCcyType;
    }

    public void setTxnCcyType(String txnCcyType) {
        this.txnCcyType = txnCcyType == null ? null : txnCcyType.trim();
    }

    public String getFrontEndUrl() {
        return frontEndUrl;
    }

    public void setFrontEndUrl(String frontEndUrl) {
        this.frontEndUrl = frontEndUrl == null ? null : frontEndUrl.trim();
    }

    public String getBackEndUrl() {
        return backEndUrl;
    }

    public void setBackEndUrl(String backEndUrl) {
        this.backEndUrl = backEndUrl == null ? null : backEndUrl.trim();
    }

    public String getTxnAttach() {
        return txnAttach;
    }

    public void setTxnAttach(String txnAttach) {
        this.txnAttach = txnAttach == null ? null : txnAttach.trim();
    }

    public String getPagySteDate() {
        return pagySteDate;
    }

    public void setPagySteDate(String pagySteDate) {
        this.pagySteDate = pagySteDate == null ? null : pagySteDate.trim();
    }

    public String getPagyTxnState() {
        return pagyTxnState;
    }

    public void setPagyTxnState(String pagyTxnState) {
        this.pagyTxnState = pagyTxnState == null ? null : pagyTxnState.trim();
    }

    public String getPagyRespCode() {
        return pagyRespCode;
    }

    public void setPagyRespCode(String pagyRespCode) {
        this.pagyRespCode = pagyRespCode == null ? null : pagyRespCode.trim();
    }

    public String getPagyRespMsg() {
        return pagyRespMsg;
    }

    public void setPagyRespMsg(String pagyRespMsg) {
        this.pagyRespMsg = pagyRespMsg == null ? null : pagyRespMsg.trim();
    }

    public String getSysTraceId() {
        return sysTraceId;
    }

    public void setSysTraceId(String sysTraceId) {
        this.sysTraceId = sysTraceId == null ? null : sysTraceId.trim();
    }

    public String getInitLocalhostIp() {
        return initLocalhostIp;
    }

    public void setInitLocalhostIp(String initLocalhostIp) {
        this.initLocalhostIp = initLocalhostIp == null ? null : initLocalhostIp.trim();
    }

    public Date getPagyCrtTm() {
        return pagyCrtTm;
    }

    public void setPagyCrtTm(Date pagyCrtTm) {
        this.pagyCrtTm = pagyCrtTm;
    }

    public String getUpdLocalhostIp() {
        return updLocalhostIp;
    }

    public void setUpdLocalhostIp(String updLocalhostIp) {
        this.updLocalhostIp = updLocalhostIp == null ? null : updLocalhostIp.trim();
    }

    public Date getPagyUpTm() {
        return pagyUpTm;
    }

    public void setPagyUpTm(Date pagyUpTm) {
        this.pagyUpTm = pagyUpTm;
    }
}