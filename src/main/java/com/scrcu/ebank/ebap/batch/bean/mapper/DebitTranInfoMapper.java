package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.DebitTranInfo;

public interface DebitTranInfoMapper {
    int deleteByPrimaryKey(String channelSeq);

    int insert(DebitTranInfo record);

    int insertSelective(DebitTranInfo record);

    DebitTranInfo selectByPrimaryKey(String channelSeq);

    int updateByPrimaryKeySelective(DebitTranInfo record);

    int updateByPrimaryKey(DebitTranInfo record);

	int deleteByDate(String settleDate);

	int deleteTallyByDate(String settleDate);
}