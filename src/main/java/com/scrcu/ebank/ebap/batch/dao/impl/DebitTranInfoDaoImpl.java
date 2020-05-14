package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.DebitTranInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.DebitTranInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.DebitTranInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("DebitTranInfoDao")
public class DebitTranInfoDaoImpl extends BaseBatisDao implements DebitTranInfoDao {

	private final Class<DebitTranInfoMapper> debitTranInfoMapper=DebitTranInfoMapper.class;
	
	@Override
	public int deleteByDate(String settleDate) {
		return super.getSqlSession().getMapper(debitTranInfoMapper).deleteByDate(settleDate);
	}

	@Override
	public int insertSelectiveList(List<DebitTranInfo> debitTranInfoList) {
		for (DebitTranInfo debitTranInfo : debitTranInfoList) {
			getSqlSession().getMapper(debitTranInfoMapper).insertSelective(debitTranInfo);
		}
		return 0;
	}

	@Override
	public DebitTranInfo selectByPrimaryKey(String pagyPayTxnSsn) {
		return super.getSqlSession().getMapper(debitTranInfoMapper).selectByPrimaryKey(pagyPayTxnSsn);
	}

	@Override
	public int update(DebitTranInfo debitTranInfo) {
		return super.getSqlSession().getMapper(debitTranInfoMapper).updateByPrimaryKeySelective(debitTranInfo);
	}

	@Override
	public int insertSelective(DebitTranInfo debitTranInfo) {
		return super.getSqlSession().getMapper(debitTranInfoMapper).insertSelective(debitTranInfo);
	}

	@Override
	public List<DebitTranInfo> selectList(String statement, Map<String, Object> params) {
		return getSqlSession().selectList(statement, params);
	}

	@Override
	public int updateByPrimaryKeySelective(DebitTranInfo debitTranInfo) {
		return super.getSqlSession().getMapper(debitTranInfoMapper).updateByPrimaryKeySelective(debitTranInfo);
	}

	@Override
	public int deleteTallyByDate(String settleDate) {
		return super.getSqlSession().getMapper(debitTranInfoMapper).deleteTallyByDate(settleDate);
	}

	@Override
	public int delete(String statement, Map<String, Object> params) {
		return getSqlSession().delete(statement, params);
	}

}
