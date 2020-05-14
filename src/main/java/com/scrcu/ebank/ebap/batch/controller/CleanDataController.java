package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.service.CleanDataService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class CleanDataController {
    @Resource
    private CleanDataService cleanDataService;

    @SOA("001.cleanData")
    @Explain(name = "每日增量数据清理", logLv = LogLevel.DEBUG)
    public CommonResponse cleanData() throws Exception {
        CommonResponse commonResponse  = cleanDataService.cleanData();
        return commonResponse;
    }
}
