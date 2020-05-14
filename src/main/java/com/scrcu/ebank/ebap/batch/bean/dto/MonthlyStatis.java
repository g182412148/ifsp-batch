package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;

/**
 * 分店月统计列表
 * @author lenovo
 *
 */
public class MonthlyStatis {

	public String mchtId;//分店商户号
	public String subMchtName;//分店名称
	public BigDecimal monthTxnAmt;//交易金额
	public BigDecimal monthInAcctAmt;//入账金额
	public int monthCnt;//笔数
	public String getMchtId() {
		return mchtId;
	}
	public void setMchtId(String mchtId) {
		this.mchtId = mchtId;
	}
	public String getSubMchtName() {
		return subMchtName;
	}
	public void setSubMchtName(String subMchtName) {
		this.subMchtName = subMchtName;
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
