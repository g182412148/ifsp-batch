package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetPagyTxnInfoRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.util.concurrent.ExecutionException;


/**
 * @author ljy
 */
public interface GenTestDataService {
    CommonResponse genWxTestData(GetPagyTxnInfoRequest request);

    CommonResponse genAliTestData(GetPagyTxnInfoRequest request);

    CommonResponse genUnionQrcTestData(GetPagyTxnInfoRequest request);

    CommonResponse genUnionAllChnlTestData(GetPagyTxnInfoRequest request);

    CommonResponse genSxkTestData(GetPagyTxnInfoRequest request) throws InterruptedException;

    CommonResponse genSxeTestData(GetPagyTxnInfoRequest request) throws ExecutionException, InterruptedException;

    CommonResponse genLoanpayTestData(GetPagyTxnInfoRequest request);

    CommonResponse genPointpayTestData(GetPagyTxnInfoRequest request);
}
