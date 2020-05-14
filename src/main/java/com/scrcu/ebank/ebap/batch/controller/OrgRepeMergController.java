package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.OrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.bean.request.SelectOrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.bean.response.SelectOrgRepeMergResponse;
import com.scrcu.ebank.ebap.batch.service.OrgRepeMergService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;


/**
 * 名称：〈机构撤并〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019年06月223日 <br>
 * 作者：xsl <br>
 */
@Controller
public class OrgRepeMergController {
    @Resource
    private OrgRepeMergService orgRepeMergService;

    @SOA("009.orgRepeMerg")
    @Explain(name = "执行机构撤并", logLv = LogLevel.DEBUG)
    public CommonResponse orgRepeMerg(@IfspValid OrgRepeMergRequest req) {
        return  orgRepeMergService.orgRepeMerg(req);
    }

    @SOA("009.selectOrgRepeMerg")
    @Explain(name = "查询机构撤并执行状态", logLv = LogLevel.DEBUG)
    public SelectOrgRepeMergResponse selectOrgRepeMerg(@IfspValid SelectOrgRepeMergRequest req){
        return  orgRepeMergService.selectOrgRepeMerg(req);
    }
}
