package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthAliFileDet;

public interface BthAliFileDetDao {

	int deleteBypagyNoAndDate(String pagyNo, String settleDate);

	int insertSelectiveList(List<BthAliFileDet> bthAliFileDetList);

	List<BthAliFileDet> selectList(String string, Map<String, Object> params);

	int updateByPrimaryKeySelective(BthAliFileDet bthAliFileDet);

}
