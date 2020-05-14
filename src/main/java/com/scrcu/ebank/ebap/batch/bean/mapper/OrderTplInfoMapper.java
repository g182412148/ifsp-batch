package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.OrderTplInfo;

public interface OrderTplInfoMapper {
    int deleteByPrimaryKey(String tplSsn);

    int insert(OrderTplInfo record);

    int insertSelective(OrderTplInfo record);

    OrderTplInfo selectByPrimaryKey(String tplSsn);

    int updateByPrimaryKeySelective(OrderTplInfo record);

    int updateByPrimaryKey(OrderTplInfo record);

    int deleteByOrderSsn(String orderSsn);

}