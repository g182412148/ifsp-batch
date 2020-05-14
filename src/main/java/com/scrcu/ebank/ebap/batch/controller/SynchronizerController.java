package com.scrcu.ebank.ebap.batch.controller;


import com.scrcu.ebank.ebap.batch.service.SynchronizeAccService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

/**
 * 数据同步器
 * 1、同步订单中心审核后的结算账户到清算中心
 */
@Controller
public class SynchronizerController {

    @Autowired
    private SynchronizeAccService synchronizeAccService;


    @SOA("001.synchronizeSettleAcc")
    @Explain(name = "结算账户信息同步", logLv = LogLevel.DEBUG)
    public CommonResponse synchronizeSettleAcc() throws Exception {
        return synchronizeAccService.synchronizeSettleAcc();
    }
}
