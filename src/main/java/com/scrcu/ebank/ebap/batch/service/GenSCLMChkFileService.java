package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface GenSCLMChkFileService 
{
	/**
     * 生成授信支付放款文件
     * @param request
     * @return
     */
    CommonResponse genSCLMChkFile(BatchRequest request) throws Exception;
}
