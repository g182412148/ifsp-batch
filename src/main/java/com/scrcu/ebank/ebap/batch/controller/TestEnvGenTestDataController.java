package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetPagyTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.service.GenTestDataService;
import com.scrcu.ebank.ebap.batch.service.TestEnvGenAccFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * 性能环境测试数据生成
 * @author: ljy
 * @create: 2018-11-06 10:31
 */
@Controller
public class TestEnvGenTestDataController {

    @Resource
    private GenTestDataService genTestDataService;


    @SOA("699.genLoanPayTestData")
    @Explain(name = "性能环境授信支付数据生成", logLv = LogLevel.DEBUG)
    public CommonResponse genLoanPayTestData(GetPagyTxnInfoRequest request){

        return genTestDataService.genLoanpayTestData(request);
    }

    @SOA("699.genPointPayTestData")
    @Explain(name = "性能环境纯积分支付数据生成", logLv = LogLevel.DEBUG)
    public CommonResponse genPointPayTestData(GetPagyTxnInfoRequest request){
        return genTestDataService.genPointpayTestData(request);
    }
    @SOA("699.genAliTestData")
    @Explain(name = "性能环境支付宝支付数据生成", logLv = LogLevel.DEBUG)
    public CommonResponse genAliTestData(GetPagyTxnInfoRequest request){
        return genTestDataService.genAliTestData(request);
    }

    @SOA("699.genWxTestData")
    @Explain(name = "性能环境微信支付数据生成", logLv = LogLevel.DEBUG)
    public CommonResponse genWxTestData(GetPagyTxnInfoRequest request){
        return genTestDataService.genWxTestData(request);
    }


    @SOA("699.genUnionQrcTestData")
    @Explain(name = "测试环境银联二维码对账单生成", logLv = LogLevel.DEBUG)
    public CommonResponse genUnionQrcTestData(GetPagyTxnInfoRequest request){
        return genTestDataService.genUnionQrcTestData(request);

    }

    @SOA("699.genUnionAllChnlTestData")
    @Explain(name = "测试环境银联全渠道对账单生成", logLv = LogLevel.DEBUG)
    public CommonResponse genUnionAllChnlTestData(GetPagyTxnInfoRequest request){
        return genTestDataService.genUnionAllChnlTestData(request);
    }


}
