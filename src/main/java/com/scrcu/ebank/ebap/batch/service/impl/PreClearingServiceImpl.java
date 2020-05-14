package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.vo.CacheMchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.vo.CacheMchtContInfo;
import com.scrcu.ebank.ebap.batch.bean.vo.CacheMerStlRateInfo;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.PreClearingService;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-06-27 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Service
@Slf4j
public class PreClearingServiceImpl implements PreClearingService
{
    private static BigDecimal ONE_HUNDRED = new BigDecimal(100);

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
    private KeepAccInfoDao keepAccInfoDao;         //记账表


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

    public CommonResponse prepare(BatchRequest request) throws Exception
    {
//		String batchDate = request.getSettleDate();
//		if(IfspDataVerifyUtil.isBlank(batchDate))
//		{
//			batchDate = DateUtil.format(new Date(), "yyyyMMdd");
//		}
//		int count = this.getTotalResult(batchDate);
//		log.info(">>>>>>>>>>>>>>>>>>>>>check succuss order-count of " + batchDate +" is " + count);
//
//		int pageCount = (int)Math.ceil((double) count / threadCapacity);
//		log.info("共分为[{}]组处理", pageCount);
//		//处理结果
//		List<Future> futureList = new ArrayList<>();
//		initPool();
//		try {
//			for(int pageIdx = 1 ; pageIdx <= pageCount; pageIdx ++ ) {
//				int minIndex = (pageIdx - 1) * threadCapacity + 1;
//				int maxIndex = pageIdx * threadCapacity;
//				log.info("处理第[{}]组数据", pageIdx);
//				PreClearingTask preClearingTask = new PreClearingTask(batchDate,new DataInterval(minIndex, maxIndex),preClearingService);
//				Future<CommonResponse> future = executor.submit(preClearingTask);
//				futureList.add(future);
//			}
//			/*
//			 * 获取处理结果
//			 */
//			for (Future future : futureList) {
//				try {
//					future.get(10, TimeUnit.MINUTES);
//				} catch (Exception e) {
//					log.error("清分数据抽取线程处理异常: ", e);
//					//取消其他任务
//					destoryPool();
//					log.warn("其他子任务已取消.");
//					//返回结果
//					throw new IfspSystemException(SystemConfig.getSysErrorCode(), "清分数据抽取线程处理异常:" + e.getMessage());
//				}
//			}
//		}finally {
//			//释放线程池
//			destoryPool();
//		}


        //应答
        CommonResponse commonResponse = new CommonResponse();

        commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

        return commonResponse;
    }

    @Override
    public void calcMerFee4SubOrder(PayOrderInfo orderInfo, PaySubOrderInfo subOrder) {
        this.calMerFee4SubOrder(orderInfo,subOrder,null,null);
    }

    @Override
    public BigDecimal calMerFee4$Order(PayOrderInfo orderInfo) {
        BigDecimal txnAmt = new BigDecimal(orderInfo.getPayAmt());
        if(null != orderInfo.getBankCouponAmt()){
            txnAmt = txnAmt.add(new BigDecimal(orderInfo.getBankCouponAmt()));
        }
        BthMerInAccDtl orderStlInfo = new BthMerInAccDtl();

        String chnlNo;
        if(Constans.TXN_TYPE_O1000002.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_O1000003.equals(orderInfo.getTxnTypeNo()))
        {
            //主扫交易/微信公众号() 取CHL_NO
            chnlNo = orderInfo.getChlNo();
        }
        else{
            //被扫支付
            chnlNo = orderInfo.getAcptChlNo();   //如果是退款交易，渠道也取acptChlNo
        }
        String proId;
        if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
        {
            proId = Constans.PRO_ID_ONLINE;
        }
        else
        {
            proId = Constans.PRO_ID_SM;
        }
        return this.calcMerFee4Order(txnAmt,orderInfo.getMchtId(), orderInfo,chnlNo,proId);
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

    /**
     * 抽取订单信息
     *
     * @param date    对账成功日期
     * @param pageIdx 分页页码 ： 页码从1开始
     * @return
     */
    private List<BthChkRsltInfo> getDataList(String date, int pageIdx)
    {
        int startIdx = (pageIdx - 1) * threadCapacity + 1;
        int endIdx = pageIdx * threadCapacity;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("chkSuccDt", date);
        params.put("stlmSt", Constans.SETTLE_STATUS_NOT_CLEARING);
        params.put("startIdx", startIdx);
        params.put("endIdx", endIdx);

        List<BthChkRsltInfo> orderList = bthChkRsltInfoDao.selectList("selectChkSuccOrderByDate", params);
        return orderList;
    }

//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
//    private void execute(List<BthChkRsltInfo> chkSuccList, String batchDate)
//    {
        //this.dataGathering(chkSuccOrd);
        //this.updateOrderStlStatus(chkSuccOrd);
//        PreClearingTask preClearingTask = new PreClearingTask(chkSuccList);
//    }

    /**
     * 根据对账成功订单信息查询订单信息及商户信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Deprecated
    public void dataGathering(List<BthChkRsltInfo> chkSuccOrdList, String batchDate) throws Exception
    {
        long start=System.currentTimeMillis();
        List<BthMerInAccDtl> bthMerInAccDtlList = new ArrayList<BthMerInAccDtl>();
        //查询订单信息 加上日期利用分区
        Map<String, Object> parameter = new HashMap<String, Object>();
        Date clDate = null;
        try
        {
            clDate = IfspDateTime.getYYYYMMDD(batchDate);
        }
        catch (ParseException e)
        {
            log.error("日期转换错误【{}】",batchDate,e);
            throw new IfspSystemException(e);
        }

        List<String> orderSsnList = chkSuccOrdList.stream().map(BthChkRsltInfo::getOrderSsn).collect(Collectors.toList());   //生成orderSSn为泛型的List

        parameter.put("startDate", new DateTime(clDate).minusDays(1).toDate());
        parameter.put("endDate", clDate);
        parameter.put("orderSsnList", orderSsnList);
        //考虑到PayOrderInfo表为分区表，所以首先带日期进行查询，并形成Map<订单号,订单对象>结构
        List<PayOrderInfo> orderInfoList = payOrderInfoDao.selectList("selectOrderInfoByReqOrderSsnListAndDate", parameter);   //增加按orderSsnList批量查询条件
        Map<String, PayOrderInfo> payOrderInfoMap = orderInfoList.stream().collect(Collectors.toMap(PayOrderInfo::getOrderSsn, a -> a,(k1,k2)->k1));

        log.info("查询订单信息集合完成，查询数量【{}】，耗时【{}】",orderSsnList.size(),System.currentTimeMillis()-start);
        for(String orderSsn:orderSsnList)
        {
            /**
             * 如果payOrderInfoMap不存在orderSsn，就意味着使用selectOrderInfoByReqOrderSsnAndDate查询时没有查到PayOrderInfo
             * 这样就去掉日期条件只使用orderSsn再次查询
             * 查询后，将orderInfo补充放入orderInfoList中
             * 1、尽可能享受分区带来的性能红利
             * 2、为保险起见再次补查
             */
            if(!payOrderInfoMap.containsKey(orderSsn))
            {
                parameter.put("orderSsn", orderSsn);
                PayOrderInfo orderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);
                orderInfoList.add(orderInfo);
            }
        }
        if(orderInfoList==null)
        {
            log.info("查询订单信息集合为空，本片任务终止处理");
            return;
        }
        payOrderInfoMap = orderInfoList.stream().filter(Objects::nonNull).collect(Collectors.toMap(PayOrderInfo::getOrderSsn, a -> a,(k1,k2)->k1));

        log.info("查询订单信息集合完成，查询数量【{}】，耗时【{}】",orderSsnList.size(),System.currentTimeMillis()-start);

        //从订单信息集合中提取原订单信息流水号，用来查询原订单集合，在后续退款逻辑中使用
        List<String> origOrderSsnList = orderInfoList.stream().filter(Objects::nonNull).map(PayOrderInfo::getOrigOrderSsn).filter(Objects::nonNull).collect(Collectors.toList());

        List<PayOrderInfo> origOrderInfoList=new ArrayList<>();
        Map<String, PayOrderInfo> origPayOrderInfoMap=new HashMap<String, PayOrderInfo>();
        if(origOrderSsnList!=null&&origOrderSsnList.size()>0)
        {
            parameter = new HashMap<String, Object>();
            parameter.put("orderSsnList", origOrderSsnList);
            origOrderInfoList=payOrderInfoDao.selectList("selectOrderInfoByReqOrderSsnList", parameter);
            if(origOrderInfoList==null)
            {
                origOrderInfoList=new ArrayList<>();    //避免null值报错
            }

            //Map<订单号,订单对象>结构
            origPayOrderInfoMap = origOrderInfoList.stream().collect(Collectors.toMap(PayOrderInfo::getOrderSsn, a -> a,(k1,k2)->k1));
            log.info("查询原订单信息集合完成，查询数量【{}】，耗时【{}】",origOrderInfoList.size(),System.currentTimeMillis()-start);

        }

        List<String> mchtIdList = orderInfoList.stream().filter(Objects::nonNull).map(PayOrderInfo::getMchtId).collect(Collectors.toList());   //生成MchtId为泛型的List
        //批量初始化商户基本信息缓存（如果缓存中已存在orderSsnList中的ssn，不会再次查询数据库）
        this.initMerInfo(mchtIdList);
        //批量初始化商户结算信息到缓存（如果缓存中已存在orderSsnList中的ssn，不会再次查询数据库）
        this.initMerStlInfo(mchtIdList);
        log.info("批量初始化商户基本信息、商户结算信息完成，耗时【{}】",System.currentTimeMillis()-start);

