package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtGainsInfo;
import com.scrcu.ebank.ebap.batch.dao.MchtGainsInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

@Repository
public class MchtGainsInfoDaoImpl extends BaseBatisDao implements MchtGainsInfoDao {

	@Override
	public List<MchtGainsInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement,parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtGainsInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

}
