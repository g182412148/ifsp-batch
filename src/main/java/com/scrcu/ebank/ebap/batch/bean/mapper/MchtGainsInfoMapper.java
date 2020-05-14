package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtGainsInfo;

public interface MchtGainsInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(MchtGainsInfo record);

    int insertSelective(MchtGainsInfo record);

    MchtGainsInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MchtGainsInfo record);

    int updateByPrimaryKey(MchtGainsInfo record);
}