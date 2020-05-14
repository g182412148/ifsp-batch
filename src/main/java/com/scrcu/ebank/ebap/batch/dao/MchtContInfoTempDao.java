package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfoTemp;

public interface MchtContInfoTempDao 
{
	List<MchtContInfoTemp> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    MchtContInfoTemp selectOne(String statement,Map<String,Object> parameter);
}
