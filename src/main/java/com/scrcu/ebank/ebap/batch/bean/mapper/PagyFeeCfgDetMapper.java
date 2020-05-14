package com.scrcu.ebank.ebap.batch.bean.mapper;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyFeeCfgDet;

public interface PagyFeeCfgDetMapper {
    int deleteByPrimaryKey(String detId);

    int insert(PagyFeeCfgDet record);

    int insertSelective(PagyFeeCfgDet record);

    PagyFeeCfgDet selectByPrimaryKey(String detId);

    int updateByPrimaryKeySelective(PagyFeeCfgDet record);

    int updateByPrimaryKey(PagyFeeCfgDet record);

	PagyFeeCfgDet queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardType(@Param("pagyNo")String pagyNo, @Param("pagySysNo")String pagySysNo, @Param("cardType")String cardType);

    PagyFeeCfgDet queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardTypeAndOrderSsn(@Param("pagyNo")String pagyNo, @Param("pagySysNo")String pagySysNo, @Param("cardType")String cardType, @Param("orderSsn")String orderSsn);

    /**
     * 根据通道编号查询费率配置表
     * @param pagyNo
     * @return
     */
    PagyFeeCfgDet queryPagyFeeCfgByPagyNo(@Param("pagyNo")String pagyNo);
}