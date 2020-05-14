package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.msg.IBankMsg;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthBatchAccountFileDao;
import com.scrcu.ebank.ebap.batch.service.InvokeCH1730Service;
import com.scrcu.ebank.ebap.batch.soaclient.ClearingSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvokeCH1730ServiceImpl implements InvokeCH1730Service 
{

	@Resource
	private ClearingSoaService clearingSoaService;
	
	@Resource
	private BthBatchAccountFileDao bthBatchAccountFileDao;
	
	/**
	 * 将处理后的文件备份到这个目录
	 */
	@Value("${accountRecvDirBackUp}")
	private String accountRecvDirBackUp;
	
	/**
	 * 核心反馈文件目录
	 */
	@Value("${accountRecvDir}")
	private String accountRecvDir;
	
	@Override
	public CommonResponse invokeCH1730(String fileType) 
	{
		log.info(">>>>>>>>>>>>>>>>>>>>>>invokeCH1730 service begin");
		List<BthBatchAccountFile> accFileList = this.getUnhandleAccFileList(fileType);
		for(BthBatchAccountFile accFile : accFileList)
		{
			this.callCH1730(accFile);
		}
		return null;
	}
	
	public List<BthBatchAccountFile> getUnhandleAccFileList(String fileType)
	{
		Map<String,Object> map = new HashMap<>();
	    map.put("fileType",fileType);
	    map.put("dealStatus",Constans.FILE_STATUS_00);
	        
		List<BthBatchAccountFile> accFileList = bthBatchAccountFileDao.selectList("accF_selectByState", map);
		
		return accFileList;
	}
	
	//调用CH1730接口发起对账请求并获取核心对账文件文件名
	public boolean callCH1730(BthBatchAccountFile accFile)
	{
		Date currDate = new Date();
		String timeStamp = DateUtil.format(currDate, "yyyyMMddHHmmss");
		
		log.info(">>>>>>>>>>>>>>Check file:{file name = "+accFile.getAccFileName()+"}");
		String resultPath = accountRecvDirBackUp + accFile.getAccFileName();
		File stlFile = new File(resultPath);
		int waitCount = 0;
		
		//等待文件传输，最多等待6分钟，如果6分钟没传完，则第二天再执行此文件
		while (!stlFile.exists() && waitCount <5)     
		{
			try 
			{
				Thread.sleep(1000 * 60 * 1);
				waitCount ++;
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
		if(!stlFile.exists())
		{
			//文件还没传输到核心，暂不执行批量记账操作
			log.info(">>>>>>>>>>>>>>>>>>>>file : " + accFile.getAccFileName() + "未传输到核心！");
			return false;
		}
		
		//平台日期
		String sysDate = DateUtil.format(currDate, "yyyyMMdd");
		
		String currTime = timeStamp;
		
		SoaParams params = new SoaParams();
		SoaResults result;
		
		try
		{
			//调用CH1730
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>invoke CH1730 to deal file : " + accFile.getAccFileName());
			params = IBankMsg.PagyFeeClearing(params, accFile.getAccFileName());
			log.info("CH1730接口参数:" + params);
			log.info(">>>>>>>>>>>>>>>>>>>>invoking CH1730...");
			result = clearingSoaService.clearingSoa(params);
			log.info("调用CH1730接口参数返回的报文>>>>>>>>>>>>"+result);
			if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode"))) {
				throw new IfspBizException(RespConstans.RESP_FAIL.getCode(),
						RespConstans.RESP_FAIL.getDesc());
			}

			if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
					RespConstans.RESP_SUCCESS.getCode())) 
			{
				log.info("=====================调用CH1730失败================================");
				accFile.setDealStatus(Constans.FILE_STATUS_03);   //03-核心处理失败
				bthBatchAccountFileDao.updateByPrimaryKeySelective(accFile);
			} 
			else 
			{
				log.info("=====================调用CH1730成功================================");
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);
				
				accFile.setBkFileName(accountRecvDir + accFile.getAccFileName() + ".dow.ok");
				accFile.setUpdateDate(timeStamp);
				
				accFile.setDealStatus(Constans.FILE_STATUS_02);   //02-核心处理成功
				bthBatchAccountFileDao.updateByPrimaryKeySelective(accFile);
			}
		} 
		catch (Exception e)
		{
			log.error("Call ESB CH1730 failed, error msg is :{"+e.getMessage()+"}");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
