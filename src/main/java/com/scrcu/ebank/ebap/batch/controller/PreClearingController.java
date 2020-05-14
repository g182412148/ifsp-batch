package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.service.PreClearingServiceRun;
import com.scrcu.ebank.ebap.batch.service.PreClearingUpdDataService;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.PreClearingService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;

/**
 * 清分数据准备Controller
 * @author ydl
 *
 */
@Controller
public class PreClearingController {
	
	@Resource
    private PreClearingService preClearingService;

    @Resource
    private PreClearingServiceRun preClearingServiceRun;

    @Resource
    private PreClearingUpdDataService preClearingUpdDataService;

    @SOA("001.preOrderClearing")
    @Explain(name = "清分数据抽取", logLv = LogLevel.DEBUG)
    public CommonResponse coreOrderClearing(BatchRequest request) throws Exception {
        return preClearingServiceRun.prepare(request);
    }

//    @SOA("001.preOrderClearingUpd ")
//    @Explain(name = "清分数据抽取更新", logLv = LogLevel.DEBUG)
//    public CommonResponse coreOrderClearingUpd(BatchRequest request) throws Exception {
//        return preClearingUpdDataService.prepareUpd(request);
//    }
    
}
