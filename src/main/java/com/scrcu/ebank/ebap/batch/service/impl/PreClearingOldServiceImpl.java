package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.service.PreClearingOldService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthChkRsltInfoDao;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtSettlRateCfgDao;
import com.scrcu.ebank.ebap.batch.dao.OrderTplInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.PreClearingService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author ydl
 * modified @ 20181221 for online marketing
 * 线上订单实际交易金额需加上银行营销金额和用户机构营销金额
 */
@Service
@Slf4j
public class PreClearingOldServiceImpl implements PreClearingOldService {
    private static BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private static final int PAGE_SIZE = 100;

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


    public CommonResponse prepare(BatchRequest request) throws Exception
    {
        String batchDate = request.getSettleDate();
        if(IfspDataVerifyUtil.isBlank(batchDate))
        {
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
        }
        int count = this.getTotalResult(batchDate);
        log.info(">>>>>>>>>>>>>>>>>>>>>check succuss order-count of " + batchDate +" is " + count);

        //设置FixedThreadPool线程池线程数量为机器cpu数 * 4
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);

        List<Future<CommonResponse>> futures = new ArrayList<Future<CommonResponse>>();

        int pageCount = (int)Math.ceil((double) count / PAGE_SIZE);

    	/*for(BthChkRsltInfo chkSuccOrd : chkSuccList )
    	{
    		this.execute(chkSuccOrd);
    		//executor.submit()
    	}*/

        for(int pageIdx = 1 ; pageIdx <= pageCount; pageIdx ++ )
        {
            List<BthChkRsltInfo> chkSuccList = this.getDataList(batchDate,pageIdx);

            PreClearingTask preClearingTask = new PreClearingTask(chkSuccList);
            Future<CommonResponse> future = executor.submit(preClearingTask);
            //this.execute(chkSuccList);
            futures.add(future);
        }

