package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.CalcOnlineOrderStlDateService;
import com.scrcu.ebank.ebap.batch.service.StlStatusSyncService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;

import lombok.extern.slf4j.Slf4j;

/**
 * 间接结算Controller
 * @author ydl
 *
 */
@Controller
@Slf4j
public class OrderSyncController {
	
	@Resource
    private CalcOnlineOrderStlDateService calcOnlineOrderStlDateService;
	
	@Resource
    private StlStatusSyncService stlStatusSyncService;
	

    @SOA("001.calcStlDate")
    @Explain(name = "计算子订单结算日期", logLv = LogLevel.DEBUG)
    public CommonResponse calcStlDate(BatchRequest request) throws Exception 
    {
    	log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>计算子订单结算日期");
    	//1)统计数据，写转账信息表
    	calcOnlineOrderStlDateService.calcStlDate(request);
    	
    	//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
        return commonResponse;
    }
    
    
    @SOA("001.syncStlStatus")
    @Explain(name = "同步入账明细表与子订单表订单结算状态", logLv = LogLevel.DEBUG)
    public CommonResponse syncStlStatus(BatchRequest request) throws Exception 
    {
    	stlStatusSyncService.syncStlStatus(request);
    	//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
        return commonResponse;
    }
    
}
