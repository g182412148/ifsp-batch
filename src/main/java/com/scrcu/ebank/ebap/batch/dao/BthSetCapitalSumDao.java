package com.scrcu.ebank.ebap.batch.dao;


import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalSum;

import java.util.List;
import java.util.Map;

public interface BthSetCapitalSumDao
{
    public int clear();

    public List<BthSetCapitalSum> selectList(String statement, Map<String, Object> parameter);

    public int initData(String batchNo);
}
