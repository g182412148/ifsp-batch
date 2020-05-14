package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtContInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class MchtContInfoDaoImpl extends BaseBatisDao implements MchtContInfoDao {

    private final Class<MchtContInfoMapper> mchtContInfoMapper = MchtContInfoMapper.class;
    /**
     * 查询所有合同细信息
     * @return
     */
    @Override
    public List<MchtContInfo> selectAllInfo() throws Exception {

        try {
            return getSqlSession().getMapper(mchtContInfoMapper).selectAllInfo();
        } catch (Exception e) {
            log.error("查询商户合同信息失败!: "+e);
            throw new Exception("查询商户合同信息失败",e);
        }
    }
	@Override
	public List<MchtContInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}
	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}
	@Override
	public MchtContInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}
	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}
	@Override
	public MchtContInfo queryByMchtId(String mchtId) {
		
		return getSqlSession().getMapper(mchtContInfoMapper).queryByMchtId(mchtId);
	}
}
