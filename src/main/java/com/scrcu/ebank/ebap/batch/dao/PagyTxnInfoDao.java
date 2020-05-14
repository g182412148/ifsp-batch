package com.scrcu.ebank.ebap.batch.dao;

import java.util.ArrayList;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyTxnInfo;

public interface PagyTxnInfoDao {

	ArrayList<PagyTxnInfo> selectByDateAndState(String settleDate);

	PagyTxnInfo selectOne(String string, Map<String, Object> params);

}
