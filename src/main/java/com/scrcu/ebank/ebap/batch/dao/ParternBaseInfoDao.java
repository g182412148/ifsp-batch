package com.scrcu.ebank.ebap.batch.dao;
import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.ParternDepInfo;


public interface ParternBaseInfoDao {
	
	/**
	 * 查询合作商基本信息
	 * @param parternCode
	 * @return
	 */
	public ParternBaseInfo selectParternBaseInfo(String parternCode);
	
	List<ParternBaseInfo> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    ParternBaseInfo selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);

	List<ParternDepInfo> selectParternBaseInfoList(Map<String,Object> parameter);

}
