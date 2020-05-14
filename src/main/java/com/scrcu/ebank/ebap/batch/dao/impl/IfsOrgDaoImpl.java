package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.SynchronizeOrgInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.SynchronizeOrgInfoMapper;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsOrg;
import com.scrcu.ebank.ebap.batch.bean.mapper.IfsOrgMapper;
import com.scrcu.ebank.ebap.batch.dao.IfsOrgDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("IfsOrgDao")
public class IfsOrgDaoImpl extends BaseBatisDao implements IfsOrgDao {
	private final Class<IfsOrgMapper> ifsOrgMapper=IfsOrgMapper.class;
	private final Class<SynchronizeOrgInfoMapper> synchronizeOrgInfoMapper=SynchronizeOrgInfoMapper.class;
	
	@Override
	public int insert(IfsOrg ifsOrg) {
		return super.getSqlSession().getMapper(ifsOrgMapper).insertSelective(ifsOrg);
	}

	@Override
	public int update(IfsOrg ifsOrg) {
		return super.getSqlSession().getMapper(ifsOrgMapper).updateByPrimaryKeySelective(ifsOrg);
	}

    @Override
    public IfsOrg selectByPrimaryKey(String orgId) {
        return super.getSqlSession().getMapper(ifsOrgMapper).selectByPrimaryKey(orgId);
    }

	@Override
	public List<IfsOrg> selectAll() {
		return super.getSqlSession().getMapper(ifsOrgMapper).selectAll();
	}

	@Override
	public List<IfsOrg> selectCorpById(String brId) {
		return super.getSqlSession().getMapper(ifsOrgMapper).selectCorpById(brId);
	}

	@Override
	public int insert(SynchronizeOrgInfo synchronizeOrgInfo) {
		return super.getSqlSession().getMapper(synchronizeOrgInfoMapper).insertSelective(synchronizeOrgInfo);
	}

	@Override
	public List<String> selectBrAreaCode(String areaCode) {
		return super.getSqlSession().getMapper(ifsOrgMapper).selectBrAreaCode(areaCode);
	}

}
