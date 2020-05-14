package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillResult;
import com.scrcu.ebank.ebap.batch.bean.mapper.IbankBillResultMapper;
import com.scrcu.ebank.ebap.batch.dao.IbankBillResultDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class IbankBillResultDaoImpl extends BaseBatisDao implements IbankBillResultDao {

    private final Class<IbankBillResultMapper> mapper = IbankBillResultMapper.class;

    @Override
    public int insert(IbankBillResult record) {
        return getSqlSession().getMapper(mapper).insert(record);
    }

    @Override
    public int insertSelective(IbankBillResult record) {
        return getSqlSession().getMapper(mapper).insertSelective(record);
    }

    @Override
    public int insertIBankResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).insertIBankResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
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
