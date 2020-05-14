package com.scrcu.ebank.ebap.batch.dao;


import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtSettlRateCfg;

public interface MchtSettlRateCfgDao {
    List<MchtSettlRateCfg> selectAllInfo() throws Exception;

	MchtSettlRateCfg selectByMchtId(String mchtId, String acctTypeId, String acctSubTypeId);
	
	List<MchtSettlRateCfg> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    MchtSettlRateCfg selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);
}
