package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccSumInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccSumInfoExtend;
import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccVo;

public interface BthMerInAccMapper {
    int deleteByPrimaryKey(@Param("dateStlm") String dateStlm, @Param("txnSsn") String txnSsn);

    int insert(BthMerInAcc record);

    int insertSelective(BthMerInAcc record);

    BthMerInAcc selectByPrimaryKey(@Param("dateStlm") String dateStlm, @Param("txnSsn") String txnSsn);

    int updateByPrimaryKeySelective(BthMerInAcc record);

    int updateByPrimaryKey(BthMerInAcc record);

	BthMerInAcc queryMerInAccByMchtAndInAcctDate(@Param("mchtId") String mchtId,@Param("qryDate") String qryDate);

    void deleteByStlmDate(@Param("stlmDate") String stlmDate);

    List<BthMerInAcc> selectByInAcctType(@Param("inAcctType") String inAcctType);

    List<BthMerInAcc> selectByInAcctTypeFlag(@Param("inAcctType")String inAcctType);


    BthMerInAcc queryByTxnSSn(String reserved);

    List<BthMerInAcc> selectByInAcctType2(@Param("inAcctType1")String inAcctType1,  @Param("inAcctType2")String inAcctType2);

    void updateHandleState(@Param("dateStlm") String dateStlm, @Param("txnSsn")String txnSsn,@Param("handleState") String handleState, @Param("handleMark")String handleMark);

    List<BthMerInAccSumInfo> selectMerInAccSumInfo(Map<String, Object> params);

    BthMerInAccSumInfoExtend selectByBatchNo(String batchNo);

	BthMerInAcc queryAccState(@Param("mchtId") String mchtId,@Param("batchNo") List<String> batchNo);

	List<BthMerInAcc> queryCommissionAmt(@Param("mchtId") String mchtId,@Param("inAcctDate") String inAcctDate);

    BthMerInAccSumInfoExtend selectByTxnSsn(String txnSsn);

    int batchUpdate(@Param("updList")List<BthMerInAcc> updList);

    int batchUpdateHandleState(@Param("updList")List<BthMerInAcc> updList);

    int updateBySsn(BthMerInAcc bthMerInAcc);

    String getBatchNo(String dateStlm);

    int updateBthMerInAccDaoT0ForTxnSsn(BthMerInAcc bthMerInAcc);

    BthMerInAcc getTxnSsn(Map<String,String> map);

    int insertT0(BthMerInAcc record);

    int updateByPrimaryKeySelectiveT0(BthMerInAcc record);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(@Param("recordList")List<BthMerInAcc> recordList);

    int insertBatchNo(@Param("cleaTime")String cleaTime, @Param("batchNo")String batchNo);

    BthMerInAcc checkInAccAmt(@Param("batchNo")String batchNo);

    /**
     * 查询服务商入账信息
     * @param inAcctType
     * @param inAcctStat
     * @param entryType
     * @return
     */
    List<BthMerInAcc> queryServeMchtInAcct(@Param("inAcctType") String inAcctType,
                                           @Param("inAcctStat") String inAcctStat,@Param("entryType") String entryType);

    int batchUpdateForPartern(@Param("updList")List<BthMerInAcc> updList);
    BthMerInAcc otherSetlFeeSumInfo(@Param("mchtId") String mchtId,@Param("inAcctDate") String inAcctDate);

    int cleaOther();

    int initOtherData();
}
