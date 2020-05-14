package com.scrcu.ebank.ebap.batch.dao.impl;/**
 * Created by Administrator on 2019-05-10.
 */

import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillOuter;
import com.scrcu.ebank.ebap.batch.bean.mapper.UnionBillOuterMapper;
import com.scrcu.ebank.ebap.batch.dao.UnionBillOuterDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-05-10 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Repository
@Slf4j
public class UnionBillOuterDaoImp extends BaseBatisDao implements UnionBillOuterDao {

    private final Class<UnionBillOuterMapper> mapper = UnionBillOuterMapper.class;

    @Override
    public int insertBatch(List<UnionBillOuter> recordList) {
        return getSqlSession().getMapper(mapper).insertBatch(recordList);
    }

    @Override
    public UnionBillOuter queryById(String txnSsn) {
        return getSqlSession().getMapper(mapper).selectByPrimaryKey(txnSsn);
    }

    @Override
    public UnionBillOuter selectByPrimaryKeyDate(String settleKey, Date recoDate) {
        return getSqlSession().getMapper(mapper).selectByPrimaryKeyDate(settleKey, recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public UnionBillOuter selectByOrderId(String txnSsn, Date recoDate) {
        return getSqlSession().getMapper(mapper).selectByOrderId(txnSsn, recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updateById(UnionBillOuter updBean) {
        return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(updBean);
    }

    @Override
    public List<UnionBillOuter> queryNotReco(Date recoDate, String pagyNo) {
        return getSqlSession().getMapper(mapper).queryNotReco(recoDate, new DateTime(recoDate).minusDays(1).toDate(), pagyNo);
    }

    @Override
    public int recovery(Date recoDate, String pagyNo) {
        return getSqlSession().getMapper(mapper).recovery(recoDate, pagyNo);
    }

    @Override
    public int recoveryDubious(Date recoDate, String pagyNo) {
        return getSqlSession().getMapper(mapper).recoveryDubious(recoDate, new DateTime(recoDate).minusDays(1).toDate(),pagyNo);
    }

    @Override
    public int clear(Date recoDate) {
        return getSqlSession().getMapper(mapper).clear(recoDate);
    }

    @Override
    public int updateByUnionQrcResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).updateByUnionQrcResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updateByUnionAllResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).updateByUnionAllResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public List<UnionBillOuter> queryBill(Date recoDate, String txnType) {
        return getSqlSession().getMapper(mapper).queryBill(recoDate,txnType);
    }

    @Override
    public UnionBillOuter queryOrigBill(Date recoDate, String orgTranceNum, String orgTransDate) {
        return getSqlSession().getMapper(mapper).queryOrigBill(recoDate,orgTranceNum,orgTransDate);
    }

    @Override
    public List<UnionBillOuter> queryCancelBill(Date recoDate, String cancelTxnType, String tranceNum, String transDate) {
        return getSqlSession().getMapper(mapper).queryCancelBill(recoDate,cancelTxnType,tranceNum,transDate);
    }

}
