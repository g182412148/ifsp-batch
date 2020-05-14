package com.scrcu.ebank.ebap.batch.service;/**
 * Created by Administrator on 2019-04-28.
 */

import com.scrcu.ebank.ebap.batch.bean.request.GetFileNameRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetSerApprFileNameRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OfflineCreatMerChkFileRequest;
import com.scrcu.ebank.ebap.batch.bean.response.GetFileNameResponse;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 名称：〈生成线下商户对账文件〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-04-28 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
public interface GenOfflineMerChkFileService {
    /**
     * 线下商户生成商户对账文件
     */
    public CommonResponse genOffLineMerChkFile(OfflineCreatMerChkFileRequest request);

    public GetFileNameResponse qryFileName(GetFileNameRequest request);

    GetFileNameResponse qrySerMerApprFileName(GetSerApprFileNameRequest request);
}
