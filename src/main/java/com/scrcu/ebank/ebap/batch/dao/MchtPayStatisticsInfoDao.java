package com.scrcu.ebank.ebap.batch.dao;


import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfoResp;

import java.util.List;
import java.util.Map;

public interface MchtPayStatisticsInfoDao {

    public int insert(MchtPayStatisticsInfo record);

    public int insertSelective(MchtPayStatisticsInfo record);

    public int insertBatch(List<MchtPayStatisticsInfo> list);

    public List<MchtPayStatisticsInfoResp> selectMchtIdAndTime(String chlMchtNo, String time);

    List<MchtPayStatisticsInfoResp> queryTimeQuanTum(String mchtId, String startDate, String endDate, String timeQuanTum);

    long insertToMchtPayInfo(Map<String,Object> map);
}
