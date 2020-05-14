package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.SynchronizeStaffInfo;

public interface SynchronizeStaffInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(SynchronizeStaffInfo record);

    int insertSelective(SynchronizeStaffInfo record);

    SynchronizeStaffInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SynchronizeStaffInfo record);

    int updateByPrimaryKey(SynchronizeStaffInfo record);
}