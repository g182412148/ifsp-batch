package com.scrcu.ebank.ebap.batch.task;


import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.UUIDCreator;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.FeeCalcService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.exception.IfspBizException;
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

/**
 * 日间核心记账定时任务T+0 :  每1分钟扫描一次记账表 , 将记账状态为 00 - 待记账的记录取出,更新为  01 - 记账中
 * 多线程调用本行通道完成记账 , 根据返回结果更新记账表
 *
 * @author ljy
 */
@Slf4j
public class TimedKeepAccountTaskT0 {

    private static BigDecimal ONE_HUNDRED = new BigDecimal(100);

    @Resource
    private KeepAccSoaService keepAccSoaService;

    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    @Resource
    private PayOrderInfoDao payOrderInfoDao;           // 订单信息

    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           // 商户基本信息

    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;   // 入账明细信息

    @Resource
    private BthSetCapitalDetailDao bthSetCapitalDetailDao;     // 清分表

    /**
     * 商户入账汇总信息
     */
    @Resource
    private BthMerInAccDao bthMerInAccDao;

    /**
     * 每个线程处理数据量
     */
    @Value("${clearSum.threadCapacity}")
    private Integer threadCapacity;

    //商户信息缓存
    private Map<String,MchtContInfo> merMapInfo = new HashMap<String,MchtContInfo>();

    private static  ExecutorService executorService = Executors.newFixedThreadPool(40);

    private static  ExecutorService executorService1 = Executors.newFixedThreadPool(40);

    private static BigDecimal ZERO = new BigDecimal(0);

    private static String BatchNo9 = "999999";

    public void coreKeepAccount() {
        log.info("======================================>>  TimedKeepAccountTaskT0  Start ......");
        // 1.扫描记账表,剔出T+0数据
        TimedKeepAccountTaskT0 timedKeepAccountTask = (TimedKeepAccountTaskT0)IfspSpringContextUtils.getInstance().getBean("TimedKeepAccountTaskT0");

        //long start=System.currentTimeMillis();
        //清算时间当天
//        String stlmDate = IfspDateTime.getYYYYMMDD();
//        //确立批次号
//        String capBatchNo = getBatchNo(stlmDate);
//        log.info("---------------------批次号---------------------"+capBatchNo);
//        if(IfspDataVerifyUtil.isEmpty(capBatchNo)){
//            capBatchNo = IfspId.getUUID(31);
//            try{
//                insertBatchNo(stlmDate, capBatchNo);
//            }catch (Exception e){
//                log.error("插入批次号异常:",e);
//            }
//
//        }

        Map<String ,List<KeepAccInfo>>  map = timedKeepAccountTask.scanTab();
        // 2.多线程调本行通道 , 一个订单一组
        if (IfspDataVerifyUtil.isNotEmptyMap(map)){
            Set<Map.Entry<String, List<KeepAccInfo>>> entries = map.entrySet();
            for (Map.Entry<String, List<KeepAccInfo>> entry : entries) {
                List<KeepAccInfo> value = getKeepAccInfos(entry);
                Runnable runnable = keepAccTask(value);
                try{
                    executorService.execute(runnable);
                }catch (Exception e){
                    log.error("T+0记账线程异常:",e);
                    continue;
                }

            }
            log.info("---------------------记账结束---------------------");
        }else {
            log.info("========================>>本次扫描没有发现需要记账的数据,处理结束<<===============================");
        }
        //log.info("本次T+0日间记账，耗时【{}】:",System.currentTimeMillis()-start);
        log.info("======================================>>  TimedKeepAccountTaskT0  End ......");
    }


