package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.QueryMerInAccDtlRequest;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMerInAccRequest;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccDtlResponse;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccResponse;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface QueryMerInAccRstService {
    /**
     * 商户入账汇总查询
     * @param request
     * @return
     */
    QueryMerInAccResponse queryMerInAcc(QueryMerInAccRequest request);

    /**
     * 商户明细查询
     * @param request
     * @return
     */
    QueryMerInAccDtlResponse queryMerInAccDtl(QueryMerInAccDtlRequest request);
}
