package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.CoreRecoErr;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019-05-19.
 */
public interface CoreRecoErrDao {
    /**
     * 插入
     * @param record
     * @return
     */
    int insert(CoreRecoErr record);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(List<CoreRecoErr> recordList);

    /**
     * 清楚指定日期的差错数据
     * @param recoDate
     */
    int clear(Date recoDate);


    /**
     * 核心可疑、单边（对账日前一天、对账日均未对上）
     * @param recoDate
     * @return
     */
    int updateCoreDubiousOrError(String recoDate);

    /**
     * 本地可疑、单边（对账日前一天、对账日均未对上）
     * @param recoDate
     * @return
     */
    int updateLocalDubiousOrError(String recoDate);

    /**
     * 核心单边，记录差错表（连续两天都没有关联出本地记录）
     * @param recoDate
     * @return
     */
    int insertCoreError(String recoDate);

    /**
     * 本地单边，记录差错表（连续两天都没有关联出核心记录）
     * @param recoDate
     * @return
     */
    int insertLocalError(String recoDate);

    /**
     * 将对账成功的核心流水更新为记账成功、对账一致
     * @param recoDate
     * @return
     */
    int updateCoreSuccess(String recoDate);

    /**
     * 将对账成功的本地流水更新为记账成功、对账一致
     * @param recoDate
     * @return
     */
    int updateLocalSuccess(String recoDate);

    /**
     * 根据本地流水更新记账表
     * @param recoDate
     * @return
     */
    int updateAccInfo(String recoDate,String orderTmStart,String orderTmEnd);
}
