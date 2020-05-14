package com.scrcu.ebank.ebap.batch.controller;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.dto.BillRecoErr;
import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.WxBillLocal;
import com.scrcu.ebank.ebap.batch.bean.dto.WxBillOuter;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.ErroProcStateDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoResultDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthChkRsltInfoDao;
import com.scrcu.ebank.ebap.batch.dao.WxBillResultDao;
import com.scrcu.ebank.ebap.batch.dao.test.BillRecoErrDao;
import com.scrcu.ebank.ebap.batch.dao.test.WxBillLocalDao;
import com.scrcu.ebank.ebap.batch.dao.test.WxBillOuterDao;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对账
 */
@Controller
public class BillRecoController {

    Logger log = IfspLoggerFactory.getLogger(BillRecoController.class);

    @Resource
    private WxBillLocalDao localDao;
    @Resource
    private WxBillOuterDao outerDao;
    @Resource
    private BthChkRsltInfoDao identicalDao;
    @Resource
    private BillRecoErrDao recoErrDao;
    @Resource
    private WxBillResultDao resultDao;
    /**
     * 处理线程数量
     */
    @Value("${wxReco.threadCount}")
    private Integer threadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${wxReco.threadCapacity}")
    private Integer threadCapacity;
    /**
     * 线程池
     */
    ExecutorService executor;


    @SOA("001.WxBillContrast")
    @Explain(name = "微信对账")
    public CommonResponse wechatReco(AcctContrastRequest req) {
        long start=System.currentTimeMillis();
        //获取对账日志
        Date recoDate = DateUtil.getAfterDate(req.getSettleDate(), 1);
        //数据清理

        clear(recoDate);
        /** 自本行起为本次微信对账改造代码 start */
        reco(recoDate);
        /** end */

//        //初始化线程池
//        initPool();
//        //对账
//        try {
//            reco(recoDate);
//        }finally {
//            //释放线程池
//            destoryPool();
//        }
        log.info("微信对账结束，对账日期【{}】，总耗时【{}】",DateUtil.toDateString(recoDate),System.currentTimeMillis()-start);
        return new CommonResponse();
    }


    /**
     * 数据清理
     * @param recoDate
     */
    private void clear(Date recoDate) {
        long start=System.currentTimeMillis();
        //恢复本地对账明细
        int localRecCount = localDao.recovery(recoDate);
        log.info("恢复当日本地对账明细, 数量【{}】，耗时【{}】" , localRecCount,System.currentTimeMillis()-start);
        //恢复本地可疑对账明细
        int localDubisCount = localDao.recoveryDubious(new DateTime(recoDate).minusDays(1).toDate());
        log.info("恢复前一天本地可疑明细, 数量【{}】，耗时【{}】" , localDubisCount,System.currentTimeMillis()-start);
        //恢复微信对账明细
        int outerRecCount = outerDao.recovery(recoDate);
        log.info("恢复当日微信对账明细, 数量【{}】，耗时【{}】" , outerRecCount,System.currentTimeMillis()-start);
        //恢复微信可疑对账明细
        int outerDubisCount = outerDao.recoveryDubious(new DateTime(recoDate).minusDays(1).toDate());
        log.info("恢复前一天微信可疑明细, 数量【{}】，耗时【{}】" , outerDubisCount,System.currentTimeMillis()-start);
        //清空平账结果
        int resultCount = identicalDao.deleteAliByStlmDateAndPagySysNo(IfspDateTime.getYYYYMMDD(recoDate), Constans.WX_SYS_NO+" ");
        log.info("清空平账结果, 数量【{}】，耗时【{}】" , resultCount,System.currentTimeMillis()-start);
        //清空差错结果
        int errCount = recoErrDao.clear(recoDate, Constans.WX_SYS_NO);
        log.info("清空差错结果, 数量【{}】，耗时【{}】" , errCount,System.currentTimeMillis()-start);
        //清空微信对账结果
        int wxResultCount = resultDao.clear();
        log.info("清空微信对账结果, 数量【{}】，耗时【{}】" , wxResultCount,System.currentTimeMillis()-start);
    }

