package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.request.*;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccDtlResponse;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccPosiResponse;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccResponse;
import com.scrcu.ebank.ebap.batch.service.*;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;

/**
 * @author ljy
 */
@Controller
public class ClearStlmJobController {

    /**
     * 清分汇总
     */
    @Resource
    private ClearStlmSumJobService clearStlmSumService;


    /**
     * 生成入账文件
     */
    @Resource
    private GenStlFileGrpService genStlFileGrpService;

    /**
     * 入账结果查询
     */
    @Resource
    private QueryMerInAccRstService queryMerInAccRstService;


    /**
     * 核心反馈文件检查
     */
    @Resource
    private CoreAccBkFileChkService coreAccBkFileChkService;

    /**
     * 商户门户入账查询
     */
    @Resource
    private QueryMerInAccPositiveService queryMerInAccPositiveService;

    /**
     * 反欺诈服务
     */
    @Resource
    private AntiFraudService antiFraudService;

    /**
     * 生成入账文件
     */
    @Resource
    private GenOtherStlService genOtherStlService;


    @SOA("001.CapitalSummarizeStep")
    @Explain(name = "清分汇总", logLv = LogLevel.DEBUG)
    public CommonResponse clearSumJob(@IfspValid ClearRequest request){
        return clearStlmSumService.clearSumJob(request);
    }


    @SOA("002.GenerateStlFileGrp")
    @Explain(name = "入账文件生成", logLv = LogLevel.DEBUG)
    public CommonResponse generateStlFileGrp(@IfspValid GenerateStlFileRequest request){
        return genStlFileGrpService.generateStlFileGrp(request);
    }

//    @SOA("003.QueryMerInAcc")
//    @Explain(name = "商户入账查询接口", logLv = LogLevel.DEBUG)
//    public QueryMerInAccResponse queryMerInAcc(@IfspValid QueryMerInAccRequest request){
//        return queryMerInAccRstService.queryMerInAcc(request);
//    }
//
//    @SOA("004.QueryMerInAccDtl")
//    @Explain(name = "商户入账明细查询接口", logLv = LogLevel.DEBUG)
//    public QueryMerInAccDtlResponse queryMerInAccDtl(@IfspValid QueryMerInAccDtlRequest request){
//        return queryMerInAccRstService.queryMerInAccDtl(request);
//    }

    @SOA("005.CoreAccBkFileChk")
    @Explain(name = "核心反馈文件检查", logLv = LogLevel.DEBUG)
    public CommonResponse coreAccBkFileChk(@IfspValid CoreBkFileChkRequest request){
        return coreAccBkFileChkService.coreAccBkFileChk(request);
    }

//    @SOA("006.QueryMerInAccPositive")
//    @Explain(name = "门户商户入账查询", logLv = LogLevel.DEBUG)
//    public QueryMerInAccPosiResponse queryMerInAccPositive(@IfspValid QueryMchtsMerInAccRequest request){
//        return queryMerInAccPositiveService.queryMerInAccPositive(request);
//    }
//    @SOA("merInAccPositiveBillDown")
//    @Explain(name = "门户商户入账查询", logLv = LogLevel.DEBUG)
//    public QueryMerInAccPosiResponse merInAccPositiveBillDown(@IfspValid QueryMchtsMerInAccRequest request){
//        return queryMerInAccPositiveService.queryMerInAccPositiveDown(request);
//    }

    @SOA("699.MerInAccAntiFraud")
    @Explain(name = "反欺诈控制商户入账任务", logLv = LogLevel.DEBUG)
    public CommonResponse merInAccAntiFraud(){
        return antiFraudService.merInAccAntiFraud();
    }

    @SOA("699.MerInAccRstAntiFraud")
    @Explain(name = "商户入账结束后上送反欺诈结果", logLv = LogLevel.DEBUG)
    public CommonResponse merInAccRstAntiFraud(@IfspValid BatchRequest request){
        return antiFraudService.merInAccRstAntiFraud(request);
    }

    @SOA("699.otherSel")
    @Explain(name = "他行入账", logLv = LogLevel.DEBUG)
    public CommonResponse otherSel(@IfspValid GenerateStlFileRequest request){
        return genOtherStlService.otherSel(request);
    }

    @SOA("699.otherSelUpdateState")
    @Explain(name = "更新他行入账状态", logLv = LogLevel.DEBUG)
    public CommonResponse otherSelUpdateState(@IfspValid GenerateStlFileRequest request){
        return genOtherStlService.otherSelUpdateState(request);
    }

}
