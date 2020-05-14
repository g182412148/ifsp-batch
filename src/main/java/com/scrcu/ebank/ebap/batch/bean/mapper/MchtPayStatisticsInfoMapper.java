package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfoResp;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MchtPayStatisticsInfoMapper {

    int insert(MchtPayStatisticsInfo record);

    int insertSelective(MchtPayStatisticsInfo record);

    int insertBatch(List<MchtPayStatisticsInfo> list);

    List<MchtPayStatisticsInfoResp> selectMchtIdAndTime(@Param("chlMchtNo") String chlMchtNo, @Param("time") String time);

    List<MchtPayStatisticsInfoResp> queryTimeQuanTum(@Param("chlMchtNo") String chlMchtNo, @Param("startDate") String startDate,
                                         @Param("endDate") String endDate, @Param("timeQuanTum") String timeQuanTum);
    long insertToMchtPayInfo(Map<String,Object> map);
}