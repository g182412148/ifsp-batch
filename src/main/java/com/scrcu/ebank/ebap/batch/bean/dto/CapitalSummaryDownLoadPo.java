package com.scrcu.ebank.ebap.batch.bean.dto;

/**
 * 核心入账结果文件下载
 * @author lenovo
 *
 */
public class CapitalSummaryDownLoadPo {
	private String tranOrg;//机构
	private String feeCatalog;//费用编号
	private String borrowFlag;//借方标志
	private String outAccoutOrg;//借方机构
	private String outAccountNo;//借方账号
	private String lendFlag;//贷方标志
	private String inAccoutOrg;//贷方机构
	private String inAccountNo;//贷方账号
	private String transAmount;//发生金额
	private String transCur;//币种
	private String billFlag;//钞汇标志
	private String summaryCode;//摘要码
	private String summary;//摘要
	private String reserved;//唯一索引
	private String dealResultCode;//错误码
	private String dealResultRemark;//错误信息
	public String getTranOrg() {
		return tranOrg;
	}
	public void setTranOrg(String tranOrg) {
		this.tranOrg = tranOrg;
	}
	public String getFeeCatalog() {
		return feeCatalog;
	}
	public void setFeeCatalog(String feeCatalog) {
		this.feeCatalog = feeCatalog;
	}
	public String getBorrowFlag() {
		return borrowFlag;
	}
	public void setBorrowFlag(String borrowFlag) {
		this.borrowFlag = borrowFlag;
	}
	public String getOutAccoutOrg() {
		return outAccoutOrg;
	}
	public void setOutAccoutOrg(String outAccoutOrg) {
		this.outAccoutOrg = outAccoutOrg;
	}
	public String getOutAccountNo() {
		return outAccountNo;
	}
	public void setOutAccountNo(String outAccountNo) {
		this.outAccountNo = outAccountNo;
	}
	public String getLendFlag() {
		return lendFlag;
	}
	public void setLendFlag(String lendFlag) {
		this.lendFlag = lendFlag;
	}
	public String getInAccoutOrg() {
		return inAccoutOrg;
	}
	public void setInAccoutOrg(String inAccoutOrg) {
		this.inAccoutOrg = inAccoutOrg;
	}
	public String getInAccountNo() {
		return inAccountNo;
	}
	public void setInAccountNo(String inAccountNo) {
		this.inAccountNo = inAccountNo;
	}
	public String getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(String transAmount) {
		this.transAmount = transAmount;
	}
	public String getTransCur() {
		return transCur;
	}
	public void setTransCur(String transCur) {
		this.transCur = transCur;
	}
	public String getBillFlag() {
		return billFlag;
	}
	public void setBillFlag(String billFlag) {
		this.billFlag = billFlag;
	}
	public String getSummaryCode() {
		return summaryCode;
	}
	public void setSummaryCode(String summaryCode) {
		this.summaryCode = summaryCode;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getReserved() {
		return reserved;
	}
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	public String getDealResultCode() {
		return dealResultCode;
	}
	public void setDealResultCode(String dealResultCode) {
		this.dealResultCode = dealResultCode;
	}
	public String getDealResultRemark() {
		return dealResultRemark;
	}
	public void setDealResultRemark(String dealResultRemark) {
		this.dealResultRemark = dealResultRemark;
	}

	
	
	
}
