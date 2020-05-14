package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.TxnSectionRequest;
import com.scrcu.ebank.ebap.batch.service.MchtTxnCountService;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-09-06 14:56
 */
@Controller
@Slf4j
public class MchtTxnCountController {

    @Resource
    private MchtTxnCountService mchtTxnCountService;


//    @SOA("queryMerTxnCount")
//    @Explain(name = "门户交易金额分段统计", logLv = LogLevel.DEBUG)
//    public Map<String, Object> queryMerTxnCount(@IfspValid TxnSectionRequest request){
//        return mchtTxnCountService.queryMerTxnCount(request);
//    }
}
