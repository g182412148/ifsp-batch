package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.SettleAccModTask;
import com.scrcu.ebank.ebap.batch.bean.mapper.SettleAccModTaskMapper;
import com.scrcu.ebank.ebap.batch.dao.SettleAccModTaskDao;
import com.scrcu.ebank.ebap.batch.dao.SettleAccModTaskDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class SettleAccModTaskDaoImpl extends BaseBatisDao implements SettleAccModTaskDao {

    private final Class<SettleAccModTaskMapper> SettleAccModTaskMapper=SettleAccModTaskMapper.class;
    @Override
    public int insertSelective(SettleAccModTask dtl)  
    {
        return getSqlSession().getMapper(SettleAccModTaskMapper).insertSelective(dtl);
    }
	@Override
	public int updateByPrimaryKeySelective(SettleAccModTask record) {
		return 0;
	}
	@Override
	public int update(String statement, SettleAccModTask vatInfo) {
		return 0;
	}
	@Override
	public List<SettleAccModTask> selectList(String statement, Map<String, Object> parameter) {
		 return getSqlSession().selectList(statement, parameter);
	}
	@Override
	public int insert(SettleAccModTask record) {
		 return getSqlSession().getMapper(SettleAccModTaskMapper).insert(record);
	}
	
	@Override
	public void deleteByPrimaryKey(String id) {
		getSqlSession().getMapper(SettleAccModTaskMapper).deleteByPrimaryKey(id);
		
	}
	@Override
	public int delete(String statement, Map<String, Object> parameter) {
		return getSqlSession().delete(statement, parameter);
	}
	
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}
	
	@Override
	public SettleAccModTask selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}
	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

}
