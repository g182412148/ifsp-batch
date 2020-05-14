package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.QueryTimeOutQueryRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface OrderTimeOutQueryService {
    CommonResponse queryTimeOutOrder(QueryTimeOutQueryRequest request);
}
