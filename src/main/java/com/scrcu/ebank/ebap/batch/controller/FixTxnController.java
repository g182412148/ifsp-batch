package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.FillUnionTxnService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.text.ParseException;

/**
 * 银联补单任务
 * @author ljy
 */
@Controller
public class FixTxnController {

    @Resource
    private FillUnionTxnService fillUnionTxnService;


//  TODO   @SOA("fillUnionTxn")
    @Explain(name = "银联补单任务", logLv = LogLevel.DEBUG)
    public CommonResponse fillUnionTxn(@IfspValid BatchRequest request) throws ParseException {
        return fillUnionTxnService.fillUnionTxn(request);
    }






}
