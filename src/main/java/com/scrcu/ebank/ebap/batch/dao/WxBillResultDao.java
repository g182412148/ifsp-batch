package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillResult;

import java.util.Date;

public interface WxBillResultDao {
    int insert(WxBillResult record);

    int insertSelective(WxBillResult record);

    int insertWxResult(Date recoDate);

    int clear();

    int countLocal();

    int countOuter();
}
