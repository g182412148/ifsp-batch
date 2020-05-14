package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.ExtensionContractService;
import com.scrcu.ebank.ebap.batch.service.GuaranteeDepositService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class GuaranteeDeposit {

    @Resource
    private GuaranteeDepositService guaranteeDepositService;

    @SOA("001.guaranteeDeposit")
    @Explain(name = "补充保证金", logLv = LogLevel.DEBUG)
    public CommonResponse guaranteeDeposit(@IfspValid BatchRequest request) throws Exception
    {

        return guaranteeDepositService.guaDep(request);
    }
}
