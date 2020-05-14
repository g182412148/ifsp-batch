package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.request.GetFileNameRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetSerApprFileNameRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OfflineCreatMerChkFileRequest;
import com.scrcu.ebank.ebap.batch.bean.response.GetFileNameResponse;
import com.scrcu.ebank.ebap.batch.service.*;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;

/**
 * 商户文件生成Controller
 * @author ydl
 *1、生成商户对账文件
 *2、生成商户结算文件
 *3、生成商户审核结果文件
 */
@Controller
public class MchtFileController 
{
	
	@Resource
	private GenMerStlFileService genMerStlFileService;
	
	@Resource
	private GenMerChkFileService genMerChkFileService;

    @Resource
    private GenOfflineMerChkFileService genOfflineMerChkFileService;

	
	@Resource
	private GenMerApprFileService genMerApprFileService;
	
	@Resource
	private FeeCalcService feeCalcService;
	
	@Resource
	private PointFileDispatchService pointFileDispatchService;

    @Resource
    private GenEptFileService genEptFileService;

    @SOA("001.genMerStlFile")
    @Explain(name = "生成商户结算文件", logLv = LogLevel.DEBUG)
    public CommonResponse loanpayChk(BatchRequest request) throws Exception 
    {
        return genMerStlFileService.genMerStlFile(request);
    }
    
    @SOA("001.genMerChkFile")
    @Explain(name = "生成商户对账文件", logLv = LogLevel.DEBUG)
    public CommonResponse genMerChkFile(BatchRequest request) throws Exception 
    {
        return genMerChkFileService.genMerChkFile(request);
    }
    
    @SOA("001.genMerApprFile")
    @Explain(name = "生成商户审核文件", logLv = LogLevel.DEBUG)
    public CommonResponse genMerApprFile(BatchRequest request) throws Exception 
    {
        return genMerApprFileService.genMerApprFile(request);
    }
    
    @SOA("001.pointFileDispatch")
    @Explain(name = "积分文件分发", logLv = LogLevel.DEBUG)
    public CommonResponse pointFileDispatch(BatchRequest request) throws Exception 
    {
        return pointFileDispatchService.pointFileDispatch();
    }

    @SOA("001.genEptDataService")
    @Explain(name = "生成增值税数据", logLv = LogLevel.DEBUG)
    public CommonResponse genEptDataService(BatchRequest request) throws Exception
    {
        return genEptFileService.genEptChkFile(request);
    }
    
    @SOA("test.calcMerFee4Order")
    @Explain(name = "计算订单手续费", logLv = LogLevel.DEBUG)
    public CommonResponse calcMerFee4Order(BatchRequest request) throws Exception 
    {
        feeCalcService.calcMerFee4Order("18112710570000006952");
        //应答
  		CommonResponse commonResponse = new CommonResponse();
  		
  		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
  		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
  				
  		return commonResponse;
    }
    
    @SOA("test.calcMerFee4SubOrder")
    @Explain(name = "计算子订单手续费", logLv = LogLevel.DEBUG)
    public CommonResponse calcMerFee4SubOrder(BatchRequest request) throws Exception 
    {
        feeCalcService.calcMerFee4SubOrder("18112014200000005397S0001");
        //应答
  		CommonResponse commonResponse = new CommonResponse();
  		
  		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
  		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
  				
  		return commonResponse;
    }

    @SOA("001.genOffLineMerChkFile")
    @Explain(name = "商户生成商户对账文件", logLv = LogLevel.DEBUG)
    public CommonResponse genOffLineMerChkFile(OfflineCreatMerChkFileRequest request) throws Exception
    {
        return genOfflineMerChkFileService.genOffLineMerChkFile(request);
    }
    //qryFileName
    @SOA("qryFileName")
    @Explain(name = "查询对账文件名称", logLv = LogLevel.DEBUG)
    public GetFileNameResponse qryFileName(GetFileNameRequest request) throws Exception
    {
        return genOfflineMerChkFileService.qryFileName(request);
    }
    @SOA("qrySerMerApprFileName")
    @Explain(name = "查询服务商审核文件", logLv = LogLevel.DEBUG)
    public GetFileNameResponse qrySerMerAppFileName(GetSerApprFileNameRequest request) throws Exception
    {
        return genOfflineMerChkFileService.qrySerMerApprFileName(request);
    }
}
