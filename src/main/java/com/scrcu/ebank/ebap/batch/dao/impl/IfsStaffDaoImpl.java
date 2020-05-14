package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.SynchronizeStaffInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.SynchronizeStaffInfoMapper;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaff;
import com.scrcu.ebank.ebap.batch.bean.mapper.IfsStaffMapper;
import com.scrcu.ebank.ebap.batch.dao.IfsStaffDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("IfsStaffDao")
public class IfsStaffDaoImpl extends BaseBatisDao implements IfsStaffDao {
	private final Class<IfsStaffMapper> ifsStaffMapper=IfsStaffMapper.class;
	private final Class<SynchronizeStaffInfoMapper> synchronizeStaffInfoMapper=SynchronizeStaffInfoMapper.class;
	
	@Override
	public int insertSelective(IfsStaff ifsStaff) {
		return super.getSqlSession().getMapper(ifsStaffMapper).insertSelective(ifsStaff);
	}

	@Override
	public int updateByPrimaryKeySelective(IfsStaff ifsStaff) {
		return super.getSqlSession().getMapper(ifsStaffMapper).updateByPrimaryKeySelective(ifsStaff);
	}

	@Override
	public IfsStaff selectByTlrId(String tlrId) {
		return super.getSqlSession().getMapper(ifsStaffMapper).selectByPrimaryKey(tlrId);
	}

	@Override
	public int insert(SynchronizeStaffInfo synchronizeStaffInfo) {
		return super.getSqlSession().getMapper(synchronizeStaffInfoMapper).insertSelective(synchronizeStaffInfo);
	}

}
