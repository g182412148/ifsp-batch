package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthUnionBrandFeeInfo;

import java.util.List;

public interface BthUnionBrandFeeInfoDao {
    void deleteByStlmDate(String settleDate);

    void insertList(List<BthUnionBrandFeeInfo> bthUnionBrandFeeInfoList);

    List<BthUnionBrandFeeInfo> selectByStlmDate(String settleDate);
}
