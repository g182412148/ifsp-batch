package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamIbankTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamIbankTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.mapper.TpamIbankTxnInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.TpamIbankTxnInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<本行通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("tpamIbankTxnInfoDao")
public class TpamIbankTxnInfoDaoImpl extends BaseBatisDao implements TpamIbankTxnInfoDao {
	private final Class<TpamIbankTxnInfoMapper> tpamIbankTxnInfoMapper = TpamIbankTxnInfoMapper.class;
	@Override
	public List<TpamIbankTxnInfoVo> selectByDateAndState(String settleDate) {
		return super.getSqlSession().getMapper(tpamIbankTxnInfoMapper).selectByDateAndState(settleDate);
	}
	@Override
	public TpamIbankTxnInfo selectByPrimaryKey(String channelSeq) {
		return super.getSqlSession().getMapper(tpamIbankTxnInfoMapper).selectByPrimaryKey(channelSeq);
	}

}
