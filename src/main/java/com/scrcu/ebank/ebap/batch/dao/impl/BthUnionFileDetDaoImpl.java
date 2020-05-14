package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthAliFileDet;
import com.scrcu.ebank.ebap.batch.bean.dto.BthUnionFileDet;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthUnionFileDetMapper;
import com.scrcu.ebank.ebap.batch.dao.BthUnionFileDetDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("BthUnionFileDetDao")
public class BthUnionFileDetDaoImpl extends BaseBatisDao implements BthUnionFileDetDao {
	private final Class<BthUnionFileDetMapper> bthUnionFileDetMapper=BthUnionFileDetMapper.class;
	
	@Override
	public int deleteBypagyNoAndDate(String pagyNo, String settleDate) {
		return super.getSqlSession().getMapper(bthUnionFileDetMapper).deleteBypagyNoAndDate(pagyNo,settleDate);
	}

	@Override
	public int insertSelectiveList(List<BthUnionFileDet> bthUnionFileDetList) {
		for (BthUnionFileDet bthUnionFileDet : bthUnionFileDetList) {
			getSqlSession().getMapper(bthUnionFileDetMapper).insertSelective(bthUnionFileDet);
		}
		return 0;
	}

	@Override
	public List<BthUnionFileDet> selectList(String statement, Map<String, Object> params) {
		return getSqlSession().selectList(statement, params);
	}

	@Override
	public int updateByPrimaryKeySelective(BthUnionFileDet bthUnionFileDet) {
		return super.getSqlSession().getMapper(bthUnionFileDetMapper).updateByPrimaryKeySelective(bthUnionFileDet);
	}


    @Override
    public void insert(BthUnionFileDet bthUnionFileDet) {
        getSqlSession().getMapper(bthUnionFileDetMapper).insert(bthUnionFileDet);
    }

    @Override
    public void deleteByPagySysNoAndDate(String pagySysNo, String settleDate) {
        getSqlSession().getMapper(bthUnionFileDetMapper).deleteByPagySysNoAndDate(pagySysNo, settleDate);
    }

	@Override
	public BthUnionFileDet selectOne(String statement, Map<String, Object> params) {
		return getSqlSession().selectOne(statement,params);
	}
}
