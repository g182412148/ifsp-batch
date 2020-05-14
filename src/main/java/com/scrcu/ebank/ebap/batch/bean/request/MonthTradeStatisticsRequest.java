package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.exception.IfspValidException;

public class MonthTradeStatisticsRequest extends CommRegiRequest{
	
	@NotEmpty(message = "用户号不能为空")
	private String userId;//mchtId
	@NotEmpty(message = "开始日期不能为空")
	private String startDate;
	@NotEmpty(message = "结束日期不能为空")
	private String endDate;
	
	// 页码
	private String pageNo;
	// 条数
	private String pageSize;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
	

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public void valid() throws IfspValidException {
	}

}
