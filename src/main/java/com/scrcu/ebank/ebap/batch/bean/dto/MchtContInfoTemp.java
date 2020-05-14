package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class MchtContInfoTemp extends CommonDTO {
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

	private String settlAcctOrgName;
    private String settlCycleType;

    private Short settlCycleParam;

    private String depFlag;

    private String depAcctNo;
	private String depAcctName;
	private String depAcctOrgId;

    private BigDecimal depAmt;

    private String depIntFlag;

    private BigDecimal depIntRate;

    private String commType;

    private BigDecimal commParam;

    private String contState;

    private String autoSettlFlag;

	private String liqAcctNo;
	private String liqAcctName;
	private String liqAcctOrgId;

    private String acctNat;

    public String getContNo() {
        return contNo;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo == null ? null : contNo.trim();
    }

    public String getPaperContNo() {
        return paperContNo;
    }

    public void setPaperContNo(String paperContNo) {
        this.paperContNo = paperContNo == null ? null : paperContNo.trim();
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
        this.mchtId = mchtId == null ? null : mchtId.trim();
    }

    public String getSettlType() {
        return settlType;
    }

    public void setSettlType(String settlType) {
        this.settlType = settlType == null ? null : settlType.trim();
    }

    public String getSettlAcctName() {
        return settlAcctName;
    }

    public void setSettlAcctName(String settlAcctName) {
        this.settlAcctName = settlAcctName == null ? null : settlAcctName.trim();
    }

    public String getSettlAcctNo() {
        return settlAcctNo;
    }

    public void setSettlAcctNo(String settlAcctNo) {
        this.settlAcctNo = settlAcctNo == null ? null : settlAcctNo.trim();
    }

    public String getSettlAcctType() {
        return settlAcctType;
    }

    public void setSettlAcctType(String settlAcctType) {
        this.settlAcctType = settlAcctType == null ? null : settlAcctType.trim();
    }

    public String getSettlAcctOrgId() {
        return settlAcctOrgId;
    }

    public void setSettlAcctOrgId(String settlAcctOrgId) {
        this.settlAcctOrgId = settlAcctOrgId == null ? null : settlAcctOrgId.trim();
    }

	public String getSettlAcctOrgName() {
		return settlAcctOrgName;
	}

	public void setSettlAcctOrgName(String settlAcctOrgName) {
		this.settlAcctOrgName = settlAcctOrgName == null ? null : settlAcctOrgName.trim();
	}

	public String getSettlCycleType() {
		return settlCycleType;
	}

    public void setSettlCycleType(String settlCycleType) {
        this.settlCycleType = settlCycleType == null ? null : settlCycleType.trim();
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
        this.depFlag = depFlag == null ? null : depFlag.trim();
    }

    public String getDepAcctNo() {
        return depAcctNo;
    }

    public void setDepAcctNo(String depAcctNo) {
        this.depAcctNo = depAcctNo == null ? null : depAcctNo.trim();
	}
	public String getDepAcctName() {
	    return depAcctName;
	}

	public void setDepAcctName(String depAcctName) {
		this.depAcctName = depAcctName == null ? null : depAcctName.trim();
	}

	public String getDepAcctOrgId() {
	    return depAcctOrgId;
	}

	public void setDepAcctOrgId(String depAcctOrgId) {
	    this.depAcctOrgId = depAcctOrgId == null ? null : depAcctOrgId.trim();
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

    public String getCommType() {
        return commType;
    }

    public void setCommType(String commType) {
        this.commType = commType == null ? null : commType.trim();
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
        this.contState = contState == null ? null : contState.trim();
    }

    public String getAutoSettlFlag() {
        return autoSettlFlag;
    }

    public void setAutoSettlFlag(String autoSettlFlag) {
        this.autoSettlFlag = autoSettlFlag == null ? null : autoSettlFlag.trim();
    }

	public String getLiqAcctNo() {
		return liqAcctNo;
	}

	public void setLiqAcctNo(String liqAcctNo) {
	    this.liqAcctNo = liqAcctNo == null ? null : liqAcctNo.trim();
	}

	public String getLiqAcctName() {
	    return liqAcctName;
	}

	public void setLiqAcctName(String liqAcctName) {
	    this.liqAcctName = liqAcctName == null ? null : liqAcctName.trim();
	}

	public String getLiqAcctOrgId() {
	    return liqAcctOrgId;
	}

	public void setLiqAcctOrgId(String liqAcctOrgId) {
	    this.liqAcctOrgId = liqAcctOrgId == null ? null : liqAcctOrgId.trim();
	}

	public String getAcctNat() {
	    return acctNat;
	}

	public void setAcctNat(String acctNat) {
	    this.acctNat = acctNat == null ? null : acctNat.trim();
	}
}