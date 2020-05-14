package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * Created by Administrator on 2019-07-09.
 */
public interface OrderClearing {
    public CommonResponse channelClearing(BatchRequest request) throws Exception;
}
