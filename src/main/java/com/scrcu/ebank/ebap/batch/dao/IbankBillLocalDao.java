package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillLocal;

import java.util.Date;
import java.util.List;

/**
 * @author ljy
 * @date 2019-05-10
 */
public interface IbankBillLocalDao {


    /**
     * 从交易流水表复制
     * @param startDate
     * @param endDate
     */
    int copy(Date startDate, Date endDate);


    /**
     * 根据对账日期删除本行本地流水表
     * @param recoDate
     */
    int clear(Date recoDate);

    /**
     * 恢复本地对账状态
     * @param recoDate
     */
    int recovery(Date recoDate);

    /**
     * 统计本地
     * @param recoDate
     * @return
     */
    int count(Date recoDate);

    /**
     * 根据对账日期查询本地流水
     * @param recoDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<IbankBillLocal> queryByRange(Date recoDate, Integer minIndex, Integer maxIndex);

    /**
     * 根据对账日期恢复本地可疑状态
     * @param recoDate
     * @return
     */
    int recoveryDubious(Date recoDate);

    /**
     * 根据主键更新本行本地流水表
     * @param ibankBillLocal
     * @return
     */
    int updateById(IbankBillLocal ibankBillLocal);

    /**
     * 抽取退款流水
     * @param txnDate
     * @param recoDate
     * @return
     */
    int copyReturn(Date txnDate, Date recoDate);

    /**
     * 根据本行对账结果更新本地对账状态
     * @param recoDate
     * @return
     */
    int updateByResult(Date recoDate);
}
