package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffRelTemp;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtStaffRelTempMapper;
import com.scrcu.ebank.ebap.batch.dao.MchtStaffRelTempDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

/**
 * <p>名称 : 商户关联人员临时表Impl </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/14 </p>
 */
@Repository
public class MchtStaffRelTempDaoImpl extends BaseBatisDao implements MchtStaffRelTempDao {
    private Class<MchtStaffRelTempMapper> mchtStaffRelTempMapper = MchtStaffRelTempMapper.class;

	@Override
	public List<MchtStaffRelTemp> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtStaffRelTemp selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

    
}
