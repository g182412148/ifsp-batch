package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;

public interface UnionAllChnlContrastService {
    AcctContrastResponse unionAllChnlBillContrast(AcctContrastRequest request) throws Exception;
}
