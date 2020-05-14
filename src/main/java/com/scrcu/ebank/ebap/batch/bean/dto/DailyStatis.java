package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分店日统计列表
 * @author lenovo
 *
 */
public class DailyStatis {
	
	private String txnDate;//交易日期
	private BigDecimal monthTxnAmt;//交易金额
	private BigDecimal monthInAcctAmt;//入账金额
	private int monthCnt;//笔数
	public String getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	public BigDecimal getMonthTxnAmt() {
		return monthTxnAmt;
	}
	public void setMonthTxnAmt(BigDecimal monthTxnAmt) {
		this.monthTxnAmt = monthTxnAmt;
	}
	public BigDecimal getMonthInAcctAmt() {
		return monthInAcctAmt;
	}
	public void setMonthInAcctAmt(BigDecimal monthInAcctAmt) {
		this.monthInAcctAmt = monthInAcctAmt;
	}
	public int getMonthCnt() {
		return monthCnt;
	}
	public void setMonthCnt(int monthCnt) {
		this.monthCnt = monthCnt;
	}
	
	
}
