package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface AccFailDataClearingService 
{
    /**
     * 记账失败数据处理
     * @param request
     * @return
     */
    CommonResponse handleAccFailData(BatchRequest request) throws Exception;
    
}
