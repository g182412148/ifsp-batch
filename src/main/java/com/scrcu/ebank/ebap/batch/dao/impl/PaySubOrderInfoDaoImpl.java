package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.PaySubOrderInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

/**
 * <p>名称 :  </p>
 * <p>方法 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : ydl </p>
 * <p>日期 : 2018/6/28 0028  21:06</p>
 */
@Repository
public class PaySubOrderInfoDaoImpl extends BaseBatisDao implements PaySubOrderInfoDao {

    private Class<PaySubOrderInfoMapper> paySubOrderInfoMapper= PaySubOrderInfoMapper.class;
    @Override
    public int deleteByPrimaryKey(String subOrderSsn) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).deleteByPrimaryKey(subOrderSsn);
    }

    @Override
    public int insert(PaySubOrderInfo record) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).insert(record);
    }

    @Override
    public int insertSelective(PaySubOrderInfo record) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).insertSelective(record);
    }

    @Override
    public PaySubOrderInfo selectByPrimaryKey(String subOrderSsn) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).selectByPrimaryKey(subOrderSsn);
    }

    @Override
    public int updateByPrimaryKeySelective(PaySubOrderInfo record) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(PaySubOrderInfo record) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).updateByPrimaryKey(record);
    }

    @Override
    public List<PaySubOrderInfo> selectByOrderNo(String orderSsn) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).selectByOrderNo(orderSsn);
    }

    @Override
    public PaySubOrderInfo selectByMerOrderNo(String merId, String merOrderNo) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).selectByMerOrderNo(merId,merOrderNo);
    }

    @Override
    public int deleteByOrderSsn(String orderSsn) {
        return getSqlSession().getMapper(paySubOrderInfoMapper).deleteByOrderSsn(orderSsn);
    }

	@Override
	public List<PaySubOrderInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement,parameter);
	}

	@Override
	public PaySubOrderInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement,parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}
}
