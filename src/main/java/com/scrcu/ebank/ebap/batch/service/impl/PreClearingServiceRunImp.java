package com.scrcu.ebank.ebap.batch.service.impl;/**
 * Created by Administrator on 2019-07-09.
 */

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalSum;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.CacheMerStlRateInfo;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthChkRsltInfoDao;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.service.PreClearingService;
import com.scrcu.ebank.ebap.batch.service.PreClearingServiceRun;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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
@Service
@Slf4j
public class PreClearingServiceRunImp implements PreClearingServiceRun
{
    @Resource
    private PreClearingService preClearingService;

    @Resource
    private BthChkRsltInfoDao bthChkRsltInfoDao;    //对账成功结果信息

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;   // 入账明细信息

    /**
     * 处理线程数量
     */
    @Value("${preClea.threadCount}")
    private Integer threadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${preClea.threadCapacity}")
    private Integer threadCapacity;
    /**
     * 线程池
     */
    ExecutorService executor;

    public void init(String chkSuccDt)
    {
        long start=System.currentTimeMillis();

        bthChkRsltInfoDao.delete("clearChkSuccTemp",null);  //清空临时表

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("chkSuccDt", chkSuccDt);
        params.put("stlmSt", Constans.SETTLE_STATUS_NOT_CLEARING);    //00-初始化状态
        int count = bthChkRsltInfoDao.update("initChkSuccTemp",params); //将今日数据放入临时表中待查
        log.info("将今日数据放入临时表中待查，数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
//        count = bthChkRsltInfoDao.update("updateChkOrderTempStlStatusByChkDate",params);//将临时表中对账结果还原为未清算
//        log.info("还原对账结果临时表中所有订单为为未清算，数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);

        //bthMerInAccDtlDao.delete("clearBthMerInAccDtlTempNoPagy",null);   //清除入账明细临时表
        CacheMerStlRateInfo.clearCache();
    }
    @Override
    public CommonResponse prepare(BatchRequest request) throws Exception
    {
        long start=System.currentTimeMillis();
        String batchDate = request.getSettleDate();
        if (IfspDataVerifyUtil.isBlank(batchDate))
        {
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
        }
//        int count = this.getTotalResult(batchDate);
//        log.info(">>>>>>>>>>>>>>>>>>>>>check succuss order-count of " + batchDate + " is " + count);
//        int pageCount = (int) Math.ceil((double) count / threadCapacity);
//        log.info("共分为[{}]组处理", pageCount);
        //处理结果
        //应答
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
        commonResponse.setRespMsg(IfspRespCodeEnum.RESP_SUCCESS.getDesc());
        List<Future> futureList = new ArrayList<>();
        initPool();
        try
        {
            this.init(batchDate);
            log.info("订单状态初始化完成，耗时【{}】",System.currentTimeMillis()-start);
            List<BthChkRsltInfo> chkSuccList=null;
            List<BthChkRsltInfo> subList=new ArrayList<>();
            int pageIdx=1;
            while(true)
            {
                int minIndex = (pageIdx - 1) * threadCapacity*threadCount + 1;  //这里乘以线程数threadCount，一次多搂点
                int maxIndex = pageIdx * threadCapacity*threadCount;


                long queryStart=System.currentTimeMillis();
                chkSuccList = bthChkRsltInfoDao.selectChkSuccOrderByDateByRangeTemp(batchDate, minIndex, maxIndex);
                if(chkSuccList==null||chkSuccList.size()==0)
                {
                    log.info("待处理结果集chkSuccList为空，循环退出，pageIdx【{}】，minIndex【{}】，maxIndex【{}】",pageIdx,minIndex,maxIndex);
                    break;
                }
                log.info("处理主线程查询待分片处理结果集，数量【{}】，本次查询耗时【{}】",chkSuccList.size(),System.currentTimeMillis()-queryStart);

                int countDown=0;
                if(chkSuccList.size()%threadCapacity==0)
                {
                    countDown=chkSuccList.size()/threadCapacity;
                }
                else
                {
                    countDown=chkSuccList.size()/threadCapacity+1;
                }
                CountDownLatch countDownLatch=new CountDownLatch(countDown);


                if(pageIdx%10==0)
                {
                    System.gc();
                }
                log.info("处理第【{}】组数据", pageIdx);
                pageIdx++;

                int threadCapacityTemp=1;
                //拆成小块分给每个子线程
                for(Iterator<BthChkRsltInfo> it = chkSuccList.iterator(); it.hasNext();)
                {
                    subList.add(it.next());
                    it.remove();

                    if(threadCapacityTemp%threadCapacity==0)
                    {
                        PreClearingTask preClearingTask = new PreClearingTask(batchDate, subList, preClearingService,countDownLatch);
                        Future<CommonResponse> future = executor.submit(preClearingTask);
                        futureList.add(future);
                        subList=new ArrayList<>();
                    }
                    threadCapacityTemp++;
                }

                if(subList!=null&&subList.size()>0)
                {
                    PreClearingTask preClearingTask = new PreClearingTask(batchDate, subList, preClearingService,countDownLatch);
                    Future<CommonResponse> future = executor.submit(preClearingTask);
                    futureList.add(future);
                }

//                countDownLatch.await(10, TimeUnit.MINUTES);
            }



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
                    log.error("清分数据抽取线程处理异常: ", e);
                    //返回结果
                    commonResponse.setRespCode(SystemConfig.getSysErrorCode());
                    commonResponse.setRespMsg("清分数据抽取线程处理异常:" + e.getMessage());
                    return commonResponse;
//                    throw new IfspSystemException(SystemConfig.getSysErrorCode(), "清分数据抽取线程处理异常:" + e.getMessage());
                }
            }

            if(chkSuccList!=null)
            {
                chkSuccList.clear();
            }

//            int count = bthMerInAccDtlDao.delete("clearBthMerInAccDtl",null);//将当日数据从入账明细正式表中清除
//            log.info("清空任务失败遗留清分数据，数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
//
//            this.insertMerInAccDtlFromTemp();
//            log.info("入账明细临时表数据回迁入正式表完成，总耗时【{}】",System.currentTimeMillis()-start);
//
//            Map<String, Object> params = new HashMap<String, Object>();
//            params.put("stlmStNew", Constans.SETTLE_STATUS_CLEARING);    //01-清分中
//            params.put("stlmStOld", Constans.SETTLE_STATUS_NOT_CLEARING);    //00-初始化状态
//            params.put("chkSuccDt", batchDate);
//            count = bthChkRsltInfoDao.update("updateChkOrderStlStatusByDate", params); //更新正式表中初始化状态订单为清算中
//            log.info("更新正式表中所有订单为清算中，数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
        }
        finally
        {
            //释放线程池
            destoryPool();
        }
        return commonResponse;
    }

    class PreClearingTask<T> implements Callable<T>
    {
        String batchDate;
        //最小行数
        private DataInterval dataInterval;
        private List<BthChkRsltInfo> chkSuccList;
        PreClearingService preClearingService;
        private CountDownLatch countDownLatch;
        public PreClearingTask(String batchDt, List<BthChkRsltInfo> chkSuccList, PreClearingService preClearingService,CountDownLatch countDownLatch)
        {
            this.batchDate = batchDt;
            this.chkSuccList = chkSuccList;
            this.preClearingService = preClearingService;
            this.countDownLatch=countDownLatch;
        }

        public PreClearingTask(String batchDt, DataInterval dataInterval, PreClearingService preClearingService)
        {
            this.batchDate = batchDt;
            this.dataInterval = dataInterval;
            this.preClearingService = preClearingService;
        }

        public void run() throws Exception
        {
            long sTime = System.currentTimeMillis();
//            List<BthChkRsltInfo> chkSuccList = bthChkRsltInfoDao.selectChkSuccOrderByDateByRange(batchDate, dataInterval.getMin(), dataInterval.getMax());
//            long eTime = System.currentTimeMillis();
//            for (Iterator<BthChkRsltInfo> it = chkSuccList.iterator(); it.hasNext(); )
//            {
//                BthChkRsltInfo chkSuccOrd = it.next();
//                if (!Constans.SETTLE_STATUS_NOT_CLEARING.equals(chkSuccOrd.getStlmSt()))
//                {
//                    //跳过已清分数据
//                    chkSuccOrd = null;
//                    it.remove();
//                    continue;
//                }
//                preClearingService.dataGathering(chkSuccOrd, batchDate);
//            }

            if(chkSuccList!=null&&chkSuccList.size()>0)
            {
                preClearingService.
                        dataGathering(chkSuccList, batchDate);   //批量处理
            }
            countDownLatch.countDown();
            log.info("【{}】清分数据抽取批量，当片数据处理完成，耗时【{}】", batchDate, System.currentTimeMillis() - sTime);
        }

        @Override
        public T call() throws Exception
        {

            CommonResponse failResponse = new CommonResponse();
            this.run();

            return (T) failResponse;
        }
    }

    /**
     * 统计当前渠道对账成功数据量
     *
     * @param date 对账成功日期
     * @return
     */
    private int getTotalResult(String date)
    {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>income test.........");
        int count = 0;
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("chkSuccDt", date);
        count = bthChkRsltInfoDao.count("countChkSuccOrderByDate", m);
        return count;
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
                return new Thread(r, "clearingHander_" + this.atomic.getAndIncrement());
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
                log.error("awaitTermination interrupted: " + e);
                executor.shutdownNow();
            }
        }
        log.info("====销毁线程池(end)====");
    }


    public int insertMerInAccDtlFromTemp()
    {
        return bthMerInAccDtlDao.insert("insertMerInAccDtlFromTemp", null);
    }
}
