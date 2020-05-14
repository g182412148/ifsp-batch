package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.ExtensionContractService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class ExtensionContractController {

    @Resource
    private ExtensionContractService extensionContractService;

    @SOA("001.extensionContract")
    @Explain(name = "延长商户合同时间", logLv = LogLevel.DEBUG)
    public CommonResponse extensionContract(@IfspValid BatchRequest request) throws Exception
    {

        return extensionContractService.extCon(request);
    }
}
