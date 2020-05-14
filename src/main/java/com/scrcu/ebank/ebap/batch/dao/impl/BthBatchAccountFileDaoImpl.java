package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthBatchAccountFileMapper;
import com.scrcu.ebank.ebap.batch.dao.BthBatchAccountFileDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class BthBatchAccountFileDaoImpl extends BaseBatisDao implements BthBatchAccountFileDao {

    private final Class<BthBatchAccountFileMapper> bthBatchAccountFileMapper = BthBatchAccountFileMapper.class;

    @Override
    public void insertSelective(BthBatchAccountFile f) {
        getSqlSession().getMapper(bthBatchAccountFileMapper).insertSelective(f);
    }

	@Override
	public List<BthBatchAccountFile> queryByDealtatus(String stus) {
		return getSqlSession().getMapper(bthBatchAccountFileMapper).queryByDealtatus(stus);
	}

	@Override
	public int updateByPrimaryKeySelective(BthBatchAccountFile record) {
		return getSqlSession().getMapper(bthBatchAccountFileMapper).updateByPrimaryKeySelective(record);
	}

    @Override
    public List<BthBatchAccountFile> selectList(String statement, Map<String, Object> parameter) {
        return getSqlSession().selectList(statement, parameter);
    }

	@Override
	public List<BthBatchAccountFile> queryByDealtatuss(String fileStatus00, String fileStatus01, String fileStatus03) {
		// TODO Auto-generated method stub
		return getSqlSession().getMapper(bthBatchAccountFileMapper).queryByDealtatuss(fileStatus00,fileStatus01,fileStatus03);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public int update(String statement, BthBatchAccountFile accFileInfo) {
		return getSqlSession().update(statement, accFileInfo);
	}

	@Override
	public BthBatchAccountFile selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}
	
}
