package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;

/**
 * Created by Administrator on 2019-05-20.
 */
public interface CoreAcctContrastNewService {

    AcctContrastResponse keepAccCoreContrastNew(AcctContrastRequest request) throws Exception;
}
