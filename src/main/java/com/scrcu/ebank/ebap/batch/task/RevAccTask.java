package com.scrcu.ebank.ebap.batch.task;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 记账冲正轮询任务
 * @author ljy
 */
@Slf4j
@Service
public class RevAccTask {

//    /**
//     * 处理线程数量
//     */
//    @Value("${resvKeepAcct.threadCount}")
//    private Integer threadCount;
    /**
     * 每次查询据量
     */
    @Value("${resvKeepAcct.qryCount}")
    private Integer qryCount;

    /**
     * 最大尝试次数
     */
    @Value("${resvKeepAcct.retryCount}")
    private Integer retryCount;

    /**
     * zk地址
     */
    @Value("${ZOOKEEPER_URL}")
    private String zookeeperUrl;
    /**
     * 睡眠时间
     */
    @Value("${BASE_SLEEP_TIME_MS}")
    private int baseSleepTimeMS;
    /**
     * 最大重试次数
     */
    @Value("${MAX_RETRIES}")
    private int maxRetries;
    /**
     *
     */
    @Value("${TIME}")
    private int time;

//    /**
//     * 线程池
//     */
//    ExecutorService executor;

    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    @Resource
    private KeepAccSoaService KeepAccSoaService;


    public void coreRevAcc() {

        RetryPolicy retryPolicy;
        CuratorFramework client = null;
        InterProcessMutex lock = null;
        boolean getLockfl = true;
        try {
            // STEP1 获取zk锁
            retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMS, maxRetries);
            client = CuratorFrameworkFactory.newClient(zookeeperUrl, retryPolicy);
            client.start();
            lock = new InterProcessMutex(client,Constans.rootPath + Constans.asynRevKeepAccKey);
            if (lock.acquire(time, TimeUnit.SECONDS)) {
                log.info("记账冲正轮询任务获取锁成功。。。");
                // STEP2 业务处理
                handleKeepInfo();
            }else{
                log.info("记账冲正轮询任务获取锁失败,该次定时任务结束。。。");
                getLockfl = false;
            }
        } catch (Exception e){
            log.info("记账冲正轮询任务获取锁异常[{}]",e);
            throw new IfspValidException(RespEnum.RESP_FAIL.getCode(),RespEnum.RESP_FAIL.getDesc());
        }finally {
            try {
                if(getLockfl){
                    log.info("释放锁。。。");
                    lock.release();
                    log.info("释放锁成功。。。");
                }
            } catch (IllegalMonitorStateException e) {
                log.info("没获取到锁，释放锁异常:", e);
                throw new IfspValidException(RespEnum.RESP_FAIL.getCode(),RespEnum.RESP_FAIL.getDesc());
            } catch (Exception e) {
                log.error(e.toString());
                throw new IfspValidException(RespEnum.RESP_FAIL.getCode(), RespEnum.RESP_FAIL.getDesc());
            }finally {
                if(IfspDataVerifyUtil.isNotEmpty(client)){
                    client.close();
                }
            }


        }



    }

    /**
     * 调用单笔冲正服务进行冲正, 更新返回结果
     */
    private void handleKeepInfo() {
        log.info("========================>>开始扫描记账表待冲正明细<<===============================");
        // STEP2.1 获取记账表待冲正且重试次数小于三次的记账明细 , 以订单时间排序取前500条
        List<KeepAccInfo> reco = keepAccInfoDao.queryResvRec(qryCount,retryCount);
        if (IfspDataVerifyUtil.isEmptyList(reco)){
            log.info("========================>>记账表待冲正明细为 [0]<<===============================");
            return;
        }
        log.info("========================>>记账表待冲正明细为 [{}]<<===============================",reco.size());

        int suc = 0;
        int fail = 0;
        int timeOut = 0;
        int skip = 0;
        List<CommonResponse> respList = new ArrayList<>();
        for (KeepAccInfo keepAccInfo : reco) {
            // STEP2.3 调用单边冲正服务
            respList.add(handle(keepAccInfo));
        }
        // STEP2.4 获取处理结果并打印日志
        for (CommonResponse response : respList) {
            if(IfspDataVerifyUtil.equals(response.getRespCode(), RespEnum.RESP_SUCCESS.getCode())){
                suc++;
            }else if(IfspDataVerifyUtil.equals(response.getRespCode(), RespEnum.RESP_FAIL.getCode())){
                fail++;
            }else if(IfspDataVerifyUtil.equals(response.getRespCode(), RespEnum.RESP_TIMEOUT.getCode())){
                timeOut++;
            }else {
                skip++;
            }
        }
        log.info("冲正结果: 成功数量: [{}]条, 失败数量: [{}]条, 超时数量: [{}]条 ,  跳过数量: [{}]条",suc ,fail , timeOut,  skip );
    }


    /**
     * 调用单笔冲正服务获取返回码
     * @param keepAccInfo
     * @return
     */
    public CommonResponse handle(KeepAccInfo keepAccInfo) {
        //调用单笔冲正接口
        CommonResponse commonResponse = new CommonResponse();
        SoaParams params = new SoaParams();
        /**
         * 冲正流水号
         */
        params.put("pagyPayTxnSsn",keepAccInfo.getCoreSsn());
        /**
         * 冲正时间
         */
        params.put("pagyPayTxnTm",IfspDateTime.getYYYYMMDDHHMMSS());
        /**
         * 被冲正流水号
         */
        params.put("origPagyPayTxnSsn",keepAccInfo.getOrigCoreSsn());
        /**
         * 抹账标志 S-自动，M-手工
         */
        params.put("erasPayInd","S");
        SoaResults soaResults = KeepAccSoaService.onceRevKeepAcc(params);
        commonResponse.setRespCode(soaResults.getRespCode());
        commonResponse.setRespMsg(soaResults.getRespMsg());
        return commonResponse;

    }