        for(Future future : futures)
        {
            CommonResponse response = null;
            response = (CommonResponse) future.get();
            if (response.getRespCode().equals(RespConstans.RESP_FAIL.getCode()))
            {
                log.error(">>>>>>>>>>>>>>Exception occurred on core clearing>>>>>>>>");
                log.error(">>>>>>>>>>>>>>Excetion detail : ");
                log.error(response.getRespMsg());

                //response.setRespMsg(RespConstans.RESP_FAIL.getDesc());  //返回原始错误信息给切面

                executor.shutdownNow();
                return response;
            }
        }


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
    private int getTotalResult(String date)
    {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>income test.........");
        int count = 0;
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("chkSuccDt", date);
        count = bthChkRsltInfoDao.count("countChkSuccOrderByDate", m);
        return count;
    }
    /**
     * 抽取订单信息
     * @param date    对账成功日期
     * @param pageIdx  分页页码 ： 页码从1开始
     * @return
     */
    private List<BthChkRsltInfo> getDataList(String date,int pageIdx)
    {
        int startIdx = (pageIdx - 1) * PAGE_SIZE + 1;
        int endIdx = pageIdx * PAGE_SIZE;
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("chkSuccDt", date);
        params.put("stlmSt", Constans.SETTLE_STATUS_NOT_CLEARING);
        params.put("startIdx", startIdx);
        params.put("endIdx", endIdx);

        List<BthChkRsltInfo> orderList = bthChkRsltInfoDao.selectList("selectChkSuccOrderByDate", params);
        return orderList;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    private void execute(List<BthChkRsltInfo> chkSuccList)
    {
        //this.dataGathering(chkSuccOrd);
        //this.updateOrderStlStatus(chkSuccOrd);
        PreClearingTask preClearingTask = new PreClearingTask(chkSuccList);


    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public void dataGathering(List<BthChkRsltInfo> bthChkRsltInfoList){
        for(BthChkRsltInfo chkSuccOrd : bthChkRsltInfoList)
        {
            if(!Constans.SETTLE_STATUS_NOT_CLEARING.equals(chkSuccOrd.getStlmSt()))
            {
                //跳过已清分数据
                chkSuccOrd = null;
                continue;
            }
            dataGathering(chkSuccOrd);
            updateOrderStlStatus(chkSuccOrd);
        }
    }

    /**
     * 根据对账成功订单信息查询订单信息及商户信息
     */
    @Override
    //@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public void dataGathering(BthChkRsltInfo chkSuccOrd)
    {
        //1)查询订单信息
        //根据通道应答流水查询可能存在因日间记账超时，导致订单表中应答流水为空情况
        //PayOrderInfo orderInfo = payOrderInfoDao.selectByPagyTxnSsn(chkSuccOrd.getPagyTxnSsn());
        Map<String,Object> parameter = new HashMap<String,Object>();
        parameter.put("orderSsn", chkSuccOrd.getOrderSsn());
        PayOrderInfo orderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);

        //2)查询商户结算信息
        MchtBaseInfo merInfo  = this.getMerInfo(orderInfo.getMchtId());
        MchtContInfo merStlInfo = this.getMerStlInfo(orderInfo.getMchtId());

        //logic:
        //根据对账成功表的ORDER_SSN查询订单信息，根据交易码判断是线上交易还是线下交易
        //如果是线上交易，根据订单号查询所有的子订单信息（主订单成功，子订单默认支付成功，如失败通过异步补机制处理）
        if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(orderInfo.getTxnTypeNo()))
        {
            //线上交易处理
            //1)查询子订单信息
            Map<String,Object> m = new HashMap<String,Object>();
            m.put("orderSsn", orderInfo.getOrderSsn());
            List<PaySubOrderInfo> subOrderList = this.paySubOrderInfoDao.selectList("selectSubOrderByOrderId", m);
            if(subOrderList == null || subOrderList.size() == 0)
            {
                log.error(">>>>>>>>>>>>>>>线上支付找不到子订单！orderSsn = " + orderInfo.getOrderSsn());
            }
            else
            {
                //计算渠道手续费
                this.calcChnlFee4SubOrder(subOrderList, chkSuccOrd);
                //计算行内手续费
                for(PaySubOrderInfo subOrder : subOrderList)
                {
                    this.calMerFee4SubOrder(orderInfo, subOrder, chkSuccOrd);
                }

                //生成子订单入账明细信息
                for(PaySubOrderInfo subOrder : subOrderList)
                {
                    //查询子订单商户信息
                    merInfo  = this.getMerInfo(subOrder.getSubMchtId());
                    merStlInfo = this.getMerStlInfo(subOrder.getSubMchtId());
                    //2)根据子订单信息生成
                    BthMerInAccDtl orderStlInfo = this.buildOnlineOrderStlInfo(subOrder,orderInfo, chkSuccOrd, merInfo, merStlInfo);

                    try {
                        bthMerInAccDtlDao.insertSelective(orderStlInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("插入入账明细表异常");
                    }
                }
            }
        }
        else
        {
            //线下订单
            //根据订单信息生成清算信息
            BthMerInAccDtl orderStlInfo = this.buildSMOrderStlInfo(orderInfo, chkSuccOrd, merInfo, merStlInfo);
            try
            {
                bthMerInAccDtlDao.insertSelective(orderStlInfo);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException("插入入账明细表异常");

            }
        }
    }

    class PreClearingTask<T> implements Callable<T>
    {
        //List<String> page;  //分页处理，每个线程处理一页的数据
        List<BthChkRsltInfo> chkSuccList;
        public PreClearingTask(List<BthChkRsltInfo> chkSuccList)
        {
            this.chkSuccList = chkSuccList;
        }

        @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
        public void run()
        {
            for(BthChkRsltInfo chkSuccOrd : chkSuccList)
            {
                if(!Constans.SETTLE_STATUS_NOT_CLEARING.equals(chkSuccOrd.getStlmSt()))
                {
                    //跳过已清分数据
                    chkSuccOrd = null;
                    continue;
                }
                dataGathering(chkSuccOrd);
                updateOrderStlStatus(chkSuccOrd);
            }

        }

        @Override
        public T call() throws Exception {

            CommonResponse failResponse = new CommonResponse();
            try{
                this.run();
                failResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            }
            catch (Exception e)
            {
                //e.printStackTrace();
                failResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
                failResponse.setRespMsg(ExceptionUtils.getFullStackTrace(e));
            }
            finally {
            }

            return (T)failResponse;
        }
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
     * 根据商户号查询商户结算信息
     * @param mchtId
     * @return
     */
    private MchtContInfo getMerStlInfo(String mchtId)
    {
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("mchtId", mchtId);
        MchtContInfo merInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);

        return merInfo;
    }

    /**
     * 根据商户号、渠道号、交易账户类型查询手续费费率信息
     * @param chnlNo
     * @param merId
     * @param accType
     * @param prdId
     * @return
     */
    private MchtSettlRateCfg getMerStlRateInfo(String chnlNo,String merId,String accType,String prdId)
    {
        MchtSettlRateCfg merRateInfo= null;
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("mchtId", merId);
        m.put("accChnlNo", chnlNo);
        m.put("acctType", accType);
        m.put("prodId", prdId);
        List<MchtSettlRateCfg> merRateInfoList = mchtSettlRateCfgDao.selectList("selectMerStlRateInfoByChnlAndAccType", m);

        if(merRateInfoList.size() > 0)
        {
            merRateInfo = merRateInfoList.get(0);
        }

        return merRateInfo;
    }

    /**
     *
     * @param checkedOrder
     * @param merInfo
     * @return
     */
    private BthMerInAccDtl buildSMOrderStlInfo(PayOrderInfo orderInfo,BthChkRsltInfo checkedOrder,MchtBaseInfo merInfo,MchtContInfo merStlInfo)
    {
        //根据商户结算（合同）信息计算订单结算日期
        String stlDate = this.calcStlDate(merStlInfo);
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
        orderStlInfo.setInAcctDate(stlDate);          //清算日期
        orderStlInfo.setChlMerId(merInfo.getMchtId());    //商户号
        orderStlInfo.setChlMerName(merInfo.getMchtName());   //商户名称
        orderStlInfo.setTxnTm(orderInfo.getOrderTm());

        //商户为二级商户
        if(merInfo.getMchtId().length() > 15 && !IfspDataVerifyUtil.isBlank(merInfo.getParMchId()))
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
        if(Constans.MCHT_SETTLE_TYPE_0.equals(orderInfo.getMchtSettleType()))
        {
            //已实时结算,手续费未清分
            orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
        }
        else
        {
            //初始化结算状态
            orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_NOT_CLEARING);
        }

        orderStlInfo.setInAcctStat(Constans.IN_ACC_STAT_PRE);


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
        BigDecimal merFee = new BigDecimal(0);    //行内手续费
        BigDecimal stlAmt = new BigDecimal(0);    //结算金额
        BigDecimal bankCouponAmt = new BigDecimal(0);    //营销总金额


        if(orderInfo.getBankCouponAmt() != null)
        {
            bankCouponAmt = new BigDecimal(orderInfo.getBankCouponAmt());
            txnAmt = txnAmt.add(bankCouponAmt);
        }

        //计算佣金
        BigDecimal commissionAmt;
        if(IfspDataVerifyUtil.isBlank(merInfo.getParMchId()))
        {
            commissionAmt = new BigDecimal(0);
        }
        else
        {
            if(Constans.ORDER_TYPE_CONSUME.equals(orderStlInfo.getOrderType()))
            {
                commissionAmt = this.calcCommissionAmt(txnAmt, merInfo.getMchtId());      //修改佣金费率取值，取当前商户信息数据，而非父商户数据
            }
            else
            {
                //处理部分退款情况佣金返回
                commissionAmt = this.calcCommissionAmt4Return(orderInfo);
            }
        }

        if(Constans.ORDER_TYPE_CONSUME.equals(orderStlInfo.getOrderType()))
        {
            //计算手续费
            String proId;
            if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
            {
                proId = Constans.PRO_ID_ONLINE;
            }
            else
            {
                proId = Constans.PRO_ID_SM;
            }
            merFee = this.calcMerFee4Order(txnAmt,orderInfo.getMchtId(), orderInfo,orderStlInfo.getFundChannel(),proId);
        }
        else
        {
            //计算退款手续费
            merFee = this.calcMerFee4Order4Return(orderInfo);
        }

        //task 15047 佣金金额 + 手续费金额不能大于订单金额
        if(txnAmt.compareTo(merFee.add(commissionAmt)) == -1)
        {
            commissionAmt = txnAmt.subtract(merFee);
        }


        //交易金额=支付金额+红包抵扣金额？
        orderStlInfo.setTxnAmt(txnAmt.intValue() + "");
        merFee = merFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        stlAmt = txnAmt.subtract(merFee).subtract(commissionAmt);
        orderStlInfo.setSetlAmt(stlAmt.intValue() + "");
        orderStlInfo.setSetlFeeAmt(merFee.intValue()+"");       //行内手续费
        orderStlInfo.setCommissionAmt(commissionAmt.intValue()+"");
        orderStlInfo.setBankCouponAmt(bankCouponAmt.intValue()+"");

        //第三方手续费
        if(checkedOrder.getTpamTxnFeeAmt() == null)
        {
            orderStlInfo.setTramFeeAmt("0");
        }
        else
        {
            orderStlInfo.setTramFeeAmt(checkedOrder.getTpamTxnFeeAmt()+"");
        }

        orderStlInfo.setTxnSeqId(orderInfo.getOrderSsn());           //设置订单号
        orderStlInfo.setAgentName(orderInfo.getOrigOrderSsn());      //原交易订单号，针对退款交易有效
        orderStlInfo.setCreateDate(DateUtil.format(new Date(), "yyyyMMdd"));

        //设置品牌服务费
        if(!IfspDataVerifyUtil.isBlank(checkedOrder.getUnionBrandFee()))
        {
            orderStlInfo.setBrandFee(checkedOrder.getUnionBrandFee().intValue()+"");
        }


        //设置隔日间接结算信息
        //TODO:20181128版本-业务上暂不支持

        return orderStlInfo;
    }

