package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaffOrgRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IfsStaffOrgRelMapper {
    int deleteByPrimaryKey(@Param("tlrId") String tlrId, @Param("brId") String brId);

    int insert(IfsStaffOrgRel record);

    int insertSelective(IfsStaffOrgRel record);

	int updateSelective(IfsStaffOrgRel ifsStaffOrgRel);

	IfsStaffOrgRel selectById(@Param("brId")String brId, @Param("tlrId")String tlrId);

    List<IfsStaffOrgRel> selectByTlrId(@Param("tlrId")String tlrId);

	int updateByPrimaryKeySelective(IfsStaffOrgRel ifsStaffOrgRel);
}