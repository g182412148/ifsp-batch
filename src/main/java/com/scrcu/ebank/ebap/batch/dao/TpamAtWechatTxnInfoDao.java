package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfoVo;

public interface TpamAtWechatTxnInfoDao {

	List<TpamAtWechatTxnInfoVo> selectByDateAndState(String settleDate);

	TpamAtWechatTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);

	int updateByPrimaryKeySelective(TpamAtWechatTxnInfo tpamAtWechatTxnInfo);

}
