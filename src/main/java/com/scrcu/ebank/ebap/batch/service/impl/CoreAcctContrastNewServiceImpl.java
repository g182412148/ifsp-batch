package com.scrcu.ebank.ebap.batch.service.impl;/**
 * Created by Administrator on 2019-05-17.
 */

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.DubisFlagDict;
import com.scrcu.ebank.ebap.batch.common.dict.ErroProcStateDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoResultDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.batch.dao.CoreBillInfoDao;
import com.scrcu.ebank.ebap.batch.dao.CoreRecoErrDao;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.dao.KeepRecoInfoDao;
import com.scrcu.ebank.ebap.batch.service.CoreAcctContrastNewService;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-05-17 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Service
@Slf4j
public class CoreAcctContrastNewServiceImpl implements CoreAcctContrastNewService
{

    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    @Resource
    private KeepRecoInfoDao keepRecoInfoDao;

    @Resource
    private CoreRecoErrDao coreRecoErrDao;

    @Resource
    private CoreBillInfoDao coreBillInfoDao;

    /**
     * 处理线程数量
     */
    @Value("${coreReco.threadCount}")
    private Integer coreThreadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${coreReco.threadCapacity}")
    private Integer coreThreadCapacity;
    /**
     * 线程池
     */
    ExecutorService executor;

    /**
     * 线程池更新记账表
     */
    ExecutorService keepExecutor;

    @Override
    public AcctContrastResponse keepAccCoreContrastNew(AcctContrastRequest request) throws Exception
    {
        long start=System.currentTimeMillis();
        log.info("---------------------核心vs记账表对账开始--------------------");
        /***** 获取请求参数值 ********/
        String settleDate = request.getSettleDate();// 清算日期
        //获取的订单数据的交易日期
        Date txnDate = getRecoDate(request.getSettleDate());
        //计算对账日期 = 交易日期 + 1
        Date recoDate = new DateTime(txnDate).plusDays(1).toDate();
        /********* 初始化响应对象 ***********/
        AcctContrastResponse acctContrastResponse = new AcctContrastResponse();
        log.info("date:" + settleDate);
        String doubtDate = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, -1);
        log.info("doubtDate:" + doubtDate);
        /***
         * 2.还原核心记账流水表数据（考虑是否只还原前一天数据）
         */
        int recoveryCount = coreBillInfoDao.recovery(recoDate);
        log.info("还原对账文件明细表数据{}条", recoveryCount);
//        int recoveryCountDubious = coreBillInfoDao.recoveryDubious(recoDate);
//        log.info("还原对账文件明细表前一天可疑数据数据{}条", recoveryCountDubious);
        /***
         * 3.还原核心记账流水表数据（考虑是否只还原前一天数据）
         */
        int keepCount = keepRecoInfoDao.recovery(recoDate);
        log.info("还原记账对账流水表数据{}条", keepCount);
        int keepCountDubious = keepRecoInfoDao.recoveryDubious(recoDate);
        log.info("还原记账对账流水表可疑数据{}条", keepCountDubious);
        /**
         * 4.还原记账表数据keep_acc_info 当天数据（交易日）
         */
        int keepRecCount = keepAccInfoDao.recovery(settleDate);
        log.info("还原记账表数据{}条", keepRecCount);
        /**
         * 4.1还原记账表数据keep_acc_info 前一天可疑数据（交易日前一天）
         */
        int keepRecCountDou = keepAccInfoDao.recovery(doubtDate);
        log.info("还原核心记账流水表[{}]可疑数据{}条", doubtDate, keepRecCountDou);
        /**
         * 4.清理差错数据
         */
        int clear = coreRecoErrDao.clear(recoDate);
        log.info("清理差错表数据{}条", clear);
        initPool();
        /***
         * 5.分批查询核心记账流水表当天数据和前一天未对账数据到本地记账表做流水匹配，匹配结果分别
         * 更新或者入差错。最后处理本地记账表未对账数据。
         */
        log.info("核心记账对账-begin");
        try
        {
            reco(recoDate);
        }
        finally
        {
            destoryPool();
        }

