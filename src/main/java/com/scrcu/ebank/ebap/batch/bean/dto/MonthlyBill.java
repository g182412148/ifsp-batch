package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author lenovo
 *
 */
public class MonthlyBill {

	private String txnTime;//日期
	private BigDecimal inAcctAmt;//入账金额
	private BigDecimal txnAmt;//交易金额
	private int txnCount;//笔数
	private String inAcctStat;//入账状态
	
	public String getTxnTime() {
		return txnTime;
	}
	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
	}
	public BigDecimal getInAcctAmt() {
		return inAcctAmt;
	}
	public void setInAcctAmt(BigDecimal inAcctAmt) {
		this.inAcctAmt = inAcctAmt;
	}
	public BigDecimal getTxnAmt() {
		return txnAmt;
	}
	public void setTxnAmt(BigDecimal txnAmt) {
		this.txnAmt = txnAmt;
	}
	public int getTxnCount() {
		return txnCount;
	}
	public void setTxnCount(int txnCount) {
		this.txnCount = txnCount;
	}
	public String getInAcctStat() {
		return inAcctStat;
	}
	public void setInAcctStat(String inAcctStat) {
		this.inAcctStat = inAcctStat;
	}
	
	
	
}
