package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthVatInfo;

public interface BthVatInfoDao {

	int insertSelective(BthVatInfo vatInfo);

	int updateByPrimaryKeySelective(BthVatInfo record);
	
	int update(String statement,BthVatInfo vatInfo);

    List<BthVatInfo> selectList(String statement,Map<String,Object> parameter);
    
    int insert(BthVatInfo vatInfo);

    void deleteByPrimaryKey(String id);
    
    int delete(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    BthVatInfo selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);

    public int insertBatch(List<BthVatInfo> recordList);
}
