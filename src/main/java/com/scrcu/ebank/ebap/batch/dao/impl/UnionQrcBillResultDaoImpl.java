package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionQrcBillResult;
import com.scrcu.ebank.ebap.batch.bean.mapper.UnionQrcBillResultMapper;
import com.scrcu.ebank.ebap.batch.dao.UnionQrcBillResultDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class UnionQrcBillResultDaoImpl extends BaseBatisDao implements UnionQrcBillResultDao {
    private final Class<UnionQrcBillResultMapper> mapper = UnionQrcBillResultMapper.class;

    @Override
    public int insert(UnionQrcBillResult record) {
        return getSqlSession().getMapper(mapper).insert(record);
    }

    @Override
    public int insertSelective(UnionQrcBillResult record) {
        return getSqlSession().getMapper(mapper).insertSelective(record);
    }

    @Override
    public int insertUnionQrcResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).insertUnionQrcResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
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
