package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.BthWxFileDet;

public interface BthWxFileDetMapper {
    int deleteByPrimaryKey(@Param("orderNo")String orderNo,@Param("orderTp")String orderTp);

    int insert(BthWxFileDet record);

    int insertSelective(BthWxFileDet record);

    BthWxFileDet selectByPrimaryKey(@Param("orderNo")String orderNo,@Param("orderTp")String orderTp);

    int updateByPrimaryKeySelective(BthWxFileDet record);

    int updateByPrimaryKey(BthWxFileDet record);
    /**
     * 根据通道编号和清算日期删除BthWxFileDet信息
     * @param pagyNo
     * @param settleDate
     * @return
     */
	int deleteBypagyNoAndDate(@Param("pagyNo")String pagyNo, @Param("settleDate")String settleDate);

	BthWxFileDet queryWxFileDetByOrderNo(@Param("pagyPayTxnSsn")String pagyPayTxnSsn);
	/**
	 * 根据日期查询明细表信息
	 * @param settleDate
	 * @return
	 */
	List<BthWxFileDet> selectByDate(String settleDate);
	/**
	 * 根据日期和状态查询微信信息
	 * @param settleDate
	 * @param state
	 * @return
	 */
	List<BthWxFileDet> selectByDateAndStat(@Param("settleDate")String settleDate, @Param("state")String state);
	/**
	 * 根据日期和对账状态查询微信信息
	 * @param doubtDate
	 * @param chkSt
	 * @return
	 */
	List<BthWxFileDet> selectByDateAndChkstat(@Param("doubtDate")String doubtDate, @Param("chkSt")String chkSt);

    /**
     * 根据文件商户订单号更新文件表状态
     * @param orderNo
     */
    void updateChkStRstByOrderNo(String orderNo);
}