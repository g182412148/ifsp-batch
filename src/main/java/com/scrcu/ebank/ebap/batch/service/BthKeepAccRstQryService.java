package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.KeepAccQryRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BthKeepAccResponse;

public interface BthKeepAccRstQryService {
    BthKeepAccResponse bthKeepAccountRstQry(KeepAccQryRequest request);
}
