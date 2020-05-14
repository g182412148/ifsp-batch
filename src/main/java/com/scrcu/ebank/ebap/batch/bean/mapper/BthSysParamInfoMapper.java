package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthSysParamInfo;

public interface BthSysParamInfoMapper {
    int deleteByPrimaryKey(String papamCode);

    int insert(BthSysParamInfo record);

    int insertSelective(BthSysParamInfo record);

    BthSysParamInfo selectByPrimaryKey(String papamCode);

    int updateByPrimaryKeySelective(BthSysParamInfo record);

    int updateByPrimaryKey(BthSysParamInfo record);
}