package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class MchtBaseInfoTemp extends CommonDTO {
    private String mchtId;

    private String mchtName;

    private String mchtSimName;

    private String areaNo;

    private String mchtAddr;

    private String mchtType;

    private String mchtNat;

    private String platFlag;

    private String mchtSrc;

    private String mchtState;

    private String parMchId;

    private String cmNo;

    private String cmName;

    private String blNo;

    private String blType;

    private String blExpType;

    private Date blExpDate;

    private String mccGroupNo;

    private String mccNo;

    private String icpNo;

    private String webSiteAddr;

    private Date regDate;

    private Date applyTime;

    private Date examTime;

    private String examResult;

    private String examPs;

    private String crtTlr;

    private String openClientId;

    private String updTlr;

    private Date updTm;

    private Date crtTm;

    private String mchtClass;

    private BigDecimal mchtLimitForCust;

    private String mchtDesc;

    private String remark;

    private String auditId;

    private String syncState;

    private String examRefuseReason;

    private String mchtEnName;

    private String appName;

    private String email;

    private String approveType;     //审核文件添加

    private String approveResult;     //审核文件添加

    private String settlAcctNo;     //审核文件添加

    private String settlAcctName;     //审核文件添加

    private String settlAcctType;     //审核文件添加

    private String settlAcctOrgId;     //审核文件添加

    private String acctNat;     //审核文件添加

    private String settlCycleParam;     //审核文件添加

    private String phone;     //审核文件添加

    private String contactName;     //审核文件添加联系人姓名

    private String mobilePhone;     //审核文件添加

    private String auditTime;     //审核文件添加 审核时间



    public String getApproveType() {
        return approveType;
    }

    public void setApproveType(String approveType) {
        this.approveType = approveType;
    }

    public String getApproveResult() {
        return approveResult;
    }

    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    public String getSettlAcctNo() {
        return settlAcctNo;
    }

    public void setSettlAcctNo(String settlAcctNo) {
        this.settlAcctNo = settlAcctNo;
    }

    public String getSettlAcctName() {
        return settlAcctName;
    }

    public void setSettlAcctName(String settlAcctName) {
        this.settlAcctName = settlAcctName;
    }

    public String getSettlAcctType() {
        return settlAcctType;
    }

    public void setSettlAcctType(String settlAcctType) {
        this.settlAcctType = settlAcctType;
    }

    public String getSettlAcctOrgId() {
        return settlAcctOrgId;
    }

    public void setSettlAcctOrgId(String settlAcctOrgId) {
        this.settlAcctOrgId = settlAcctOrgId;
    }

    public String getAcctNat() {
        return acctNat;
    }

    public void setAcctNat(String acctNat) {
        this.acctNat = acctNat;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getSettlCycleParam() {
        return settlCycleParam;
    }

    public void setSettlCycleParam(String settlCycleParam) {
        this.settlCycleParam = settlCycleParam;
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId == null ? null : mchtId.trim();
    }

    public String getMchtName() {
        return mchtName;
    }

    public void setMchtName(String mchtName) {
        this.mchtName = mchtName == null ? null : mchtName.trim();
    }

    public String getMchtSimName() {
        return mchtSimName;
    }

    public void setMchtSimName(String mchtSimName) {
        this.mchtSimName = mchtSimName == null ? null : mchtSimName.trim();
    }

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo == null ? null : areaNo.trim();
    }

    public String getMchtAddr() {
        return mchtAddr;
    }

    public void setMchtAddr(String mchtAddr) {
        this.mchtAddr = mchtAddr == null ? null : mchtAddr.trim();
    }

    public String getMchtType() {
        return mchtType;
    }

    public void setMchtType(String mchtType) {
        this.mchtType = mchtType == null ? null : mchtType.trim();
    }

    public String getMchtNat() {
        return mchtNat;
    }

    public void setMchtNat(String mchtNat) {
        this.mchtNat = mchtNat == null ? null : mchtNat.trim();
    }

    public String getPlatFlag() {
        return platFlag;
    }

    public void setPlatFlag(String platFlag) {
        this.platFlag = platFlag == null ? null : platFlag.trim();
    }

    public String getMchtSrc() {
        return mchtSrc;
    }

    public void setMchtSrc(String mchtSrc) {
        this.mchtSrc = mchtSrc == null ? null : mchtSrc.trim();
    }

    public String getMchtState() {
        return mchtState;
    }

    public void setMchtState(String mchtState) {
        this.mchtState = mchtState == null ? null : mchtState.trim();
    }

    public String getParMchId() {
        return parMchId;
    }

    public void setParMchId(String parMchId) {
        this.parMchId = parMchId == null ? null : parMchId.trim();
    }

    public String getCmNo() {
        return cmNo;
    }

    public void setCmNo(String cmNo) {
        this.cmNo = cmNo == null ? null : cmNo.trim();
    }

    public String getCmName() {
        return cmName;
    }

    public void setCmName(String cmName) {
        this.cmName = cmName == null ? null : cmName.trim();
    }

    public String getBlNo() {
        return blNo;
    }

    public void setBlNo(String blNo) {
        this.blNo = blNo == null ? null : blNo.trim();
    }

    public String getBlType() {
        return blType;
    }

    public void setBlType(String blType) {
        this.blType = blType == null ? null : blType.trim();
    }

    public String getBlExpType() {
        return blExpType;
    }

    public void setBlExpType(String blExpType) {
        this.blExpType = blExpType == null ? null : blExpType.trim();
    }

    public Date getBlExpDate() {
        return blExpDate;
    }

    public void setBlExpDate(Date blExpDate) {
        this.blExpDate = blExpDate;
    }

    public String getMccGroupNo() {
        return mccGroupNo;
    }

    public void setMccGroupNo(String mccGroupNo) {
        this.mccGroupNo = mccGroupNo == null ? null : mccGroupNo.trim();
    }

    public String getMccNo() {
        return mccNo;
    }

    public void setMccNo(String mccNo) {
        this.mccNo = mccNo == null ? null : mccNo.trim();
    }

    public String getIcpNo() {
        return icpNo;
    }

    public void setIcpNo(String icpNo) {
        this.icpNo = icpNo == null ? null : icpNo.trim();
    }

    public String getWebSiteAddr() {
        return webSiteAddr;
    }

    public void setWebSiteAddr(String webSiteAddr) {
        this.webSiteAddr = webSiteAddr == null ? null : webSiteAddr.trim();
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getExamTime() {
        return examTime;
    }

    public void setExamTime(Date examTime) {
        this.examTime = examTime;
    }

    public String getExamResult() {
        return examResult;
    }

    public void setExamResult(String examResult) {
        this.examResult = examResult == null ? null : examResult.trim();
    }

    public String getExamPs() {
        return examPs;
    }

    public void setExamPs(String examPs) {
        this.examPs = examPs == null ? null : examPs.trim();
    }

    public String getCrtTlr() {
        return crtTlr;
    }

    public void setCrtTlr(String crtTlr) {
        this.crtTlr = crtTlr == null ? null : crtTlr.trim();
    }

    public String getOpenClientId() {
        return openClientId;
    }

    public void setOpenClientId(String openClientId) {
        this.openClientId = openClientId == null ? null : openClientId.trim();
    }

    public String getUpdTlr() {
        return updTlr;
    }

    public void setUpdTlr(String updTlr) {
        this.updTlr = updTlr == null ? null : updTlr.trim();
    }

    public Date getUpdTm() {
        return updTm;
    }

    public void setUpdTm(Date updTm) {
        this.updTm = updTm;
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public String getMchtClass() {
        return mchtClass;
    }

    public void setMchtClass(String mchtClass) {
        this.mchtClass = mchtClass == null ? null : mchtClass.trim();
    }

    public BigDecimal getMchtLimitForCust() {
        return mchtLimitForCust;
    }

    public void setMchtLimitForCust(BigDecimal mchtLimitForCust) {
        this.mchtLimitForCust = mchtLimitForCust;
    }

    public String getMchtDesc() {
        return mchtDesc;
    }

    public void setMchtDesc(String mchtDesc) {
        this.mchtDesc = mchtDesc == null ? null : mchtDesc.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId == null ? null : auditId.trim();
    }

    public String getSyncState() {
        return syncState;
    }

    public void setSyncState(String syncState) {
        this.syncState = syncState == null ? null : syncState.trim();
    }

    public String getExamRefuseReason() {
        return examRefuseReason;
    }

    public void setExamRefuseReason(String examRefuseReason) {
        this.examRefuseReason = examRefuseReason == null ? null : examRefuseReason.trim();
    }

    public String getMchtEnName() {
        return mchtEnName;
    }

    public void setMchtEnName(String mchtEnName) {
        this.mchtEnName = mchtEnName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }
}