package com.scrcu.ebank.ebap.batch.controller;


import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.SendMsg4InvalidAccService;
import com.scrcu.ebank.ebap.batch.service.SynchronizeAccService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 数据同步器
 * 1、同步订单中心审核后的结算账户到清算中心
 */
@Controller
public class NoticeController {

    @Autowired
    private SendMsg4InvalidAccService sendMsg4InvalidAccService;


    //【四川农信】尊敬的xxx（联系人姓名），您的商户xxxxxx（商户名称）因结算账户状态异常清算失败，
    // 请联系客户经理恢复账户状态或修改结算账户，修改后未入账交易将会清算至新账户。
    @SOA("001.sendMsg4InvalidAcc")
    @Explain(name = "结算失败发送短信", logLv = LogLevel.DEBUG)
    public CommonResponse sendMsg4InvalidAcc(BatchRequest request) throws Exception {
        return sendMsg4InvalidAccService.sendMsg4InvalidAcc(request);
    }
}
