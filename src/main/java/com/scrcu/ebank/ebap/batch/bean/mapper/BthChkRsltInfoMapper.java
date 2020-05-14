package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;

public interface BthChkRsltInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(BthChkRsltInfo record);

    int insertSelective(BthChkRsltInfo record);

    BthChkRsltInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(BthChkRsltInfo record);

    int updateByPrimaryKey(BthChkRsltInfo record);
    /**
     * 通过通过系统编号和清算日期查询对账结果信息
     * @param pagySysNo
     * @param settleDate
     * @return
     */
	List<BthChkRsltInfo> queryByPagySysNoAndDate(@Param("pagySysNo")String pagySysNo,@Param("settleDate")String settleDate);


	List<BthChkRsltInfo> selectBthChkRsltInfoByPagySysNoAndSettleDate(@Param("pagySysNo") String pagySysNo, @Param("settleDate") String settleDate);


    /**
     * 根据清算日期查询对账结果信息
     * @param settleDate
     * @return
     */
    List<BthChkRsltInfo> queryByStlmDate(@Param("settleDate") String settleDate);
    /**
     * 通过通过系统编号和清算日期删除对账结果信息
     * @param pagySysNo
     * @param settleDate
     * @return
     */
	int deleteByStlmDateAndPagySysNo(@Param("pagySysNo")String pagySysNo,@Param("settleDate")String settleDate);
	/**
	 * 
	 * @param pagySysNo
	 * @param settleDate
	 * @return
	 */
	int deleteAliByStlmDateAndPagySysNo(@Param("pagySysNo")String pagySysNo,@Param("settleDate")String settleDate);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(@Param("recordList") List<BthChkRsltInfo> recordList);

    /**
     * 根据通道系统编号与对账日期查询通道流水号
     * @param pagySysNo
     * @param recoDate
     * @return
     */
    List<String> queryByPagySysNoAndSuccDate(@Param("pagySysNo")String pagySysNo,@Param("recoDate") Date recoDate);

    int updateByOrderSsnSt(BthChkRsltInfo record);

    List<BthChkRsltInfo> selectListByStlSt(@Param("chkSuccDt")String chkSuccDt, @Param("stlmSt")String stlmSt);

    List<BthChkRsltInfo> selectChkSuccOrderByDateByRange(@Param("chkSuccDt")String chkSuccDt, @Param("minIndex")int minIndex, @Param("maxIndex")int maxIndex);
    List<BthChkRsltInfo> selectChkSuccOrderByDateByRangeTemp(@Param("chkSuccDt")String chkSuccDt, @Param("minIndex")int minIndex, @Param("maxIndex")int maxIndex);

    /**
     * 根据微信对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertWxChkResult(@Param("recoDate") Date recoDate);

    /**
     * 根据支付宝对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertAliChkResult(@Param("recoDate") Date recoDate);

    /**
     * 根据本行对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertIBankChkResult(@Param("recoDate") Date recoDate);

    /**
     * 根据银联二维码对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertUnionQrcChkResult(@Param("recoDate") Date recoDate);

    /**
     * 根据银联全渠道对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertUnionAllChkResult(@Param("recoDate") Date recoDate);
}