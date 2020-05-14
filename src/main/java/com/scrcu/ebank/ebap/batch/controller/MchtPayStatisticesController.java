package com.scrcu.ebank.ebap.batch.controller;

import com.alibaba.fastjson.JSONArray;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfoResp;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMchtPayStatisticsRequset;
import com.scrcu.ebank.ebap.batch.bean.request.TimeQuanTumRequest;
import com.scrcu.ebank.ebap.batch.bean.response.TimeQuanTumResponse;
import com.scrcu.ebank.ebap.batch.service.MchtTransactionStatisticsService;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;


@Controller
@Slf4j
public class MchtPayStatisticesController {

    @Resource
    MchtTransactionStatisticsService mchtTransactionStatisticsService;

//    @SOA("queryMchtPayStatistics")
//    @ResponseBody
//    public Map<String, Object> queryMchtPayStatistics(QueryMchtPayStatisticsRequset requset){
//        log.info("商户日交易统计查询请求报文（queryMchtPayStatistics）：" + IfspFastJsonUtil.tojson(requset));
//        if (IfspDataVerifyUtil.isBlank(requset)){
//            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "请求报文为空！");
//        }
//        //校验
//        requset.valid();
//        List<MchtPayStatisticsInfoResp> list = mchtTransactionStatisticsService.queryMchtPayStatistics(requset);
//        Map<String, Object> map = new HashMap<>();
//        map.put("data", JSONArray.toJSONString(list));
//        map.put("respCode", SystemConfig.getSuccessCode());
//        map.put("respMsg", SystemConfig.getSuccessMsg());
//        return map;
//    }
//
//    @SOA("699.timeQuanTum")
//    public TimeQuanTumResponse timeQuanTum (TimeQuanTumRequest request) {
//        return mchtTransactionStatisticsService.timeQuanTum(request);
//    }


}
