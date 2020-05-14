package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillLocal;
import com.scrcu.ebank.ebap.batch.bean.mapper.IbankBillLocalMapper;
import com.scrcu.ebank.ebap.batch.dao.IbankBillLocalDao;
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
public class IbankBillLocalDaoImpl  extends BaseBatisDao implements IbankBillLocalDao {

    private final Class<IbankBillLocalMapper> ibankBillLocalMapper=IbankBillLocalMapper.class;


    @Override
    public int copy(Date startDate, Date endDate) {
        return getSqlSession().getMapper(ibankBillLocalMapper).copy(startDate,endDate);
    }

    @Override
    public int clear(Date recoDate) {
        return getSqlSession().getMapper(ibankBillLocalMapper).clear(recoDate);
    }

    @Override
    public int recovery(Date recoDate) {
        return getSqlSession().getMapper(ibankBillLocalMapper).recovery(recoDate,new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int count(Date recoDate) {
        return getSqlSession().getMapper(ibankBillLocalMapper).count(recoDate,new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public List<IbankBillLocal> queryByRange(Date recoDate, Integer minIndex, Integer maxIndex) {
        return getSqlSession().getMapper(ibankBillLocalMapper).queryByRange(recoDate,new DateTime(recoDate).minusDays(1).toDate(),minIndex,maxIndex);
    }

    @Override
    public int recoveryDubious(Date recoDate) {
        return getSqlSession().getMapper(ibankBillLocalMapper).recoveryDubious(recoDate,new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updateById(IbankBillLocal ibankBillLocal) {
        return getSqlSession().getMapper(ibankBillLocalMapper).updateByPrimaryKeySelective(ibankBillLocal);
    }

    @Override
    public int copyReturn(Date txnDate, Date recoDate) {
        return getSqlSession().getMapper(ibankBillLocalMapper).copyReturn(txnDate,recoDate);
    }

    @Override
    public int updateByResult(Date recoDate) {
        return getSqlSession().getMapper(ibankBillLocalMapper).updateByResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }
}
