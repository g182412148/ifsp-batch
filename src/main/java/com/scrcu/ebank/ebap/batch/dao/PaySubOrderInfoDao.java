package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;


public interface PaySubOrderInfoDao {

    int deleteByPrimaryKey(String subOrderSsn);

    int insert(PaySubOrderInfo record);

    int insertSelective(PaySubOrderInfo record);

    PaySubOrderInfo selectByPrimaryKey(String subOrderSsn);

    int updateByPrimaryKeySelective(PaySubOrderInfo record);

    int updateByPrimaryKey(PaySubOrderInfo record);

    List<PaySubOrderInfo> selectByOrderNo(String orderSsn);

    PaySubOrderInfo selectByMerOrderNo(String merId, String merOrderNo);

    int deleteByOrderSsn(String orderSsn);
    
    List<PaySubOrderInfo> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    PaySubOrderInfo selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);
}
