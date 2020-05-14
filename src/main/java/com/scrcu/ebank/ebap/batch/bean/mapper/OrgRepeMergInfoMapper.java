package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.OrgRepeMergInfo;
import com.scrcu.ebank.ebap.batch.bean.request.SelectOrgRepeMergRequest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface OrgRepeMergInfoMapper {
    int deleteByPrimaryKey(String repeMergId);

    int insert(OrgRepeMergInfo record);

    int insertSelective(OrgRepeMergInfo record);

    OrgRepeMergInfo selectByPrimaryKey(String repeMergId);

    int updateByPrimaryKeySelective(OrgRepeMergInfo record);

    int updateByPrimaryKey(OrgRepeMergInfo record);

    /**
     * 更加撤销机构和并入机构查询撤并状态
     * @param repeOrg
     * @param mergOrg
     * @return
     */
    String selectByRepeMerg(String repeOrg,String mergOrg);

    /**
     * 查询机构撤并执行状态
     * @param repeOrg
     * @param mergOrg
     * @param mergDt
     * @return
     */
    String selectOrgRepeMerg(@Param("repeOrg")String repeOrg,@Param("mergOrg")String mergOrg,@Param("mergDt")Date mergDt);

    /**
     * 查询机构撤并记录
     * @param repeOrg
     * @param mergOrg
     * @param mergDt
     * @return
     */
    OrgRepeMergInfo selectOrgMergInfo(@Param("repeOrg")String repeOrg,@Param("mergOrg")String mergOrg,@Param("mergDt")Date mergDt);
}