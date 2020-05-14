package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;

public interface PaySubOrderInfoMapper {
    int deleteByPrimaryKey(String subOrderSsn);

    int insert(PaySubOrderInfo record);

    int insertSelective(PaySubOrderInfo record);

    PaySubOrderInfo selectByPrimaryKey(String subOrderSsn);

    int updateByPrimaryKeySelective(PaySubOrderInfo record);

    int updateByPrimaryKey(PaySubOrderInfo record);

    /**
     * 根据订单号查询子订单列表
     * @param orderSsn
     * @return
     */
    List<PaySubOrderInfo> selectByOrderNo(String orderSsn);

    /**
     * 根据商户号，商户订单号查询
     * @param merId 商户号
     * @param merOrderNo 商户订单号
     * @return
     */
    PaySubOrderInfo selectByMerOrderNo(@Param(value = "merId") String merId, @Param(value = "merOrderNo") String merOrderNo);

    /**
     * 删除主订单下所以子订单信息
     * @param orderSsn
     * @return
     */
    int deleteByOrderSsn(String orderSsn);

}