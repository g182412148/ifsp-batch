package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.CoreRecoErr;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface CoreRecoErrMapper {
    int insert(CoreRecoErr record);

    int insertSelective(CoreRecoErr record);

    /**
     * 清楚指定日期的差错数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate") Date recoDate);

    /**
     * 核心可疑、单边（对账日前一天、对账日均未对上）
     * @param recoDate
     * @return
     */
    int updateCoreDubiousOrError(@Param("recoDate") String recoDate);

    /**
     * 本地可疑、单边（对账日前一天、对账日均未对上）
     * @param recoDate
     * @return
     */
    int updateLocalDubiousOrError(@Param("recoDate") String recoDate);

    /**
     * 核心单边，记录差错表（连续两天都没有关联出本地记录）
     * @param recoDate
     * @return
     */
    int insertCoreError(@Param("recoDate") String recoDate);

    /**
     * 本地单边，记录差错表（连续两天都没有关联出核心记录）
     * @param recoDate
     * @return
     */
    int insertLocalError(@Param("recoDate") String recoDate);

    /**
     * 将对账成功的核心流水更新为记账成功、对账一致
     * @param recoDate
     * @return
     */
    int updateCoreSuccess(@Param("recoDate") String recoDate);

    /**
     * 将对账成功的本地流水更新为记账成功、对账一致
     * @param recoDate
     * @return
     */
    int updateLocalSuccess(@Param("recoDate") String recoDate);

    /**
     * 根据本地流水更新记账表
     * @param recoDate
     * @return
     */
    int updateAccInfo(@Param("recoDate") String recoDate,@Param("orderTmStart") String orderTmStart,@Param("orderTmEnd") String orderTmEnd);
}