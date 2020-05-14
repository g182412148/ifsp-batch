package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.dao.BthBatchAccountFileDao;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.batch.service.GenAccFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GenAccFileServiceImpl implements GenAccFileService 
{
	@Resource
    private BthMerInAccDao bthMerInAccDao;
	
	private static BigDecimal ZERO = new BigDecimal(0);

    @Resource
    private BthBatchAccountFileDao bthBatchAccountFileDao;
    
	@Value("${inacctFilePath}")
    private String inacctFilePath;
	
	@Value("${accountRecvDir}")
    private String accountRecvDir;
	
	private String fileName;
	
	@Override
	public CommonResponse genAccFile(String accType) 
	{
		Date currDate = new Date();
		fileName = FileUtil.genFileName("S");
		
		List<BthMerInAcc> merInAccList = getDataList(accType);
		log.info(">>>>>>>>>>>>>>>>generate acccount file of Type : "+accType + " @date "+DateUtil.format(currDate, "yyyyMMdd")+
				"file name : " + fileName);
		
		
		for(BthMerInAcc accInfo : merInAccList)
		{
			String tranOrg = accInfo.getOutAcctNoOrg();
			if(tranOrg == null || "".equals(tranOrg))
			{
				tranOrg = accInfo.getInAcctNoOrg();
			}
			if(tranOrg == null || "".equals(tranOrg))
			{
				tranOrg = "9996";
			}
			
			//设置交易机构
			accInfo.setTranOrg(tranOrg);
			
			accInfo.setTransCur(Constans.TXN_CUR_CNY);     //交易币种
			accInfo.setBillFlag(Constans.TXN_BILL_FLAG);   //钞汇标志
			accInfo.setSummaryCode(Constans.TXN_SUMMARY_CODE);  //交易摘要码
			
			if(Constans.IN_ACCT_TYPE_TRANSFER.equals(accType))
			{
				accInfo.setSummary(Constans.TXN_SUMMARY_TRANSFER);
			}
			else if(Constans.IN_ACCT_TYPE_SUBMER.equals(accType))
			{
				accInfo.setSummary(Constans.TXN_SUMMARY_SUBMER);
			}
			else
			{
				accInfo.setSummary(Constans.TXN_SUMMARY_OTHER);
			}
		}
		
		if(merInAccList != null && merInAccList.size() > 0)
		{
			String fullName = inacctFilePath + File.separator + fileName;
			FileUtil.write(fullName, merInAccList, true, null, null,true);
			
			//创建“.ok”文件
			String okFileName = fullName+".ok";
			FileUtil.createFile(okFileName);
			
			//将文件信息插入到批量文件信息表
			this.recordBatchFileInfo(fileName, accountRecvDir, null, merInAccList.size(), ZERO,accType);
			
			//更新记录为处理中
			this.updateObject(accType);
		}
		else
		{
			log.info(">>>>>>>>>>>>>>>>>>no data  of Type : "+accType +" to stl @date "+DateUtil.format(currDate, "yyyyMMdd"));
		}
		
		
		//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
		return commonResponse;
	}
	
	private List<BthMerInAcc> getDataList(String accType)
	{
		List<BthMerInAcc> dataList = new ArrayList<BthMerInAcc>();
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("inAccType", accType);
		
		if(Constans.IN_ACCT_TYPE_TRANSFER.equals(accType))
		{
			//转账只针对未处理数据
			parameter.put("handleState", Constans.HANDLE_STATE_PRE);
			List<BthMerInAcc> transferList = bthMerInAccDao.selectList("selectAccInfoByAccType",parameter);
			dataList = transferList;
		}
		else
		{
			//二级商户隔日间接结算包括0-未处理理数据与3-处理失败数据
			parameter.put("handleState", Constans.HANDLE_STATE_PRE);
			List<BthMerInAcc> unHandleList = bthMerInAccDao.selectList("selectAccInfoByAccType",parameter);
			
			parameter.put("handleState", Constans.HANDLE_STATE_FAIL);
			List<BthMerInAcc> failList = bthMerInAccDao.selectList("selectAccInfoByAccType",parameter);
			dataList.addAll(unHandleList);
			dataList.addAll(failList);
		}
		
		
		return dataList;
	}
	
	//更新入账记录状态为处理中
	public void updateObject(String accType)
	{
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("inAccType", accType);
		parameter.put("handleMark", Constans.DEAL_RESULT_DESC_DEALING);
		
		
		if(Constans.IN_ACCT_TYPE_TRANSFER.equals(accType))
		{
			parameter.put("handleState", Constans.HANDLE_STATE_PRE);
			
			//更新未处理的转账记录为处理中
			bthMerInAccDao.update("updateAccInfoByAccTypeOfTransfer", parameter);
		}
		else
		{
			parameter.put("handleState1", Constans.HANDLE_STATE_PRE);
			parameter.put("handleState2", Constans.HANDLE_STATE_FAIL);
			
			//更新未处理及处理失败的隔日间接记录为处理中
			bthMerInAccDao.update("updateAccInfoByAccTypeOfSubMerStl", parameter);
		}
	}
	
	/**
	 * 将文件信息记录到记账文件控制表
	 * 
	 * @param accFileName ：记账文件名
	 * @param batchNo　：　批次号
	 * @param rowCt　：　文件总行数
	 * @param fileAmt　：　文件总金额
	 */
	private void recordBatchFileInfo(String accFileName,String resultPath, String batchNo,int rowCt, BigDecimal fileAmt,String accType)
	{
		BthBatchAccountFile accFileInfo = new BthBatchAccountFile();
		
		Date d = new Date();
		accFileInfo.setAccDate(DateUtil.format(d, "yyyyMMdd"));
		accFileInfo.setAccFileName(accFileName);
		int startIndex = accFileName.lastIndexOf(File.separator);
		String resultFileNm = accFileName.substring(startIndex+1)+".dow";    //反馈文件
		resultFileNm = resultFileNm.trim();
		accFileInfo.setBkFileName(resultPath+resultFileNm);
		
		accFileInfo.setCreateDate(DateUtil.format(d, "yyyyMMddHHmmss"));
		accFileInfo.setUpdateDate(DateUtil.format(d, "yyyyMMddHHmmss"));
		
		accFileInfo.setBatchNo(batchNo);
		
		accFileInfo.setDealStatus("00");
		accFileInfo.setGenFileClass(this.getClass().getName());
		
		//设置文件总行数，总金额
		accFileInfo.setFileCount(""+rowCt);
		accFileInfo.setFileAmt(fileAmt);
		
		if(Constans.IN_ACCT_TYPE_TRANSFER.equals(accType))
		{
			accFileInfo.setFileType(Constans.FILE_TYPE_TRANSFER);
		}
		else
		{
			accFileInfo.setFileType(Constans.FILE_TYPE_SUBMER_STL);
		}
		
		accFileInfo.setId(IfspId.getUUID(32));
		
		bthBatchAccountFileDao.insertSelective(accFileInfo);
	}

}
