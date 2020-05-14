package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtSettlRateCfg;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface MchtSettlRateCfgMapper {
    int deleteByPrimaryKey(String rowId);

    int insert(MchtSettlRateCfg record);

    int insertSelective(MchtSettlRateCfg record);

    MchtSettlRateCfg selectByPrimaryKey(String rowId);

    int updateByPrimaryKeySelective(MchtSettlRateCfg record);

    int updateByPrimaryKey(MchtSettlRateCfg record);

    List<MchtSettlRateCfg> selectAllInfo();
    
	MchtSettlRateCfg selectByMchtId(@Param("mchtId")String mchtId, @Param("acctTypeId")String acctTypeId, @Param("acctSubTypeId")String acctSubTypeId);
}