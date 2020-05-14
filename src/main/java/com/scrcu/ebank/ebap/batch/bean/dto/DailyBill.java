package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author lenovo
 *
 */
public class DailyBill {

	private String txnSeqId; // 流水号
	private String txnType; // 交易类型
	private BigDecimal txnAmt; // 交易金额
	private BigDecimal feeAmt; // 手续费金额
	private BigDecimal discountAmt; // 营销补贴金额
	private BigDecimal inAcctAmt; // 入账金额
	private String txnTime; // 交易时间
	private String inAcctStat; // 交易状态
	
	public String getTxnSeqId() {
		return txnSeqId;
	}
	public void setTxnSeqId(String txnSeqId) {
		this.txnSeqId = txnSeqId;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public BigDecimal getTxnAmt() {
		return txnAmt;
	}
	public void setTxnAmt(BigDecimal txnAmt) {
		this.txnAmt = txnAmt;
	}
	public BigDecimal getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(BigDecimal feeAmt) {
		this.feeAmt = feeAmt;
	}
	public BigDecimal getDiscountAmt() {
		return discountAmt;
	}
	public void setDiscountAmt(BigDecimal discountAmt) {
		this.discountAmt = discountAmt;
	}
	public BigDecimal getInAcctAmt() {
		return inAcctAmt;
	}
	public void setInAcctAmt(BigDecimal inAcctAmt) {
		this.inAcctAmt = inAcctAmt;
	}
	public String getTxnTime() {
		return txnTime;
	}
	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
	}

	public String getInAcctStat() {
		return inAcctStat;
	}

	public void setInAcctStat(String inAcctStat) {
		this.inAcctStat = inAcctStat;
	}
}
