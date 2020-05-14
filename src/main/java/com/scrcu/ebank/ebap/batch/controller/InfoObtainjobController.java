package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.service.GetLocalInfoService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.batch.bean.request.*;
import com.scrcu.ebank.ebap.batch.service.LocalKeepAcctInfoService;
import com.scrcu.ebank.ebap.batch.service.LocalUnionTxnInfoService;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.GetOrderInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetPagyTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetSettleDateRequest;
import com.scrcu.ebank.ebap.batch.bean.request.LocalTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.response.GetOrderInfoResponse;
import com.scrcu.ebank.ebap.batch.bean.response.GetPagyTxnInfoResponse;
import com.scrcu.ebank.ebap.batch.bean.response.GetSettleDateResponse;
import com.scrcu.ebank.ebap.batch.bean.response.LocalTxnInfoResponse;
import com.scrcu.ebank.ebap.batch.service.DateService;
import com.scrcu.ebank.ebap.batch.service.LocalTxnInfoService;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;

/**
 * 名称：〈流水抽取批量任务〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月20日 <br>
 * 作者：lijingbo <br>
 * 说明：用途: 控制层(controller)暴露对外服务,调用业务层(service)完成业务处理 <br>
 *      声明: 使用@org.springframework.stereotype.Controller声明该类为一个控制器 <br>
 */
@Controller
public class InfoObtainjobController {

    @Resource
    private DateService dateService;
    
    @Resource
    private LocalTxnInfoService localTxnInfoService;
    /**
     * 本行抽数
     */
    @Resource
    private GetLocalInfoService iBankLocalTxnService;
    /**
     * 阿里抽数
     */
    @Resource
    private GetLocalInfoService aliLocalTxnService;

    @Resource
    private LocalUnionTxnInfoService localUnionTxnInfoService;

    @Resource
    private LocalKeepAcctInfoService localKeepAcctInfoService;
    
//    @SOA("002.GetWxAtTxnInfo")
//    @Explain(name = "微信通道流水抽取", logLv = LogLevel.DEBUG)
    public LocalTxnInfoResponse getWxAtTxnInfo(@IfspValid LocalTxnInfoRequest request) throws Exception {
        return localTxnInfoService.getWxAtTxnInfo(request);
    }
    
    @SOA("003.GetAliAtTxnInfo")
    @Explain(name = "支付宝通道流水抽取", logLv = LogLevel.DEBUG)
    public CommonResponse getAliAtTxnInfo(@IfspValid GetOrderInfoRequest request)  {
        return aliLocalTxnService.getLocalTxnInfo(request);
    }
    
//    @SOA("004.GetUnionTxnInfo")
//    @Explain(name = "银联二维码流水抽取", logLv = LogLevel.DEBUG)
//    public LocalTxnInfoResponse getUnionTxnInfo(@IfspValid LocalTxnInfoRequest request) throws Exception {
//        return localTxnInfoService.getUnionTxnInfo(request);
//    }
    
    @SOA("005.GetIbankTxnInfo")
    @Explain(name = "本行通道流水抽取", logLv = LogLevel.DEBUG)
    public CommonResponse getIbankLocalInfo(@IfspValid GetOrderInfoRequest request) {
    	return iBankLocalTxnService.getLocalTxnInfo(request);
    }
    
//    @SOA("006.GetTotalUnionTxn")
//    @Explain(name = "银联全渠道流水抽取", logLv = LogLevel.DEBUG)
//    public LocalTxnInfoResponse getTotalUnionTxn(@IfspValid LocalTxnInfoRequest request) throws Exception {
//    	return localTxnInfoService.getTotalUnionTxn(request);
//    }

    /**
     * 20190510 add by yangqi
     */
    @SOA("004.GetUnionTxnInfo")
    @Explain(name = "银联二维码流水抽取", logLv = LogLevel.DEBUG)
    public LocalTxnInfoResponse getUnionQrcTxnInfo(@IfspValid LocalTxnInfoRequest request) throws Exception {
        return localUnionTxnInfoService.getUnionTxnInfo(request);
    }

    @SOA("006.GetTotalUnionTxn")
    @Explain(name = "银联全渠道流水抽取", logLv = LogLevel.DEBUG)
    public LocalTxnInfoResponse getUnionOnlinTxn(@IfspValid LocalTxnInfoRequest request) throws Exception {
        return localUnionTxnInfoService.getTotalUnionTxn(request);
    }

    @SOA("006.GetKeepAccTxn")
    @Explain(name = "记账表流水抽取", logLv = LogLevel.DEBUG)
    public LocalTxnInfoResponse getKeepAccInfo(@IfspValid LocalKeepTxnInfoRequest request) throws Exception {
        return localKeepAcctInfoService.getKeepAccInfo(request);
    }

}
