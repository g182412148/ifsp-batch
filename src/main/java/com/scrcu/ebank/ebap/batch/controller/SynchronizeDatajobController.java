package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.SynchronizeDataRequest;
import com.scrcu.ebank.ebap.batch.bean.response.SynchronizeDataResponse;
import com.scrcu.ebank.ebap.batch.service.SynchronizeDataService;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;


/**
 * 名称：〈主数据同步文件下载及解析批量任务〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月21日 <br>
 * 作者：lijingbo <br>
 * 说明：用途: 控制层(controller)暴露对外服务,调用业务层(service)完成业务处理 <br>
 *      声明: 使用@org.springframework.stereotype.Controller声明该类为一个控制器 <br>
 */
@Controller
public class SynchronizeDatajobController {
    @Resource
    private SynchronizeDataService synchronizeDataService;

    @SOA("003.SynchronizeStaff")
    @Explain(name = "同步操作员", logLv = LogLevel.DEBUG)
    public CommonResponse synchronizeStaff() throws Exception {
    	return  synchronizeDataService.synchronizeStaff();
    }

    @SOA("003.SynchronizeOrg")
    @Explain(name = "同步机构", logLv = LogLevel.DEBUG)
    public CommonResponse synchronizeOrg() throws Exception {
    	return synchronizeDataService.synchronizeOrg();
    }
    
    @SOA("003.SynchronizeOrgCorpId")
    @Explain(name = "同步机构", logLv = LogLevel.DEBUG)
    public SynchronizeDataResponse synchronizeOrgCorpId(@IfspValid SynchronizeDataRequest request) throws Exception {
    	return synchronizeDataService.synchronizeOrgCorpId(request);
    }
}
