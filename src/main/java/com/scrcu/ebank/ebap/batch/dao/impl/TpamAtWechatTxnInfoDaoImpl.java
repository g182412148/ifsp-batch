package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.mapper.TpamAtWechatTxnInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.TpamAtWechatTxnInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<微信通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("tpamAtWechatTxnInfoDao")
public class TpamAtWechatTxnInfoDaoImpl extends BaseBatisDao implements TpamAtWechatTxnInfoDao {
	private final Class<TpamAtWechatTxnInfoMapper> tpamAtWechatTxnInfoMapper = TpamAtWechatTxnInfoMapper.class;
	@Override
	public List<TpamAtWechatTxnInfoVo> selectByDateAndState(String settleDate) {
		return super.getSqlSession().getMapper(tpamAtWechatTxnInfoMapper).selectByDateAndState(settleDate);
	}
	@Override
	public TpamAtWechatTxnInfo selectByPrimaryKey(String pagyPayTxnSsn) {
		return super.getSqlSession().getMapper(tpamAtWechatTxnInfoMapper).selectByPrimaryKey(pagyPayTxnSsn);
	}
	@Override
	public int updateByPrimaryKeySelective(TpamAtWechatTxnInfo tpamAtWechatTxnInfo) {
		return super.getSqlSession().getMapper(tpamAtWechatTxnInfoMapper).updateByPrimaryKeySelective(tpamAtWechatTxnInfo);
	}

}
