package com.scrcu.ebank.ebap.batch.bean.dto;

/**
 * 他行入账结果文件
 */
public class SubMerCapMultibank {
    private String seqNo;//交易序号
	private String outAccountNo;//付款人账号
	private String outAccountName;//付款人户名
	private String outAccoutOrg;//付款人机构
	private String inAccountNo;//收款人账号
	private String inAccountName;//收款人户名
	private String inAccoutOrg;//收款人行行号
	private String transCur;//币种
	private String txnAmt;//交易金额
	private String lendFlag;//收费标志
	private String feeCatalog;//费用编号
	private String feeAmt;//手续费金额
	private String summaryCode;//摘要码
	private String summary;//摘要
	private String isScrcuBank;//行内行外标志
	private String id;//文档上-备用字段
	private String reserved2;//文档上-备用字段
	private String dealSuccessTime;//支付平台日期
	private String dealSuccessTxnSsn;//支付平台流水
	private String dealResultCode;//交易状态
	private String dealResultRemark;//文档上-备用字段   说明
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getOutAccountNo() {
		return outAccountNo;
	}
	public void setOutAccountNo(String outAccountNo) {
		this.outAccountNo = outAccountNo;
	}
	public String getOutAccountName() {
		return outAccountName;
	}
	public void setOutAccountName(String outAccountName) {
		this.outAccountName = outAccountName;
	}
	public String getOutAccoutOrg() {
		return outAccoutOrg;
	}
	public void setOutAccoutOrg(String outAccoutOrg) {
		this.outAccoutOrg = outAccoutOrg;
	}
	public String getInAccountNo() {
		return inAccountNo;
	}
	public void setInAccountNo(String inAccountNo) {
		this.inAccountNo = inAccountNo;
	}
	public String getInAccountName() {
		return inAccountName;
	}
	public void setInAccountName(String inAccountName) {
		this.inAccountName = inAccountName;
	}
	public String getInAccoutOrg() {
		return inAccoutOrg;
	}
	public void setInAccoutOrg(String inAccoutOrg) {
		this.inAccoutOrg = inAccoutOrg;
	}
	public String getTransCur() {
		return transCur;
	}
	public void setTransCur(String transCur) {
		this.transCur = transCur;
	}
	public String getTxnAmt() {
		return txnAmt;
	}
	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}
	public String getLendFlag() {
		return lendFlag;
	}
	public void setLendFlag(String lendFlag) {
		this.lendFlag = lendFlag;
	}
	public String getFeeCatalog() {
		return feeCatalog;
	}
	public void setFeeCatalog(String feeCatalog) {
		this.feeCatalog = feeCatalog;
	}
	public String getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
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
	public String getIsScrcuBank() {
		return isScrcuBank;
	}
	public void setIsScrcuBank(String isScrcuBank) {
		this.isScrcuBank = isScrcuBank;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReserved2() {
		return reserved2;
	}
	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	public String getDealSuccessTime() {
		return dealSuccessTime;
	}
	public void setDealSuccessTime(String dealSuccessTime) {
		this.dealSuccessTime = dealSuccessTime;
	}
	public String getDealSuccessTxnSsn() {
		return dealSuccessTxnSsn;
	}
	public void setDealSuccessTxnSsn(String dealSuccessTxnSsn) {
		this.dealSuccessTxnSsn = dealSuccessTxnSsn;
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
