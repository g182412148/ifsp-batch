package com.scrcu.ebank.ebap.batch.bean.response;

import java.math.BigDecimal;

import com.scrcu.ebank.ebap.batch.bean.dto.DailyBill;

/**
 * 名称：〈日账单Response〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月20日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
public class QueryDailyBillResponse extends CommRegiResponse {
	private BigDecimal totalTxnAmt; // 交易金额 totalTxnAmt Amount
	private Integer totalTxnCnt; // 交易笔数 totalTxnCnt Number
	private BigDecimal totalDiscountAmt; // 营销补贴金额（商户出资） totalDiscountAmt Amount
	private BigDecimal totalRefundAmt; // 退款金额 totalRefundAmt Amount
	private BigDecimal totalReceiptFeeAmt; // 收款手续费金额 
	private BigDecimal totalReturnFeeAmt; // 退货手续费金额 
	private BigDecimal totalInAcctAmt; // 入账金额 totalInAcctAmt Amount
	private DailyBill[] dailyBill; // 日账单明细列表 dailyBill JSON Array
	
	private BigDecimal twoMchtOutCommissionAmt;//二级商户／分店反佣金额
	private BigDecimal twoMchtInTxnAmt;//二级商户／分店交易入账至一级商户金额
	private BigDecimal OneToTwoInAcctAmt;//一级商户向二级商户／分店入账金额
	private BigDecimal platMchtOutCommissionAmt;//向平台商户反佣金额
	private BigDecimal oneMchtOutCommissionAmt;//向一级商户反佣金额
	private BigDecimal oneMchtInTxnAmt;//向一级商户入账金额
	private int totalSize;//总条数
	private String inAcctStat;//0:待入账;1:入账成功;2:入账失败
	private String incctime;//入账时间

	public BigDecimal getTotalTxnAmt() {
		return totalTxnAmt;
	}

	public void setTotalTxnAmt(BigDecimal totalTxnAmt) {
		this.totalTxnAmt = totalTxnAmt;
	}

	public Integer getTotalTxnCnt() {
		return totalTxnCnt;
	}

	public void setTotalTxnCnt(Integer totalTxnCnt) {
		this.totalTxnCnt = totalTxnCnt;
	}

	public BigDecimal getTotalDiscountAmt() {
		return totalDiscountAmt;
	}

	public void setTotalDiscountAmt(BigDecimal totalDiscountAmt) {
		this.totalDiscountAmt = totalDiscountAmt;
	}

	public BigDecimal getTotalRefundAmt() {
		return totalRefundAmt;
	}

	public void setTotalRefundAmt(BigDecimal totalRefundAmt) {
		this.totalRefundAmt = totalRefundAmt;
	}

	public BigDecimal getTotalReceiptFeeAmt() {
		return totalReceiptFeeAmt;
	}

	public void setTotalReceiptFeeAmt(BigDecimal totalReceiptFeeAmt) {
		this.totalReceiptFeeAmt = totalReceiptFeeAmt;
	}

	public BigDecimal getTotalReturnFeeAmt() {
		return totalReturnFeeAmt;
	}

	public void setTotalReturnFeeAmt(BigDecimal totalReturnFeeAmt) {
		this.totalReturnFeeAmt = totalReturnFeeAmt;
	}

	public BigDecimal getTotalInAcctAmt() {
		return totalInAcctAmt;
	}

	public void setTotalInAcctAmt(BigDecimal totalInAcctAmt) {
		this.totalInAcctAmt = totalInAcctAmt;
	}

	public DailyBill[] getDailyBill() {
		return dailyBill;
	}

	public void setDailyBill(DailyBill[] dailyBill) {
		this.dailyBill = dailyBill;
	}

	public String getInAcctStat() {
		return inAcctStat;
	}

	public void setInAcctStat(String inAcctStat) {
		this.inAcctStat = inAcctStat;
	}

	public BigDecimal getTwoMchtOutCommissionAmt() {
		return twoMchtOutCommissionAmt;
	}

	public void setTwoMchtOutCommissionAmt(BigDecimal twoMchtOutCommissionAmt) {
		this.twoMchtOutCommissionAmt = twoMchtOutCommissionAmt;
	}

	public BigDecimal getTwoMchtInTxnAmt() {
		return twoMchtInTxnAmt;
	}

	public void setTwoMchtInTxnAmt(BigDecimal twoMchtInTxnAmt) {
		this.twoMchtInTxnAmt = twoMchtInTxnAmt;
	}

	public BigDecimal getOneToTwoInAcctAmt() {
		return OneToTwoInAcctAmt;
	}

	public void setOneToTwoInAcctAmt(BigDecimal oneToTwoInAcctAmt) {
		OneToTwoInAcctAmt = oneToTwoInAcctAmt;
	}

	public BigDecimal getPlatMchtOutCommissionAmt() {
		return platMchtOutCommissionAmt;
	}

	public void setPlatMchtOutCommissionAmt(BigDecimal platMchtOutCommissionAmt) {
		this.platMchtOutCommissionAmt = platMchtOutCommissionAmt;
	}

	public BigDecimal getOneMchtOutCommissionAmt() {
		return oneMchtOutCommissionAmt;
	}

	public void setOneMchtOutCommissionAmt(BigDecimal oneMchtOutCommissionAmt) {
		this.oneMchtOutCommissionAmt = oneMchtOutCommissionAmt;
	}

	public BigDecimal getOneMchtInTxnAmt() {
		return oneMchtInTxnAmt;
	}

	public void setOneMchtInTxnAmt(BigDecimal oneMchtInTxnAmt) {
		this.oneMchtInTxnAmt = oneMchtInTxnAmt;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public String getIncctime() {
		return incctime;
	}

	public void setIncctime(String incctime) {
		this.incctime = incctime;
	}



	

}
