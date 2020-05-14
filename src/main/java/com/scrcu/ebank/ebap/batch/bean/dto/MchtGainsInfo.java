package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;

public class MchtGainsInfo {
    private String id;

    private String accChnlNo;

    private String acctType;

    private BigDecimal cellectOrgDist;

    private BigDecimal sendOrgDist;

    private BigDecimal proxyOrgDist;

    private String status;

    private String crtTlr;

    private String crtDt;

    private String updTlr;

    private String updDt;

    private String parternId;//服务商编号
    
    //user add
  	private String profitAssignAcc;        //手续费分润账号
  	private BigDecimal profitAssignAmt;    //手续费分润金额
  	private String profitAssignOrg;        //分润账户所属机构

    public String getProfitAssignAcc() {
		return profitAssignAcc;
	}

	public void setProfitAssignAcc(String profitAssignAcc) {
		this.profitAssignAcc = profitAssignAcc;
	}

	public BigDecimal getProfitAssignAmt() {
		return profitAssignAmt;
	}

	public void setProfitAssignAmt(BigDecimal profitAssignAmt) {
		this.profitAssignAmt = profitAssignAmt;
	}

	public String getProfitAssignOrg() {
		return profitAssignOrg;
	}

	public void setProfitAssignOrg(String profitAssignOrg) {
		this.profitAssignOrg = profitAssignOrg;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccChnlNo() {
        return accChnlNo;
    }

    public void setAccChnlNo(String accChnlNo) {
        this.accChnlNo = accChnlNo;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public BigDecimal getCellectOrgDist() {
        return cellectOrgDist;
    }

    public void setCellectOrgDist(BigDecimal cellectOrgDist) {
        this.cellectOrgDist = cellectOrgDist;
    }

    public BigDecimal getSendOrgDist() {
        return sendOrgDist;
    }

    public void setSendOrgDist(BigDecimal sendOrgDist) {
        this.sendOrgDist = sendOrgDist;
    }

    public BigDecimal getProxyOrgDist() {
        return proxyOrgDist;
    }

    public void setProxyOrgDist(BigDecimal proxyOrgDist) {
        this.proxyOrgDist = proxyOrgDist;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCrtTlr() {
        return crtTlr;
    }

    public void setCrtTlr(String crtTlr) {
        this.crtTlr = crtTlr;
    }

    public String getCrtDt() {
        return crtDt;
    }

    public void setCrtDt(String crtDt) {
        this.crtDt = crtDt;
    }

    public String getUpdTlr() {
        return updTlr;
    }

    public void setUpdTlr(String updTlr) {
        this.updTlr = updTlr;
    }

    public String getUpdDt() {
        return updDt;
    }

    public void setUpdDt(String updDt) {
        this.updDt = updDt;
    }

    public String getParternId() {
        return parternId;
    }

    public void setParternId(String parternId) {
        this.parternId = parternId;
    }
}