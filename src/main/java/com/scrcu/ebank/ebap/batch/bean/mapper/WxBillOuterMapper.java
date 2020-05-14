package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillOuter;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WxBillOuterMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(WxBillOuter record);

    int insertSelective(WxBillOuter record);

    WxBillOuter selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(WxBillOuter record);

    int updateByPrimaryKey(WxBillOuter record);

    /**
     * 查询指定日期的未对账数据
     * @param recoDate 对账日期
     * @param dubiousDate 可疑日期
     * @return
     */
    List<WxBillOuter> queryNotReco(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recovery(@Param("recoDate") Date recoDate);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(@Param("recordList") List<WxBillOuter> recordList);

    /**
     * 清空指定对账日期的数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate") Date recoDate);

    /**
     * 查询明细: 根据分区日期和流水号
     * @param txnSsn
     * @param recoDate
     * @return
     */
    WxBillOuter queryByIdAndDate(@Param("txnSsn") String txnSsn, @Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 恢复前一日可疑数据
     * @param dubiousDate
     * @return
     */
    int recoveryDubious(@Param("dubiousDate") Date dubiousDate);

    /**
     * 查询对账日期下交易状态为撤销的,将其原交易的对账状态更新为无需参与对账
     * @param recoDate
     * @return
     */
    int updateSrcByRevoked(@Param("recoDate")Date recoDate);

    /**
     * 根据微信对账结果更新三方对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);
    Map<String, String> getUpacpOrderId(@Param("pagyTxnSsn")String pagyTxnSsn);
    Map<String, String> getCupQrcOrderId(@Param("pagyTxnSsn")String pagyTxnSsn);
    Map<String, String> getAlipayOrderId(@Param("pagyTxnSsn")String pagyTxnSsn);
    Map<String, String> getWechatOrderId(@Param("pagyTxnSsn")String pagyTxnSsn);
}