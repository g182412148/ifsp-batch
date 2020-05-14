package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;

public interface MchtContInfoDao {
    List<MchtContInfo> selectAllInfo() throws Exception;
    
    List<MchtContInfo> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    MchtContInfo selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);

	MchtContInfo queryByMchtId(String mchtId);
}
