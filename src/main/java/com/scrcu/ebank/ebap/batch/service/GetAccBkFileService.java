package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface GetAccBkFileService {

    /**
     * 处理入账文件表中调用
     * @param fileType : 
     * @return
     */
    CommonResponse getAccBkFile(String fileType);
}
