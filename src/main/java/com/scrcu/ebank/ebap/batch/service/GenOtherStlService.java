package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.request.GenerateStlFileRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.util.List;

public interface GenOtherStlService {

    /**
     * 他行入账
     * @param request
     * @return
     */
    CommonResponse otherSel(GenerateStlFileRequest request);

    /**
     * 他行入账结果更新
     * @param request
     * @return
     */
    CommonResponse otherSelUpdateState(GenerateStlFileRequest request);
}
