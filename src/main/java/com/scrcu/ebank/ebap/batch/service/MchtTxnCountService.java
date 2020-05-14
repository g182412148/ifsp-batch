package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDailyTxnCount;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDayAmtSum;
import com.scrcu.ebank.ebap.batch.bean.request.TxnSectionRequest;

import java.util.List;
import java.util.Map;

/**
 * @author ljy
 */
public interface MchtTxnCountService {

    /**
     * 商户交易金额分段统计
     * @param request
     * @return
     */
    Map<String, Object> queryMerTxnCount(TxnSectionRequest request);

    /**
     * 每天凌晨统计昨日商户交易数据
     * @return
     */
    int merDailyTxnCount() throws Exception;

    /**
     * 每天凌晨统计昨日商户交易数据以金额分段
     * @return
     */
    int merAmtSection() throws Exception;

    /**
     * 判断能否执行定时任务
     * @param taskId
     * @return
     * @throws Exception
     */
    Boolean canExecute(String taskId) throws Exception;

    /**
     * 事务控制入库
     * @param list
     * @param txnDate
     * @param i
     * @throws Exception
     */
    int executeDayTxn(List<BthMerDailyTxnCount> list, String txnDate, int i) throws Exception;

    /**
     *  事务控制入库
     * @param saveMap
     * @return
     * @throws Exception
     */
    int executeDayAmtSection(Map<String, BthMerDayAmtSum> saveMap) throws Exception;
}
