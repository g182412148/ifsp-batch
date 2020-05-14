package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;

public class ParternInfo extends CommonDTO {
    private String parternId;

    private String parternType;

    private String parternCode;

    private String parternName;

    private String name;

    private String phone;

    private String accountType;

    private String accountNo;

    private String accountName;

    private String accountOrg;

    private BigDecimal settleDay;

    private String monthDay;

    private String qualifiType;

    private String certNo;

    private String artifName;

    private String artifPhone;

    private String artifCardType;

    private String artifCardNo;

    private String cardStartDate;

    private String cardEndDate;

    private String createTlr;

    private String createTime;

    private String updTlr;

    private String updTime;

    private String cooperationPath;

    private String parternStatus;

    private String depAcctNo;

    private BigDecimal depAmt;

    private String depIntFlag;

    private BigDecimal depIntRate;

    private String openClientId;

    private String cmName;

    private String cmPhone;

    private String guaranteeDeposit;

    private String cardExpType;

    private String depOrg;

    public String getParternId() {
        return parternId;
    }

    public void setParternId(String parternId) {
        this.parternId = parternId == null ? null : parternId.trim();
    }

    public String getParternType() {
        return parternType;
    }

    public void setParternType(String parternType) {
        this.parternType = parternType == null ? null : parternType.trim();
    }

    public String getParternCode() {
        return parternCode;
    }

    public void setParternCode(String parternCode) {
        this.parternCode = parternCode == null ? null : parternCode.trim();
    }

    public String getParternName() {
        return parternName;
    }

    public void setParternName(String parternName) {
        this.parternName = parternName == null ? null : parternName.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType == null ? null : accountType.trim();
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo == null ? null : accountNo.trim();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getAccountOrg() {
        return accountOrg;
    }

    public void setAccountOrg(String accountOrg) {
        this.accountOrg = accountOrg == null ? null : accountOrg.trim();
    }

    public BigDecimal getSettleDay() {
        return settleDay;
    }

    public void setSettleDay(BigDecimal settleDay) {
        this.settleDay = settleDay;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay == null ? null : monthDay.trim();
    }

    public String getQualifiType() {
        return qualifiType;
    }

    public void setQualifiType(String qualifiType) {
        this.qualifiType = qualifiType == null ? null : qualifiType.trim();
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo == null ? null : certNo.trim();
    }

    public String getArtifName() {
        return artifName;
    }

    public void setArtifName(String artifName) {
        this.artifName = artifName == null ? null : artifName.trim();
    }

    public String getArtifPhone() {
        return artifPhone;
    }

    public void setArtifPhone(String artifPhone) {
        this.artifPhone = artifPhone == null ? null : artifPhone.trim();
    }

    public String getArtifCardType() {
        return artifCardType;
    }

    public void setArtifCardType(String artifCardType) {
        this.artifCardType = artifCardType == null ? null : artifCardType.trim();
    }

    public String getArtifCardNo() {
        return artifCardNo;
    }

    public void setArtifCardNo(String artifCardNo) {
        this.artifCardNo = artifCardNo == null ? null : artifCardNo.trim();
    }

    public String getCardStartDate() {
        return cardStartDate;
    }

    public void setCardStartDate(String cardStartDate) {
        this.cardStartDate = cardStartDate == null ? null : cardStartDate.trim();
    }

    public String getCardEndDate() {
        return cardEndDate;
    }

    public void setCardEndDate(String cardEndDate) {
        this.cardEndDate = cardEndDate == null ? null : cardEndDate.trim();
    }

    public String getCreateTlr() {
        return createTlr;
    }

    public void setCreateTlr(String createTlr) {
        this.createTlr = createTlr == null ? null : createTlr.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getUpdTlr() {
        return updTlr;
    }

    public void setUpdTlr(String updTlr) {
        this.updTlr = updTlr == null ? null : updTlr.trim();
    }

    public String getUpdTime() {
        return updTime;
    }

    public void setUpdTime(String updTime) {
        this.updTime = updTime == null ? null : updTime.trim();
    }

    public String getCooperationPath() {
        return cooperationPath;
    }

    public void setCooperationPath(String cooperationPath) {
        this.cooperationPath = cooperationPath == null ? null : cooperationPath.trim();
    }

    public String getParternStatus() {
        return parternStatus;
    }

    public void setParternStatus(String parternStatus) {
        this.parternStatus = parternStatus == null ? null : parternStatus.trim();
    }

    public String getDepAcctNo() {
        return depAcctNo;
    }

    public void setDepAcctNo(String depAcctNo) {
        this.depAcctNo = depAcctNo == null ? null : depAcctNo.trim();
    }

    public BigDecimal getDepAmt() {
        return depAmt;
    }

    public void setDepAmt(BigDecimal depAmt) {
        this.depAmt = depAmt;
    }

    public String getDepIntFlag() {
        return depIntFlag;
    }

    public void setDepIntFlag(String depIntFlag) {
        this.depIntFlag = depIntFlag == null ? null : depIntFlag.trim();
    }

    public BigDecimal getDepIntRate() {
        return depIntRate;
    }

    public void setDepIntRate(BigDecimal depIntRate) {
        this.depIntRate = depIntRate;
    }

    public String getOpenClientId() {
        return openClientId;
    }

    public void setOpenClientId(String openClientId) {
        this.openClientId = openClientId == null ? null : openClientId.trim();
    }

    public String getCmName() {
        return cmName;
    }

    public void setCmName(String cmName) {
        this.cmName = cmName == null ? null : cmName.trim();
    }

    public String getCmPhone() {
        return cmPhone;
    }

    public void setCmPhone(String cmPhone) {
        this.cmPhone = cmPhone == null ? null : cmPhone.trim();
    }

    public String getGuaranteeDeposit() {
        return guaranteeDeposit;
    }

    public void setGuaranteeDeposit(String guaranteeDeposit) {
        this.guaranteeDeposit = guaranteeDeposit == null ? null : guaranteeDeposit.trim();
    }

    public String getCardExpType() {
        return cardExpType;
    }

    public void setCardExpType(String cardExpType) {
        this.cardExpType = cardExpType;
    }

    public String getDepOrg() {
        return depOrg;
    }

    public void setDepOrg(String depOrg) {
        this.depOrg = depOrg;
    }
}