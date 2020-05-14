package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.UnionChkHandleService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @ClassName UnionChkHandleController
 * @Description 银联对账文件处理(冲正/冲正撤销/被冲正交易)
 * @Author NiklausZhu
 * @Date 2019/12/2 20:01
 **/
@Controller
public class UnionChkHandleController {

    @Resource
    private UnionChkHandleService unionChkHandleService;


    @SOA("003.UnionChkHandle")
    @Explain(name = "银联通道对账明细处理", logLv = LogLevel.DEBUG)
    public CommonResponse unionChkHandle(@IfspValid BatchRequest request) throws Exception{
        return unionChkHandleService.unionChkHandle(request);
    }
}
