package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.CoreBkFileChkRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * @author: ljy
 * @create: 2018-08-24 16:48
 */
public interface CoreAccBkFileChkService {
    /**
     * 检查核心记账反馈文件
     * @param request
     * @return
     */
    CommonResponse coreAccBkFileChk(CoreBkFileChkRequest request);

}
