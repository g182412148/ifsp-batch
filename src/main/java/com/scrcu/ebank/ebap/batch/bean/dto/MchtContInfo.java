package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

public class MchtContInfo {
    private String contNo;

    private String paperContNo;

    private Date contEffDate;

    private Date contExpDate;

    private String mchtId;

    private String settlType;

    private String settlAcctName;

    private String settlAcctNo;

    private String settlAcctType;

    private String settlAcctOrgId;

    private String settlCycleType;

    private Short settlCycleParam;

    private String depFlag;

    private String depAcctNo;

    private BigDecimal depAmt;

    private String depIntFlag;

    private BigDecimal depIntRate;

    private String commType;

    private BigDecimal commParam;

    private String contState;

    private String autoSettlFlag;
    
    private String deptAcctName;
    
    private String deptAcctOrgId;
    
    //商户待清算账户信息
    private String liqAcctNo;
    private String liqAcctName;
    private String liqAcctOrgId;

    //他行手续费信息
    private String otherSettFeeType;
    private BigDecimal otherSettFee;
    private BigDecimal maxOtherSettFee;
    private BigDecimal minOtherSettFee;

    public String getDeptAcctName() {
		return deptAcctName;
	}

	public void setDeptAcctName(String deptAcctName) {
		this.deptAcctName = deptAcctName;
	}

	public String getDeptAcctOrgId() {
		return deptAcctOrgId;
	}

	public void setDeptAcctOrgId(String deptAcctOrgId) {
		this.deptAcctOrgId = deptAcctOrgId;
	}

	public String getLiqAcctNo() {
		return liqAcctNo;
	}

	public void setLiqAcctNo(String liqAcctNo) {
		this.liqAcctNo = liqAcctNo;
	}

	public String getLiqAcctName() {
		return liqAcctName;
	}

	public void setLiqAcctName(String liqAcctName) {
		this.liqAcctName = liqAcctName;
	}

	public String getLiqAcctOrgId() {
		return liqAcctOrgId;
	}

	public void setLiqAcctOrgId(String liqAcctOrgId) {
		this.liqAcctOrgId = liqAcctOrgId;
	}

	public String getContNo() {
        return contNo;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo;
    }

    public String getPaperContNo() {
        return paperContNo;
    }

    public void setPaperContNo(String paperContNo) {
        this.paperContNo = paperContNo;
    }

    public Date getContEffDate() {
        return contEffDate;
    }

    public void setContEffDate(Date contEffDate) {
        this.contEffDate = contEffDate;
    }

    public Date getContExpDate() {
        return contExpDate;
    }

    public void setContExpDate(Date contExpDate) {
        this.contExpDate = contExpDate;
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getSettlType() {
        return settlType;
    }

    public void setSettlType(String settlType) {
        this.settlType = settlType;
    }

    public String getSettlAcctName() {
        return settlAcctName;
    }

    public void setSettlAcctName(String settlAcctName) {
        this.settlAcctName = settlAcctName;
    }

    public String getSettlAcctNo() {
        return settlAcctNo;
    }

    public void setSettlAcctNo(String settlAcctNo) {
        this.settlAcctNo = settlAcctNo;
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

    public String getSettlCycleType() {
        return settlCycleType;
    }

    public void setSettlCycleType(String settlCycleType) {
        this.settlCycleType = settlCycleType;
    }

    public Short getSettlCycleParam() {
        return settlCycleParam;
    }

    public void setSettlCycleParam(Short settlCycleParam) {
        this.settlCycleParam = settlCycleParam;
    }

    public String getDepFlag() {
        return depFlag;
    }

    public void setDepFlag(String depFlag) {
        this.depFlag = depFlag;
    }

    public String getDepAcctNo() {
        return depAcctNo;
    }

    public void setDepAcctNo(String depAcctNo) {
        this.depAcctNo = depAcctNo;
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
        this.depIntFlag = depIntFlag;
    }

    public BigDecimal getDepIntRate() {
        return depIntRate;
    }

    public void setDepIntRate(BigDecimal depIntRate) {
        this.depIntRate = depIntRate;
    }

    public String getCommType() {
        return commType;
    }

    public void setCommType(String commType) {
        this.commType = commType;
    }

    public BigDecimal getCommParam() {
        return commParam;
    }

    public void setCommParam(BigDecimal commParam) {
        this.commParam = commParam;
    }

    public String getContState() {
        return contState;
    }

    public void setContState(String contState) {
        this.contState = contState;
    }

    public String getAutoSettlFlag() {
        return autoSettlFlag;
    }

    public void setAutoSettlFlag(String autoSettlFlag) {
        this.autoSettlFlag = autoSettlFlag;
    }

    public String getOtherSettFeeType() {
        return otherSettFeeType;
    }

    public void setOtherSettFeeType(String otherSettFeeType) {
        this.otherSettFeeType = otherSettFeeType;
    }

    public BigDecimal getOtherSettFee() {
        return otherSettFee;
    }

    public void setOtherSettFee(BigDecimal otherSettFee) {
        this.otherSettFee = otherSettFee;
    }

    public BigDecimal getMaxOtherSettFee() {
        return maxOtherSettFee;
    }

    public void setMaxOtherSettFee(BigDecimal maxOtherSettFee) {
        this.maxOtherSettFee = maxOtherSettFee;
    }

    public BigDecimal getMinOtherSettFee() {
        return minOtherSettFee;
    }

    public void setMinOtherSettFee(BigDecimal minOtherSettFee) {
        this.minOtherSettFee = minOtherSettFee;
    }
}