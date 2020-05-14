package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillOuter;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UnionBillOuterMapper {
    int insert(UnionBillOuter record);

    int insertSelective(UnionBillOuter record);

    int deleteByPrimaryKey(String txnSsn);

    UnionBillOuter selectByPrimaryKey(String txnSsn);

    UnionBillOuter selectByPrimaryKeyDate(@Param("txnSsn") String txnSsn, @Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    UnionBillOuter selectByOrderId(@Param("orderId") String orderId, @Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    int updateByPrimaryKeySelective(UnionBillOuter record);

    int updateByPrimaryKey(UnionBillOuter record);

    /**
     * 查询指定日期的未对账数据
     * @param recoDate 对账日期
     * @param dubiousDate 可疑日期
     * @return
     */
    List<UnionBillOuter> queryNotReco(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate, @Param("pagyNo") String pagyNo);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recovery(@Param("recoDate") Date recoDate, @Param("pagyNo") String pagyNo);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recoveryDubious(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate, @Param("pagyNo") String pagyNo);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(@Param("recordList") List<UnionBillOuter> recordList);

    /**
     * 清空指定对账日期的数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate") Date recoDate);

    /**
     * 根据银联二维码对账结果更新三方对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByUnionQrcResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 根据银联全渠道对账结果更新三方对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByUnionAllResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 查询银联账单中某种类型(冲正)的交易
     * @param recoDate
     * @param txnType
     * @return
     */
    List<UnionBillOuter> queryBill(@Param("recoDate") Date recoDate,@Param("txnType")String txnType);

    /**
     * 根据系统跟踪号与交易日期查询原交易
     * @param recoDate
     * @param orgTranceNum
     * @param orgTransDate
     * @return
     */
    UnionBillOuter queryOrigBill(@Param("recoDate") Date recoDate,@Param("orgTranceNum") String orgTranceNum,@Param("orgTransDate") String orgTransDate);

    /**
     * 根据系统跟踪号与交易日期查询某类型(冲正撤销)交易
     * @param recoDate
     * @param cancelTxnType
     * @param tranceNum
     * @param transDate
     * @return
     */
    List<UnionBillOuter> queryCancelBill(@Param("recoDate") Date recoDate,@Param("cancelTxnType")String cancelTxnType,@Param("tranceNum") String tranceNum,@Param("transDate") String transDate);
}