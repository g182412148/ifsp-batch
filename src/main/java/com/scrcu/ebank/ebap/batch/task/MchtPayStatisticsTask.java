package com.scrcu.ebank.ebap.batch.task;

import com.scrcu.ebank.ebap.batch.service.MchtTransactionStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * 交易统计定时任务
 */
@Controller
@Slf4j
public class MchtPayStatisticsTask {

    @Resource
    MchtTransactionStatisticsService mchtTransactionStatisticsService;

    /**
     * 加载缓存任务 (每天凌晨0点开始，每隔两小时执行一次)
     * @throws Exception
     *
     */
    @Scheduled(cron = "0 0 0,2,4,6,8,10,12,14,16,18,20,22 * * ?")
    public void mchtPayStatistics() throws Exception {
        log.info("商户交易统计定时任务开始：");
        long count = mchtTransactionStatisticsService.getMchtPayStatistics();
        log.info("商户交易统计定时任务结束：共更新" + count + "条。");
    }


}
