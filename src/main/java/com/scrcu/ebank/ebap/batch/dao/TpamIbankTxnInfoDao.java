package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamIbankTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamIbankTxnInfoVo;

public interface TpamIbankTxnInfoDao {

	List<TpamIbankTxnInfoVo> selectByDateAndState(String settleDate);

	TpamIbankTxnInfo selectByPrimaryKey(String channelSeq);

}
