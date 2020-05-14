package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamUpacpTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamUpacpTxnInfoVo;

import java.util.List;

public interface TpamUpacpTxnInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(TpamUpacpTxnInfo record);

    int insertSelective(TpamUpacpTxnInfo record);

    TpamUpacpTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(TpamUpacpTxnInfo record);

    int updateByPrimaryKey(TpamUpacpTxnInfo record);

    /**
     * 根据交易日期查询银联全渠道本地表
     * @param settleDate
     * @return
     */
    List<TpamUpacpTxnInfoVo> scanTpamUpacpTxnInfo(String settleDate);
}