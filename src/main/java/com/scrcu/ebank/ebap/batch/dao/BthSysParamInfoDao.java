package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthSysParamInfo;

public interface BthSysParamInfoDao {
    BthSysParamInfo selectByParamCode(String paramCode);

    void updateInfo(BthSysParamInfo s);
}
