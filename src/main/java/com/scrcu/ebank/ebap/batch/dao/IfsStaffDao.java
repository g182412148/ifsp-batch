package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaff;
import com.scrcu.ebank.ebap.batch.bean.dto.SynchronizeStaffInfo;

public interface IfsStaffDao {

	int insertSelective(IfsStaff ifsStaff);

	int updateByPrimaryKeySelective(IfsStaff ifsStaff);

	IfsStaff selectByTlrId(String tlrId);

	int insert(SynchronizeStaffInfo synchronizeStaffInfo);
}
