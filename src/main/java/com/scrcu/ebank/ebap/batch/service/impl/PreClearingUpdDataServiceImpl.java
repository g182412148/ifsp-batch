package com.scrcu.ebank.ebap.batch.service.impl;/**
* Created by Administrator on 2019-06-12.
*/

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.PreClearingOldService;
import com.scrcu.ebank.ebap.batch.service.PreClearingService;
import com.scrcu.ebank.ebap.batch.service.PreClearingUpdDataService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
* 名称：〈清分数据抽取后状态和数据更新〉<br>
* 功能：〈功能详细描述〉<br>
* 方法：〈方法简述 - 方法描述〉<br>
* 版本：1.0 <br>
* 日期：2019-06-12 <br>
* 作者：yangqi <br>
* 说明：<br>
*/
@Service
@Slf4j
public class PreClearingUpdDataServiceImpl implements PreClearingUpdDataService {

    private static BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private static final int PAGE_SIZE = 500;

    @Resource
    private BthChkRsltInfoDao bthChkRsltInfoDao;    //对账成功结果信息

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;   // 入账明细信息

    @Resource
    private PayOrderInfoDao payOrderInfoDao;           // 订单信息

    @Resource
    private PaySubOrderInfoDao paySubOrderInfoDao;           //子订单信息

    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           // 商户基本信息

    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息

    @Resource
    private OrderTplInfoDao orderTplInfoDao;     //物流信息

    @Resource
    private MchtSettlRateCfgDao mchtSettlRateCfgDao;         //商户结算费率配置表

    @Resource
    private PreClearingOldService preClearingOldService;

    //商户信息缓存
    private Map<String,MchtContInfo> merMapInfo = new HashMap<String,MchtContInfo>();

    /**
     * 处理线程数量
     */
    @Value("${preCleaUpd.threadCount}")
    private Integer threadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${preCleaUpd.threadCapacity}")
    private Integer threadCapacity;
    /**
     * 线程池
     */
    ExecutorService executor;

    /**
     * 清分数据抽取后数据补充和更新
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public CommonResponse prepareUpd(BatchRequest request) throws Exception {
        String batchDate = request.getSettleDate();
        if(IfspDataVerifyUtil.isBlank(batchDate)){
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
        }
        int count = this.getTotalResult(batchDate);
        log.info(">>>>>>>>>>>>>>>>>>>>>check succuss order-count of " + batchDate +" is " + count);
        //分组数量
        int groupCount = (int) Math.ceil((double) count / threadCapacity);
        log.info("总分组数量[{}]页", groupCount);
        //处理结果
        List<Future> futureList = new ArrayList<>();
        initPool();
        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++) {
            int minIndex = (groupIndex - 1) * threadCapacity + 1;
            int maxIndex = groupIndex * threadCapacity;
            log.info("处理第[{}]组数据", groupIndex);
            Future future = executor.submit(new Handler(batchDate, new DataInterval(minIndex, maxIndex)));
            futureList.add(future);
        }
        /*
         * 获取处理结果
         */
        log.info("获取处理结果。。。。。。");
        for (Future future : futureList) {
            try {
                future.get(10, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("线程处理异常: ", e);
                //取消其他任务
                destoryPool();
                log.warn("其他子任务已取消.");
                //返回结果
                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
            }
        }
        /**
         * 针对补单交易，根据对账结果表未找到明细表数据，就是补单数据并且补的改版之前的交易，
         * 重跑原清分数据抽取逻辑
         */
        //1）查询未清分数据
        List<BthChkRsltInfo> chkSuccList = bthChkRsltInfoDao.selectListByStlSt(batchDate, Constans.SETTLE_STATUS_NOT_CLEARING);
        //2) 补清分数据抽取
        preClearingOldService.dataGathering(chkSuccList);
        //应答
        CommonResponse commonResponse = new CommonResponse();

        commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

        return commonResponse;
    }

    /**
     * 统计当前渠道对账成功数据量
     * @param date    对账成功日期
     * @return
     */
    private int getTotalResult(String date){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>income test.........");
        int count = 0;
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("chkSuccDt", date);
        count = bthChkRsltInfoDao.count("countChkSuccOrderByDate", m);
        return count;
    }

    /**
     * 抽取订单信息
     * @param date
     * @param min
     * @param max
     * @return
     */
    private List<BthChkRsltInfo> getDataList(String date,int min, int max){
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("chkSuccDt", date);
//        params.put("stlmSt", Constans.SETTLE_STATUS_NOT_CLEARING);
        params.put("startIdx", min);
        params.put("endIdx", max);

        List<BthChkRsltInfo> orderList = bthChkRsltInfoDao.selectList("selectChkSuccOrderByDate", params);
        return orderList;
    }

