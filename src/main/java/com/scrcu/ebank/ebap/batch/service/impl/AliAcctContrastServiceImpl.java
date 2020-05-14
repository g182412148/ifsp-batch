package com.scrcu.ebank.ebap.batch.service.impl;



import javax.annotation.Resource;


import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.AliBillLocal;
import com.scrcu.ebank.ebap.batch.bean.dto.AliBillOuter;
import com.scrcu.ebank.ebap.batch.bean.dto.BillRecoErr;
import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.ErroProcStateDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoResultDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.dao.test.BillRecoErrDao;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.service.AliAcctContrastService;


import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ljy
 * @date 2019-05-23
 */
@Service
@Slf4j
public class AliAcctContrastServiceImpl implements AliAcctContrastService {


    @Resource
    private AliBillOuterDao outerDao;

    @Resource
    private AliBillLocalDao localDao;

    @Resource
    private BthChkRsltInfoDao identicalDao;

    @Resource
    private BillRecoErrDao recoErrDao;

    @Resource
    private AliBillResultDao resultDao;


    /**
     * 处理线程数量
     */
//    @Value("${aliReco.threadCount}")
//    private Integer threadCount;
    /**
     * 每个线程处理数据量
     */
//    @Value("${aliReco.threadCapacity}")
//    private Integer threadCapacity;
    /**
     * 线程池
     */
//    ExecutorService executor;
	

	/**
     * zfb通道对账单
     * @param req
     * @return
	 * @throws Exception 
     */
	@Override
	public CommonResponse aliBillContrast(AcctContrastRequest req)  {
        log.info("---------------------支付宝通道系统vs支付宝对账开始--------------------");
        long start=System.currentTimeMillis();
        //获取的订单数据的交易日期
        Date txnDate = DateUtil.getDate(req.getSettleDate());
        log.info("交易日期: " + DateUtil.toDateString(txnDate));
        //计算对账日期 = 交易日期 + 1
        Date recoDate = DateUtil.getAfterDate(req.getSettleDate(),1);
        log.info("对账日期: " + DateUtil.toDateString(recoDate));

        //数据清理
        clear(recoDate);
        /** 自本行起为本次支付宝对账改造代码 start */
        reco(recoDate);
        /** end */
//        //初始化线程池
//        initPool();
//        try {
//            //对账
//            reco(recoDate);
//        } finally {
//            //释放线程池
//            destoryPool();
//        }
//
        log.info("支付宝对账结束，对账日期【{}】，总耗时【{}】",DateUtil.toDateString(recoDate),System.currentTimeMillis()-start);
        return new CommonResponse();
    }


