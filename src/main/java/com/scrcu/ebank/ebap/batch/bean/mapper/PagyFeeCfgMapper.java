package com.scrcu.ebank.ebap.batch.bean.mapper;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyFeeCfg;

public interface PagyFeeCfgMapper {
    int deleteByPrimaryKey(String cfgId);

    int insert(PagyFeeCfg record);

    int insertSelective(PagyFeeCfg record);

    PagyFeeCfg selectByPrimaryKey(String cfgId);

    int updateByPrimaryKeySelective(PagyFeeCfg record);

    int updateByPrimaryKey(PagyFeeCfg record);

}