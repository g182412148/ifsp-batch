package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillResult;

import java.util.Date;

public interface IbankBillResultDao {
    int insert(IbankBillResult record);

    int insertSelective(IbankBillResult record);

    int insertIBankResult(Date recoDate);

    int clear();

    int countLocal();

    int countOuter();
}
