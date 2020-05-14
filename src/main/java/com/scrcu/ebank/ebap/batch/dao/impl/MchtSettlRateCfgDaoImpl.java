package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtSettlRateCfg;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtSettlRateCfgMapper;
import com.scrcu.ebank.ebap.batch.dao.MchtSettlRateCfgDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class MchtSettlRateCfgDaoImpl extends BaseBatisDao implements MchtSettlRateCfgDao {
    private final Class<MchtSettlRateCfgMapper> mchtSettlRateCfgMapper = MchtSettlRateCfgMapper.class;
    /**
     * 查询所有商户结算费率信息
     * @return
     */
    @Override
    public List<MchtSettlRateCfg> selectAllInfo() throws Exception {
        try {
            return getSqlSession().getMapper(mchtSettlRateCfgMapper).selectAllInfo();
        } catch (Exception e) {
            log.error("查询商户结算费率失败: "+e);
            throw new Exception("查询商户结算费率失败!",e);
        }
    }
    /**
     * 根据商户号查询费率配置信息
     */
	@Override
	public MchtSettlRateCfg selectByMchtId(String mchtId, String acctTypeId, String acctSubTypeId) {
		 return getSqlSession().getMapper(mchtSettlRateCfgMapper).selectByMchtId(mchtId,acctTypeId,acctSubTypeId);
	}
	@Override
	public List<MchtSettlRateCfg> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}
	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}
	@Override
	public MchtSettlRateCfg selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}
	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}
}
