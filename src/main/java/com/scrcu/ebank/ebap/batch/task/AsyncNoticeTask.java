package com.scrcu.ebank.ebap.batch.task;

import com.scrcu.ebank.ebap.batch.service.AsyncNoticeTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * 不再使用!!!  2019-04-09
 *
 *
 * 异步通知记账结果定时任务
 * @author: ljy
 * @create: 2018-09-04 22:55
 */
@Controller
@Slf4j
public class AsyncNoticeTask {

//    /**
//     * 每2分钟扫描记账表,通知记账结果
//     */
//    @Resource
//    private AsyncNoticeTaskService asyncNoticeTaskService;
//
//    @Scheduled(cron = "0 0/2 * * * ?")
//    public void asyncNoticeTask(){
//        log.info("~~~~~~~~~~~~~~记账结果通知定时任务开始~~~~~~~~~~~~~~~");
//        asyncNoticeTaskService.asyncNotice();
//        log.info("~~~~~~~~~~~~~~记账结果通知定时任务结束~~~~~~~~~~~~~~~");
//    }


}
