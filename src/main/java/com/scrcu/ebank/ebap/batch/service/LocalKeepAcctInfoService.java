package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.LocalKeepTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.request.LocalTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.response.LocalTxnInfoResponse;

/**
 * Created by Administrator on 2019-05-20.
 */
public interface LocalKeepAcctInfoService {

    /**
     * 记账表本地流水抽取
     * @param request
     * @return
     * @throws Exception
     */
    LocalTxnInfoResponse getKeepAccInfo(LocalKeepTxnInfoRequest request) throws Exception;

}
