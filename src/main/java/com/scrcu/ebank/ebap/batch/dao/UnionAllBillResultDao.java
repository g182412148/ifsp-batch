package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionAllBillResult;

import java.util.Date;

public interface UnionAllBillResultDao {
    int insert(UnionAllBillResult record);

    int insertSelective(UnionAllBillResult record);

    int insertUnionAllResult(Date recoDate);

    int clear();

    int countLocal();

    int countOuter();
}
