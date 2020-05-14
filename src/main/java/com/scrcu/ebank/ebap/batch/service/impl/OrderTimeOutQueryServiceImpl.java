package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.QueryTimeOutQueryRequest;
import com.scrcu.ebank.ebap.batch.common.dict.ChlNo;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.OrderTimeOutQueryService;
import com.scrcu.ebank.ebap.batch.soaclient.TxnQueryDetailService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderTimeOutQueryServiceImpl implements OrderTimeOutQueryService {

    @Resource
    private PayOrderInfoDao payOrderInfoDao;

    @Value("${ord.queryPeriod}")
    private Integer queryPeriod;

    @Value("${ord.queryCapacity}")
    private Integer queryCapacity;

    @Resource
    private TxnQueryDetailService txnQueryDetailService;

    @Override
    public CommonResponse queryTimeOutOrder(QueryTimeOutQueryRequest request) {
        //1、计算查询时间，开始时间点，截止时间点
        Date endDate = new DateTime(new Date()).minusMinutes(queryPeriod).toDate();
        Date startDate = new DateTime(endDate).minusDays(1).toDate();
        if(IfspDataVerifyUtil.isNotBlank(request.getTxnStartTime())){
            startDate = DateUtil.parse(request.getTxnStartTime(),"yyyyMMddHHmmss");
        }
        if(IfspDataVerifyUtil.isNotBlank(request.getTxnEndTime())){
            endDate = DateUtil.parse(request.getTxnEndTime(),"yyyyMMddHHmmss");
        }

        //2、查询queryCapacity条所有符合条件的订单，得到orderList
        List<PayOrderInfo> orderInfoList = payOrderInfoDao.queryTimeOutOrder(startDate, endDate,0,queryCapacity);

        //3、遍历orderList调用订单系统交易查询接口
        if(IfspDataVerifyUtil.isNotEmptyList(orderInfoList)){
            //3.1、遍历orderList
            log.info("==查询超时订单数量:" + orderInfoList.size());
            for(PayOrderInfo orderInfo:orderInfoList){
                //3.2、调用订单系统交易查询接口
                log.info("==查证订单:" + orderInfo.getOrderSsn() + "(start)==");
                Map<Object, Object> paramMap = createQueryReq(orderInfo);
                SoaParams soaParams = new SoaParams();
                soaParams.setDatas(paramMap);
                SoaResults soaResults = txnQueryDetailService.txnQueryDetail(soaParams);
                log.info("查证结果:"+soaResults.getRespCode());
                log.info("==查证订单:" + orderInfo.getOrderSsn() + "(end)==");
            }
        }

        return new CommonResponse();
    }

    private Map<Object, Object> createQueryReq(PayOrderInfo orderInfo){
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("mchtNo",orderInfo.getMchtId());
        paramMap.put("userId","batch");//标识是批量系统发的请求
        paramMap.put("origRespSsn",orderInfo.getOrderSsn());
        paramMap.put("chlNo", ChlNo.HUIPAY.getCode());
        paramMap.put("txnTypeNo", "O1300005");
        return paramMap;
    }

}
