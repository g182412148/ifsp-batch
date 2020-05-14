package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class MchtStaffInfoTemp extends CommonDTO {
    private String staffId;

    private String staffName;

    private String forceUpdFlag;

    private String staffPhone;

    private String staffEmail;

    private String certType;

    private String certNo;

    private Date certEffDate;

    private Date certExpDate;

    private String frontCertPic;

    private String backCertPic;

    private String allowLogin;

    private String loginPw;

    private String gestPw;

    private String encryptId;

    private String encryptCode;

    private String lastLoginIp;

    private Date lastLoginTm;

    private Short loginPwErrorCnt;

    private Short gestPwErrorCnt;

    private String gestureFlag;

    private String trackFlag;

    private String pushFlag;

    private String voiceFlag;

    private String printFlag;

    private Long printNum;

    private String staffState;

    private Date loginPwErrorTm;

    private Date gestPwErrorTm;

    private String modPwState;

    private String auditId;

    private Date updTm;

    private String termCode;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId == null ? null : staffId.trim();
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName == null ? null : staffName.trim();
    }

    public String getForceUpdFlag() {
        return forceUpdFlag;
    }

    public void setForceUpdFlag(String forceUpdFlag) {
        this.forceUpdFlag = forceUpdFlag == null ? null : forceUpdFlag.trim();
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone == null ? null : staffPhone.trim();
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail == null ? null : staffEmail.trim();
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType == null ? null : certType.trim();
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo == null ? null : certNo.trim();
    }

    public Date getCertEffDate() {
        return certEffDate;
    }

    public void setCertEffDate(Date certEffDate) {
        this.certEffDate = certEffDate;
    }

    public Date getCertExpDate() {
        return certExpDate;
    }

    public void setCertExpDate(Date certExpDate) {
        this.certExpDate = certExpDate;
    }

    public String getFrontCertPic() {
        return frontCertPic;
    }

    public void setFrontCertPic(String frontCertPic) {
        this.frontCertPic = frontCertPic == null ? null : frontCertPic.trim();
    }

    public String getBackCertPic() {
        return backCertPic;
    }

    public void setBackCertPic(String backCertPic) {
        this.backCertPic = backCertPic == null ? null : backCertPic.trim();
    }

    public String getAllowLogin() {
        return allowLogin;
    }

    public void setAllowLogin(String allowLogin) {
        this.allowLogin = allowLogin == null ? null : allowLogin.trim();
    }

    public String getLoginPw() {
        return loginPw;
    }

    public void setLoginPw(String loginPw) {
        this.loginPw = loginPw == null ? null : loginPw.trim();
    }

    public String getGestPw() {
        return gestPw;
    }

    public void setGestPw(String gestPw) {
        this.gestPw = gestPw == null ? null : gestPw.trim();
    }

    public String getEncryptId() {
        return encryptId;
    }

    public void setEncryptId(String encryptId) {
        this.encryptId = encryptId == null ? null : encryptId.trim();
    }

    public String getEncryptCode() {
        return encryptCode;
    }

    public void setEncryptCode(String encryptCode) {
        this.encryptCode = encryptCode == null ? null : encryptCode.trim();
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp == null ? null : lastLoginIp.trim();
    }

    public Date getLastLoginTm() {
        return lastLoginTm;
    }

    public void setLastLoginTm(Date lastLoginTm) {
        this.lastLoginTm = lastLoginTm;
    }

    public Short getLoginPwErrorCnt() {
        return loginPwErrorCnt;
    }

    public void setLoginPwErrorCnt(Short loginPwErrorCnt) {
        this.loginPwErrorCnt = loginPwErrorCnt;
    }

    public Short getGestPwErrorCnt() {
        return gestPwErrorCnt;
    }

    public void setGestPwErrorCnt(Short gestPwErrorCnt) {
        this.gestPwErrorCnt = gestPwErrorCnt;
    }

    public String getGestureFlag() {
        return gestureFlag;
    }

    public void setGestureFlag(String gestureFlag) {
        this.gestureFlag = gestureFlag == null ? null : gestureFlag.trim();
    }

    public String getTrackFlag() {
        return trackFlag;
    }

    public void setTrackFlag(String trackFlag) {
        this.trackFlag = trackFlag == null ? null : trackFlag.trim();
    }

    public String getPushFlag() {
        return pushFlag;
    }

    public void setPushFlag(String pushFlag) {
        this.pushFlag = pushFlag == null ? null : pushFlag.trim();
    }

    public String getVoiceFlag() {
        return voiceFlag;
    }

    public void setVoiceFlag(String voiceFlag) {
        this.voiceFlag = voiceFlag == null ? null : voiceFlag.trim();
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag == null ? null : printFlag.trim();
    }

    public Long getPrintNum() {
        return printNum;
    }

    public void setPrintNum(Long printNum) {
        this.printNum = printNum;
    }

    public String getStaffState() {
        return staffState;
    }

    public void setStaffState(String staffState) {
        this.staffState = staffState == null ? null : staffState.trim();
    }

    public Date getLoginPwErrorTm() {
        return loginPwErrorTm;
    }

    public void setLoginPwErrorTm(Date loginPwErrorTm) {
        this.loginPwErrorTm = loginPwErrorTm;
    }

    public Date getGestPwErrorTm() {
        return gestPwErrorTm;
    }

    public void setGestPwErrorTm(Date gestPwErrorTm) {
        this.gestPwErrorTm = gestPwErrorTm;
    }

    public String getModPwState() {
        return modPwState;
    }

    public void setModPwState(String modPwState) {
        this.modPwState = modPwState == null ? null : modPwState.trim();
    }

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId == null ? null : auditId.trim();
    }

    public Date getUpdTm() {
        return updTm;
    }

    public void setUpdTm(Date updTm) {
        this.updTm = updTm;
    }

    public String getTermCode() {
        return termCode;
    }

    public void setTermCode(String termCode) {
        this.termCode = termCode == null ? null : termCode.trim();
    }
}