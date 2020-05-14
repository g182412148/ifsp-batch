package com.scrcu.ebank.ebap.batch.task;

import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RevAccTaskExecutor implements Runnable{

    private RevAccTask revAccTask;

    /**
     * 线程停止标志
     */
    public volatile static boolean exit = false;

    private static long WAIT_TIME = 5000;

    @Override
    public void run() {
        while (!exit){
            try {
                revAccTask = (RevAccTask) IfspSpringContextUtils.getInstance().getBean("revAccTask");
                revAccTask.coreRevAcc();
            }catch (Exception e){
                log.info("异步记账冲正线程异常：",e);
            }

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                log.info("异步记账冲正线程异常:",e);
            }

        }
    }
}
