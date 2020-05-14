package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.service.ErrInfoService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @Description:
 * @CopyRightInformation : 数云
 * @Prject: 数云PMS
 * @author: sun_b
 * @date: 2020/5/8
 */
@Controller
public class ErrInfoController {

    @Resource
    private ErrInfoService errInfoService;

    @SOA("699.getYestodayErrInfo")
    @Explain(name = "抽取前一日差错数据", logLv = LogLevel.DEBUG)
    public CommonResponse getYestodayErrInfo() throws Exception {

        return errInfoService.getYestodayErrInfo();
    }

    @SOA("699.pushErrFile")
    @Explain(name = "推送差错文件", logLv = LogLevel.DEBUG)
    public CommonResponse pushErrFile() throws Exception {

        return errInfoService.pushErrFile();
    }

    @SOA("699.ErrAcc")
    @Explain(name = "差错记账", logLv = LogLevel.DEBUG)
    public CommonResponse ErrAcc() throws Exception {

        return errInfoService.errAcc();
    }



}
