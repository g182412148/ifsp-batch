package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillOuter;
import com.scrcu.ebank.ebap.batch.bean.mapper.AliBillOuterMapper;
import com.scrcu.ebank.ebap.batch.dao.AliBillOuterDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author ljy
 * @date 2019-05-21
 */
@Repository
public class AliBillOuterDaoImpl extends BaseBatisDao implements AliBillOuterDao {

    private final Class<AliBillOuterMapper> mapper = AliBillOuterMapper.class;


    @Override
    public int insertBatch(List<AliBillOuter> outerRecordList) {
        return getSqlSession().getMapper(mapper).insertBatch(outerRecordList);
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
        return getSqlSession().getMapper(mapper).recoveryDubious(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public List<AliBillOuter> queryNotReco(Date recoDate) {
        return getSqlSession().getMapper(mapper).queryNotReco(recoDate ,  new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updateById(AliBillOuter aliBillOuter) {
        return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(aliBillOuter);
    }

    @Override
    public AliBillOuter queryByIdAndDate(String txnSsn, Date recoDate) {
        return getSqlSession().getMapper(mapper).queryByIdAndDate(txnSsn, recoDate , new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updateByResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).updateByResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }
}
