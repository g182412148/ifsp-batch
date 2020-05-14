package com.scrcu.ebank.ebap.batch.service.impl;/**
 * Created by Administrator on 2019-07-09.
 */

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.service.ClearingExecutor;
import com.scrcu.ebank.ebap.batch.service.OrderClearing;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-07-09 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Service("wxClearingServiceImp")
@Slf4j
public class WxClearingServiceImp implements OrderClearing
{

    @Resource(name = "wxClearingExecutor")
    private ClearingExecutor wxClearingExecutor;

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;   // 入账明细信息


    /**
     * 处理线程数量
     */
    @Value("${orderClea.threadCount}")
    private Integer threadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${orderClea.threadCapacity}")
    private Integer threadCapacity;
    /**
     * 微信线程池
     */
    ExecutorService executor;

    @Override
    public CommonResponse channelClearing(BatchRequest request) throws Exception
    {
        long start = System.currentTimeMillis();
        String batchDate = request.getSettleDate();
        if (IfspDataVerifyUtil.isBlank(batchDate))
        {
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
        }

        //处理结果
        List<Future> futureList = new ArrayList<>();
        //应答
        CommonResponse commonResponse = new CommonResponse();

        commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        initPool();
        wxClearingExecutor.init(request.getPagyNo(),batchDate);
        log.info("批量初始化完成，耗时【{}】",System.currentTimeMillis()-start);
        try
        {
            List<BthMerInAccDtl> clearDataList=null;
            List<BthMerInAccDtl> subList=new ArrayList<>();
            int pageIdx=1;
            while(true)
            {
                int minIndex = (pageIdx - 1) * threadCapacity*threadCount + 1;  //这里乘以线程数threadCount，一次多搂点
                int maxIndex = pageIdx * threadCapacity*threadCount;
                clearDataList = bthMerInAccDtlDao.getCleaDateListTemp(batchDate, request.getPagyNo(), "", minIndex, maxIndex);
                if(clearDataList==null||clearDataList.size()==0)
                {
                    log.info("待处理结果集clearDataList为空，循环退出，pageIdx【{}】，minIndex【{}】，maxIndex【{}】",pageIdx,minIndex,maxIndex);
                    break;
                }

                int countDown=0;
                if(clearDataList.size()%threadCapacity==0)
                {
                    countDown=clearDataList.size()/threadCapacity;
                }
                else
                {
                    countDown=clearDataList.size()/threadCapacity+1;
                }
                CountDownLatch countDownLatch=new CountDownLatch(countDown);


                if(pageIdx%10==0)
                {
                    System.gc();
                }
                log.info("处理第[{}]组数据", pageIdx);
                pageIdx++;

                int threadCapacityTemp=1;
                //拆成小块分给每个子线程
                for(Iterator<BthMerInAccDtl> it=clearDataList.iterator();it.hasNext();)
                {
                    subList.add(it.next());
                    it.remove();

                    if(threadCapacityTemp%threadCapacity==0)
                    {
                        ClearingTask clearingTask = new ClearingTask(subList, wxClearingExecutor,countDownLatch);
                        Future<CommonResponse> future = executor.submit(clearingTask);
                        futureList.add(future);

                        subList=new ArrayList<>();
                    }
                    threadCapacityTemp++;
                }

                if(clearDataList.isEmpty()&&subList.size()>0)
                {
                    ClearingTask clearingTask = new ClearingTask(subList, wxClearingExecutor,countDownLatch);
                    Future<CommonResponse> future = executor.submit(clearingTask);
                    futureList.add(future);

                    subList=new ArrayList<>();
                }
//                countDownLatch.await(5, TimeUnit.MINUTES);    //先去掉
            }

            clearDataList.clear();
            /*
             * 获取处理结果
             */
            for (Future future : futureList)
            {
                try
                {
                    future.get(30, TimeUnit.MINUTES);
                }
                catch (Exception e)
                {
                    log.error("微信清分线程处理异常: ", e);
                    //返回结果
                    commonResponse.setRespCode(SystemConfig.getSysErrorCode());
                    commonResponse.setRespMsg("微信清分线程处理异常:" + e.getMessage());
                }
            }

//            int count=this.wxClearingExecutor.updateStatus(request.getPagyNo(),request.getSettleDate());
//            log.info("更新入账明细表状态为清分中完成，数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
//            count=this.wxClearingExecutor.insertFromTempTable(request.getPagyNo());
//            log.info("批量清分明细表完成，数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
        }
        finally
        {
            //释放线程池
            destoryPool();
            log.info("微信清分处理完成，总耗时【{}】",System.currentTimeMillis()-start);
        }


        return commonResponse;
    }

    class ClearingTask<T> implements Callable<T>
    {
        List<BthMerInAccDtl> clearDataList;
        String batchDate;
        String pagyNo;
        //最小行数
        private DataInterval dataInterval;
        ClearingExecutor wxClearingExecutor;
        private CountDownLatch countDownLatch;

        public ClearingTask(List<BthMerInAccDtl> clearDataList, ClearingExecutor wxClearingExecutor,CountDownLatch countDownLatch)
        {
            this.clearDataList = clearDataList;
            this.wxClearingExecutor = wxClearingExecutor;
            this.countDownLatch=countDownLatch;
        }

        public ClearingTask(String batchDt, DataInterval dataInterval, String pagyNo,
                            ClearingExecutor wxClearingExecutor)
        {
            this.batchDate = batchDt;
            this.dataInterval = dataInterval;
            this.pagyNo = pagyNo;
            this.wxClearingExecutor = wxClearingExecutor;
        }

//        @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
        public void run() throws Exception
        {

//            for (Iterator<BthMerInAccDtl> it = clearDataList.iterator(); it.hasNext(); )
//            {
//                BthMerInAccDtl bthMerInAccDtl=it.next();
//                if (Constans.SETTLE_STATUS_NOT_CLEARING.equals(bthMerInAccDtl.getStlStatus()) ||
//                        Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING.equals(bthMerInAccDtl.getStlStatus()))
//                {
//                    //aliClearingExecutor.execute(bthMerInAccDtl);
//                    ;//改为什么都不做
//                }
//                else
//                {
//                    //移除当前分片中已清分数据
//                    it.remove();
//                }
//            }

            wxClearingExecutor.execute(this.clearDataList);    //根据订单编号（分片批量）进行清分
            clearDataList.clear();
            this.countDownLatch.countDown();
        }

        @Override
        public T call() throws Exception
        {

            CommonResponse failResponse = new CommonResponse();
            this.run();
            return (T) failResponse;
        }
    }

    private void initPool()
    {
        destoryPool();
        /*
         * 构建
         */
        log.info("====初始化线程池(start)====");
        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory()
        {
            AtomicInteger atomic = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "wxClearingHander_" + this.atomic.getAndIncrement());
            }
        });
        log.info("====初始化线程池(end)====");
    }


    private void destoryPool()
    {
        log.info("====销毁线程池(start)====");
        /*
         * 初始化线程池
         */
        if (executor != null)
        {
            log.info("线程池为null, 无需清理");
            /*
             * 关闭线程池
             */
            try
            {
                executor.shutdown();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                {
                    executor.shutdownNow();
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("awaitTermination interrupted: " + e);
                executor.shutdownNow();
            }
        }
        log.info("====销毁线程池(end)====");
    }
}
