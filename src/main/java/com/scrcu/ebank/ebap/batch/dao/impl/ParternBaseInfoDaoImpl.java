package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternDepInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.ParternBaseInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.ParternBaseInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
@Repository("parternBaseInfoDao")
public class ParternBaseInfoDaoImpl extends BaseBatisDao implements ParternBaseInfoDao {
	// 合作商基本信息
	private Class<ParternBaseInfoMapper> parternBaseInfoMapper = ParternBaseInfoMapper.class;
	
	/**
	 *名称：〈查询合作商列表〉<br>
	 * 功能：〈功能详细描述〉<br>
	 * 方法：〈方法简述 - 方法描述〉<br>
	 * 版本：1.0 <br>
	 * 日期：2018年06月29日 <br>
	 * 作者：root <br>
	 * 说明：<br>
	 * @param parternCode
	 * @return
	 */
	@Override
	public ParternBaseInfo selectParternBaseInfo(String parternCode) {
		return getSqlSession().getMapper(parternBaseInfoMapper).selectParternBaseInfo(parternCode);
	}

	@Override
	public List<ParternBaseInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public ParternBaseInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public List<ParternDepInfo> selectParternBaseInfoList(Map<String, Object> parameter) {
		return getSqlSession().getMapper(parternBaseInfoMapper).selectParternBaseInfoList(parameter);
	}


}