    /**
     * 多线程任务去本行记账
     * @param value
     * @return
     */
    private Runnable keepAccTask(List<KeepAccInfo> value) {
        log.info("=================>>开始处理账户["+value.get(0).getInAccNo()+value.get(0).getOutAccNo()+"]记账<<====================");
        return new Runnable() {
            @Override
            public void run() {


                Iterator<KeepAccInfo> iterator = value.iterator();
                while (iterator.hasNext()) {
                    KeepAccInfo info = iterator.next();
                    SoaParams params = new SoaParams();
                    initParam(params, info);
                    //调用核心接口
                    SoaResults results = keepAccSoaService.keepAcc(params);

                    List<BthMerInAccDtl> bthMerInAccDtlList = new ArrayList<BthMerInAccDtl>();
                    List<BthSetCapitalDetail> bthSetCapitalDetails = new ArrayList<>();
                    BthMerInAccDtl orderStlInfo = new BthMerInAccDtl();
                    //1)查询订单信息
                    //查询订单信息 加上日期利用分区
                    Map<String, Object> parameter = new HashMap<String, Object>();
                    parameter.put("orderSsn", info.getOrderSsn());
                    PayOrderInfo orderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);

                    //清算时间当天
                    String stlmDate = DateUtil.format(orderInfo.getOrderTm(), "yyyyMMdd");
                    //确立批次号
//                    String capBatchNo = getBatchNo(stlmDate);
//                    if(IfspDataVerifyUtil.isEmpty(capBatchNo)){
//                        capBatchNo = IfspId.getUUID(31);
//                        insertBatchNo(stlmDate, capBatchNo);
//                    }
//                    String BatchNo = capBatchNo;
                    String BatchNo = stlmDate+BatchNo9;
                    //2)查询商户结算信息
                    MchtBaseInfo merInfo = getMerInfo(orderInfo.getMchtId());
                    MchtContInfo merStlInfo = getMerStlInfo(orderInfo.getMchtId());
                    orderStlInfo = buildSMOrderStlInfo(orderInfo, merInfo, merStlInfo, info);

                    //为批量插入清分表准备数据
                    BthSetCapitalDetail bthSetCapitalDetail = initCapitalDetail(info, orderInfo, merInfo, merStlInfo);
                    bthSetCapitalDetail.setBatchNo(BatchNo);



                    // 根据返回结果作相应处理
                    if (RespEnum.RESP_SUCCESS.getCode().equals(results.getRespCode())) {
                        log.info("===================>>本行通道受理记账成功<<===========================");
                        info.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                        info.setPagyRespCode(results.getRespCode());
                        info.setPagyRespMsg(results.getRespMsg());
                        if (IfspDataVerifyUtil.isNotBlank(results.get("HDTellrSeqNum"))) {
                            info.setReserved2(String.valueOf(results.get("HDTellrSeqNum")));
                        }
                        keepAccInfoDao.updateByPrimaryKeySelective(info);//完成记账
                        //完成记账--开始清分
                        //已实时结算
                        orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
                        //入账成功
                        orderStlInfo.setInAcctStat(Constans.IN_ACC_STAT_SUCC);
                        bthMerInAccDtlList.add(orderStlInfo);
                        // 处理状态:00-未处理 01-处理中 02-处理成功 03-处理失败
                        bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
                        // 入账状态：00 -未入账 01 - 入账成功 02 - 入账失败
                        bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
                        bthSetCapitalDetail.setDealRemark("处理成功");//处理结果描述
                        bthSetCapitalDetails.add(bthSetCapitalDetail);

                    } else if (RespEnum.RESP_TIMEOUT.getCode().equals(results.getRespCode())) {
                        log.warn("===================>>调用本行通道记账接口超时<<===========================");
                        // 记为超时 , 待日终改为记账失败
                        info.setState(Constans.KEEP_ACCOUNT_STAT_TIMEOUT);
                        info.setPagyRespCode(results.getRespCode());
                        info.setPagyRespMsg(results.getRespMsg());
                        if (IfspDataVerifyUtil.isNotBlank(results.get("HDTellrSeqNum"))){
                            info.setReserved2(String.valueOf(results.get("HDTellrSeqNum")));
                        }
                        keepAccInfoDao.updateByPrimaryKeySelective(info);

                        //完成记账--开始清分
                        //结算失败
                        orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_FAILE_CLEARING);
                        //入账失败
                        orderStlInfo.setInAcctStat(Constans.IN_ACC_STAT_FAIL);
                        // 入账状态：00 -未入账 01 - 入账成功 02 - 入账失败
                        bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_FAILE);
                        bthSetCapitalDetail.setDealRemark(results.getRespMsg());//处理结果描述
                        //bthMerInAccDtlList.add(orderStlInfo);
                        //bthSetCapitalDetails.add(bthSetCapitalDetail);
                    } else {
                        log.error("========================>>本行通道调用失败,返回码["+results.getRespCode()+"],返回信息["+results.getRespMsg()+"]<<====================");
                        info.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);
                        info.setPagyRespCode(results.getRespCode());
                        info.setPagyRespMsg(results.getRespMsg());
                        if (IfspDataVerifyUtil.isNotBlank(results.get("HDTellrSeqNum"))){
                            info.setReserved2(String.valueOf(results.get("HDTellrSeqNum")));
                        }
                        keepAccInfoDao.updateByPrimaryKeySelective(info);

                        //完成记账--开始清分
                        //结算失败
                        orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_FAILE_CLEARING);
                        //入账失败
                        orderStlInfo.setInAcctStat(Constans.IN_ACC_STAT_FAIL);
                        // 入账状态：00 -未入账 01 - 入账成功 02 - 入账失败
                        bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_FAILE);
                        bthSetCapitalDetail.setDealRemark(results.getRespMsg());//处理结果描述
                        bthMerInAccDtlList.add(orderStlInfo);
                        bthSetCapitalDetails.add(bthSetCapitalDetail);
                    }

                    try{
                        //批量插入入账明细表
                        if (bthMerInAccDtlList.size() > 0) {
                            int count = bthMerInAccDtlDao.insertBatch(bthMerInAccDtlList);
                            log.info("批量插入入账明细表[{}]条数据", count);
                        }
                        //批量插入清分表
                        if (bthSetCapitalDetails.size() > 0) {
                            int count = bthSetCapitalDetailDao.insertBatch(bthSetCapitalDetails);
                            log.info("插入[{}]清分表[{}]条", count);
                        }
                    }catch (Exception e){
                        log.info("订单号[{}]插入清分表异常",info.getOrderSsn());
                        log.error("插入清分表异常:",e);
                        continue;
                    }



                    //清分汇总t0
                    //获取入账流水号
                    Map<String ,String> map = new HashMap<>();
                    map.put("dateStlm", stlmDate);
                    map.put("outAcctNo", bthSetCapitalDetail.getOutAccountNo());
                    map.put("inAcctNo", bthSetCapitalDetail.getInAccountNo());
                    map.put("merId", bthSetCapitalDetail.getMerId());
                    BthMerInAcc bthMerInAccT0 = getTxnSsn(map);
                    if(IfspDataVerifyUtil.isEmpty(bthMerInAccT0)){
//                        Map<String,Object> pramer = new HashMap<>();
//                        pramer.put("batchNo", BatchNo);
//                        pramer.put("outAccoutOrg", bthSetCapitalDetail.getOutAccoutOrg());
//                        pramer.put("outAccountNo", bthSetCapitalDetail.getOutAccountNo());
//                        pramer.put("inAccoutOrg", bthSetCapitalDetail.getInAccoutOrg());
//                        pramer.put("inAccountNo", bthSetCapitalDetail.getInAccountNo());
//                        pramer.put("merId", bthSetCapitalDetail.getMerId());
                        //BthSetCapitalDetail sumDetail =  bthSetCapitalDetailDao.selectOne("queryOneClearSumInfoMchtInT0",pramer);
                        dealSumObj(BatchNo,bthSetCapitalDetail,orderStlInfo,DateUtil.format(orderInfo.getOrderTm(), "yyyyMMdd"));
                    }else{
                        bthMerInAccT0.setInAcctStat(bthMerInAccT0.getInAcctStat().equals(bthSetCapitalDetail.getAccountStauts().trim().substring(1, 2))?bthMerInAccT0.getInAcctStat():"3");
                        bthMerInAccT0.setInAcctAmt(bthSetCapitalDetail.getTranAmount().toString());
                        bthMerInAccT0.setTxnAmt(orderStlInfo.getTxnAmt());
                        bthMerInAccT0.setFeeAmt(orderStlInfo.getTramFeeAmt());
                        updateBthMerInAccDaoT0ForTxnSsn(bthMerInAccT0);
                    }
                    log.info("=================>>订单号["+orderStlInfo.getTxnSeqId()+"]记账结束<<====================");

                }

                log.info("=================>>账户["+value.get(0).getInAccNo()+value.get(0).getOutAccNo()+"]记账结束<<====================");
            }
        };
    }


    /**
     * 事务控制锁表,防止多台机器同时查到
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String ,List<KeepAccInfo>> scanTab()  {
        log.info("========================>>定时任务开始扫描记账表<<===============================");
        // 初始化记账map
        Map<String ,List<KeepAccInfo>> map = new HashMap<>(10);

        // 1.根据T+0,实时 ,订单状态锁表
        Map<String ,Object> param = new HashMap<>(2);
        param.put("isSync",Constans.KEEP_ACCT_FLAG_ASYNC);
        param.put("state",Constans.KEEP_ACCOUNT_STAT_PRE);
        param.put("realTmFlag", Constans.isRealTmFlag);
        List<KeepAccInfo> list = keepAccInfoDao.selectList("selectByOrderSsnAndStateT0", param);
        log.info("=====================>>本次T+0需要记账条数["+list.size()+"]<<===========================");
        // 2.改为记账中 , 并且整理打包出 账户对应的流水
        Map<String,SoaParams> mapT0 = new HashMap<>();
        if (IfspDataVerifyUtil.isNotEmptyList(list)) {
            Iterator<KeepAccInfo> iterator = list.iterator();
            KeepAccInfo info;
            String hostAddress;
            while (iterator.hasNext()) {
                info = iterator.next();
                //判断是否再次记账
                if(IfspDataVerifyUtil.equals("9999", info.getPagyRespCode())){
                    keepAccInfoDao.updateByState(info.getCoreSsn(),Constans.KEEP_ACCOUNT_STAT_FAIL,info.getRegisterIp());
                    continue;
                };
                // 判断金额是否是0 , 如果是 0 则直接改为记账成功
                if (0L == info.getTransAmt()){
                    keepAccInfoDao.updateByState(info.getCoreSsn(),Constans.KEEP_ACCOUNT_STAT_SUCCESS,info.getRegisterIp());
                }else {
                    info.setState(Constans.KEEP_ACCOUNT_STAT_IN);
                    try {
                        hostAddress = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException e) {
                        log.error("未知服务主机ip!!!",e);
                        hostAddress = "Unknown IP";
                    }
                    info.setRegisterIp(hostAddress);
                    // // 分商户出入账号整理记账流水
                    String inOutAccNo;
                    inOutAccNo = info.getInAccNo()+info.getOutAccNo();
                    if(map.containsKey(inOutAccNo)){
                        List<KeepAccInfo> list1 = map.get(inOutAccNo);
                        list1.add(info);
                    }else {
                        List<KeepAccInfo> list2 = new ArrayList<>();
                        list2.add(info);
                        map.put(inOutAccNo,list2);
                    }
                    //keepAccInfoDao.updateByStateT0(info.getCoreSsn(),info.getState(),info.getRegisterIp(),mapT0.get(inOutAccNo).get("pagyPayTxnSsn").toString());
                    keepAccInfoDao.updateByState(info.getCoreSsn(),info.getState(),info.getRegisterIp());
                }

            }
        }

        return map;
    }

    /**
     * 根据商户查询商户基本信息
     * @param mchtId
     * @return
     */
    private MchtBaseInfo getMerInfo(String mchtId)
    {
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("mchtId", mchtId);
        MchtBaseInfo merInfo = mchtBaseInfoDao.selectOne("selectMerInfoByMchtId", m);

        return merInfo;
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
            if(IfspDataVerifyUtil.isNotEmpty(merInfo)){
                merMapInfo.put(mchtId, merInfo);
            }
        }
        return merInfo;
    }

    /**
     *
     * @param
     * @param merInfo
     * @return
     */
    private BthMerInAccDtl buildSMOrderStlInfo(PayOrderInfo orderInfo, MchtBaseInfo merInfo, MchtContInfo merStlInfo, KeepAccInfo keepAccInfo)
    {
        //根据商户结算（合同）信息计算订单结算日期
        String stlDate = DateUtil.format(orderInfo.getOrderTm(), "yyyyMMdd");
        //init
        BthMerInAccDtl orderStlInfo = new BthMerInAccDtl();

        //交易类型
        if(Constans.TXN_TYPE_CANCEL.equals(orderInfo.getTxnTypeNo()) ||
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
        orderStlInfo.setInAcctDate(stlDate);          //入账日期
        orderStlInfo.setChlMerId(merInfo.getMchtId());    //商户号
        orderStlInfo.setChlMerName(merInfo.getMchtName());   //商户名称
        orderStlInfo.setTxnTm(orderInfo.getOrderTm());//时间

        //商户为二级商户
        if(merInfo.getMchtId().length() > 15 && !IfspDataVerifyUtil.isBlank(merInfo.getParMchId()))
        {
            orderStlInfo.setChlSubMerId(merInfo.getMchtId());
            orderStlInfo.setChlSubMerName(merInfo.getMchtName());
        }

        //orderStlInfo.setPagyNo(getPagyNO(orderInfo.getPagyTxnSsn()));   //通道编号
        orderStlInfo.setOpenBrc(orderInfo.getOpenBrc());    //本行支付开卡机构

        //金额初始化
        orderStlInfo.setBankHbAmt("0");
        orderStlInfo.setLogisFee("0");
        orderStlInfo.setBrandFee("0");
        orderStlInfo.setPointDedcutAmt("0");
        //已实时结算
        //orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
        //入账成功
        //orderStlInfo.setInAcctStat(Constans.IN_ACC_STAT_SUCC);


        //设置交易类型与接入渠道
        orderStlInfo.setTxnType(orderInfo.getTxnTypeNo());
        if(Constans.TXN_TYPE_O1000002.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_O1000003.equals(orderInfo.getTxnTypeNo()))
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
        if(IfspDataVerifyUtil.isBlank(orderInfo.getPayAmt()))
        {
            orderInfo.setPayAmt("0");
        }
        BigDecimal txnAmt = new BigDecimal(orderInfo.getPayAmt());    //交易金额
        BigDecimal merFee = keepAccInfo.getFeeAmt();    //行内手续费
        BigDecimal stlAmt = new BigDecimal(keepAccInfo.getTransAmt());    //结算金额
        BigDecimal bankCouponAmt = ZERO;
        if(IfspDataVerifyUtil.isNotEmpty(orderInfo.getBankCouponAmt())){
            bankCouponAmt = new BigDecimal(orderInfo.getBankCouponAmt());
        }
        //营销总金额(商户营销总金额(单位分，如：1.23元=123)+银行营销总金额(单位分，如：1.23元=123))
        if(IfspDataVerifyUtil.isNotEmpty(orderInfo.getMchtCouponAmt())){
            bankCouponAmt = new BigDecimal(orderInfo.getMchtCouponAmt()).add(new BigDecimal(orderInfo.getBankCouponAmt()));
        }


        if(orderInfo.getBankCouponAmt() != null)
        {
            bankCouponAmt = new BigDecimal(orderInfo.getBankCouponAmt());
            txnAmt = txnAmt.add(bankCouponAmt);
        }

        //商户奖励金
        if(IfspDataVerifyUtil.isNotBlank(orderInfo.getMchtIncentiveAmt())){
            BigDecimal mchtIncentiveAmt=new BigDecimal(orderInfo.getMchtIncentiveAmt());
            txnAmt=txnAmt.add(mchtIncentiveAmt);
        }

        //佣金
        BigDecimal commissionAmt = keepAccInfo.getCommissionAmt();




        //task 15047 佣金金额 + 手续费金额不能大于订单金额
        if(txnAmt.compareTo(merFee.add(commissionAmt)) == -1)
        {
            commissionAmt = txnAmt.subtract(merFee);
        }


        //交易金额=支付金额+红包抵扣金额？
        orderStlInfo.setTxnAmt(txnAmt.intValue() + "");
        merFee = merFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        stlAmt = txnAmt.subtract(merFee).subtract(commissionAmt);//清算金额
        orderStlInfo.setSetlAmt(stlAmt.intValue() + "");//清算金额
        orderStlInfo.setSetlFeeAmt(merFee.intValue()+"");       //行内手续费
        orderStlInfo.setCommissionAmt(commissionAmt.intValue()+"");//佣金
        orderStlInfo.setBankCouponAmt(bankCouponAmt.intValue()+"");//营销金额



        //第三方手续费
//        if(checkedOrder.getTpamTxnFeeAmt() == null)
//        {
//            orderStlInfo.setTramFeeAmt("0");
//        }
//        else
//        {
//            orderStlInfo.setTramFeeAmt(checkedOrder.getTpamTxnFeeAmt()+"");
//        }

        orderStlInfo.setTxnSeqId(orderInfo.getOrderSsn());           //设置订单号
        orderStlInfo.setAgentName(orderInfo.getOrigOrderSsn());      //原交易订单号，针对退款交易有效
        orderStlInfo.setCreateDate(DateUtil.format(new Date(), "yyyyMMdd"));

        //设置品牌服务费
//        if(!IfspDataVerifyUtil.isBlank(checkedOrder.getUnionBrandFee()))
//        {
//            orderStlInfo.setBrandFee(checkedOrder.getUnionBrandFee().intValue()+"");
//        }



        return orderStlInfo;
    }

    /**
     * 记账排序
     * @param entry
     * @return
     */
    private List<KeepAccInfo> getKeepAccInfos(Map.Entry<String, List<KeepAccInfo>> entry) {
        List<KeepAccInfo> value = entry.getValue();
//            // 按照订单号排序入账
//            Collections.sort(value,new Comparator<KeepAccInfo>() {
//                @Override
//                public int compare(KeepAccInfo o1, KeepAccInfo o2) {
//                    return Integer.valueOf(o1.getOrderSsn()) -Integer.valueOf(o2.getOrderSsn());
//                }
//            });
        return value;
    }

    /**
     * 初始化本金资金明细
     *
     * @param
     * @return
     */
    private BthSetCapitalDetail initCapitalDetail(KeepAccInfo keepAccInfo, PayOrderInfo orderInfo , MchtBaseInfo mgtMerInfo, MchtContInfo merStlInfo )
    {
        Date d = orderInfo.getOrderTm();
        String curDate = DateUtil.format(d, "yyyyMMdd");
        String merId = mgtMerInfo.getMchtId();

        BthSetCapitalDetail detail = new BthSetCapitalDetail();
        detail.setId(UUIDCreator.randomUUID().toString());
        detail.setCleaTime(curDate); // 清算日期
        detail.setOrderId(keepAccInfo.getOrderSsn());    //订单流水号(扫码为订单号，线上未子订单号)
        detail.setMerId(mgtMerInfo.getMchtId());
        detail.setMerName(mgtMerInfo.getMchtName()); // 商户名称
        detail.setOutAccountNo(keepAccInfo.getOutAccNo());//出账
        detail.setOutAccountName(keepAccInfo.getOutAccNoName());//出账名
        detail.setOutAccoutOrg(merStlInfo.getLiqAcctOrgId());//转出账户所在机构
        detail.setInAccountNo(keepAccInfo.getInAccNo());//入账
        detail.setInAccountName(keepAccInfo.getInAccNoName());//入账名
        detail.setInAccoutOrg(merStlInfo.getSettlAcctOrgId());//转入账户所在机构
        detail.setTransCur("01"); // 交易币种
        detail.setAccountType(merStlInfo.getSettlAcctType());//交易类型
        //detail.setTranAmount(new BigDecimal(orderInfo.getTxnAmt()).divide(ONE_HUNDRED));//交易金额
        detail.setTranAmount(new BigDecimal(keepAccInfo.getTransAmt()).divide(ONE_HUNDRED));//交易金额


        // 分录流水类型:01-商户入账(商户本金结算)；02-手续费分润（行内手续费进行分润）；03-手续费垫付（行内手续费垫付）04-商户手续费扣帐（商户手续费结算）；05-第三方手续费入账（第三方资金通道的手续费结算）
        detail.setEntryType(Constans.ENTRY_TYPE_MER);

        detail.setTranType("01");    // 交易类型:01-消费 02-退货
        detail.setFundChannel(orderInfo.getChlNo());     // 资金通道:01-微信记账

        // 处理状态:00-未处理 01-处理中 02-处理成功 03-处理失败
        //detail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
        // 入账状态：00 -未入账 01 - 入账成功 02 - 入账失败
        //detail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
        detail.setCreateDate(DateUtil.format(new Date(), "yyyyMMddHHmmss")); // 创建时间
        //detail.setDealRemark("处理成功");//处理结果描述
        detail.setMerOrderId(keepAccInfo.getOrderSsn());//


        return detail;
    }


    /**
     * 统计待汇总的条数
     * @param capBatchNo
     * @return
     */
    private int getSumCount(String capBatchNo) {
        Map<String ,Object> map = new HashMap<>();
        map.put("batchNo",capBatchNo);
        //统计待汇总的条数
        return bthSetCapitalDetailDao.count("countInAccSumT0",map);
    }

    /**
     * 获取当天的批次号
     * @param dateStlm
     * @return
     */
    private String getBatchNo(String dateStlm){
        return bthMerInAccDao.getBatchNo(dateStlm);

    }
    /**
     * 插入当天的批次号
     * @param dateStlm
     * @return
     */
    private void insertBatchNo(String dateStlm,String batchNo){
        bthMerInAccDao.insertBatchNo(dateStlm,batchNo);
    }

    /**
     * 更新
     * @param bthMerInAccT0
     * @return
     */
    private  int updateBthMerInAccDaoT0ForTxnSsn(BthMerInAcc bthMerInAccT0){
        return bthMerInAccDao.updateBthMerInAccDaoT0ForTxnSsn(bthMerInAccT0);

    }

    /**
     * 获取入账流水号
     * @param map
     * @return
     */
    private BthMerInAcc getTxnSsn(Map<String,String> map){
        return bthMerInAccDao.getTxnSsn(map);

    }

    /**
     * 多线程任务去清分汇总
     * @param
     * @return
     */
    /*private Runnable bthMerInAccT0(String stlmDate,String capBatchNo,DataInterval dataInterval) {
        log.debug("====处理{}数据(start)====", dataInterval);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    // 汇总清分表
                    Map<String ,Object>  map = new HashMap<>();
                    map.put("batchNo",capBatchNo);
                    map.put("minIndex",dataInterval.getMin());
                    map.put("maxIndex",dataInterval.getMax());
                    // 商户汇总
                    List<BthSetCapitalDetail> bthMerInAccList =  bthSetCapitalDetailDao.selectList("queryClearSumInfoMchtInT0",map);
                    int size = bthMerInAccList.size();
                    log.info("待处理入账汇总记录总条数: {}",size);

                    // sumDetail 为汇总的对象
                    for (BthSetCapitalDetail sumDetail : bthMerInAccList) {
                        dealSumObj(capBatchNo,sumDetail,stlmDate);
                    }
                    log.debug("====处理{}数据(end)====", dataInterval);
                } catch (Exception e) {
                    log.error("汇总未知异常:", e);
                    log.debug("====处理{}数据(error end)====", dataInterval);
                    throw e;
                }
            }
        };
    }*/

    /**
     * 处理汇总对象
     * @param capBatchNo
     * @param sumDetail
     * @param stlmDate
     */
    private void dealSumObj(String capBatchNo, BthSetCapitalDetail sumDetail,BthMerInAccDtl orderStlInfo, String stlmDate){

        BthMerInAcc bthMerInAcc = new BthMerInAcc();
        // 本行卡
        bthMerInAcc.setBrno(Constans.SETTL_ACCT_TYPE_PLAT);
        // 清算日期
        bthMerInAcc.setDateStlm(stlmDate);
        // 入账状态  0- 待入账  1- 入账成功  2- 入账失败 3-部分成功
        bthMerInAcc.setInAcctStat(sumDetail.getAccountStauts().trim().substring(1, 2));
        // 处理状态  0-未处理  1-处理中 2-处理成功 3-处理失败(参考清分表)
        bthMerInAcc.setHandleState(Constans.HANDLE_STATE_SUCC);
        bthMerInAcc.setHandleMark("处理成功");
        // 入账流水号
        bthMerInAcc.setTxnSsn(IfspId.getUUID(32));
        // 商户号
        bthMerInAcc.setChlMerId(sumDetail.getMerId());
        // 商户名
        bthMerInAcc.setChlMerName(sumDetail.getMerName());
        // 二级商户号
        bthMerInAcc.setChlSubMerId(sumDetail.getSubMerId());
        // 二级商户名
        bthMerInAcc.setChlSubMerName(sumDetail.getSubMerName());
        // 出账账户账号
        bthMerInAcc.setOutAcctNo(sumDetail.getOutAccountNo());
        // 出账账户名称
        bthMerInAcc.setOutAcctName(sumDetail.getOutAccountName());
        // 出账账户机构
        bthMerInAcc.setOutAcctNoOrg(sumDetail.getOutAccoutOrg());
        // 借方标记
        if (IfspDataVerifyUtil.isBlank(sumDetail.getOutAccountNo())) {
            bthMerInAcc.setBorrowFlag(Constans.BORROW_FEE_TO_BE_ALLOCATED);
            if (Constans.ENTRY_TYPE_FEE_PAY_SD_ORG.equals(sumDetail.getEntryType())
                    ||Constans.ENTRY_TYPE_FEE_PAY_OPEN_ORG.equals(sumDetail.getEntryType())
                    ||Constans.ENTRY_TYPE_FEE_PAY_OPERATE_ORG.equals(sumDetail.getEntryType())){
                bthMerInAcc.setBorrowFlag(Constans.BORROW_FEE_EXPENSE);
            }
        }else {
            bthMerInAcc.setBorrowFlag(Constans.BORROW_ALLOCATED_ACCT);
        }

        // 入账账户账号
        bthMerInAcc.setInAcctNo(sumDetail.getInAccountNo());
        // 入账账户名称
        bthMerInAcc.setInAcctName(sumDetail.getInAccountName());
        // 入账账户机构
        bthMerInAcc.setInAcctNoOrg(sumDetail.getInAccoutOrg());
        // 贷方标记
        if (IfspDataVerifyUtil.isBlank(sumDetail.getInAccountNo())){
            bthMerInAcc.setLendFlag(Constans.LEND_FEE_INCOME);
        }else {
            bthMerInAcc.setLendFlag(Constans.LEND_ALLOCATED_ACCT);
        }

        // 入账金额
        bthMerInAcc.setInAcctAmt(String.valueOf(sumDetail.getTranAmount()));

        // 设置入账类型
        initInAcctType(sumDetail, bthMerInAcc);


        // 批次号
        bthMerInAcc.setBatchNo(capBatchNo);

        // 如果是商户入账 , 根据清分表订单号查询明细表,得到交易笔数 ,总订单金额
        if (Constans.IN_ACCT_TYPE_MCHT.equals(bthMerInAcc.getInAcctType())){
            // 统计出手续费, 交易金额 , 交易笔数BthMerInAccInfo
            /*BthMerInAccInfo inf = getBthMerInAccInfo(capBatchNo, sumDetail);
            bthMerInAcc.setTxnAmt(String.valueOf(inf.getTxnAmt()));
            bthMerInAcc.setFeeAmt(String.valueOf(inf.getFeeAmt()));
            bthMerInAcc.setTxnCount(inf.getTxnCount());*/
            bthMerInAcc.setTxnAmt(String.valueOf(orderStlInfo.getTxnAmt()));
            bthMerInAcc.setFeeAmt(String.valueOf(orderStlInfo.getSetlFeeAmt()));
            bthMerInAcc.setTxnCount(1);
        }
        //获取入账流水号
        /*Map<String ,String> map = new HashMap<>();
        map.put("dateStlm", stlmDate);
        map.put("outAcctNo", bthMerInAcc.getOutAcctNo());
        map.put("inAcctNo", bthMerInAcc.getInAcctNo());
        map.put("merId", bthMerInAcc.getChlMerId());
        String txnSsn = getTxnSsn(map);
        if(IfspDataVerifyUtil.isEmpty(txnSsn)){
            int n = bthMerInAccDao.insertT0(bthMerInAcc);
        }else{
            bthMerInAcc.setTxnSsn(txnSsn);
            bthMerInAccDao.updateByPrimaryKeySelectiveT0(bthMerInAcc);
        }*/
        try{
            int n = bthMerInAccDao.insertT0(bthMerInAcc);
        } catch (Exception e) {
            log.error("插入T+0汇总表失败", e);
            Map<String ,String> map = new HashMap<>();
            map.put("dateStlm", stlmDate);
            map.put("outAcctNo", sumDetail.getOutAccountNo());
            map.put("inAcctNo", sumDetail.getInAccountNo());
            map.put("merId", sumDetail.getMerId());
            BthMerInAcc bthMerInAccT0 = getTxnSsn(map);
            if(IfspDataVerifyUtil.isEmpty(bthMerInAccT0)){
                log.info("插入汇总表失败，同时查询汇总表失败"+map);
                return;
            }
            bthMerInAcc.setInAcctStat(bthMerInAccT0.getInAcctStat().equals(bthMerInAcc.getInAcctStat())?bthMerInAccT0.getInAcctStat():"3");
            updateBthMerInAccDaoT0ForTxnSsn(bthMerInAcc);
        }


    }

    /**
     * 统计交易金额,手续费,交易笔数
     * @param capBatchNo
     * @param sumDetail
     * @return
     */
    private BthMerInAccInfo getBthMerInAccInfo(String capBatchNo, BthSetCapitalDetail sumDetail) {
        // 组装参数
        Map<String ,Object> inAccDtlMap = new HashMap<>();
        // 查询参数
        inAccDtlMap.put("merId",sumDetail.getMerId());
        inAccDtlMap.put("entryType","01");
        inAccDtlMap.put("entryType2","01");
        inAccDtlMap.put("capBatchNo",capBatchNo);

        // 根据订单号汇总交易金额 , 商户手续费
        BthMerInAccInfo bthMerInAccInfo = bthMerInAccDtlDao.sumTxnAmtFeeAmt(inAccDtlMap);
        return bthMerInAccInfo;
    }

    /**
     * 初始化入账类型
     * 与ClearingServiceImpl getEntryTypes方法对应!!!
     * @param sumDetail
     * @param bthMerInAcc
     */
    private void initInAcctType(BthSetCapitalDetail sumDetail, BthMerInAcc bthMerInAcc) {
        // 设置入账类型
        switch (sumDetail.getEntryType()){
            // 商户入账   ( 当分录类型为 99 -商户退款 时,看作 01-商户入账 ,汇总sql查询时与01一起汇总)
            case Constans.ENTRY_TYPE_MER:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_MCHT);
                break;
            // 通道还钱
            case Constans.ENTRY_TYPE_BRANCH_FEE:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_PAGY);
                break;
            // 手续费 收入
            case Constans.ENTRY_TYPE_FEE_GAINS_SD_ORG:
            case Constans.ENTRY_TYPE_FEE_GAINS_OPEN_ORG:
            case Constans.ENTRY_TYPE_FEE_GAINS_UNIVERSAL:
            case Constans.ENTRY_TYPE_FEE_GAINS_OPERATE_ORG:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_FEE_2);
                break;
            // 手续费支出
            case Constans.ENTRY_TYPE_FEE_PAY_SD_ORG:
            case Constans.ENTRY_TYPE_FEE_PAY_OPEN_ORG:
            case Constans.ENTRY_TYPE_FEE_PAY_OPERATE_ORG:
            case Constans.ENTRY_TYPE_FEE_PAY_EBANK:
            case Constans.ENTRY_TYPE_FEE_GUARANTEE:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_FEE);
                break;
            // 日间记账失败
            case Constans.ENTRY_TYPE_FOR_ACCOUNT:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_DAY_FAIL);
                break;
            // 保证金
            case Constans.ENTRY_TYPE_FOR_GUARANTEE:
                //case Constans.ENTRY_TYPE_MER_FOR_GUARANTEE:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_GUARANTEE);
                break;
            // 商户佣金收入
            case Constans.ENTRY_TYPE_COMM_IN:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPR_COMMISSION);
                break;
            default:
                // 没有这种状态 ,不予处理
                log.error("没有["+sumDetail.getEntryType()+"]对应的分录流水类型!!!");

        }
    }

    /**
     * t+0清分汇总
     * @param capBatchNo
     * @param capBatchNo
     * @return
     */
    /*private void CapitalSummarizeStepT0(String capBatchNo, String stlmDate) {
        log.info("---------------------清分汇总开始---------------------");
        log.info("清算时间为： " + stlmDate);
        int count = getSumCount(capBatchNo);
        log.info("统计待汇总的条数为[{}]",count );
        //分组数量
        int groupCount = (int) Math.ceil((double) count / threadCapacity);

        //处理结果
        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++) {
            int minIndex = (groupIndex-1)* threadCapacity + 1;
            int maxIndex = groupIndex* threadCapacity ;
            Runnable runnable = bthMerInAccT0(stlmDate,capBatchNo,new DataInterval(minIndex, maxIndex));
            executorService1.execute(runnable);
        }

        log.info("---------------------清分汇总结束---------------------");
    }*/

    /**
     * 组装参数
     * @param params
     * @param info
     */
    public static void initParam(SoaParams params,KeepAccInfo info) {
        //通道支付请求流水号
        params.put("pagyPayTxnSsn", info.getCoreSsn());
        //通道支付请求流水时间
        params.put("pagyPayTxnTm", IfspDateTime.getYYYYMMDDHHMMSS());
        //通道支付订单号
        params.put("pagyTxnSsn", info.getOrderSsn());
        //通道订单时间
        params.put("pagyTxnTm", info.getOrderTm());
        //借方账号
        params.put("dtAcctNo", info.getOutAccNo());
        //贷方账号
        params.put("ctAcctNo", info.getInAccNo());
        //交易描述
        params.put("txnDesc", info.getTxnDesc());
        //支付金额
        params.put("txnAmt", info.getTransAmt());
        //币种
        params.put("txnCcyType", Constans.CCY_TYPE);
        // 非必输,有就送
        //借方账户名称
        params.put("dtAcctNm", info.getOutAccNoName());
        //贷方账户名称
        params.put("ctAcctNm", info.getInAccNoName());
        //商户所在机构
        params.put("proxyOrg", info.getProxyOrg());
        // 摘要
        if (IfspDataVerifyUtil.isNotBlank(info.getMemo())){
            params.put("memo",info.getMemo());
        }
    }



}
