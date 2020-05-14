package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionQrcBillResult;

import java.util.Date;

public interface UnionQrcBillResultDao {
    int insert(UnionQrcBillResult record);

    int insertSelective(UnionQrcBillResult record);

    int insertUnionQrcResult(Date recoDate);

    int clear();

    int countLocal();

    int countOuter();
}
