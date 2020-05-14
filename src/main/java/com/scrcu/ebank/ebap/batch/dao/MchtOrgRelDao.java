package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtOrgRel;

public interface MchtOrgRelDao {
    MchtOrgRel selectByMchtIdType(String mchtId, String orgType);
    

    List<MchtOrgRel> selectList(String statement,Map<String,Object> parameter);
    
    List<String> selectMchtNoList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    MchtOrgRel selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);
}
