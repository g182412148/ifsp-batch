package com.scrcu.ebank.ebap.batch.controller;


import com.scrcu.ebank.ebap.batch.bean.request.QueryTimeOutQueryRequest;
import com.scrcu.ebank.ebap.batch.service.OrderTimeOutQueryService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
@Slf4j
public class OrderTimeOutQueryController {

    @Resource
    private OrderTimeOutQueryService orderTimeOutQueryService;

    @SOA("009.queryTimeOut")
    @Explain(name = "查询处理中订单", logLv = LogLevel.DEBUG)
    public CommonResponse queryTimeOutOrder(QueryTimeOutQueryRequest request){
        CommonResponse response = orderTimeOutQueryService.queryTimeOutOrder(request);
        return response;
    }
}
