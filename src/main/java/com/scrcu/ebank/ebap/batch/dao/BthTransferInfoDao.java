package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.*;

public interface BthTransferInfoDao {
    void insertSelective(BthTransferInfo transferInfo) throws Exception;

	int updateByPrimaryKeySelective(BthTransferInfo transferInfo);
	
    List<BthTransferInfo> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    BthTransferInfo selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);

}
