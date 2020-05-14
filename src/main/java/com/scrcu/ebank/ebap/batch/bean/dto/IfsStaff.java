package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

import java.sql.Timestamp;
import java.util.Date;


public class IfsStaff extends CommonDTO {
    private String tlrId;

    private String tlrName;

    private String tlrPw;

    private String tlrCertNo;

    private String workNo;

    private String tlrPhone;

    private String tlrEmail;

    private String tlrState;

    private String sessnId;

    private String loginIp;

    private Date lastLoginSucDt;

    private Date lastLogoutDt;

    private Date lastUpdPwDt;

    private Date lastLoginFailDt;

    private Short pwErrTimes;

    private String isLock;

    private String lockReason;

    private Timestamp crtDt;

    private String crtTlr;

    private Timestamp updDt;

    private String updTlr;

    private String quitFlag;

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId == null ? null : tlrId.trim();
    }

    public String getTlrName() {
        return tlrName;
    }

    public void setTlrName(String tlrName) {
        this.tlrName = tlrName == null ? null : tlrName.trim();
    }

    public String getTlrPw() {
        return tlrPw;
    }

    public void setTlrPw(String tlrPw) {
        this.tlrPw = tlrPw == null ? null : tlrPw.trim();
    }

    public String getTlrCertNo() {
        return tlrCertNo;
    }

    public void setTlrCertNo(String tlrCertNo) {
        this.tlrCertNo = tlrCertNo == null ? null : tlrCertNo.trim();
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo == null ? null : workNo.trim();
    }

    public String getTlrPhone() {
        return tlrPhone;
    }

    public void setTlrPhone(String tlrPhone) {
        this.tlrPhone = tlrPhone == null ? null : tlrPhone.trim();
    }

    public String getTlrEmail() {
        return tlrEmail;
    }

    public void setTlrEmail(String tlrEmail) {
        this.tlrEmail = tlrEmail == null ? null : tlrEmail.trim();
    }

    public String getTlrState() {
        return tlrState;
    }

    public void setTlrState(String tlrState) {
        this.tlrState = tlrState == null ? null : tlrState.trim();
    }

    public String getSessnId() {
        return sessnId;
    }

    public void setSessnId(String sessnId) {
        this.sessnId = sessnId == null ? null : sessnId.trim();
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp == null ? null : loginIp.trim();
    }

    public Date getLastLoginSucDt() {
        return lastLoginSucDt;
    }

    public void setLastLoginSucDt(Date lastLoginSucDt) {
        this.lastLoginSucDt = lastLoginSucDt;
    }

    public Date getLastLogoutDt() {
        return lastLogoutDt;
    }

    public void setLastLogoutDt(Date lastLogoutDt) {
        this.lastLogoutDt = lastLogoutDt;
    }

    public Date getLastUpdPwDt() {
        return lastUpdPwDt;
    }

    public void setLastUpdPwDt(Date lastUpdPwDt) {
        this.lastUpdPwDt = lastUpdPwDt;
    }

    public Date getLastLoginFailDt() {
        return lastLoginFailDt;
    }

    public void setLastLoginFailDt(Date lastLoginFailDt) {
        this.lastLoginFailDt = lastLoginFailDt;
    }

    public Short getPwErrTimes() {
        return pwErrTimes;
    }

    public void setPwErrTimes(Short pwErrTimes) {
        this.pwErrTimes = pwErrTimes;
    }

    public String getIsLock() {
        return isLock;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock == null ? null : isLock.trim();
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason == null ? null : lockReason.trim();
    }

    public Timestamp getCrtDt() {
        return crtDt;
    }

    public void setCrtDt(Timestamp crtDt) {
        this.crtDt = crtDt;
    }

    public String getCrtTlr() {
        return crtTlr;
    }

    public void setCrtTlr(String crtTlr) {
        this.crtTlr = crtTlr == null ? null : crtTlr.trim();
    }

    public Timestamp getUpdDt() {
        return updDt;
    }

    public void setUpdDt(Timestamp updDt) {
        this.updDt = updDt;
    }

    public String getUpdTlr() {
        return updTlr;
    }

    public void setUpdTlr(String updTlr) {
        this.updTlr = updTlr == null ? null : updTlr.trim();
    }

    public String getQuitFlag() {
        return quitFlag;
    }

    public void setQuitFlag(String quitFlag) {
        this.quitFlag = quitFlag == null ? null : quitFlag.trim();
    }
}