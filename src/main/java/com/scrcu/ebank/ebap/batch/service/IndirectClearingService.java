package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface IndirectClearingService 
{
    /**
     * 间接结算订单清分
     * @param request
     * @return
     */
    CommonResponse indirectStlClearing(BatchRequest request) throws Exception;
   
}
