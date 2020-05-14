package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.OrderTplInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.OrderTplInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.OrderTplInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

/**
 * <p>名称 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : luy </p>
 * <p>日期 : 2018/7/18 0018  9:14</p>
 */
@Repository
public class OrderTplInfoDaoImpl extends BaseBatisDao implements OrderTplInfoDao {

    private Class<OrderTplInfoMapper> orderTplInfoMapper = OrderTplInfoMapper.class;

    @Override
    public int deleteByPrimaryKey(String tplSsn) {
        return getSqlSession().getMapper(orderTplInfoMapper).deleteByPrimaryKey(tplSsn);
    }

    @Override
    public int insert(OrderTplInfo record) {
        return getSqlSession().getMapper(orderTplInfoMapper).insert(record);
    }

    @Override
    public int insertSelective(OrderTplInfo record) {
        return getSqlSession().getMapper(orderTplInfoMapper).insertSelective(record);
    }

    @Override
    public OrderTplInfo selectByPrimaryKey(String tplSsn) {
        return getSqlSession().getMapper(orderTplInfoMapper).selectByPrimaryKey(tplSsn);
    }

    @Override
    public int updateByPrimaryKeySelective(OrderTplInfo record) {
        return getSqlSession().getMapper(orderTplInfoMapper).updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(OrderTplInfo record) {
        return getSqlSession().getMapper(orderTplInfoMapper).updateByPrimaryKey(record);
    }

	@Override
	public List<OrderTplInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public OrderTplInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

}
