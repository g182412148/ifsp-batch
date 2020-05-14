package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthChkUnequalInfo;
import org.apache.ibatis.annotations.Param;

public interface BthChkUnequalInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(BthChkUnequalInfo record);

    int insertSelective(BthChkUnequalInfo record);

    BthChkUnequalInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(BthChkUnequalInfo record);

    int updateByPrimaryKey(BthChkUnequalInfo record);

    void deleteByChkUeqDtAndPagySysNo(@Param("chkUeqDate") String chkUeqDate, @Param("pagySysNo") String pagySysNo);

    void updChkUnequalInfoProcStByPagyPayTxnSsn(String pagyPayTxnSsn);
}