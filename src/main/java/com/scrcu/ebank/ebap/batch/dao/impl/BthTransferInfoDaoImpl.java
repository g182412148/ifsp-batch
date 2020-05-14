package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthTransferInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthTransferInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.BthTransferInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;


@Repository("bthTransferInfoDao")
public class BthTransferInfoDaoImpl extends BaseBatisDao implements BthTransferInfoDao {

	 
	private final Class<BthTransferInfoMapper> bthTransferInfoMapper=BthTransferInfoMapper.class;
	 
	@Override
	public void insertSelective(BthTransferInfo transferInfo) throws Exception {
		 getSqlSession().getMapper(bthTransferInfoMapper).insertSelective(transferInfo);
	}

	@Override
	public int updateByPrimaryKeySelective(BthTransferInfo transferInfo) {
		 return super.getSqlSession().getMapper(bthTransferInfoMapper).updateByPrimaryKeySelective(transferInfo);
	}

	@Override
	public List<BthTransferInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public BthTransferInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

}
