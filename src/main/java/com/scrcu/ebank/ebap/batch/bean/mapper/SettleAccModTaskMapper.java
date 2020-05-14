package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.SettleAccModTask;

public interface SettleAccModTaskMapper {
    int deleteByPrimaryKey(String id);

    int insert(SettleAccModTask record);

    int insertSelective(SettleAccModTask record);

    SettleAccModTask selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SettleAccModTask record);

    int updateByPrimaryKey(SettleAccModTask record);
}