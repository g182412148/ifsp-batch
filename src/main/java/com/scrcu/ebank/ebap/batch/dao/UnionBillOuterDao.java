package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillOuter;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019-05-10.
 */
public interface UnionBillOuterDao {
    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(List<UnionBillOuter> recordList);

    /**
     * 根据流水号查询
     * @param txnSsn
     * @return
     */
    UnionBillOuter queryById(String txnSsn);

    /**
     * 根据流水号和时间查询
     * @param settleKey
     * @return
     */
    UnionBillOuter selectByPrimaryKeyDate(String settleKey, Date recoDate) ;

    /**
     * 根据流水号和时间查询
     * @param txnSsn
     * @return
     */
    UnionBillOuter selectByOrderId(String txnSsn, Date recoDate) ;


    /**
     * 更新
     * @param updBean
     * @return
     */
    int updateById(UnionBillOuter updBean);

    /**
     * 查询指定对账日期的未对账数据
     * @param recoDate
     */
    List<UnionBillOuter> queryNotReco(Date recoDate, String pagyNo);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     */
    int recovery(Date recoDate, String pagyNo);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     */
    int recoveryDubious(Date recoDate, String pagyNo);

    /**
     * 清空指定对账日期的数据
     * @param date
     * @return
     */
    int clear(Date date);

    /**
     * 根据银联二维码对账结果更新三方对账状态
     * @param recoDate
     * @return
     */
    int updateByUnionQrcResult(Date recoDate);

    /**
     * 根据银联全渠道对账结果更新三方对账状态
     * @param recoDate
     * @return
     */
    int updateByUnionAllResult(Date recoDate);

    /**
     * 查询银联账单中某种类型(冲正)的交易
     * @param recoDate
     * @param txnType
     * @return
     */
    List<UnionBillOuter> queryBill(Date recoDate,String txnType);

    /**
     * 根据系统跟踪号与交易日期查询原交易
     * @param recoDate
     * @param orgTranceNum
     * @param orgTransDate
     * @return
     */
    UnionBillOuter queryOrigBill(Date recoDate,String orgTranceNum,String orgTransDate);

    /**
     * 根据系统跟踪号与交易日期查询某类型(冲正撤销)交易
     * @param recoDate
     * @param cancelTxnType
     * @param tranceNum
     * @param transDate
     * @return
     */
    List<UnionBillOuter> queryCancelBill(Date recoDate,String cancelTxnType,String tranceNum,String transDate);
}
