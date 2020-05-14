package com.scrcu.ebank.ebap.batch.service.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtSettlRateCfg;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetPagyTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.GenTestDataService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class GenTestDataServiceImpl implements GenTestDataService {

    private static  Long index = System.currentTimeMillis();

    private static String mchtId;

    private static final int COUNT = 200000;

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



    @Override
    public CommonResponse genWxTestData(GetPagyTxnInfoRequest request) {
        return null;
    }

    @Override
    public CommonResponse genAliTestData(GetPagyTxnInfoRequest request) {
        return null;
    }

    @Override
    public CommonResponse genUnionQrcTestData(GetPagyTxnInfoRequest request) {
        return null;
    }

    @Override
    public CommonResponse genUnionAllChnlTestData(GetPagyTxnInfoRequest request) {
        return null;
    }

    @Override
    public CommonResponse genSxkTestData(GetPagyTxnInfoRequest request) throws InterruptedException {
        return null;
    }

    @Override
    public CommonResponse genSxeTestData(GetPagyTxnInfoRequest request) throws ExecutionException, InterruptedException {
        return null;
    }

    /**
     * 授信支付测试数据生成
     * @param request
     * @return
     */
    @Override
    public CommonResponse genLoanpayTestData(GetPagyTxnInfoRequest request) {


        for(int i=0; i < COUNT ; i++)
        {
            SecureRandom random = new SecureRandom();
            String orderSsn = this.genOrderSssn();
            String payAmt = "" + random.nextInt() * 10;
            PayOrderInfo orderInfo = this.orderInit(orderSsn,true, Constans.GAINS_CHANNEL_LOAN,payAmt);
            PaySubOrderInfo subOrderInfo = this.subOrderInit(orderSsn,Constans.GAINS_CHANNEL_LOAN,payAmt);

            this.payOrderInfoDao.insertSelective(orderInfo);
            this.paySubOrderInfoDao.insertSelective(subOrderInfo);

        }

        CommonResponse response = new CommonResponse();
        response.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        response.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        return response;
    }

    /**
     * 积分支付测试数据生成
     * @param request
     * @return
     */
    @Override
    public CommonResponse genPointpayTestData(GetPagyTxnInfoRequest request) {

        for(int i=0; i < COUNT ; i++)
        {
            SecureRandom random = new SecureRandom();
            String orderSsn = this.genOrderSssn();
            String payAmt = "" + random.nextInt() * 10;
            PayOrderInfo orderInfo = this.orderInit(orderSsn,true, Constans.GAINS_CHANNEL_POINT,payAmt);
            PaySubOrderInfo subOrderInfo = this.subOrderInit(orderSsn,Constans.GAINS_CHANNEL_POINT,payAmt);

            this.payOrderInfoDao.insertSelective(orderInfo);
            this.paySubOrderInfoDao.insertSelective(subOrderInfo);

        }


        CommonResponse response = new CommonResponse();
        response.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        response.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        return response;
    }

    /**
     * 初始化订单信息
     * @param orderSsn ： 订单号
     * @param onlineFlag ： 是否为线上订单
     * @param fundChnl ： 支付渠道
     * @param payAmt ： 支付金额
     */
    private PayOrderInfo orderInit(String orderSsn,boolean onlineFlag,String fundChnl,String payAmt)
    {
        this.getMchtId();

        PayOrderInfo orderInfo = new PayOrderInfo();
        orderInfo.setOrderSsn(orderSsn);
        if(onlineFlag)
        {
            orderInfo.setTxnTypeNo("O1000006");
            orderInfo.setSubOrderFlag("0");
            orderInfo.setTplFlag("0");
        }
        else
        {
            orderInfo.setTxnTypeNo("O1000001");
            orderInfo.setSubOrderFlag("1");
            orderInfo.setTplFlag("1");
        }
        orderInfo.setTxnAmt(payAmt);
        orderInfo.setPayAmt(payAmt);
        orderInfo.setOrderTm(new Date());
        orderInfo.setCrtTm(new Date());
        orderInfo.setOrderType("0");
        orderInfo.setAcptChlNo(fundChnl);
        orderInfo.setChlNo(fundChnl);
        orderInfo.setFundChannel(fundChnl);

        if("10".equals(fundChnl))
        {
            orderInfo.setOlOrderType("23");
        }

        orderInfo.setTxnRfdFlag("0");
        orderInfo.setOrderState("00");

        orderInfo.setMchtId(mchtId);

        return orderInfo;
    }

    /**
     * 初始化子订单新
     * @param orderSsn ： 订单号
     * @param fundChnl ： 支付渠道
     * @param payAmt ： 支付金额
     */
    private PaySubOrderInfo subOrderInit(String orderSsn,String fundChnl,String payAmt)
    {
        String dateStr = DateUtil.format(new Date(),"yyyyMMdd");
        PaySubOrderInfo subOrderInfo = new PaySubOrderInfo();

        subOrderInfo.setOrderSsn(orderSsn);
        subOrderInfo.setSubOrderSsn(orderSsn+"S0001");
        subOrderInfo.setFundChannel(fundChnl);
        subOrderInfo.setPayAmount(payAmt);
        subOrderInfo.setSubOrderAmt(payAmt);
        subOrderInfo.setTransFactTime(dateStr);

        subOrderInfo.setCrtTm(dateStr);
        subOrderInfo.setSubOrderTm(dateStr);
        subOrderInfo.setReqSubOrderTm(dateStr);
        subOrderInfo.setAutoSettleFlag("2");
        subOrderInfo.setNoticeTime(dateStr);
        subOrderInfo.setOrderStatus("00");

        subOrderInfo.setCouopnDesc("[{\"consumeAmt\":100,\"consumePoint\":\"500\",\"consumeType\":\"06\"}]");
        subOrderInfo.setReqSubOrderSsn(orderSsn);
        subOrderInfo.setBankCouponAmt("0");
        subOrderInfo.setBankPayAmt("0");

        subOrderInfo.setSubMchtId(mchtId);

        return subOrderInfo;
    }

    private String genOrderSssn()
    {
        String dateStr = DateUtil.format(new Date(),"yyyyMMdd");
        StringBuffer orderSsn = new StringBuffer();
        orderSsn.append("T").append(dateStr).append(index++);

        return orderSsn.toString();
    }

    private void getMchtId()
    {
        List<MchtSettlRateCfg> mchtList = mchtSettlRateCfgDao.selectList("selectMchtId4Pet",null);
        mchtId = mchtList.get(0).getMchtId();
    }
}
