package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.exception.IfspValidException;
import com.scrcu.ebank.ebap.msg.common.request.ScrcuCommonRequest;

/**
 * 每周自然日交易金额走势request
 * @author lenovo
 *
 */
public class WeekCountQueryRequest extends ScrcuCommonRequest{
	
	@NotEmpty(message = "商户号不能为空")
	private String mchtId;
	@NotEmpty(message = "开始日期不能为空")
	private String startDate;
	@NotEmpty(message = "结束日期不能为空")
	private String endDate;
	
	private String wookDate;

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

	public String getMchtId() {
		return mchtId;
	}

	public void setMchtId(String mchtId) {
		this.mchtId = mchtId;
	}

	public String getWookDate() {
		return wookDate;
	}

	public void setWookDate(String wookDate) {
		this.wookDate = wookDate;
	}

	@Override
	public void valid() throws IfspValidException {
	}

}
