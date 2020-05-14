package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionQrcBillResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface UnionQrcBillResultMapper {
    int insert(UnionQrcBillResult record);

    int insertSelective(UnionQrcBillResult record);

    int insertUnionQrcResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    int clear();

    int countLocal();

    int countOuter();
}