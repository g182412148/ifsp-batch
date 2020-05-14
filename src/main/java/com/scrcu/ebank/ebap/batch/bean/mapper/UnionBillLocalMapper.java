package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillLocal;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UnionBillLocalMapper {
    int insert(UnionBillLocal record);

    int insertSelective(UnionBillLocal record);

    UnionBillLocal selectByPrimaryKey(String txnSsn);

    UnionBillLocal selectByPrimaryKeyForSettleKey(String settleKey);

    int updateByPrimaryKeySelective(UnionBillLocal record);

    int updateByPrimaryKey(UnionBillLocal record);
    /**
     * 查询指定日期中需要处理的数据量
     * @param recoDate
     * @return
     */
    int count(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate, @Param("pagyNo") String pagyNo);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(@Param("recordList") List<UnionBillLocal> recordList);

    /**
     * 分页查询
     * @param recoDate
     * @return
     */
    List<UnionBillLocal> queryByRange(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate, @Param("minIndex") int minIndex, @Param("maxIndex") int maxIndex, @Param("pagyNo") String pagyNo);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recovery(@Param("recoDate") Date recoDate, @Param("pagyNo") String pagyNo);
    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recoveryDubious(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate, @Param("pagyNo") String pagyNo);

    /**
     * 清空指定对账日期的数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate") Date recoDate, @Param("pagyNo") String pagyNo);

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    int copyQrc(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    int copyOnlin(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

    /**
     * 根据银联二维码对账结果更新本地对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByUnionQrcResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 根据银联全渠道对账结果更新本地对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByUnionAllResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

}