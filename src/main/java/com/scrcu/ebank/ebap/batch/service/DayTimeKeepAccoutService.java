package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.DayTimeKeepAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OnceKeepAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.response.KeepAccResponse;

public interface DayTimeKeepAccoutService {
    KeepAccResponse daytimeKeepAccount(DayTimeKeepAcctRequest request);

    KeepAccResponse onceKeepAccount(OnceKeepAcctRequest request);
}
