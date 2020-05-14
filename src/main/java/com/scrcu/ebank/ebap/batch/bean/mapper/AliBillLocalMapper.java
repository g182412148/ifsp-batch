package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillLocal;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AliBillLocalMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(AliBillLocal record);

    int insertSelective(AliBillLocal record);

    AliBillLocal selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(AliBillLocal record);

    int updateByPrimaryKey(AliBillLocal record);

    int copy(@Param("txnDate") Date txnDate,@Param("recoDate") Date recoDate);

    int clear(@Param("recoDate") Date recoDate);

    /**
     * 恢复可疑流水状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int recoveryDubious(@Param("recoDate") Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 恢复未对账流水状态
     * @param recoDate
     * @return
     */
    int recovery(@Param("recoDate") Date recoDate);

    /**
     * 统计对账日下待对账总条数
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int count(@Param("recoDate") Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 分页查询本地流水信息
     * @param recoDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<AliBillLocal> queryByRange(@Param("recoDate")Date recoDate,@Param("dubiousDate")Date dubiousDate, @Param("minIndex")Integer minIndex, @Param("maxIndex")Integer maxIndex);

    /**
     * 根据支付宝对账结果更新本地对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);
}