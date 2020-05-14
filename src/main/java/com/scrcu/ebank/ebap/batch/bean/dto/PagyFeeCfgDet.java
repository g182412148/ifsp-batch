package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class PagyFeeCfgDet extends CommonDTO {
    private String detId;

    private String cfgId;

    private String pagySysSoaNo;

    private String cardType;

    private String feeTp;

    private BigDecimal feeAmt;

    private BigDecimal feeRate;

    private BigDecimal feeMinAmt;

    private BigDecimal feeMaxAmt;

    private String remark;

    private Date crtTm;

    private Date lastUpdTm;
    
    private String mchtClass;

    public String getMchtClass() {
		return mchtClass;
	}

	public void setMchtClass(String mchtClass) {
		this.mchtClass = mchtClass;
	}

	public String getDetId() {
        return detId;
    }

    public void setDetId(String detId) {
        this.detId = detId == null ? null : detId.trim();
    }

    public String getCfgId() {
        return cfgId;
    }

    public void setCfgId(String cfgId) {
        this.cfgId = cfgId == null ? null : cfgId.trim();
    }

    public String getPagySysSoaNo() {
        return pagySysSoaNo;
    }

    public void setPagySysSoaNo(String pagySysSoaNo) {
        this.pagySysSoaNo = pagySysSoaNo == null ? null : pagySysSoaNo.trim();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
    }

    public String getFeeTp() {
        return feeTp;
    }

    public void setFeeTp(String feeTp) {
        this.feeTp = feeTp == null ? null : feeTp.trim();
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public BigDecimal getFeeMinAmt() {
        return feeMinAmt;
    }

    public void setFeeMinAmt(BigDecimal feeMinAmt) {
        this.feeMinAmt = feeMinAmt;
    }

    public BigDecimal getFeeMaxAmt() {
        return feeMaxAmt;
    }

    public void setFeeMaxAmt(BigDecimal feeMaxAmt) {
        this.feeMaxAmt = feeMaxAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Date getLastUpdTm() {
        return lastUpdTm;
    }

    public void setLastUpdTm(Date lastUpdTm) {
        this.lastUpdTm = lastUpdTm;
    }
}