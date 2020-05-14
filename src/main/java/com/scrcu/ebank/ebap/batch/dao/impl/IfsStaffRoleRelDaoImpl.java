package com.scrcu.ebank.ebap.batch.dao.impl;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaffRoleRel;
import com.scrcu.ebank.ebap.batch.bean.mapper.IfsStaffRoleRelMapper;
import com.scrcu.ebank.ebap.batch.dao.IfsStaffRoleRelDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

import java.util.List;

/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("IfsStaffRoleRelDao")
public class IfsStaffRoleRelDaoImpl extends BaseBatisDao implements IfsStaffRoleRelDao {
	private final Class<IfsStaffRoleRelMapper> ifsStaffRoleRelMapper=IfsStaffRoleRelMapper.class;
	
	@Override
	public int insert(IfsStaffRoleRel ifsStaffRoleRel) {
		return super.getSqlSession().getMapper(ifsStaffRoleRelMapper).insert(ifsStaffRoleRel);
	}

	@Override
	public int update(IfsStaffRoleRel ifsStaffRoleRel) {
		return super.getSqlSession().getMapper(ifsStaffRoleRelMapper).updateByPrimaryKeySelective(ifsStaffRoleRel);
	}

	@Override
	public IfsStaffRoleRel selectById(String brId, String tlrId) {
		return super.getSqlSession().getMapper(ifsStaffRoleRelMapper).selectById(brId,tlrId);
	}

	@Override
	public List<IfsStaffRoleRel> selectByTlrId(String tlrId) {
		return super.getSqlSession().getMapper(ifsStaffRoleRelMapper).selectByTlrId(tlrId);
	}

	@Override
	public int delete(String tlrId, String roleId, String brId) {
		return super.getSqlSession().getMapper(ifsStaffRoleRelMapper).deleteByPrimaryKey(tlrId,roleId,brId);
	}

}
