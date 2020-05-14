package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface InvokeCH1730Service {

    /**
     * 调用核心服务CH1730进行记账
     * @param request
     * @return
     */
    CommonResponse invokeCH1730(String fileType);
}
