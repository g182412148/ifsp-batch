package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerTxnFee;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalDetail;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface BthMerTxnFeeDao
{
    public int clear();

    public List<BthMerTxnFee> selectList(String statement, Map<String, Object> parameter);

    public int initData(String batchNo);
}
