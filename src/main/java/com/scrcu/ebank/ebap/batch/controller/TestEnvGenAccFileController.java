package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetPagyTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.service.TestEnvGenAccFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

/**
 * 测试环境模拟对账单任务
 * @author: ljy
 * @create: 2018-11-06 10:31
 */
@Controller
public class TestEnvGenAccFileController {

    @Resource
    private TestEnvGenAccFileService testEnvGenAccFileService;


    @SOA("699.testEnvGenWxFile")
    @Explain(name = "测试环境微信对账单生成", logLv = LogLevel.DEBUG)
    public CommonResponse WxFile(GetPagyTxnInfoRequest request){

        return testEnvGenAccFileService.genWxFile(request);
    }

    @SOA("699.testEnvGenAliFile")
    @Explain(name = "测试环境支付宝对账单生成", logLv = LogLevel.DEBUG)
    public CommonResponse AliFile(GetPagyTxnInfoRequest request){
        return testEnvGenAccFileService.genAliFile(request);
    }


    @SOA("699.testEnvGenUnionQrcFile")
    @Explain(name = "测试环境银联二维码对账单生成", logLv = LogLevel.DEBUG)
    public CommonResponse UnionQrcFile(GetPagyTxnInfoRequest request){
        return testEnvGenAccFileService.genUnionQrcFile(request);

    }

    @SOA("699.testEnvGenUnionAllChnlFile")
    @Explain(name = "测试环境银联全渠道对账单生成", logLv = LogLevel.DEBUG)
    public CommonResponse UnionAllChnlFile(GetPagyTxnInfoRequest request){
        return testEnvGenAccFileService.genUnionAllChnlFile(request);
    }

    @SOA("699.testEnvMoveFile")
    @Explain(name = "测试环境本行对账文件与记账文件从远程目录移动到本地", logLv = LogLevel.DEBUG)
    public CommonResponse testEnvMoveFile(GetPagyTxnInfoRequest request){
        return testEnvGenAccFileService.moveFile(request);
    }


    @SOA("699.testEnvMoveFileCycle")
    @Explain(name = "测试环境本行对账文件与记账文件从远程目录移动到本地 (循环调用版)", logLv = LogLevel.DEBUG)
    public CommonResponse testEnvMoveFilePerTm(GetPagyTxnInfoRequest request) throws InterruptedException {
        return testEnvGenAccFileService.testEnvMoveFilePerTm(request);
    }


    @SOA("699.testEnvMoveInaccFile")
    @Explain(name = "测试环境移动入账文件任务", logLv = LogLevel.DEBUG)
    public CommonResponse testEnvMoveInaccFile(GetPagyTxnInfoRequest request) throws InterruptedException, ExecutionException {
        return testEnvGenAccFileService.testEnvMoveInaccFile(request);
    }


    @SOA("699.prdEnvBthRstFile")
    @Explain(name = "生产环境生成跑批结果文件任务", logLv = LogLevel.DEBUG)
    public CommonResponse prdEnvBthRstFile(BatchRequest request)  {
        return testEnvGenAccFileService.prdEnvBthRstFile(request);
    }


    @SOA("699.prdSupplementAcc")
    @Explain(name = "补充记账表任务", logLv = LogLevel.DEBUG)
    public CommonResponse prdSupplementAcc(GetPagyTxnInfoRequest request)  {
        return testEnvGenAccFileService.prdSupplementAcc(request);
    }



}
