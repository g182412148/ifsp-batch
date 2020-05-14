package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.service.*;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;


/**
 * 名称：〈通道对账批量任务〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月21日 <br>
 * 作者：lijingbo <br>
 * 说明：用途: 控制层(controller)暴露对外服务,调用业务层(service)完成业务处理 <br>
 *      声明: 使用@org.springframework.stereotype.Controller声明该类为一个控制器 <br>
 */
@Controller
public class AcctContrastJobController {
	
    @Resource
    private AcctContrastService acctContrastService;
    
    @Resource
    private AliAcctContrastService aliAcctContrastService;
    
    @Resource
    private UnionAcctContrastService unionAcctContrastService;
    
    @Resource
    private DebitAcctContrastService debitAcctContrastService;
    
    @Resource
    private KeepAcctContrastService keepAcctContrastService;
    
    @Resource
    private CoreAcctContrastService CoreAcctContrastService;

    @Resource
    private CoreAcctContrastNewService coreAcctContrastNewService;

    @Resource
    private UnionAllChnlContrastService unionAllChnlContrastService;

    @Resource
    private UnionBrandFeeStoreService unionBrandFeeStoreService;
    
    

    @SOA("002.AliBillContrast")
    @Explain(name = "支付宝通道对账", logLv = LogLevel.DEBUG)
    public CommonResponse aliBillContrast(@IfspValid AcctContrastRequest request) {
        return aliAcctContrastService.aliBillContrast(request);
    }
    
    @SOA("003.UnionBillContrast")
    @Explain(name = "银联二维码通道对账", logLv = LogLevel.DEBUG)
    public AcctContrastResponse unionBillContrastNew(@IfspValid AcctContrastRequest request) throws Exception {
        return unionAcctContrastService.unionBillContrastNew(request);
    }

//    @SOA("003.UnionBillContrast")
    @Explain(name = "银联二维码通道对账", logLv = LogLevel.DEBUG)
    public AcctContrastResponse unionBillContrast(@IfspValid AcctContrastRequest request) throws Exception {
        return unionAcctContrastService.unionBillContrast(request);
    }
    
    @SOA("004.DebitBillContrast")
    @Explain(name = "本行借记卡核心对账", logLv = LogLevel.DEBUG)
    public CommonResponse debitBillContrast(@IfspValid AcctContrastRequest request) throws Exception {
    	return debitAcctContrastService.debitBillContrast(request);
    }


    @SOA("006.RsltKeepAccContrast")
    @Explain(name = "补记订单记账流水", logLv = LogLevel.DEBUG)
    public AcctContrastResponse rsltKeepAccContrast(@IfspValid AcctContrastRequest request) throws Exception {
    	return keepAcctContrastService.rsltKeepAccContrast(request);
    }

    @SOA("007.KeepAccCoreContrast")
    @Explain(name = "记账表与核心对账", logLv = LogLevel.DEBUG)
    public AcctContrastResponse keepAccCoreContrast(@IfspValid AcctContrastRequest request) throws Exception {
    	return CoreAcctContrastService.keepAccCoreContrast(request);
    }

    //@SOA("007.KeepAccCoreContrast")
    @Explain(name = "记账表与核心对账", logLv = LogLevel.DEBUG)
    public AcctContrastResponse keepAccCoreContrastNew(@IfspValid AcctContrastRequest request) throws Exception {
        return coreAcctContrastNewService.keepAccCoreContrastNew(request);
    }

//    @SOA("699.UnionAllChnlBillContrast")
    @Explain(name = "银联全渠道对账", logLv = LogLevel.DEBUG)
    public AcctContrastResponse unionAllChnlBillContrast(@IfspValid AcctContrastRequest request) throws Exception {
        return unionAllChnlContrastService.unionAllChnlBillContrast(request);
    }

    @SOA("699.UnionAllChnlBillContrast")
    @Explain(name = "银联全渠道对账", logLv = LogLevel.DEBUG)
    public AcctContrastResponse unionAllChnlBillContrastNew(@IfspValid AcctContrastRequest request) throws Exception {
        return unionAllChnlContrastService.unionAllChnlBillContrast(request);
    }

    @SOA("699.UnionBrandFeeStore")
    @Explain(name = "银联品牌服务费入银联文件明细表", logLv = LogLevel.DEBUG)
    public CommonResponse unionBrandFeeStore(@IfspValid MerRegRequest request) throws Exception {
        return unionBrandFeeStoreService.unionBrandFeeStore(request);
    }


}
