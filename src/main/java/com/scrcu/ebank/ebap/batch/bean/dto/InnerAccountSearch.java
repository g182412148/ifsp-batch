package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public class InnerAccountSearch extends CommonResponse {
	private String acctNm;
	
	private String openAcctOrgId;
	
	private String bal;//结算账户余额
	
	private String merId;
	
	private String stopPayTyp;
	
	private String acctTyp;
	
	private String frzTyp;
	
	private String usablBal;//可用余额
	
	private String closAcctStat;

	public String getAcctNm() {
		return acctNm;
	}

	public void setAcctNm(String acctNm) {
		this.acctNm = acctNm;
	}

	public String getOpenAcctOrgId() {
		return openAcctOrgId;
	}

	public void setOpenAcctOrgId(String openAcctOrgId) {
		this.openAcctOrgId = openAcctOrgId;
	}

	public String getBal() {
		return bal;
	}

	public void setBal(String bal) {
		this.bal = bal;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getStopPayTyp() {
		return stopPayTyp;
	}

	public void setStopPayTyp(String stopPayTyp) {
		this.stopPayTyp = stopPayTyp;
	}

	public String getAcctTyp() {
		return acctTyp;
	}

	public void setAcctTyp(String acctTyp) {
		this.acctTyp = acctTyp;
	}

	public String getFrzTyp() {
		return frzTyp;
	}

	public void setFrzTyp(String frzTyp) {
		this.frzTyp = frzTyp;
	}

	public String getUsablBal() {
		return usablBal;
	}

	public void setUsablBal(String usablBal) {
		this.usablBal = usablBal;
	}

	public String getClosAcctStat() {
		return closAcctStat;
	}

	public void setClosAcctStat(String closAcctStat) {
		this.closAcctStat = closAcctStat;
	}
	
}
