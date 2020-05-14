package com.scrcu.ebank.ebap.batch.controller;


import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.service.ClearingService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;

/**
 * 结算
 * @author lenovo
 *
 */
@Controller
public class ClearingController {
	
	@Resource
    private ClearingService clearingService;

    
    @SOA("699.callCH1730")
    @Explain(name = "调用CH1730接口", logLv = LogLevel.DEBUG)
    public CommonResponse callCH1730() throws Exception {
        return clearingService.callCH1730();
    }
    
    @SOA("699.updateStat")
    @Explain(name = "修改本行或他行状态", logLv = LogLevel.DEBUG)
    public CommonResponse updateStat() throws Exception {
        return clearingService.updateStat();
    }
    
    
    
}
