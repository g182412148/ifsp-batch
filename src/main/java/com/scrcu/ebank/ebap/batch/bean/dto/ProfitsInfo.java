package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;

public class ProfitsInfo 
{
	private String orgType;    //分润方类型01-收单机构，02-运营机构；03-发卡机构
	private String profitOrg;  //分润机构号
	private String profitAcc;  //分润账户，委托模式使用
	private BigDecimal profitAmt;  //分润金额
	private BigDecimal ratio;  //分润比例
	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	public String getProfitOrg() {
		return profitOrg;
	}
	public void setProfitOrg(String profitOrg) {
		this.profitOrg = profitOrg;
	}
	public BigDecimal getProfitAmt() {
		return profitAmt;
	}
	public void setProfitAmt(BigDecimal profitAmt) {
		this.profitAmt = profitAmt;
	}
	public BigDecimal getRatio() {
		return ratio;
	}
	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}
	public String getProfitAcc() {
		return profitAcc;
	}
	public void setProfitAcc(String profitAcc) {
		this.profitAcc = profitAcc;
	}
	
}
