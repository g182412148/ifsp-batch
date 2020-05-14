package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class TpamAtAlipayTxnInfoVo extends CommonDTO {
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

    private String tpamPagyAppId;

    private String tpamPagyAppVersion;

    private String tpamPagyMchtNo;

    private String tpmaPagyMchtTokenId;

    private String tpamPagyMchtAppId;

    private String tpamPagyMchtAppVersion;

    private String tpamTxnTypeNo;

    private String tpamTxnSsn;

    private String tpamTxnTm;

    private String tpamOrigTxnSsn;

    private String tpamOrigTxnTm;

    private String tpamAcctTypeNo;

    private String tpamAcctSubTypeNo;

    private String tpamCardTypeNo;

    private String tpamAcctNo;

    private String tpamBankNo;

    private String tpamBankTxnTm;

    private String tpamTokenPayId;

    private String tpamTokenPayInfo;

    private String pagyBackendUrl;

    private String tpamTxnStartTm;

    private String tpamTxnExpireTm;

    private Long tpamTxnAmt;

    private String tpamIsSubscribe;

    private String tpamSubIsSubscribe;

    private String tpamStlmDt;

    private String tpamStlmTm;

    private String tpamStlmAmt;

    private String tpamSettlementFlag;

    private String tpamSettleFee;

    private String tpamStlmCecyCode;

    private String tpamCouponFee;

    private String tpamCashFee;

    private String tpamCashFeeType;

    private String tpamIssueBankId;

    private String tpamIssueCardType;

    private String tpamIsslnsCode;

    private String tpamPayCardType;

    private String tpamStoreId;

    private String tpamTerminalId;

    private String tpamBuyerId;

    private String tpamBuyerAccount;

    private String tpamPayStatus;

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
    
    private String txnReqSsn;

    private Date txnReqTm;

    private String origTxnReqSsn;

    private Date origTxnReqTm;
    
    private String prodId;

    private String txnType;
    
    
    public String getTxnReqSsn() {
		return txnReqSsn;
	}

	public void setTxnReqSsn(String txnReqSsn) {
		this.txnReqSsn = txnReqSsn;
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
		this.origTxnReqSsn = origTxnReqSsn;
	}

	public Date getOrigTxnReqTm() {
		return origTxnReqTm;
	}

	public void setOrigTxnReqTm(Date origTxnReqTm) {
		this.origTxnReqTm = origTxnReqTm;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

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

    public String getTpamPagyAppId() {
        return tpamPagyAppId;
    }

    public void setTpamPagyAppId(String tpamPagyAppId) {
        this.tpamPagyAppId = tpamPagyAppId == null ? null : tpamPagyAppId.trim();
    }

    public String getTpamPagyAppVersion() {
        return tpamPagyAppVersion;
    }

    public void setTpamPagyAppVersion(String tpamPagyAppVersion) {
        this.tpamPagyAppVersion = tpamPagyAppVersion == null ? null : tpamPagyAppVersion.trim();
    }

    public String getTpamPagyMchtNo() {
        return tpamPagyMchtNo;
    }

    public void setTpamPagyMchtNo(String tpamPagyMchtNo) {
        this.tpamPagyMchtNo = tpamPagyMchtNo == null ? null : tpamPagyMchtNo.trim();
    }

    public String getTpmaPagyMchtTokenId() {
        return tpmaPagyMchtTokenId;
    }

    public void setTpmaPagyMchtTokenId(String tpmaPagyMchtTokenId) {
        this.tpmaPagyMchtTokenId = tpmaPagyMchtTokenId == null ? null : tpmaPagyMchtTokenId.trim();
    }

    public String getTpamPagyMchtAppId() {
        return tpamPagyMchtAppId;
    }

    public void setTpamPagyMchtAppId(String tpamPagyMchtAppId) {
        this.tpamPagyMchtAppId = tpamPagyMchtAppId == null ? null : tpamPagyMchtAppId.trim();
    }

    public String getTpamPagyMchtAppVersion() {
        return tpamPagyMchtAppVersion;
    }

    public void setTpamPagyMchtAppVersion(String tpamPagyMchtAppVersion) {
        this.tpamPagyMchtAppVersion = tpamPagyMchtAppVersion == null ? null : tpamPagyMchtAppVersion.trim();
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

    public Long getTpamTxnAmt() {
        return tpamTxnAmt;
    }

    public void setTpamTxnAmt(Long tpamTxnAmt) {
        this.tpamTxnAmt = tpamTxnAmt;
    }

    public String getTpamIsSubscribe() {
        return tpamIsSubscribe;
    }

    public void setTpamIsSubscribe(String tpamIsSubscribe) {
        this.tpamIsSubscribe = tpamIsSubscribe == null ? null : tpamIsSubscribe.trim();
    }

    public String getTpamSubIsSubscribe() {
        return tpamSubIsSubscribe;
    }

    public void setTpamSubIsSubscribe(String tpamSubIsSubscribe) {
        this.tpamSubIsSubscribe = tpamSubIsSubscribe == null ? null : tpamSubIsSubscribe.trim();
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

    public String getTpamCouponFee() {
        return tpamCouponFee;
    }

    public void setTpamCouponFee(String tpamCouponFee) {
        this.tpamCouponFee = tpamCouponFee == null ? null : tpamCouponFee.trim();
    }

    public String getTpamCashFee() {
        return tpamCashFee;
    }

    public void setTpamCashFee(String tpamCashFee) {
        this.tpamCashFee = tpamCashFee == null ? null : tpamCashFee.trim();
    }

    public String getTpamCashFeeType() {
        return tpamCashFeeType;
    }

    public void setTpamCashFeeType(String tpamCashFeeType) {
        this.tpamCashFeeType = tpamCashFeeType == null ? null : tpamCashFeeType.trim();
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

    public String getTpamStoreId() {
        return tpamStoreId;
    }

    public void setTpamStoreId(String tpamStoreId) {
        this.tpamStoreId = tpamStoreId == null ? null : tpamStoreId.trim();
    }

    public String getTpamTerminalId() {
        return tpamTerminalId;
    }

    public void setTpamTerminalId(String tpamTerminalId) {
        this.tpamTerminalId = tpamTerminalId == null ? null : tpamTerminalId.trim();
    }

    public String getTpamBuyerId() {
        return tpamBuyerId;
    }

    public void setTpamBuyerId(String tpamBuyerId) {
        this.tpamBuyerId = tpamBuyerId == null ? null : tpamBuyerId.trim();
    }

    public String getTpamBuyerAccount() {
        return tpamBuyerAccount;
    }

    public void setTpamBuyerAccount(String tpamBuyerAccount) {
        this.tpamBuyerAccount = tpamBuyerAccount == null ? null : tpamBuyerAccount.trim();
    }

    public String getTpamPayStatus() {
        return tpamPayStatus;
    }

    public void setTpamPayStatus(String tpamPayStatus) {
        this.tpamPayStatus = tpamPayStatus == null ? null : tpamPayStatus.trim();
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