package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface GenAccFileService {

    /**
     * 生成指定类型入账文件
     * @param request
     * @return
     */
    CommonResponse genAccFile(String accType);
}
