package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.KeepAccInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

/**
 *名称：<记账任务> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/7/27 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("KeepAccInfoDao")
public class keepAccInfoDaoImpl extends BaseBatisDao implements KeepAccInfoDao {
	private final Class<KeepAccInfoMapper> keepAccInfoMapper=KeepAccInfoMapper.class;
	
	@Override
	public int insert(KeepAccInfo keepAccInfo) {
		return super.getSqlSession().getMapper(keepAccInfoMapper).insert(keepAccInfo);
	}

	@Override
	public KeepAccInfo selectByPrimaryKey(String orderSsn) {
		return super.getSqlSession().getMapper(keepAccInfoMapper).selectByPrimaryKey(orderSsn);
	}

	@Override
	public int update(KeepAccInfo keepAccInfoVo) {
		return super.getSqlSession().getMapper(keepAccInfoMapper).updateByPrimaryKey(keepAccInfoVo);
	}

    @Override
    public int updateByPrimaryKeySelective(KeepAccInfo keepAccInfoVo) {
        return super.getSqlSession().getMapper(keepAccInfoMapper).updateByPrimaryKeySelective(keepAccInfoVo);
    }

    @Override
    public int updateByPrimaryKeySelectiveState(KeepAccInfo keepAccInfoVo) {
        return super.getSqlSession().getMapper(keepAccInfoMapper).updateByPrimaryKeySelectiveState(keepAccInfoVo);
    }

    @Override
    public List<KeepAccInfo> selectByState(String state) {
        return getSqlSession().getMapper(keepAccInfoMapper).selectByState(state);
    }

	@Override
	public List<KeepAccInfo> selectList(String statement, Map<String, Object> params) {
		return getSqlSession().selectList(statement, params);
	}

    @Override
    public void updateByState(String coreSsn, String state, String registerIp) {
        getSqlSession().getMapper(keepAccInfoMapper).updateByState(coreSsn,state,registerIp);
    }

    @Override
    public int update(String statement, Map<String, Object> parameter) {
        return getSqlSession().update(statement, parameter);
    }

    @Override
    public List<KeepAccInfo> selectByOrderSsn(String orderSsn) {
        return getSqlSession().getMapper(keepAccInfoMapper).selectByOrderSsn(orderSsn);
    }

    @Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

    @Override
    public List<String> selectByStateIsSync( Map<String, Object> parameter) {
        return getSqlSession().getMapper(keepAccInfoMapper).selectByStateIsSync( parameter);
    }

    @Override
    public int recovery(String settleDate) {
        return getSqlSession().getMapper(keepAccInfoMapper).recovery(settleDate);
    }

    @Override
    public int recoveryDubious(String settleDateDubious) {
        return getSqlSession().getMapper(keepAccInfoMapper).recoveryDubious(settleDateDubious);
    }

    @Override
    public int updateByPrimaryKeyAndSt(KeepAccInfo keepAccInfoVo) {
        return getSqlSession().getMapper(keepAccInfoMapper).updateByPrimaryKeyAndSt(keepAccInfoVo);
    }

    @Override
    public List<KeepAccInfo> queryByOrderSsnAndTm(String orderSsn, String orderTm) {
        return getSqlSession().getMapper(keepAccInfoMapper).queryByOrderSsnAndTm(orderSsn,orderTm);
    }

    @Override
    public List<KeepAccInfo> queryResvRec(Integer count, Integer retryCount) {
        return getSqlSession().getMapper(keepAccInfoMapper).queryResvRec(count,retryCount);
    }

    @Override
    public int updateRetryCount(String coreSsn, String state, String respCode, String respMsg) {
        return getSqlSession().getMapper(keepAccInfoMapper).updateRetryCount(coreSsn,state,respCode,respMsg);
    }

    @Override
    public List<KeepAccInfo> queryByOrderSsnTmAndVerNoSucc(String orderSsn, String orderTm,String verNo) {
        return getSqlSession().getMapper(keepAccInfoMapper).queryByOrderSsnTmAndVerNoSucc(orderSsn,orderTm,verNo);
    }

    @Override
    public String queryByOrigCoreSsn(String origCoreSsn) {
        return getSqlSession().getMapper(keepAccInfoMapper).queryByOrigCoreSsn(origCoreSsn);
    }

    @Override
    public List<KeepAccInfo> queryByOrderSsnAndTmAndVerNo(String orderSsn, String orderTm, String verNo) {
        return getSqlSession().getMapper(keepAccInfoMapper).queryByOrderSsnAndTmAndVerNo(orderSsn,orderTm,verNo);
    }

    @Override
    public List<KeepAccInfo> queryByOrderSsnTmAndMaxVerNoSucc(String orderSsn, String orderTm) {
        return getSqlSession().getMapper(keepAccInfoMapper).queryByOrderSsnTmAndMaxVerNoSucc(orderSsn,orderTm );
    }

    @Override
    public int insertIgnoreExist(KeepAccInfo keepAccInfo) {
        return super.getSqlSession().getMapper(keepAccInfoMapper).insertIgnoreExist(keepAccInfo);
    }

    @Override
    public KeepAccInfo selectByOrderSsnT0(String orderSsn) {
        return super.getSqlSession().getMapper(keepAccInfoMapper).selectByOrderSsnT0(orderSsn);
    }

}
