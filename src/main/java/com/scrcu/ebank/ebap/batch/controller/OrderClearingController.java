package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.service.OrderClearing;
import com.scrcu.ebank.ebap.batch.service.impl.AliClearingServiceImp;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.ClearRequest;
import com.scrcu.ebank.ebap.batch.service.AccFailDataClearingService;
import com.scrcu.ebank.ebap.batch.service.OrderClearingService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;

/**
 * 订单清分Controller
 * @author ydl
 *
 */
@Controller
public class OrderClearingController {

    @Resource
    private OrderClearingService orderClearingService;

    @Resource
    private AccFailDataClearingService accFailDataClearingService;

    @Resource
    private OrderClearing wxClearingServiceImp;

    @Resource
    private OrderClearing aliClearingServiceImp;

    @Resource
    private OrderClearing coreClearingServiceImp;

    @Resource
    private OrderClearing unionpayClearingServiceImp;



    @SOA("001.coreOrderClearing")
    @Explain(name = "本行订单清分", logLv = LogLevel.DEBUG)
    public CommonResponse coreOrderClearing(BatchRequest request) throws Exception {
        request.setPagyNo(Constans.IBANK_SYS_NO);
        return coreClearingServiceImp.channelClearing(request);
    }

    @SOA("001.wxOrderClearing")
    @Explain(name = "微信订单清分", logLv = LogLevel.DEBUG)
    public CommonResponse wxOrderClearing(BatchRequest request) throws Exception {
        request.setPagyNo(Constans.WX_SYS_NO);
        return wxClearingServiceImp.channelClearing(request);
    }

    @SOA("001.aliOrderClearing")
    @Explain(name = "支付宝订单清分", logLv = LogLevel.DEBUG)
    public CommonResponse aliOrderClearing(BatchRequest request) throws Exception {
        request.setPagyNo(Constans.ALI_SYS_NO);
        return aliClearingServiceImp.channelClearing(request);
    }

    @SOA("001.unionpayOrderClearing")
    @Explain(name = "银联订单清分", logLv = LogLevel.DEBUG)
    public CommonResponse unionpayOrderClearing(BatchRequest request) throws Exception {
        return unionpayClearingServiceImp.channelClearing(request);
    }

    @SOA("001.accoutFailDataHandle")
    @Explain(name = "记账失败数据处理", logLv = LogLevel.DEBUG)
    public CommonResponse accoutFailDataHandle(BatchRequest request) throws Exception {
        return accFailDataClearingService.handleAccFailData(request);
    }
}
