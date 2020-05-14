package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface AntiFraudService {

    /**
     * 调用反欺诈
     * @return
     */
    CommonResponse merInAccAntiFraud();

    /**
     * 上送反欺诈结果
     * @param request
     * @return
     */
    CommonResponse merInAccRstAntiFraud(BatchRequest request);
}