//    /**
//     * 异步冲正工作线程
//     */
//    class ResvTask implements Callable<CommonResponse>{
//
//        private KeepAccInfo keepAccInfo;
//
//        private KeepAccSoaService keepAccSoaService;
//
//        public ResvTask(KeepAccSoaService keepAccSoaService, KeepAccInfo keepAccInfo) {
//            this.keepAccSoaService = keepAccSoaService;
//            this.keepAccInfo = keepAccInfo;
//        }
//
//
//        @Override
//        public CommonResponse call() throws Exception {
//            //调用单笔冲正接口
//            CommonResponse commonResponse = new CommonResponse();
//            SoaParams params = new SoaParams();
//            /**
//             * 冲正流水号
//             */
//            params.put("pagyPayTxnSsn",keepAccInfo.getCoreSsn());
//            /**
//             * 冲正时间
//             */
//            params.put("pagyPayTxnTm",IfspDateTime.getYYYYMMDDHHMMSS());
//            /**
//             * 被冲正流水号
//             */
//            params.put("origPagyPayTxnSsn",keepAccInfo.getOrigCoreSsn());
//            /**
//             * 抹账标志 S-自动，M-手工
//             */
//            params.put("erasPayInd","S");
//            SoaResults soaResults = KeepAccSoaService.onceRevKeepAcc(params);
//            commonResponse.setRespCode(soaResults.getRespCode());
//            commonResponse.setRespMsg(soaResults.getRespMsg());
//            return commonResponse;
//
//        }
//    }




//    private void initPool() {
//        destoryPool();
//        log.info("====初始化线程池(start)====");
//        /*
//         * 构建
//         */
//        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
//            AtomicInteger atomic = new AtomicInteger();
//
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "revAccTask_" + this.atomic.getAndIncrement());
//            }
//        });
//        log.info("====初始化线程池(end)====");
//    }
//
//
//    private void destoryPool() {
//        log.info("====销毁线程池(start)====");
//        /*
//         * 初始化线程池
//         */
//        if (executor != null) {
//            log.info("线程池为null, 无需清理");
//            /*
//             * 关闭线程池
//             */
//            try {
//                executor.shutdown();
//                if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
//                    executor.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                System.out.println("awaitTermination interrupted: " + e);
//                executor.shutdownNow();
//            }
//        }
//        log.info("====销毁线程池(end)====");
//    }

}