    /**
     * 计算子订单的渠道手续费
     * @param subOrderList
     * @param chkSuccOrd
     */
    private void calcChnlFee4SubOrder(List<PaySubOrderInfo> subOrderList,BthChkRsltInfo chkSuccOrd)
    {
        BigDecimal branchFeeAmtRemain = null;
        long branchFeeAmt;
        if(chkSuccOrd.getTpamTxnFeeAmt() == null)
        {
            branchFeeAmtRemain = new BigDecimal(0);
            branchFeeAmt = 0L;
        }
        else
        {
            branchFeeAmtRemain = new BigDecimal(chkSuccOrd.getTpamTxnFeeAmt());
            branchFeeAmt = chkSuccOrd.getTpamTxnFeeAmt();
        }

        if(subOrderList.size() == 1)
        {
            subOrderList.get(0).setBranchFee(chkSuccOrd.getTpamTxnFeeAmt()+"");
        }
        else
        {
            //根据子订单金额占比分摊手续费
            int index = 0;
            for(PaySubOrderInfo subOrder : subOrderList)
            {

                //设置默认值
                subOrder.setBranchFee("0");

                index++;
                //保证手续费分完全分摊
                if(index == subOrderList.size())
                {
                    subOrder.setBranchFee(branchFeeAmtRemain.longValue()+"");
                    break;
                }

                //渠道手续费
                BigDecimal currBranchFee = this.feeCalc(subOrder.getPayAmount(), chkSuccOrd.getTxnAmt(), branchFeeAmt);
                if(currBranchFee.compareTo(branchFeeAmtRemain) == 1)
                {
                    subOrder.setBranchFee(branchFeeAmtRemain.longValue()+"");
                    branchFeeAmtRemain = new BigDecimal("0");   //行内手续费已分完
                }
                else
                {
                    subOrder.setBranchFee(currBranchFee.longValue()+"");
                    branchFeeAmtRemain = branchFeeAmtRemain.subtract(currBranchFee);
                }

            }
        }
    }

    /**
     * 计算子订单的银联品牌服务费
     * @param subOrderList
     * @param chkSuccOrd
     */
    private void calcChnlFee4SubOrderUnion(List<PaySubOrderInfo> subOrderList,BthChkRsltInfo chkSuccOrd)
    {
        BigDecimal brandFeeAmtRemain = null;
        long brandFeeAmtUnion;

        if(chkSuccOrd.getUnionBrandFee() == null)
        {
            brandFeeAmtRemain = new BigDecimal(0);
            brandFeeAmtUnion = 0L;
        }
        else
        {
            brandFeeAmtRemain = new BigDecimal(chkSuccOrd.getUnionBrandFee().intValue());
            brandFeeAmtUnion = chkSuccOrd.getUnionBrandFee().intValue();
        }

        if(subOrderList.size() == 1)
        {
            subOrderList.get(0).setBrandFeeUnion(chkSuccOrd.getUnionBrandFee().intValue()+"");
        }
        else
        {
            //根据子订单金额占比分摊品牌服务费
            int index = 0;
            for(PaySubOrderInfo subOrder : subOrderList)
            {

                //设置默认值
                subOrder.setBrandFeeUnion("0");

                index++;
                //保证品牌服务费完全分摊
                if(index == subOrderList.size())
                {
                    subOrder.setBrandFeeUnion(brandFeeAmtRemain.longValue()+"");
                    break;
                }

                //品牌服务费
                BigDecimal currBrandFee = this.feeCalc(subOrder.getPayAmount(), chkSuccOrd.getTxnAmt(), brandFeeAmtUnion);
                if(currBrandFee.compareTo(brandFeeAmtRemain) == 1)
                {
                    subOrder.setBrandFeeUnion(brandFeeAmtRemain.longValue()+"");
                    brandFeeAmtRemain = new BigDecimal("0");   //品牌服务费已分完
                }
                else
                {
                    subOrder.setBrandFeeUnion(currBrandFee.longValue()+"");
                    brandFeeAmtRemain = brandFeeAmtRemain.subtract(currBrandFee);
                }

            }
        }
    }

