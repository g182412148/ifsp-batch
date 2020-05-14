package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.service.MerRegService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-10-23 15:51
 */
@Controller
public class GenMerRegFileController {

    @Resource
    private MerRegService merRegService;

    /**
     * 生成商户注册文件任务
     * @param request
     * @return
     */
    @SOA("699.genMerRegFile")
    @Explain(name = "生成商户注册文件", logLv = LogLevel.DEBUG)
    public CommonResponse genMerRegFile(@IfspValid MerRegRequest request) throws IOException, ParseException {
        return merRegService.genMerRegFile(request);
    }


    /**
     * 获取商户注册反馈文件任务
     * @param request
     * @return
     */
    @SOA("699.getMerRegRtnFile")
    @Explain(name = "获取商户注册反馈文件", logLv = LogLevel.DEBUG)
    public CommonResponse getMerRegRtnFile(@IfspValid MerRegRequest request) throws IOException {
        return merRegService.getMerRegRtnFile(request) ;
    }


}
