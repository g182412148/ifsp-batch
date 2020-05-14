package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.request.*;
import com.scrcu.ebank.ebap.batch.bean.response.BthKeepAccResponse;
import com.scrcu.ebank.ebap.batch.bean.response.KeepAccResponse;
import com.scrcu.ebank.ebap.batch.service.*;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;


/**
 * 名称：〈记账任务〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年07月26日 <br>
 * 作者：lijingbo <br>
 * 说明：用途: 控制层(controller)暴露对外服务,调用业务层(service)完成业务处理 <br>
 *      声明: 使用@org.springframework.stereotype.Controller声明该类为一个控制器 <br>
 */
@Controller
public class KeepAccountjobController {

    @Resource
    private DayTimeKeepAccoutService dayTimeKeepAccoutService;

    @Resource
    private BthKeepAccRstQryService bthKeepAccRstQryService;

    @Resource
    private BthKeepAcctReverseService bthKeepAcctReverseService;

    @Resource
    private BthSynKeepAccountService bthSynKeepAccountService;


//    @SOA("001.DaytimeKeepAccount")
//    @Explain(name = "日间订单初始化记账", logLv = LogLevel.DEBUG)
//    public KeepAccResponse daytimeKeepAccount(@IfspValid DayTimeKeepAcctRequest request) throws Exception {
//        return dayTimeKeepAccoutService.daytimeKeepAccount(request);
//    }
//
//    @SOA("009.onceKeepAccount")
//    @Explain(name = "单笔记账", logLv = LogLevel.DEBUG)
//    public KeepAccResponse onceKeepAccount(@IfspValid OnceKeepAcctRequest request) throws Exception {
//        return dayTimeKeepAccoutService.onceKeepAccount(request);
//    }
//
//
//    @SOA("009.bthSynKeepAccount")
//    @Explain(name = "批量同步记账", logLv = LogLevel.INFO)
//    public BthKeepAccResponse bthSynKeepAccount(@IfspValid NewDayTimeKeepAcctRequest request) throws Exception {
//        return bthSynKeepAccountService.bthSynKeepAccount(request);
//    }
//
//    @SOA("009.bthKeepAccountRstQry")
//    @Explain(name = "批量记账结果查询", logLv = LogLevel.INFO)
//    public BthKeepAccResponse bthKeepAccountRstQry(@IfspValid KeepAccQryRequest request) throws Exception {
//        return bthKeepAccRstQryService.bthKeepAccountRstQry(request);
//    }
//
//    @SOA("009.onceKeepAccountReverse")
//    @Explain(name = "单笔冲正", logLv = LogLevel.INFO)
//    public BthKeepAccResponse onceKeepAccountReverse(@IfspValid OnceRevKeepAccRequest request) throws Exception {
//        return bthKeepAcctReverseService.onceKeepAccountReverse(request);
//    }
//
//    @SOA("009.bthKeepAccountReverse")
//    @Explain(name = "批量记账冲正", logLv = LogLevel.INFO)
//    public BthKeepAccResponse bthKeepAccountReverse(@IfspValid KeepAccRevRequest request) throws Exception {
//        return bthKeepAcctReverseService.bthKeepAccountReverse(request);
//    }

}
