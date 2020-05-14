package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.exception.IfspValidException;

/**
 * 分店交易日统计request
 * @author lenovo
 *
 */
public class DailyTradeStatisticsRequest extends CommRegiRequest{
	
	@NotEmpty(message = "开始日期不能为空")
	private String startDate;
	@NotEmpty(message = "结束日期不能为空")
	private String endDate;

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

	@Override
	public void valid() throws IfspValidException {
	}

}
