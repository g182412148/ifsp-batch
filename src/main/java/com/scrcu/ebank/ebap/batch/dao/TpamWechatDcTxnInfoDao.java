package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamWechatDcTxnInfo;

/**
 * 名称：〈微信通道支付流水数据操作〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2017-12-04 <br>
 * 作者：zhaodk <br>
 * 说明：<br>
 */
public interface TpamWechatDcTxnInfoDao {

	List<TpamAtWechatTxnInfoVo> selectByDateAndState(String settleDate);

	TpamWechatDcTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);

	int updateByPrimaryKeySelective(TpamWechatDcTxnInfo tpamAtWechatTxnInfo);
	
	List<TpamAtWechatTxnInfoVo> selectList(String statement,Map<String,Object> parameter);

}
