package com.scrcu.ebank.ebap.batch.service;


import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfoResp;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMchtPayStatisticsRequset;
import com.scrcu.ebank.ebap.batch.bean.request.TimeQuanTumRequest;
import com.scrcu.ebank.ebap.batch.bean.response.TimeQuanTumResponse;

import java.util.List;

public interface MchtTransactionStatisticsService {

    public long getMchtPayStatistics() throws Exception;

    public List<MchtPayStatisticsInfoResp> queryMchtPayStatistics(QueryMchtPayStatisticsRequset requset);

    public TimeQuanTumResponse timeQuanTum(TimeQuanTumRequest request);


    /**
     * 判断能否执行定时任务
     * @param taskId
     * @return
     * @throws Exception
     */
    Boolean canExecute(String taskId) throws Exception;

}
