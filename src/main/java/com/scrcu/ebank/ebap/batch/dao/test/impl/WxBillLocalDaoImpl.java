package com.scrcu.ebank.ebap.batch.dao.test.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillLocal;
import com.scrcu.ebank.ebap.batch.bean.mapper.WxBillLocalMapper;
import com.scrcu.ebank.ebap.batch.dao.test.WxBillLocalDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Repository
public class WxBillLocalDaoImpl extends BaseBatisDao implements WxBillLocalDao {

	private final Class<WxBillLocalMapper> mapper = WxBillLocalMapper.class;

	@Override
	public int count(Date recoDate) {
		return getSqlSession().getMapper(mapper).count(recoDate, new DateTime(recoDate).minusDays(1).toDate());
	}

	@Override
	public int insertBatch(List<WxBillLocal> recordList) {
		return getSqlSession().getMapper(mapper).insertBatch(recordList);
	}

	@Override
	public List<WxBillLocal> queryByRange(Date recoDate, int minIndex, int maxIndex) {
		return getSqlSession().getMapper(mapper).queryByRange(recoDate, new DateTime(recoDate).minusDays(1).toDate(), minIndex, maxIndex);
	}

	@Override
	public int updateById(WxBillLocal updBean) {
		return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(updBean);
	}

	@Override
	public int recovery(Date recoDate) {
		return getSqlSession().getMapper(mapper).recovery(recoDate);
	}

	@Override
	public int recoveryDubious(Date dubiousDate) {
		return getSqlSession().getMapper(mapper).recoveryDubious(dubiousDate);
	}

	@Override
	public int clear(Date recoDate) {
		return getSqlSession().getMapper(mapper).clear(recoDate);
	}

	@Override
	public int copy(Date startDate, Date endDate) {
		return getSqlSession().getMapper(mapper).copy(startDate, endDate);
	}

    @Override
    public List<WxBillLocal> selectList(String statement, Map<String, Object> map) {
        return getSqlSession().selectList(statement,  map);
    }

	@Override
	public int updateByResult(Date recoDate) {
		return getSqlSession().getMapper(mapper).updateByResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
	}
}
