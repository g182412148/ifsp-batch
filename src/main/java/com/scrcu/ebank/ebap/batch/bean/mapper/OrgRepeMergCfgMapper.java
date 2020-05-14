package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.OrgRepeMergCfg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrgRepeMergCfgMapper {
    int deleteByPrimaryKey(@Param("tableName") String tableName, @Param("param") String param);

    int insert(OrgRepeMergCfg record);

    int insertSelective(OrgRepeMergCfg record);

    OrgRepeMergCfg selectByPrimaryKey(@Param("tableName") String tableName, @Param("param") String param);

    int updateByPrimaryKeySelective(OrgRepeMergCfg record);

    int updateByPrimaryKey(OrgRepeMergCfg record);

    List<OrgRepeMergCfg> selectAllOrg();
}