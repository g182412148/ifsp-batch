package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;

public class WeekDay {

	private BigDecimal totalAmt;// 总交易金额

	private int totalCount;// 总笔数
	
	private String weekFlag;//工作日

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getWeekFlag() {
		return weekFlag;
	}

	public void setWeekFlag(String weekFlag) {
		this.weekFlag = weekFlag;
	}
	
}
