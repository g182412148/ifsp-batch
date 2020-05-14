package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface LoanpayOrderChkService 
{
	/**
     * 授信支付对账
     * @param request
     * @return
     */
    CommonResponse loanpayOrderChk(BatchRequest request) throws Exception;
}
