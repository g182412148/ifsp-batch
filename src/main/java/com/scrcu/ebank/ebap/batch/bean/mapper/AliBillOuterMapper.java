package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.AliBillOuter;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AliBillOuterMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(AliBillOuter record);

    int insertSelective(AliBillOuter record);

    AliBillOuter selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(AliBillOuter record);

    int updateByPrimaryKey(AliBillOuter record);

    int insertBatch(@Param("recordList") List<AliBillOuter> recordList);

    int clear(@Param("recoDate")Date recoDate);

    /**
     * 恢复待对账数据
     * @param recoDate
     * @return
     */
    int recovery(@Param("recoDate")Date recoDate);

    /**
     * 恢复待对账可疑数据
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int recoveryDubious(@Param("recoDate")Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 根据日期查询未对账数据
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    List<AliBillOuter> queryNotReco(@Param("recoDate")Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 根据流水号与对账日期查询三方文件表
     * @param txnSsn
     * @param recoDate
     * @return
     */
    AliBillOuter queryByIdAndDate(@Param("txnSsn")String txnSsn,@Param("recoDate") Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 根据支付宝对账结果更新三方对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);
}