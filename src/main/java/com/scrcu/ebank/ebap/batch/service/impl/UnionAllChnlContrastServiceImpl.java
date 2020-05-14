package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthChkRsltInfoDao;
import com.scrcu.ebank.ebap.batch.dao.UnionAllBillResultDao;
import com.scrcu.ebank.ebap.batch.dao.UnionBillLocalDao;
import com.scrcu.ebank.ebap.batch.dao.UnionBillOuterDao;
import com.scrcu.ebank.ebap.batch.dao.test.BillRecoErrDao;
import com.scrcu.ebank.ebap.batch.service.UnionAllChnlContrastService;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author: ljy
 * @create: 2018-10-30 23:44
 */
@Slf4j
@Service
public class UnionAllChnlContrastServiceImpl implements UnionAllChnlContrastService {

    @Resource
    private BthChkRsltInfoDao bthChkRsltInfoDao;

    @Resource
    private UnionBillLocalDao unionBillLocalDao;

    @Resource
    private UnionBillOuterDao unionBillOuterDao;

    @Resource
    private BillRecoErrDao billRecoErrDao;

    @Resource
    private UnionAllBillResultDao resultDao;

    /**
     * 处理线程数量
     */
    @Value("${unionReco.threadCount}")
    private Integer unionThreadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${unionReco.threadCapacity}")
    private Integer unionThreadCapacity;
    /**
     * 线程池
     */
    ExecutorService executorUnionAll;

    /**
     * 银联全渠道对账
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public AcctContrastResponse unionAllChnlBillContrast(AcctContrastRequest request) throws Exception {
        long start=System.currentTimeMillis();
        log.info("---------------------银联通道系统vs银联全渠道对账开始--------------------");
        //获取对账日期
        Date recoDate = getRecoDate(request.getSettleDate());
        log.info("获取对账日期--->"+recoDate);
        //数据清理
        log.info("银联全渠道对账数据清理-begin");
        clear(recoDate, Constans.ALL_CHNL_UNION_SYS_NO);
        log.info("银联全渠道对账数据清理-end");

        log.info("银联全渠道对账-begin");
        reco(recoDate, request.getPagySysNo());
        //初始化线程池
//        log.info("银联全渠道对账线程池初始化");
//        initPool();
//        //对账
//        log.info("银联全渠道对账-begin");
//        try{
//            reco(recoDate, request.getPagySysNo());
//        }finally {
//            destoryPool();
//        }

        log.info("银联全渠道对账结束，对账日期【{}】，总耗时【{}】", DateUtil.toDateString(recoDate), System.currentTimeMillis()-start);
        return new AcctContrastResponse();
    }

    /**
     * 获取对账日期
     * @param dateStr
     * @return
     */
    private Date getRecoDate(String dateStr){
        if(StringUtils.isBlank(dateStr)){
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期为空");
        }
        try{
            return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").plusDays(1).toDate(); //todo 改成常数
        }catch (Exception e){
            log.error("对账日期格式错误: ", e);
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期格式错误");
        }
    }

    /**
     * 数据清理
     * @param recoDate
     */
    private void clear(Date recoDate, String pagyNo) {
        long start=System.currentTimeMillis();
        //恢复本地当天对账明细和前一天可疑 recoveryDubious
        int recCount = unionBillLocalDao.recovery(recoDate, pagyNo);
        log.info("恢复本地当天对账明细数量【{}】，耗时【{}】",recCount,System.currentTimeMillis());
        int recCountDub = unionBillLocalDao.recoveryDubious(recoDate, pagyNo);
        log.info("恢复本地前一天可疑明细数量【{}】，耗时【{}】",recCountDub,System.currentTimeMillis());
        //恢复银联全渠道当天对账明细和前一天可疑
        int recOutCount = unionBillOuterDao.recovery(recoDate, pagyNo);
        log.info("恢复银联全渠道当天对账明细数量【{}】，耗时【{}】", recOutCount,System.currentTimeMillis());
        int recOutCountDub = unionBillOuterDao.recoveryDubious(recoDate, pagyNo);
        log.info("恢复银联全渠道前一天可疑明细数量【{}】，耗时【{}】", recOutCountDub,System.currentTimeMillis());
        //清空平账结果
        log.info("清空当天平账结果表，耗时【{}】",System.currentTimeMillis());
        bthChkRsltInfoDao.deleteAliByStlmDateAndPagySysNo(IfspDateTime.getYYYYMMDD(recoDate), pagyNo+" ");
        //清空差错结果
        log.info("清空当天差错结果表，耗时【{}】",System.currentTimeMillis());
        billRecoErrDao.clear(recoDate, pagyNo);
        //清空银联全渠道对账结果表
        int resultCount = resultDao.clear();
        log.info("清空银联全渠道对账结果, 数量【{}】，耗时【{}】" , resultCount,System.currentTimeMillis());
    }

