package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillLocal;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AliBillLocalDao {

    /**
     * 支付宝抽数
     * @param txnDate
     * @param recoDate
     * @return
     */
    int copy(Date txnDate, Date recoDate);

    /**
     * 清理抽数记录
     * @param recoDate
     */
    int clear(Date recoDate);

    /**
     * 恢复本地未对账记录
     * @param recoDate
     * @return
     */
    int recovery(Date recoDate);

    /**
     * 恢复本地可疑记录
     * @param recoDate
     * @return
     */
    int recoveryDubious(Date recoDate);

    /**
     * 统计本地待对账总条数
     * @param recoDate
     * @return
     */
    int count(Date recoDate);

    /**
     * 分页查询本地流水记录
     * @param recoDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<AliBillLocal> queryByRange(Date recoDate, Integer minIndex, Integer maxIndex);

    /**
     * 根据主键更新本地流水
     * @param aliBillLocal
     * @return
     */
    int updateById(AliBillLocal aliBillLocal);

    List<AliBillLocal> selectList(String statement, Map<String,Object> map);

    /**
     * 根据支付宝对账结果更新本地对账状态
     * @param recoDate
     * @return
     */
    int updateByResult(Date recoDate);
}
