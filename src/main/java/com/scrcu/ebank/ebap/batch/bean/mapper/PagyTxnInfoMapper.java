package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.ArrayList;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyTxnInfo;

public interface PagyTxnInfoMapper {
    int deleteByPrimaryKey(String pagyTxnSsn);

    int insert(PagyTxnInfo record);

    int insertSelective(PagyTxnInfo record);

    PagyTxnInfo selectByPrimaryKey(String pagyTxnSsn);

    int updateByPrimaryKeySelective(PagyTxnInfo record);

    int updateByPrimaryKey(PagyTxnInfo record);
    /**
     * 根据清算日期查询交易成功的PagyTxnInfo
     * @param settleDate
     * @return
     */
	ArrayList<PagyTxnInfo> selectByDateAndState(String settleDate);
}