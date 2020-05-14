package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.common.utils.EncryptUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.common.utils.SftpUtil;
import com.scrcu.ebank.ebap.batch.service.GenMerStlFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenMerStlFileServiceImpl implements GenMerStlFileService
{
	private String batchDate;
	private String txnDate;

	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;
	@Resource
	private BthMerInAccDao bthMerInAccDao;

	@Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           //商户基本信息

    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息
    @Resource
    private PaySubOrderInfoDao paySubOrderInfoDao;     //子订单表
    @Resource
    private MchtOrgRelDao mchtOrgRelDao;               //商户组织关联表

	@Value("${merChkFilePath}")
    private String merChkFilePath;

    @Value("${dmzFtpHost}")
    private String dmzFtpHost;

    @Value("${dmzFtpPort}")
    private String dmzFtpPort;

    @Value("${dmzFtpUserName}")
    private String dmzFtpUserName;

    @Value("${dmzFtpPwd}")
    private String dmzFtpPassword;

    @Value("${dmzFtpPath}")
    private String dmzFtpPath;

    @Value("${jsFilePath}")
    private String jsFilePath;


	@Override
	public CommonResponse genMerStlFile(BatchRequest request)
	{
		String batchDate = request.getSettleDate();
    	if(IfspDataVerifyUtil.isBlank(batchDate))
    	{
    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
    		txnDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
    	}

		this.setBatchDate(batchDate);
		this.setTxnDate(DateUtil.format(DateUtil.getDiffStringDate(DateUtil.parse(batchDate, "yyyyMMdd"), -1), "yyyyMMdd"));

		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>genMerStlFile service of "+this.batchDate+" executing ");

		//1)生成平台商户对账文件
		List<String> platMerList = this.getPlatMerList();
		for(String platMerNo : platMerList)
		{
			List<BthMerInAccDtl> stlOrderList = this.getDataList(platMerNo, Constans.MER_LEVLE_00);
			if(stlOrderList.size() == 0)
			{
				log.info(">>>>>>>>>>>>>>>>>>>>plat mer : " + platMerNo+ " has no data @"+this.getBatchDate());
				continue;
			}
			String stlmDate=stlOrderList.get(0).getStlmDate();
			log.info(">>>>>>>>>>>>>>>>>>>>gen settle file for plat mer : " + platMerNo);

			//对账文件存放路径
			String remoteFileName = "";
			StringBuffer stlFile = new StringBuffer(merChkFilePath);
			stlFile.append(txnDate).append("/").append(platMerNo).append("/");
			stlFile.append(platMerNo).append("-").append(txnDate).append("stlfile.txt");
			remoteFileName =platMerNo+"-"+txnDate+"stlfile.txt";

			//统计首行信息
			BthMerInAccDtl merInAcc = this.getFirstLineData(platMerNo, Constans.MER_LEVLE_00);
			int totalRow = Integer.parseInt(merInAcc.getRowCount());
			String totalAmount = merInAcc.getTxnAmt();
			String firstLine = this.genFirstLine(totalRow, totalAmount);
			BthMerInAcc bthMerInAcc=bthMerInAccDao.otherSetlFeeSumInfo(platMerNo,stlmDate);
			if(bthMerInAcc!=null){
				String otherSetlFee=bthMerInAcc.getOtherSetlFee();
				if(IfspDataVerifyUtil.isNotBlank(otherSetlFee)){
					if(!"0".equals(otherSetlFee)){
						StringBuffer othStr = new StringBuffer(""+otherSetlFee);
						while(othStr.length() < 12)
						{
							othStr.insert(0, "0");
						}
						firstLine=firstLine+othStr.toString()+"|";
					}
				}
			}

			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate settle file:"+stlFile.toString());
			this.writeFileFirstLine(stlFile.toString(), firstLine);

			//写文件
			FileUtil.write(stlFile.toString(), stlOrderList, true, null, null,true,"2");

			String fullName = stlFile.toString();
			File f = new File(fullName);

			String localDir = f.getParent();
			String shortName = f.getName();
			//将文件ftp到dmz区
			try
			{
				String dmzJsFilePath =  this.dmzFtpPath.replace("merId", platMerNo)  + this.jsFilePath;

				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>uploading file : " + shortName);
				SftpUtil.ftpUploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword,
						remoteFileName, dmzJsFilePath, fullName,localDir);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>upload file : " + shortName + " completed!");

				//FTP上传商户结算文件
				//FtpUtil.uploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, "", dmzJsFilePath+shortName, fullName);
			}
			catch(Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>>>ftp upload settle file to dmz zone failed!<<<<<<<<<<<<<<<<<<<<<<<<");
				e.printStackTrace();
			}

		}

		//2)生成商城商户对账文件
		List<String> merList = this.getMerList();
		for(String merNo : merList)
		{
			List<BthMerInAccDtl> stlOrderList = this.getDataList(merNo, Constans.MER_LEVLE_01);

			if(stlOrderList.size() == 0)
			{
				log.info(">>>>>>>>>>>>>>>>>>>>normal mer : " + merNo+ " has no data @"+this.getBatchDate());
				continue;
			}
			String stlmDate=stlOrderList.get(0).getStlmDate();
			log.info(">>>>>>>>>>>>>>>>>>>>gen settle file for normal mer : " + merNo);

			//对账文件存放路径
			String remoteFileName = "";
			StringBuffer stlFile = new StringBuffer(merChkFilePath);
			stlFile.append(txnDate).append("/").append(merNo).append("/");
			stlFile.append(merNo).append("-").append(txnDate).append("stlfile.txt");
			remoteFileName =merNo+"-"+txnDate+"stlfile.txt";
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate settle file:"+stlFile.toString());

			//统计首行信息
			BthMerInAccDtl merInAcc = this.getFirstLineData(merNo, Constans.MER_LEVLE_01);
			int totalRow = Integer.parseInt(merInAcc.getRowCount());
			String totalAmount = merInAcc.getTxnAmt();
			String firstLine = this.genFirstLine(totalRow, totalAmount);

			BthMerInAcc bthMerInAcc=bthMerInAccDao.otherSetlFeeSumInfo(merNo,stlmDate);
			if(bthMerInAcc!=null){
				String otherSetlFee=bthMerInAcc.getOtherSetlFee();
				if(IfspDataVerifyUtil.isNotBlank(otherSetlFee)){
					if(!"0".equals(otherSetlFee)){
						StringBuffer othStr = new StringBuffer(""+otherSetlFee);
						while(othStr.length() < 12)
						{
							othStr.insert(0, "0");
						}
						firstLine=firstLine+othStr.toString()+"|";
					}
				}
			}

			this.writeFileFirstLine(stlFile.toString(), firstLine);

			//写文件
			FileUtil.write(stlFile.toString(), stlOrderList, true, null, null,true,"2");

			String fullName = stlFile.toString();
			File f = new File(fullName);
			String shortName = f.getName();
			String localDir = f.getParent();

			//将文件ftp到dmz区
			try
			{
				String dmzJsFilePath =  this.dmzFtpPath.replace("merId", merNo)  + this.jsFilePath;
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> remote path : " + dmzJsFilePath);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> local path : " + localDir);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> local file name : " + fullName);

				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>uploading file : " + shortName);
				SftpUtil.ftpUploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword,
						remoteFileName, dmzJsFilePath, fullName,localDir);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>upload file : " + shortName + " completed!");

				//FTP上传商户结算文件
				//FtpUtil.uploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, "", dmzJsFilePath+shortName, fullName);
			}
			catch(Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>>>ftp upload settle file to dmz zone failed!<<<<<<<<<<<<<<<<<<<<<<<<");
				e.printStackTrace();
			}
		}

		//应答
		CommonResponse commonResponse = new CommonResponse();

		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

		return commonResponse;
	}

	public int getTotalResult()
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("stlResult", Constans.SETTLE_STATUS_SUCCESS_CLEARING);
		params.put("updateDate", batchDate+"%");
		count = bthMerInAccDtlDao.count("countStlSuccOrderOfCurrDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>order-count-num  update @"+this.getBatchDate()+ " is : " +count);
		return count;
	}

	/**
	 * 根据商户号查询商户订单信息
	 * @param merNo ： 商户号
	 * @param merType ：商户类型
	 * @return
	 */
	public List<BthMerInAccDtl> getDataList(String merNo,String merType)
	{
		Map<String,Object> params = new HashMap<String,Object>();

		params.put("updateDate", this.getBatchDate()+"%");
		params.put("stlResult", Constans.SETTLE_STATUS_SUCCESS_CLEARING);
		List<BthMerInAccDtl> inAccDtlList = null;
		if(Constans.MER_LEVLE_00.equals(merType))
		{
			//查询平台商户订单数据
			params.put("platMerNo", merNo);
			inAccDtlList = bthMerInAccDtlDao.selectList("selectStlDataByPlatMerNo", params);
		}
		else
		{
			//查询一级商户订单数据
			params.put("merNo", merNo);
			inAccDtlList = bthMerInAccDtlDao.selectList("selectStlDataByMerNo", params);
		}
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>inAccDtlList size of merchant :" + merNo + " @" + this.batchDate +" is " + inAccDtlList.size());
		return inAccDtlList;
	}


	/**
	 * 将首行内容写入文件
	 * @param fileName ： 文件名
	 * @param content ：文件首行内容
	 */
	private void writeFileFirstLine(String fileName,String content)
	{
		File file = new File(fileName);

		if(!file.exists())
		{
			try
			{
				if (!file.getParentFile().exists())
				{
					file.getParentFile().mkdirs();
	            }
				file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}


		try(FileOutputStream fos = new FileOutputStream(fileName, false);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, "utf-8");
				BufferedWriter bw = new BufferedWriter(outputStreamWriter)){
			bw.write(content);
			bw.write(System.getProperty("line.separator"));
		}catch (IOException e){
			e.printStackTrace();
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(BthMerInAccDtl inAccDtl)
	{
	}

	public void updateObject()
	{
		//更新二级商户结算状态
		log.info(">>>>>>>>>>>>>>>>>>>syncStlStatus @"+this.getBatchDate() + "completed..");
	}

	//查询所有一级商户
	public List<String> getMerList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("orgType", Constans.ORG_TYPE_PLAT);
		// for forstlfile 增加查询条件
		params.put("updateDate", this.getBatchDate()+"%");
		params.put("stlResult", Constans.SETTLE_STATUS_SUCCESS_CLEARING);

		// for forstlfile 增加查询条件,减少结果集
		List<String> merList = mchtOrgRelDao.selectMchtNoList("selectNonPlatMerNoListForStlFile", params);

		return merList;
	}

	//查询所有平台商户
	public List<String> getPlatMerList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("orgType", Constans.ORG_TYPE_PLAT);

		List<String> platMerList = mchtOrgRelDao.selectMchtNoList("selectPlatMerNoList", params);

		return platMerList;
	}

	public BthMerInAccDtl getFirstLineData(String merNo,String merType)
	{
		Map<String,Object> params = new HashMap<String,Object>();

		params.put("createDate", this.getBatchDate());
		params.put("updateDate", this.getBatchDate()+'%');
		params.put("stlStatus", Constans.SETTLE_STATUS_SUCCESS_CLEARING);

		BthMerInAccDtl inAccDtl = null;
		if(Constans.MER_LEVLE_00.equals(merType))
		{
			//查询平台商户订单数据
			params.put("platMerNo", merNo);
			inAccDtl = bthMerInAccDtlDao.selectOne("selectStlFileCountDataByPlatMerNo", params);
		}
		else
		{
			//查询一级商户订单数据
			params.put("merNo", merNo);
			inAccDtl = bthMerInAccDtlDao.selectOne("selectStlFileCountDataByMerNo", params);
		}
		return inAccDtl;
	}

	/**
	 * 拼接首行内容
	 * @param rowCount ： 总行数
	 * @param totalAmt ：总金额
	 * @return ： 首行内容
	 */
	private String genFirstLine(int rowCount,String totalAmt)
	{
		String firstLineContent = "";
		StringBuffer rowCountStr = new StringBuffer(""+rowCount);
		while(rowCountStr.length() < 12)
		{
			rowCountStr.insert(0, "0");
		}

		StringBuffer iAmtStr = new StringBuffer(""+totalAmt);
		while(iAmtStr.length() < 12)
		{
			iAmtStr.insert(0, "0");
		}

		firstLineContent = rowCountStr.append("|").append(iAmtStr).append("|").
				append("000000000000").append("|").append("000000000000").append("|").toString();

		return firstLineContent;
	}

	public String getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}

	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}


}
