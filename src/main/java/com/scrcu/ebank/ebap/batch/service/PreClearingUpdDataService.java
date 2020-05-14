package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 清分数据抽取后状态和数据更新
 * Created by Administrator on 2019-06-12.
 */
public interface PreClearingUpdDataService {
    CommonResponse prepareUpd(BatchRequest request) throws Exception ;
}
