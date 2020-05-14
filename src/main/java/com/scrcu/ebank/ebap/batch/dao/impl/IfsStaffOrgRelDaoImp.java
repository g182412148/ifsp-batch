package com.scrcu.ebank.ebap.batch.dao.impl;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaffOrgRel;
import com.scrcu.ebank.ebap.batch.bean.mapper.IfsStaffOrgRelMapper;
import com.scrcu.ebank.ebap.batch.dao.IfsStaffOrgRelDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

import java.util.List;

/**
 *名称：<> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("IfsStaffOrgRelDao")
public class IfsStaffOrgRelDaoImp extends BaseBatisDao implements IfsStaffOrgRelDao {
	private final Class<IfsStaffOrgRelMapper> ifsStaffOrgRelMapper=IfsStaffOrgRelMapper.class;
	
	@Override
	public int insert(IfsStaffOrgRel ifsStaffOrgRel) {
		return super.getSqlSession().getMapper(ifsStaffOrgRelMapper).insert(ifsStaffOrgRel);
	}

	@Override
	public int update(IfsStaffOrgRel ifsStaffOrgRel) {
		return super.getSqlSession().getMapper(ifsStaffOrgRelMapper).updateByPrimaryKeySelective(ifsStaffOrgRel);
	}

	@Override
	public IfsStaffOrgRel selectById(String brId, String tlrId) {
		return super.getSqlSession().getMapper(ifsStaffOrgRelMapper).selectById(brId,tlrId);
	}

	@Override
	public List<IfsStaffOrgRel> selectByTlrId(String tlrId) {
		return super.getSqlSession().getMapper(ifsStaffOrgRelMapper).selectByTlrId(tlrId);
	}

	@Override
	public int delete(String tlrId, String brId) {
		return super.getSqlSession().getMapper(ifsStaffOrgRelMapper).deleteByPrimaryKey(tlrId,brId);
	}

}
