package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionAllBillResult;
import com.scrcu.ebank.ebap.batch.bean.mapper.UnionAllBillResultMapper;
import com.scrcu.ebank.ebap.batch.dao.UnionAllBillResultDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class UnionAllBillResultDaoImpl extends BaseBatisDao implements UnionAllBillResultDao {

    private final Class<UnionAllBillResultMapper> mapper = UnionAllBillResultMapper.class;

    @Override
    public int insert(UnionAllBillResult record) {
        return getSqlSession().getMapper(mapper).insert(record);
    }

    @Override
    public int insertSelective(UnionAllBillResult record) {
        return getSqlSession().getMapper(mapper).insertSelective(record);
    }

    @Override
    public int insertUnionAllResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).insertUnionAllResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
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
