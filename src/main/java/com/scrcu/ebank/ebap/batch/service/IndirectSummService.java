package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface IndirectSummService 
{
    /**
     * 间接结算订单汇总
     * @param request
     * @return
     */
    CommonResponse indirectOrderSumm(BatchRequest request) throws Exception;
   
}
