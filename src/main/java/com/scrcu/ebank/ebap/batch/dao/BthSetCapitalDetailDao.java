package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalDetail;


public interface BthSetCapitalDetailDao {

    int deleteByPrimaryKey(String subOrderSsn);

    int insert(BthSetCapitalDetail record);

    int insertSelective(BthSetCapitalDetail record);

    BthSetCapitalDetail selectByPrimaryKey(String subOrderSsn);

    int updateByPrimaryKeySelective(BthSetCapitalDetail record);

    int updateByPrimaryKey(BthSetCapitalDetail record);

    List<BthSetCapitalDetail> selectList(String statement,Map<String,Object> parameter);
    
    List<String> selectOrderIdList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    BthSetCapitalDetail selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);

	List<BthSetCapitalDetail> querybthMerInAccDtlByWhere(String chlMerId, Set<String> entryType,
                                                         String pch, String outAcctNo, String inAcctNo, String outAcctNoOrg, String inAcctNoOrg);

    List<String> selectByBatchNo(String batchNo);

	List<BthSetCapitalDetail> queryTranAmountAndentryType(String mchtId, List<String> batchNo);

    List<BthSetCapitalDetail> queryByAccNo(String outAcctNo, String inAcctNo, String pch);

    List<BthSetCapitalDetail> queryByDate(String dateStr);

    int batchUpdate(List<BthSetCapitalDetail> updList);

    /**
     * 批量插入
     * @param insertList
     * @return
     */
    int insertBatch(List<BthSetCapitalDetail> insertList);
    int insertBatchTempTable(List<BthSetCapitalDetail> insertList,String pagyNo);

    int updateFailureT0(BthSetCapitalDetail bthSetCapitalDetail);

    BthSetCapitalDetail selectClearSumInfoMchtInT0(Map map);

    public int delete(String statement, Map<String, Object> parameter);

    BthSetCapitalDetail queryByOrderEntry(String orderId,String entryType);

    int batchUpdateForPartern(List<BthSetCapitalDetail> updList);
}
