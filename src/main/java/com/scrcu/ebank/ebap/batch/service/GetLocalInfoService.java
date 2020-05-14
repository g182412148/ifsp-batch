package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.GetOrderInfoRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 抽取通道本地流水服务
 * @author ljy
 * @date 2019-05-10
 */
public interface GetLocalInfoService {

    /**
     * 通道抽数
     * @param request
     * @return
     */
    CommonResponse getLocalTxnInfo(GetOrderInfoRequest request);

}
