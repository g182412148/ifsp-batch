package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthTransferInfo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.dao.BthBatchAccountFileDao;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.BthSetCapitalDetailDao;
import com.scrcu.ebank.ebap.batch.dao.BthTransferInfoDao;
import com.scrcu.ebank.ebap.batch.service.GetAccBkFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GetAccBkFileServiceImpl implements GetAccBkFileService 
{
	@Resource
    private BthMerInAccDao bthMerInAccDao;
	
	@Resource
	private BthTransferInfoDao bthTransferInfoDao;
	
	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;
	
	@Resource
    private BthBatchAccountFileDao bthBatchAccountFileDao;
	
	@Resource
	private BthSetCapitalDetailDao bthSetCapitalDetailDao;
	
	private Date sysDate;
	
	@Value("${accountRecvDir}")
	private String accountBkReceivePath;           //记账反馈本地文件
	
	@Value("${accountRecvDirBackUp}")
	private String accountBkFileBackupPath;        //记账反馈文件备份路径
	
	@Override
	public CommonResponse getAccBkFile(String fileType) 
	{
		log.info(">>>>>>>>>>>>>>>>>>getAccBkFile service 获取核心记账反馈文件步骤开始>>>>>>>>>>>>>>>>>>>>>>>>");
		this.setSysDate(new Date());
		List<BthBatchAccountFile> fileList = this.getFileList(fileType);
		
		for(BthBatchAccountFile accFile : fileList)
		{
			int count = this.getTotalResult(accFile);
			List<BthMerInAcc> merInAccList = this.getDataList(0, count, accFile);
			for(BthMerInAcc merInAcc : merInAccList)
			{
				//根据核心记账结果更新商户入账表状态
				this.execute(merInAcc,fileType);
			}
			
			//备份文件，更新记账文件表状态
			this.updateObject(accFile,fileType);
		}
		return null;
	}

	public List<BthBatchAccountFile> getFileList(String fileType)
	{
		List<BthBatchAccountFile> fileList = new ArrayList<BthBatchAccountFile>();
		
		Map<String,Object> map = new HashMap<>();
	    map.put("dealStatus",Constans.FILE_STATUS_02);
	    map.put("fileType",fileType);

	    fileList = bthBatchAccountFileDao.selectList("accF_selectByState",map);  
		
		if(fileList.size() == 0)
		{
			log.info(">>>>>>>>>>>>>>>>>>暂无核心正在处理的批量文件!!!");
			return fileList;
		}
		
		int waitCount = 0;
		boolean recvFlag = false;
		while (!recvFlag && waitCount <30)     
		{
			try 
			{
				Thread.sleep(1000 * 60 * 1);
				recvFlag = true;
				for(BthBatchAccountFile accFile : fileList)
				{
					File bkFile = new File(accFile.getBkFileName());
					//只要有一个文件没返回就继续等待
					if(!bkFile.exists())
					{
						recvFlag = false;
					}
				}
				
				waitCount ++;
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
		return fileList;
	}
	
	public int getTotalResult(BthBatchAccountFile accFile)
	{
		log.info(">>>>>>>>>>>>>calc row count of account file : "+accFile.getAccFileName());
		//判断文件是否存在，
		String okfileName = accFile.getBkFileName();
		File okFile = new File(okfileName);
		if(!okFile.exists())
		{
			return -1;
		}
		
		String dowFileName = okfileName.substring(0, okfileName.length() - 3);
		
		int counts = 0;
		
		counts = (int)FileUtil.getFileRowCount(dowFileName, BthMerInAcc.class);
		
		log.info(">>>>>>>>>>>>>{fileName :"+dowFileName+",row count:"+counts+"}");
		
		return counts ;
	}
	
	public List<BthMerInAcc> getDataList(int offset, int pageSize, BthBatchAccountFile accFile)
	{
		String fileName = accFile.getBkFileName();
		fileName = fileName.substring(0,fileName.length() - 3);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>read file to list:{file name = "+fileName+"}");
		//fileName = "C:\\Users\\ydl\\Desktop\\test\\RPS052S20181019164919990.dow";
		// 分批取数据文件记录
        List<BthMerInAcc> dataList =
                FileUtil.readFileToList(fileName, BthMerInAcc.class, offset, pageSize);
		
        log.info(">>>>>>>>>>>>>>>>>>>>>>>dataList size : "+dataList.size());
		return dataList ;
	}
	
	
	public void execute(BthMerInAcc capitalSummary,String fileType)
	{
		String timeStamp = DateUtil.format(this.getSysDate(), "yyyyMMddHHmmss");
		
		BthMerInAcc merInAcc = new BthMerInAcc();
		
		//1)根据核心结果更新商户入账汇总表状态
		if(Constans.CORE_RETURN_CODE_SUCCESS.equals(capitalSummary.getDealResultCode()))
		{
			merInAcc.setHandleState(Constans.HANDLE_STATE_SUCC);
			merInAcc.setHandleMark(Constans.DEAL_RESULT_DESC_SUCC);
			merInAcc.setInAcctAmt(timeStamp);
			merInAcc.setInAcctStat(Constans.IN_ACC_STAT_SUCC);
		}
		else
		{
			merInAcc.setHandleState(Constans.HANDLE_STATE_TRANSFER_FAIL);
			merInAcc.setHandleMark(Constans.DEAL_RESULT_DESC_TRANSFER_FAIL);
			merInAcc.setInAcctStat(Constans.IN_ACC_STAT_FAIL);
			merInAcc.setStatMark(capitalSummary.getDealResultRemark());
		}
		
		merInAcc.setTxnSsn(capitalSummary.getTxnSsn());
		
		this.bthMerInAccDao.update("updateByTxnSsn",merInAcc);
		
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("txnSsn", capitalSummary.getTxnSsn());
		
		//2)更新商户入账明细表状态
		if(Constans.FILE_TYPE_TRANSFER.equals(fileType))
		{
			//更新转账表状态
			BthTransferInfo transferInfo = new BthTransferInfo();
			transferInfo.setId(capitalSummary.getTxnSsn());
			transferInfo.setUpdateDate(DateUtil.format(this.getSysDate(), "yyyyMMddHHmmss"));
			if(Constans.CORE_RETURN_CODE_SUCCESS.equals(capitalSummary.getDealResultCode()))
			{
				transferInfo.setDealStatus(Constans.DEAL_RESULT_SUCCESS);     //转账成功
				transferInfo.setDealSuccessTime(DateUtil.format(this.getSysDate(), "yyyyMMddHHmmss"));
				transferInfo.setDealResultRemark(Constans.DEAL_RESULT_DESC_SUCC);
			}
			else
			{
				transferInfo.setDealStatus(Constans.DEAL_RESULT_FAILE);
				transferInfo.setDealResultRemark(Constans.DEAL_RESULT_DESC_TRANSFER_FAIL);
			}
			bthTransferInfoDao.updateByPrimaryKeySelective(transferInfo);
		}
		else
		{
			BthMerInAcc merInAcc2 = bthMerInAccDao.selectOne("selectMerInAccByTxnSsn", param);
			
			//更新清分表状态
			Map<String,Object> iparam = new HashMap<String,Object>();
			//条件
			iparam.put("batchNo", merInAcc2.getBatchNo());
			iparam.put("inAccountNo", merInAcc2.getInAcctNo());
			iparam.put("outAccountNo", merInAcc2.getOutAcctNo());
			iparam.put("entryType", Constans.ENTRY_TYPE_SUBMER_IN);    //二级商户隔日间接入账
			
			//结果
			if(Constans.CORE_RETURN_CODE_SUCCESS.equals(capitalSummary.getDealResultCode()))
			{
				iparam.put("dealResult", Constans.DEAL_RESULT_SUCCESS);  
				iparam.put("dealRemark", Constans.ACC_RESULT_DESC_SUCC);
				iparam.put("accountStauts", Constans.ACCOUNT_STATUS_SUCCESS);   
			}
			else
			{
				iparam.put("dealResult", Constans.DEAL_RESULT_FAILE);    
				iparam.put("dealRemark", capitalSummary.getDealResultRemark());
				iparam.put("accountStauts", Constans.ACCOUNT_STATUS_FAILE);   
			}
			iparam.put("updateDate", DateUtil.format(this.getSysDate(), "yyyyMMddHHmmss"));
			
			bthSetCapitalDetailDao.update("updateSubMerStlStatusByBatchNo", iparam);
		}
		
	}

	public void updateObject(BthBatchAccountFile accFile,String fileType) 
	{
		//备份文件
		if(accFile != null)
		{
			log.info(">>>>>>>>>>>>>>>>>>>>>>>backup file....");
			//更新记账文件处理状态
			accFile.setDealStatus(Constans.FILE_STATUS_04);
			accFile.setUpdateDate(DateUtil.format(this.getSysDate(), "yyyyMMddHHmmss"));
			this.bthBatchAccountFileDao.update("updateBatchAccFileDealStatus",accFile);
			
			String okfileName = accFile.getBkFileName();
			String dowFileName = okfileName.substring(0, okfileName.length() - 3);
			File dowFile = new File(dowFileName);
			File okFile = new File(okfileName);
			File resultPath = new File(this.accountBkFileBackupPath);
			if(!resultPath.exists())
			{
				resultPath.mkdirs();
			}
			
			File resultFile = new File(this.accountBkFileBackupPath+dowFile.getName());
			File okResultFile = new File(this.accountBkFileBackupPath+okFile.getName());
			
			dowFile.renameTo(resultFile);
			okFile.renameTo(okResultFile);
		}
		
		
		log.info(">>>>>>>>>>>>>>>步骤=获取核心记账反馈文件  处理结束...................") ;
	}

	public Date getSysDate() {
		return sysDate;
	}

	public void setSysDate(Date sysDate) {
		this.sysDate = sysDate;
	}
	
	
}