//        this.initMerStlRateInfo(mchtIdList);

        //批量初始化当前线程分片范围内所有子订单，并形成Map<订单号,List<子订单对象>>结构
        parameter = new HashMap<String, Object>();
        parameter.put("orderSsnList", orderSsnList);
        List<PaySubOrderInfo> subOrderAllList = this.paySubOrderInfoDao.selectList("selectSubOrderByOrderIdList", parameter);
        Map<String, List<PaySubOrderInfo>> subOrderMap = subOrderAllList.stream().collect(Collectors.groupingBy(PaySubOrderInfo::getOrderSsn));
        log.info("批量查询子订单信息完成，本片订单下子订单数量【{}】，耗时【{}】",subOrderAllList.size(),System.currentTimeMillis()-start);

        //抽取subOrderAllList集合中子订单编号形成List<原子订单编号>
        Map<String, PaySubOrderInfo> origSubOrderMap=new HashMap<String, PaySubOrderInfo>();
        List<String> origSubOrderSsn = subOrderAllList.stream().map(PaySubOrderInfo::getOriDetailsId).filter(Objects::nonNull).collect(Collectors.toList());
        if(origSubOrderSsn!=null&&origSubOrderSsn.size()>0)
        {
            //批量初始化当前线程分片范围内所有子订单所对应的原子订单集合
            parameter = new HashMap<String, Object>();
            parameter.put("subOrderSsnList", origSubOrderSsn);
            List<PaySubOrderInfo> origSubOrderList=this.paySubOrderInfoDao.selectList("selectSubOrderInfoBySubOrderNoList",parameter);
            //形成Map<订单号,子订单对象>结构
            origSubOrderMap = origSubOrderList.stream().collect(Collectors.toMap(PaySubOrderInfo::getSubOrderSsn, a -> a,(k1,k2)->k1));
            log.info("批量查询原子订单信息完成，数量【{}】，耗时【{}】",origSubOrderList.size(),System.currentTimeMillis()-start);
        }

        //批量初始化当前线程分片范围内所有记账信息集合，并形成Map<订单号,记账信息对象>结构
        parameter = new HashMap<String, Object>();
        parameter.put("orderSsnList", orderSsnList);
        List<KeepAccInfo> keepAccInfoList = keepAccInfoDao.selectList("selectByOrderSsnT0List",parameter);
        Map<String, KeepAccInfo> keepAccInfoMap = keepAccInfoList.stream().collect(Collectors.toMap(KeepAccInfo::getOrderSsn, a -> a,(k1,k2)->k1));
        log.info("批量查询记账信息集合完成，数量【{}】,耗时【{}】",keepAccInfoList.size(),System.currentTimeMillis()-start);

        List<BthMerInAccDtl> notClearingOrderList=new ArrayList<>();//准备收集线上订单😈
        MchtBaseInfo merInfo=null;
        MchtContInfo merStlInfo=null;
        //😎😎😎😎😎
        for(BthChkRsltInfo chkSuccOrd:chkSuccOrdList)
        {
            PayOrderInfo orderInfo=payOrderInfoMap.get(chkSuccOrd.getOrderSsn());
            if(orderInfo==null)
            {
                log.error("pay_order_info中查不到该订单，订单号【{}】",chkSuccOrd.getOrderSsn());
                continue;
            }
            PayOrderInfo origOrderInfo=origPayOrderInfoMap.get(chkSuccOrd.getOrderSsn());

            if (Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(
                    orderInfo.getTxnTypeNo()))
            {
                //线上交易
                List<PaySubOrderInfo> subOrderList = subOrderMap.get(orderInfo.getOrderSsn());
                if (subOrderList == null || subOrderList.size() == 0)
                {
                    log.error(">>>>>>>>>>>>>>>线上支付找不到子订单！orderSsn = " + orderInfo.getOrderSsn());
                }
                else
                {
                    //计算渠道手续费
                    this.calcChnlFee4SubOrder(subOrderList, chkSuccOrd);
                    //计算银联品牌服务费
                    if (!IfspDataVerifyUtil.isBlank(chkSuccOrd.getUnionBrandFee()))
                    {
                        calcChnlFee4SubOrderUnion(subOrderList, chkSuccOrd);
                    }
                    //计算行内手续费
                    for (PaySubOrderInfo subOrder : subOrderList)
                    {
                        this.calMerFee4SubOrder(orderInfo, subOrder, chkSuccOrd,origOrderInfo);
                    }

                    //生成子订单入账明细信息
                    for (PaySubOrderInfo subOrder : subOrderList)
                    {
                        //查询子订单商户信息
                        merInfo = this.getMerInfo(subOrder.getSubMchtId());
                        merStlInfo = this.getMerStlInfo(subOrder.getSubMchtId());

                        PaySubOrderInfo origSubOrderInfo=origSubOrderMap.get(subOrder.getOriDetailsId());

                        //2)根据子订单信息生成
                        BthMerInAccDtl orderStlInfo = this.buildOnlineOrderStlInfo(subOrder, orderInfo, chkSuccOrd,merInfo, merStlInfo,origSubOrderInfo);
                        bthMerInAccDtlList.add(orderStlInfo);
                    }
                }
                subOrderMap.remove(orderInfo.getOrderSsn());
                origPayOrderInfoMap.remove(orderInfo.getOrderSsn());
            }
            else
            {
                //线下订单
                merInfo = this.getMerInfo(orderInfo.getMchtId());
                if(merInfo==null)
                {
                    log.error("当前订单商户信息不存在，放弃处理，订单号【{}】，商户号【{}】",orderInfo.getOrderSsn(),orderInfo.getMchtId());
                    continue;
                }
                merStlInfo = this.getMerStlInfo(orderInfo.getMchtId());
                //根据订单信息生成清算信息
                BthMerInAccDtl orderStlInfo = this.buildSMOrderStlInfo(orderInfo, chkSuccOrd, merInfo, merStlInfo,origOrderInfo);
                //20190920
//                KeepAccInfo keepAccInfo = keepAccInfoDao.selectByOrderSsnT0(orderStlInfo.getTxnSeqId());
                KeepAccInfo keepAccInfo=keepAccInfoMap.get(orderStlInfo.getTxnSeqId());
                if (IfspDataVerifyUtil.isEmpty(keepAccInfo))
                {
                    bthMerInAccDtlList.add(orderStlInfo);
                }
                else
                {
                    log.info("更新订单号清分_[{}]", orderStlInfo.getTxnSeqId());
                    orderStlInfo.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                    //已实时结算,手续费未清分
                    orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
                    //20190929 T+0时间取和T+1一样
                    orderStlInfo.setStlmDate(DateUtil.format(new Date(), "yyyyMMdd"));
//                    bthMerInAccDtlDao.batchUpdateForT0(orderStlInfo);

                    notClearingOrderList.add(orderStlInfo);
                }
                keepAccInfoMap.remove(orderStlInfo.getTxnSeqId());
            }


            payOrderInfoMap.remove(chkSuccOrd.getOrderSsn());

            orderInfo=null;
        }
        log.info("订单抽取逻辑计算完成，耗时【{}】", System.currentTimeMillis()-start);
        int updateCount=this.updateBatchOrderStlStatus(orderSsnList);
        log.info("批量更新订单号清分完成，更新数量【{}】", updateCount);