    /**
     * 根据子订单金额在总订单金额的比例计算子订单的应收手续费
     * @param currPayAmt : 子订单金额
     * @param totalPayAmt：订单总金额
     * @param totalFee ： 总的手续费值
     * @return
     */
    private BigDecimal feeCalc(String currPayAmt,Long totalPayAmt,Long totalFee)
    {
        BigDecimal currFee = new BigDecimal(totalFee);
        currFee = currFee.multiply(new BigDecimal(currPayAmt)).divide(new BigDecimal(totalPayAmt),3,BigDecimal.ROUND_HALF_UP);
        currFee = currFee.setScale(0, BigDecimal.ROUND_HALF_UP);

        return currFee;
    }
    /**
     * 根据商户号查询商户结算信息(加缓存)
     * @param mchtId
     * @return
     */
    private MchtContInfo getMerStlInfo(String mchtId) {
        if(merMapInfo == null){
            merMapInfo = new HashMap<String,MchtContInfo>();
        }
        MchtContInfo merInfo = merMapInfo.get(mchtId);
        if(merInfo == null){
            Map<String,Object> m = new HashMap<String,Object>();
            m.put("mchtId", mchtId);
            merInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);
            merMapInfo.put(mchtId, merInfo);
        }
        return merInfo;
    }

    /**
     * 计算订单结算日期
     * 因为对账相对交易日期已经晚了一天，所以在计算时根据当前日期-1，再加上N
     * @param merStlInfo
     * @return
     */
    private String calcStlDate(MchtContInfo merStlInfo)
    {
        Date preDate = DateUtil.add(new Date(), Calendar.DAY_OF_MONTH, -1);
        Date stlDate = null;
        if(Constans.STL_TYPE_DN.equals(merStlInfo.getSettlCycleType()))
        {
            //TODO:当前都是根据T+N进行结算，后续如果考虑（D+N）节假日不进行结算，需维护节日表
            //但由于收单现有模式是结算失败的会在第二天继续结算，所以非节假日计算很难保证
            stlDate = DateUtil.add(preDate, Calendar.DAY_OF_MONTH, merStlInfo.getSettlCycleParam());
        }
        else if (Constans.STL_TYPE_TN.equals(merStlInfo.getSettlCycleType()))
        {
            stlDate = DateUtil.add(preDate, Calendar.DAY_OF_MONTH, merStlInfo.getSettlCycleParam());
        }
        else if (Constans.STL_TYPE_BY_MONTH.equals(merStlInfo.getSettlCycleType()))
        {
            int day = merStlInfo.getSettlCycleParam();
            if(day > 30)
            {
                day = 30;
            }

            String stlDate2 = DateUtil.getNextMonthDay(day);

            return stlDate2;
        }
        else
        {
            //TODO:实时结算-注意设置结算状态，避免再次结算
            stlDate = preDate;
        }

        return DateUtil.format(stlDate, "yyyyMMdd");
    }


    /**
     * 工作线程
     *
     * @param <T>
     */
    class Handler<T> implements Callable<T> {

        List<BthChkRsltInfo> chkSuccList;
        //对账日期
        private String batchDate;
        //最小行数
        private DataInterval dataInterval;

        public Handler(String batchDate, DataInterval dataInterval) {
            this.batchDate = batchDate;
            this.dataInterval = dataInterval;
        }

        @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
        public void run(){

            chkSuccList = getDataList(batchDate, dataInterval.getMin(), dataInterval.getMax());
            for(BthChkRsltInfo chkSuccOrd : chkSuccList) {
                if(!Constans.SETTLE_STATUS_NOT_CLEARING.equals(chkSuccOrd.getStlmSt())) {
                    //跳过已清分数据
                    chkSuccOrd = null;
                    continue;
                }
                /**
                 * 1.通过订单号查询明细表，查不到再通过订单号查询子订单号通过子订单号查询明细表
                 */
//                BthMerInAccDtl bthMerInAccDtl = null ;
                boolean updFlag = true;
                try {

//                    bthMerInAccDtl = bthMerInAccDtlDao.selectByTxnSeqId(chkSuccOrd.getOrderSsn());
                    //查询订单信息 加上日期利用分区
                    Map<String,Object> parameter = new HashMap<String,Object>();
                    Date clDate = null;
                    try {
                        clDate = IfspDateTime.getYYYYMMDD(batchDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        throw new IfspSystemException(e);
                    }
                    parameter.put("startDate", new DateTime(clDate).minusDays(1).toDate());
                    parameter.put("endDate", clDate);
                    parameter.put("orderSsn", chkSuccOrd.getOrderSsn());
                    PayOrderInfo orderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsnAndDate", parameter);
                    if(IfspDataVerifyUtil.isEmpty(orderInfo)){
                        orderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);
                    }
                    /**
                     * 根据对账成功表的ORDER_SSN查询订单信息，根据交易码判断是线上交易还是线下交易
                     * 如果是线上交易，根据订单号查询所有的子订单信息（主订单成功，子订单默认支付成功，如失败通过异步补机制处理）
                     */
                    if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(orderInfo.getTxnTypeNo())){
                        //线上交易处理

                        //1)查询子订单信息
                        Map<String,Object> m = new HashMap<String,Object>();
                        m.put("orderSsn", orderInfo.getOrderSsn());
                        List<PaySubOrderInfo> subOrderList = paySubOrderInfoDao.selectList("selectSubOrderByOrderId", m);
                        if(subOrderList == null || subOrderList.size() == 0) {
                            log.error(">>>>>>>>>>>>>>>线上支付找不到子订单！orderSsn = " + orderInfo.getOrderSsn());
                        } else {
                            //计算渠道手续费
                            calcChnlFee4SubOrder(subOrderList, chkSuccOrd);
                            //计算银联品牌服务费
                            if(!IfspDataVerifyUtil.isBlank(chkSuccOrd.getUnionBrandFee())) {
                                calcChnlFee4SubOrderUnion(subOrderList, chkSuccOrd);
                            }
                            //计算行内手续费
                            for(PaySubOrderInfo subOrder : subOrderList) {
                                preClearingOldService.calMerFee4SubOrder(orderInfo, subOrder, null);
                            }
                            //生成子订单入账明细信息
                            for(PaySubOrderInfo subOrder : subOrderList) {
                                //组装更新信息 更新明细表
                                BthMerInAccDtl bthMerInAccDtl = new BthMerInAccDtl();
                                bthMerInAccDtl.setTxnSeqId(subOrder.getSubOrderSsn());
                                bthMerInAccDtl.setTramFeeAmt(IfspDataVerifyUtil.isNotBlank(subOrder.getBranchFee())?subOrder.getBranchFee():"0");
                                bthMerInAccDtl.setBrandFee(IfspDataVerifyUtil.isNotBlank(subOrder.getBrandFeeUnion())?subOrder.getBrandFeeUnion():"0");
//                                String stlDate = "99991230";
//                                bthMerInAccDtl.setStlmDate(stlDate);            //清算日期
//                                bthMerInAccDtl.setInAcctDate(stlDate);          //清算日期
                                if(Constans.MCHT_SETTLE_TYPE_0.equals(orderInfo.getMchtSettleType())) {
                                    //已实时结算,手续费未清分
                                    bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
                                } else {
                                    //初始化结算状态
                                    bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_NOT_CLEARING);
                                }
                                //设置通道编号
                                if(Constans.LOAN_SYS_NO.equals(chkSuccOrd.getPagySysNo()) || Constans.LOAN_SYS_POINT.equals(chkSuccOrd.getPagySysNo())) {
                                    bthMerInAccDtl.setPagyNo(Constans.IBANK_SYS_NO);      //授信支付通道设置为本行
                                } else {
                                    bthMerInAccDtl.setPagyNo(chkSuccOrd.getPagySysNo());   //通道编号
                                }
                                int dtl = bthMerInAccDtlDao.updateByOrderSsn(bthMerInAccDtl);
                                if(dtl == 0){
                                    updFlag = false;
                                }
                            }
                        }
                    }else{
                        //线下交易
                        MchtContInfo merStlInfo = getMerStlInfo(orderInfo.getMchtId());
                        //组装更新信息 更新明细表
                        BthMerInAccDtl bthMerInAccDtl = new BthMerInAccDtl();
                        bthMerInAccDtl.setTxnSeqId(chkSuccOrd.getOrderSsn());
                        bthMerInAccDtl.setTramFeeAmt(chkSuccOrd.getTpamTxnFeeAmt()!=null?chkSuccOrd.getTpamTxnFeeAmt()+"":"0");//渠道手续费
                        bthMerInAccDtl.setBrandFee(chkSuccOrd.getUnionBrandFee()!=null?chkSuccOrd.getUnionBrandFee()+"":"0");
                        //根据商户结算（合同）信息计算订单结算日期
                        String stlDate = calcStlDate(merStlInfo);
                        bthMerInAccDtl.setStlmDate(stlDate);            //清算日期
                        bthMerInAccDtl.setInAcctDate(stlDate);          //清算日期
                        bthMerInAccDtl.setPagyNo(chkSuccOrd.getPagySysNo());   //通道编号
                        if(Constans.MCHT_SETTLE_TYPE_0.equals(orderInfo.getMchtSettleType())) {
                            //已实时结算,手续费未清分
                            bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
                        } else {
                            //初始化结算状态
                            bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_NOT_CLEARING);
                        }
                        int dtl = bthMerInAccDtlDao.updateByOrderSsn(bthMerInAccDtl);
                        if(dtl == 0){
                            updFlag = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
                if(updFlag){
                    chkSuccOrd.setStlmSt(Constans.SETTLE_STATUS_CLEARING); // 清分中
                    bthChkRsltInfoDao.updateByOrderSsnSt(chkSuccOrd);
                }
            }
        }

        @Override
        public T call() throws Exception {
            try{
                this.run();
            } catch (Exception e) {
                log.error("线程处理未知异常:", e);
                log.debug("====处理{}数据(error end)====", dataInterval);
                throw e;
            }
            finally {
            }

            return null;
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
                return new Thread(r, "unionAllRecoHander_" + this.atomic.getAndIncrement());
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
