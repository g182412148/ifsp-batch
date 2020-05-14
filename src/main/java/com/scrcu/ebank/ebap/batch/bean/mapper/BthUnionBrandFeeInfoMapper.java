package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthUnionBrandFeeInfo;

import java.util.List;

public interface BthUnionBrandFeeInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(BthUnionBrandFeeInfo record);

    int insertSelective(BthUnionBrandFeeInfo record);

    BthUnionBrandFeeInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BthUnionBrandFeeInfo record);

    int updateByPrimaryKey(BthUnionBrandFeeInfo record);

    void deleteByStlmDate(String settleDate);

    List<BthUnionBrandFeeInfo> selectByStlmDate(String settleDate);
}