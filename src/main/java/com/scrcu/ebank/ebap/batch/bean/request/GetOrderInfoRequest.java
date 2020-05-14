package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

public class GetOrderInfoRequest extends CommonRequest{
	
	@NotEmpty(message = "清算日期不能为空")
	private String settleDate;
	
	
	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	@Override
	public void valid() throws IfspValidException {
	}

}
