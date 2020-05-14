package com.scrcu.ebank.ebap.batch.bean.response;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.MonthlyBill;

/**
 * 名称：〈月账单明细列表Response〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月20日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
public class QueryMonthBillResponse extends CommRegiResponse {
	
	private List<MonthlyBill> monthlyBill;
	
	private int totalTxnCount;
	
	private String totalTxnAmt;

	public List<MonthlyBill> getMonthlyBill() {
		return monthlyBill;
	}

	public void setMonthlyBill(List<MonthlyBill> monthlyBill) {
		this.monthlyBill = monthlyBill;
	}

	public int getTotalTxnCount() {
		return totalTxnCount;
	}

	public void setTotalTxnCount(int totalTxnCount) {
		this.totalTxnCount = totalTxnCount;
	}

	public String getTotalTxnAmt() {
		return totalTxnAmt;
	}

	public void setTotalTxnAmt(String totalTxnAmt) {
		this.totalTxnAmt = totalTxnAmt;
	}

	
	
	
}
