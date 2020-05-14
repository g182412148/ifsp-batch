package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.CreditTranInfo;

public interface CreditTranInfoDao {

	int deleteByDate(String settleDate);

	int insertSelectiveList(List<CreditTranInfo> creditTranInfoList);

}
