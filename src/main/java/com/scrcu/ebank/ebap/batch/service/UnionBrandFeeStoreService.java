package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface UnionBrandFeeStoreService {
    CommonResponse unionBrandFeeStore(MerRegRequest request);
}