    /**
     * 对账
     */
    private void reco(Date recoDate){
        long start=System.currentTimeMillis();
        /*
         * 查询本地流水表的数据总数
         */
        int count = localDao.count(recoDate);
        /** 自本行起为本次微信对账改造代码 start */
        log.info("本地流水表数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);

        //将本地流水表(WX_BILL_LOCAL)与三方流水表(WX_BILL_OUTER)进行比对，比对结果插入至微信对账结果表(WX_BILL_RESULT)
        int wxChkResultCount = resultDao.insertWxResult(recoDate);
        log.info("本次微信对账共勾兑流水数量【{}】，耗时【{}】",wxChkResultCount,System.currentTimeMillis()-start);

        //统计微信对账结果表中本地可疑和本地单边流水数量
        int localDebiousCount = resultDao.countLocal();
        log.info("本次微信对账本地可疑和本地单边流水数量【{}】，耗时【{}】",localDebiousCount,System.currentTimeMillis()-start);

        //统计微信对账结果表中三方可疑和三方单边流水数量
        int outerDebiousCount = resultDao.countOuter();
        log.info("本次微信对账三方可疑和三方单边流水数量【{}】，耗时【{}】",outerDebiousCount,System.currentTimeMillis()-start);

        //将差错记录插入到本地差错记录表
        int errCount = recoErrDao.insertWxErrResult(recoDate);
        log.info("本次微信对账差错记录数量【{}】，耗时【{}】",errCount,System.currentTimeMillis()-start);

        //将微信对账结果插入到对账结果表
        int chkResultCount = identicalDao.insertWxChkResult(recoDate);
        log.info("本次微信对账已对账记录数量【{}】，耗时【{}】",chkResultCount,System.currentTimeMillis()-start);

        //更新本地流水表
        int localCount = localDao.updateByResult(recoDate);
        log.info("本次微信对账共勾兑本地流水数量【{}】，耗时【{}】",localCount,System.currentTimeMillis()-start);

        //更新三方流水表
        int outerCount = outerDao.updateByResult(recoDate);
        log.info("本次微信对账共勾兑三方流水数量【{}】，耗时【{}】",outerCount,System.currentTimeMillis()-start);
        /** end */
//        //分组数量
//        int groupCount = (int) Math.ceil((double) count / threadCapacity);
//        /*
//         * 已本地为准 勾兑 第三方
//         */
//        //处理结果
//        List<Future> futureList = new ArrayList<>();
//        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++) {
//            int minIndex = (groupIndex - 1) * threadCapacity + 1;
//            int maxIndex = groupIndex * threadCapacity;
//            Future future = executor.submit(new Handler(recoDate, new DataInterval(minIndex, maxIndex)));
//            futureList.add(future);
//        }
//        /*
//         * 获取处理结果
//         */
//        for (Future future : futureList) {
//            try {
//               future.get(10, TimeUnit.MINUTES);
//            } catch (Exception e) {
//                log.error("对账线程处理异常: ", e);
//                //取消其他任务
//                destoryPool();
//                log.warn("其他子任务已取消.");
//                //返回结果
//                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "对账线程处理异常:" + e.getMessage());
//            }
//        }
//        /*
//         * 处理三方未对账数据
//         *      1) 可疑交易, 更改对账状态为可疑
//         *      2) 单边交易, 更新对账状态为已对账; 记入对账差错表;
//         */
//        List<WxBillOuter> outerList = outerDao.queryNotReco(recoDate);
//        if(outerList == null || outerList.isEmpty()){
//            log.info("微信三方对账单中无单边或可疑明细");
//        }else {
//            for(WxBillOuter outer : outerList){
//                //可疑交易
//                if(isDubious(recoDate, outer.getRecoDate())){
//                    outerDao.updateById(new WxBillOuter(outer.getTxnSsn(), RecoStatusDict.DUBIOUS));
//                }else {
//                    recoErrDao.insert(getRecoErr(recoDate,null, outer, Constans.WX_SYS_NO, RecoResultDict.OUTER_UA));
//                    outerDao.updateById(new WxBillOuter(outer.getTxnSsn(), RecoStatusDict.FINISH));
//                }
//            }
//        }
    }

//    /**
//     * 工作线程
//     *
//     * @param <T>
//     */
//    class Handler<T> implements Callable<T> {
//
//        //对账日期
//        private Date recoDate;
//        //最小行数
//        private DataInterval dataInterval;
//
//        public Handler(Date recoDate, DataInterval dataInterval) {
//            this.recoDate = recoDate;
//            this.dataInterval = dataInterval;
//        }
//
//        @Override
//        public T call() throws Exception {
//            try {
//                log.debug("====处理{}数据(start)====", dataInterval);
//                /*
//                 * 结果集合
//                 */
//                //对平
//                List<BthChkRsltInfo> identicalList = new ArrayList<>();
//                //不平: 包括本地单边 和 不平
//                List<BillRecoErr> errorList = new ArrayList<>();
//                /*
//                 * 对账
//                 */
//                //分页查询出本地流水
//                List<WxBillLocal> localRecords = localDao.queryByRange(recoDate, dataInterval.getMin(), dataInterval.getMax());
//                if (localRecords == null || localRecords.isEmpty()) {
//                    log.warn("{}数据为null", dataInterval);
//                } else {
//                    log.debug("{},数据容量:{}", dataInterval, localRecords.size());
//                    /*
//                     * 以本地流水为准, 匹配三方流水:
//                     *      1)匹配不上, 本地单边;
//                     *      2)匹配上了, 对比流水内容;
//                     */
//                    for (WxBillLocal localRecord : localRecords) {
//                        //根据本地流水查询微信流水
//                        WxBillOuter outerRecord = outerDao.queryByIdAndDate(localRecord.getTxnSsn(), recoDate, new DateTime(recoDate).minusDays(1).toDate());
//                        /*
//                         * 本地有, 微信无时:
//                         *      1) 本地流水对账日期 == 当前对账日期, 本地流水进入可疑
//                         *      2) 本地流水对账日期 + 1 == 当前对账日期, 表示两个对账周期都没有找到匹配的流水, 本地流水进入单边;
//                         */
//                        if (outerRecord == null) {
//                            //判断是否可疑
//                            if(isDubious(recoDate, localRecord.getRecoDate())){
//                                //更新对账状态为: 可疑
//                                localDao.updateById(new WxBillLocal(localRecord.getTxnSsn(), RecoStatusDict.DUBIOUS));
//                            }else {
//                                //加入不平列表
//                                errorList.add(getRecoErr(recoDate, localRecord, null, Constans.WX_SYS_NO, RecoResultDict.LOCAL_UA));
//                                //更新对账状态为: 已完成对账
//                                localDao.updateById(new WxBillLocal(localRecord.getTxnSsn(), RecoStatusDict.FINISH));
//                            }
//                        } else {
//                            //比较流水内容
//                            RecoResultDict recoResult = compare(localRecord, outerRecord);
//                            //对平
//                            if (recoResult == RecoResultDict.IDENTICAL) {
//                                //加入对平列表
//                                identicalList.add(getIdel(localRecord, outerRecord, recoDate));
//                            }
//                            //不平
//                            else {
//                                errorList.add(getRecoErr(recoDate, localRecord, outerRecord, Constans.WX_SYS_NO, recoResult));
//                            }
//                            //更新对账状态
//                            localDao.updateById(new WxBillLocal(localRecord.getTxnSsn(), RecoStatusDict.FINISH));
//                            outerDao.updateById(new WxBillOuter(localRecord.getTxnSsn(), RecoStatusDict.FINISH));
//                        }
//                    }
//                }
//                /*
//                 * 结果处理
//                 */
//                //记录平账流水
//                identicalDao.insertBatch(identicalList);
//                //记录不平流水
//                recoErrDao.insertBatch(errorList);
//                log.debug("====处理{}数据(end)====", dataInterval);
//            } catch (Exception e) {
//                log.error("对账未知异常:", e);
//                log.debug("====处理{}数据(error end)====", dataInterval);
//                throw e;
//            }
//            return null;
//        }
//    }
//
//    /**
//     * 本地流水 转换为 平账流水
//     *
//     * @return
//     */
//    private BthChkRsltInfo getIdel(WxBillLocal localRecord, WxBillOuter outerRecord, Date recoDate) {
//        BthChkRsltInfo result = new BthChkRsltInfo();
//        result.setPagyPayTxnSsn(localRecord.getTxnSsn());
//        result.setPagyPayTxnTm(localRecord.getTxnTime());
//        result.setChkSt(RecoStatusDict.FINISH.getCode());
//        result.setChkRst(null);
//        result.setOrderSsn(localRecord.getOrderId());
//        result.setTxnAmt(localRecord.getTxnAmt() == null? null : localRecord.getTxnAmt().longValue());
//
//        result.setPagyTxnTm(localRecord.getOrderDate()); //没用, 但是数据库需要
//        result.setPagySysSoaNo("0");  //没用, 但是数据库需要
//        result.setPagySysSoaVersion("0");  //没用, 但是数据库需要
//        result.setChkSuccDt(recoDate);  //没用, 但是数据库需要
//
//        /*
//         * 清分需要用
//         */
//        result.setPagySysNo(Constans.WX_SYS_NO); //通道号
//        result.setTpamTxnFeeAmt(outerRecord.getFeeAmt() == null? null : outerRecord.getFeeAmt().longValue()); //渠道手续费
//        result.setStlmSt(Constans.STLM_ST_00); //未清算
//        return result;
//    }
//
//    /**
//     * 本地流水 转换为 不平账流水
//     *
//     * @return
//     */
//    private BillRecoErr getRecoErr(Date recoDate, WxBillLocal localRecord, WxBillOuter outerRecord, String chnlId, RecoResultDict resultDict) {
//        if (localRecord == null && outerRecord == null) {
//            throw new IllegalArgumentException("localRecord and outerRecord is null");
//        }
//        if (StringUtils.isBlank(chnlId)) {
//            throw new IllegalArgumentException("chnlId is blank");
//        }
//        if (resultDict == null) {
//            throw new IllegalArgumentException("resultDict is null");
//        }
//        BillRecoErr result = new BillRecoErr();
//        result.setChnlNo(chnlId);
//        /*
//         * 记录本地流水信息
//         */
//        if (localRecord != null) {
//            result.setTxnSsn(localRecord.getTxnSsn());
//            result.setRecoDate(recoDate);
//            result.setTxnTime(localRecord.getTxnTime());
//            result.setLocalTxnType(localRecord.getTxnType());
//            result.setLocalTxnState(localRecord.getTxnState());
//            result.setLocalTxnAmt(localRecord.getTxnAmt());
//            result.setOrderId(localRecord.getOrderId());
//            result.setOrderDate(localRecord.getOrderDate());
//        }
//        /*
//         * 记录三方流水信息
//         */
//        if (outerRecord != null) {
//            result.setTxnSsn(outerRecord.getTxnSsn());
//            result.setRecoDate(recoDate);
//            result.setTxnTime(outerRecord.getTxnTime());
//            result.setOuterTxnType(outerRecord.getTxnType());
//            result.setOuterTxnState(outerRecord.getTxnState());
//            result.setOuterTxnAmt(outerRecord.getTxnAmt());
//        }
//        result.setRecoRest(resultDict.getCode());
//        result.setProcState(ErroProcStateDict.INIT.getCode()); //状态为: 未处理
//        result.setProcDesc("未处理");
//        result.setUpdDate(new Date());
//
//        return result;
//    }
//
//    /**
//     * 比较流水内容
//     *
//     * @param localRecord
//     * @param outerRecord
//     * @return
//     */
//    private RecoResultDict compare(WxBillLocal localRecord, WxBillOuter outerRecord) {
//        //交易状态
//        if (!StringUtils.equals(localRecord.getTxnState(), outerRecord.getTxnState())) {
//            return RecoResultDict.STATE_UI;
//        }
//        //交易类型
//        if (!StringUtils.equals(localRecord.getTxnType(), outerRecord.getTxnType())) {
//            return RecoResultDict.TYPE_UI;
//        }
//        //交易金额
//        if (!equalsAmt(localRecord.getTxnAmt(), outerRecord.getTxnAmt())) {
//            return RecoResultDict.AMT_UI;
//        }
//        return RecoResultDict.IDENTICAL;
//    }
//
//
//    /**
//     * 比较金额
//     *
//     * @param localAmt
//     * @param outerAmt
//     * @return
//     */
//    private boolean equalsAmt(BigDecimal localAmt, BigDecimal outerAmt) {
//        if (localAmt == null) {
//            return false;
//        }
//        if (outerAmt == null) {
//            return false;
//        }
//        return localAmt.compareTo(outerAmt) == 0;
//    }
//
//    private void initPool() {
//        destoryPool();
//        /*
//         * 构建
//         */
//        log.info("====初始化线程池(start)====");
//        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
//            AtomicInteger atomic = new AtomicInteger();
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "wxRecoHander_" + this.atomic.getAndIncrement());
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
//
//
//    /**
//     * 判断是否单边
//     * @param recoDate 当前对账日期
//     * @param recordRecoDate  流水对账日期
//     * @return
//     */
//    private boolean isDubious(Date recoDate, Date recordRecoDate){
//        //本地流水对账日期 == 当前对账日期
//        if (recordRecoDate.compareTo(recoDate) == 0) {
//            return true;
//        }else {
//            return false;
//        }
////        //本地流水对账日期 + 1 == 当前对账日期
////        else if ((new DateTime(recordRecoDate)).plusDays(1).toDate().compareTo(recoDate) == 0) {
////            return true;
////        }else {
////            return null;
////        }
//    }
//
//
//    /**
//     * 获取对账日期
//     * @param dateStr
//     * @return
//     */
//    private Date getRecoDate(String dateStr){
//        if(StringUtils.isBlank(dateStr)){
//            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期为空");
//        }
//        try{
//            return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").toDate(); //todo 改成常数
//        }catch (Exception e){
//            log.error("对账日期格式错误: ", e);
//            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期格式错误");
//        }
//    }

}