    /**
     * 对账
     * @param recoDate
     */
    private void reco(Date recoDate, String pagyNo){
        long start=System.currentTimeMillis();
        /*
         * 查询本地流水表的数据总数
         */
        int count = unionBillLocalDao.count(recoDate, pagyNo);
        log.info("本地需对账数据, 数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
        /** 自本行起为本次银联全渠道对账改造代码 start */
        //将本地流水表(UNION_BILL_LOCAL)与三方流水表(UNION_BILL_OUTER)进行比对，比对结果插入至银联全渠道对账结果表(UNION_ALL_BILL_RESULT)
        int unionAllChkResultCount = resultDao.insertUnionAllResult(recoDate);
        log.info("本次银联全渠道对账共勾兑流水数量【{}】，耗时【{}】:",unionAllChkResultCount,System.currentTimeMillis()-start);
        //统计银联全渠道对账结果表中本地可疑和本地单边流水数量
        int localDebiousCount = resultDao.countLocal();
        log.info("本次银联全渠道对账本地可疑和本地单边流水数量【{}】，耗时【{}】",localDebiousCount,System.currentTimeMillis()-start);
        //统计银联全渠道对账结果表中三方可疑和三方单边流水数量
        int outerDebiousCount = resultDao.countOuter();
        log.info("本次银联全渠道对账三方可疑和三方单边流水数量【{}】，耗时【{}】",outerDebiousCount,System.currentTimeMillis()-start);
        //将差错记录插入到本地差错记录表
        int errCount = billRecoErrDao.insertUnionAllErrResult(recoDate);
        log.info("本次银联全渠道对账差错记录数量【{}】，耗时【{}】",errCount,System.currentTimeMillis()-start);
        //将银联全渠道对账结果插入到对账结果表
        int chkResultCount = bthChkRsltInfoDao.insertUnionAllChkResult(recoDate);
        log.info("本次银联全渠道对账已对账记录数量【{}】，耗时【{}】",chkResultCount,System.currentTimeMillis()-start);
        //更新本地流水表
        int localCount = unionBillLocalDao.updateByUnionAllResult(recoDate);
        log.info("本次银联全渠道对账共勾兑本地流水数量【{}】，耗时【{}】",localCount,System.currentTimeMillis()-start);
        //更新三方流水表
        int outerCount = unionBillOuterDao.updateByUnionAllResult(recoDate);
        log.info("本次银联全渠道对账共勾兑三方流水数量【{}】，耗时【{}】",outerCount,System.currentTimeMillis()-start);
        /** end */
        //分组数量
//        int groupCount = (int) Math.ceil((double) count / unionThreadCapacity);
//        log.info("总分组数量[{}]页", groupCount);
//        /*
//         * 已本地为准 勾兑 第三方
//         */
//        //处理结果
//        List<Future> futureList = new ArrayList<>();
//        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++) {
//            int minIndex = (groupIndex - 1) * unionThreadCapacity + 1;
//            int maxIndex = groupIndex * unionThreadCapacity;
//            log.info("处理第[{}]组数据", groupIndex);
//            Future future = executorUnionAll.submit(new Handler(recoDate, new DataInterval(minIndex, maxIndex), pagyNo));
//            futureList.add(future);
//        }
//        /*
//         * 获取处理结果
//         */
//        log.info("获取处理结果。。。。。。");
//        for (Future future : futureList) {
//            try {
//                future.get(10, TimeUnit.MINUTES);
//            } catch (Exception e) {
//                log.error("对账线程处理异常: ", e);
//                //取消其他任务
//                destoryPool();
//                log.warn("其他子任务已取消.");
//                //返回结果
//                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
//            }
//        }
//        /*
//         * 处理三方未对账数据
//         *      1) 可疑交易, 更改对账状态为可疑
//         *      2) 单边交易, 更新对账状态为已对账; 记入对账差错表;
//         */
//        log.info("处理三方未对账数据-begin");
//        List<UnionBillOuter> outerList = unionBillOuterDao.queryNotReco(recoDate, pagyNo);
//        if(outerList == null || outerList.isEmpty()){
//            log.info("三方无单边或可疑明细");
//        }else {
//            for(UnionBillOuter outer : outerList){
//                //可疑交易
//                if(isDubious(recoDate, outer.getRecoDate())){
//                    unionBillOuterDao.updateById(new UnionBillOuter(outer.getTxnSsn(), RecoStatusDict.FINISH.getCode(), outer.getRecoDate(), recoDate, DubisFlagDict.TRUE.getCode()));
//                }else {
//                    billRecoErrDao.insert(getRecoErr(recoDate,null, outer, pagyNo, RecoResultDict.OUTER_UA));
//                    unionBillOuterDao.updateById(new UnionBillOuter(outer.getTxnSsn(), RecoStatusDict.SKIP.getCode(), outer.getRecoDate(), recoDate));
//                }
//            }
//        }
    }

    /**
     * 工作线程
     *
     * @param <T>
     */
//    class Handler<T> implements Callable<T> {
//
//        //对账日期
//        private Date recoDate;
//        //最小行数
//        private DataInterval dataInterval;
//
//        private String pagyNo;
//
//        public Handler(Date recoDate, DataInterval dataInterval, String pagyNo) {
//            this.recoDate = recoDate;
//            this.dataInterval = dataInterval;
//            this.pagyNo = pagyNo;
//        }
//
//        @Override
//        public T call() throws Exception {
//            try {
//                log.info("====处理{}数据(start)====", dataInterval);
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
//                List<UnionBillLocal> localRecords = unionBillLocalDao.queryByRange(recoDate, dataInterval.getMin(), dataInterval.getMax(), pagyNo);
//                if (localRecords == null || localRecords.isEmpty()) {
//                    log.warn("{}数据为null", dataInterval);
//                } else {
//                    log.debug("{},数据容量:{}", dataInterval, localRecords.size());
//                    /*
//                     * 以本地流水为准, 匹配三方流水:
//                     *      1)匹配不上, 本地单边;
//                     *      2)匹配上了, 对比流水内容;
//                     */
//                    for (UnionBillLocal localRecord : localRecords) {
//                        //根据本地流水查询银联全渠道流水（区分二维码和全渠道，二维码用清算主键全渠道用流水号）
//                        UnionBillOuter outerRecord = null;
//                        if(IfspDataVerifyUtil.equals(Constans.ALL_CHNL_UNION_SYS_NO, pagyNo)){
//                            outerRecord = unionBillOuterDao.selectByPrimaryKeyDate(localRecord.getTxnSsn(), recoDate);
//                        }else{
//                            if(IfspDataVerifyUtil.isBlank(localRecord.getSettleKey())){
//                                /**
//                                 * 清算主键为空交易处于中间状态，应该使用通道流水号去三方账单查询
//                                 */
//                                outerRecord = unionBillOuterDao.selectByOrderId(localRecord.getTxnSsn(), recoDate);
//                            }else{
//                                outerRecord = unionBillOuterDao.selectByPrimaryKeyDate(localRecord.getSettleKey(), recoDate);
//                            }
//
//                        }
//                        /*
//                         * 本地有, 银联全渠道无时:
//                         *      1) 本地流水对账日期 == 当前对账日期, 本地流水进入可疑
//                         *      2) 本地流水对账日期 + 1 == 当前对账日期, 表示两个对账周期都没有找到匹配的流水, 本地流水进入单边;
//                         */
//                        if (outerRecord == null) {
//                            //判断是否可疑
//                            if(isDubious(recoDate, localRecord.getRecoDate())){
//                                //更新对账状态为: 可疑
//                                unionBillLocalDao.updateById(new UnionBillLocal(localRecord.getTxnSsn(), localRecord.getSettleKey(), RecoStatusDict.FINISH.getCode(), localRecord.getRecoDate(), recoDate, DubisFlagDict.TRUE.getCode()));
//                            }else {
//                                //加入不平列表
//                                errorList.add(getRecoErr(recoDate, localRecord, null, pagyNo, RecoResultDict.LOCAL_UA));
//                                //更新对账状态为: 已完成对账
//                                unionBillLocalDao.updateById(new UnionBillLocal(localRecord.getTxnSsn(), localRecord.getSettleKey(), RecoStatusDict.SKIP.getCode(), localRecord.getRecoDate(), recoDate));
//                            }
//                        } else {
//                            //比较流水内容
//                            RecoResultDict recoResult = compare(localRecord, outerRecord);
//                            //对平
//                            if (recoResult == RecoResultDict.IDENTICAL) {
//                                //加入对平列表
//                                identicalList.add(local2Idel(localRecord, outerRecord, recoDate));
//                            }
//                            //不平
//                            else {
//                                errorList.add(getRecoErr(recoDate, localRecord, outerRecord, pagyNo, recoResult));
//                            }
//                            //更新对账状态
//                            unionBillLocalDao.updateById(new UnionBillLocal(localRecord.getTxnSsn(), localRecord.getSettleKey(), RecoStatusDict.FINISH.getCode(), localRecord.getRecoDate(), recoDate));
//                            unionBillOuterDao.updateById(new UnionBillOuter(outerRecord.getTxnSsn(), RecoStatusDict.FINISH.getCode(), outerRecord.getRecoDate(), recoDate));
//                        }
//                    }
//                }
//                /*
//                 * 结果处理
//                 */
//                //记录平账流水
//                bthChkRsltInfoDao.insertBatch(identicalList);
//                //记录不平流水
//                billRecoErrDao.insertBatch(errorList);
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
//    private BthChkRsltInfo local2Idel(UnionBillLocal localRecord, UnionBillOuter outer, Date recoDate) {
//        BthChkRsltInfo result = new BthChkRsltInfo();
//        result.setPagyPayTxnSsn(localRecord.getTxnSsn());
//        result.setPagyPayTxnTm(localRecord.getTxnTime());
//        result.setPagyTxnTm(localRecord.getOrderDate()); //没用, 但是数据库需要
//        result.setPagySysSoaNo("0");  //没用, 但是数据库需要
//        result.setPagySysSoaVersion("0");  //没用, 但是数据库需要
//        result.setChkSuccDt(recoDate);
//        result.setChkSt(RecoStatusDict.FINISH.getCode());
//        result.setChkRst(null);
//        result.setOrderSsn(localRecord.getOrderId());
//        result.setPagySysNo(localRecord.getPagyNo());
//
//        result.setTxnAmt(localRecord.getTxnAmt() == null ? null : localRecord.getTxnAmt().longValue());
//        result.setTpamTxnFeeAmt(outer.getCustomerFee() == null ? null : Long.parseLong(outer.getCustomerFee())); //渠道手续费
//        result.setStlmSt(Constans.STLM_ST_00); //未清算
//        return result;
//    }
//
//    /**
//     * 本地流水 转换为 不平账流水
//     *
//     * @return
//     */
//    private BillRecoErr getRecoErr(Date recoDate, UnionBillLocal localRecord, UnionBillOuter outerRecord, String chnlId, RecoResultDict resultDict) {
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
//        result.setRecoDate(recoDate);
//        /*
//         * 记录本地流水信息
//         */
//        if (localRecord != null) {
//            result.setTxnSsn(localRecord.getTxnSsn());
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
//            result.setTxnTime(outerRecord.getTxnTime());
//            result.setOuterTxnType(outerRecord.getTxnType());
//            result.setOuterTxnState(outerRecord.getTxnState());
//            result.setOuterTxnAmt(outerRecord.getTransAmt());
//        }
//        result.setRecoRest(resultDict.getCode());
//        result.setProcState(ErroProcStateDict.INIT.getCode()); //状态为: 未处理
//        result.setProcDesc(resultDict.getDesc());
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
//    private RecoResultDict compare(UnionBillLocal localRecord, UnionBillOuter outerRecord) {
//        //交易状态
//        if (!StringUtils.equals(localRecord.getTxnState(), outerRecord.getTxnState())) {
//            return RecoResultDict.STATE_UI;
//        }
//        //交易类型
//        if (!StringUtils.equals(localRecord.getTxnType(), outerRecord.getTxnType())) {
//            return RecoResultDict.TYPE_UI;
//        }
//        //交易金额
//        if (!equalsAmt(localRecord.getTxnAmt(), outerRecord.getTransAmt())) {
//            return RecoResultDict.AMT_UI;
//        }
//        return RecoResultDict.IDENTICAL;
//    }
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
//        executorUnionAll = Executors.newFixedThreadPool(unionThreadCount, new ThreadFactory() {
//            AtomicInteger atomic = new AtomicInteger();
//
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "unionAllRecoHander_" + this.atomic.getAndIncrement());
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
//        if (executorUnionAll != null) {
//            log.info("线程池为null, 无需清理");
//            /*
//             * 关闭线程池
//             */
//            try {
//                executorUnionAll.shutdown();
//                if(!executorUnionAll.awaitTermination(10, TimeUnit.SECONDS)){
//                    executorUnionAll.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                System.out.println("awaitTermination interrupted: " + e);
//                executorUnionAll.shutdownNow();
//            }
//        }
//        log.info("====销毁线程池(end)====");
//    }
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
//    }


}