    private BthMerInAccDtl buildOnlineOrderStlInfo(PaySubOrderInfo subOrderInfo,PayOrderInfo orderInfo,BthChkRsltInfo checkedOrder,
                                                   MchtBaseInfo merInfo,MchtContInfo merStlInfo)
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


        if(StringUtils.hasText(subOrderInfo.getMchtPayAmt()))
        {
            mchtMarketingAmt = new BigDecimal(subOrderInfo.getMchtPayAmt());
        }

        if(StringUtils.hasText(subOrderInfo.getBankPayAmt()))
        {
            bankMarketingAmt = new BigDecimal(subOrderInfo.getBankPayAmt());
        }

        if(StringUtils.hasText(subOrderInfo.getUserorgPayAmt()))
        {
            orgMarketingAmt = new BigDecimal(subOrderInfo.getUserorgPayAmt());
        }


        if(!IfspDataVerifyUtil.isBlank(subOrderInfo.getBankCouponAmt()))
        {
            hbAmt = new BigDecimal(subOrderInfo.getBankCouponAmt());
        }

        BigDecimal txnAmt = payAmt.add(hbAmt);   //BankCouponAmt(hbAmt)已经包括了红包+积分金额
        txnAmt = txnAmt.add(marketingAmt);       //营销金额

        BigDecimal merFee = new BigDecimal(subOrderInfo.getMerFee());    //行内手续费


