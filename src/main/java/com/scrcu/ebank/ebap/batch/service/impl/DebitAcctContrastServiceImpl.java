package com.scrcu.ebank.ebap.batch.service.impl;



import javax.annotation.Resource;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.ErroProcStateDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoResultDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.dao.test.BillRecoErrDao;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.service.DebitAcctContrastService;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本行对账
 * @author ljy
 * @date 2019-05-17
 */

@Service
@Slf4j
public class DebitAcctContrastServiceImpl implements DebitAcctContrastService {

	@Resource
    private IbankBillOutDao outerDao;

	@Resource
    private IbankBillLocalDao localDao;

    @Resource
    private BthChkRsltInfoDao identicalDao;

    @Resource
    private BillRecoErrDao recoErrDao;

    @Resource
    private IbankBillResultDao resultDao;


    /**
     * 处理线程数量
     */
//    @Value("${ibankReco.threadCount}")
//    private Integer threadCount;
    /**
     * 每个线程处理数据量
     */
//    @Value("${ibankReco.threadCapacity}")
//    private Integer threadCapacity;
    /**
     * 线程池
     */
//    ExecutorService executor;


	
	@Override
	public CommonResponse debitBillContrast(AcctContrastRequest req) throws Exception {
        //获取交易日期
        Date txnDate = getRecoDate(req.getSettleDate());
        //计算对账日期 = 交易日期 + 1
        Date recoDate = new DateTime(txnDate).plusDays(1).toDate();
        //数据清理
        clear(recoDate);
        /** 自本行起为本次本行对账改造代码 start */
        reco(recoDate);
        /** end */
        //初始化线程池
//        initPool();
//        try {
//            //对账
//            reco(recoDate);
//        } finally {
//            //释放线程池
//            destoryPool();
//        }

        return new CommonResponse();
    }

    /**
     * 初始化线程池
     */
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
//                return new Thread(r, "iBankRecoHander_" + this.atomic.getAndIncrement());
//            }
//        });
//        log.info("====初始化线程池(end)====");
//
//    }



