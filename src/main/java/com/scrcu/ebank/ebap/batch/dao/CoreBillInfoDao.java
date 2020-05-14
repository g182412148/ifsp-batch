package com.scrcu.ebank.ebap.batch.dao;


import com.scrcu.ebank.ebap.batch.bean.dto.CoreBillInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019-05-15.
 */
public interface CoreBillInfoDao {
    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(List<CoreBillInfo> recordList);

    /**
     * 根据流水号查询
     * @param txnSsn
     * @return
     */
    CoreBillInfo queryById(String txnSsn);


    /**
     * 更新
     * @param updBean
     * @return
     */
    int updateById(CoreBillInfo updBean);

    /**
     * 更新对账成功数据
     * @param updBean
     * @return
     */
    int updateSucState(CoreBillInfo updBean);


    /**
     * 查询指定对账日期的未对账数据
     * @param recoDate
     */
    List<CoreBillInfo> queryNotReco(Date recoDate);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     */
    int recovery(Date recoDate);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     */
    int recoveryDubious(Date recoDate);

    /**
     * 清空指定对账日期的数据
     * @param date
     * @return
     */
    int clear(Date date);

    /**
     * 查询指定日期中需要处理的数据量
     * @param recoDate 对账日期
     * @return
     */
    int count(Date recoDate);

    /**
     * 批量更新
     * @param recordList
     * @return
     */
    int updBatch(List<CoreBillInfo> recordList);

    /**
     * 分页查询询指定日期的数据
     * @param recoDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<CoreBillInfo> queryByRange(Date recoDate, int minIndex, int maxIndex);

}
