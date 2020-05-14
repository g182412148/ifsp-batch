package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BthChkRsltInfoDao {

	List<BthChkRsltInfo> queryByPagySysNoAndDate(String pagySysNo, String settleDate);

    List<BthChkRsltInfo> queryByStlmDate(String settleDate);

    BthChkRsltInfo selectByPrimaryKey(String pagyTxnSsn);

	int deleteByStlmDateAndPagySysNo(String settleDate, String pagySysNo);
	
    List<BthChkRsltInfo> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    BthChkRsltInfo selectOne(String statement,Map<String,Object> parameter);
    
    int update(String statement,Map<String,Object> parameter);

	int insert(BthChkRsltInfo record);

	int deleteAliByStlmDateAndPagySysNo(String settleDate, String pagySysNo);

	int updateByPrimaryKeySelective(BthChkRsltInfo bthChkRsltInfo);

    void deleteByPrimaryKey(String pagyPayTxnSsn);
    
    int delete(String statement,Map<String,Object> parameter);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(List<BthChkRsltInfo> recordList);

    int updateByOrderSsnSt(BthChkRsltInfo bthChkRsltInfo);

    List<BthChkRsltInfo> selectListByStlSt(String chkSuccDt,String stlmSt);

    //分页查询
    List<BthChkRsltInfo> selectChkSuccOrderByDateByRange(String chkSuccDt, int minIndex, int maxIndex);
    List<BthChkRsltInfo> selectChkSuccOrderByDateByRangeTemp(String chkSuccDt, int minIndex, int maxIndex);

    /**
     * 根据微信对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertWxChkResult(Date recoDate);

    /**
     * 根据支付宝对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertAliChkResult(Date recoDate);

    /**
     * 根据本行对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertIBankChkResult(Date recoDate);

    /**
     * 根据银联二维码对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertUnionQrcChkResult(Date recoDate);

    /**
     * 根据银联全渠道对账结果插入对账对平结果表
     * @param recoDate
     * @return
     */
    int insertUnionAllChkResult(Date recoDate);

}
