package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.AreaInfo;

public interface AreaInfoMapper {
    int deleteByPrimaryKey(String areaCode);

    int insert(AreaInfo record);

    int insertSelective(AreaInfo record);

    AreaInfo selectByPrimaryKey(String areaCode);

    int updateByPrimaryKeySelective(AreaInfo record);

    int updateByPrimaryKey(AreaInfo record);

}