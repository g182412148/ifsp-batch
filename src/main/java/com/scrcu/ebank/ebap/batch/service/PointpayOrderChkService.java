package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface PointpayOrderChkService 
{
	/**
     * 积分支付对账
     * @param request
     * @return
     */
    CommonResponse pointpayOrderChk(BatchRequest request) throws Exception;
}
