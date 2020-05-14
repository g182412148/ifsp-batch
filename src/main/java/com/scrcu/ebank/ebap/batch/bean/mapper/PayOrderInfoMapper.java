package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDailyTxnCount;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface PayOrderInfoMapper {
    int deleteByPrimaryKey(String orderSsn);

    int insert(PayOrderInfo record);

    int insertSelective(PayOrderInfo record);

    PayOrderInfo selectByPrimaryKey(String orderSsn);

    int updateByPrimaryKeySelective(PayOrderInfo record);

    int updateByPrimaryKey(PayOrderInfo record);
    /**
     * 根据清算日期查询交易成功订单流水信息
     * @param settleDate
     * @return
     */
	ArrayList<PayOrderInfo> selectByDateAndState(String settleDate);

    /**
     * 根据通道返回流水号查询订单表信息
     * @param pagyTxnSsn
     * @return
     */
    PayOrderInfo selectByPagyTxnSsn(@Param("pagyTxnSsn") String pagyTxnSsn);

	PayOrderInfo queryByTxnSeqId(String txnSeqId);


    /**
     * 查询商户交易统计
     * @param startTime
     * @param endTime
     * @return
     */
	List<MchtPayStatisticsInfo> queryMchtPayStatistics(@Param("startTime") String startTime,
                                                       @Param("endTime") String endTime);

    /**
     * 商户日交易统计主扫+公众号
     * @param startTm
     * @param endTm
     * @return
     */
    List<BthMerDailyTxnCount> selectDayTxnGroupByMcht(@Param("startTm") String startTm, @Param("endTm") String endTm);

    /**
     * 商户日交易统计被扫
     * @param startTm
     * @param endTm
     * @return
     */
    List<BthMerDailyTxnCount> selectDayTxnGroupByMcht2(@Param("startTm") String startTm, @Param("endTm") String endTm);

    /**
     * 商户日交易统计退款与撤销
     * @param startTm
     * @param endTm
     * @return
     */
    List<BthMerDailyTxnCount> selectDayTxnGroupByMcht3(@Param("startTm") String startTm, @Param("endTm") String endTm);

    /**
     * 查询商户日所有交易
     * @param startTm
     * @param endTm
     * @return
     */
    List<PayOrderInfo> selectByTime(@Param("startTm") String startTm, @Param("endTm") String endTm);

    /**
     * 查询一天的交易
     * @param startDate
     * @param endDate
     * @return
     */
    List<PayOrderInfo> selectOrderListByDay(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                            @Param("minIndex") int minIndex, @Param("maxIndex") int maxIndex);

    int selectOrderByDayCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    /**
     * 通过商户号更新订单表
     * @param mchtId
     * @param merg
     * @return
     */
    int updatePayOrderInfo(@Param("mchtId") String mchtId, @Param("merg") String merg, @Param("repe") String repe);

    int countOrders(@Param("mchtId") String mchtId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    int clearUntradedMerchants();

    List<PayOrderInfo> untradedMerchants(@Param("minIndex") int minIndex, @Param("maxIndex") int maxIndex,
                                         @Param("orderTM") String orderTM);

    int insertBatch(@Param("recordList") List<PayOrderInfo> recordList);


    /**
     * 查询一定时间间隔内，一定数量的交易
     * @param startDate
     * @param endDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<PayOrderInfo> queryTimeOutOrder(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("minIndex") int minIndex, @Param("maxIndex") int maxIndex);


}