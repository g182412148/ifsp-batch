package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillResult;
import com.scrcu.ebank.ebap.batch.bean.mapper.AliBillResultMapper;
import com.scrcu.ebank.ebap.batch.dao.AliBillResultDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class AliBillResultDaoImpl extends BaseBatisDao implements AliBillResultDao {
    private final Class<AliBillResultMapper> mapper = AliBillResultMapper.class;

    @Override
    public int insert(AliBillResult record) {
        return getSqlSession().getMapper(mapper).insert(record);
    }

    @Override
    public int insertSelective(AliBillResult record) {
        return getSqlSession().getMapper(mapper).insertSelective(record);
    }

    @Override
    public int insertAliResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).insertAliResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int clear() {
        return getSqlSession().getMapper(mapper).clear();
    }

    @Override
    public int countLocal() {
        return getSqlSession().getMapper(mapper).countLocal();
    }

    @Override
    public int countOuter() {
        return getSqlSession().getMapper(mapper).countOuter();
    }
}
