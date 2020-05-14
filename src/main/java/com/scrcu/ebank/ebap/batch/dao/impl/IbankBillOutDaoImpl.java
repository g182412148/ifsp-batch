package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillOuter;
import com.scrcu.ebank.ebap.batch.bean.mapper.IbankBillOuterMapper;
import com.scrcu.ebank.ebap.batch.dao.IbankBillOutDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 本行本地流水表
 * @author ljy
 * @date 2019-05-10
 */
@Repository
public class IbankBillOutDaoImpl extends BaseBatisDao implements IbankBillOutDao {

    private final Class<IbankBillOuterMapper> ibankBillOuterMapper=IbankBillOuterMapper.class;


    @Override
    public int insertBatch(List<IbankBillOuter> recordList) {
        return getSqlSession().getMapper(ibankBillOuterMapper).insertBatch(recordList);
    }

    @Override
    public int recovery(Date recoDate) {
        return getSqlSession().getMapper(ibankBillOuterMapper).recovery(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int recoveryDubious(Date recoDate) {
        return getSqlSession().getMapper(ibankBillOuterMapper).recoveryDubious(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public IbankBillOuter queryByIdAndDate(String txnSsn, Date recoDate) {
        return getSqlSession().getMapper(ibankBillOuterMapper).queryByIdAndDate(txnSsn,recoDate,new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updateById(IbankBillOuter ibankBillOuter) {
        return getSqlSession().getMapper(ibankBillOuterMapper).updateByPrimaryKeySelective(ibankBillOuter);
    }

    @Override
    public List<IbankBillOuter> queryNotReco(Date recoDate) {
        return getSqlSession().getMapper(ibankBillOuterMapper).queryNotReco(recoDate,new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int clear(Date recoDate) {
        return getSqlSession().getMapper(ibankBillOuterMapper).clear(recoDate);
    }

    @Override
    public int updateByResult(Date recoDate) {
        return getSqlSession().getMapper(ibankBillOuterMapper).updateByResult(recoDate,new DateTime(recoDate).minusDays(1).toDate());
    }
}
