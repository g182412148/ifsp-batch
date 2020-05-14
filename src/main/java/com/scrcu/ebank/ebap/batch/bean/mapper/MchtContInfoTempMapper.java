package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfoTemp;

public interface MchtContInfoTempMapper {
    int deleteByPrimaryKey(String contNo);

    int insert(MchtContInfoTemp record);

    int insertSelective(MchtContInfoTemp record);

    MchtContInfoTemp selectByPrimaryKey(String contNo);

    int updateByPrimaryKeySelective(MchtContInfoTemp record);

    int updateByPrimaryKey(MchtContInfoTemp record);

    int updateMchtContInfoTempDep(@Param("oldSettleNo")String oldSettleNo, @Param("newSettleNo")String newSettleNo);

    int updateMchtContInfoTempLiq(@Param("oldSettleNo")String oldSettleNo, @Param("newSettleNo")String newSettleNo);
}