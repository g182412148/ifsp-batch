package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtOrgRelTemp;
import org.apache.ibatis.annotations.Param;

public interface MchtOrgRelTempMapper {
    int deleteByPrimaryKey(@Param("mchtNo") String mchtNo, @Param("orgId") String orgId, @Param("orgType") String orgType);

    int insert(MchtOrgRelTemp record);

    int insertSelective(MchtOrgRelTemp record);

    MchtOrgRelTemp selectByPrimaryKey(@Param("mchtNo") String mchtNo, @Param("orgId") String orgId, @Param("orgType") String orgType);

    int updateByPrimaryKeySelective(MchtOrgRelTemp record);

    int updateByPrimaryKey(MchtOrgRelTemp record);

    int updateMchtOrgRelTemp(@Param("reqe")String reqe, @Param("merg")String merg, @Param("orgName")String orgName);
}