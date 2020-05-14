package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface IbankBillResultMapper {
    int insert(IbankBillResult record);

    int insertSelective(IbankBillResult record);

    int insertIBankResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    int clear();

    int countLocal();

    int countOuter();
}