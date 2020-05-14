package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.request.KeepAccRevRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OnceRevKeepAccRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BthKeepAccResponse;

import java.util.List;

public interface BthKeepAcctReverseService {
    BthKeepAccResponse bthKeepAccountReverse(KeepAccRevRequest request);

    void saveKeepAccInfo(List<KeepAccInfo> keepAccInfoList);

    BthKeepAccResponse onceKeepAccountReverse(OnceRevKeepAccRequest request);

    void updateData(OnceRevKeepAccRequest request, String respCode, String respMsg);
}