    /**
     * 对账
     * @param recoDate
     */
    private void reco(Date recoDate) {
        long start=System.currentTimeMillis();
	    //统计本地流水表对账日下总条数
        int count = localDao.count(recoDate);
        /** 自本行起为本次支付宝对账改造代码 start */
        log.info("本地流水表数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
        //将本地流水表(ALI_BILL_LOCAL)与三方流水表(ALI_BILL_OUTER)进行比对，比对结果插入至支付宝对账结果表(ALI_BILL_RESULT)
        int aliChkResultCount = resultDao.insertAliResult(recoDate);
        log.info("本次支付宝对账共勾兑流水数量【{}】，耗时【{}】",aliChkResultCount,System.currentTimeMillis()-start);
        //统计支付宝对账结果表中本地可疑和本地单边流水数量
        int localDebiousCount = resultDao.countLocal();
        log.info("本次支付宝对账本地可疑和本地单边流水数量【{}】，耗时【{}】",localDebiousCount,System.currentTimeMillis()-start);
        //统计支付宝对账结果表中三方可疑和三方单边流水数量
        int outerDebiousCount = resultDao.countOuter();
        log.info("本次支付宝对账三方可疑和三方单边流水数量【{}】，耗时【{}】",outerDebiousCount,System.currentTimeMillis()-start);
        //将差错记录插入到本地差错记录表
        int errCount = recoErrDao.insertAliErrResult(recoDate);
        log.info("本次支付宝对账差错记录数量【{}】，耗时【{}】",errCount,System.currentTimeMillis()-start);
        //将支付宝对账结果插入到对账结果表
        int chkResultCount = identicalDao.insertAliChkResult(recoDate);
        log.info("本次支付宝对账已对账记录数量【{}】，耗时【{}】",chkResultCount,System.currentTimeMillis()-start);
        //更新本地流水表
        int localCount = localDao.updateByResult(recoDate);
        log.info("本次支付宝对账共勾兑本地流水数量【{}】，耗时【{}】",localCount,System.currentTimeMillis()-start);
        //更新三方流水表
        int outerCount = outerDao.updateByResult(recoDate);
        log.info("本次支付宝对账共勾兑三方流水数量【{}】，耗时【{}】",outerCount,System.currentTimeMillis()-start);
        /** end */

//        //分组数量
//        int groupCount = (int) Math.ceil((double) count / threadCapacity);
//
//        /*
//         * 已本地为准 勾兑 第三方
//         */
//        //处理结果
//        List<Future> futureList = new ArrayList<>();
//        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++) {
//            int minIndex = (groupIndex-1)* threadCapacity + 1;
//            int maxIndex = groupIndex* threadCapacity ;
//            Future future = executor.submit(new Handler(recoDate, new DataInterval(minIndex, maxIndex)));
//            futureList.add(future);
//        }
//
//
//        /**
//         * 获取处理结果
//         */
//        for (Future future : futureList) {
//            try {
//                future.get(10,TimeUnit.MINUTES);
//            } catch (Exception e) {
//                log.error("对账线程处理异常: ", e);
//                //取消其他任务
//                destoryPool();
//                log.warn("其他子任务已取消.");
//                //返回结果
//                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常"+ e.getMessage());
//            }
//        }
//
//        /*
//         * 处理三方未对账数据
//         *      1) 可疑交易, 更改对账状态为可疑
//         *      2) 单边交易, 更新对账状态为已对账; 记入对账差错表;
//         */
//        List<AliBillOuter> outerList =  outerDao.queryNotReco(recoDate);
//        if (IfspDataVerifyUtil.isEmptyList(outerList)){
//            log.debug("支付宝对账文件无单边或可疑明细");
//        }else {
//
//            for (AliBillOuter aliBillOuter : outerList) {
//                //可疑交易
//                if (isDubious(recoDate, aliBillOuter.getRecoDate())){
//                    outerDao.updateById(new AliBillOuter(aliBillOuter.getTxnSsn(), RecoStatusDict.DUBIOUS, recoDate));
//                }else {
//                    recoErrDao.insert(getRecoErr(recoDate,null, aliBillOuter, Constans.ALI_SYS_NO, RecoResultDict.OUTER_UA));
//                    outerDao.updateById(new AliBillOuter(aliBillOuter.getTxnSsn(), RecoStatusDict.FINISH, recoDate));
//                }
//            }
//
//        }


    }

    /**
     * 生成差错对象
     * @param recoDate
     * @param localRecord
     * @param outerRecord
     * @param chnlId
     * @param resultDict
     * @return
     */
//    private BillRecoErr getRecoErr(Date recoDate, AliBillLocal localRecord, AliBillOuter outerRecord, String chnlId, RecoResultDict resultDict) {
//        if (localRecord == null && outerRecord == null) {
//            throw new IllegalArgumentException("localRecord and outerRecord is null");
//        }
//        if (org.apache.commons.lang.StringUtils.isBlank(chnlId)) {
//            throw new IllegalArgumentException("chnlId is blank");
//        }
//        if (resultDict == null) {
//            throw new IllegalArgumentException("resultDict is null");
//        }
//
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
//
//        result.setRecoRest(resultDict.getCode());
//        result.setProcState(ErroProcStateDict.INIT.getCode()); //状态为: 未处理
//        result.setProcDesc("未处理");
//        result.setUpdDate(new Date());
//
//        return result;
//
//
//
//
//    }
//
//
//    /**
//     * 是否可疑
//     * @param recoDate
//     * @param recordRecoDate
//     * @return
//     */
//    private boolean isDubious(Date recoDate, Date recordRecoDate) {
//        if (recoDate.compareTo(recordRecoDate) == 0 ){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//
//    /**
//     * 初始化线程池
//     */
//    private void initPool() {
//        destoryPool();
//
//        /**
//         * 构建线程池
//         */
//        log.info("====初始化线程池(start)====");
//        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
//            AtomicInteger atomic = new AtomicInteger();
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "aliRecoHander_" + this.atomic.getAndIncrement());
//            }
//        });
//        log.info("====初始化线程池(end)====");
//
//    }
//
//
//    /**
//     * 销毁线程池
//     */
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






