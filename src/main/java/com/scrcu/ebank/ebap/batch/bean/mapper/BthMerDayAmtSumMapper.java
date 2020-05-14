package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDayAmtSum;

import java.util.List;
import java.util.Map;

public interface BthMerDayAmtSumMapper {
    int deleteByPrimaryKey(String id);

    int insert(BthMerDayAmtSum record);

    int insertSelective(BthMerDayAmtSum record);

    BthMerDayAmtSum selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BthMerDayAmtSum record);

    int updateByPrimaryKey(BthMerDayAmtSum record);

    List<BthMerDayAmtSum> selectByTmAmt(Map<String,Object> map);
}