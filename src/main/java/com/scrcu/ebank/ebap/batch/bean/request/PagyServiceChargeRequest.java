package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

public class PagyServiceChargeRequest extends CommonRequest{
	
	@NotEmpty(message = "通道系统编号不能为空")
	private String pagySysNo;//PAGY_SYS_NO
	@NotEmpty(message = "清算日期不能为空")
	private String settleDate;
	
	
	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getPagySysNo() {
		return pagySysNo;
	}

	public void setPagySysNo(String pagySysNo) {
		this.pagySysNo = pagySysNo;
	}

	@Override
	public void valid() throws IfspValidException {
	}

}
