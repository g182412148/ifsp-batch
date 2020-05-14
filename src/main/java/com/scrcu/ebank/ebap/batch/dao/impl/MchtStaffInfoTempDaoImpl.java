package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfoTemp;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtStaffInfoTempMapper;
import com.scrcu.ebank.ebap.batch.dao.MchtStaffInfoTempDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

/**
 * <p>名称 : 商户人员信息临时表Impl </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/14 </p>
 */
@Repository
public class MchtStaffInfoTempDaoImpl extends BaseBatisDao implements MchtStaffInfoTempDao {

    private Class<MchtStaffInfoTempMapper> mchtStaffInfoTempMapper = MchtStaffInfoTempMapper.class;

	@Override
	public List<MchtStaffInfoTemp> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtStaffInfoTemp selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

   
}
