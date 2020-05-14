package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchJobExecution;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthBatchJobExecutionMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthVatInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.BthBatchJobExecutionDao;
import com.scrcu.ebank.ebap.batch.dao.BthVatInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class BthBatchJobExecutionDaoImpl extends BaseBatisDao implements BthBatchJobExecutionDao {

    private final Class<BthBatchJobExecutionMapper> bthBatchJobExecutionMapperMapper=BthBatchJobExecutionMapper.class;
    @Override
    public int insertSelective(BthBatchJobExecution dtl)  
    {
        return getSqlSession().getMapper(bthBatchJobExecutionMapperMapper).insertSelective(dtl);
    }
	@Override
	public int updateByPrimaryKeySelective(BthBatchJobExecution record) {
		return 0;
	}
	@Override
	public int update(String statement, BthBatchJobExecution vatInfo) {
		return 0;
	}
	@Override
	public List<BthBatchJobExecution> selectList(String statement, Map<String, Object> parameter) {
		 return getSqlSession().selectList(statement, parameter);
	}
	@Override
	public int insert(BthBatchJobExecution record) {
		 return getSqlSession().getMapper(bthBatchJobExecutionMapperMapper).insert(record);
	}
	
	@Override
	public void deleteByPrimaryKey(String id) {
		getSqlSession().getMapper(bthBatchJobExecutionMapperMapper).deleteByPrimaryKey(id);
		
	}
	@Override
	public int delete(String statement, Map<String, Object> parameter) {
		return getSqlSession().delete(statement, parameter);
	}
	
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}
	
	@Override
	public BthBatchJobExecution selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}
	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public int insert(String statement, Map<String, Object> parameter) {
		return getSqlSession().insert(statement,parameter);
	}
}
