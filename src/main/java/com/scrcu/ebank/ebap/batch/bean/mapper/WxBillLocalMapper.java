package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillLocal;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface WxBillLocalMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(WxBillLocal record);

    int insertSelective(WxBillLocal record);

    WxBillLocal selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(WxBillLocal record);

    int updateByPrimaryKey(WxBillLocal record);

    /**
     * 查询指定日期中需要处理的数据量
     * @param recoDate
     * @return
     */
    int count(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(@Param("recordList") List<WxBillLocal> recordList);

    /**
     * 分页查询
     * @param recoDate
     * @return
     */
    List<WxBillLocal> queryByRange(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate, @Param("minIndex") int minIndex, @Param("maxIndex") int maxIndex);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recovery(@Param("recoDate") Date recoDate);

    /**
     * 恢复前一日可疑数据
     * @param dubiousDate
     * @return
     */
    int recoveryDubious(@Param("dubiousDate") Date dubiousDate);

    /**
     * 清空指定对账日期的数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate") Date recoDate);

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    int copy(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

    /**
     * 根据微信对账结果更新本地对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);


}