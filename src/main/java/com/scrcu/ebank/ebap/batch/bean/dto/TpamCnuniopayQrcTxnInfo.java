package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class TpamCnuniopayQrcTxnInfo extends CommonDTO {
    private String pagyPayTxnSsn;

    private Date pagyPayTxnTm;

    private String pagyTxnSsn;

    private Date pagyTxnTm;

    private String origPagyPayTxnSsn;

    private Date origPagyPayTxnTm;

    private String pagySysNo;

    private String pagySysSoaNo;

    private String pagySysSoaVersion;

    private String pagySysSoaActionFlag;

    private String pagyNo;

    private String pagyMchtNo;

    private String tpamPagyNo;

    private String tpamChannelNo;

    private String tpamSetlModel;

    private String tpamPagyMchtNo;

    private String tpamTxnTypeNo;

    private String tpamTxnSsn;

    private String tpamTxnTm;

    private String tpamOrigTxnSsn;

    private String tpamOrigTxnTm;

    private String tpamAcctTypeNo;

    private String tpamAcctSubTypeNo;

    private String tpamCardTypeNo;

    private String tpamAcctNo;

    private String tpamOrderType;

    private String tpamQrValidTm;

    private String tpamAddNcondInfo;

    private String tpamAddnOpUrl;

    private String tpamTokenPayId;

    private String tpamTokenPayInfo;

    private String tpamEmvQrcCode;

    private String tpamTokenPayLimitCount;

    private String tpamRiskInfo;

    private String pagyBackendUrl;

    private String tpamTxnStartTm;

    private String tpamTxnExpireTm;

    private String tpamCouponifo;

    private String tpamBankNo;

    private String tpamBankTxnTm;

    private String tpamComInfo;

    private String tpamSettleKey;

    private String tpamTxnNo;

    private String tpamOrigTxnAmt;

    private Long tpamTxnAmt;

    private String tpamStlmDt;

    private String tpamStlmTm;

    private String tpamStlmAmt;

    private String tpamSettlementFlag;

    private String tpamSettleFee;

    private String tpamStlmCecyCode;

    private String tpamIssueBankId;

    private String tpamIssueCardType;

    private String tpamIsslnsCode;

    private String tpamPayCardType;

    private String tpamRespCode;

    private String tpamRespMsg;

    private String pagyRespCode;

    private String pagyRespMsg;

    private String pagySteDate;

    private String initLocalhostIp;

    private String updLocalhostIp;

    private String sysTraceId;

    private Date pagyCrtTm;

    private Date pagyUpTm;

    public String getPagyPayTxnSsn() {
        return pagyPayTxnSsn;
    }

    public void setPagyPayTxnSsn(String pagyPayTxnSsn) {
        this.pagyPayTxnSsn = pagyPayTxnSsn == null ? null : pagyPayTxnSsn.trim();
    }

    public Date getPagyPayTxnTm() {
        return pagyPayTxnTm;
    }

    public void setPagyPayTxnTm(Date pagyPayTxnTm) {
        this.pagyPayTxnTm = pagyPayTxnTm;
    }

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

    public String getOrigPagyPayTxnSsn() {
        return origPagyPayTxnSsn;
    }

    public void setOrigPagyPayTxnSsn(String origPagyPayTxnSsn) {
        this.origPagyPayTxnSsn = origPagyPayTxnSsn == null ? null : origPagyPayTxnSsn.trim();
    }

    public Date getOrigPagyPayTxnTm() {
        return origPagyPayTxnTm;
    }

    public void setOrigPagyPayTxnTm(Date origPagyPayTxnTm) {
        this.origPagyPayTxnTm = origPagyPayTxnTm;
    }

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public String getPagySysSoaNo() {
        return pagySysSoaNo;
    }

    public void setPagySysSoaNo(String pagySysSoaNo) {
        this.pagySysSoaNo = pagySysSoaNo == null ? null : pagySysSoaNo.trim();
    }

    public String getPagySysSoaVersion() {
        return pagySysSoaVersion;
    }

    public void setPagySysSoaVersion(String pagySysSoaVersion) {
        this.pagySysSoaVersion = pagySysSoaVersion == null ? null : pagySysSoaVersion.trim();
    }

    public String getPagySysSoaActionFlag() {
        return pagySysSoaActionFlag;
    }

    public void setPagySysSoaActionFlag(String pagySysSoaActionFlag) {
        this.pagySysSoaActionFlag = pagySysSoaActionFlag == null ? null : pagySysSoaActionFlag.trim();
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getPagyMchtNo() {
        return pagyMchtNo;
    }

    public void setPagyMchtNo(String pagyMchtNo) {
        this.pagyMchtNo = pagyMchtNo == null ? null : pagyMchtNo.trim();
    }

    public String getTpamPagyNo() {
        return tpamPagyNo;
    }

    public void setTpamPagyNo(String tpamPagyNo) {
        this.tpamPagyNo = tpamPagyNo == null ? null : tpamPagyNo.trim();
    }

    public String getTpamChannelNo() {
        return tpamChannelNo;
    }

    public void setTpamChannelNo(String tpamChannelNo) {
        this.tpamChannelNo = tpamChannelNo == null ? null : tpamChannelNo.trim();
    }

    public String getTpamSetlModel() {
        return tpamSetlModel;
    }

    public void setTpamSetlModel(String tpamSetlModel) {
        this.tpamSetlModel = tpamSetlModel == null ? null : tpamSetlModel.trim();
    }

    public String getTpamPagyMchtNo() {
        return tpamPagyMchtNo;
    }

    public void setTpamPagyMchtNo(String tpamPagyMchtNo) {
        this.tpamPagyMchtNo = tpamPagyMchtNo == null ? null : tpamPagyMchtNo.trim();
    }

    public String getTpamTxnTypeNo() {
        return tpamTxnTypeNo;
    }

    public void setTpamTxnTypeNo(String tpamTxnTypeNo) {
        this.tpamTxnTypeNo = tpamTxnTypeNo == null ? null : tpamTxnTypeNo.trim();
    }

    public String getTpamTxnSsn() {
        return tpamTxnSsn;
    }

    public void setTpamTxnSsn(String tpamTxnSsn) {
        this.tpamTxnSsn = tpamTxnSsn == null ? null : tpamTxnSsn.trim();
    }

    public String getTpamTxnTm() {
        return tpamTxnTm;
    }

    public void setTpamTxnTm(String tpamTxnTm) {
        this.tpamTxnTm = tpamTxnTm == null ? null : tpamTxnTm.trim();
    }

    public String getTpamOrigTxnSsn() {
        return tpamOrigTxnSsn;
    }

    public void setTpamOrigTxnSsn(String tpamOrigTxnSsn) {
        this.tpamOrigTxnSsn = tpamOrigTxnSsn == null ? null : tpamOrigTxnSsn.trim();
    }

    public String getTpamOrigTxnTm() {
        return tpamOrigTxnTm;
    }

    public void setTpamOrigTxnTm(String tpamOrigTxnTm) {
        this.tpamOrigTxnTm = tpamOrigTxnTm == null ? null : tpamOrigTxnTm.trim();
    }

    public String getTpamAcctTypeNo() {
        return tpamAcctTypeNo;
    }

    public void setTpamAcctTypeNo(String tpamAcctTypeNo) {
        this.tpamAcctTypeNo = tpamAcctTypeNo == null ? null : tpamAcctTypeNo.trim();
    }

    public String getTpamAcctSubTypeNo() {
        return tpamAcctSubTypeNo;
    }

    public void setTpamAcctSubTypeNo(String tpamAcctSubTypeNo) {
        this.tpamAcctSubTypeNo = tpamAcctSubTypeNo == null ? null : tpamAcctSubTypeNo.trim();
    }

    public String getTpamCardTypeNo() {
        return tpamCardTypeNo;
    }

    public void setTpamCardTypeNo(String tpamCardTypeNo) {
        this.tpamCardTypeNo = tpamCardTypeNo == null ? null : tpamCardTypeNo.trim();
    }

    public String getTpamAcctNo() {
        return tpamAcctNo;
    }

    public void setTpamAcctNo(String tpamAcctNo) {
        this.tpamAcctNo = tpamAcctNo == null ? null : tpamAcctNo.trim();
    }

    public String getTpamOrderType() {
        return tpamOrderType;
    }

    public void setTpamOrderType(String tpamOrderType) {
        this.tpamOrderType = tpamOrderType == null ? null : tpamOrderType.trim();
    }

    public String getTpamQrValidTm() {
        return tpamQrValidTm;
    }

    public void setTpamQrValidTm(String tpamQrValidTm) {
        this.tpamQrValidTm = tpamQrValidTm == null ? null : tpamQrValidTm.trim();
    }

    public String getTpamAddNcondInfo() {
        return tpamAddNcondInfo;
    }

    public void setTpamAddNcondInfo(String tpamAddNcondInfo) {
        this.tpamAddNcondInfo = tpamAddNcondInfo == null ? null : tpamAddNcondInfo.trim();
    }

    public String getTpamAddnOpUrl() {
        return tpamAddnOpUrl;
    }

    public void setTpamAddnOpUrl(String tpamAddnOpUrl) {
        this.tpamAddnOpUrl = tpamAddnOpUrl == null ? null : tpamAddnOpUrl.trim();
    }

    public String getTpamTokenPayId() {
        return tpamTokenPayId;
    }

    public void setTpamTokenPayId(String tpamTokenPayId) {
        this.tpamTokenPayId = tpamTokenPayId == null ? null : tpamTokenPayId.trim();
    }

    public String getTpamTokenPayInfo() {
        return tpamTokenPayInfo;
    }

    public void setTpamTokenPayInfo(String tpamTokenPayInfo) {
        this.tpamTokenPayInfo = tpamTokenPayInfo == null ? null : tpamTokenPayInfo.trim();
    }

    public String getTpamEmvQrcCode() {
        return tpamEmvQrcCode;
    }

    public void setTpamEmvQrcCode(String tpamEmvQrcCode) {
        this.tpamEmvQrcCode = tpamEmvQrcCode == null ? null : tpamEmvQrcCode.trim();
    }

    public String getTpamTokenPayLimitCount() {
        return tpamTokenPayLimitCount;
    }

    public void setTpamTokenPayLimitCount(String tpamTokenPayLimitCount) {
        this.tpamTokenPayLimitCount = tpamTokenPayLimitCount == null ? null : tpamTokenPayLimitCount.trim();
    }

    public String getTpamRiskInfo() {
        return tpamRiskInfo;
    }

    public void setTpamRiskInfo(String tpamRiskInfo) {
        this.tpamRiskInfo = tpamRiskInfo == null ? null : tpamRiskInfo.trim();
    }

    public String getPagyBackendUrl() {
        return pagyBackendUrl;
    }

    public void setPagyBackendUrl(String pagyBackendUrl) {
        this.pagyBackendUrl = pagyBackendUrl == null ? null : pagyBackendUrl.trim();
    }

    public String getTpamTxnStartTm() {
        return tpamTxnStartTm;
    }

    public void setTpamTxnStartTm(String tpamTxnStartTm) {
        this.tpamTxnStartTm = tpamTxnStartTm == null ? null : tpamTxnStartTm.trim();
    }

    public String getTpamTxnExpireTm() {
        return tpamTxnExpireTm;
    }

    public void setTpamTxnExpireTm(String tpamTxnExpireTm) {
        this.tpamTxnExpireTm = tpamTxnExpireTm == null ? null : tpamTxnExpireTm.trim();
    }

    public String getTpamCouponifo() {
        return tpamCouponifo;
    }

    public void setTpamCouponifo(String tpamCouponifo) {
        this.tpamCouponifo = tpamCouponifo == null ? null : tpamCouponifo.trim();
    }

    public String getTpamBankNo() {
        return tpamBankNo;
    }

    public void setTpamBankNo(String tpamBankNo) {
        this.tpamBankNo = tpamBankNo == null ? null : tpamBankNo.trim();
    }

    public String getTpamBankTxnTm() {
        return tpamBankTxnTm;
    }

    public void setTpamBankTxnTm(String tpamBankTxnTm) {
        this.tpamBankTxnTm = tpamBankTxnTm == null ? null : tpamBankTxnTm.trim();
    }

    public String getTpamComInfo() {
        return tpamComInfo;
    }

    public void setTpamComInfo(String tpamComInfo) {
        this.tpamComInfo = tpamComInfo == null ? null : tpamComInfo.trim();
    }

    public String getTpamSettleKey() {
        return tpamSettleKey;
    }

    public void setTpamSettleKey(String tpamSettleKey) {
        this.tpamSettleKey = tpamSettleKey == null ? null : tpamSettleKey.trim();
    }

    public String getTpamTxnNo() {
        return tpamTxnNo;
    }

    public void setTpamTxnNo(String tpamTxnNo) {
        this.tpamTxnNo = tpamTxnNo == null ? null : tpamTxnNo.trim();
    }

    public String getTpamOrigTxnAmt() {
        return tpamOrigTxnAmt;
    }

    public void setTpamOrigTxnAmt(String tpamOrigTxnAmt) {
        this.tpamOrigTxnAmt = tpamOrigTxnAmt == null ? null : tpamOrigTxnAmt.trim();
    }

    public Long getTpamTxnAmt() {
        return tpamTxnAmt;
    }

    public void setTpamTxnAmt(Long tpamTxnAmt) {
        this.tpamTxnAmt = tpamTxnAmt;
    }

    public String getTpamStlmDt() {
        return tpamStlmDt;
    }

    public void setTpamStlmDt(String tpamStlmDt) {
        this.tpamStlmDt = tpamStlmDt == null ? null : tpamStlmDt.trim();
    }

    public String getTpamStlmTm() {
        return tpamStlmTm;
    }

    public void setTpamStlmTm(String tpamStlmTm) {
        this.tpamStlmTm = tpamStlmTm == null ? null : tpamStlmTm.trim();
    }

    public String getTpamStlmAmt() {
        return tpamStlmAmt;
    }

    public void setTpamStlmAmt(String tpamStlmAmt) {
        this.tpamStlmAmt = tpamStlmAmt == null ? null : tpamStlmAmt.trim();
    }

    public String getTpamSettlementFlag() {
        return tpamSettlementFlag;
    }

    public void setTpamSettlementFlag(String tpamSettlementFlag) {
        this.tpamSettlementFlag = tpamSettlementFlag == null ? null : tpamSettlementFlag.trim();
    }

    public String getTpamSettleFee() {
        return tpamSettleFee;
    }

    public void setTpamSettleFee(String tpamSettleFee) {
        this.tpamSettleFee = tpamSettleFee == null ? null : tpamSettleFee.trim();
    }

    public String getTpamStlmCecyCode() {
        return tpamStlmCecyCode;
    }

    public void setTpamStlmCecyCode(String tpamStlmCecyCode) {
        this.tpamStlmCecyCode = tpamStlmCecyCode == null ? null : tpamStlmCecyCode.trim();
    }

    public String getTpamIssueBankId() {
        return tpamIssueBankId;
    }

    public void setTpamIssueBankId(String tpamIssueBankId) {
        this.tpamIssueBankId = tpamIssueBankId == null ? null : tpamIssueBankId.trim();
    }

    public String getTpamIssueCardType() {
        return tpamIssueCardType;
    }

    public void setTpamIssueCardType(String tpamIssueCardType) {
        this.tpamIssueCardType = tpamIssueCardType == null ? null : tpamIssueCardType.trim();
    }

    public String getTpamIsslnsCode() {
        return tpamIsslnsCode;
    }

    public void setTpamIsslnsCode(String tpamIsslnsCode) {
        this.tpamIsslnsCode = tpamIsslnsCode == null ? null : tpamIsslnsCode.trim();
    }

    public String getTpamPayCardType() {
        return tpamPayCardType;
    }

    public void setTpamPayCardType(String tpamPayCardType) {
        this.tpamPayCardType = tpamPayCardType == null ? null : tpamPayCardType.trim();
    }

    public String getTpamRespCode() {
        return tpamRespCode;
    }

    public void setTpamRespCode(String tpamRespCode) {
        this.tpamRespCode = tpamRespCode == null ? null : tpamRespCode.trim();
    }

    public String getTpamRespMsg() {
        return tpamRespMsg;
    }

    public void setTpamRespMsg(String tpamRespMsg) {
        this.tpamRespMsg = tpamRespMsg == null ? null : tpamRespMsg.trim();
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

    public String getPagySteDate() {
        return pagySteDate;
    }

    public void setPagySteDate(String pagySteDate) {
        this.pagySteDate = pagySteDate == null ? null : pagySteDate.trim();
    }

    public String getInitLocalhostIp() {
        return initLocalhostIp;
    }

    public void setInitLocalhostIp(String initLocalhostIp) {
        this.initLocalhostIp = initLocalhostIp == null ? null : initLocalhostIp.trim();
    }

    public String getUpdLocalhostIp() {
        return updLocalhostIp;
    }

    public void setUpdLocalhostIp(String updLocalhostIp) {
        this.updLocalhostIp = updLocalhostIp == null ? null : updLocalhostIp.trim();
    }

    public String getSysTraceId() {
        return sysTraceId;
    }

    public void setSysTraceId(String sysTraceId) {
        this.sysTraceId = sysTraceId == null ? null : sysTraceId.trim();
    }

    public Date getPagyCrtTm() {
        return pagyCrtTm;
    }

    public void setPagyCrtTm(Date pagyCrtTm) {
        this.pagyCrtTm = pagyCrtTm;
    }

    public Date getPagyUpTm() {
        return pagyUpTm;
    }

    public void setPagyUpTm(Date pagyUpTm) {
        this.pagyUpTm = pagyUpTm;
    }
}