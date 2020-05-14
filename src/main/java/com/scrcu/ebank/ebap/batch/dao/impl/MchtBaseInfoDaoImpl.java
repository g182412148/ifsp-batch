package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtExtInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtBaseInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("mchtBaseInfoDaoImpl")
@Slf4j
public class MchtBaseInfoDaoImpl extends BaseBatisDao implements MchtBaseInfoDao {

    private final Class<MchtBaseInfoMapper> mchtBaseInfoMapper = MchtBaseInfoMapper.class;

    @Override
    public List<MchtBaseInfo> selectAllInfo(Map parameter) throws Exception {

        try {
            return getSqlSession().getMapper(mchtBaseInfoMapper).selectAllInfo(parameter);
        } catch (Exception e) {
            log.error("查询商户信息失败: ",e);
            throw new Exception("查询商户信息失败!");
        }
    }

	@Override
	public List<MchtBaseInfo> querySubbranchByMchtId(String mchtId) {
		
		return getSqlSession().getMapper(mchtBaseInfoMapper).querySubbranchByMchtId(mchtId);
	}

	@Override
	public List<MchtBaseInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtBaseInfo selectOne(String statement, Map<String, Object> parameter) {
		return  getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public MchtBaseInfo queryById(String mchtId) {
		// TODO Auto-generated method stub
		return getSqlSession().getMapper(mchtBaseInfoMapper).queryById(mchtId);
	}

	@Override
	public List<MchtBaseInfoVo> queryMchtTypeByMchtId(String mchtId) {
		
		return getSqlSession().getMapper(mchtBaseInfoMapper).queryMchtTypeByMchtId(mchtId);
	}
	@Override
	public 	MchtExtInfo queryMchtExtInfoByMchtId(String mchtId){

		return getSqlSession().getMapper(mchtBaseInfoMapper).queryMchtExtInfoByMchtId(mchtId);
	}

	@Override
	public MchtBaseInfo selectByPrimaryKey(String mchtId) {
		return getSqlSession().getMapper(mchtBaseInfoMapper).selectByPrimaryKey(mchtId);
	}

	@Override
	public List<MchtBaseInfo> selectUntradedMerchants(String orderTM,String months) {
		return getSqlSession().getMapper(mchtBaseInfoMapper).selectUntradedMerchants(orderTM,months);
	}


}
