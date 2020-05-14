package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaffOrgRel;

import java.util.List;

public interface IfsStaffOrgRelDao {

	int insert(IfsStaffOrgRel ifsStaffOrgRel);

	int update(IfsStaffOrgRel ifsStaffOrgRel);

	IfsStaffOrgRel selectById(String string, String string2);

	List<IfsStaffOrgRel> selectByTlrId(String tlrId);

	int delete(String tlrId, String brId);

}
