package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchJobExecution;
import com.scrcu.ebank.ebap.batch.bean.dto.BthVatInfo;

import java.util.List;
import java.util.Map;

public interface BthBatchJobExecutionDao {

	int insertSelective(BthBatchJobExecution batchJobExecution);

	int updateByPrimaryKeySelective(BthBatchJobExecution record);
	
	int update(String statement, BthBatchJobExecution batchJobExecution);

    List<BthBatchJobExecution> selectList(String statement, Map<String, Object> parameter);
    
    int insert(BthBatchJobExecution batchJobExecution);

    int insert(String statement, Map<String, Object> parameter);

    void deleteByPrimaryKey(String id);
    
    int delete(String statement, Map<String, Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    BthBatchJobExecution selectOne(String statement, Map<String, Object> parameter);
    
    int update(String statement, Map<String, Object> parameter);
    
}
