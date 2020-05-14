package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerTxnFee;

public interface BthMerTxnFeeMapper {
    int insert(BthMerTxnFee record);

    int insertSelective(BthMerTxnFee record);

    int clear();

    int initData(String batchNo);
}