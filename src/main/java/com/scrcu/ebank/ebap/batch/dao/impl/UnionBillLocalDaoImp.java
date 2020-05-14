package com.scrcu.ebank.ebap.batch.dao.impl;/**
 * Created by Administrator on 2019-05-10.
 */

import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillLocal;
import com.scrcu.ebank.ebap.batch.bean.mapper.UnionBillLocalMapper;
import com.scrcu.ebank.ebap.batch.dao.UnionBillLocalDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class UnionBillLocalDaoImp extends BaseBatisDao implements UnionBillLocalDao {

    private final Class<UnionBillLocalMapper> mapper = UnionBillLocalMapper.class;

    @Override
    public int count(Date recoDate, String pagyNo) {
        return getSqlSession().getMapper(mapper).count(recoDate, new DateTime(recoDate).minusDays(1).toDate(), pagyNo);
    }

    @Override
    public int insertBatch(List<UnionBillLocal> recordList) {
        return getSqlSession().getMapper(mapper).insertBatch(recordList);
    }

    @Override
    public List<UnionBillLocal> queryByRange(Date recoDate, int minIndex, int maxIndex, String pagyNo) {
        return getSqlSession().getMapper(mapper).queryByRange(recoDate, new DateTime(recoDate).minusDays(1).toDate(), minIndex, maxIndex, pagyNo);
    }

    @Override
    public int updateById(UnionBillLocal updBean) {
        return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(updBean);
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
    public int clear(Date recoDate, String pagyNo) {
        return getSqlSession().getMapper(mapper).clear(recoDate, pagyNo);
    }

    @Override
    public int copyQrc(Date startDate, Date endDate) {
        return getSqlSession().getMapper(mapper).copyQrc(startDate, endDate);
    }

    @Override
    public int copyOnlin(Date startDate, Date endDate) {
        return getSqlSession().getMapper(mapper).copyOnlin(startDate, endDate);
    }

    @Override
    public List<UnionBillLocal> selectList(String statement, Map<String, Object> map) {
        return getSqlSession().selectList(statement,  map);
    }

    @Override
    public int updateByUnionQrcResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).updateByUnionQrcResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

    @Override
    public int updateByUnionAllResult(Date recoDate) {
        return getSqlSession().getMapper(mapper).updateByUnionAllResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
    }

}
