package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.*;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.vo.MerCommissionVo;
import com.scrcu.ebank.ebap.batch.bean.vo.InAccMerVo;
import com.scrcu.ebank.ebap.batch.bean.vo.TxnCountVo;
import org.apache.ibatis.annotations.Param;

public interface BthMerInAccDtlMapper {
    int deleteByPrimaryKey(BthMerInAccDtlKey key);

    int insert(BthMerInAccDtl record);

    int insertSelective(BthMerInAccDtl record);

    BthMerInAccDtl selectByPrimaryKey(BthMerInAccDtlKey key);

    int updateByPrimaryKeySelective(BthMerInAccDtl record);

    int updateByPrimaryKey(BthMerInAccDtl record);

    void delByStlmDate(@Param("settleDate") String settleDate);

    List<BthMerInAccDtlVo> queryByMchtAndInAcctDate(@Param("mchtId") String mchtId,@Param("qryDate")  String qryDate,@Param("pageNo")  int pageNo,@Param("pageSize")  int pageSize);

	List<MonthlyBill> queryByMchtAndMonth(@Param("mchtId") String mchtId, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("txnFlag") String txnFlag);

	BthMerInAccDtlVo queryMerInAccDtlByMchtIdAndTime(@Param("mchtId") String mchtId,@Param("startDate") String startDate,@Param("endDate") String endDate);

	BthMerInAccDtlVo queryMerSum(@Param("mchtId") String mchtId,@Param("startDate") String startDate,@Param("endDate") String endDate);

	BthMerInAccDtl selectByTxnSeqId(String orderSsn);

	List<BthMerInAccDtl> querybthMerInAccDtlByWhere(@Param("chlMerId") String chlMerId,@Param("inAcctTime") String inAcctTime,@Param("inAcctNo") String inAcctNo,
			@Param("outAcctNo") String outAcctNo,@Param("pch") String pch);

    BthMerInAccInfo selectByMerIdOrderType(@Param("stlmDate") String stlmDate,@Param("merId") String merId, @Param("orderType") String orderType);
    
	List<DailyStatis> queryDailyStatis(@Param("mchtId") String mchtId,@Param("startDate") String startDate,@Param("endDate") String endDate);

	BthMerInAccDtlVo queryTotalTxnAmtAndTotalTxnCountByMchtAndMonth(@Param("mchtId") String mchtId, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("txnFlag") String txnFlag);

    BthMerInAccInfo sumTxnAmtFeeAmt( Map<String, Object> map);

	int queryCount(@Param("mchtId") String mchtId,@Param("qryDate") String qryDate);

    List<BthMerInAccDtlInfo> selectByBatchNo(@Param("merId")String merId , @Param("batchNo")String batchNo);

	List<BthMerInAccDtl> queryByTxnSeqIdAndPch(@Param("txnSeqId") String txnSeqId);


    BthMerInAccMchts selectByMerTxn(Map<String,Object> map);
    List<BthMerInAccMchts> selectByMerTxnList(Map<String,Object> map);
    List<BthMerInAccMchtsDtl> selectByMerTxnDtl(Map<String,Object> map);
    List<BthMerInAccMchtsDtl> selectByMerTxnDtlDown(Map<String,Object> map);
	List<MonthlyBill> queryStateByMchtIdAndStartDateAndEndDate(@Param("mchtId") String mchtId,@Param("startDate") String startDate,@Param("endDate") String endDate);

	List<BthMerInAccDtlVo> queryAccCount(@Param("mchtId") String mchtId,@Param("qryDate") String qryDate);

    List<BthMerTxnCountInfo> selectGroupByMchtFundType(Map<String,Object> map);

	List<BthMerInAccDtlVo> queryGSCommissionAmt(@Param("mchtId") String mchtId,@Param("qryDate") String qryDate);

	List<BthMerInAccDtl> querySubMchtAll(@Param("mchtId") String mchtId,@Param("qryDate") String qryDate);

	String queryCommissionAmt(@Param("subMchtIds") List<String> subMchtIds, @Param("txnTime") String txnTime);

    List<BthMerInAccMchtsDtl> selectByMerTxnDtlOnline(Map<String,Object> map);
    List<BthMerInAccMchtsDtl> selectByMerTxnDtlOnlineDown(Map<String,Object> map);
    List<MonthlyBill> queryByMchtAndTxnTm(@Param("mchtId") String mchtId, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("txnFlag") String txnFlag);

    int updateByTxnSeqId(BthMerInAccDtl bthMerInAccDtl);

    List<String> queryInAccStatByMchtAndInAcctDate(@Param("mchtId") String mchtId,@Param("qryDate")  String qryDate);

    List<MerCommissionVo> selectCommissionAmtBySubMerIdAndTxnTm(@Param("mchtId") String mchtId, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("txnFlag")  String txnFlag);

    List<MerCommissionVo> selectCommissionAmtBySubMerIdAndInAcctDate(@Param("mchtId")String mchtId, @Param("startDate")String startDate, @Param("endDate")String endDate, @Param("txnFlag") String txnFlag);

    List<InAccMerVo> prdCountByBatchNo();

    List<TxnCountVo> txnCountAmtSumGroupByPagyNo(String runDate);

    int selectForMerNum(String runDate);

    String selectInAccEndTm(String runDate);

    List<TxnCountVo> txnCountAmtSumGroupByPagyNoOnline(String runDate);

    String countInAccAmt(@Param("mchtId") String mchtId, @Param("inAccDate") String inAccDate);

    String countInAccAmtT0(@Param("mchtId") String mchtId, @Param("inAccDate") String inAccDate);

    List<InAccMerVo> prdCountFailRecord(@Param("batchNo")String batchNo, @Param("chlMerId")String chlMerId, @Param("statMark")String statMark);

    int batchUpdate(@Param("updList")List<BthMerInAccDtl> updList);

    int updateByOrderSsn(BthMerInAccDtl bthMerInAccDtl);

    int recovery(@Param("createDate")String createDate);

    int cleCount(@Param("stlDate")String stlDate, @Param("channel")String channel, @Param("stlStatus")String stlStatus);

    List<BthMerInAccDtl> getCleaDateList(@Param("stlDate")String stlDate, @Param("channel")String channel, @Param("stlStatus")String stlStatus, @Param("minIndex")int minIndex, @Param("maxIndex")int maxIndex);
    List<BthMerInAccDtl> getCleaDateListTemp(@Param("stlDate")String stlDate, @Param("channel")String channel, @Param("stlStatus")String stlStatus, @Param("minIndex")int minIndex, @Param("maxIndex")int maxIndex);

    List<BthMerInAccDtl> getCleaDateAllList(@Param("stlDate")String stlDate, @Param("channel")String channel);

    int insertBatch(@Param("insertList")List<BthMerInAccDtl> insertList);

    int insertBatchTemp(@Param("insertList")List<BthMerInAccDtl> insertList);

    BthMerInAccInfo sumTxnAmtFeeAmtT0( Map<String, Object> map);

    int batchUpdateForT0Bak(BthMerInAccDtl bthMerInAccDtl);

    int batchUpdateForT0(@Param("bthMerInAccDtlList") List<BthMerInAccDtl> bthMerInAccDtlList);

    String selectUpdateDate(String txnSeqId);

    BthMerTxnCountInfo checkSumInAccAmtDtl(@Param("stlmDate")String stlmDate);
}