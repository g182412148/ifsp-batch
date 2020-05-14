package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillLocal;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface IbankBillLocalMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(IbankBillLocal record);

    int insertSelective(IbankBillLocal record);

    IbankBillLocal selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(IbankBillLocal record);

    int updateByPrimaryKey(IbankBillLocal record);

    /**
     * 复制表
     * @param txnDate
     * @param recoDate
     * @return
     */
    int copy(@Param("txnDate")Date txnDate, @Param("recoDate")Date recoDate);

    /**
     * 清楚本地流水表数据
     * @param recoDate
     */
    int clear(@Param("recoDate")Date recoDate);

    /**
     * 恢复本地对账状态
     * @param recoDate
     * @param dubiousDate
     */
    int recovery(@Param("recoDate") Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 分页查询本地流水
     * @param recoDate
     * @param dubiousDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<IbankBillLocal> queryByRange(@Param("recoDate")Date recoDate,@Param("dubiousDate") Date dubiousDate,
                                      @Param("minIndex") Integer minIndex,@Param("maxIndex") Integer maxIndex);

    /**
     * 恢复本地可疑状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int recoveryDubious(@Param("recoDate")Date recoDate, @Param("dubiousDate")Date dubiousDate);

    /**
     * 统计本地待对账数量
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int count(@Param("recoDate")Date recoDate, @Param("dubiousDate")Date dubiousDate);

    /**
     * 表复制退款
     * @param txnDate
     * @param recoDate
     * @return
     */
    int copyReturn(@Param("txnDate")Date txnDate,@Param("recoDate") Date recoDate);

    /**
     * 根据本行对账结果更新本地对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);
}