package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class PagyMchtInfo extends CommonDTO {
    private String pagyMchtNo;

    private String pagyMchtNm;

    private String pagyNo;

    private String chlNo;

    private String chlMchtNo;

    private String tpamPagyMchtNo;

    private String tpamMccId;

    private String tpamMccSubId;

    private String tpamMchtAddsCode;

    private String tpamPagyMchtAppId;

    private String tpamPagyMchtAppVersion;

    private BigDecimal tpamCommissionRate;

    private String pagyMchtAplDate;

    private String pagyMchtState;

    private String pagyMchtFailedRes;

    private String crtTlr;

    private Date crtTm;

    private String lastUpdTlr;

    private Date lastUpdTm;

    private String mchtClass;
    private String upMchtSynState;
    private String upMchtSynType;
    private String upMchtSynDate;
    private String upMchtSynFileRes;
    /**
     * 0:pagy_mcht_ifno
     * 1:pagy_mcht_ifno_old
     */
    private String tableType;

    public String getUpMchtSynState() {
        return upMchtSynState;
    }

    public void setUpMchtSynState(String upMchtSynState) {
        this.upMchtSynState = upMchtSynState;
    }

    public String getUpMchtSynType() {
        return upMchtSynType;
    }

    public void setUpMchtSynType(String upMchtSynType) {
        this.upMchtSynType = upMchtSynType;
    }

    public String getUpMchtSynDate() {
        return upMchtSynDate;
    }

    public void setUpMchtSynDate(String upMchtSynDate) {
        this.upMchtSynDate = upMchtSynDate;
    }

    public String getUpMchtSynFileRes() {
        return upMchtSynFileRes;
    }

    public void setUpMchtSynFileRes(String upMchtSynFileRes) {
        this.upMchtSynFileRes = upMchtSynFileRes;
    }

    public String getPagyMchtNo() {
        return pagyMchtNo;
    }

    public void setPagyMchtNo(String pagyMchtNo) {
        this.pagyMchtNo = pagyMchtNo == null ? null : pagyMchtNo.trim();
    }

    public String getPagyMchtNm() {
        return pagyMchtNm;
    }

    public void setPagyMchtNm(String pagyMchtNm) {
        this.pagyMchtNm = pagyMchtNm == null ? null : pagyMchtNm.trim();
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
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

    public String getTpamPagyMchtNo() {
        return tpamPagyMchtNo;
    }

    public void setTpamPagyMchtNo(String tpamPagyMchtNo) {
        this.tpamPagyMchtNo = tpamPagyMchtNo == null ? null : tpamPagyMchtNo.trim();
    }

    public String getTpamMccId() {
        return tpamMccId;
    }

    public void setTpamMccId(String tpamMccId) {
        this.tpamMccId = tpamMccId == null ? null : tpamMccId.trim();
    }

    public String getTpamMccSubId() {
        return tpamMccSubId;
    }

    public void setTpamMccSubId(String tpamMccSubId) {
        this.tpamMccSubId = tpamMccSubId == null ? null : tpamMccSubId.trim();
    }

    public String getTpamMchtAddsCode() {
        return tpamMchtAddsCode;
    }

    public void setTpamMchtAddsCode(String tpamMchtAddsCode) {
        this.tpamMchtAddsCode = tpamMchtAddsCode == null ? null : tpamMchtAddsCode.trim();
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

    public BigDecimal getTpamCommissionRate() {
        return tpamCommissionRate;
    }

    public void setTpamCommissionRate(BigDecimal tpamCommissionRate) {
        this.tpamCommissionRate = tpamCommissionRate;
    }

    public String getPagyMchtAplDate() {
        return pagyMchtAplDate;
    }

    public void setPagyMchtAplDate(String pagyMchtAplDate) {
        this.pagyMchtAplDate = pagyMchtAplDate == null ? null : pagyMchtAplDate.trim();
    }

    public String getPagyMchtState() {
        return pagyMchtState;
    }

    public void setPagyMchtState(String pagyMchtState) {
        this.pagyMchtState = pagyMchtState == null ? null : pagyMchtState.trim();
    }

    public String getPagyMchtFailedRes() {
        return pagyMchtFailedRes;
    }

    public void setPagyMchtFailedRes(String pagyMchtFailedRes) {
        this.pagyMchtFailedRes = pagyMchtFailedRes == null ? null : pagyMchtFailedRes.trim();
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

    public String getMchtClass() {
        return mchtClass;
    }

    public void setMchtClass(String mchtClass) {
        this.mchtClass = mchtClass == null ? null : mchtClass.trim();
    }


    private MchtBaseInfo mchtBaseInfo;

    public MchtBaseInfo getMchtBaseInfo() {
        return mchtBaseInfo;
    }

    public void setMchtBaseInfo(MchtBaseInfo mchtBaseInfo) {
        this.mchtBaseInfo = mchtBaseInfo;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }
}