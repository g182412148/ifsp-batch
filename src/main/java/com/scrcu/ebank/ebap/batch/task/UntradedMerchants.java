package com.scrcu.ebank.ebap.batch.task;


import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import com.scrcu.ebank.ebap.batch.common.utils.UUIDCreator;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.dubbo.scan.SoaKey;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *日终处理未交易商户
 *
 *
 * @author
 */
@Slf4j
public class UntradedMerchants {

    @Resource
    private PayOrderInfoDao payOrderInfoDao;           // 订单信息

    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           // 商户基本信息

    /**
     * 每个线程处理数据量
     */
    @Value("${clearSum.threadCapacity}")
    private Integer threadCapacity;
    /**
     * 处理线程数量
     */
    @Value("${preClea.threadCount}")
    private Integer threadCount;

    //商户信息缓存
    private Map<String,MchtContInfo> merMapInfo = new HashMap<String,MchtContInfo>();

    //private static  ExecutorService executorService = Executors.newFixedThreadPool(120);

    /**
     * 线程池
     */
    ExecutorService executor;

    public void untradedMchtInfoAccount() {
        log.info("======================================>>  UntradedMerchants  Start ......");

        UntradedMerchants untradedMerchants = (UntradedMerchants)IfspSpringContextUtils.getInstance().getBean("UntradedMerchants");
        String orderTM = DateUtil.format(new Date(), "yyyyMMdd");

        //注销12月未交易用户
        untradedMerchants.cancelMerchants(orderTM);
        // 1.扫描订单表，查询出3个月未交易的数据并记录
        untradedMerchants.scanTab(orderTM);

        log.info("======================================>>  UntradedMerchants  End ......");
    }

    /**
     * 查询出3个月未交易的数据，并记录
     * @return
     */
    public void scanTab(String orderTM)  {
        log.info("========================>>定时任务开始扫描3个月未交易商户信息<<===============================");


        int count = this.getTotalResult(orderTM);
        log.info(">>>>>>>>>>>>>>>>>>>>>3个月未交易商户数： " + count);
        if(count>0){
            int clearCount = clearUntradedMerchants();
            log.info(">>>>>>>>>>>>>>>>>>>>>清理3个月未交易商户数： " + clearCount);
            int pageCount = (int)Math.ceil((double) count / threadCapacity);
            log.info("共分为[{}]组处理", pageCount);
            initPool();
            //处理结果
            for (int groupIndex = 1; groupIndex <= pageCount; groupIndex++) {
                int minIndex = (groupIndex-1)* threadCapacity + 1;
                int maxIndex = groupIndex* threadCapacity ;
                Runnable runnable = untradedMchtInfo(new DataInterval(minIndex, maxIndex),orderTM);
                executor.execute(runnable);
            }
        }


    }

    /**
     * 统计前3个月未交易的数据量
     * @param date
     * @return
     */
    private int getTotalResult(String date)
    {
        int count = 0;
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("orderTM", date);
        count = payOrderInfoDao.count("countUntradedMerchants", m);
        return count;
    }

    /**
     * 清理未交易商户表
     * @param
     * @return
     */
    private int clearUntradedMerchants()
    {
        int count = 0;
        count = payOrderInfoDao.clearUntradedMerchants();
        return count;
    }

    /**
     * 多线程任务去处理3个月未交易商户
     * @param
     * @return
     */
    private Runnable untradedMchtInfo(DataInterval dataInterval,String orderTM) {
        log.debug("====处理{}数据(start)====", dataInterval);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    //未交易商户汇总
                    List<PayOrderInfo> payOrderInfoList =  payOrderInfoDao.untradedMerchants(dataInterval.getMin(),dataInterval.getMax(),orderTM);
                    int size = payOrderInfoList.size();
                    log.info("待处理3个月未交易商户记录总条数: {}",size);
                    if(IfspDataVerifyUtil.isNotEmpty(payOrderInfoList)){
                        int a = payOrderInfoDao.insertBatch(payOrderInfoList);
                        log.info("批量插入3个月未交易商户表[{}]条数据", a);
                    }
                    log.debug("====处理{}数据(end)====", dataInterval);
                } catch (Exception e) {
                    log.error("未知异常:", e);
                    log.debug("====处理{}数据(error end)====", dataInterval);
                    throw e;
                }
            }
        };
    }

    /**
     * 注销12个月未交易的商户的二维码
     * @param
     * @return
     */
    private void cancelMerchants(String orderTM) {
        log.info("========================>>定时任务开始扫描12个月未交易商户信息<<===============================");
        List<MchtBaseInfo> merchantList = mchtBaseInfoDao.selectUntradedMerchants(orderTM,"-12");
        log.info(">>>>>>>>>>>>>>>>>>>>>12个月未交易商户数： " + merchantList.size());
        Iterator<MchtBaseInfo> iterator = merchantList.iterator();
        while(iterator.hasNext()){
            MchtBaseInfo mchtBaseInfo = iterator.next();
            Map<String, Object> params = new HashMap<>();
            params.put("mchtId", mchtBaseInfo.getMchtId());
            params.put("mchtState", "09");
            params.put("orgCode", "9909");
            params.put("reqChnl", "98");
            params.put("mchtName", mchtBaseInfo.getMchtName());
            params.put("mchtSimName", mchtBaseInfo.getMchtSimName());
            params.put("reqSsn",  IfspId.getUUID32());
            params.put("reqTm",IfspDateTime.getYYYYMMDDHHMMSS());
            params.put("userNo","000000");
            Map resMap = DubboServiceUtil.invokeDubboService(params, new SoaKey("001.MchtUpdateState", null, null));
            if(!"0000".equals(resMap.get("respCode"))){
                log.info(mchtBaseInfo.getMchtId()+"注销商户申请失败"+resMap.toString());
                continue;
                //System.exit(1);
            }
            Map<String, Object> paramsa = new HashMap<>();
            paramsa.put("mchtId", mchtBaseInfo.getMchtId());
            paramsa.put("auditResult", "0");
            paramsa.put("auditView", "");
            paramsa.put("rejectReason", "");
            paramsa.put("reqChnl", "98");
            paramsa.put("reqSsn", IfspId.getUUID32());
            paramsa.put("reqTm",IfspDateTime.getYYYYMMDDHHMMSS());
            paramsa.put("userNo","000000");
            Map resMapa = DubboServiceUtil.invokeDubboService(paramsa, new SoaKey("001.mchtCenterMchtAudit", null, null));
            if(!"0000".equals(resMapa.get("respCode"))){
                log.info(mchtBaseInfo.getMchtId()+"注销商户审核失败" + resMapa.toString());
            }else {
                log.info(mchtBaseInfo.getMchtId()+"12个月未交易，注销商户成功");
            }
        }

    }

    private void initPool() {
        destoryPool();
        /*
         * 构建
         */
        log.info("====初始化线程池(start)====");
        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
            AtomicInteger atomic = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "clearingHander_" + this.atomic.getAndIncrement());
            }
        });
        log.info("====初始化线程池(end)====");
    }


    private void destoryPool() {
        log.info("====销毁线程池(start)====");
        /*
         * 初始化线程池
         */
        if (executor != null) {
            log.info("线程池为null, 无需清理");
            /*
             * 关闭线程池
             */
            try {
                executor.shutdown();
                if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.out.println("awaitTermination interrupted: " + e);
                executor.shutdownNow();
            }
        }
        log.info("====销毁线程池(end)====");
    }
}
