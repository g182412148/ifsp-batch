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

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.DebitTranInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FtpUtil;
import com.scrcu.ebank.ebap.batch.common.utils.SftpUtil;
import com.scrcu.ebank.ebap.batch.dao.DebitTranInfoDao;
import com.scrcu.ebank.ebap.batch.service.CreditPayChkService;
import com.scrcu.ebank.ebap.batch.soaclient.CreditChkSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreditPayChkServiceImpl implements CreditPayChkService 
{
	@Resource
	private DebitTranInfoDao debitTranInfoDao;
	
	@Resource
	private CreditChkSoaService creditChkSoaService;
	
	@Value("${creditBillPath}")
	private String creditBillPath;               //信用卡对账单地址
	
	@Value("${creditBillBkPath}")
	private String creditBillBkPath;             //信用卡对账单备份地址
	
	@Value("${creditFtpHost}")
	private String host;					    // FTP服务器ip
	
	@Value("${creditFtpPort}")
	private int port;						    // FTP服务器端口
	
	@Value("${creditFtpUserName}")
	private String username;				    // FTP服务器用户名
	
	@Value("${creditFtpPwd}")
	private String password;				    // FTP服务器密码
	
	@Value("${creditFileFtpPath}")
	private String remoteFilepath;				// FTP服务器路径
	
	
	@Override
	//信用卡值对账单下载
	public CommonResponse creditPayChk(BatchRequest request) throws Exception 
	{
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>wxBillDownload service executing");
		String preDate = request.getSettleDate();         //对账日期
    	if(IfspDataVerifyUtil.isBlank(preDate))
    	{
    		preDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
    	}
    	log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>对账日期 : " + preDate);
    	
    	//1.根据清算日期和通道编号删除微信对账文件明细表(BTH_WX_FILE_DET)
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("srcType", Constans.WX_ORDER_TYPE_DIRECT);
		params.put("settleDate", preDate);
		params.put("pagyNo", Constans.WX_SYS_NO+" ");
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>删除表数据:");
		
		debitTranInfoDao.delete("deleteBySrcTypeAndDate", params);
		
		SoaParams soaParams = new SoaParams();
		
		String fileName = "";
		fileName = FileUtil.genFileName("CC");
		
		Map<String,String> customBase = new HashMap<String,String>(); 
		customBase.put("quota", "EBAP001");    //对账分类编码: 分批次对账时必输 当前系统在实时交易时送的固定值EBAP001
		customBase.put("chkDate", preDate);
		customBase.put("fileName", fileName);
		customBase.put("procFlag", "0");      //同步异步标志
		customBase.put("operFlag", "0");      //操作标志
		
		soaParams.put("customBase", customBase);
		soaParams.put("getPagyPayTxnSsn", IfspId.getId32UpperCase());
		
		SoaResults result;
		//2.请求贷记卡对账文件
		result = creditChkSoaService.downloadCreditBill(soaParams);
		
		Map<Object, Object> datas = result.getDatas();
		if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode"))) 
		{
			throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		}
		
		if (!IfspDataVerifyUtil.equals((String) result.get("respCode"), RespConstans.RESP_SUCCESS.getCode())) 
		{
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>downloadCreditBill fail, response code is : " +  result.get("respCode"));
			throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		} 
		else 
		{
			log.info("===================="+datas+"=========================");
			log.info("=====================成功================================");
		}
		String creditFilePath=creditBillPath;
		
		String localFilePath = creditFilePath + fileName;                 //本地存放路径 
		log.info(">>>>>>>>>>>>>>>>>>>>>>>本地存放路径为:" + localFilePath);
		
		try
		{
			Thread.sleep(1000*60*5);
			FtpUtil.downloadFile(host, port, username, password, "", remoteFilepath+fileName, localFilePath);
			//SftpUtil.ftpDownloadFile(host, port, username, password, fileName, remoteFilepath, fileName, creditBillPath);
		}
		catch(Exception e)
		{
			log.error("<<<<<<<<<<<<<<<<<<<<<<<<<下载对账文件失败>>>>>>>>>>>>>>>>>>>/n");
			log.error(e.getMessage());
			return null;
		}
		
		//3.下载对账单
		File creditBillFile = new File(localFilePath);
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>file check...");
		//本地存在直接读取，不存在中断
		if(!creditBillFile.exists())
		{
			log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>对账文件不存在："+localFilePath);
			CommonResponse commonResponse = new CommonResponse();
			
			commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
			commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
			
			return commonResponse;
		}
		int rowCount = 0;
		//4.解析文件
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>解析贷记卡对账单开始,fileName :{"+fileName+"}");
        try 
        {
        	//解析文件
        	rowCount = (int)FileUtil.getFileRowCount(localFilePath, DebitTranInfo.class);
        	List<DebitTranInfo> dataList = FileUtil.readFileToList(localFilePath, DebitTranInfo.class, 0, rowCount);
        	
        	int count = 0;
        	for(DebitTranInfo order : dataList)
        	{
        		order.setPagySysNo(Constans.IBANK_SYS_NO); 	// 通道系统编号 PAGY_SYS_NO
        		order.setPagyNo("604000000000001");		    // 通道编号 PAGY_NO
        		order.setChkDataDt(preDate);                 // 对账日期 CHK_DATA_DT
        		order.setChkAcctSt("00");                       // 对账状态 CHK_ACCT_ST
        		order.setChkRst(""); // 对账结果 CHK_RST
        		order.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS()); // 最后更新时间 LST_UPD_TM
        	}
        	count = debitTranInfoDao.insertSelectiveList(dataList);
        	log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>贷记卡订单数@"+preDate + " is : "+dataList.size());
        }
        catch (Exception e)
        {
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException("解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
        
        
        //文件备份
        File resultFile = new File(this.creditBillBkPath+fileName);
        creditBillFile.renameTo(resultFile);
		
		//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
		return commonResponse;
	}

}
