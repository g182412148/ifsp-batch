package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

import lombok.Data;

@Data
public class BatchRequest  extends CommonRequest {

	private static final long serialVersionUID = 12L;

	private String settleDate;

    private String pagyNo;

    @Override
    public void valid() throws IfspValidException {

    }
}
