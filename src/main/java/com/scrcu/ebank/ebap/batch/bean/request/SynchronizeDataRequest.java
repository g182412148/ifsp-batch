package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

public class SynchronizeDataRequest extends CommonRequest{
	
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