//        if(notClearingOrderSsnList!=null&&notClearingOrderSsnList.size()>0)
//        {
//            parameter = new HashMap<String, Object>();
//            parameter.put("notClearingOrderSsnList", notClearingOrderSsnList);
//            parameter.put("updateDate",IfspDateTime.getYYYYMMDDHHMMSS());
//            bthMerInAccDtlDao.update("updateNotClearingOrderSsnList",parameter);
//        }

        if(notClearingOrderList!=null&&notClearingOrderList.size()>0)
        {
            updateCount=bthMerInAccDtlDao.batchUpdateForT0(notClearingOrderList);
            log.info("批量更新线上订单清分完成，更新数量【{}】", updateCount);
        }

        if (bthMerInAccDtlList.size() > 0)
        {
            int count = bthMerInAccDtlDao.insertBatch(bthMerInAccDtlList);
            log.info("批量插入入账明细表[{}]条数据，耗时【{}】", count,System.currentTimeMillis()-start);
        }

        orderSsnList.clear();
        payOrderInfoMap.clear();
        subOrderAllList.clear();
        subOrderMap.clear();
        origOrderInfoList.clear();
        origPayOrderInfoMap.clear();
        origSubOrderMap.clear();
        keepAccInfoList.clear();
        notClearingOrderList.clear();
        //😘
    }

    /**
     * 根据对账成功订单信息查询订单信息及商户信息
     */

    @Override
    @Deprecated
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void dataGathering(BthChkRsltInfo chkSuccOrd, String batchDate) throws Exception
    {
        try
        {
            List<BthMerInAccDtl> bthMerInAccDtlList = new ArrayList<BthMerInAccDtl>();
            //1)查询订单信息
            //查询订单信息 加上日期利用分区
            Map<String, Object> parameter = new HashMap<String, Object>();
            Date clDate = null;
            try
            {
                clDate = IfspDateTime.getYYYYMMDD(batchDate);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                throw new IfspSystemException(e);
            }
            parameter.put("startDate", new DateTime(clDate).minusDays(1).toDate());
            parameter.put("endDate", clDate);
            parameter.put("orderSsn", chkSuccOrd.getOrderSsn());
            PayOrderInfo orderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsnAndDate", parameter);
            if (IfspDataVerifyUtil.isEmpty(orderInfo))
            {
                orderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);
            }

            //2)查询商户结算信息
            MchtBaseInfo merInfo = this.getMerInfo(orderInfo.getMchtId());
            MchtContInfo merStlInfo = this.getMerStlInfo(orderInfo.getMchtId());

            //logic:
            //根据对账成功表的ORDER_SSN查询订单信息，根据交易码判断是线上交易还是线下交易
            //如果是线上交易，根据订单号查询所有的子订单信息（主订单成功，子订单默认支付成功，如失败通过异步补机制处理）
            if (Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(
                    orderInfo.getTxnTypeNo()))
            {
                //线上交易处理
                //1)查询子订单信息
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("orderSsn", orderInfo.getOrderSsn());
                List<PaySubOrderInfo> subOrderList = this.paySubOrderInfoDao.selectList("selectSubOrderByOrderId", m);
                if (subOrderList == null || subOrderList.size() == 0)
                {
                    log.error(">>>>>>>>>>>>>>>线上支付找不到子订单！orderSsn = " + orderInfo.getOrderSsn());
                }
                else
                {
                    //计算渠道手续费
                    this.calcChnlFee4SubOrder(subOrderList, chkSuccOrd);
                    //计算银联品牌服务费
                    if (!IfspDataVerifyUtil.isBlank(chkSuccOrd.getUnionBrandFee()))
                    {
                        calcChnlFee4SubOrderUnion(subOrderList, chkSuccOrd);
                    }
                    //计算行内手续费
                    for (PaySubOrderInfo subOrder : subOrderList)
                    {
                        this.calMerFee4SubOrder(orderInfo, subOrder, chkSuccOrd,null);
                    }

                    //生成子订单入账明细信息
                    for (PaySubOrderInfo subOrder : subOrderList)
                    {
                        //查询子订单商户信息
                        merInfo = this.getMerInfo(subOrder.getSubMchtId());
                        merStlInfo = this.getMerStlInfo(subOrder.getSubMchtId());
                        //2)根据子订单信息生成
                        BthMerInAccDtl orderStlInfo = this.buildOnlineOrderStlInfo(subOrder, orderInfo, chkSuccOrd,
                                                                                   merInfo, merStlInfo,null);
                        bthMerInAccDtlList.add(orderStlInfo);
                    }
                }
            }
            else
            {
                //线下订单
                //根据订单信息生成清算信息
                BthMerInAccDtl orderStlInfo = this.buildSMOrderStlInfo(orderInfo, chkSuccOrd, merInfo, merStlInfo,null);
                //20190920
                KeepAccInfo keepAccInfo = keepAccInfoDao.selectByOrderSsnT0(orderStlInfo.getTxnSeqId());
                if (IfspDataVerifyUtil.isEmpty(keepAccInfo))
                {
                    bthMerInAccDtlList.add(orderStlInfo);
                }
                else
                {
//                    log.info("更新订单号清分_[{}]", orderStlInfo.getTxnSeqId());
                    //已实时结算,手续费未清分
                    orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
                    //20190929 T+0时间取和T+1一样
                    orderStlInfo.setStlmDate(DateUtil.format(new Date(), "yyyyMMdd"));
                    bthMerInAccDtlDao.batchUpdateForT0Bak(orderStlInfo);
                }
            }
            this.updateOrderStlStatus(chkSuccOrd);
            if (bthMerInAccDtlList.size() > 0)
            {
                int count = bthMerInAccDtlDao.insertBatch(bthMerInAccDtlList);
                log.info("批量插入入账明细表[{}]条数据", count);
            }
        }
        catch (Exception e)
        {
            log.info("该订单清分抽取出现异常_[{}]", e.getMessage());
            throw new Exception(e);
        }

    }


    /**
     * 根据商户查询商户基本信息(加缓存)
     *
     * @param mchtId
     * @return
     */
    private MchtBaseInfo getMerInfo(String mchtId)
    {
        MchtBaseInfo merInfo;
        if (IfspDataVerifyUtil.isNotBlank(CacheMchtBaseInfo.getCache(mchtId)))
        {
            merInfo = CacheMchtBaseInfo.getCache(mchtId);
        }
        else
        {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("mchtId", mchtId);
            merInfo = mchtBaseInfoDao.selectOne("selectMerInfoByMchtId", m);
            if (IfspDataVerifyUtil.isNotBlank(merInfo))
            {
                // 加入缓存
                CacheMchtBaseInfo.addCache(mchtId, merInfo);
            }
        }

        return merInfo;
    }

    /**
     * 批量初始化商户基本信息数据到缓存
     *
     * @param mchtIdList
     * @return
     */
    private void initMerInfo(List<String> mchtIdList)
    {
        for(Iterator<String> it=mchtIdList.iterator();it.hasNext();)
        {
            //将缓存中已存在的商户id从mchtIdList中移出
            if (IfspDataVerifyUtil.isNotBlank(CacheMchtBaseInfo.getCache(it.next())))
            {
                it.remove();
            }
        }
        if(mchtIdList!=null&&mchtIdList.size()>0)
        {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("mchtIdList", mchtIdList);
            List<MchtBaseInfo> mchtBaseInfoList= mchtBaseInfoDao.selectList("selectMerInfoByMchtIdList", m);
            for(Iterator<MchtBaseInfo> it=mchtBaseInfoList.iterator();it.hasNext();)
            {
                MchtBaseInfo mchtBaseInfo=it.next();
                if (mchtBaseInfo!=null)
                {
                    CacheMchtBaseInfo.addCache(mchtBaseInfo.getMchtId(), mchtBaseInfo);
                }
                it.remove();
            }
        }
    }

    /**
     * 根据商户号查询商户结算信息(加缓存)
     *
     * @param mchtId
     * @return
     */
    private MchtContInfo getMerStlInfo(String mchtId)
    {
        MchtContInfo merStlInfo;
        if (IfspDataVerifyUtil.isNotBlank(CacheMchtContInfo.getCache(mchtId)))
        {
            merStlInfo = CacheMchtContInfo.getCache(mchtId);
        }
        else
        {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("mchtId", mchtId);
            merStlInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);
            if (IfspDataVerifyUtil.isNotBlank(merStlInfo))
            {
                // 加入缓存
                CacheMchtContInfo.addCache(mchtId, merStlInfo);
            }
        }
        return merStlInfo;
    }

    /**
     * 批量初始化商户号查询商户结算信息到缓存
     *
     * @param mchtIdList
     * @return
     */
    private void initMerStlInfo(List<String> mchtIdList)
    {
        for(Iterator<String> it=mchtIdList.iterator();it.hasNext();)
        {
            //将缓存中已存在的商户id从mchtIdList中移出
            if (IfspDataVerifyUtil.isNotBlank(CacheMchtContInfo.getCache(it.next())))
            {
                it.remove();
            }
        }
        if(mchtIdList!=null&&mchtIdList.size()>0)
        {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("mchtIdList", mchtIdList);
            List<MchtContInfo> mchtContInfoList= mchtContInfoDao.selectList("selectMerStlInfoByMchtIdList", m);
            for(Iterator<MchtContInfo> it=mchtContInfoList.iterator();it.hasNext();)
            {
                MchtContInfo mchtContInfo=it.next();
                if (mchtContInfo!=null)
                {
                    CacheMchtContInfo.addCache(mchtContInfo.getMchtId(), mchtContInfo);
                }
                it.remove();
            }
        }
    }

    /**
     * 批量初始化商户号查询商户结算信息到缓存
     *
     * @param mchtIdList
     * @return
     */
    private void initMerStlRateInfo(List<String> mchtIdList)
    {
        if (mchtIdList != null && mchtIdList.size() > 0)
        {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("mchtIdList", mchtIdList);
            List<MchtSettlRateCfg> merRateInfoList = mchtSettlRateCfgDao.selectList(
                    "selectMerStlRateInfoByMchtIdList", m);
            if (merRateInfoList == null)
            {
                return;
            }
            for (Iterator<MchtSettlRateCfg> it = merRateInfoList.iterator(); it.hasNext(); )
            {
                MchtSettlRateCfg mchtSettlRateCfg = it.next();
                if (mchtSettlRateCfg != null)
                {
                    String key=mchtSettlRateCfg.getMchtId() + "-" + mchtSettlRateCfg.getAccChnlNo() + "-" + mchtSettlRateCfg.getAcctType() + "-" + mchtSettlRateCfg.getProdId();
                    CacheMerStlRateInfo.addCache(key,mchtSettlRateCfg);
                }
                it.remove();
            }
        }
    }

    /**
     * 根据商户号、渠道号、交易账户类型查询手续费费率信息
     *
     * @param chnlNo
     * @param merId
     * @param accType
     * @param prdId
     * @return
     */
    private MchtSettlRateCfg getMerStlRateInfo(String chnlNo, String merId, String accType, String prdId)
    {
        String key = merId + "-" + chnlNo + "-" + accType + "-" + prdId;
        MchtSettlRateCfg merRateInfo = CacheMerStlRateInfo.getCache(key);

        if (merRateInfo == null)
        {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("mchtId", merId);
            m.put("accChnlNo", chnlNo);
            m.put("acctType", accType);
            m.put("prodId", prdId);

            List<MchtSettlRateCfg> merRateInfoList = mchtSettlRateCfgDao.selectList(
                    "selectMerStlRateInfoByChnlAndAccType", m);

            if (merRateInfoList.size() > 0)
            {
                merRateInfo = merRateInfoList.get(0);
            }
        }
        return merRateInfo;
    }


    /**
     * @param checkedOrder
     * @param merInfo
     * @return
     */
    private BthMerInAccDtl buildSMOrderStlInfo(PayOrderInfo orderInfo, BthChkRsltInfo checkedOrder,
                                               MchtBaseInfo merInfo, MchtContInfo merStlInfo,PayOrderInfo origOrderInfo)
    {
        //根据商户结算（合同）信息计算订单结算日期
        String stlDate = this.calcStlDate(merStlInfo,orderInfo.getOrderTm());
        //init
        BthMerInAccDtl orderStlInfo = new BthMerInAccDtl();

        //交易类型
        if (Constans.TXN_TYPE_CANCEL.equals(orderInfo.getTxnTypeNo()) ||
                Constans.TXN_TYPE_REFUND.equals(orderInfo.getTxnTypeNo()))
        {
            orderStlInfo.setOrderType(Constans.ORDER_TYPE_RETURN);
            orderStlInfo.setRefundAccType(orderInfo.getRefundAcctType());
        }
        else
        {
            orderStlInfo.setOrderType(Constans.ORDER_TYPE_CONSUME);
        }

        orderStlInfo.setStlmDate(stlDate);            //清算日期
        orderStlInfo.setInAcctDate(stlDate);          //清算日期
        orderStlInfo.setChlMerId(merInfo.getMchtId());    //商户号
        orderStlInfo.setChlMerName(merInfo.getMchtName());   //商户名称
        orderStlInfo.setTxnTm(orderInfo.getOrderTm());

        //商户为二级商户
        if (merInfo.getMchtId().length() > 15 && !IfspDataVerifyUtil.isBlank(merInfo.getParMchId()))
        {
            orderStlInfo.setChlSubMerId(merInfo.getMchtId());
            orderStlInfo.setChlSubMerName(merInfo.getMchtName());
        }

        orderStlInfo.setPagyNo(checkedOrder.getPagySysNo());   //通道编号
        orderStlInfo.setOpenBrc(orderInfo.getOpenBrc());    //本行支付开卡机构

        //金额初始化
        orderStlInfo.setBankHbAmt("0");
        orderStlInfo.setLogisFee("0");
        orderStlInfo.setBrandFee("0");
        orderStlInfo.setPointDedcutAmt("0");
        if (Constans.MCHT_SETTLE_TYPE_0.equals(orderInfo.getMchtSettleType()))
        {
            //已实时结算,手续费未清分
            orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
            //20190929 T+0时间取和T+1一样
            Date clDate = null;
            try
            {
                clDate = IfspDateTime.getYYYYMMDD(stlDate);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                throw new IfspSystemException(e);
            }
            new DateTime(stlDate);
            orderStlInfo.setStlmDate(DateUtil.format(DateUtil.add(clDate, Calendar.DAY_OF_MONTH, 1), "yyyyMMdd"));
        }
        else
        {
            //初始化结算状态
            orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_NOT_CLEARING);
        }

        orderStlInfo.setInAcctStat(Constans.IN_ACC_STAT_PRE);


        //设置交易类型与接入渠道
        orderStlInfo.setTxnType(orderInfo.getTxnTypeNo());
        if (Constans.TXN_TYPE_O1000002.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_O1000003.equals(
                orderInfo.getTxnTypeNo()))
        {
            //主扫交易/微信公众号() 取CHL_NO
            orderStlInfo.setFundChannel(orderInfo.getChlNo());
        }
        else
        {
            //被扫支付
            orderStlInfo.setFundChannel(orderInfo.getAcptChlNo());   //如果是退款交易，渠道也取acptChlNo
        }

        //清算账户信息
        orderStlInfo.setSetlAcctNo(merStlInfo.getSettlAcctNo());
        orderStlInfo.setSetlAcctName(merStlInfo.getSettlAcctName());
        orderStlInfo.setSetlAcctType(merStlInfo.getSettlAcctType());         //0-本行,1-他行
        orderStlInfo.setSetlAcctInstitute2(merStlInfo.getSettlAcctOrgId());       //机构
        orderStlInfo.setOutAcctNo(merStlInfo.getLiqAcctNo());
        if (IfspDataVerifyUtil.isBlank(orderInfo.getPayAmt()))
        {
            orderInfo.setPayAmt("0");
        }
        BigDecimal txnAmt = new BigDecimal(orderInfo.getPayAmt());    //交易金额
        BigDecimal merFee = new BigDecimal(0);    //行内手续费
        BigDecimal stlAmt = new BigDecimal(0);    //结算金额
        BigDecimal bankCouponAmt = new BigDecimal(0);    //营销总金额


        if (orderInfo.getBankCouponAmt() != null)
        {
            bankCouponAmt = new BigDecimal(orderInfo.getBankCouponAmt());
            txnAmt = txnAmt.add(bankCouponAmt);
        }

        //商户奖励金
        if (IfspDataVerifyUtil.isNotBlank(orderInfo.getMchtIncentiveAmt()))
        {
            BigDecimal mchtIncentiveAmt = new BigDecimal(orderInfo.getMchtIncentiveAmt());
            txnAmt = txnAmt.add(mchtIncentiveAmt);
        }

        //计算佣金
        BigDecimal commissionAmt;
        if (IfspDataVerifyUtil.isBlank(merInfo.getParMchId()))
        {
            commissionAmt = new BigDecimal(0);
        }
        else
        {
            if (Constans.ORDER_TYPE_CONSUME.equals(orderStlInfo.getOrderType()))
            {
                commissionAmt = this.calcCommissionAmt(txnAmt, merInfo.getMchtId());      //修改佣金费率取值，取当前商户信息数据，而非父商户数据
            }
            else
            {
                //处理部分退款情况佣金返回
                commissionAmt = this.calcCommissionAmt4Return(orderInfo,origOrderInfo);
            }
        }

        if (Constans.ORDER_TYPE_CONSUME.equals(orderStlInfo.getOrderType()))
        {
            //计算手续费
            String proId;
            if (Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
            {
                proId = Constans.PRO_ID_ONLINE;
            }
            else
            {
                proId = Constans.PRO_ID_SM;
            }
            merFee = this.calcMerFee4Order(txnAmt, orderInfo.getMchtId(), orderInfo, orderStlInfo.getFundChannel(),
                                           proId);
        }
        else
        {
            //计算退款手续费
            merFee = this.calcMerFee4Order4Return(orderInfo);
        }

        //task 15047 佣金金额 + 手续费金额不能大于订单金额
        if (txnAmt.compareTo(merFee.add(commissionAmt)) == -1)
        {
            commissionAmt = txnAmt.subtract(merFee);
        }


        //交易金额=支付金额+红包抵扣金额？
        orderStlInfo.setTxnAmt(txnAmt.intValue() + "");
        merFee = merFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        stlAmt = txnAmt.subtract(merFee).subtract(commissionAmt);
        orderStlInfo.setSetlAmt(stlAmt.intValue() + "");
        orderStlInfo.setSetlFeeAmt(merFee.intValue() + "");       //行内手续费
        orderStlInfo.setCommissionAmt(commissionAmt.intValue() + "");
        orderStlInfo.setBankCouponAmt(bankCouponAmt.intValue() + "");

        //第三方手续费
        if (checkedOrder.getTpamTxnFeeAmt() == null)
        {
            orderStlInfo.setTramFeeAmt("0");
        }
        else
        {
            orderStlInfo.setTramFeeAmt(checkedOrder.getTpamTxnFeeAmt() + "");
        }

        orderStlInfo.setTxnSeqId(orderInfo.getOrderSsn());           //设置订单号
        orderStlInfo.setAgentName(orderInfo.getOrigOrderSsn());      //原交易订单号，针对退款交易有效
        orderStlInfo.setCreateDate(DateUtil.format(new Date(), "yyyyMMdd"));

        //设置品牌服务费
        if (!IfspDataVerifyUtil.isBlank(checkedOrder.getUnionBrandFee()))
        {
            orderStlInfo.setBrandFee(checkedOrder.getUnionBrandFee().intValue() + "");
        }


        //设置隔日间接结算信息
        //TODO:20181128版本-业务上暂不支持

        return orderStlInfo;
    }

    private BthMerInAccDtl buildOnlineOrderStlInfo(PaySubOrderInfo subOrderInfo, PayOrderInfo orderInfo,
                                                   BthChkRsltInfo checkedOrder,
                                                   MchtBaseInfo merInfo, MchtContInfo merStlInfo,PaySubOrderInfo origSubOrderInfo)
    {
        //根据商户结算（合同）信息计算订单结算日期
        //String stlDate = this.calcStlDate(merStlInfo);
        String stlDate = "99991230";
        //init
        BthMerInAccDtl orderStlInfo = new BthMerInAccDtl();
        orderStlInfo.setStlmDate(stlDate);            //清算日期
        orderStlInfo.setInAcctDate(stlDate);          //清算日期
        orderStlInfo.setTxnTm(orderInfo.getOrderTm());
        orderStlInfo.setChlMerId(merInfo.getMchtId());    //商户号
        orderStlInfo.setChlMerName(merInfo.getMchtName());   //商户名称

        //金额初始化
        orderStlInfo.setBankHbAmt("0");
        orderStlInfo.setLogisFee("0");
        orderStlInfo.setBrandFee("0");
        orderStlInfo.setPointDedcutAmt("0");


        //清算账户信息
        orderStlInfo.setSetlAcctNo(merStlInfo.getSettlAcctNo());
        orderStlInfo.setSetlAcctName(merStlInfo.getSettlAcctName());
        orderStlInfo.setSetlAcctType(merStlInfo.getSettlAcctType());         //0-本行,1-他行
        orderStlInfo.setSetlAcctInstitute2(merStlInfo.getSettlAcctOrgId());       //机构
        orderStlInfo.setOutAcctNo(merStlInfo.getLiqAcctNo());

        BigDecimal payAmt = new BigDecimal(subOrderInfo.getPayAmount());
        BigDecimal hbAmt = new BigDecimal(0);
        BigDecimal stlAmt = new BigDecimal(0);
        BigDecimal commissionAmt = new BigDecimal(0);
        //BigDecimal logisAmt = new BigDecimal(subOrderInfo.getLogisFee()) ;   //this.getLogisAmt(subOrderInfo);
        BigDecimal pointAmt = this.getCouponAmt(subOrderInfo, Constans.CONSUME_TYPE_POINT);
        BigDecimal bankHbAmt = this.getCouponAmt(subOrderInfo, Constans.CONSUME_TYPE_ORG);
        BigDecimal platHbAmt = this.getCouponAmt(subOrderInfo, Constans.CONSUME_TYPE_PLAT);

        BigDecimal mchtMarketingAmt = new BigDecimal(0);     //商户营销金额
        BigDecimal bankMarketingAmt = new BigDecimal(0);     //银行营销金额
        BigDecimal orgMarketingAmt = new BigDecimal(0);      //用户机构营销金额
        BigDecimal marketingAmt = this.getMarketingAmt(subOrderInfo);   //营销总金额

        //bug 9632


        if (StringUtils.hasText(subOrderInfo.getMchtPayAmt()))
        {
            mchtMarketingAmt = new BigDecimal(subOrderInfo.getMchtPayAmt());
        }

        if (StringUtils.hasText(subOrderInfo.getBankPayAmt()))
        {
            bankMarketingAmt = new BigDecimal(subOrderInfo.getBankPayAmt());
        }

        if (StringUtils.hasText(subOrderInfo.getUserorgPayAmt()))
        {
            orgMarketingAmt = new BigDecimal(subOrderInfo.getUserorgPayAmt());
        }


        if (!IfspDataVerifyUtil.isBlank(subOrderInfo.getBankCouponAmt()))
        {
            hbAmt = new BigDecimal(subOrderInfo.getBankCouponAmt());
        }

        BigDecimal txnAmt = payAmt.add(hbAmt);   //BankCouponAmt(hbAmt)已经包括了红包+积分金额
        txnAmt = txnAmt.add(marketingAmt);       //营销金额

        BigDecimal merFee = new BigDecimal(subOrderInfo.getMerFee());    //行内手续费


        //计算佣金，物流费不收佣金
        if (Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
        {
            commissionAmt = this.calcCommissionAmt(payAmt.add(hbAmt).add(marketingAmt), merInfo.getMchtId());
        }
        else
        {
            commissionAmt = this.calcCommissionAmt4SubOrder4Return(subOrderInfo,origSubOrderInfo);
        }

        //task 15047 佣金金额 + 手续费金额不能大于订单金额
        if (txnAmt.compareTo(merFee.add(commissionAmt)) == -1)
        {
            commissionAmt = txnAmt.subtract(merFee);
        }

        stlAmt = payAmt.add(hbAmt).add(marketingAmt).subtract(merFee).subtract(commissionAmt);

        orderStlInfo.setCommissionAmt(commissionAmt.intValue() + "");       //佣金
        orderStlInfo.setSetlAmt(stlAmt.intValue() + "");                    //清算金额
        orderStlInfo.setSetlFeeAmt(subOrderInfo.getMerFee());             //清算手续费
        orderStlInfo.setTxnAmt(txnAmt.intValue() + "");
        orderStlInfo.setBankHbAmt(bankHbAmt.intValue() + "");         //机构红包
        orderStlInfo.setPlatHbAmt(platHbAmt.intValue() + "");         //平台红包
        orderStlInfo.setPointDedcutAmt(pointAmt.intValue() + "");

        //设置营销金额
        orderStlInfo.setMchtPayAmt(mchtMarketingAmt.intValue() + "");
        orderStlInfo.setBankPayAmt(bankMarketingAmt.intValue() + "");
        orderStlInfo.setUserorgPayAmt(orgMarketingAmt.intValue() + "");


        //第三方手续费
        if (checkedOrder.getTpamTxnFeeAmt() == null)
        {
            orderStlInfo.setTramFeeAmt("0");
        }
        else
        {
            orderStlInfo.setTramFeeAmt(subOrderInfo.getBranchFee() + "");
        }

        orderStlInfo.setTxnSeqId(subOrderInfo.getSubOrderSsn());           //子订单号
        orderStlInfo.setAgentName(subOrderInfo.getOriDetailsId());            //原交易订单号，针对退款交易有效
        orderStlInfo.setOrderType(subOrderInfo.getOrderType());           //设置订单类型
        orderStlInfo.setRefundAccType(orderInfo.getRefundAcctType());     //退款账户类型
        orderStlInfo.setFundChannel(orderInfo.getFundChannel());          //子订单的支付渠道取主订单中的FUND_CHANNEL值
        orderStlInfo.setTxnType(orderInfo.getTxnTypeNo());                //交易码
        if (Constans.LOAN_SYS_NO.equals(checkedOrder.getPagySysNo()) || Constans.LOAN_SYS_POINT.equals(
                checkedOrder.getPagySysNo()))
        {
            orderStlInfo.setPagyNo(Constans.IBANK_SYS_NO);      //授信支付通道设置为本行
        }
        else
        {
            orderStlInfo.setPagyNo(checkedOrder.getPagySysNo());   //通道编号
        }


        //设置物流费信息
        orderStlInfo.setLogisType(subOrderInfo.getLogisType());
        orderStlInfo.setLogisFee(subOrderInfo.getLogisFee());
        orderStlInfo.setLogisPartnerCode(subOrderInfo.getLogisPartnerCode());
        orderStlInfo.setLogisFeeAmt(subOrderInfo.getLogisFeeAmt());    //物流手续费

        orderStlInfo.setOpenBrc(orderInfo.getOpenBrc());    //本行支付开卡机构
        orderStlInfo.setCreateDate(DateUtil.format(new Date(), "yyyyMMdd"));

        if (Constans.MCHT_SETTLE_TYPE_0.equals(orderInfo.getMchtSettleType()))
        {
            //已实时结算,手续费未清分
            orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
        }
        else
        {
            //初始化结算状态
            orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_NOT_CLEARING);
        }

        if (Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
        {
            orderStlInfo.setOrderType(Constans.ORDER_TYPE_CONSUME);
        }
        else
        {
            orderStlInfo.setOrderType(Constans.ORDER_TYPE_RETURN);
            orderStlInfo.setRefundAccType(orderInfo.getRefundAcctType());
        }

        //设置品牌服务费
        if (!IfspDataVerifyUtil.isBlank(checkedOrder.getUnionBrandFee()))
        {
            orderStlInfo.setBrandFee(checkedOrder.getUnionBrandFee().intValue() + "");
        }

        //bug : 二级商户返佣给了平台商户 （商户为二级商户）
        if (merInfo.getMchtId().length() > 15 && !IfspDataVerifyUtil.isBlank(merInfo.getParMchId()))
        {
            orderStlInfo.setChlSubMerId(merInfo.getMchtId());
            orderStlInfo.setChlSubMerName(merInfo.getMchtName());
        }

        return orderStlInfo;
    }

    //private void

    /**
     * 计算订单结算日期
     * 因为对账相对交易日期已经晚了一天，所以在计算时根据当前日期-1，再加上N
     *
     * @param merStlInfo
     * @return
     */
    private String calcStlDate(MchtContInfo merStlInfo,Date orderTime)
    {
        Date preDate = DateUtil.add(new Date(), Calendar.DAY_OF_MONTH, -1);
        Date stlDate = null;
        if (Constans.STL_TYPE_DN.equals(merStlInfo.getSettlCycleType()))
        {
            //TODO:当前都是根据T+N进行结算，后续如果考虑（D+N）节假日不进行结算，需维护节日表
            //但由于收单现有模式是结算失败的会在第二天继续结算，所以非节假日计算很难保证
            //20191009
            short s = 1;
            if (merStlInfo.getSettlCycleParam() == 0)
            {
                merStlInfo.setSettlCycleParam(s);
            }
            stlDate = DateUtil.add(preDate, Calendar.DAY_OF_MONTH, merStlInfo.getSettlCycleParam());
        }
        else if (Constans.STL_TYPE_TN.equals(merStlInfo.getSettlCycleType()))
        {
            stlDate = DateUtil.add(preDate, Calendar.DAY_OF_MONTH, merStlInfo.getSettlCycleParam());
        }
        else if (Constans.STL_TYPE_BY_MONTH.equals(merStlInfo.getSettlCycleType()))
        {
            int day = merStlInfo.getSettlCycleParam();
            if (day > 30)
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
     * 计算子订单的行内手续费(支付金额+红包金额+物流金额)*行内扣率
     *
     * @param chkSuccOrd
     */
    private void calMerFee4SubOrder(PayOrderInfo orderInfo, PaySubOrderInfo subOrder, BthChkRsltInfo chkSuccOrd,PayOrderInfo origOrderInfo)
    {
        BigDecimal merFee = new BigDecimal(0);
        //支付金额
        BigDecimal payAmt = new BigDecimal(subOrder.getPayAmount());
        //红包金额
        BigDecimal bhAmt = this.getHbAmt(subOrder);

        //营销金额
        BigDecimal marketingAmt = this.getMarketingAmt(subOrder);

        //积分金额
        //BigDecimal pointAmt = this.getPointAmt(subOrder);   //BankCouponAmt包括了积分金额

        //总金额
        BigDecimal txnAmt = payAmt.add(bhAmt).add(marketingAmt);           // payAmt.add(bhAmt).add(logisFee);

        //物流金额
        BigDecimal logisFee = this.getLogisAmt(subOrder);
        subOrder.setLogisFee(logisFee.intValue() + "");
        subOrder.setLogisFeeAmt("0");
        //本行支付物流费不收手续费
        if (!(Constans.GAINS_CHANNEL_SXE.equals(subOrder.getFundChannel()) || Constans.GAINS_CHANNEL_SXK.equals(
                subOrder.getFundChannel()) ||
                Constans.GAINS_CHANNEL_LOAN.equals(subOrder.getFundChannel()) || Constans.GAINS_CHANNEL_POINT.equals(
                subOrder.getFundChannel())))
        {
            if (logisFee.compareTo(new BigDecimal(0)) == 1)
            {
                //txnAmt = txnAmt.add(logisFee);                      //物流费不是结算给商户，不能算作txnAmt

                //计算物流部分手续费,因为结算金额
                //计算手续费
                String proId;
                if (Constans.TXN_TYPE_ONLINE_PAY.equals(
                        orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(orderInfo.getTxnTypeNo()))
                {
                    proId = Constans.PRO_ID_ONLINE;
                }
                else
                {
                    proId = Constans.PRO_ID_SM;
                }
                BigDecimal logisFeeAmt = this.calcMerFee4Order(logisFee, subOrder.getSubMchtId(), orderInfo,
                                                               subOrder.getFundChannel(), proId);

                subOrder.setLogisFee(logisFee.intValue() + "");
                subOrder.setLogisFeeAmt(logisFeeAmt.intValue() + "");
            }
            else
            {
                subOrder.setLogisFee(logisFee.intValue() + "");
                subOrder.setLogisFeeAmt("0");
            }
        }


        String proId;
        if (Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(
                orderInfo.getTxnTypeNo()))
        {
            proId = Constans.PRO_ID_ONLINE;
        }
        else
        {
            proId = Constans.PRO_ID_SM;
        }

        if (Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
        {

            merFee = this.calcMerFee4Order(txnAmt, subOrder.getSubMchtId(), orderInfo, subOrder.getFundChannel(),
                                           proId);
        }
        else
        {
            merFee = this.calcMerFee4SubOrder4Return(subOrder, proId,origOrderInfo);
        }


        subOrder.setMerFee(merFee.intValue() + "");
    }

    /**
     * 计算子订单的渠道手续费
     *
     * @param subOrderList
     * @param chkSuccOrd
     */
    private void calcChnlFee4SubOrder(List<PaySubOrderInfo> subOrderList, BthChkRsltInfo chkSuccOrd)
    {
        BigDecimal branchFeeAmtRemain = null;
        long branchFeeAmt;
        if (chkSuccOrd.getTpamTxnFeeAmt() == null)
        {
            branchFeeAmtRemain = new BigDecimal(0);
            branchFeeAmt = 0L;
        }
        else
        {
            branchFeeAmtRemain = new BigDecimal(chkSuccOrd.getTpamTxnFeeAmt());
            branchFeeAmt = chkSuccOrd.getTpamTxnFeeAmt();
        }

        if (subOrderList.size() == 1)
        {
            subOrderList.get(0).setBranchFee(chkSuccOrd.getTpamTxnFeeAmt() + "");
        }
        else
        {
            //根据子订单金额占比分摊手续费
            int index = 0;
            for (PaySubOrderInfo subOrder : subOrderList)
            {

                //设置默认值
                subOrder.setBranchFee("0");

                index++;
                //保证手续费分完全分摊
                if (index == subOrderList.size())
                {
                    subOrder.setBranchFee(branchFeeAmtRemain.longValue() + "");
                    break;
                }

                //渠道手续费
                BigDecimal currBranchFee = this.feeCalc(subOrder.getPayAmount(), chkSuccOrd.getTxnAmt(), branchFeeAmt);
                if (currBranchFee.compareTo(branchFeeAmtRemain) == 1)
                {
                    subOrder.setBranchFee(branchFeeAmtRemain.longValue() + "");
                    branchFeeAmtRemain = new BigDecimal("0");   //行内手续费已分完
                }
                else
                {
                    subOrder.setBranchFee(currBranchFee.longValue() + "");
                    branchFeeAmtRemain = branchFeeAmtRemain.subtract(currBranchFee);
                }

            }
        }
    }

    /**
     * 计算子订单的银联品牌服务费
     *
     * @param subOrderList
     * @param chkSuccOrd
     */
    private void calcChnlFee4SubOrderUnion(List<PaySubOrderInfo> subOrderList, BthChkRsltInfo chkSuccOrd)
    {
        BigDecimal brandFeeAmtRemain = null;
        long brandFeeAmtUnion;

        if (chkSuccOrd.getUnionBrandFee() == null)
        {
            brandFeeAmtRemain = new BigDecimal(0);
            brandFeeAmtUnion = 0L;
        }
        else
        {
            brandFeeAmtRemain = new BigDecimal(chkSuccOrd.getUnionBrandFee().intValue());
            brandFeeAmtUnion = chkSuccOrd.getUnionBrandFee().intValue();
        }

        if (subOrderList.size() == 1)
        {
            subOrderList.get(0).setBrandFeeUnion(chkSuccOrd.getUnionBrandFee().intValue() + "");
        }
        else
        {
            //根据子订单金额占比分摊品牌服务费
            int index = 0;
            for (PaySubOrderInfo subOrder : subOrderList)
            {

                //设置默认值
                subOrder.setBrandFeeUnion("0");

                index++;
                //保证品牌服务费完全分摊
                if (index == subOrderList.size())
                {
                    subOrder.setBrandFeeUnion(brandFeeAmtRemain.longValue() + "");
                    break;
                }

                //品牌服务费
                BigDecimal currBrandFee = this.feeCalc(subOrder.getPayAmount(), chkSuccOrd.getTxnAmt(),
                                                       brandFeeAmtUnion);
                if (currBrandFee.compareTo(brandFeeAmtRemain) == 1)
                {
                    subOrder.setBrandFeeUnion(brandFeeAmtRemain.longValue() + "");
                    brandFeeAmtRemain = new BigDecimal("0");   //品牌服务费已分完
                }
                else
                {
                    subOrder.setBrandFeeUnion(currBrandFee.longValue() + "");
                    brandFeeAmtRemain = brandFeeAmtRemain.subtract(currBrandFee);
                }

            }
        }
    }

    /**
     * 根据子订单金额在总订单金额的比例计算子订单的应收手续费
     *
     * @param currPayAmt        : 子订单金额
     * @param totalPayAmt：订单总金额
     * @param totalFee          ： 总的手续费值
     * @return
     */
    private BigDecimal feeCalc(String currPayAmt, Long totalPayAmt, Long totalFee)
    {
        BigDecimal currFee = new BigDecimal(totalFee);
        currFee = currFee.multiply(new BigDecimal(currPayAmt)).divide(new BigDecimal(totalPayAmt), 3,
                                                                      BigDecimal.ROUND_HALF_UP);
        currFee = currFee.setScale(0, BigDecimal.ROUND_HALF_UP);

        return currFee;
    }

    /**
     * 根据红包Json信息计算红包金额(还是直接从保存的红包金额总费用总获取)
     *
     * @param subOrder
     * @return
     */
    private BigDecimal getHbAmt(PaySubOrderInfo subOrder)
    {
        BigDecimal hbAmt = new BigDecimal(0);
        if (subOrder.getBankCouponAmt() != null)
        {
            hbAmt = new BigDecimal(subOrder.getBankCouponAmt());
        }
        return hbAmt;
    }

    /**
     * 获得营销金额（银行营销 + 机构营销）
     *
     * @param subOrder
     * @return
     */
    private BigDecimal getMarketingAmt(PaySubOrderInfo subOrder)
    {
        BigDecimal marketingAmt = new BigDecimal(0);
        if (StringUtils.hasText(subOrder.getBankPayAmt()))
        {
            marketingAmt = marketingAmt.add(new BigDecimal(subOrder.getBankPayAmt()));
        }

        if (StringUtils.hasText(subOrder.getUserorgPayAmt()))
        {
            marketingAmt = marketingAmt.add(new BigDecimal(subOrder.getUserorgPayAmt()));
        }

        return marketingAmt;
    }

    /**
     * 根据红包Json信息计算指定优惠类型金额
     *
     * @param subOrder   ： 子订单信息
     * @param couponType ：
     * @return
     */
    private BigDecimal getCouponAmt(PaySubOrderInfo subOrder, String couponType)
    {
        String couponDesc = subOrder.getCouopnDesc();
        String couponAmtStr = "0";
        if (couponDesc != null)
        {
            log.debug(">>>>>>>>>>>>>>>>>>>coupon desc" + couponDesc);
            //couponDesc = couponDesc.replaceAll("\\[", "").replaceAll("\\]", "");
            if (!"".equals(couponDesc))
            {
                //Map couponMap = IfspFastJsonUtil.jsonTOmap(couponDesc);
                List<Map> couponList = IfspFastJsonUtil.jsonTOlist(couponDesc);
                for (Map m : couponList)
                {
                    if (couponType.equals(m.get("consumeType") + ""))
                    {
                        if (m.get("consumeAmt") != null && !"".equals(m.get("consumeAmt")))
                        {
                            couponAmtStr = m.get("consumeAmt") + "";
                        }
                        break;
                    }

                }

            }
        }

        BigDecimal couponAmt = new BigDecimal(couponAmtStr);
        return couponAmt;
    }

    /**
     * 查询物流费用
     *
     * @param subOrder
     * @return
     */
    private BigDecimal getLogisAmt(PaySubOrderInfo subOrder)
    {
        BigDecimal logisAmt = new BigDecimal(0);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("subOrderSsn", subOrder.getSubOrderSsn());
        List<OrderTplInfo> logisList = orderTplInfoDao.selectList("selectOrderTplInfoBySubOrderId", params);
        if (logisList.size() > 0)
        {
            logisAmt = new BigDecimal(logisList.get(0).getTplOrderAmt());

            //取物流信息，存入账明细表
            subOrder.setLogisType(logisList.get(0).getTplType());
            subOrder.setLogisFee(logisList.get(0).getTplOrderAmt());
            subOrder.setLogisPartnerCode(logisList.get(0).getTplId());
        }

        return logisAmt;
    }

    /**
     * 根据交易金额计算返佣费用
     *
     * @param txnAmt      ： 交易金额
     * @param parentMerId ： 父商户ID
     * @return
     */
    private BigDecimal calcCommissionAmt(BigDecimal txnAmt, String parentMerId)
    {
        BigDecimal commissionAmt = new BigDecimal(0);
        if (parentMerId == null || parentMerId == "")
        {
            return commissionAmt;
        }
        //查询上级商户信息
        MchtContInfo parentMerStlInfo = this.getMerStlInfo(parentMerId);
        if (parentMerStlInfo != null)
        {
            String commType = parentMerStlInfo.getCommType();
            if (Constans.COMM_TYPE_NONE.equals(commType))
            {
                //无返佣
                return commissionAmt;
            }
            else if (Constans.COMM_TYPE_FIX_AMT.equals(commType))
            {
                //按固定金额返佣
                if (parentMerStlInfo.getCommParam() != null)
                {
                    commissionAmt = parentMerStlInfo.getCommParam().multiply(ONE_HUNDRED);   //按固定金额收取佣金是，参数以元为单位
                }
            }
            else if (Constans.COMM_TYPE_BY_RATE.equals(commType))
            {
                //按比例返佣
                if (parentMerStlInfo.getCommParam() != null)
                {
                    commissionAmt = parentMerStlInfo.getCommParam().multiply(txnAmt).divide(
                            ONE_HUNDRED);   //按百分比收取佣金是，参数为百分比
                }
            }

        }

        //四舍五入，取整
        commissionAmt = commissionAmt.setScale(0, BigDecimal.ROUND_HALF_UP);

        return commissionAmt;
    }


    /**
     * 计算退款订单佣金
     *
     * @param orderInfo ： 退款订单
     * @return
     */
    private BigDecimal calcCommissionAmt4Return(PayOrderInfo orderInfo,PayOrderInfo origOrderInfo)
    {
        //1)计算原交易佣金
        BigDecimal oriCommFee = new BigDecimal(0);
        if(origOrderInfo==null)
        {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("orderSsn", orderInfo.getOrigOrderSsn());
            origOrderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);
        }
        if (IfspDataVerifyUtil.isBlank(origOrderInfo.getBankCouponAmt()))
        {
            origOrderInfo.setBankCouponAmt("0");
        }

        BigDecimal txnAmt = new BigDecimal(origOrderInfo.getPayAmt());
        if (StringUtils.hasText(origOrderInfo.getBankCouponAmt()))
        {
            txnAmt = txnAmt.add(new BigDecimal(origOrderInfo.getBankCouponAmt()));
        }

        oriCommFee = this.calcCommissionAmt(txnAmt, origOrderInfo.getMchtId());

        //2)计算已退款佣金
        BigDecimal returnedFee = new BigDecimal(0);
        BigDecimal returnedAmt = new BigDecimal(0);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("oriOrderSsn", orderInfo.getOrigOrderSsn());
        List<BthMerInAccDtl> returnedList = bthMerInAccDtlDao.selectList("selectRetOrderInfoByOriOrderSsn", m);
        for (BthMerInAccDtl dtl : returnedList)
        {
            returnedFee = returnedFee.add(new BigDecimal(dtl.getCommissionAmt()));  //累加已退手续费
            returnedAmt = returnedAmt.add(new BigDecimal(dtl.getTxnAmt()));         //累加已退金额
        }

        //3)按比例计算当前退款订单应退手续费
        if (IfspDataVerifyUtil.isBlank(orderInfo.getBankCouponAmt()))
        {
            orderInfo.setBankCouponAmt("0");
        }
        BigDecimal retAmt = new BigDecimal(orderInfo.getPayAmt()).add(new BigDecimal(orderInfo.getBankCouponAmt()));
        BigDecimal retFee = this.calcReturnFee(txnAmt, retAmt, oriCommFee);


        //4)计算实际退款手续费
        if (retFee.compareTo(oriCommFee.subtract(returnedFee)) == 1)
        {
            retFee = oriCommFee.subtract(returnedFee);
        }

        if (txnAmt.compareTo(returnedAmt.add(retAmt)) == 0)    //已退完
        {
            if (retFee.compareTo(oriCommFee.subtract(returnedFee)) != 0)
            {
                retFee = oriCommFee.subtract(returnedFee);
            }
        }

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        return retFee;
    }


    /**
     * 计算退款订单佣金
     *
     * @return
     */
    private BigDecimal calcCommissionAmt4SubOrder4Return(PaySubOrderInfo subOrderInfo,PaySubOrderInfo origSubOrderInfo)
    {
        //1)计算原交易佣金
        BigDecimal oriCommFee = new BigDecimal(0);

        if(origSubOrderInfo==null)
        {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("subOrderSsn", subOrderInfo.getOriDetailsId());
            origSubOrderInfo = paySubOrderInfoDao.selectOne("selectSubOrderInfoBySubOrderNo", parameter);
        }


        if (IfspDataVerifyUtil.isBlank(origSubOrderInfo.getBankCouponAmt()))
        {
            origSubOrderInfo.setBankCouponAmt("0");
        }

        BigDecimal txnAmt = new BigDecimal(origSubOrderInfo.getPayAmount()).add(
                this.getMarketingAmt(origSubOrderInfo));  //支付 + 营销
        if (StringUtils.hasText(origSubOrderInfo.getBankCouponAmt()))
        {
            txnAmt = txnAmt.add(new BigDecimal(origSubOrderInfo.getBankCouponAmt()));    // + 红包（包括积分抵扣）
        }

        //原订单佣金
        oriCommFee = this.calcCommissionAmt(txnAmt, origSubOrderInfo.getSubMchtId());

        //2)计算已退款佣金
        BigDecimal returnedFee = new BigDecimal(0);
        BigDecimal returnedAmt = new BigDecimal(0);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("oriOrderSsn", subOrderInfo.getOriDetailsId());
        List<BthMerInAccDtl> returnedList = bthMerInAccDtlDao.selectList("selectRetOrderInfoByOriOrderSsn", m);

        for (BthMerInAccDtl dtl : returnedList)
        {
            returnedFee = returnedFee.add(new BigDecimal(dtl.getCommissionAmt()));  //累加已退手续费
            returnedAmt = returnedAmt.add(new BigDecimal(dtl.getTxnAmt()));         //累加已退金额
        }

        //3)按比例计算当前退款订单应退手续费
        if (IfspDataVerifyUtil.isBlank(subOrderInfo.getBankCouponAmt()))
        {
            subOrderInfo.setBankCouponAmt("0");
        }
        BigDecimal retAmt = new BigDecimal(subOrderInfo.getPayAmount()).add(
                new BigDecimal(subOrderInfo.getBankCouponAmt())).
                add(this.getMarketingAmt(subOrderInfo));

        BigDecimal retFee = this.calcReturnFee(txnAmt, retAmt, oriCommFee);


        //4)计算实际退款佣金
        if (retFee.compareTo(oriCommFee.subtract(returnedFee)) == 1)
        {
            retFee = oriCommFee.subtract(returnedFee);
        }

        if (txnAmt.compareTo(returnedAmt.add(retAmt)) == 0)    //已退完
        {
            if (retFee.compareTo(oriCommFee.subtract(returnedFee)) != 0)
            {
                retFee = oriCommFee.subtract(returnedFee);
            }
        }

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        return retFee;
    }

    /**
     * 根据交易金额计算行内手续费
     *
     * @param txnAmt ： 交易金额
     * @param prdId  ： pro1 线上，pro2线上
     * @return
     */
    private BigDecimal calcMerFee4Order(BigDecimal txnAmt, String merId, PayOrderInfo orderInfo, String chnlNo,
                                        String prdId)
    {
        BigDecimal merFee = new BigDecimal(0);

        String accType = orderInfo.getAcctSubTypeId();
		/*if(accType == null )          //内管手续费配置规则修改 ： 20181105
		{
			accType = "*";
		}*/
        MchtSettlRateCfg stlRateInfo = this.getMerStlRateInfo(chnlNo, merId, accType, prdId);
        if (stlRateInfo == null)
        {
            //没配置费率信息,不收手续费
            return merFee;
        }

        String rateCalcType = stlRateInfo.getRateCalType();
        if (Constans.COMM_TYPE_FIX_AMT.equals(rateCalcType))
        {
            //按固定金额返佣
            if (stlRateInfo.getRateCalParam() != null)
            {
                merFee = stlRateInfo.getRateCalParam().multiply(ONE_HUNDRED);   //参数单位为元
                if (txnAmt.compareTo(merFee) < 0)
                {
                    log.info(
                            "商户[" + merId + "]行内手续费[" + merFee + "]大于支付金额[" + txnAmt + "] , 手续费金额最多只收取订单金额, 即为 [" + txnAmt + "]分 .");
                    merFee = txnAmt;
                }
            }
        }
        else if (Constans.COMM_TYPE_BY_RATE.equals(rateCalcType))
        {
            //按比例返佣
            if (stlRateInfo.getRateCalParam() != null)
            {
                merFee = stlRateInfo.getRateCalParam().multiply(txnAmt).divide(ONE_HUNDRED);
                ;
                if (stlRateInfo.getMaxParam() != null)
                {
                    //大于最大手续费
                    if (merFee.compareTo(stlRateInfo.getMaxParam().multiply(ONE_HUNDRED)) == 1)
                    {
                        merFee = stlRateInfo.getMaxParam().multiply(ONE_HUNDRED);
                    }
                }

                if (stlRateInfo.getMinParam() != null)
                {
                    //小于最小手续费
                    if (merFee.compareTo(stlRateInfo.getMinParam().multiply(ONE_HUNDRED)) == -1)
                    {
                        merFee = stlRateInfo.getMinParam().multiply(ONE_HUNDRED);
                    }
                }

                //手续费不能大于交易金额
                if (txnAmt.compareTo(merFee) < 0)
                {
                    log.info(
                            "商户[" + merId + "]行内手续费[" + merFee + "]大于支付金额[" + txnAmt + "] , 手续费金额最多只收取订单金额, 即为 [" + txnAmt + "]分 .");
                    merFee = txnAmt;
                }
            }
        }


        //四舍五入，取整
        merFee = merFee.setScale(0, BigDecimal.ROUND_HALF_UP);

        return merFee;
    }

    private BigDecimal calcMerFee4Order4Return(PayOrderInfo orderInfo)
    {
        //1)计算原交易手续费
        BigDecimal oriMerFee = new BigDecimal(0);
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("orderSsn", orderInfo.getOrigOrderSsn());
        PayOrderInfo oriOrderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);

        if(oriOrderInfo==null)
        {
            return new BigDecimal(0);
        }

        if (IfspDataVerifyUtil.isBlank(oriOrderInfo.getBankCouponAmt()))
        {
            oriOrderInfo.setBankCouponAmt("0");
        }

        BigDecimal txnAmt = new BigDecimal(oriOrderInfo.getPayAmt()).add(
                new BigDecimal(oriOrderInfo.getBankCouponAmt()));
        //奖励金
        if (IfspDataVerifyUtil.isNotBlank(oriOrderInfo.getMchtIncentiveAmt()))
        {
            txnAmt = txnAmt.add(new BigDecimal(oriOrderInfo.getMchtIncentiveAmt()));
        }

        String chnlNo;
        if (Constans.TXN_TYPE_O1000002.equals(oriOrderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_O1000003.equals(
                oriOrderInfo.getTxnTypeNo()))
        {
            //主扫交易/微信公众号() 取CHL_NO
            chnlNo = oriOrderInfo.getChlNo();
        }
        else
        {
            //被扫支付
            chnlNo = oriOrderInfo.getAcptChlNo();     //如果是退款交易，渠道也取acptChlNo
        }

        String proId;
        if (Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(
                orderInfo.getTxnTypeNo()))
        {
            proId = Constans.PRO_ID_ONLINE;
        }
        else
        {
            proId = Constans.PRO_ID_SM;
        }

        oriMerFee = this.calcMerFee4Order(txnAmt, oriOrderInfo.getMchtId(), oriOrderInfo, chnlNo, proId);

        //2)计算已退款手续费
        BigDecimal returnedFee = new BigDecimal(0);
        BigDecimal returnedAmt = new BigDecimal(0);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("oriOrderSsn", orderInfo.getOrigOrderSsn());
        List<BthMerInAccDtl> returnedList = bthMerInAccDtlDao.selectList("selectRetOrderInfoByOriOrderSsn", m);
        for (BthMerInAccDtl dtl : returnedList)
        {
            returnedFee = returnedFee.add(new BigDecimal(dtl.getSetlFeeAmt()));  //累加已退手续费
            returnedAmt = returnedAmt.add(new BigDecimal(dtl.getTxnAmt()));         //累加已退金额
        }

        //3)按比例计算当前退款订单应退手续费
        if (IfspDataVerifyUtil.isBlank(orderInfo.getBankCouponAmt()))
        {
            orderInfo.setBankCouponAmt("0");
        }
        BigDecimal retAmt = new BigDecimal(orderInfo.getPayAmt()).add(new BigDecimal(orderInfo.getBankCouponAmt()));
        //奖励金
        if (IfspDataVerifyUtil.isNotBlank(orderInfo.getMchtIncentiveAmt()))
        {
            retAmt = retAmt.add(new BigDecimal(orderInfo.getMchtIncentiveAmt()));
        }

        BigDecimal retFee = this.calcReturnFee(txnAmt, retAmt, oriMerFee);


        //4)计算实际退款手续费
        if (retFee.compareTo(oriMerFee.subtract(returnedFee)) == 1)
        {
            retFee = oriMerFee.subtract(returnedFee);
        }

        if (txnAmt.compareTo(returnedAmt.add(retAmt)) == 0)    //已退完
        {
            if (retFee.compareTo(oriMerFee.subtract(returnedFee)) != 0)
            {
                retFee = oriMerFee.subtract(returnedFee);
            }
        }

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        return retFee;
    }


    /**
     * 退款订单号
     *
     * @param subOrderInfo
     * @return
     */
    private BigDecimal calcMerFee4SubOrder4Return(PaySubOrderInfo subOrderInfo, String prdId,PayOrderInfo origOrderInfo)
    {
        //1)计算原交易手续费
        BigDecimal oriMerFee = new BigDecimal(0);
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("subOrderSsn", subOrderInfo.getOriDetailsId());

        //查询原交易信息
        PaySubOrderInfo oriSubOrderInfo = paySubOrderInfoDao.selectOne("selectSubOrderInfoBySubOrderNo", parameter);

        if(origOrderInfo==null)
        {
            origOrderInfo=payOrderInfoDao.selectOne("selectOrderInfoBySubOrderSsn", parameter);
        }

        //统计原子订单总金额
        if (IfspDataVerifyUtil.isBlank(oriSubOrderInfo.getBankCouponAmt()))
        {
            oriSubOrderInfo.setBankCouponAmt("0");
        }

        //原交易金额
        BigDecimal txnAmt = new BigDecimal(oriSubOrderInfo.getPayAmount()).add(
                new BigDecimal(oriSubOrderInfo.getBankCouponAmt())).
                add(this.getMarketingAmt(oriSubOrderInfo));

        oriMerFee = this.calcMerFee4Order(txnAmt, oriSubOrderInfo.getSubMchtId(), origOrderInfo,
                                          oriSubOrderInfo.getFundChannel(), prdId);

        //2)计算已退款手续费
        BigDecimal returnedFee = new BigDecimal(0);
        BigDecimal returnedAmt = new BigDecimal(0);

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("oriOrderSsn", subOrderInfo.getOriDetailsId());
        List<BthMerInAccDtl> returnedList = bthMerInAccDtlDao.selectList("selectRetOrderInfoByOriOrderSsn", m);

            for (BthMerInAccDtl dtl : returnedList) {
                returnedFee = returnedFee.add(new BigDecimal(dtl.getSetlFeeAmt()));     //累加已退手续费
                returnedAmt = returnedAmt.add(new BigDecimal(dtl.getTxnAmt()));         //累加已退金额
            }

        //3)按比例计算当前退款订单应退手续费
        if (IfspDataVerifyUtil.isBlank(subOrderInfo.getBankCouponAmt()))
        {
            subOrderInfo.setBankCouponAmt("0");
        }

        //当前退款订单交易金额
        BigDecimal retAmt = new BigDecimal(subOrderInfo.getPayAmount()).add(
                new BigDecimal(subOrderInfo.getBankCouponAmt())).
                add(this.getMarketingAmt(oriSubOrderInfo));
        BigDecimal retFee = this.calcReturnFee(txnAmt, retAmt, oriMerFee);


        //4)计算实际退款手续费
        if (retFee.compareTo(oriMerFee.subtract(returnedFee)) == 1)
        {
            retFee = oriMerFee.subtract(returnedFee);
        }

        if (txnAmt.compareTo(returnedAmt.add(retAmt)) == 0)    //已退完
        {
            if (retFee.compareTo(oriMerFee.subtract(returnedFee)) != 0)
            {
                retFee = oriMerFee.subtract(returnedFee);
            }
        }

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);

        return retFee;
    }


    /**
     * 根据退款金额计算应退手续费
     *
     * @param txnAmt ： 原交易金额
     * @param retAmt ： 当前退款金额
     * @param oriFee ： 原手续费金额
     * @return
     */
    private BigDecimal calcReturnFee(BigDecimal txnAmt, BigDecimal retAmt, BigDecimal oriFee)
    {
        BigDecimal retFee = new BigDecimal(0);

        retFee = oriFee.multiply(retAmt.divide(txnAmt, 3, BigDecimal.ROUND_HALF_UP));

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);

        return retFee;
    }

    public int updateOrderStlStatus(BthChkRsltInfo chkedOrder)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderSsn", chkedOrder.getOrderSsn());
        params.put("stlmSt", Constans.SETTLE_STATUS_CLEARING);    //01-清分中
        return bthChkRsltInfoDao.update("updateChkOrderStlStatusByOrderId", params);
    }

    public int updateBatchOrderStlStatus(List<String> orderSsnList)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderSsnList", orderSsnList);
        params.put("stlmSt", Constans.SETTLE_STATUS_CLEARING);    //01-清分中
        return bthChkRsltInfoDao.update("updateChkOrderStlStatusByOrderIdList", params);
    }
}

