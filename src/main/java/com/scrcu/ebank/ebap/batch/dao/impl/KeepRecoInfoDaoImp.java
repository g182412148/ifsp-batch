package com.scrcu.ebank.ebap.batch.dao.impl;/**
 * Created by Administrator on 2019-05-19.
 */

import com.scrcu.ebank.ebap.batch.bean.dto.KeepRecoInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.KeepRecoInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.KeepRecoInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.log4j.Log4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-05-19 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Repository
@Log4j
public class KeepRecoInfoDaoImp extends BaseBatisDao implements KeepRecoInfoDao {

    private final Class<KeepRecoInfoMapper> mapper = KeepRecoInfoMapper.class;

    @Override
    public int recovery(Date recoDate) {
        return getSqlSession().getMapper(mapper).recovery(recoDate);
    }

    @Override
    public int recoveryDubious(Date recoDate) {
        return getSqlSession().getMapper(mapper).recoveryDubious(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updateById(KeepRecoInfo updBean) {
        return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(updBean);
    }

    @Override
    public KeepRecoInfo queryByIdOfDate(String txnSsn, Date recoDate, Date doubtDate) {
        return getSqlSession().getMapper(mapper).queryByIdOfDate(txnSsn, recoDate, doubtDate);
    }

    @Override
    public List<KeepRecoInfo> queryNotReco(Date recoDate) {
        return getSqlSession().getMapper(mapper).queryNotReco(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int count(Date recoDate) {
        return getSqlSession().getMapper(mapper).count(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updBatch(List<KeepRecoInfo> recordList) {
        return 0;
    }

    @Override
    public List<KeepRecoInfo> queryByRange(Date recoDate, int minIndex, int maxIndex) {
        return getSqlSession().getMapper(mapper).queryByRange(recoDate, new DateTime(recoDate).minusDays(1).toDate(), minIndex, maxIndex);
    }

    @Override
    public int clear(Date recoDate) {
        return getSqlSession().getMapper(mapper).clear(recoDate);
    }

    @Override
    public int copy(Date startDate, Date endDate) {
        return getSqlSession().getMapper(mapper).copy(startDate, endDate);
    }
}
