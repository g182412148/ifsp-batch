package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.SettleAccModTask;

import java.util.List;
import java.util.Map;

public interface SettleAccModTaskDao {

	int insertSelective(SettleAccModTask record);

	int updateByPrimaryKeySelective(SettleAccModTask record);
	
	int update(String statement, SettleAccModTask record);

    List<SettleAccModTask> selectList(String statement, Map<String, Object> parameter);
    
    int insert(SettleAccModTask record);

    void deleteByPrimaryKey(String id);
    
    int delete(String statement, Map<String, Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    SettleAccModTask selectOne(String statement, Map<String, Object> parameter);
    
    int update(String statement, Map<String, Object> parameter);
    
}