        log.info("核心记账对账-end");
        /**
         * 6.分批查询本地记账表（keep_reco_info）并批量更新状态到记账表（keep_acc_info）
         */
        initPoolKeep();
        try
        {
            updKeepAccInfo(recoDate);
        }
        finally
        {
            destoryPoolKeep();
        }

        return acctContrastResponse;
    }

    /**
     * 对账
     *
     * @param recoDate
     */
    private void reco(Date recoDate)
    {
        /*
         * 查询核心流水表的数据总数
         */
        int count = coreBillInfoDao.count(recoDate);
        log.info("对账文件对账数据[{}]条", count);
        //分组数量
        int groupCount = (int) Math.ceil((double) count / coreThreadCapacity);
        log.info("总分组数量[{}]页", groupCount);
        /*
         * 已核心为准 勾兑 记账表
         */
        //处理结果
        List<Future> futureList = new ArrayList<>();
        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++)
        {
            int minIndex = (groupIndex - 1) * coreThreadCapacity + 1;
            int maxIndex = groupIndex * coreThreadCapacity;
            log.info("处理第[{}]组数据", groupIndex);
            Future future = executor.submit(new CoreHandler(recoDate, new DataInterval(minIndex, maxIndex)));
            futureList.add(future);
        }
        /*
         * 获取处理结果
         */
        log.info("获取处理结果。。。。。。");
        for (Future future : futureList)
        {
            try
            {
                future.get(10, TimeUnit.MINUTES);
            }
            catch (Exception e)
            {
                log.error("对账线程处理异常: ", e);
                //取消其他任务
                destoryPool();
                log.warn("其他子任务已取消.");
                //返回结果
                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
            }
        }
        /**
         * 更新核心对账文件解析表堆成功的这状态（在对账过程中未更新对平的core_bill_info数据，在此统一更新）
         */
        CoreBillInfo coreBillInfoUpd = new CoreBillInfo();
        coreBillInfoUpd.setRecoDate(recoDate);
        coreBillInfoUpd.setUpdTm(recoDate);
        coreBillInfoUpd.setRecoState(RecoStatusDict.FINISH.getCode());
        coreBillInfoUpd.setChkRst(Constans.CHK_RST_00);
        coreBillInfoUpd.setDubiousDate(new DateTime(recoDate).minusDays(1).toDate());
        int updCount = coreBillInfoDao.updateSucState(coreBillInfoUpd);
        log.info("更新(core_bill_info)对账成功数据共{}条", updCount);
        /**
         * 处理三方未对账数据
         *      1) 可疑交易, 更改对账状态为可疑
         *      2) 单边交易, 更新对账状态为已对账; 记入对账差错表;
         */
        List<KeepRecoInfo> locList = keepRecoInfoDao.queryNotReco(recoDate);
        if (locList == null || locList.isEmpty())
        {
            log.info("本地记账无单边或可疑明细");
        }
        else
        {
            for (KeepRecoInfo keepRecoInfo : locList)
            {
                //可疑交易
                if (isDubious(recoDate, keepRecoInfo.getRecoDate()))
                {
                    KeepRecoInfo keepRecoInfoUpd = new KeepRecoInfo(keepRecoInfo.getTxnSsn(),
                                                                    keepRecoInfo.getRecoDate(), recoDate,
                                                                    RecoStatusDict.FINISH.getCode(),
                                                                    DubisFlagDict.TRUE.getCode());
//                    keepRecoInfoUpd.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                    keepRecoInfoUpd.setChkRst(Constans.CHK_RST_02);
                    keepRecoInfoDao.updateById(keepRecoInfoUpd);
                }
                else
                {
                    KeepRecoInfo keepRecoInfoUpd = new KeepRecoInfo(keepRecoInfo.getTxnSsn(),
                                                                    keepRecoInfo.getRecoDate(), recoDate,
                                                                    RecoStatusDict.FINISH.getCode());
                    if (Constans.KEEP_ACCOUNT_STAT_TIMEOUT.equals(keepRecoInfo.getState()))
                    {
                        keepRecoInfoUpd.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);
                    }
                    keepRecoInfoUpd.setChkRst(Constans.CHK_RST_03);
                    keepRecoInfoDao.updateById(keepRecoInfoUpd);
                    coreRecoErrDao.insert(getRecoErr(recoDate, keepRecoInfo, null, RecoResultDict.LOCAL_UA));
                }
            }
        }
    }

    public void updKeepAccInfo(Date recoDate)
    {
        /*
         * 查询核心流水表的数据总数
         */
        int count = keepRecoInfoDao.count(recoDate);
        //分组数量
        int groupCount = (int) Math.ceil((double) count / coreThreadCapacity);
        /*
         * 已核心为准 勾兑 记账表
         */
        //处理结果
        List<Future> futureList = new ArrayList<>();
        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++)
        {
            int minIndex = (groupIndex - 1) * coreThreadCapacity + 1;
            int maxIndex = groupIndex * coreThreadCapacity;
            Future future = keepExecutor.submit(new KeepHandler(recoDate, new DataInterval(minIndex, maxIndex)));
            futureList.add(future);
        }
        /*
         * 获取处理结果
         */
        log.info("获取处理结果。。。。。。");
        for (Future future : futureList)
        {
            try
            {
                future.get(10, TimeUnit.MINUTES);
            }
            catch (Exception e)
            {
                log.error("对账线程处理异常: ", e);
                //取消其他任务
                destoryPoolKeep();
                log.warn("其他子任务已取消.");
                //返回结果
                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
            }
        }
    }

    /**
     * 核心对账流水表状态更新工作线程
     *
     * @param <T>
     */
    class CoreHandler<T> implements Callable<T>
    {

        //对账日期
        private Date recoDate;
        //最小行数
        private DataInterval dataInterval;

        public CoreHandler(Date recoDate, DataInterval dataInterval)
        {
            this.recoDate = recoDate;
            this.dataInterval = dataInterval;
        }

        @Override
        public T call() throws Exception
        {
            try
            {
                log.debug("====处理{}数据(start)====", dataInterval);
                /*
                 * 结果集合
                 */
                //对平
                List<BthChkRsltInfo> identicalList = new ArrayList<>();
                //不平: 包括本地单边 和 不平
                List<CoreRecoErr> errorList = new ArrayList<>();
                /*
                 * 对账
                 */
                //分页查询出本地流水
                List<CoreBillInfo> coreRecords = coreBillInfoDao.queryByRange(recoDate, dataInterval.getMin(),
                                                                              dataInterval.getMax());
                if (coreRecords == null || coreRecords.isEmpty())
                {
                    log.warn("{}数据为null", dataInterval);
                }
                else
                {
                    log.debug("{},数据容量:{}", dataInterval, coreRecords.size());
                    //对账成功list
                    List<CoreBillInfo> coreBillInfoListSuc = new ArrayList<>();
                    /*
                     * 以核心记账流水为准, 匹配本地记账表流水:
                     *      1)匹配不上, 核心单边（前一天入差错当天可疑）;
                     *      2)匹配上了, 更新为平账;
                     */
                    for (CoreBillInfo coreBillInfo : coreRecords)
                    {
                        //根据核心记账流水查询本地记账对账流水表流水（keep_reco_info）
                        KeepRecoInfo keepRecoInfo = null;
                        keepRecoInfo = keepRecoInfoDao.queryByIdOfDate(coreBillInfo.getChannelSeq(), recoDate,
                                                                       new DateTime(recoDate).minusDays(1).toDate());
                        /*
                         * 核心记账有, 本地记账无:
                         *      1) 核心记账流水对账日期 == 当前对账日期, 核心记账流水进入可疑
                         *      2) 核心记账流水对账日期 + 1 == 当前对账日期, 表示两个对账周期都没有找到匹配的流水, 核心记账流水进入单边;
                         */
                        if (keepRecoInfo == null)
                        {
                            //判断是否可疑
                            if (isDubious(recoDate, coreBillInfo.getRecoDate()))
                            {
                                //更新对账状态为: 可疑
                                coreBillInfoDao.updateById(
                                        new CoreBillInfo(coreBillInfo.getTxnSsn(), coreBillInfo.getRecoDate(), recoDate,
                                                         RecoStatusDict.FINISH.getCode(),
                                                         DubisFlagDict.TRUE.getCode()));
                            }
                            else
                            {
                                //加入不平列表
                                errorList.add(getRecoErr(recoDate, null, coreBillInfo, RecoResultDict.OUTER_UA));
                                //更新对账状态为: 已完成对账
                                CoreBillInfo coreBillInfoUpd = new CoreBillInfo(coreBillInfo.getTxnSsn(),
                                                                                coreBillInfo.getRecoDate(), recoDate,
                                                                                RecoStatusDict.FINISH.getCode());
                                coreBillInfoUpd.setChkRst(Constans.CHK_RST_03);
                                coreBillInfoDao.updateById(coreBillInfoUpd);
                            }
                        }
                        else
                        {
                            //更新对账状态(考虑加入list批量更新)
                            CoreBillInfo coreBillInfoUpd = new CoreBillInfo(coreBillInfo.getTxnSsn(),
                                                                            coreBillInfo.getRecoDate(), recoDate,
                                                                            RecoStatusDict.FINISH.getCode());
                            coreBillInfoUpd.setChkRst(Constans.CHK_RST_00);
//                            coreBillInfoDao.updateById(coreBillInfoUpd);
//                            coreBillInfoListSuc.add(coreBillInfoUpd);
                            KeepRecoInfo keepRecoInfoUpd = new KeepRecoInfo(keepRecoInfo.getTxnSsn(),
                                                                            keepRecoInfo.getRecoDate(), recoDate,
                                                                            RecoStatusDict.FINISH.getCode());
                            keepRecoInfoUpd.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                            keepRecoInfoUpd.setChkRst(Constans.CHK_RST_00);
                            keepRecoInfoDao.updateById(keepRecoInfoUpd);
                        }
                    }
                }
                /*
                 * 结果处理
                 */
                //记录不平流水
                coreRecoErrDao.insertBatch(errorList);
                log.debug("====处理{}数据(end)====", dataInterval);
            }
            catch (Exception e)
            {
                log.error("对账未知异常:", e);
                log.debug("====处理{}数据(error end)====", dataInterval);
                throw e;
            }
            return null;
        }
    }

    /**
     * 获取对账日期
     *
     * @param dateStr
     * @return
     */
    private Date getRecoDate(String dateStr)
    {
        if (StringUtils.isBlank(dateStr))
        {
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期为空");
        }
        try
        {
            return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").toDate(); //todo 改成常数
        }
        catch (Exception e)
        {
            log.error("对账日期格式错误: ", e);
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期格式错误");
        }
    }

    /**
     * 记账表状态更新工作线程
     *
     * @param <T>
     */
    class KeepHandler<T> implements Callable<T>
    {

        //对账日期
        private Date recoDate;
        //最小行数
        private DataInterval dataInterval;

        public KeepHandler(Date recoDate, DataInterval dataInterval)
        {
            this.recoDate = recoDate;
            this.dataInterval = dataInterval;
        }

        @Override
        public T call() throws Exception
        {
            try
            {
                log.debug("====处理{}数据(start)====", dataInterval);
                //分页查询出本地流水
                List<KeepRecoInfo> keepRecords = keepRecoInfoDao.queryByRange(recoDate, dataInterval.getMin(),
                                                                              dataInterval.getMax());
                if (keepRecords == null || keepRecords.isEmpty())
                {
                    log.warn("{}数据为null", dataInterval);
                }
                else
                {
                    log.debug("{},数据容量:{}", dataInterval, keepRecords.size());
                    /**
                     * 更新记账表(todo 批量更新,先更新可疑部分，剩余的都是成功)
                     */
                    for (KeepRecoInfo keepRecoInfo : keepRecords)
                    {
                        KeepAccInfo keepAccInfo = new KeepAccInfo();
                        keepAccInfo.setCoreSsn(keepRecoInfo.getTxnSsn());
                        keepAccInfo.setState(keepRecoInfo.getState());
                        keepAccInfo.setChkRst(keepRecoInfo.getChkRst());
                        keepAccInfo.setChkSt(Constans.CHK_STATE_01);
                        if (IfspDataVerifyUtil.equals(keepRecoInfo.getChkRst(), Constans.CHK_RST_02))
                        {
                            keepAccInfo.setDubiousFlag(Constans.DUBIOUS_FLAG_01);
                        }
                        keepAccInfoDao.updateByPrimaryKeySelectiveState(keepAccInfo);
                    }

                }
            }
            catch (Exception e)
            {
                log.error("对账未知异常:", e);
                log.debug("====处理{}数据(error end)====", dataInterval);
                throw e;
            }
            return null;
        }
    }

    //    @PostConstruct
    private void initPool()
    {
        destoryPool();
        log.info("====初始化线程池(start)====");
        /*
         * 构建
         */
        executor = Executors.newFixedThreadPool(coreThreadCount, new ThreadFactory()
        {
            AtomicInteger atomic = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "coreExecutorHander_" + this.atomic.getAndIncrement());
            }
        });
        log.info("====初始化线程池(end)====");
    }

    //    @PostConstruct
    private void initPoolKeep()
    {
        destoryPoolKeep();
        log.info("====初始化线程池(start)====");
        /*
         * 构建
         */
        keepExecutor = Executors.newFixedThreadPool(coreThreadCount, new ThreadFactory()
        {
            AtomicInteger atomic = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "keepExecutorHander_" + this.atomic.getAndIncrement());
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

    private void destoryPoolKeep()
    {
        log.info("====销毁线程池(start)====");
        /*
         * 初始化线程池
         */
        if (keepExecutor != null)
        {
            log.info("线程池为null, 无需清理");
            /*
             * 关闭线程池
             */
            try
            {
                keepExecutor.shutdown();
                if (!keepExecutor.awaitTermination(10, TimeUnit.SECONDS))
                {
                    keepExecutor.shutdownNow();
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("awaitTermination interrupted: " + e);
                keepExecutor.shutdownNow();
            }
        }
        log.info("====销毁线程池(end)====");
    }

    /**
     * 判断是否单边
     *
     * @param recoDate       当前对账日期
     * @param recordRecoDate 流水对账日期
     * @return
     */
    private boolean isDubious(Date recoDate, Date recordRecoDate)
    {
        //本地流水对账日期 == 当前对账日期
        if (recordRecoDate.compareTo(recoDate) == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
//        //本地流水对账日期 + 1 == 当前对账日期
//        else if ((new DateTime(recordRecoDate)).plusDays(1).toDate().compareTo(recoDate) == 0) {
//            return true;
//        }else {
//            return null;
//        }
    }

    /**
     * 本地流水 转换为 不平账流水
     *
     * @return
     */
    private CoreRecoErr getRecoErr(Date recoDate, KeepRecoInfo localRecord, CoreBillInfo outerRecord,
                                   RecoResultDict resultDict)
    {
        if (localRecord == null && outerRecord == null)
        {
            throw new IllegalArgumentException("localRecord and outerRecord is null");
        }
        if (resultDict == null)
        {
            throw new IllegalArgumentException("resultDict is null");
        }
        CoreRecoErr result = new CoreRecoErr();
        result.setRecoDate(recoDate);
        /*
         * 记录本地流水信息
         */
        if (localRecord != null)
        {
            result.setTxnSsn(localRecord.getTxnSsn());
            result.setCoreSsn(localRecord.getTxnSsn());
            if (IfspDataVerifyUtil.isNotBlank(localRecord.getOrderTm()))
            {
                try
                {
                    result.setOrderDate(IfspDateTime.getYYYYMMDDHHMMSS(localRecord.getOrderTm()));
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                    throw new IfspBizException("时间装换异常");
                }
            }
            result.setLocalTxnState(localRecord.getTxnState());
            result.setLocalTxnAmt(new BigDecimal(localRecord.getTransAmt()));
            result.setOrderId(localRecord.getOrderSsn());
        }
        /*
         * 记录三方流水信息
         */
        if (outerRecord != null)
        {
            result.setTxnSsn(outerRecord.getTxnSsn());
            result.setOuterTxnState(outerRecord.getTxnState());
            result.setOuterTxnAmt(outerRecord.getTxnAmount());
            result.setCoreSsn(outerRecord.getChannelSeq());
        }
        result.setRecoRest(resultDict.getCode());
        result.setProcState(ErroProcStateDict.INIT.getCode()); //状态为: 未处理
        result.setProcDesc("未处理");
        result.setUpdDate(new Date());

        return result;
    }
}
