package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.SynchronizeOrgInfo;

public interface SynchronizeOrgInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(SynchronizeOrgInfo record);

    int insertSelective(SynchronizeOrgInfo record);

    SynchronizeOrgInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SynchronizeOrgInfo record);

    int updateByPrimaryKey(SynchronizeOrgInfo record);
}