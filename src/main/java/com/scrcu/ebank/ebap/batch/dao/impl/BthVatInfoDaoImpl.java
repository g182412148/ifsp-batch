package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthVatInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthVatInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.BthVatInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BthVatInfoDaoImpl extends BaseBatisDao implements BthVatInfoDao{

    private final Class<BthVatInfoMapper> bthVatInfoMapper=BthVatInfoMapper.class;
    @Override
    public int insertSelective(BthVatInfo dtl)  
    {
        return getSqlSession().getMapper(bthVatInfoMapper).insertSelective(dtl);
    }
	@Override
	public int updateByPrimaryKeySelective(BthVatInfo record) {
		return 0;
	}
	@Override
	public int update(String statement, BthVatInfo vatInfo) {
		return 0;
	}
	@Override
	public List<BthVatInfo> selectList(String statement, Map<String, Object> parameter) {
		 return getSqlSession().selectList(statement, parameter);
	}
	@Override
	public int insert(BthVatInfo record) {
		 return getSqlSession().getMapper(bthVatInfoMapper).insert(record);
	}


	@Override
	public void deleteByPrimaryKey(String id) {
		getSqlSession().getMapper(bthVatInfoMapper).deleteByPrimaryKey(id);
		
	}
	@Override
	public int delete(String statement, Map<String, Object> parameter) {
		return getSqlSession().delete(statement, parameter);
	}
	
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}
	
	@Override
	public BthVatInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}
	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public int insertBatch(List<BthVatInfo> recordList) {
		return getSqlSession().getMapper(bthVatInfoMapper).insertBatch(recordList);
	}
}
