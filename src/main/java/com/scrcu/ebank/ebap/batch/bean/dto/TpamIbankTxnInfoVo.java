package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class TpamIbankTxnInfoVo extends CommonDTO {
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

    private String tpamChannelNo;

    private String tpamSetlModel;

    private String tpamTxnTypeNo;

    private String tpamTxnSsn;

    private String tpamTxnTm;

    private String tpamOrigTxnSsn;

    private String tpamOrigTxnTm;

    private String tpamAcctOutTypeNo;

    private String tpamAcctOutSubTypeNo;

    private String tpamCardOutTypeNo;

    private String tpamAcctOutNo;

    private String tpamAcctOutCustomerInfo;

    private String tpamAcctOutTokenBindId;

    private String tpamAcctInTypeNo;

    private String tpamAcctInSubTypeNo;

    private String tpamCardInTypeNo;

    private String tpamAcctInNo;

    private String tpamAcctInCustomerInfo;

    private String tpamAcctInTokenBindId;

    private String tpamTerType;

    private String tpamBrNo;

    private String tpamTellerNo;

    private String tpamQuotaNo;

    private String tpamOption;

    private String tpamFuncCtrlFlag;

    private String tpmaMsInd;

    private String tpamCashTranLnd;

    private String tpamCntPtyMsLnd;

    private String tpamCntPtyCashFlag;

    private Long tpamTxnAmt;

    private String pagyBackendUrl;

    private String tpamStlmDt;

    private String tpamStlmTm;

    private String tpamStlmAmt;

    private String tpamSettlementFlag;

    private String tpamSettleFee;

    private String tpamStlmCecyCode;

    private String tpamRespCode;

    private String tpamRespMsg;

    private String pagySteDate;

    private String pagyRespCode;

    private String pagyRespMsg;

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

    public String getTpamAcctOutTypeNo() {
        return tpamAcctOutTypeNo;
    }

    public void setTpamAcctOutTypeNo(String tpamAcctOutTypeNo) {
        this.tpamAcctOutTypeNo = tpamAcctOutTypeNo == null ? null : tpamAcctOutTypeNo.trim();
    }

    public String getTpamAcctOutSubTypeNo() {
        return tpamAcctOutSubTypeNo;
    }

    public void setTpamAcctOutSubTypeNo(String tpamAcctOutSubTypeNo) {
        this.tpamAcctOutSubTypeNo = tpamAcctOutSubTypeNo == null ? null : tpamAcctOutSubTypeNo.trim();
    }

    public String getTpamCardOutTypeNo() {
        return tpamCardOutTypeNo;
    }

    public void setTpamCardOutTypeNo(String tpamCardOutTypeNo) {
        this.tpamCardOutTypeNo = tpamCardOutTypeNo == null ? null : tpamCardOutTypeNo.trim();
    }

    public String getTpamAcctOutNo() {
        return tpamAcctOutNo;
    }

    public void setTpamAcctOutNo(String tpamAcctOutNo) {
        this.tpamAcctOutNo = tpamAcctOutNo == null ? null : tpamAcctOutNo.trim();
    }

    public String getTpamAcctOutCustomerInfo() {
        return tpamAcctOutCustomerInfo;
    }

    public void setTpamAcctOutCustomerInfo(String tpamAcctOutCustomerInfo) {
        this.tpamAcctOutCustomerInfo = tpamAcctOutCustomerInfo == null ? null : tpamAcctOutCustomerInfo.trim();
    }

    public String getTpamAcctOutTokenBindId() {
        return tpamAcctOutTokenBindId;
    }

    public void setTpamAcctOutTokenBindId(String tpamAcctOutTokenBindId) {
        this.tpamAcctOutTokenBindId = tpamAcctOutTokenBindId == null ? null : tpamAcctOutTokenBindId.trim();
    }

    public String getTpamAcctInTypeNo() {
        return tpamAcctInTypeNo;
    }

    public void setTpamAcctInTypeNo(String tpamAcctInTypeNo) {
        this.tpamAcctInTypeNo = tpamAcctInTypeNo == null ? null : tpamAcctInTypeNo.trim();
    }

    public String getTpamAcctInSubTypeNo() {
        return tpamAcctInSubTypeNo;
    }

    public void setTpamAcctInSubTypeNo(String tpamAcctInSubTypeNo) {
        this.tpamAcctInSubTypeNo = tpamAcctInSubTypeNo == null ? null : tpamAcctInSubTypeNo.trim();
    }

    public String getTpamCardInTypeNo() {
        return tpamCardInTypeNo;
    }

    public void setTpamCardInTypeNo(String tpamCardInTypeNo) {
        this.tpamCardInTypeNo = tpamCardInTypeNo == null ? null : tpamCardInTypeNo.trim();
    }

    public String getTpamAcctInNo() {
        return tpamAcctInNo;
    }

    public void setTpamAcctInNo(String tpamAcctInNo) {
        this.tpamAcctInNo = tpamAcctInNo == null ? null : tpamAcctInNo.trim();
    }

    public String getTpamAcctInCustomerInfo() {
        return tpamAcctInCustomerInfo;
    }

    public void setTpamAcctInCustomerInfo(String tpamAcctInCustomerInfo) {
        this.tpamAcctInCustomerInfo = tpamAcctInCustomerInfo == null ? null : tpamAcctInCustomerInfo.trim();
    }

    public String getTpamAcctInTokenBindId() {
        return tpamAcctInTokenBindId;
    }

    public void setTpamAcctInTokenBindId(String tpamAcctInTokenBindId) {
        this.tpamAcctInTokenBindId = tpamAcctInTokenBindId == null ? null : tpamAcctInTokenBindId.trim();
    }

    public String getTpamTerType() {
        return tpamTerType;
    }

    public void setTpamTerType(String tpamTerType) {
        this.tpamTerType = tpamTerType == null ? null : tpamTerType.trim();
    }

    public String getTpamBrNo() {
        return tpamBrNo;
    }

    public void setTpamBrNo(String tpamBrNo) {
        this.tpamBrNo = tpamBrNo == null ? null : tpamBrNo.trim();
    }

    public String getTpamTellerNo() {
        return tpamTellerNo;
    }

    public void setTpamTellerNo(String tpamTellerNo) {
        this.tpamTellerNo = tpamTellerNo == null ? null : tpamTellerNo.trim();
    }

    public String getTpamQuotaNo() {
        return tpamQuotaNo;
    }

    public void setTpamQuotaNo(String tpamQuotaNo) {
        this.tpamQuotaNo = tpamQuotaNo == null ? null : tpamQuotaNo.trim();
    }

    public String getTpamOption() {
        return tpamOption;
    }

    public void setTpamOption(String tpamOption) {
        this.tpamOption = tpamOption == null ? null : tpamOption.trim();
    }

    public String getTpamFuncCtrlFlag() {
        return tpamFuncCtrlFlag;
    }

    public void setTpamFuncCtrlFlag(String tpamFuncCtrlFlag) {
        this.tpamFuncCtrlFlag = tpamFuncCtrlFlag == null ? null : tpamFuncCtrlFlag.trim();
    }

    public String getTpmaMsInd() {
        return tpmaMsInd;
    }

    public void setTpmaMsInd(String tpmaMsInd) {
        this.tpmaMsInd = tpmaMsInd == null ? null : tpmaMsInd.trim();
    }

    public String getTpamCashTranLnd() {
        return tpamCashTranLnd;
    }

    public void setTpamCashTranLnd(String tpamCashTranLnd) {
        this.tpamCashTranLnd = tpamCashTranLnd == null ? null : tpamCashTranLnd.trim();
    }

    public String getTpamCntPtyMsLnd() {
        return tpamCntPtyMsLnd;
    }

    public void setTpamCntPtyMsLnd(String tpamCntPtyMsLnd) {
        this.tpamCntPtyMsLnd = tpamCntPtyMsLnd == null ? null : tpamCntPtyMsLnd.trim();
    }

    public String getTpamCntPtyCashFlag() {
        return tpamCntPtyCashFlag;
    }

    public void setTpamCntPtyCashFlag(String tpamCntPtyCashFlag) {
        this.tpamCntPtyCashFlag = tpamCntPtyCashFlag == null ? null : tpamCntPtyCashFlag.trim();
    }

    public Long getTpamTxnAmt() {
        return tpamTxnAmt;
    }

    public void setTpamTxnAmt(Long tpamTxnAmt) {
        this.tpamTxnAmt = tpamTxnAmt;
    }

    public String getPagyBackendUrl() {
        return pagyBackendUrl;
    }

    public void setPagyBackendUrl(String pagyBackendUrl) {
        this.pagyBackendUrl = pagyBackendUrl == null ? null : pagyBackendUrl.trim();
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

    public String getPagySteDate() {
        return pagySteDate;
    }

    public void setPagySteDate(String pagySteDate) {
        this.pagySteDate = pagySteDate == null ? null : pagySteDate.trim();
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