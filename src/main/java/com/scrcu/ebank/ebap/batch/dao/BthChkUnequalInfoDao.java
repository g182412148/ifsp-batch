package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthChkUnequalInfo;

import java.util.List;
import java.util.Map;

public interface BthChkUnequalInfoDao {
    void deleteByChkUeqDtAndPagySysNo(String chkUeqDate, String  pagySysNo);

    void insert(BthChkUnequalInfo unEqRecord);

    List<BthChkUnequalInfo> selectList(String statement, Map<String,Object> map);

    void updChkUnequalInfoProcStByPagyPayTxnSsn(String pagyPayTxnSsn);
}
