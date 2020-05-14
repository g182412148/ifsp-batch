package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.ParternInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.ParternInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName ParternInfoDaoImpl
 * @Description TODO
 * @Author NiklausZhu
 * @Date 2020/4/1 11:23
 **/
@Repository("parternInfoDao")
public class ParternInfoDaoImpl extends BaseBatisDao implements ParternInfoDao{

    private Class<ParternInfoMapper> parternInfoMapper = ParternInfoMapper.class;
    @Override
    public int deleteByPrimaryKey(String parternId) {
        return getSqlSession().getMapper(parternInfoMapper).deleteByPrimaryKey(parternId);
    }

    @Override
    public int insert(ParternInfo record) {
        return getSqlSession().getMapper(parternInfoMapper).insert(record);
    }

    @Override
    public int insertSelective(ParternInfo record) {
        return getSqlSession().getMapper(parternInfoMapper).insertSelective(record);
    }

    @Override
    public ParternInfo selectByPrimaryKey(String parternId) {
        return getSqlSession().getMapper(parternInfoMapper).selectByPrimaryKey(parternId);
    }

    @Override
    public int updateByPrimaryKeySelective(ParternInfo record) {
        return getSqlSession().getMapper(parternInfoMapper).updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ParternInfo record) {
        return getSqlSession().getMapper(parternInfoMapper).updateByPrimaryKey(record);
    }

    @Override
    public List<ParternInfo> selectParternList(List<String> parternCodeList) {
        return getSqlSession().getMapper(parternInfoMapper).selectParternList(parternCodeList);
    }
}
