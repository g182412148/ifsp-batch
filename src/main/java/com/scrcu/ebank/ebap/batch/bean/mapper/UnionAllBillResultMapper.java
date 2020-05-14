package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionAllBillResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface UnionAllBillResultMapper {
    int insert(UnionAllBillResult record);

    int insertSelective(UnionAllBillResult record);

    int insertUnionAllResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    int clear();

    int countLocal();

    int countOuter();
}