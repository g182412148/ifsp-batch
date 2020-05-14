package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerHangAcc;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface BthMerHangAccMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(BthMerHangAcc record);

    int insertSelective(BthMerHangAcc record);

    BthMerHangAcc selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(BthMerHangAcc record);

    int updateByPrimaryKey(BthMerHangAcc record);

    List<BthMerHangAcc> selectByHangSt(String hangSt);

    void updateHangStByKey(@Param("pagyPayTxnSsn") String pagyPayTxnSsn, @Param("date") Date date,@Param("settleDate") String settleDate);

    void deleteBySettleDateIn(String settleDate);

    void updateHangStBySettleDateOut(String settleDate);
}