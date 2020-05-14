package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDailyTxnCount;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerTxnCountInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.WeekDay;

import org.apache.ibatis.annotations.Param;

public interface BthMerDailyTxnCountMapper {
    int deleteByPrimaryKey(@Param("mchtId") String mchtId, @Param("txnDate") Date txnDate, @Param("fundChannel") String fundChannel);

    int insert(BthMerDailyTxnCount record);

    int insertSelective(BthMerDailyTxnCount record);

    BthMerDailyTxnCount selectByPrimaryKey(@Param("mchtId") String mchtId, @Param("txnDate") Date txnDate, @Param("fundChannel") String fundChannel);

    int updateByPrimaryKeySelective(BthMerDailyTxnCount record);

    int updateByPrimaryKey(BthMerDailyTxnCount record);

    List<BthMerDailyTxnCount> selectByTmAmt(Map<String,Object> map);

	List<WeekDay> queryWeekCountAll(@Param("mchtId") String mchtId,@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("wookDate") String wookDate);
}