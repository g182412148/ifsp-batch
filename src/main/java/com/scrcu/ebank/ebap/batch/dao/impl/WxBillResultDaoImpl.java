package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillResult;
import com.scrcu.ebank.ebap.batch.bean.mapper.WxBillResultMapper;
import com.scrcu.ebank.ebap.batch.dao.WxBillResultDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class WxBillResultDaoImpl extends BaseBatisDao implements WxBillResultDao {

    private final Class<WxBillResultMapper> mapper = WxBillResultMapper.class;


    @Override
    public int insert(WxBillResult record) {
        return getSqlSession().getMapper(mapper).insert(record);
    }

    @Override
    public int insertSelective(WxBillResult record) {
        return getSqlSession().getMapper(mapper).insertSelective(record);
    }

    @Override
    public int insertWxResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).insertWxResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
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