    /**
     * 清理数据
     * @param recoDate
     */
    private void clear(Date recoDate) {
        long start=System.currentTimeMillis();
        int localRecCount = localDao.recovery(recoDate);
        log.info("恢复本地对账明细, 数量【{}】，耗时【{}】" , localRecCount,System.currentTimeMillis()-start);
        int localDubiousRecCount = localDao.recoveryDubious(recoDate);
        log.info("恢复本地可疑对账明细, 数量【{}】，耗时【{}】" , localDubiousRecCount,System.currentTimeMillis()-start);
        int outerRecCount = outerDao.recovery(recoDate);
        log.info("恢复三方对账明细, 数量【{}】，耗时【{}】" , outerRecCount,System.currentTimeMillis()-start);
        int outerDubiousRecCount = outerDao.recoveryDubious(recoDate);
        log.info("恢复三方可疑对账明细, 数量【{}】，耗时【{}】" , outerDubiousRecCount,System.currentTimeMillis()-start);
        int resultCount = identicalDao.deleteAliByStlmDateAndPagySysNo(IfspDateTime.getYYYYMMDD(recoDate), Constans.ALI_SYS_NO + " ");
        log.info("清空平账结果, 数量【{}】，耗时【{}】" , resultCount,System.currentTimeMillis()-start);
        int errCount = recoErrDao.clear(recoDate, Constans.ALI_SYS_NO);
        log.info("清空差错结果, 数量【{}】，耗时【{}】" , errCount,System.currentTimeMillis()-start);
        int aliResultCount = resultDao.clear();
        log.info("清空支付宝对账结果, 数量【{}】，耗时【{}】" , aliResultCount,System.currentTimeMillis()-start);

    }