        //计算佣金，物流费不收佣金
        if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
        {
            commissionAmt = this.calcCommissionAmt(payAmt.add(hbAmt).add(marketingAmt), merInfo.getMchtId());
        }
        else
        {
            commissionAmt = this.calcCommissionAmt4SubOrder4Return(subOrderInfo);
        }

        //task 15047 佣金金额 + 手续费金额不能大于订单金额
        if(txnAmt.compareTo(merFee.add(commissionAmt)) == -1)
        {
            commissionAmt = txnAmt.subtract(merFee);
        }

        stlAmt = payAmt.add(hbAmt).add(marketingAmt).subtract(merFee).subtract(commissionAmt);

        orderStlInfo.setCommissionAmt(commissionAmt.intValue()+"");       //佣金
        orderStlInfo.setSetlAmt(stlAmt.intValue()+"");                    //清算金额
        orderStlInfo.setSetlFeeAmt(subOrderInfo.getMerFee());             //清算手续费
        orderStlInfo.setTxnAmt(txnAmt.intValue()+"");
        orderStlInfo.setBankHbAmt(bankHbAmt.intValue()+"");         //机构红包
        orderStlInfo.setPlatHbAmt(platHbAmt.intValue()+"");         //平台红包
        orderStlInfo.setPointDedcutAmt(pointAmt.intValue() + "");

        //设置营销金额
        orderStlInfo.setMchtPayAmt(mchtMarketingAmt.intValue() + "");
        orderStlInfo.setBankPayAmt(bankMarketingAmt.intValue() + "");
        orderStlInfo.setUserorgPayAmt(orgMarketingAmt.intValue() + "");



        //第三方手续费
        if(checkedOrder.getTpamTxnFeeAmt() == null)
        {
            orderStlInfo.setTramFeeAmt("0");
        }
        else
        {
            orderStlInfo.setTramFeeAmt(checkedOrder.getTpamTxnFeeAmt()+"");
        }

        orderStlInfo.setTxnSeqId(subOrderInfo.getSubOrderSsn());           //子订单号
        orderStlInfo.setAgentName(subOrderInfo.getOriDetailsId());            //原交易订单号，针对退款交易有效
        orderStlInfo.setOrderType(subOrderInfo.getOrderType());           //设置订单类型
        orderStlInfo.setRefundAccType(orderInfo.getRefundAcctType());     //退款账户类型
        orderStlInfo.setFundChannel(orderInfo.getFundChannel());          //子订单的支付渠道取主订单中的FUND_CHANNEL值
        orderStlInfo.setTxnType(orderInfo.getTxnTypeNo());                //交易码
        if(Constans.LOAN_SYS_NO.equals(checkedOrder.getPagySysNo()) || Constans.LOAN_SYS_POINT.equals(checkedOrder.getPagySysNo()))
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

