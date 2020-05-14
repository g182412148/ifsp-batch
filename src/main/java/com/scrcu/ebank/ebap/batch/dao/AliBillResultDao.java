package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillResult;

import java.util.Date;

public interface AliBillResultDao {
    int insert(AliBillResult record);

    int insertSelective(AliBillResult record);

    int insertAliResult(Date recoDate);

    int clear();

    int countLocal();

    int countOuter();
}
