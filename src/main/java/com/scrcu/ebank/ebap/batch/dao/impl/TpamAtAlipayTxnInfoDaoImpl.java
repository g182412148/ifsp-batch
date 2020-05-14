package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtAlipayTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.mapper.TpamAtAlipayTxnInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.TpamAtAlipayTxnInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<支付宝通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("tpamAtAlipayTxnInfoDao")
public class TpamAtAlipayTxnInfoDaoImpl extends BaseBatisDao implements TpamAtAlipayTxnInfoDao {
	private final Class<TpamAtAlipayTxnInfoMapper> tpamAtAlipayTxnInfoMapper = TpamAtAlipayTxnInfoMapper.class;
	@Override
	public List<TpamAtAlipayTxnInfoVo> selectByDateAndState(String settleDate) {
		return super.getSqlSession().getMapper(tpamAtAlipayTxnInfoMapper).selectByDateAndState(settleDate);
	}

}
