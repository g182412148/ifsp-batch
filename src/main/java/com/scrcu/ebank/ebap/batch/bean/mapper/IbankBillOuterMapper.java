package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillOuter;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface IbankBillOuterMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(IbankBillOuter record);

    int insertSelective(IbankBillOuter record);

    IbankBillOuter selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(IbankBillOuter record);

    int updateByPrimaryKey(IbankBillOuter record);

    int insertBatch(@Param("recordList")List<IbankBillOuter> recordList);

    /**
     * 恢复本行对账文件对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int recovery(@Param("recoDate") Date recoDate,@Param("dubiousDate")  Date dubiousDate);

    /**
     * 恢复本行对账文件可疑状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int recoveryDubious(@Param("recoDate") Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 查询本地文件未对账与可疑的流水
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    List<IbankBillOuter> queryNotReco(@Param("recoDate")Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 清除本行对账文件数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate")Date recoDate);

    /**
     * 根据对账日,对账日前日以及流水号查询
     * @param txnSsn
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    IbankBillOuter queryByIdAndDate(@Param("txnSsn") String txnSsn,@Param("recoDate") Date recoDate,@Param("dubiousDate") Date dubiousDate);

    /**
     * 根据本行对账结果更新三方对账状态
     * @param recoDate
     * @param dubiousDate
     * @return
     */
    int updateByResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);
}