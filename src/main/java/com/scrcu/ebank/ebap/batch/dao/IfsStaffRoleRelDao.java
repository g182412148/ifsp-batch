package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaffRoleRel;

import java.util.List;

public interface IfsStaffRoleRelDao {

	int insert(IfsStaffRoleRel ifsStaffRoleRel);

	int update(IfsStaffRoleRel ifsStaffRoleRel);

	IfsStaffRoleRel selectById(String string, String string2);

	List<IfsStaffRoleRel> selectByTlrId(String tlrId);

	int delete(String tlrId, String roleId, String brId);

}
