package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.InAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.response.InAcctResponse;
import com.scrcu.ebank.ebap.batch.service.BthMerInAccDtlServer;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class BthMerInAccDtlController {
    @Resource
    private BthMerInAccDtlServer bthMerInAccDtlServer;


    @SOA("001.getInAccTime")
    @Explain(name = "获取订单实际入账时间", logLv = LogLevel.DEBUG)
    public InAcctResponse getInAccTime(InAcctRequest request){
        InAcctResponse response =bthMerInAccDtlServer.selectUpdateDate(request);
        return response;
    }
}
