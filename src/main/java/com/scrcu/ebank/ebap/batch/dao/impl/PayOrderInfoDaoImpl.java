package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDailyTxnCount;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfo;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.PayOrderInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("payOrderInfoDao")
public class PayOrderInfoDaoImpl extends BaseBatisDao implements PayOrderInfoDao {
	private final Class<PayOrderInfoMapper> payOrderInfoMapper=PayOrderInfoMapper.class;
	
	@Override
	public ArrayList<PayOrderInfo> selectByDateAndState(String settleDate) {
		return super.getSqlSession().getMapper(payOrderInfoMapper).selectByDateAndState(settleDate);
	}

    @Override
    public PayOrderInfo selectByPagyTxnSsn(String pagyTxnSsn) {
        return super.getSqlSession().getMapper(payOrderInfoMapper).selectByPagyTxnSsn(pagyTxnSsn);
    }

	@Override
	public List<PayOrderInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement,parameter);
	}

	@Override
	public PayOrderInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement,parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public PayOrderInfo selectByPrimaryKey(String orderSsn) {
		return super.getSqlSession().getMapper(payOrderInfoMapper).selectByPrimaryKey(orderSsn);
	}

	@Override
	public PayOrderInfo queryByTxnSeqId(String txnSeqId) {
		return super.getSqlSession().getMapper(payOrderInfoMapper).queryByTxnSeqId(txnSeqId);
	}

	@Override
	public List<MchtPayStatisticsInfo> queryMchtPayStatistics(String startTime, String endTime) {
		return super.getSqlSession().getMapper(payOrderInfoMapper).queryMchtPayStatistics(startTime, endTime);
	}

    @Override
    public List<BthMerDailyTxnCount> selectDayTxnGroupByMcht(String startTm, String endTm) {
        return getSqlSession().getMapper(payOrderInfoMapper).selectDayTxnGroupByMcht(startTm,endTm);
    }

    @Override
    public List<BthMerDailyTxnCount> selectDayTxnGroupByMcht2(String startTm, String endTm) {
        return getSqlSession().getMapper(payOrderInfoMapper).selectDayTxnGroupByMcht2(startTm,endTm);
    }

    @Override
    public List<BthMerDailyTxnCount> selectDayTxnGroupByMcht3(String startTm, String endTm) {
        return getSqlSession().getMapper(payOrderInfoMapper).selectDayTxnGroupByMcht3(startTm,endTm);
    }

    @Override
    public List<PayOrderInfo> selectByTime(String startTm, String endTm) {
        return getSqlSession().getMapper(payOrderInfoMapper).selectByTime(startTm,endTm);
    }

	@Override
	public int insertSelective(PayOrderInfo payOrderInfo) {
		return getSqlSession().getMapper(payOrderInfoMapper).insertSelective(payOrderInfo);
	}

	@Override
	public List<PayOrderInfo> selectOrderListByDay(Date startDate, Date endDate, int minIndex, int maxIndex) {
		return getSqlSession().getMapper(payOrderInfoMapper).selectOrderListByDay(startDate, endDate, minIndex, maxIndex);
	}

	@Override
	public int selectOrderByDayCount(Date startDate, Date endDate) {
		return getSqlSession().getMapper(payOrderInfoMapper).selectOrderByDayCount(startDate, endDate);
	}
	@Override
	public int countOrders(String mchtId,Date startDate,Date endDate)
	{
		return getSqlSession().getMapper(payOrderInfoMapper).countOrders(mchtId,startDate, endDate);

	}

	@Override
	public int clearUntradedMerchants()
	{
		return getSqlSession().getMapper(payOrderInfoMapper).clearUntradedMerchants();

	}

	@Override
	public List<PayOrderInfo> untradedMerchants(int minIndex, int maxIndex, String orderTM)
	{
		return getSqlSession().getMapper(payOrderInfoMapper).untradedMerchants(minIndex,maxIndex,orderTM);

	}

	@Override
	public int insertBatch(List<PayOrderInfo> recordList)
	{
		return getSqlSession().getMapper(payOrderInfoMapper).insertBatch(recordList);

	}

	@Override
	public List<PayOrderInfo> queryTimeOutOrder(Date startDate, Date endDate, int minIndex, int maxIndex) {
		return getSqlSession().getMapper(payOrderInfoMapper).queryTimeOutOrder(startDate, endDate, minIndex, maxIndex);
	}
}
