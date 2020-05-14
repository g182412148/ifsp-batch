package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.CreditTranInfo;

public interface CreditTranInfoMapper {
    int deleteByPrimaryKey(String channelSeq);

    int insert(CreditTranInfo record);

    int insertSelective(CreditTranInfo record);

    CreditTranInfo selectByPrimaryKey(String channelSeq);

    int updateByPrimaryKeySelective(CreditTranInfo record);

    int updateByPrimaryKey(CreditTranInfo record);

	int deleteByDate(String settleDate);
}