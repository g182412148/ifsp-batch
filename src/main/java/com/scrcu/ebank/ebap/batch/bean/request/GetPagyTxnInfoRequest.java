package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

import java.util.List;

public class GetPagyTxnInfoRequest extends CommonRequest{
	
	@NotEmpty(message = "清算日期不能为空")
	private String settleDate;

	private List<String> orderSsn ;

    public List<String> getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(List<String> orderSsn) {
        this.orderSsn = orderSsn;
    }

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
