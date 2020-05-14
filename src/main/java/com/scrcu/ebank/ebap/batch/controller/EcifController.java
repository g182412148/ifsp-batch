package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.EcifService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;


/**
 * 描述 </br>
 *
 * @author M.chen
 * 2019/6/13 14:25
 */
@Controller
public class EcifController {

    @Resource
    private EcifService ecifService;

    @SOA("ecifUpdateMchtInfo")
    @Explain(name = "ecif同步商户信息任务", logLv = LogLevel.DEBUG)
    public CommonResponse ecifUpdateMchtInfo(@IfspValid BatchRequest request){
        return ecifService.ecifUpdateMchtInfo(request);
    }
}
