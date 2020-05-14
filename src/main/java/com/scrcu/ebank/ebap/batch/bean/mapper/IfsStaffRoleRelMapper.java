package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaffRoleRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IfsStaffRoleRelMapper {
    int deleteByPrimaryKey(@Param("tlrId") String tlrId, @Param("roleId") String roleId, @Param("brId") String brId);

    int insert(IfsStaffRoleRel record);

    int insertSelective(IfsStaffRoleRel record);

	int updateSelective(IfsStaffRoleRel ifsStaffRoleRel);

	IfsStaffRoleRel selectById(@Param("brId")String brId, @Param("tlrId")String tlrId);

    List<IfsStaffRoleRel> selectByTlrId(@Param("tlrId")String tlrId);

	int updateByPrimaryKeySelective(IfsStaffRoleRel ifsStaffRoleRel);
}