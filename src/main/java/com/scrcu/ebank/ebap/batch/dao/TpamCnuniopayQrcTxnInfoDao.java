package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamCnuniopayQrcTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamCnuniopayQrcTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamUpacpTxnInfoVo;

public interface TpamCnuniopayQrcTxnInfoDao {

	List<TpamCnuniopayQrcTxnInfoVo> selectByDateAndState(String settleDate);

    List<TpamUpacpTxnInfoVo> scanTpamUpacpTxnInfo(String settleDate);

    TpamCnuniopayQrcTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);
}
