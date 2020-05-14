package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BillRecoErr;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface BillRecoErrMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(BillRecoErr record);

    int insertSelective(BillRecoErr record);

    BillRecoErr selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(BillRecoErr record);

    int updateByPrimaryKey(BillRecoErr record);

    /**
     * 清楚指定日期的差错数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate") Date recoDate, @Param("chnlNo") String chnlNo);

    /**
     * 根据微信对账结果插入差错记录
     * @param recoDate
     * @return
     */
    int insertWxErrResult(@Param("recoDate") Date recoDate);

    /**
     * 根据支付宝对账结果插入差错记录
     * @param recoDate
     * @return
     */
    int insertAliErrResult(@Param("recoDate") Date recoDate);

    /**
     * 根据本行对账结果插入差错记录
     * @param recoDate
     * @return
     */
    int insertIBankErrResult(@Param("recoDate") Date recoDate);

    /**
     * 根据银联二维码对账结果插入差错记录
     * @param recoDate
     * @return
     */
    int insertUnionQrcErrResult(@Param("recoDate") Date recoDate);

    /**
     * 根据银联全渠道对账结果插入差错记录
     * @param recoDate
     * @return
     */
    int insertUnionAllErrResult(@Param("recoDate") Date recoDate);
}