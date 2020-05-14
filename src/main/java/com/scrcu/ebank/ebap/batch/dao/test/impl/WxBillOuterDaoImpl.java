package com.scrcu.ebank.ebap.batch.dao.test.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillOuter;
import com.scrcu.ebank.ebap.batch.bean.mapper.WxBillOuterMapper;
import com.scrcu.ebank.ebap.batch.dao.test.WxBillOuterDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public class WxBillOuterDaoImpl extends BaseBatisDao implements WxBillOuterDao {

	private final Class<WxBillOuterMapper> mapper = WxBillOuterMapper.class;


	@Override
	public int insertBatch(List<WxBillOuter> recordList) {
		return getSqlSession().getMapper(mapper).insertBatch(recordList);
	}

	@Override
	public WxBillOuter queryByIdAndDate(String txnSsn, Date recoDate, Date dubiousDate) {
		return getSqlSession().getMapper(mapper).queryByIdAndDate(txnSsn, recoDate, dubiousDate);
	}

	@Override
	public WxBillOuter queryById(String txnSsn) {
		return getSqlSession().getMapper(mapper).selectByPrimaryKey(txnSsn);
	}

	@Override
	public int updateById(WxBillOuter updBean) {
		return getSqlSession().getMapper(mapper).updateByPrimaryKeySelective(updBean);
	}

	@Override
	public List<WxBillOuter> queryNotReco(Date recoDate) {
		return getSqlSession().getMapper(mapper).queryNotReco(recoDate, new DateTime(recoDate).minusDays(1).toDate());
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
    public int updateSrcByRevoked(Date recoDate) {
        return getSqlSession().getMapper(mapper).updateSrcByRevoked(recoDate);
    }

	@Override
	public int updateByResult(Date recoDate) {
		return getSqlSession().getMapper(mapper).updateByResult(recoDate, new DateTime(recoDate).minusDays(1).toDate());
	}
}
