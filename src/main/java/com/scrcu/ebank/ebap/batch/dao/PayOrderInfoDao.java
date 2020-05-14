package com.scrcu.ebank.ebap.batch.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDailyTxnCount;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;

public interface PayOrderInfoDao {

	ArrayList<PayOrderInfo> selectByDateAndState(String settleDate);

    /**
     * 根据通道返回流水号查询订单表
     * @param pagyTxnSsn
     * @return
     */
    PayOrderInfo selectByPagyTxnSsn(String pagyTxnSsn);
    
    List<PayOrderInfo> selectList(String statement, Map<String, Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    PayOrderInfo selectOne(String statement, Map<String, Object> parameter);
    
    int update(String statement, Map<String, Object> parameter);

	PayOrderInfo selectByPrimaryKey(String txnReqSsn);

	PayOrderInfo queryByTxnSeqId(String txnSeqId);

    List<MchtPayStatisticsInfo> queryMchtPayStatistics(String startTime, String endTime);

    /**
     * 商户日交易统计主扫+公众号支付
     * @param startTm
     * @param endTm
     * @return
     */
    List<BthMerDailyTxnCount> selectDayTxnGroupByMcht(String startTm, String endTm);

    /**
     * 商户日交易统计被扫
     * @param startTm
     * @param endTm
     * @return
     */
    List<BthMerDailyTxnCount> selectDayTxnGroupByMcht2(String startTm, String endTm);

    /**
     * 商户日交易统计退款与撤销
     * @param startTm
     * @param endTm
     * @return
     */
    List<BthMerDailyTxnCount> selectDayTxnGroupByMcht3(String startTm, String endTm);

    /**
     * 查询商户日所有交易
     * @param startTm
     * @param endTm
     * @return
     */
    List<PayOrderInfo> selectByTime(String startTm, String endTm);

    int insertSelective(PayOrderInfo payOrderInfo);

    List<PayOrderInfo> selectOrderListByDay(Date startDate, Date endDate, int minIndex, int maxIndex);

    int selectOrderByDayCount(Date startDate, Date endDate);

    /**
     * 统计商户在一段时间内的成功交易量
     * @param mchtId
     * @param startDate
     * @param endDate
     * @return
     */
    int countOrders(String mchtId, Date startDate, Date endDate);

    int clearUntradedMerchants();

    List<PayOrderInfo> untradedMerchants(int minIndex, int maxIndex, String orderTM);

    int insertBatch(List<PayOrderInfo> payOrderInfoList);

    /**
     * 查询一定时间间隔内，一定数量的交易
     * @param startDate
     * @param endDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<PayOrderInfo> queryTimeOutOrder(Date startDate, Date endDate, int minIndex, int maxIndex);

}
