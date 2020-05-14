package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

public class QueryMonthBillRequest extends CommRegiRequest{
	
	@NotEmpty(message = "商户号不能为空")
	private String mchtId;//mchtId
//	@NotEmpty(message = "日期不能为空")
//	private String month;
	
	@NotEmpty(message = "开始日期不能为空")
	private String startDate;
	
	@NotEmpty(message = "结束日期不能为空")
	private String endDate;

    /**
     * 交易标志 , 不为空就查询正交易
     */
	private String txnFlag;

	/**
	 * 收款统计标志，不为空则以交易时间统计
	 */
	private String isStatistics;

    public String getTxnFlag() {
        return txnFlag;
    }

    public void setTxnFlag(String txnFlag) {
        this.txnFlag = txnFlag;
    }

    @Override
    public String getMchtId() {
		return mchtId;
	}

	@Override
    public void setMchtId(String mchtId) {
		this.mchtId = mchtId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIsStatistics() {
		return isStatistics;
	}

	public void setIsStatistics(String isStatistics) {
		this.isStatistics = isStatistics;
	}
}