        if(Constans.MCHT_SETTLE_TYPE_0.equals(orderInfo.getMchtSettleType()))
        {
            //已实时结算,手续费未清分
            orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);
        }
        else
        {
            //初始化结算状态
            orderStlInfo.setStlStatus(Constans.SETTLE_STATUS_NOT_CLEARING);
        }

        if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
        {
            orderStlInfo.setOrderType(Constans.ORDER_TYPE_CONSUME);
        }
        else
        {
            orderStlInfo.setOrderType(Constans.ORDER_TYPE_RETURN);
            orderStlInfo.setRefundAccType(orderInfo.getRefundAcctType());
        }

        //设置品牌服务费
        if(!IfspDataVerifyUtil.isBlank(checkedOrder.getUnionBrandFee()))
        {
            orderStlInfo.setBrandFee(checkedOrder.getUnionBrandFee().intValue()+"");
        }

        //bug : 二级商户返佣给了平台商户 （商户为二级商户）
        if(merInfo.getMchtId().length() > 15 && !IfspDataVerifyUtil.isBlank(merInfo.getParMchId()))
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
     * 计算子订单的行内手续费(支付金额+红包金额+物流金额)*行内扣率
     * @param chkSuccOrd
     */
    public void calMerFee4SubOrder(PayOrderInfo orderInfo,PaySubOrderInfo subOrder,BthChkRsltInfo chkSuccOrd)
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
        subOrder.setLogisFee(logisFee.intValue()+"");
        subOrder.setLogisFeeAmt("0");
        //本行支付物流费不收手续费
        if(!(Constans.GAINS_CHANNEL_SXE.equals(subOrder.getFundChannel()) || Constans.GAINS_CHANNEL_SXK.equals(subOrder.getFundChannel()) ||
                Constans.GAINS_CHANNEL_LOAN.equals(subOrder.getFundChannel()) || Constans.GAINS_CHANNEL_POINT.equals(subOrder.getFundChannel())))
        {
            if(logisFee.compareTo(new BigDecimal(0)) == 1)
            {
                //txnAmt = txnAmt.add(logisFee);                      //物流费不是结算给商户，不能算作txnAmt

                //计算物流部分手续费,因为结算金额
                //计算手续费
                String proId;
                if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(orderInfo.getTxnTypeNo()))
                {
                    proId = Constans.PRO_ID_ONLINE;
                }
                else
                {
                    proId = Constans.PRO_ID_SM;
                }
                BigDecimal logisFeeAmt = this.calcMerFee4Order(logisFee, subOrder.getSubMchtId(), orderInfo,subOrder.getFundChannel(),proId);

                subOrder.setLogisFee(logisFee.intValue()+"");
                subOrder.setLogisFeeAmt(logisFeeAmt.intValue()+"");
            }
            else
            {
                subOrder.setLogisFee(logisFee.intValue()+"");
                subOrder.setLogisFeeAmt("0");
            }
        }


        String proId;
        if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(orderInfo.getTxnTypeNo()))
        {
            proId = Constans.PRO_ID_ONLINE;
        }
        else
        {
            proId = Constans.PRO_ID_SM;
        }

        if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()))
        {

            merFee = this.calcMerFee4Order(txnAmt, subOrder.getSubMchtId(), orderInfo,subOrder.getFundChannel(),proId);
        }
        else
        {
            merFee = this.calcMerFee4SubOrder4Return(subOrder,proId);
        }


        subOrder.setMerFee(merFee.intValue()+"");
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
     * 根据红包Json信息计算红包金额(还是直接从保存的红包金额总费用总获取)
     * @param subOrder
     * @return
     */
    private BigDecimal getHbAmt(PaySubOrderInfo subOrder)
    {
        BigDecimal hbAmt = new BigDecimal(0);
        if(subOrder.getBankCouponAmt() != null)
        {
            hbAmt = new BigDecimal(subOrder.getBankCouponAmt());
        }
        return hbAmt;
    }

    /**
     * 获得营销金额（银行营销 + 机构营销）
     * @param subOrder
     * @return
     */
    private BigDecimal getMarketingAmt(PaySubOrderInfo subOrder)
    {
        BigDecimal marketingAmt = new BigDecimal(0);
        if(StringUtils.hasText(subOrder.getBankPayAmt()))
        {
            marketingAmt = marketingAmt.add(new BigDecimal(subOrder.getBankPayAmt()));
        }

        if(StringUtils.hasText(subOrder.getUserorgPayAmt()))
        {
            marketingAmt = marketingAmt.add(new BigDecimal(subOrder.getUserorgPayAmt()));
        }

        return marketingAmt;
    }

    /**
     * 根据红包Json信息计算指定优惠类型金额
     * @param subOrder ： 子订单信息
     * @param couponType ：
     * @return
     */
    private BigDecimal getCouponAmt(PaySubOrderInfo subOrder,String couponType)
    {
        String couponDesc = subOrder.getCouopnDesc();
        String couponAmtStr = "0";
        if(couponDesc != null)
        {
            log.info(">>>>>>>>>>>>>>>>>>>coupon desc" + couponDesc);
            //couponDesc = couponDesc.replaceAll("\\[", "").replaceAll("\\]", "");
            if(!"".equals(couponDesc))
            {
                //Map couponMap = IfspFastJsonUtil.jsonTOmap(couponDesc);
                List<Map> couponList = IfspFastJsonUtil.jsonTOlist(couponDesc);
                for(Map m : couponList)
                {
                    if(couponType.equals(m.get("consumeType")+""))
                    {
                        if(m.get("consumeAmt") != null && !"".equals(m.get("consumeAmt")))
                        {
                            couponAmtStr = m.get("consumeAmt")+"";
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
     * @param subOrder
     * @return
     */
    private BigDecimal getLogisAmt(PaySubOrderInfo subOrder)
    {
        BigDecimal logisAmt = new BigDecimal(0);

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("subOrderSsn", subOrder.getSubOrderSsn());
        List<OrderTplInfo> logisList = orderTplInfoDao.selectList("selectOrderTplInfoBySubOrderId", params);
        if(logisList.size() > 0)
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
     * @param txnAmt ： 交易金额
     * @param parentMerId ： 父商户ID
     * @return
     */
    private BigDecimal calcCommissionAmt(BigDecimal txnAmt,String parentMerId)
    {
        BigDecimal commissionAmt = new BigDecimal(0);
        if(parentMerId == null || parentMerId == "")
        {
            return commissionAmt;
        }
        //查询上级商户信息
        MchtContInfo parentMerStlInfo = this.getMerStlInfo(parentMerId);
        if(parentMerStlInfo != null)
        {
            String commType = parentMerStlInfo.getCommType();
            if(Constans.COMM_TYPE_NONE.equals(commType))
            {
                //无返佣
                return commissionAmt;
            }
            else if(Constans.COMM_TYPE_FIX_AMT.equals(commType))
            {
                //按固定金额返佣
                if(parentMerStlInfo.getCommParam()!=null)
                {
                    commissionAmt = parentMerStlInfo.getCommParam().multiply(ONE_HUNDRED);   //按固定金额收取佣金是，参数以元为单位
                }
            }
            else if(Constans.COMM_TYPE_BY_RATE.equals(commType))
            {
                //按比例返佣
                if(parentMerStlInfo.getCommParam()!=null)
                {
                    commissionAmt = parentMerStlInfo.getCommParam().multiply(txnAmt).divide(ONE_HUNDRED);   //按百分比收取佣金是，参数为百分比
                }
            }

        }

        //四舍五入，取整
        commissionAmt = commissionAmt.setScale(0, BigDecimal.ROUND_HALF_UP);

        return commissionAmt;
    }


    /**
     * 计算退款订单佣金
     * @param orderInfo ： 退款订单
     * @return
     */
    private BigDecimal calcCommissionAmt4Return(PayOrderInfo orderInfo)
    {
        //1)计算原交易佣金
        BigDecimal oriCommFee = new BigDecimal(0);
        Map<String,Object> parameter = new HashMap<String,Object>();
        parameter.put("orderSsn", orderInfo.getOrigOrderSsn());
        PayOrderInfo oriOrderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);
        if(IfspDataVerifyUtil.isBlank(oriOrderInfo.getBankCouponAmt()))
        {
            oriOrderInfo.setBankCouponAmt("0");
        }

        BigDecimal txnAmt = new BigDecimal(oriOrderInfo.getPayAmt());
        if(StringUtils.hasText(oriOrderInfo.getBankCouponAmt()))
        {
            txnAmt = txnAmt.add(new BigDecimal(oriOrderInfo.getBankCouponAmt()));
        }

        oriCommFee = this.calcCommissionAmt(txnAmt, oriOrderInfo.getMchtId());

        //2)计算已退款佣金
        BigDecimal returnedFee = new BigDecimal(0);
        BigDecimal returnedAmt = new BigDecimal(0);
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("oriOrderSsn", orderInfo.getOrigOrderSsn());
        List<BthMerInAccDtl> returnedList = bthMerInAccDtlDao.selectList("selectRetOrderInfoByOriOrderSsn", m);
        for(BthMerInAccDtl dtl : returnedList)
        {
            returnedFee = returnedFee.add(new BigDecimal(dtl.getCommissionAmt()));  //累加已退手续费
            returnedAmt = returnedAmt.add(new BigDecimal(dtl.getTxnAmt()));         //累加已退金额
        }

        //3)按比例计算当前退款订单应退手续费
        if(IfspDataVerifyUtil.isBlank(orderInfo.getBankCouponAmt()))
        {
            orderInfo.setBankCouponAmt("0");
        }
        BigDecimal retAmt = new BigDecimal(orderInfo.getPayAmt()).add(new BigDecimal(orderInfo.getBankCouponAmt()));
        BigDecimal retFee = this.calcReturnFee(txnAmt, retAmt, oriCommFee);


        //4)计算实际退款手续费
        if(retFee.compareTo(oriCommFee.subtract(returnedFee)) == 1)
        {
            retFee = oriCommFee.subtract(returnedFee);
        }

        if(txnAmt.compareTo(returnedAmt.add(retAmt)) ==0)    //已退完
        {
            if(retFee.compareTo(oriCommFee.subtract(returnedFee)) != 0)
            {
                retFee = oriCommFee.subtract(returnedFee);
            }
        }

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        return retFee;
    }


    /**
     * 计算退款订单佣金
     * @return
     */
    private BigDecimal calcCommissionAmt4SubOrder4Return(PaySubOrderInfo subOrderInfo)
    {
        //1)计算原交易佣金
        BigDecimal oriCommFee = new BigDecimal(0);
        Map<String,Object> parameter = new HashMap<String,Object>();
        parameter.put("subOrderSsn", subOrderInfo.getOriDetailsId());
        PaySubOrderInfo oriSubOrderInfo = paySubOrderInfoDao.selectOne("selectSubOrderInfoBySubOrderNo", parameter);

        if(IfspDataVerifyUtil.isBlank(oriSubOrderInfo.getBankCouponAmt()))
        {
            oriSubOrderInfo.setBankCouponAmt("0");
        }

        BigDecimal txnAmt = new BigDecimal(oriSubOrderInfo.getPayAmount()).add(this.getMarketingAmt(oriSubOrderInfo));  //支付 + 营销
        if(StringUtils.hasText(oriSubOrderInfo.getBankCouponAmt()))
        {
            txnAmt = txnAmt.add(new BigDecimal(oriSubOrderInfo.getBankCouponAmt()));    // + 红包（包括积分抵扣）
        }

        //原订单佣金
        oriCommFee = this.calcCommissionAmt(txnAmt, oriSubOrderInfo.getSubMchtId());

        //2)计算已退款佣金
        BigDecimal returnedFee = new BigDecimal(0);
        BigDecimal returnedAmt = new BigDecimal(0);
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("oriOrderSsn", subOrderInfo.getOriDetailsId());
        List<BthMerInAccDtl> returnedList = bthMerInAccDtlDao.selectList("selectRetOrderInfoByOriOrderSsn", m);

        for(BthMerInAccDtl dtl : returnedList)
        {
            returnedFee = returnedFee.add(new BigDecimal(dtl.getCommissionAmt()));  //累加已退手续费
            returnedAmt = returnedAmt.add(new BigDecimal(dtl.getTxnAmt()));         //累加已退金额
        }

        //3)按比例计算当前退款订单应退手续费
        if(IfspDataVerifyUtil.isBlank(subOrderInfo.getBankCouponAmt()))
        {
            subOrderInfo.setBankCouponAmt("0");
        }
        BigDecimal retAmt = new BigDecimal(subOrderInfo.getPayAmount()).add(new BigDecimal(subOrderInfo.getBankCouponAmt())).
                add(this.getMarketingAmt(subOrderInfo));

        BigDecimal retFee = this.calcReturnFee(txnAmt, retAmt, oriCommFee);


        //4)计算实际退款佣金
        if(retFee.compareTo(oriCommFee.subtract(returnedFee)) == 1)
        {
            retFee = oriCommFee.subtract(returnedFee);
        }

        if(txnAmt.compareTo(returnedAmt.add(retAmt)) ==0)    //已退完
        {
            if(retFee.compareTo(oriCommFee.subtract(returnedFee)) != 0)
            {
                retFee = oriCommFee.subtract(returnedFee);
            }
        }

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        return retFee;
    }

    /**
     * 根据交易金额计算行内手续费
     * @param txnAmt ： 交易金额
     * @param prdId ： pro1 线上，pro2线上
     * @return
     */
    private BigDecimal calcMerFee4Order(BigDecimal txnAmt,String merId, PayOrderInfo orderInfo,String chnlNo,String prdId)
    {
        BigDecimal merFee = new BigDecimal(0);

        String accType = orderInfo.getAcctSubTypeId();
		/*if(accType == null )          //内管手续费配置规则修改 ： 20181105
		{
			accType = "*";
		}*/
        MchtSettlRateCfg stlRateInfo = this.getMerStlRateInfo(chnlNo, merId, accType,prdId);
        if(stlRateInfo == null)
        {
            //没配置费率信息,不收手续费
            return merFee;
        }

        String rateCalcType = stlRateInfo.getRateCalType();
        if(Constans.COMM_TYPE_FIX_AMT.equals(rateCalcType))
        {
            //按固定金额返佣
            if(stlRateInfo.getRateCalParam()!=null)
            {
                merFee = stlRateInfo.getRateCalParam().multiply(ONE_HUNDRED);   //参数单位为元
                if (txnAmt.compareTo(merFee) < 0){
                    log.info("商户["+merId+"]行内手续费["+merFee+"]大于支付金额["+txnAmt+"] , 手续费金额最多只收取订单金额, 即为 ["+txnAmt+"]分 .");
                    merFee = txnAmt;
                }
            }
        }
        else if(Constans.COMM_TYPE_BY_RATE.equals(rateCalcType))
        {
            //按比例返佣
            if(stlRateInfo.getRateCalParam()!=null)
            {
                merFee = stlRateInfo.getRateCalParam().multiply(txnAmt).divide(ONE_HUNDRED);;
                if(stlRateInfo.getMaxParam() != null)
                {
                    //大于最大手续费
                    if(merFee.compareTo(stlRateInfo.getMaxParam().multiply(ONE_HUNDRED)) == 1)
                    {
                        merFee = stlRateInfo.getMaxParam().multiply(ONE_HUNDRED);
                    }
                }

                if(stlRateInfo.getMinParam() != null)
                {
                    //小于最小手续费
                    if(merFee.compareTo(stlRateInfo.getMinParam().multiply(ONE_HUNDRED)) == -1)
                    {
                        merFee = stlRateInfo.getMinParam().multiply(ONE_HUNDRED);
                    }
                }

                //手续费不能大于交易金额
                if (txnAmt.compareTo(merFee) < 0){
                    log.info("商户["+merId+"]行内手续费["+merFee+"]大于支付金额["+txnAmt+"] , 手续费金额最多只收取订单金额, 即为 ["+txnAmt+"]分 .");
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
        Map<String,Object> parameter = new HashMap<String,Object>();
        parameter.put("orderSsn", orderInfo.getOrigOrderSsn());
        PayOrderInfo oriOrderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);

        if(IfspDataVerifyUtil.isBlank(oriOrderInfo.getBankCouponAmt()))
        {
            oriOrderInfo.setBankCouponAmt("0");
        }

        BigDecimal txnAmt = new BigDecimal(oriOrderInfo.getPayAmt()).add(new BigDecimal(oriOrderInfo.getBankCouponAmt()));
        String chnlNo;
        if(Constans.TXN_TYPE_O1000002.equals(oriOrderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_O1000003.equals(oriOrderInfo.getTxnTypeNo()))
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
        if(Constans.TXN_TYPE_ONLINE_PAY.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_ONLINE_REFUND.equals(orderInfo.getTxnTypeNo()))
        {
            proId = Constans.PRO_ID_ONLINE;
        }
        else
        {
            proId = Constans.PRO_ID_SM;
        }

        oriMerFee = this.calcMerFee4Order(txnAmt, oriOrderInfo.getMchtId(), oriOrderInfo, chnlNo,proId);

        //2)计算已退款手续费
        BigDecimal returnedFee = new BigDecimal(0);
        BigDecimal returnedAmt = new BigDecimal(0);
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("oriOrderSsn", orderInfo.getOrigOrderSsn());
        List<BthMerInAccDtl> returnedList = bthMerInAccDtlDao.selectList("selectRetOrderInfoByOriOrderSsn", m);
        for(BthMerInAccDtl dtl : returnedList)
        {
            returnedFee = returnedFee.add(new BigDecimal(dtl.getSetlFeeAmt()));  //累加已退手续费
            returnedAmt = returnedAmt.add(new BigDecimal(dtl.getTxnAmt()));         //累加已退金额
        }

        //3)按比例计算当前退款订单应退手续费
        if(IfspDataVerifyUtil.isBlank(orderInfo.getBankCouponAmt()))
        {
            orderInfo.setBankCouponAmt("0");
        }
        BigDecimal retAmt = new BigDecimal(orderInfo.getPayAmt()).add(new BigDecimal(orderInfo.getBankCouponAmt()));
        BigDecimal retFee = this.calcReturnFee(txnAmt, retAmt, oriMerFee);


        //4)计算实际退款手续费
        if(retFee.compareTo(oriMerFee.subtract(returnedFee)) == 1)
        {
            retFee = oriMerFee.subtract(returnedFee);
        }

        if(txnAmt.compareTo(returnedAmt.add(retAmt)) ==0)    //已退完
        {
            if(retFee.compareTo(oriMerFee.subtract(returnedFee)) != 0)
            {
                retFee = oriMerFee.subtract(returnedFee);
            }
        }

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        return retFee;
    }


    /**
     * 退款订单号
     * @param subOrderInfo
     * @return
     */
    private BigDecimal calcMerFee4SubOrder4Return(PaySubOrderInfo subOrderInfo,String prdId)
    {
        //1)计算原交易手续费
        BigDecimal oriMerFee = new BigDecimal(0);
        Map<String,Object> parameter = new HashMap<String,Object>();
        parameter.put("subOrderSsn", subOrderInfo.getOriDetailsId());

        //查询原交易信息
        PaySubOrderInfo oriSubOrderInfo = paySubOrderInfoDao.selectOne("selectSubOrderInfoBySubOrderNo", parameter);

        PayOrderInfo oriOrderInfo = payOrderInfoDao.selectOne("selectOrderInfoBySubOrderSsn", parameter);

        //统计原子订单总金额
        if(IfspDataVerifyUtil.isBlank(oriSubOrderInfo.getBankCouponAmt()))
        {
            oriSubOrderInfo.setBankCouponAmt("0");
        }

        //原交易金额
        BigDecimal txnAmt = new BigDecimal(oriSubOrderInfo.getPayAmount()).add(new BigDecimal(oriSubOrderInfo.getBankCouponAmt())).
                add(this.getMarketingAmt(oriSubOrderInfo));

        oriMerFee = this.calcMerFee4Order(txnAmt, oriSubOrderInfo.getSubMchtId(), oriOrderInfo, oriSubOrderInfo.getFundChannel(),prdId);

        //2)计算已退款手续费
        BigDecimal returnedFee = new BigDecimal(0);
        BigDecimal returnedAmt = new BigDecimal(0);

        Map<String,Object> m = new HashMap<String,Object>();
        m.put("oriOrderSsn", subOrderInfo.getOriDetailsId());
        List<BthMerInAccDtl> returnedList = bthMerInAccDtlDao.selectList("selectRetOrderInfoByOriOrderSsn", m);

        for(BthMerInAccDtl dtl : returnedList)
        {
            returnedFee = returnedFee.add(new BigDecimal(dtl.getSetlFeeAmt()));     //累加已退手续费
            returnedAmt = returnedAmt.add(new BigDecimal(dtl.getTxnAmt()));         //累加已退金额
        }

        //3)按比例计算当前退款订单应退手续费
        if(IfspDataVerifyUtil.isBlank(subOrderInfo.getBankCouponAmt()))
        {
            subOrderInfo.setBankCouponAmt("0");
        }

        //当前退款订单交易金额
        BigDecimal retAmt = new BigDecimal(subOrderInfo.getPayAmount()).add(new BigDecimal(subOrderInfo.getBankCouponAmt())).
                add(this.getMarketingAmt(oriSubOrderInfo));
        BigDecimal retFee = this.calcReturnFee(txnAmt, retAmt, oriMerFee);


        //4)计算实际退款手续费
        if(retFee.compareTo(oriMerFee.subtract(returnedFee)) == 1)
        {
            retFee = oriMerFee.subtract(returnedFee);
        }

        if(txnAmt.compareTo(returnedAmt.add(retAmt)) ==0)    //已退完
        {
            if(retFee.compareTo(oriMerFee.subtract(returnedFee)) != 0)
            {
                retFee = oriMerFee.subtract(returnedFee);
            }
        }

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);

        return retFee;
    }


    /**
     * 根据退款金额计算应退手续费
     * @param txnAmt ： 原交易金额
     * @param retAmt ： 当前退款金额
     * @param oriFee ： 原手续费金额
     * @return
     */
    private BigDecimal calcReturnFee(BigDecimal txnAmt,BigDecimal retAmt,BigDecimal oriFee)
    {
        BigDecimal retFee = new BigDecimal(0);

        retFee = oriFee.multiply(retAmt.divide(txnAmt,3,BigDecimal.ROUND_HALF_UP));

        retFee = retFee.setScale(0, BigDecimal.ROUND_HALF_UP);

        return retFee;
    }

    public int updateOrderStlStatus(BthChkRsltInfo chkedOrder)
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("orderSsn", chkedOrder.getOrderSsn());
        params.put("stlmSt", Constans.SETTLE_STATUS_CLEARING);    //01-清分中
        return bthChkRsltInfoDao.update("updateChkOrderStlStatusByOrderId", params);
    }

}
