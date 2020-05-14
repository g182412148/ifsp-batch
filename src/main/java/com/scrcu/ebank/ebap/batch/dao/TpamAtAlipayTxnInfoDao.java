package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtAlipayTxnInfoVo;

public interface TpamAtAlipayTxnInfoDao {

	List<TpamAtAlipayTxnInfoVo> selectByDateAndState(String settleDate);

}
