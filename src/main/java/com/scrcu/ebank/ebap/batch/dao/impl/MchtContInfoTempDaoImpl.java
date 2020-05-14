package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfoTemp;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoTempDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

@Repository
public class MchtContInfoTempDaoImpl extends BaseBatisDao implements MchtContInfoTempDao
{

	@Override
	public List<MchtContInfoTemp> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtContInfoTemp selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}
}
