package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.DirectWxBillDownloadService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;

/**
 * 直连微信Controller
 * @author ydl
 *
 */
@Controller
public class DirectWxController 
{
	
	@Resource
	private DirectWxBillDownloadService directWxBillDownloadService;         //更新

    @SOA("001.directWxBillDownload")
    @Explain(name = "直连微信对账单下载", logLv = LogLevel.DEBUG)
    public CommonResponse directWxBillDownload(BatchRequest request) throws Exception 
    {
        return directWxBillDownloadService.wxBillDownload(request);
    }
    
}
