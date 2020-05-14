package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class PagyBaseInfo extends CommonDTO {
    private String pagyNo;

    private String pagySysNo;

    private String pagyName;

    private String tpamPagyNo;

    private String tpamPagyAppId;

    private String tpamPagyAppVersion;

    private String tpamPagyAcsModel;

    private String tpamPagySetlModel;

    private String tpamPagyRefundFlag;

    private Short tpamPagyRefundLimitDate;

    private String extendsPagyMchtFlag;

    private String extendsPagyNo;

    private String pagySetlType;

    private String pagySetltypeValue;

    private String pagySetlTm;

    private String pagySetlAcctType;

    private String pagySetlBankNo;

    private String pagySetlBankNm;

    private String pagySetlBankAddr;

    private String pagySetlAcctNo;

    private String pagySetlAcctNm;

    private String pagyRateCode;

    private String pagyState;

    private String crtTlr;

    private Date crtTm;

    private String lastUpdTlr;

    private Date lastUpdTm;

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public String getPagyName() {
        return pagyName;
    }

    public void setPagyName(String pagyName) {
        this.pagyName = pagyName == null ? null : pagyName.trim();
    }

    public String getTpamPagyNo() {
        return tpamPagyNo;
    }

    public void setTpamPagyNo(String tpamPagyNo) {
        this.tpamPagyNo = tpamPagyNo == null ? null : tpamPagyNo.trim();
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

    public String getTpamPagyAcsModel() {
        return tpamPagyAcsModel;
    }

    public void setTpamPagyAcsModel(String tpamPagyAcsModel) {
        this.tpamPagyAcsModel = tpamPagyAcsModel == null ? null : tpamPagyAcsModel.trim();
    }

    public String getTpamPagySetlModel() {
        return tpamPagySetlModel;
    }

    public void setTpamPagySetlModel(String tpamPagySetlModel) {
        this.tpamPagySetlModel = tpamPagySetlModel == null ? null : tpamPagySetlModel.trim();
    }

    public String getTpamPagyRefundFlag() {
        return tpamPagyRefundFlag;
    }

    public void setTpamPagyRefundFlag(String tpamPagyRefundFlag) {
        this.tpamPagyRefundFlag = tpamPagyRefundFlag == null ? null : tpamPagyRefundFlag.trim();
    }

    public Short getTpamPagyRefundLimitDate() {
        return tpamPagyRefundLimitDate;
    }

    public void setTpamPagyRefundLimitDate(Short tpamPagyRefundLimitDate) {
        this.tpamPagyRefundLimitDate = tpamPagyRefundLimitDate;
    }

    public String getExtendsPagyMchtFlag() {
        return extendsPagyMchtFlag;
    }

    public void setExtendsPagyMchtFlag(String extendsPagyMchtFlag) {
        this.extendsPagyMchtFlag = extendsPagyMchtFlag == null ? null : extendsPagyMchtFlag.trim();
    }

    public String getExtendsPagyNo() {
        return extendsPagyNo;
    }

    public void setExtendsPagyNo(String extendsPagyNo) {
        this.extendsPagyNo = extendsPagyNo == null ? null : extendsPagyNo.trim();
    }

    public String getPagySetlType() {
        return pagySetlType;
    }

    public void setPagySetlType(String pagySetlType) {
        this.pagySetlType = pagySetlType == null ? null : pagySetlType.trim();
    }

    public String getPagySetltypeValue() {
        return pagySetltypeValue;
    }

    public void setPagySetltypeValue(String pagySetltypeValue) {
        this.pagySetltypeValue = pagySetltypeValue == null ? null : pagySetltypeValue.trim();
    }

    public String getPagySetlTm() {
        return pagySetlTm;
    }

    public void setPagySetlTm(String pagySetlTm) {
        this.pagySetlTm = pagySetlTm == null ? null : pagySetlTm.trim();
    }

    public String getPagySetlAcctType() {
        return pagySetlAcctType;
    }

    public void setPagySetlAcctType(String pagySetlAcctType) {
        this.pagySetlAcctType = pagySetlAcctType == null ? null : pagySetlAcctType.trim();
    }

    public String getPagySetlBankNo() {
        return pagySetlBankNo;
    }

    public void setPagySetlBankNo(String pagySetlBankNo) {
        this.pagySetlBankNo = pagySetlBankNo == null ? null : pagySetlBankNo.trim();
    }

    public String getPagySetlBankNm() {
        return pagySetlBankNm;
    }

    public void setPagySetlBankNm(String pagySetlBankNm) {
        this.pagySetlBankNm = pagySetlBankNm == null ? null : pagySetlBankNm.trim();
    }

    public String getPagySetlBankAddr() {
        return pagySetlBankAddr;
    }

    public void setPagySetlBankAddr(String pagySetlBankAddr) {
        this.pagySetlBankAddr = pagySetlBankAddr == null ? null : pagySetlBankAddr.trim();
    }

    public String getPagySetlAcctNo() {
        return pagySetlAcctNo;
    }

    public void setPagySetlAcctNo(String pagySetlAcctNo) {
        this.pagySetlAcctNo = pagySetlAcctNo == null ? null : pagySetlAcctNo.trim();
    }

    public String getPagySetlAcctNm() {
        return pagySetlAcctNm;
    }

    public void setPagySetlAcctNm(String pagySetlAcctNm) {
        this.pagySetlAcctNm = pagySetlAcctNm == null ? null : pagySetlAcctNm.trim();
    }

    public String getPagyRateCode() {
        return pagyRateCode;
    }

    public void setPagyRateCode(String pagyRateCode) {
        this.pagyRateCode = pagyRateCode == null ? null : pagyRateCode.trim();
    }

    public String getPagyState() {
        return pagyState;
    }

    public void setPagyState(String pagyState) {
        this.pagyState = pagyState == null ? null : pagyState.trim();
    }

    public String getCrtTlr() {
        return crtTlr;
    }

    public void setCrtTlr(String crtTlr) {
        this.crtTlr = crtTlr == null ? null : crtTlr.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public String getLastUpdTlr() {
        return lastUpdTlr;
    }

    public void setLastUpdTlr(String lastUpdTlr) {
        this.lastUpdTlr = lastUpdTlr == null ? null : lastUpdTlr.trim();
    }

    public Date getLastUpdTm() {
        return lastUpdTm;
    }

    public void setLastUpdTm(Date lastUpdTm) {
        this.lastUpdTm = lastUpdTm;
    }
}