package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.request.GenerateStlFileRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.util.List;

public interface GenStlFileGrpService {

    /**
     * 生成入账文件
     * @param request
     * @return
     */
    CommonResponse generateStlFileGrp(GenerateStlFileRequest request);

    /**
     * 更新入账汇总表状态, 再将文件信息插入批量控制文件表
     * @param list
     * @param f
     */
    void updStorage(List<BthMerInAcc> list, BthBatchAccountFile f);

}
