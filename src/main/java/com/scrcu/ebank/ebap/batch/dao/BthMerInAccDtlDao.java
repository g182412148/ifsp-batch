package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.vo.MerCommissionVo;
import com.scrcu.ebank.ebap.batch.bean.vo.InAccMerVo;
import com.scrcu.ebank.ebap.batch.bean.vo.TxnCountVo;

public interface BthMerInAccDtlDao {
    void insertSelective(BthMerInAccDtl dtl) throws Exception;

    void delByStlmDate(String settleDate) throws Exception;


	List<MonthlyBill> queryByMchtAndMonth(String mchtId, String startDate, String endDate, String txnFlag);

	List<BthMerInAccDtlVo> queryByMchtAndInAcctDate(String mchtId, String qryDate, int pageNo, int pageSize);

	BthMerInAccDtlVo queryMerInAccDtlByMchtIdAndTime(String mchtId, String startDate, String endDate);

	BthMerInAccDtl selectByTxnSeqId(String orderSsn) throws Exception;

    List<BthMerInAccDtl> querybthMerInAccDtlByWhere(String chlMerId, String inAcctTime, String inAcctNo,
			String outAcctNo, String pch);

	int updateByPrimaryKeySelective(BthMerInAccDtl bthMerInAccDtl);
	
    List<BthMerInAccDtl> selectList(String statement,Map<String,Object> parameter);

    List<String> selectTxnSeqIdList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    BthMerInAccDtl selectOne(String statement,Map<String,Object> parameter);

    int update(String statement,Map<String,Object> parameter);

    int delete(String statement,Map<String,Object> parameter);

    int insert(String statement,Map<String,Object> parameter);

    BthMerInAccInfo selectByMerIdOrderType(String stlmDate, String merId, String orderType);
    
	List<DailyStatis> queryDailyStatis(String mchtId, String startDate, String endDate);

    BthMerInAccInfo sumTxnAmtFeeAmt(Map<String, Object> map);
    
	BthMerInAccDtlVo queryTotalTxnAmtAndTotalTxnCountByMchtAndMonth(String mchtId, String startDate, String endDate, String txnFlag);

    List<BthMerInAccDtlInfo> selectByBatchNo(String merId, String batchNo, PagnParam pagnParams);
	int queryCount(String mchtId, String qryDate);

    BthMerInAccMchts selectByMerTxn(Map<String,Object> map);
    List<BthMerInAccMchts> selectByMerTxnList(Map<String,Object> map);
    List<BthMerInAccMchtsDtl> selectByMerTxnDtl(Map<String,Object> map );
    List<BthMerInAccMchtsDtl> selectByMerTxnDtlDown(Map<String,Object> map );
	List<MonthlyBill> queryStateByMchtIdAndStartDateAndEndDate(String mchtId, String startDate, String endDate);

	List<BthMerInAccDtl> queryByTxnSeqIdAndPch(String orderId);

	List<BthMerInAccDtlVo> queryAccCount(String mchtId, String qryDate);

    List<BthMerTxnCountInfo> selectGroupByMchtFundType(Map<String, Object> map);

	List<BthMerInAccDtlVo> queryGSCommissionAmt(String mchtId, String qryDate);

	List<BthMerInAccDtl> querySubMchtAll(String mchtId, String qryDate);

	String queryCommissionAmt(List<String> subMchtIds, String txnTime);

    List<BthMerInAccMchtsDtl> selectByMerTxnDtlOnline(Map<String,Object> map);
    List<BthMerInAccMchtsDtl> selectByMerTxnDtlOnlineDown(Map<String,Object> map);
	List<MonthlyBill> queryByMchtAndTxnTm(String mchtId, String startDate, String endDate, String txnFlag);

    int updateByTxnSeqId(BthMerInAccDtl bthMerInAccDtl);

    List<String> queryInAccStatByMchtAndInAcctDate(String mchtId, String qryDate);

    List<MerCommissionVo> selectCommissionAmtBySubMerIdAndTxnTm(String mchtId, String startDate, String endDate, String txnFlag);

    List<MerCommissionVo> selectCommissionAmtBySubMerIdAndInAcctDate(String mchtId, String startDate, String endDate, String txnFlag);

    List<InAccMerVo> prdCountByBatchNo();

    List<TxnCountVo> txnCountAmtSumGroupByPagyNo(String runDate);

    int selectForMerNum(String runDate);

    String selectInAccEndTm(String runDate);

    List<TxnCountVo> txnCountAmtSumGroupByPagyNoOnline(String runDate);

    String countInAccAmt(String mchtId, String qryDate);

    List<InAccMerVo> prdCountFailRecord(String batchNo, String chlMerId, String statMark);

    int batchUpdate(List<BthMerInAccDtl> updList);

    int updateByOrderSsn(BthMerInAccDtl bthMerInAccDtl);

    int recovery(String createDate);

    int cleCount(String stlDate, String channel, String stlStatus);

    List<BthMerInAccDtl> getCleaDateList(String stlDate, String channel, String stlStatus, int minIndex, int maxIndex);

    List<BthMerInAccDtl> getCleaDateListTemp(String stlDate, String channel, String stlStatus, int minIndex, int maxIndex);

    List<BthMerInAccDtl> getCleaDateAllList(String stlDate, String channel);

    /**
     * 批量插入
     * @param insertList
     * @return
     */
    int insertBatch(List<BthMerInAccDtl> insertList);
    /**
     * 批量插入临时表
     * @param insertList
     * @return
     */
    int insertBatchTemp(List<BthMerInAccDtl> insertList);

    BthMerInAccInfo sumTxnAmtFeeAmtT0(Map<String, Object> map);

    int batchUpdateForT0Bak(BthMerInAccDtl orderStlInfo);

    int batchUpdateForT0(List<BthMerInAccDtl> orderStlInfoList);

    String countInAccAmtT0(String mchtId, String qryDate);

    String selectUpdateDate(String txnSeqId);

    BthMerInAccDtlVo queryMerSum(String mchtId,String startDate,String endDate);

    BthMerTxnCountInfo checkSumInAccAmtDtl(String stlmDate);
}
