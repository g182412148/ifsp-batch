package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsOrg;

public interface IfsOrgMapper {
    int deleteByPrimaryKey(String brId);

    int insert(IfsOrg record);

    int insertSelective(IfsOrg record);

    IfsOrg selectByPrimaryKey(String brId);

    int updateByPrimaryKeySelective(IfsOrg record);

    int updateByPrimaryKey(IfsOrg record);

	List<IfsOrg> selectAll();

	List<IfsOrg> selectCorpById(String brId);
    /**
     * 通过地区号查询机构号
     *
     * @param areaCode 地区号
     * @return 机构号
     */
    List<String> selectBrAreaCode(String areaCode);
}