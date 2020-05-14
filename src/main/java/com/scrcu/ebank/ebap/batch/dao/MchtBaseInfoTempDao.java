package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfoTemp;

public interface MchtBaseInfoTempDao 
{
	List<MchtBaseInfoTemp> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    MchtBaseInfoTemp selectOne(String statement,Map<String,Object> parameter);
}
