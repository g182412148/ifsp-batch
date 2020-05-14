package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamWechatDcTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.TpamWechatDcTxnInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.TpamWechatDcTxnInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 名称：〈微信通道支付流水数据操作实现〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2017-12-04 <br>
 * 作者：zhaodk <br>
 * 说明：<br>
 */
@Repository
@Slf4j
public class TpamWechatDcTxnInfoDaoImp extends BaseBatisDao implements TpamWechatDcTxnInfoDao {
	
	private final Class<TpamWechatDcTxnInfoMapper> tpamAtWechatTxnInfoMapper = TpamWechatDcTxnInfoMapper.class;
	@Override
	public List<TpamAtWechatTxnInfoVo> selectByDateAndState(String settleDate) {
		return super.getSqlSession().getMapper(tpamAtWechatTxnInfoMapper).selectByDateAndState(settleDate);
	}
	@Override
	public TpamWechatDcTxnInfo selectByPrimaryKey(String pagyPayTxnSsn) {
		return super.getSqlSession().getMapper(tpamAtWechatTxnInfoMapper).selectByPrimaryKey(pagyPayTxnSsn);
	}
	@Override
	public int updateByPrimaryKeySelective(TpamWechatDcTxnInfo tpamAtWechatTxnInfo) {
		return super.getSqlSession().getMapper(tpamAtWechatTxnInfoMapper).updateByPrimaryKeySelective(tpamAtWechatTxnInfo);
	}
	@Override
	public List<TpamAtWechatTxnInfoVo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

}
