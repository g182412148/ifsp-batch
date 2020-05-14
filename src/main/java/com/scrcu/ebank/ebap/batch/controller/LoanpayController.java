package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.service.GenSCLMChkFileService;
import com.scrcu.ebank.ebap.batch.service.LoanpayOrderChkService;
import com.scrcu.ebank.ebap.batch.service.PointpayOrderChkService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;

/**
 * 授信支付Controller
 * @author ydl
 *
 */
@Controller
public class LoanpayController 
{
	
	@Resource
	private LoanpayOrderChkService loanpayOrderChkService;
	
	@Resource
	private GenSCLMChkFileService genSCLMChkFileService;
	
	@Resource
	private PointpayOrderChkService pointpayOrderChkService;
	
    @SOA("001.loanpayChk")
    @Explain(name = "授信支付对账", logLv = LogLevel.DEBUG)
    public CommonResponse loanpayChk(BatchRequest request) throws Exception 
    {
        return loanpayOrderChkService.loanpayOrderChk(request);
    }
    
    @SOA("001.genSCLMChkFile")
    @Explain(name = "生成授信支付放款文件", logLv = LogLevel.DEBUG)
    public CommonResponse genSCLMChkFile(BatchRequest request) throws Exception 
    {
        return genSCLMChkFileService.genSCLMChkFile(request);
    }
    
    @SOA("001.pointPayChk")
    @Explain(name = "纯积分支付对账", logLv = LogLevel.DEBUG)
    public CommonResponse pointPayChk(BatchRequest request) throws Exception 
    {
        return pointpayOrderChkService.pointpayOrderChk(request);
    }
    
}
