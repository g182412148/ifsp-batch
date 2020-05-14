package com.scrcu.ebank.ebap.batch.task;

import com.scrcu.ebank.ebap.batch.service.MchtTxnCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @author: ljy
 * @create: 2018-09-07 11:28
 */
@Slf4j
@Controller
public class MerDailyTxnCountTask{


    @Resource
    private MchtTxnCountService mchtTxnCountService;

    /**
     * 每天凌晨 00:01 统计昨日商户交易数据
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void merDailyTxnCount() throws Exception {
        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~商户日交易统计定时任务开始~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        int count = mchtTxnCountService.merDailyTxnCount();
        log.info("~~~~~~~~~~~~~~~商户日交易统计定时任务结束：共更新" + count + "条~~~~~~~~~~~~~~");
    }


    /**
     * 每天凌晨 00:01 统计昨日商户交易数据以金额分段
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void merAmtSection() throws Exception {
        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~商户日交易统计金额分段定时任务开始~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        int count = mchtTxnCountService.merAmtSection();
        log.info("~~~~~~~~~~~~~~~商户日交易统计金额分段定时任务结束：共更新" + count + "条~~~~~~~~~~~~~~");
    }

}
