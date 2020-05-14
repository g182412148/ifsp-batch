package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * @ClassName UnionChkHandleService
 * @Description 银联对账文件处理(冲正/冲正撤销/被冲正交易)
 * @Author NiklausZhu
 * @Date 2019/12/3 10:53
 **/
public interface UnionChkHandleService {

    public CommonResponse unionChkHandle(BatchRequest request) throws Exception;
}
