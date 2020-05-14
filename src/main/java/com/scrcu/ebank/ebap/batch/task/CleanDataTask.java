package com.scrcu.ebank.ebap.batch.task;

import com.scrcu.ebank.ebap.batch.bean.dto.BackupTableConfig;
import com.scrcu.ebank.ebap.batch.dao.CleanDataDao;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


@Slf4j
public class CleanDataTask implements Runnable {

    @Resource
    private CleanDataDao cleanDataDao;

    /**
     * 线程计数器
     */
    private CountDownLatch cdl;

    private BackupTableConfig backupTableConfig;

    public CleanDataTask(CountDownLatch cdl,BackupTableConfig backupTableConfig,CleanDataDao cleanDataDao) {
        this.cdl = cdl;
        this.backupTableConfig = backupTableConfig;
        this.cleanDataDao = cleanDataDao;
    }

    @Override
    public void run() {
        try {
            long begin = System.currentTimeMillis();
            log.info("CleanDataTask线程开始执行，线程名称："+Thread.currentThread().getName()+"；" +
                    "清理表名称："+this.backupTableConfig.getTableName());

            Map<String,Object> map = new HashMap<String,Object>();
            map.put("tableName",this.backupTableConfig.getTableName());
            cleanDataDao.cleanData(map);

            long end = System.currentTimeMillis();
            log.info(Thread.currentThread().getName()+"子线程执行完成，耗时："+(end-begin));
        } catch (Exception e) {
            log.info("子线程清理增量数据任务出现异常",e);
            e.printStackTrace();
        }finally {
            cdl.countDown();
        }
    }

}
