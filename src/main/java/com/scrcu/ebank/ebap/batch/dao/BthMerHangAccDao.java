package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerHangAcc;

import java.util.Date;
import java.util.List;

public interface BthMerHangAccDao {
    void insert(BthMerHangAcc record);

    List<BthMerHangAcc> selectByHangSt(String hangSt);

    BthMerHangAcc selectByPrimaryKey(String pagyPayTxnSsn);

    void updateHangStByKey(String pagyPayTxnSsn, Date date, String settleDate);

    void deleteBySettleDateIn(String settleDate);

    void updateHangStBySettleDateOut(String settleDate);
}
