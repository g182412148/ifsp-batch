package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerHangAcc;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthMerHangAccMapper;
import com.scrcu.ebank.ebap.batch.dao.BthMerHangAccDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author: ljy
 * @create: 2018-10-09 19:50
 */
@Repository
public class BthMerHangAccDaoImpl extends BaseBatisDao implements BthMerHangAccDao {
    private final Class<BthMerHangAccMapper> bthMerHangAccMapperClass = BthMerHangAccMapper.class;


    @Override
    public void insert(BthMerHangAcc record) {
        getSqlSession().getMapper(bthMerHangAccMapperClass).insert(record);
    }


    @Override
    public List<BthMerHangAcc> selectByHangSt(String hangSt) {
        return getSqlSession().getMapper(bthMerHangAccMapperClass).selectByHangSt(hangSt);
    }

    @Override
    public BthMerHangAcc selectByPrimaryKey(String pagyPayTxnSsn) {
        return getSqlSession().getMapper(bthMerHangAccMapperClass).selectByPrimaryKey(pagyPayTxnSsn);
    }

    @Override
    public void updateHangStByKey(String pagyPayTxnSsn, Date date, String settleDate) {
        getSqlSession().getMapper(bthMerHangAccMapperClass).updateHangStByKey(pagyPayTxnSsn,date,settleDate);
    }

    @Override
    public void deleteBySettleDateIn(String settleDate) {
        getSqlSession().getMapper(bthMerHangAccMapperClass).deleteBySettleDateIn(settleDate);
    }

    @Override
    public void updateHangStBySettleDateOut(String settleDate) {
        getSqlSession().getMapper(bthMerHangAccMapperClass).updateHangStBySettleDateOut(settleDate);
    }
}
