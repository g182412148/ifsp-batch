package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthWxFileDet;

public interface BthWxFileDetDao {

	int deleteBypagyNoAndDate(String pagyNo, String settleDate);

	int insertSelectiveList(List<BthWxFileDet> bthWxFileDetList);

	List<BthWxFileDet> selectByDate(String settleDate);

	int updateByPrimaryKeySelective(BthWxFileDet bthWxFileDet);

	List<BthWxFileDet> selectByDateAndStat(String settleDate, String state);

	List<BthWxFileDet> selectByDateAndChkstat(String doubtDate, String chkSt);

	List<BthWxFileDet> selectList(String string, Map<String, Object> msgs);
	
	int delete(String string, Map<String, Object> msgs);

    void updateChkStRstByOrderNo(String orderNo);
}
