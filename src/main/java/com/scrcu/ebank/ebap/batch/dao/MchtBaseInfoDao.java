package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtExtInfo;

public interface MchtBaseInfoDao {
    /**
     * 查询所有商户信息
     * @return
     * @throws Exception
     */
    List<MchtBaseInfo> selectAllInfo(Map parameter) throws Exception;

	List<MchtBaseInfo> querySubbranchByMchtId(String mchtId);
	
	List<MchtBaseInfo> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    MchtBaseInfo selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);

	MchtBaseInfo queryById(String mchtId);

	List<MchtBaseInfoVo> queryMchtTypeByMchtId(String mchtId);

    MchtExtInfo queryMchtExtInfoByMchtId(String mchtId);

    MchtBaseInfo selectByPrimaryKey(String mchtId);

    List<MchtBaseInfo> selectUntradedMerchants(String orderTM,String months);



}
