package com.scrcu.ebank.ebap.batch.bean.dto;

public class BthMerInAccVo extends BthMerInAcc{

	private String entryType;//流水分录类型
	
	private String tranAmount;//交易金额
	
	private String refundAmt;
	
	private String totalDiscountAmt;//营销补贴金额（商户出资）

	public String getEntryType() {
		return entryType;
	}

	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}

	public String getTranAmount() {
		return tranAmount;
	}

	public void setTranAmount(String tranAmount) {
		this.tranAmount = tranAmount;
	}

	public String getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(String refundAmt) {
		this.refundAmt = refundAmt;
	}

	public String getTotalDiscountAmt() {
		return totalDiscountAmt;
	}

	public void setTotalDiscountAmt(String totalDiscountAmt) {
		this.totalDiscountAmt = totalDiscountAmt;
	}
	
}
