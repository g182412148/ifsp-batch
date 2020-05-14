package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.CoreBillInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillLocal;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CoreBillInfoMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(CoreBillInfo record);

    int insertSelective(CoreBillInfo record);

    CoreBillInfo selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(CoreBillInfo record);

    int updateByPrimaryKey(CoreBillInfo record);

    /**
     * 查询指定日期的未对账数据
     * @param recoDate 对账日期
     * @param dubiousDate 可疑日期
     * @return
     */
    List<CoreBillInfo> queryNotReco(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recovery(@Param("recoDate") Date recoDate);
    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recoveryDubious(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(@Param("recordList") List<CoreBillInfo> recordList);

    /**
     * 清空指定对账日期的数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate") Date recoDate);
    /**
     * 查询指定日期中需要处理的数据量
     * @param recoDate
     * @return
     */
    int count(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 分页查询
     * @param recoDate
     * @return
     */
    List<CoreBillInfo> queryByRange(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate, @Param("minIndex") int minIndex, @Param("maxIndex") int maxIndex);

    /**
     * 更新对账成功数据
     * @param record
     * @return
     */
    int updateSucState(CoreBillInfo record);

}