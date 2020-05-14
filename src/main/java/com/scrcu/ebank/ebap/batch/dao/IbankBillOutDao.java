package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillOuter;

import java.util.Date;
import java.util.List;

public interface IbankBillOutDao {
    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(List<IbankBillOuter> recordList);

    /**
     * 恢复本行对账文件表状态
     * @param recoDate
     */
    int recovery(Date recoDate);

    /**
     * 恢复本行可疑状态
     * @param recoDate
     * @return
     */
    int recoveryDubious(Date recoDate);

    /**
     * 根据流水号查询本行文件表
     * @param txnSsn
     * @param recoDate
     * @return
     */
    IbankBillOuter queryByIdAndDate(String txnSsn, Date recoDate);

    /**
     * 根据主键更新本行对账文件表
     * @param ibankBillOuter
     * @return
     */
    int updateById(IbankBillOuter ibankBillOuter);

    /**
     * 查询三方文件可疑的流水
     * @param recoDate
     * @return
     */
    List<IbankBillOuter> queryNotReco(Date recoDate);

    /**
     * 清除本行文件数据
     * @param recoDate
     * @return
     */
    int clear(Date recoDate);

    /**
     * 根据本行对账结果更新三方对账状态
     * @param recoDate
     * @return
     */
    int updateByResult(Date recoDate);
}
