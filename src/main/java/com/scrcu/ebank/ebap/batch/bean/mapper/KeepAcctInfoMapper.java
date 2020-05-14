package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAcctInfo;
import org.apache.ibatis.annotations.Param;

public interface KeepAcctInfoMapper {
    int deleteByPrimaryKey(@Param("orderSsn") String orderSsn, @Param("subOrderSsn") String subOrderSsn, @Param("orderSeq") String orderSeq, @Param("keepType") String keepType);

    int insert(KeepAcctInfo record);

    int insertSelective(KeepAcctInfo record);

    KeepAcctInfo selectByPrimaryKey(@Param("orderSsn") String orderSsn, @Param("subOrderSsn") String subOrderSsn, @Param("orderSeq") String orderSeq, @Param("keepType") String keepType);

    int updateByPrimaryKeySelective(KeepAcctInfo record);

    int updateByPrimaryKey(KeepAcctInfo record);

    List<KeepAcctInfo> selectByOrderSsn(String orderSsn);

    List<KeepAcctInfo> querryAll();

    int updateKeepAcctInfo(@Param("mchtId")String mchtId, @Param("merg")String merg);
}