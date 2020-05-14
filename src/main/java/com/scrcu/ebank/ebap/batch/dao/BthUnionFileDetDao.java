package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthUnionFileDet;

public interface BthUnionFileDetDao {

	int deleteBypagyNoAndDate(String pagyNo, String settleDate);

	int insertSelectiveList(List<BthUnionFileDet> bthUnionFileDetList);

	List<BthUnionFileDet> selectList(String string, Map<String, Object> params);

	int updateByPrimaryKeySelective(BthUnionFileDet bthUnionFileDet);


    void insert(BthUnionFileDet bthUnionFileDet);

    void deleteByPagySysNoAndDate(String pagySysNo, String settleDate);

    BthUnionFileDet selectOne(String statement, Map<String,Object> map);
}
