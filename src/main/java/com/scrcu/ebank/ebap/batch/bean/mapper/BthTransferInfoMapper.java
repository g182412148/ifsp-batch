package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthTransferInfo;

public interface BthTransferInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(BthTransferInfo record);

    int insertSelective(BthTransferInfo record);

    BthTransferInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BthTransferInfo record);

    int updateByPrimaryKey(BthTransferInfo record);
}