package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface AliBillResultMapper {
    int insert(AliBillResult record);

    int insertSelective(AliBillResult record);

    int insertAliResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    int clear();

    int countLocal();

    int countOuter();
}