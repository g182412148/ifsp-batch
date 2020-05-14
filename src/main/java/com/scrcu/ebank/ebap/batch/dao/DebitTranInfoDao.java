package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.DebitTranInfo;

public interface DebitTranInfoDao {

	int deleteByDate(String settleDate);

	int insertSelectiveList(List<DebitTranInfo> debitTranInfoList);

	DebitTranInfo selectByPrimaryKey(String pagyPayTxnSsn);

	int update(DebitTranInfo debitTranInfo);

	int insertSelective(DebitTranInfo debitTranInfo);

	List<DebitTranInfo> selectList(String string, Map<String, Object> params);

	int updateByPrimaryKeySelective(DebitTranInfo debitTranInfo);

	int deleteTallyByDate(String settleDate);
	
	int delete(String statement, Map<String, Object> params);

}
