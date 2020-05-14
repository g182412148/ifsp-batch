package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillOuter;

import java.util.Date;
import java.util.List;

public interface AliBillOuterDao {

    /**
     * 批量插入支付宝文件明细
     * @param outerRecordList
     * @return
     */
    int insertBatch(List<AliBillOuter> outerRecordList);

    /**
     * 清除对账日下载的明细
     * @param recoDate
     * @return
     */
    int clear(Date recoDate);

    /**
     * 恢复待对账数据
     * @param recoDate
     * @return
     */
    int recovery(Date recoDate);

    /**
     * 恢复待对账可疑数据
     * @param recoDate
     * @return
     */
    int recoveryDubious(Date recoDate);

    /**
     * 根据日期查询仍未对账数据
     * @param recoDate
     * @return
     */
    List<AliBillOuter> queryNotReco(Date recoDate);

    /**
     * 根据主键更新三方文件流水表
     * @param aliBillOuter
     * @return
     */
    int updateById(AliBillOuter aliBillOuter);

    /**
     * 根据流水号与日期查询三方文件记录
     * @param txnSsn
     * @param recoDate
     * @return
     */
    AliBillOuter queryByIdAndDate(String txnSsn, Date recoDate);

    /**
     * 根据支付宝对账结果更新三方对账状态
     * @param recoDate
     * @return
     */
    int updateByResult(Date recoDate);
}