    /**
     * 销毁线程池
     */
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
        log.info("恢复本地对账明细，数量【{}】，耗时【{}】" ,localRecCount,System.currentTimeMillis()-start);
        int localDubiousRecCount = localDao.recoveryDubious(recoDate);
        log.info("恢复本地可疑对账明细，数量【{}】，耗时【{}】" , localDubiousRecCount,System.currentTimeMillis()-start);
        int outerRecCount = outerDao.recovery(recoDate);
        log.info("恢复核心对账明细，数量【{}】，耗时【{}】" , outerRecCount,System.currentTimeMillis()-start);
        int outerDubiousRecCount = outerDao.recoveryDubious(recoDate);
        log.info("恢复核心可疑对账明细，数量【{}】，耗时【{}】" , outerDubiousRecCount,System.currentTimeMillis()-start);
        int resultCount = identicalDao.deleteAliByStlmDateAndPagySysNo(IfspDateTime.getYYYYMMDD(recoDate), Constans.IBANK_SYS_NO + " ");
        log.info("清空平账结果，数量【{}】，耗时【{}】" , resultCount,System.currentTimeMillis()-start);
        int errCount = recoErrDao.clear(recoDate, Constans.IBANK_SYS_NO);
        log.info("清空差错结果，数量【{}】，耗时【{}】" , errCount,System.currentTimeMillis()-start);
        int ibankResultCount = resultDao.clear();
        log.info("清空本行对账结果，数量【{}】，耗时【{}】" , ibankResultCount,System.currentTimeMillis()-start);
    }


    /**
     * 本行对账
     * @param recoDate
     */
    private void reco(Date recoDate) {
        long start=System.currentTimeMillis();
        /*
         * 查询本地流水表的数据总数
         */
        int count = localDao.count(recoDate);
        /** 自本行起为本次本行对账改造代码 start */
        log.info("本地流水表数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
        //将本地流水表(IBANK_BILL_LOCAL)与三方流水表(IBANK_BILL_OUTER)进行比对，比对结果插入至本行对账结果表(IBANK_BILL_RESULT)
        int ibankChkResultCount = resultDao.insertIBankResult(recoDate);
        log.info("本次本行对账共勾兑流水数量【{}】，耗时【{}】",ibankChkResultCount,System.currentTimeMillis()-start);
        //统计本行对账结果表中本地可疑和本地单边流水数量
        int localDebiousCount = resultDao.countLocal();
        log.info("本次本行对账本地可疑和本地单边流水数量【{}】，耗时【{}】",localDebiousCount,System.currentTimeMillis()-start);
        //统计本行对账结果表中三方可疑和三方单边流水数量
        int outerDebiousCount = resultDao.countOuter();
        log.info("本次本行对账三方可疑和三方单边流水数量【{}】，耗时【{}】",outerDebiousCount,System.currentTimeMillis()-start);
        //将差错记录插入到本地差错记录表
        int errCount = recoErrDao.insertIBankErrResult(recoDate);
        log.info("本次本行对账差错记录数量【{}】，耗时【{}】",errCount,System.currentTimeMillis()-start);
        //将本行对账结果插入到对账结果表
        int chkResultCount = identicalDao.insertIBankChkResult(recoDate);
        log.info("本次本行对账已对账记录数量【{}】，耗时【{}】",chkResultCount,System.currentTimeMillis()-start);
        //更新本地流水表
        int localCount = localDao.updateByResult(recoDate);
        log.info("本次本行对账共勾兑本地流水数量【{}】，耗时【{}】",localCount,System.currentTimeMillis()-start);
        //更新三方流水表
        int outerCount = outerDao.updateByResult(recoDate);
        log.info("本次本行对账共勾兑三方流水数量【{}】，耗时【{}】",outerCount,System.currentTimeMillis()-start);
        /** end */
        //分组数量
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
//
//        /*
//         * 获取处理结果
//         */
//        for (Future future : futureList) {
//            try {
//                future.get(10, TimeUnit.MINUTES);
//            } catch (Exception e) {
//                log.error("对账线程处理异常: ", e);
//                //取消其他任务
//                destoryPool();
//                log.warn("其他子任务已取消.");
//                //返回结果
//                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常"+ e.getMessage());
//            }
//        }
//        /*
//         * 处理三方未对账数据
//         *      1) 可疑交易, 更改对账状态为可疑
//         *      2) 单边交易, 更新对账状态为已对账; 记入对账差错表;
//         */
//        List<IbankBillOuter> outerList = outerDao.queryNotReco(recoDate);
//
//        if (IfspDataVerifyUtil.isEmptyList(outerList)){
//            log.debug("本行对账文件无单边或可疑明细");
//        }else {
//
//            for (IbankBillOuter ibankBillOuter : outerList) {
//                //可疑交易
//                if (isDubious(recoDate, ibankBillOuter.getRecoDate())){
//                    outerDao.updateById(new IbankBillOuter(ibankBillOuter.getTxnSsn(), RecoStatusDict.DUBIOUS, recoDate));
//                }else {
//                    recoErrDao.insert(getRecoErr(recoDate,null, ibankBillOuter, Constans.IBANK_SYS_NO, RecoResultDict.OUTER_UA));
//                    outerDao.updateById(new IbankBillOuter(ibankBillOuter.getTxnSsn(), RecoStatusDict.FINISH, recoDate));
//                }
//            }
//
//        }

    }


    /**
     * 工作线程
     * @param <T>
     */
//    class Handler<T> implements Callable<T>{
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
//
//        @Override
//        public T call() throws Exception {
//            try {
//                log.debug("====处理{}数据(start)====", dataInterval);
//
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
//                List<IbankBillLocal> localRecords = localDao.queryByRange(recoDate, dataInterval.getMin(), dataInterval.getMax());
//                if (IfspDataVerifyUtil.isEmptyList(localRecords)){
//                    log.warn("{}数据为null", dataInterval);
//                }else {
//                    log.debug("{},数据容量:{}", dataInterval, localRecords.size());
//                    /*
//                     * 以本地流水为准, 匹配三方流水:
//                     *      1)匹配不上, 本地单边;
//                     *      2)匹配上了, 对比流水内容;
//                     */
//                    for (IbankBillLocal localRecord : localRecords) {
//                        //根据本地流水查询本行流水
//                        IbankBillOuter outerRecord = outerDao.queryByIdAndDate(localRecord.getTxnSsn(),recoDate);
//                        /*
//                         * 本地有, 本行文件无时:
//                         *      1) 本地流水对账日期 == 当前对账日期, 本地流水进入可疑
//                         *      2) 本地流水对账日期 + 1 == 当前对账日期, 表示两个对账周期都没有找到匹配的流水, 本地流水进入单边;
//                         */
//                        if (outerRecord == null) {
//                            //判断是否可疑
//                            if(isDubious(recoDate, localRecord.getRecoDate())){
//                                //更新对账状态为: 可疑
//                                localDao.updateById(new IbankBillLocal(localRecord.getTxnSsn(), RecoStatusDict.DUBIOUS, recoDate));
//                            }else {
//                                //加入不平列表
//                                errorList.add(getRecoErr(recoDate, localRecord, null, Constans.IBANK_SYS_NO, RecoResultDict.LOCAL_UA));
//                                //更新对账状态为: 已完成对账
//                                localDao.updateById(new IbankBillLocal(localRecord.getTxnSsn(), RecoStatusDict.FINISH, recoDate));
//                            }
//                        } else {
//                            //比较流水内容
//                            RecoResultDict recoResult = compare(localRecord, outerRecord);
//                            //对平
//                            if (recoResult == RecoResultDict.IDENTICAL) {
//                                //加入对平列表
//                                identicalList.add(getIdel(recoDate, localRecord));
//                            }
//                            //不平
//                            else {
//                                errorList.add(getRecoErr(recoDate, localRecord, outerRecord, Constans.IBANK_SYS_NO, recoResult));
//                            }
//                            //更新对账状态
//                            localDao.updateById(new IbankBillLocal(localRecord.getTxnSsn(), RecoStatusDict.FINISH, recoDate));
//                            outerDao.updateById(new IbankBillOuter(localRecord.getTxnSsn(), RecoStatusDict.FINISH, recoDate));
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
//            } catch (Exception e) {
//                log.error("对账未知异常:", e);
//                log.debug("====处理{}数据(error end)====", dataInterval);
//                throw e;
//            }
//
//
//            return null;
//        }
//    }


    /**
     * 流水比较, 本行只比较交易状态
     * @param localRecord
     * @param outerRecord
     * @return
     */
//    private RecoResultDict compare(IbankBillLocal localRecord, IbankBillOuter outerRecord) {
//        //交易状态
//        if (!org.apache.commons.lang.StringUtils.equals(localRecord.getTxnState(), outerRecord.getTxnState())) {
//            return RecoResultDict.STATE_UI;
//        }
//        return RecoResultDict.IDENTICAL;
//
//    }



    /**
     * 本地流水 转换为 平账流水
     *
     * @return
     */
//    private BthChkRsltInfo getIdel(Date recoDate, IbankBillLocal localRecord) {
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
//        // 对账成功日期
//        result.setChkSuccDt(recoDate);
//        //通道号
//        result.setPagySysNo(Constans.IBANK_SYS_NO);
//        //未清算
//        result.setStlmSt(Constans.STLM_ST_00);
//        return result;
//    }




    /**
     * 对账不平入差错
     * @param recoDate
     * @param localRecord
     * @param outerRecord
     * @param chnlId
     * @param resultDict
     * @return
     */
//    private BillRecoErr getRecoErr(Date recoDate, IbankBillLocal localRecord, IbankBillOuter outerRecord, String chnlId, RecoResultDict resultDict) {
//        if (localRecord == null && outerRecord == null) {
//            throw new IllegalArgumentException("localRecord and outerRecord is null");
//        }
//        if (org.apache.commons.lang.StringUtils.isBlank(chnlId)) {
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


    /**
     * 是否为可疑
     * @param recoDate
     * @param recordRecoDate
     * @return
     */
//    private boolean isDubious(Date recoDate, Date recordRecoDate) {
//        if (recordRecoDate.compareTo(recoDate) == 0){
//            return true;
//        }else {
//            return false;
//        }
//
//
//    }


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
            return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").toDate(); //todo 改成常数
        }catch (Exception e){
            log.error("对账日期格式错误: ", e);
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期格式错误");
        }
    }

}
