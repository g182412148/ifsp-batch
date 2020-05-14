package com.scrcu.ebank.ebap.batch.dao.impl;/**
 * Created by Administrator on 2019-05-15.
 */

import com.scrcu.ebank.ebap.batch.bean.dto.CoreBillInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.CoreBillInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.CoreBillInfoDao;
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
 * 日期：2019-05-15 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Repository
@Slf4j
public class CoreBillInfoDaoImp extends BaseBatisDao implements CoreBillInfoDao {
    private final Class<CoreBillInfoMapper> mapper = CoreBillInfoMapper.class;

    @Override
    public int insertBatch(List<CoreBillInfo> recordList) {
        return getSqlSession().getMapper(mapper).insertBatch(recordList);
    }

    @Override
    public CoreBillInfo queryById(String txnSsn) {
        return getSqlSession().getMapper(mapper).selectByPrimaryKey(txnSsn);
    }

    @Override
    public int updateById(CoreBillInfo updBean) {
        return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(updBean);
    }

    @Override
    public int updateSucState(CoreBillInfo updBean) {
        return getSqlSession().getMapper(mapper).updateSucState(updBean);
    }

    @Override
    public List<CoreBillInfo> queryNotReco(Date recoDate) {
        return getSqlSession().getMapper(mapper).queryNotReco(recoDate, new DateTime(recoDate).minusDays(1).toDate());
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
    public int clear(Date recoDate) {
        return getSqlSession().getMapper(mapper).clear(recoDate);
    }

    @Override
    public int count(Date recoDate) {
        return getSqlSession().getMapper(mapper).count(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updBatch(List<CoreBillInfo> recordList) {
        return 0;
    }

    @Override
    public List<CoreBillInfo> queryByRange(Date recoDate, int minIndex, int maxIndex) {
        return getSqlSession().getMapper(mapper).queryByRange(recoDate, new DateTime(recoDate).minusDays(1).toDate(), minIndex, maxIndex);
    }
}
