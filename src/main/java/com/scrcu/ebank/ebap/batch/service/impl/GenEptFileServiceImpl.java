package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.swing.plaf.synth.SynthTextAreaUI;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.vo.CacheMchtOrgRel;
import com.scrcu.ebank.ebap.batch.common.utils.UUIDCreator;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.config.SystemConfig;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.service.GenEptFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class GenEptFileServiceImpl implements GenEptFileService
{

    private static BigDecimal ONE_HUNDRED = new BigDecimal(100);
    private static final int PAGE_SIZE = 100;

    private String batchDate;

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           //商户基本信息

    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息

    @Resource
    private BthVatInfoDao bthVatInfoDao;               //增值税表

    @Resource
    private BthSetCapitalDetailDao bthSetCapitalDetailDao;     // 清分表

    @Resource
    private MchtOrgRelDao mchtOrgRelDao;     // 清分表

    @Value("${subjectCode}")
    private String subjectCode;

    @Value("${subjectName}")
    private String subjectName;

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
     * 处理线程池
     */
    ExecutorService executor;

    /**
     * 生成增值税系统文件
     */
    @Override
    public CommonResponse genEptChkFile(BatchRequest request)
    {
        long start=System.currentTimeMillis();
        String batchDate = request.getSettleDate();
        if (IfspDataVerifyUtil.isBlank(batchDate))
        {
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
            //txnDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
        }
        log.info(">>>>>>>>>>>>>>generate vat data of batch date " + request.getSettleDate() + " begin..");

        this.setBatchDate(batchDate);
        //this.setTxnDate(DateUtil.format(DateUtil.getDiffStringDate(DateUtil.parse(batchDate, "yyyyMMdd"), -1), "yyyyMMdd"));

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

        //支持重跑设置
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tranDate", this.getBatchDate());
        bthVatInfoDao.delete("deleteVatInfoByDate", params);
        log.info("数据清除完成，日期【{}】，耗时【{}】",this.getBatchDate(),System.currentTimeMillis()-start);

        //设置FixedThreadPool线程池线程数量为机器cpu数 * 2
//        int nThreads=Runtime.getRuntime().availableProcessors() * 2;
//        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        initPool();
        //处理结果
        List<Future> futureList = new ArrayList<>();
        try
        {

            //1、根据入账日期查询分润数据（merFee > chnlFee）
            List<String> orderSsnList=null;
            List<String> subList=new ArrayList<>();
            int pageIdx=1;
            loop:while(true)
            {
                long queryStart=System.currentTimeMillis();
                int minIndex = (pageIdx - 1) * threadCapacity*threadCount*10 + 1;  //这里乘以线程数threadCount，一次多搂点
                int maxIndex = pageIdx * threadCapacity*threadCount*10;
                orderSsnList = this.merFeeGreaterThanChnlFee(minIndex, maxIndex);
                if(orderSsnList==null||orderSsnList.size()==0)
                {
                    log.info("（行内费率大于渠道费率），待处理结果集orderSsnList为空，循环退出，pageIdx【{}】，minIndex【{}】，maxIndex【{}】",pageIdx,minIndex,maxIndex);
                    break;
                }

                log.info("（行内费率大于渠道费率），开始处理第【{}】组数据，数量【{}】，查询耗时【{}】", pageIdx,orderSsnList.size(),System.currentTimeMillis()-queryStart);
                pageIdx++;

                int threadCapacityTemp=1;
                //拆成小块分给每个子线程
                for(Iterator<String> it=orderSsnList.iterator();it.hasNext();)
                {
                    subList.add(it.next());
                    it.remove();

                    if(threadCapacityTemp%threadCapacity==0)
                    {
                        CapitalDataDealTask capitalDataDealTask = new CapitalDataDealTask(subList);
                        Future<CommonResponse> future = executor.submit(capitalDataDealTask);
                        futureList.add(future);

                        subList=new ArrayList<>();
//                        break loop;
                    }
                    threadCapacityTemp++;
                }
                if(subList!=null&&subList.size()>0)
                {
                    CapitalDataDealTask capitalDataDealTask = new CapitalDataDealTask(subList);
                    Future<CommonResponse> lastFuture = executor.submit(capitalDataDealTask);
                    futureList.add(lastFuture);
                }
            }

            orderSsnList.clear();

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
                    log.error("生成增值税数据（行内费率大于渠道费率）线程处理异常: ", e);
                    //返回结果
                    commonResponse.setRespCode(SystemConfig.getSysErrorCode());
                    commonResponse.setRespMsg("生成增值税数据（行内费率大于渠道费率）线程处理异常:" + e.getMessage());
                }
            }

            log.info("生成增值税数据（行内费率大于渠道费率）处理完成，耗时【{}】",System.currentTimeMillis()-start);

            //2、根据结算成功日期查询不分润数据（merFee <= chnlFee）
            pageIdx=1;
            loop:while(true)
            {
                long queryStart=System.currentTimeMillis();
                int minIndex = (pageIdx - 1) * threadCapacity*threadCount + 1;  //这里乘以线程数threadCount，一次多搂点
                int maxIndex = pageIdx * threadCapacity*threadCount;
                orderSsnList = this.merFeeLessThanChnlFee(minIndex, maxIndex);
                if(orderSsnList==null||orderSsnList.size()==0)
                {
                    log.info("行内费率小于等于渠道费率），待处理结果集clearDataList为空，循环退出，pageIdx【{}】，minIndex【{}】，maxIndex【{}】",pageIdx,minIndex,maxIndex);
                    break;
                }
                log.info("（行内费率小于等于渠道费率），处理第【{}】组数据，数量【{}】，查询耗时【{}】", pageIdx,orderSsnList.size(),System.currentTimeMillis()-queryStart);
                pageIdx++;

                int threadCapacityTemp=1;
                //拆成小块分给每个子线程
                for(Iterator<String> it=orderSsnList.iterator();it.hasNext();)
                {
                    subList.add(it.next());
                    it.remove();

                    if(threadCapacityTemp%threadCapacity==0)
                    {
                        MerInAccDataDealTask merInAccDataDealTask = new MerInAccDataDealTask(subList);
                        Future<CommonResponse> future = executor.submit(merInAccDataDealTask);
                        futureList.add(future);

                        subList=new ArrayList<>();
//                        break loop;
                    }
                    threadCapacityTemp++;
                }
                if(subList!=null&&subList.size()>0)
                {
                    MerInAccDataDealTask merInAccDataDealTask = new MerInAccDataDealTask(subList);
                    Future lastFuture = executor.submit(merInAccDataDealTask);
                    futureList.add(lastFuture);
                }
            }

            orderSsnList.clear();

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
                    log.error("生成增值税数据（行内费率小于等于渠道费率）线程处理异常: ", e);
                    //返回结果
                    commonResponse.setRespCode(SystemConfig.getSysErrorCode());
                    commonResponse.setRespMsg("生成增值税数据（行内费率小于等于渠道费率）线程处理异常:" + e.getMessage());
                }
            }
            log.info("生成增值税数据（行内费率小于等于渠道费率）处理完成，耗时【{}】",System.currentTimeMillis()-start);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            //释放线程池
            destoryPool();
            log.info("生成增值税处理完成，总耗时【{}】",System.currentTimeMillis()-start);
        }

        return commonResponse;
    }

    class CapitalDataDealTask<T> implements Callable<T>
    {
        List<String> orderSsnList;  //分页处理，每个线程处理一页的数据

        public CapitalDataDealTask(List<String> orderSsnList)
        {
            this.orderSsnList = orderSsnList;
        }

        //@Override
        public void run()
        {
            long start=System.currentTimeMillis();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderSsnList", orderSsnList);

            List<BthSetCapitalDetail> capDtlAllList = bthSetCapitalDetailDao.selectList("selectClearDataByOrderSsnList", params);
            if(capDtlAllList==null||capDtlAllList.size()==0)
            {
                log.info("查询清分明细表为空，处理线程退出，耗时【{}】",System.currentTimeMillis()-start);
                return ;
            }
            Map<String, List<BthSetCapitalDetail>> capDtlMap = capDtlAllList.stream().collect(
                    Collectors.groupingBy(BthSetCapitalDetail::getOrderId));
            log.info("查询清分明细表完成，数量【{}】，耗时【{}】",capDtlAllList.size(),System.currentTimeMillis()-start);
            List<String> merOrgIdList=capDtlAllList.stream().filter(x -> x.getMerId()!=null).map(BthSetCapitalDetail::getMerId).map(x->x.substring(0,15)).collect(Collectors.toList());

            merOrgIdList = merOrgIdList.stream().distinct().collect(Collectors.toList());

            Map<String, MchtOrgRel> mchtOrgRelMap=new HashMap<>();
            if(merOrgIdList!=null&&merOrgIdList.size()>0)
            {
                List<MchtOrgRel> mchtOrgRelList=getMerOrgIdByMerId(merOrgIdList);
                mchtOrgRelMap = mchtOrgRelList.stream().filter(x -> x.getMchtNo()!=null).collect(
                        Collectors.toMap(MchtOrgRel::getMchtNo, a -> a, (k1, k2) -> k1));

            }
            log.info("查询机构信息结束，数量【{}】，耗时【{}】",merOrgIdList.size(),System.currentTimeMillis()-start);
            List<BthVatInfo> bthVatInfoList=new ArrayList<>();

            //这里是否能进行事务控制
            for (String orderSsn : orderSsnList)
            {
                BthVatInfo vatInfo = init();
                BigDecimal merFee = new BigDecimal(0);
                //根据orderSsn查询清分信息

                List<BthSetCapitalDetail> capDtlList=capDtlMap.get(orderSsn);
                if (capDtlList == null || capDtlList.size() == 0)
                {
                    continue;
                }

                for (BthSetCapitalDetail detail : capDtlList)
                {
                    switch (detail.getEntryType())
                    {
                        case Constans.ENTRY_TYPE_FEE_GAINS_SD_ORG:       //收单机构分润
                            vatInfo.setMerOrgId(detail.getInAccoutOrg());
                            merFee = merFee.add(detail.getTranAmount());
                            break;
                        case Constans.ENTRY_TYPE_FEE_GAINS_OPEN_ORG:     //开户机构
                            vatInfo.setOpenOrgId(detail.getInAccoutOrg());
                            vatInfo.setOpenOrgFee(detail.getTranAmount());
                            merFee = merFee.add(detail.getTranAmount());
                            break;
                        case Constans.ENTRY_TYPE_FEE_GAINS_OPERATE_ORG:  //运营机构
                            vatInfo.setOpOrgId(detail.getInAccoutOrg());
                            vatInfo.setOpOrgFee(detail.getTranAmount());
                            merFee = merFee.add(detail.getTranAmount());
                            break;
                    }
                }
                vatInfo.setMerFee(merFee);
                vatInfo.setOrderId(orderSsn);


                if (!StringUtils.hasText(vatInfo.getMerOrgId()))
                {
                    //收单机构未参与分润或者该订单当天的分润成功记录里面不包括收单机构
                    //查询商户收单机构
                    MchtOrgRel mchtOrgRel = mchtOrgRelMap.get(capDtlList.get(0).getMerId().substring(0, 15));
                    vatInfo.setMerOrgId(mchtOrgRel.getOrgId());
                }

                bthVatInfoList.add(vatInfo);
            }

            //增值税信息插入数据库（批量插入）
            int count=bthVatInfoDao.insertBatch(bthVatInfoList);
            log.info("批量插入完成，数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
        }

        @Override
        public T call() throws Exception
        {

            CommonResponse failResponse = new CommonResponse();
            try
            {
                this.run();
                failResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            }
            catch (Exception e)
            {
                System.out.println("---------------->>>>>>>>>>" + e.getMessage());
                failResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
                failResponse.setRespMsg(ExceptionUtils.getFullStackTrace(e));
            }

            return (T) failResponse;
        }
    }


    class MerInAccDataDealTask<T> implements Callable<T>
    {
        List<String> orderSsnList;  //分页处理，每个线程处理一页的数据
        private CountDownLatch latch;

        public MerInAccDataDealTask(List<String> orderSsnList)
        {
            this.orderSsnList = orderSsnList;
        }

        public void run()
        {
            long start=System.currentTimeMillis();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderSsnList", orderSsnList);
            List<BthMerInAccDtl> merInAccDtlList = bthMerInAccDtlDao.selectList("selectVatDataByTxnSeqIdList", params);
            if(merInAccDtlList==null||merInAccDtlList.size()==0)
            {
                log.info("查询入账明细表为空，处理线程退出，耗时【{}】",System.currentTimeMillis()-start);
                return ;
            }
            Map<String, BthMerInAccDtl> merInAccDtlMap = merInAccDtlList.stream().filter(x -> x.getTxnSeqId()!=null).collect(
                    Collectors.toMap(BthMerInAccDtl::getTxnSeqId, a -> a, (k1, k2) -> k1));
            log.info("查询入账明细表完成，数量【{}】，耗时【{}】",merInAccDtlList.size(),System.currentTimeMillis()-start);

            List<String> merOrgIdList=merInAccDtlList.stream().filter(x -> x.getChlMerId()!=null).map(BthMerInAccDtl::getChlMerId).map(x->x.substring(0,15)).collect(Collectors.toList());

            log.info("查询机构信息结束，数量【{}】，耗时【{}】",merOrgIdList.size(),System.currentTimeMillis()-start);
            Map<String, MchtOrgRel> mchtOrgRelMap=new HashMap<>();
            if(merOrgIdList!=null&&merOrgIdList.size()>0)
            {
                List<MchtOrgRel> mchtOrgRelList=getMerOrgIdByMerId(merOrgIdList);
                mchtOrgRelMap = mchtOrgRelList.stream().filter(x -> x.getMchtNo()!=null).collect(
                        Collectors.toMap(MchtOrgRel::getMchtNo, a -> a, (k1, k2) -> k1));
            }

            List<BthVatInfo> bthVatInfoList=new ArrayList<>();

            //这里是否能进行事务控制
            for (String orderSsn : orderSsnList)
            {
                BthMerInAccDtl bthMerInAccDtl=merInAccDtlMap.get(orderSsn);
                if (bthMerInAccDtl == null )
                {
                    continue;
                }
                BthVatInfo vatInfo = init(bthMerInAccDtl);

                //vatInfo.setMerFee(new BigDecimal(merInAccDtl.get(0).getSetlFeeAmt()).divide(ONE_HUNDRED));
                MchtOrgRel mchtOrgRel = mchtOrgRelMap.get(bthMerInAccDtl.getChlMerId().substring(0, 15));
                if(mchtOrgRel!=null)
                {
                    vatInfo.setMerOrgId(mchtOrgRel.getOrgId());
                }

                bthVatInfoList.add(vatInfo);

            }

            //增值税信息插入数据库（批量插入）
            int count=bthVatInfoDao.insertBatch(bthVatInfoList);

            log.info("批量插入完成，数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
        }

        @Override
        public T call() throws Exception
        {
            CommonResponse failResponse = new CommonResponse();
            failResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            try
            {
                this.run();
            }
            catch (Exception e)
            {
                e.printStackTrace();

                failResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
                failResponse.setRespMsg(ExceptionUtils.getFullStackTrace(e));
            }
            return (T) failResponse;
        }
    }


    /***
     * 行内费率大于渠道费率，从清分表取数
     * @param startIdx : 分页起始行
     * @param endIdx ：  分页结束行
     * @return
     */
    public List<String> merFeeGreaterThanChnlFee(int startIdx, int endIdx)
    {
        //查询订单号
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("updateDateStart", this.getBatchDate() + "000000");
        params.put("updateDateEnd", this.getBatchDate() + "999999");
        params.put("dealResult", Constans.DEAL_RESULT_SUCCESS);
        params.put("startIdx", startIdx);
        params.put("endIdx", endIdx);

        List<String> orderSsnList = bthSetCapitalDetailDao.selectOrderIdList("selectOrderIdsByUpdateDate", params);
        return orderSsnList;
    }

    /**
     * 入账明细表数据：
     * 行内费率小于等于渠道费率，商户入账明细表取数
     */
    public List<String> merFeeLessThanChnlFee(int startIdx, int endIdx)
    {
        //查询订单号
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("updateDateStart", this.getBatchDate() + "000000");
        params.put("updateDateEnd", this.getBatchDate() + "999999");
        params.put("stlStatus", Constans.SETTLE_STATUS_SUCCESS_CLEARING);
        params.put("orderType", Constans.ORDER_TYPE_CONSUME);

        params.put("startIdx", startIdx);
        params.put("endIdx", endIdx);

        List<String> orderSsnList = bthMerInAccDtlDao.selectTxnSeqIdList("selectTxnSeqIdsByUpdateDate", params);
        return orderSsnList;
    }


    public List<BthSetCapitalDetail> getClearingDateByOrderId(String orderId)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("updateDate", this.getBatchDate() + '%');

        List<BthSetCapitalDetail> capDtlList = bthSetCapitalDetailDao.selectList("", params);

        return capDtlList;
    }

    private BthVatInfo init()
    {
        Date currDate = new Date();
        BthVatInfo vatInfo = new BthVatInfo();

        vatInfo.setId(UUIDCreator.randomUUID().toString());
        vatInfo.setSubjectCode(subjectCode);
        vatInfo.setSubjectName(subjectName);
        vatInfo.setCur(Constans.VAT_CUR_CNY);
        vatInfo.setDrCrFlag(Constans.VAT_DR_CR_FLAG_1);
        vatInfo.setOverseasFlag(Constans.VAT_OVERSEAS_FLAG_0);
        vatInfo.setTxnChnlFlag(vatInfo.getId());
        vatInfo.setTranDate(this.getBatchDate());

        vatInfo.setCreateDate(currDate);
        return vatInfo;
    }

    private BthVatInfo init(BthMerInAccDtl dtl)
    {
        Date currDate = new Date();
        BthVatInfo vatInfo = new BthVatInfo();

        vatInfo.setId(UUIDCreator.randomUUID().toString());
        vatInfo.setOrderId(dtl.getTxnSeqId());
        //vatInfo.setm

        vatInfo.setTranDate(this.getBatchDate());

        vatInfo.setSubjectCode(subjectCode);
        vatInfo.setSubjectName(subjectName);
        vatInfo.setCur(Constans.VAT_CUR_CNY);
        vatInfo.setDrCrFlag(Constans.VAT_DR_CR_FLAG_1);
        vatInfo.setOverseasFlag(Constans.VAT_OVERSEAS_FLAG_0);
        vatInfo.setTxnChnlFlag(vatInfo.getId());

        vatInfo.setMerFee(new BigDecimal(dtl.getSetlFeeAmt()).divide(ONE_HUNDRED));

        vatInfo.setCreateDate(currDate);
        return vatInfo;
    }

    /**
     * 根据商户号查询商户收单机构
     *
     * @param merIdList :　商户号
     * @return
     */
    public List<MchtOrgRel> getMerOrgIdByMerId(List<String> merIdList)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mchtNoList", merIdList);
        params.put("orgType", Constans.ORG_TYPE_SD);

        List<MchtOrgRel> mchtOrgRelList = mchtOrgRelDao.selectList("selectOrgRelByMchtNoList", params);

        return mchtOrgRelList;
    }

    /**
     * 查询商户机构信息（收单机构、运营机构）批量初始化
     *
     * @param mchtIdList
     * @return
     */
    private void initMerOrgInfo(List<String> mchtIdList)
    {
        for (Iterator<String> it = mchtIdList.iterator(); it.hasNext(); )
        {
            //将缓存中已存在的商户id从mchtIdList中移出
            if (IfspDataVerifyUtil.isNotEmptyList(CacheMchtOrgRel.getCache(it.next())))
            {
                it.remove();
            }
        }
        if (mchtIdList != null && mchtIdList.size() > 0)
        {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("mchtIdList", mchtIdList);
            m.put("orgType",Constans.ORG_TYPE_SD);
            List<MchtOrgRel> orgList = mchtOrgRelDao.selectList("selectMchtOrgRelByMerIdList", m);
            Map<String, List<MchtOrgRel>> orgListMap = orgList.stream().collect(
                    Collectors.groupingBy(MchtOrgRel::getMchtNo));

            CacheMchtOrgRel.addAllCache(orgListMap);
            orgListMap.clear();
            orgList.clear();
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
                return new Thread(r, "genEptDataHander_" + this.atomic.getAndIncrement());
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

    public String getBatchDate()
    {
        return batchDate;
    }

    public void setBatchDate(String batchDate)
    {
        this.batchDate = batchDate;
    }

}
