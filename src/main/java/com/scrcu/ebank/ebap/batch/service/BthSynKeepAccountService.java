package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.request.NewDayTimeKeepAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BthKeepAccResponse;

import java.util.List;

public interface BthSynKeepAccountService {
    BthKeepAccResponse bthSynKeepAccount(NewDayTimeKeepAcctRequest request);

    List<KeepAccInfo> saveKeepAccInfo(NewDayTimeKeepAcctRequest request);
}
