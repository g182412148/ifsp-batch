package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalDetail;

public interface BthSetCapitalDetailMapper {
    int deleteByPrimaryKey(String subOrderSsn);

    int insert(BthSetCapitalDetail record);

    int insertSelective(BthSetCapitalDetail record);

    BthSetCapitalDetail selectByPrimaryKey(String subOrderSsn);

    int updateByPrimaryKeySelective(BthSetCapitalDetail record);

    int updateByPrimaryKey(BthSetCapitalDetail record);

	List<BthSetCapitalDetail> querybthMerInAccDtlByWhere(@Param("chlMerId") String chlMerId, @Param("entryTypeSets") Set<String> entryType,
                                                         @Param("pch") String pch,@Param("outAcctNo") String outAcctNo, @Param("inAcctNo")String inAcctNo,
                                                         @Param("outAcctNoOrg") String outAcctNoOrg, @Param("inAcctNoOrg")String inAcctNoOrg);

    List<String> selectByBatchNo(String batchNo);

	List<BthSetCapitalDetail> queryTranAmountAndentryType(@Param("mchtId") String mchtId,@Param("batchNo") List<String> batchNo);

    List<BthSetCapitalDetail> queryByAccNo(@Param("outAcctNo")String outAcctNo,@Param("inAcctNo") String inAcctNo,@Param("pch") String pch);

    List<BthSetCapitalDetail> queryByDate(@Param("dateStr")String dateStr);

    int batchUpdate(@Param("updList")List<BthSetCapitalDetail> updList);

    int insertBatch(@Param("insertList")List<BthSetCapitalDetail> insertList);

    int insertBatchTempTable(@Param("insertList")List<BthSetCapitalDetail> insertList,@Param("pagyNo") String pagyNo);


    int updateFailureT0(BthSetCapitalDetail bthSetCapitalDetail);

    BthSetCapitalDetail selectClearSumInfoMchtInT0(Map map);

    BthSetCapitalDetail queryByOrderEntry(@Param("orderId")String orderId,@Param("entryType") String entryType);

    int batchUpdateForPartern(@Param("updList")List<BthSetCapitalDetail> updList);

}