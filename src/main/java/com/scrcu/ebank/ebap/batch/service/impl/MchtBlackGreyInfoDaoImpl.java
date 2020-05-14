package com.scrcu.ebank.ebap.batch.service.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBlackGreyInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtBlackGreyInfoMapper;
import com.scrcu.ebank.ebap.batch.service.MchtBlackGreyInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

/**
 * 商户黑灰名单操作
 */
@Repository("mchtBlackGreyInfoDao")
public class MchtBlackGreyInfoDaoImpl extends BaseBatisDao implements MchtBlackGreyInfoDao {

	private final Class<MchtBlackGreyInfoMapper> mapper = MchtBlackGreyInfoMapper.class;

	@Override
	public int deleteByPrimaryKey(String id) {
		return getSqlSession().getMapper(mapper).deleteByPrimaryKey(id);

	}

	@Override
	public int insert(MchtBlackGreyInfo record) {
		return getSqlSession().getMapper(mapper).insert(record);
	}

	@Override
	public int insertSelective(MchtBlackGreyInfo record) {
		return getSqlSession().getMapper(mapper).insertSelective(record);
	}

	@Override
	public MchtBlackGreyInfo selectByPrimaryKey(String id) {
		return getSqlSession().getMapper(mapper).selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(MchtBlackGreyInfo record) {
		return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MchtBlackGreyInfo record) {
		return getSqlSession().getMapper(mapper).updateByPrimaryKey(record);
	}

	@Override
	public MchtBlackGreyInfo selectByMchtId(String mchtId) {
		return getSqlSession().getMapper(mapper).selectByMchtId(mchtId);
	}
	@Override
	public MchtBlackGreyInfo queryStateByMchtId(String mchtId) {
		return getSqlSession().getMapper(mapper).queryStateByMchtId(mchtId);
	}
}
