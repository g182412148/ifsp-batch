package com.scrcu.ebank.ebap.batch.bean.mapper;

import org.apache.ibatis.annotations.Param;

public interface OrgRepeMergMapper {

    int updateOrg(@Param("tableName")String tableName, @Param("param")String param, @Param("repeOrg")String repeOrg, @Param("mergOrg")String mergOrg);

}