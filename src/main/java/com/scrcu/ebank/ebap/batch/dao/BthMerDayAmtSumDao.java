package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDayAmtSum;

import java.util.List;
import java.util.Map;

public interface BthMerDayAmtSumDao {
    List<BthMerDayAmtSum> selectByTmAmt(Map<String,Object> map);

    int insert(BthMerDayAmtSum value);
}
