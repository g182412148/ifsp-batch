package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfoTemp;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoTempDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

@Repository
public class MchtBaseInfoTempDaoImpl extends BaseBatisDao implements MchtBaseInfoTempDao
{

	@Override
	public List<MchtBaseInfoTemp> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtBaseInfoTemp selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}
}
