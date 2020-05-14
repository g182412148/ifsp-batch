package com.scrcu.ebank.ebap.batch.bean.response;

import java.math.BigDecimal;
import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.MonthlyStatis;

/**
 * 名称：〈月账单明细列表Response〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月20日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
public class MonthTradeStatisticsResponse extends CommRegiResponse {
	
	private BigDecimal totalTxnAmt;//总交易金额
	
	private BigDecimal totalInAcctAmt;//总收款金额(总交易金额减去营销金额(商户出资))
	
	private int totalCnt;
	
	private List<MonthlyStatis> monthlyStatis;

	public BigDecimal getTotalTxnAmt() {
		return totalTxnAmt;
	}

	public void setTotalTxnAmt(BigDecimal totalTxnAmt) {
		this.totalTxnAmt = totalTxnAmt;
	}

	public BigDecimal getTotalInAcctAmt() {
		return totalInAcctAmt;
	}

	public void setTotalInAcctAmt(BigDecimal totalInAcctAmt) {
		this.totalInAcctAmt = totalInAcctAmt;
	}

	public int getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}

	public List<MonthlyStatis> getMonthlyStatis() {
		return monthlyStatis;
	}

	public void setMonthlyStatis(List<MonthlyStatis> monthlyStatis) {
		this.monthlyStatis = monthlyStatis;
	}


	
	
}
