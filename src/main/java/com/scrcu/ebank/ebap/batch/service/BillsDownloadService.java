package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.GetOrderInfoRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * @author ljy
 * @date 2019-05-10
 */
public interface BillsDownloadService {

    /**
     * 文件下载
     * @param request
     * @return
     */
    CommonResponse billDownload(GetOrderInfoRequest request);
}
