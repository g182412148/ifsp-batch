package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.service.GenAccFileService;
import com.scrcu.ebank.ebap.batch.service.GetAccBkFileService;
import com.scrcu.ebank.ebap.batch.service.IndirectClearingService;
import com.scrcu.ebank.ebap.batch.service.IndirectSummService;
import com.scrcu.ebank.ebap.batch.service.InvokeCH1730Service;
import com.scrcu.ebank.ebap.batch.service.TranferBackAmtCountService;
import com.scrcu.ebank.ebap.batch.service.TransferInfoSummService;
import com.scrcu.ebank.ebap.batch.service.UpdateStlStatusService;
import com.scrcu.ebank.ebap.batch.service.UpdateTransferStatusService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;

/**
 * 间接结算Controller
 * @author ydl
 *
 */
@Controller
public class IndirectStlController {
	
	@Resource
    private TranferBackAmtCountService tranferBackAmtCountService;
	
	@Resource
    private TransferInfoSummService transferInfoSummService;
	
	@Resource
	private GenAccFileService genAccFileService;
	
	@Resource
	private InvokeCH1730Service invokeCH1730Service;
	
	@Resource
	private GetAccBkFileService getAccBkFileService;
	
	@Resource
	private UpdateStlStatusService updateStlStatusService;
	
	@Resource
	private IndirectClearingService indirectClearingService;         //二级商户间接结算清分
	@Resource
	private IndirectSummService indirectSummService;                 //二级商户间接结算汇总
	@Resource
	UpdateTransferStatusService updateTransferStatusService;         //更新

    @SOA("001.transfer4IndirectStl")
    @Explain(name = "转账", logLv = LogLevel.DEBUG)
    public CommonResponse transfer(BatchRequest request) throws Exception 
    {
    	
    	//1)统计数据，写转账信息表
    	tranferBackAmtCountService.getTransferAmtInfo();
    	//2)将转账信息写入到商户入账表
    	transferInfoSummService.summTransferInfo();
    	//3)根据入账表生成文件
    	genAccFileService.genAccFile(Constans.IN_ACCT_TYPE_TRANSFER);
    	//4)invoke ch1730
    	invokeCH1730Service.invokeCH1730(Constans.FILE_TYPE_TRANSFER);   //转账文件
    	//5)处理反馈文件(根据核心处理结果更新"商户入账表"状态及"转账表"状态)
    	getAccBkFileService.getAccBkFile(Constans.FILE_TYPE_TRANSFER);
    	//6)根据转账表状态更新商户入账明细表状态(转账状态)      
    	updateTransferStatusService.updateTransferStatus();
    	
    	//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
        return commonResponse;
    }
    
    @SOA("001.indirectStl")
    @Explain(name = "间接结算结算给二级商户", logLv = LogLevel.DEBUG)
    public CommonResponse indirectStl(BatchRequest request) throws Exception 
    {
    	//1)二级商户结算清分
    	indirectClearingService.indirectStlClearing(request);
    	//2)二级商户结算汇总
    	indirectSummService.indirectOrderSumm(request);
    	//3)生成二级商户结算文件
    	genAccFileService.genAccFile(Constans.IN_ACCT_TYPE_SUBMER);
    	//4)invoke ch1730
    	invokeCH1730Service.invokeCH1730(Constans.FILE_TYPE_SUBMER_STL);   //间接结算入账文件
    	//5)处理反馈文件(根据核心处理结果更新"商户入账表"状态及"清分表"状态)
    	getAccBkFileService.getAccBkFile(Constans.FILE_TYPE_SUBMER_STL);
    	//6)根据转账表状态更新商户入账明细表状态(转账状态)
    	updateStlStatusService.updateStlStatus(request);
    	
    	//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
        return commonResponse;
    }
    
    @SOA("001.updateSubMerStlStatus")
    @Explain(name = "更新隔日间接结算状态", logLv = LogLevel.DEBUG)
    public CommonResponse wxOrderClearing(BatchRequest request) throws Exception 
    {
    	updateStlStatusService.updateStlStatus(request);
    	//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
        return commonResponse;
    }
    
}
