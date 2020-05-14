package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.OrderTplInfo;

public interface OrderTplInfoDao {
    int deleteByPrimaryKey(String tplSsn);

    int insert(OrderTplInfo record);

    int insertSelective(OrderTplInfo record);

    OrderTplInfo selectByPrimaryKey(String tplSsn);

    int updateByPrimaryKeySelective(OrderTplInfo record);

    int updateByPrimaryKey(OrderTplInfo record);
    
    List<OrderTplInfo> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    OrderTplInfo selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);

}