    /**
     * 工作线程
     * @param <T>
     */
//    class Handler<T> implements Callable<T> {
//
//
//        //对账日期
//        private Date recoDate;
//        //数据区间
//        private DataInterval dataInterval;
//
//        public Handler(Date recoDate, DataInterval dataInterval) {
//            this.recoDate = recoDate;
//            this.dataInterval = dataInterval;
//        }
//
//        @Override
//        public T call() throws Exception {
//
//            log.debug("====处理{}数据(start)====", dataInterval);
//
//            try {
//                /*
//                 * 结果集合
//                 */
//                //对平
//                List<BthChkRsltInfo> identicalList = new ArrayList<>();
//                //不平: 包括本地单边 和 不平
//                List<BillRecoErr> errorList = new ArrayList<>();
//
//                /*
//                 * 对账
//                 */
//                //分页查询出本地流水
//                List<AliBillLocal> localRecords = localDao.queryByRange(recoDate, dataInterval.getMin(), dataInterval.getMax());
//
//                if (IfspDataVerifyUtil.isEmptyList(localRecords)){
//                    log.warn("{}数据为null", dataInterval);
//                }else {
//                    log.debug("{},数据容量:{}", dataInterval, localRecords.size());
//
//                    /*
//                     * 以本地流水为准, 匹配三方流水:
//                     *      1)匹配不上, 本地单边;
//                     *      2)匹配上了, 对比流水内容;
//                     */
//                    for (AliBillLocal localRecord : localRecords) {
//                        AliBillOuter outerRecord = outerDao.queryByIdAndDate(localRecord.getTxnSsn() ,recoDate );
//                        /*
//                         * 本地有, 本行文件无时:
//                         *      1) 本地流水对账日期 == 当前对账日期, 本地流水进入可疑
//                         *      2) 本地流水对账日期 + 1 == 当前对账日期, 表示两个对账周期都没有找到匹配的流水, 本地流水进入单边;
//                         */
//                        if (outerRecord == null) {
//                            //判断是否可疑
//                            if(isDubious(recoDate, localRecord.getRecoDate())){
//                                //更新对账状态为: 可疑
//                                localDao.updateById(new AliBillLocal(localRecord.getTxnSsn(), RecoStatusDict.DUBIOUS, recoDate));
//                            }else {
//                                //加入不平列表
//                                errorList.add(getRecoErr(recoDate, localRecord, null, Constans.ALI_SYS_NO, RecoResultDict.LOCAL_UA));
//                                //更新对账状态为: 已完成对账
//                                localDao.updateById(new AliBillLocal(localRecord.getTxnSsn(), RecoStatusDict.FINISH, recoDate));
//                            }
//                        } else {
//                            //比较流水内容
//                            RecoResultDict recoResult = compare(localRecord, outerRecord);
//                            //对平
//                            if (recoResult == RecoResultDict.IDENTICAL) {
//                                //加入对平列表
//                                identicalList.add(getIdel(recoDate, localRecord,outerRecord));
//                            }
//                            //不平
//                            else {
//                                errorList.add(getRecoErr(recoDate, localRecord, outerRecord, Constans.ALI_SYS_NO, recoResult));
//                            }
//                            //更新对账状态
//                            localDao.updateById(new AliBillLocal(localRecord.getTxnSsn(), RecoStatusDict.FINISH, recoDate));
//                            outerDao.updateById(new AliBillOuter(localRecord.getTxnSsn(), RecoStatusDict.FINISH, recoDate));
//                        }
//
//                    }
//
//                }
//
//                /*
//                 * 结果处理
//                 */
//                //记录平账流水
//                identicalDao.insertBatch(identicalList);
//                //记录不平流水
//                recoErrDao.insertBatch(errorList);
//                log.debug("====处理{}数据(end)====", dataInterval);
//
//            } catch (Exception e) {
//                log.error("对账未知异常:", e);
//                log.debug("====处理{}数据(error end)====", dataInterval);
//                throw e;
//            }
//
//            return null;
//        }
//    }
//
//
//    /**
//     * 流水比较, 本行只比较交易状态
//     * @param localRecord
//     * @param outerRecord
//     * @return
//     */
//    private RecoResultDict compare(AliBillLocal localRecord, AliBillOuter outerRecord) {
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
//
//    }
//
//    /**
//     * 比较金额
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
//
//
//    }
//
//
//
//    /**
//     * 本地流水 转换为 平账流水
//     *
//     * @return
//     */
//    private BthChkRsltInfo getIdel(Date recoDate, AliBillLocal localRecord , AliBillOuter outerRecord) {
//        BthChkRsltInfo result = new BthChkRsltInfo();
//        result.setPagyPayTxnSsn(localRecord.getTxnSsn());
//        result.setPagyPayTxnTm(localRecord.getTxnTime());
//
//        result.setPagyTxnTm(localRecord.getTxnTime()); //没用, 但是数据库需要
//        result.setPagySysSoaNo("0");  //没用, 但是数据库需要
//        result.setPagySysSoaVersion("0");  //没用, 但是数据库需要
//        /*
//         * 清分需要用
//         */
//        // 订单号
//        result.setOrderSsn(localRecord.getOrderId());
//        //交易金额
//        result.setTxnAmt(localRecord.getTxnAmt().longValue());
//        //三方手续费
//        result.setTpamTxnFeeAmt(outerRecord.getFeeAmt() == null? 0 : outerRecord.getFeeAmt().longValue());
//        //对账成功日期
//        result.setChkSuccDt(recoDate);
//        //通道号
//        result.setPagySysNo(Constans.ALI_SYS_NO);
//        //未清算
//        result.setStlmSt(Constans.STLM_ST_00);
//        return result;
//    }




}
