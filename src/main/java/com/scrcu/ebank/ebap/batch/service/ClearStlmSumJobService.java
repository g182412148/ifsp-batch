package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalDetail;
import com.scrcu.ebank.ebap.batch.bean.request.ClearRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface ClearStlmSumJobService {

    /**
     * 清分汇总
     * @param clearRequest
     * @return
     */
    CommonResponse clearSumJob(ClearRequest clearRequest);


}
