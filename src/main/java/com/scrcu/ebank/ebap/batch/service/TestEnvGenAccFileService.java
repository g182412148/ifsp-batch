package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetPagyTxnInfoRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.util.concurrent.ExecutionException;


/**
 * @author ljy
 */
public interface TestEnvGenAccFileService {
    CommonResponse genWxFile(GetPagyTxnInfoRequest request);

    CommonResponse genAliFile(GetPagyTxnInfoRequest request);

    CommonResponse genUnionQrcFile(GetPagyTxnInfoRequest request);

    CommonResponse genUnionAllChnlFile(GetPagyTxnInfoRequest request);

    CommonResponse moveFile(GetPagyTxnInfoRequest request);

    CommonResponse testEnvMoveFilePerTm(GetPagyTxnInfoRequest request) throws InterruptedException;

    CommonResponse testEnvMoveInaccFile(GetPagyTxnInfoRequest request) throws ExecutionException, InterruptedException;

    CommonResponse prdEnvBthRstFile(BatchRequest request);

    CommonResponse prdSupplementAcc(GetPagyTxnInfoRequest request);
}
