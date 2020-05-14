package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.QueryMchtsMerInAccRequest;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccPosiResponse;

/**
 * @author: ljy
 * @create: 2018-08-25 12:14
 */
public interface QueryMerInAccPositiveService {

    /**
     * 门户商户入账查询
     * @param request
     * @return
     */
    QueryMerInAccPosiResponse queryMerInAccPositive(QueryMchtsMerInAccRequest request);
    QueryMerInAccPosiResponse queryMerInAccPositiveDown(QueryMchtsMerInAccRequest request);
}
