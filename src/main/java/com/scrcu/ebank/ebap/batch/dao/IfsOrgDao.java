package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsOrg;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.SynchronizeOrgInfo;

public interface IfsOrgDao {

	int insert(IfsOrg ifsOrg);

	int update(IfsOrg ifsOrg);

    IfsOrg selectByPrimaryKey(String orgId);

	List<IfsOrg> selectAll();

	List<IfsOrg> selectCorpById(String brId);

	int insert(SynchronizeOrgInfo synchronizeOrgInfo);

	/**
	 * 通过地区号查询机构号
	 *
	 * @param areaCode 地区号
	 * @return 机构号
	 */
	List<String> selectBrAreaCode(String areaCode);
}
