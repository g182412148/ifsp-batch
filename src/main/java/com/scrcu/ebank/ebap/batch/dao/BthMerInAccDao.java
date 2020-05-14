package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccSumInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccSumInfoExtend;
import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccVo;

public interface BthMerInAccDao {
    void deleteByStlmDate(String stlmDate);

    int insert(BthMerInAcc bthMerInAcc);

	BthMerInAcc queryMerInAccByMchtAndInAcctDate(String mchtId, String qryDate);

    List<BthMerInAcc> selectByInAcctType(String inAcctType);

    List<BthMerInAcc> selectByInAcctTypeFlag(String inAcctType1);

    BthMerInAcc queryByTxnSSn(String reserved);

	int updateByPrimaryKeySelective(BthMerInAcc bthMerInAcc);

    List<BthMerInAcc> selectByInAcctType2(String inAcctType1, String inAcctType2);

    void updateHandleState(String dateStlm, String txnSsn, String handleState, String handleMark);

    List<BthMerInAccSumInfo> selectMerInAccSumInfo(Map<String,Object> params, PagnParam pagnParams);

    BthMerInAccSumInfoExtend selectByBatchNo(String batchNo);

    List<BthMerInAcc> selectList(String statement,Map<String,Object> parameter);

    BthMerInAcc selectOne(String statement,Map<String,Object> parameter);

	BthMerInAcc queryAccState(String mchtId, List<String> batchNo);

	List<BthMerInAcc> queryCommissionAmt(String mchtId, String inAcctDate);

    int update(String statement,Map<String,Object> parameter);

	int update(String statement,BthMerInAcc merInAcc);

    public Integer count(String statement, Map<String, Object> parameter);

    BthMerInAccSumInfoExtend selectByTxnSsn(String txnSsn);

    int batchUpdate(List<BthMerInAcc> inAccList);

    int batchUpdateHandleState(List<BthMerInAcc> inAccList);

    int updateBySsn(BthMerInAcc bthMerInAcc);

    String getBatchNo(String dateStlm);

    int updateBthMerInAccDaoT0ForTxnSsn(BthMerInAcc bthMerInAccT0);

    BthMerInAcc getTxnSsn(Map<String,String> map);

    int insertT0(BthMerInAcc bthMerInAcc);

    int updateByPrimaryKeySelectiveT0(BthMerInAcc bthMerInAcc);

    /**
     * 批量插入明细表
     * @param accList
     * @return
     */
    int insertBatch(List<BthMerInAcc> accList);

    int insertBatchNo(String cleaTime, String batchNo);

    BthMerInAcc checkInAccAmt(String batchNo);

    /**
     * 查询服务商入账信息
     * @param inAcctType
     * @param inAcctStat
     * @param entryType
     * @return
     */
    List<BthMerInAcc> queryServeMchtInAcct(String inAcctType,String inAcctStat,String entryType);

    int batchUpdateForPartern(List<BthMerInAcc> updList);
    BthMerInAcc otherSetlFeeSumInfo(String mchtId, String inAcctDate);

    public int cleaOther();

    public int initOtherData();
}
