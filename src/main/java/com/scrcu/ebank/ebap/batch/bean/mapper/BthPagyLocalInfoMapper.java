package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyLocalInfo;

public interface BthPagyLocalInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(BthPagyLocalInfo record);

    int insertSelective(BthPagyLocalInfo record);

    BthPagyLocalInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(BthPagyLocalInfo record);

    int updateByPrimaryKey(BthPagyLocalInfo record);
    /**
     * 根据通道系统编号和清算日期删除BTH_PAGY_LOCAL_INFO本地通道交易明细对账表数据
     * @param pagySysNo
     * @param settleDate
     * @return
     */
    int deleteBypagySysNoAndDate(@Param("pagySysNo")String pagySysNo, @Param("settleDate")String settleDate);

	BthPagyLocalInfo queryLocalInfoByPagyPayTxnSsn(@Param("pagyPayTxnSsn") String pagyPayTxnSsn);
	/**
	 * 根据通道系统编号和清算日期查询BTH_PAGY_LOCAL_INFO本地通道交易明细对账表数据
	 * @param pagySysNo
	 * @param settleDate
	 * @return
	 */
	List<BthPagyLocalInfo> selectByDateAndPagyno(@Param("pagySysNo")String pagySysNo, @Param("settleDate")String settleDate);
	/**
	 * 根据时间状态及通道系统编号查询本地对账表信息
	 * @param settleDate
	 * @param state
	 * @param pagySysNo
	 * @return
	 */
	List<BthPagyLocalInfo> selectDateAndStat(@Param("settleDate")String settleDate, @Param("state")String state, @Param("pagySysNo")String pagySysNo);
	/**
	 * 根据时间对账状态及通道系统编号查询本地对账表信息
	 * @param doubtDate
	 * @param chkRst
	 * @param pagySysNo
	 * @return
	 */
	List<BthPagyLocalInfo> selectDateAndChkstat(@Param("doubtDate")String doubtDate, @Param("chkRst")String chkRst, @Param("pagySysNo")String pagySysNo);
	/**
	 * 
	 * @param pagySysNo
	 * @param settleDate
	 * @return
	 */
	int deleteBypagySysNoAndPagyDate(@Param("pagySysNo")String pagySysNo, @Param("settleDate")String settleDate);
	/**
	 * 
	 * @param settleDate
	 * @param state
	 * @param pagySysNo
	 * @return
	 */
	List<BthPagyLocalInfo> selectAliByDateAndStat(@Param("settleDate")String settleDate, @Param("state")String state, @Param("pagySysNo")String pagySysNo);
	/**
	 * 
	 * @param settleDate
	 * @param pagySysNo
	 * @return
	 */
	List<BthPagyLocalInfo> selectAliByDateAndPagyno(@Param("settleDate")String settleDate, @Param("pagySysNo")String pagySysNo);
}