package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalSum;

public interface BthSetCapitalSumMapper {
    int insert(BthSetCapitalSum record);

    int insertSelective(BthSetCapitalSum record);

    int clear();

    int initData(String batchNo);
}