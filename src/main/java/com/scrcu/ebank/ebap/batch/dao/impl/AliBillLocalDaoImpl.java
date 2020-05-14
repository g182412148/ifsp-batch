package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillLocal;
import com.scrcu.ebank.ebap.batch.bean.mapper.AliBillLocalMapper;
import com.scrcu.ebank.ebap.batch.dao.AliBillLocalDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ljy
 * @date 2019-05-21
 */
@Repository
public class AliBillLocalDaoImpl extends BaseBatisDao implements AliBillLocalDao {

    private final Class<AliBillLocalMapper> mapper = AliBillLocalMapper.class;

    @Override
    public int copy(Date txnDate, Date recoDate) {
        return getSqlSession().getMapper(mapper).copy(txnDate,recoDate);
    }

    @Override
    public int clear(Date recoDate) {
        return getSqlSession().getMapper(mapper).clear(recoDate);
    }

    @Override
    public int recovery(Date recoDate) {
        return getSqlSession().getMapper(mapper).recovery(recoDate);
    }

    @Override
    public int recoveryDubious(Date recoDate) {
        return getSqlSession().getMapper(mapper).recoveryDubious(recoDate,new DateTime(recoDate).minusDays(1).toDate() );
    }

    @Override
    public int count(Date recoDate) {
        return getSqlSession().getMapper(mapper).count(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public List<AliBillLocal> queryByRange(Date recoDate, Integer minIndex, Integer maxIndex) {
        return getSqlSession().getMapper(mapper).queryByRange(recoDate,new DateTime(recoDate).minusDays(1).toDate(), minIndex, maxIndex );
    }

    @Override
    public int updateById(AliBillLocal aliBillLocal) {
        return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(aliBillLocal);
    }

    @Override
    public List<AliBillLocal> selectList(String statement, Map<String, Object> map) {
        return getSqlSession().selectList(statement,  map);
    }

    @Override
    public int updateByResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).updateByResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }
}
