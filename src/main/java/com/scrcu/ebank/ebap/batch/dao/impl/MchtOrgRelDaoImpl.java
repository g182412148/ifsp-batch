package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtOrgRel;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtOrgRelMapper;
import com.scrcu.ebank.ebap.batch.dao.MchtOrgRelDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class MchtOrgRelDaoImpl extends BaseBatisDao implements MchtOrgRelDao {
    private final Class<MchtOrgRelMapper> mchtOrgRelMap = MchtOrgRelMapper.class;


    @Override
    public MchtOrgRel selectByMchtIdType(String mchtId, String orgType) {
        return getSqlSession().getMapper(mchtOrgRelMap).selectByMchtIdType(mchtId,orgType);
    }

	@Override
	public List<MchtOrgRel> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtOrgRel selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public List<String> selectMchtNoList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}
}
