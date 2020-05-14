package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthAliFileDet;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthAliFileDetMapper;
import com.scrcu.ebank.ebap.batch.dao.BthAliFileDetDao;
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
@Repository("bthAliFileDetDao")
public class BthAliFileDetDaoImpl extends BaseBatisDao implements BthAliFileDetDao {
	private final Class<BthAliFileDetMapper> bthAliFileDetMapper=BthAliFileDetMapper.class;
	
	@Override
	public int deleteBypagyNoAndDate(String pagyNo, String settleDate) {
		return super.getSqlSession().getMapper(bthAliFileDetMapper).deleteBypagyNoAndDate(pagyNo,settleDate);
	}

	@Override
	public int insertSelectiveList(List<BthAliFileDet> bthAliFileDetList) {
		for (BthAliFileDet bthAliFileDet : bthAliFileDetList) {
			getSqlSession().getMapper(bthAliFileDetMapper).insertSelective(bthAliFileDet);
		}
		return 0;
	}

	@Override
	public List<BthAliFileDet> selectList(String statement, Map<String, Object> params) {
		return getSqlSession().selectList(statement, params);
	}

	@Override
	public int updateByPrimaryKeySelective(BthAliFileDet bthAliFileDet) {
		return super.getSqlSession().getMapper(bthAliFileDetMapper).updateByPrimaryKeySelective(bthAliFileDet);
	}
}
