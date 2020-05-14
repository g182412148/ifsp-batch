package com.scrcu.ebank.ebap.batch.bean.dto;

public class BthMerInAccDtlVo extends BthMerInAccDtl {
	
	private String pagyProdId;
	
	private String pagyProdMn;
	
	private String orderTm;
	
	private String acctTypeId;
	
	private String txnTypeNo;
	
	private int totalTxnCount;
	
	private String totalTxnAmt;
	
	private String mchtCouponAmt;
	
	private String chlNo;
	
	private String acptChlNo;

	// 订单原交易金额
    private String orderTxnAmt;

    public String getOrderTxnAmt() {
        return orderTxnAmt;
    }

    public void setOrderTxnAmt(String orderTxnAmt) {
        this.orderTxnAmt = orderTxnAmt;
    }

    public String getPagyProdId() {
		return pagyProdId;
	}

	public void setPagyProdId(String pagyProdId) {
		this.pagyProdId = pagyProdId;
	}

	public String getPagyProdMn() {
		return pagyProdMn;
	}

	public void setPagyProdMn(String pagyProdMn) {
		this.pagyProdMn = pagyProdMn;
	}

	public String getOrderTm() {
		return orderTm;
	}

	public void setOrderTm(String orderTm) {
		this.orderTm = orderTm;
	}

	public String getAcctTypeId() {
		return acctTypeId;
	}

	public void setAcctTypeId(String acctTypeId) {
		this.acctTypeId = acctTypeId;
	}

	public String getTxnTypeNo() {
		return txnTypeNo;
	}

	public void setTxnTypeNo(String txnTypeNo) {
		this.txnTypeNo = txnTypeNo;
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

	public String getMchtCouponAmt() {
		return mchtCouponAmt;
	}

	public void setMchtCouponAmt(String mchtCouponAmt) {
		this.mchtCouponAmt = mchtCouponAmt;
	}

	public String getChlNo() {
		return chlNo;
	}

	public void setChlNo(String chlNo) {
		this.chlNo = chlNo;
	}

	public String getAcptChlNo() {
		return acptChlNo;
	}

	public void setAcptChlNo(String acptChlNo) {
		this.acptChlNo = acptChlNo;
	}
	
	
	
}