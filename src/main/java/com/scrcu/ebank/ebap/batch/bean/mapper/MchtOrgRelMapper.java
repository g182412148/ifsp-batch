package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtOrgRel;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtOrgRelKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MchtOrgRelMapper {
    int deleteByPrimaryKey(MchtOrgRelKey key);

    int insert(MchtOrgRel record);

    int insertSelective(MchtOrgRel record);

    MchtOrgRel selectByPrimaryKey(MchtOrgRelKey key);

    int updateByPrimaryKeySelective(MchtOrgRel record);

    int updateByPrimaryKey(MchtOrgRel record);

    MchtOrgRel selectByMchtIdType(@Param("mchtNo") String mchtId, @Param("orgType") String orgType);

    List<String> selectMchtId(String orgId);

    int updateMchtOrgRel(@Param("reqe")String reqe, @Param("merg")String merg, @Param("orgName")String orgName);
}