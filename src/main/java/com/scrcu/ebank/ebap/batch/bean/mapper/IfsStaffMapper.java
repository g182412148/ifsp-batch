package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaff;

public interface IfsStaffMapper {
    int deleteByPrimaryKey(String tlrId);

    int insert(IfsStaff record);

    int insertSelective(IfsStaff record);

    IfsStaff selectByPrimaryKey(String tlrId);

    int updateByPrimaryKeySelective(IfsStaff record);

    int updateByPrimaryKey(IfsStaff record);
}