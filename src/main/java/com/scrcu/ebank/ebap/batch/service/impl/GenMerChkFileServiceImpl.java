package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.scrcu.ebank.ebap.batch.common.utils.EncryptUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.common.utils.SftpUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtOrgRelDao;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.GenMerChkFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenMerChkFileServiceImpl implements GenMerChkFileService {
	private String batchDate;
	private String txnDate;

	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;

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
	public CommonResponse genMerChkFile(BatchRequest request) {
		String batchDate = request.getSettleDate();
		if (IfspDataVerifyUtil.isBlank(batchDate)) {
			batchDate = DateUtil.format(new Date(), "yyyyMMdd");
			txnDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
		}

		this.setBatchDate(batchDate);
		this.setTxnDate(DateUtil.format(DateUtil.getDiffStringDate(DateUtil.parse(batchDate, "yyyyMMdd"), -1), "yyyyMMdd"));

		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>genMerChkFile service of " + this.batchDate + " executing ");

		//1)生成平台商户对账文件
		List<String> platMerList = this.getPlatMerList();
		for (String platMerNo : platMerList) {
			List<BthMerInAccDtl> chkOrderList = this.getDataList(platMerNo, Constans.MER_LEVLE_00);
			if (chkOrderList.size() == 0) {
				log.info(">>>>>>>>>>>>>>>>>>>>plat mer : " + platMerNo + " has no data @" + this.getBatchDate());
				continue;
			}

			log.info(">>>>>>>>>>>>>>>>>>>>gen check file for plat mer : " + platMerNo);

			//对账文件存放路径
			String remoteFileName = "";
			StringBuffer checkFile = new StringBuffer(merChkFilePath);
			checkFile.append(txnDate).append("/").append(platMerNo).append("/");
			checkFile.append(platMerNo).append("-").append(txnDate).append("checkfile.txt");
			remoteFileName = platMerNo + "-" + txnDate + "checkfile.txt";

			//统计首行信息
			BthMerInAccDtl merInAcc = this.getFirstLineData(platMerNo, Constans.MER_LEVLE_00);
			int totalRow = Integer.parseInt(merInAcc.getRowCount());
			String totalAmount = merInAcc.getTxnAmt();
			String firstLine = this.genFirstLine(totalRow, totalAmount);

			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate check file:" + checkFile.toString());
			this.writeFileFirstLine(checkFile.toString(), firstLine);

			//写文件
			FileUtil.write(checkFile.toString(), chkOrderList, true, null, null, true);

			//将文件ftp到dmz区
			try {
				String fullName = checkFile.toString();
				File apprFile = new File(fullName);    //方便测试..

				String localDir = apprFile.getParent();
				String shortName = apprFile.getName();

				String dmzJsFilePath = this.dmzFtpPath.replace("merId", platMerNo) + this.jsFilePath;

				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>uploading file : " + shortName);
				SftpUtil.ftpUploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword,
						remoteFileName, dmzJsFilePath, fullName, localDir);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>upload file : " + shortName + " completed!");
				//FTP上传商户对账文件
				//FtpUtil.uploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, "", dmzJsFilePath+shortName, fullName);
			} catch (Exception e) {
				log.error(">>>>>>>>>>>>>>>>>>>>>ftp upload check file to dmz zone failed!<<<<<<<<<<<<<<<<<<<<<<<<");
				e.printStackTrace();
			}

		}

		//2)生成商城商户对账文件
		log.info(">>>>>>>>>>>>>>>>>>>>gen check file for normal merchants>>>>>>>>>>>>>");
		List<String> merList = this.getMerList();
		for (String merNo : merList) {
			List<BthMerInAccDtl> chkOrderList = this.getDataList(merNo, Constans.MER_LEVLE_01);

			if (chkOrderList.size() == 0) {
				log.info(">>>>>>>>>>>>>>>>>>>>normal mer : " + merNo + " has no data @" + this.getBatchDate());
				continue;
			}
			log.info(">>>>>>>>>>>>>>>>>>>>gen check file for normal mer : " + merNo);

			//对账文件存放路径
			String remoteFileName = "";
			StringBuffer checkFile = new StringBuffer(merChkFilePath);
			checkFile.append(txnDate).append("/").append(merNo).append("/");
			checkFile.append(merNo).append("-").append(txnDate).append("checkfile.txt");
			remoteFileName = merNo + "-" + txnDate + "checkfile.txt";
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate check file:" + checkFile.toString());

			//统计首行信息
			BthMerInAccDtl merInAcc = this.getFirstLineData(merNo, Constans.MER_LEVLE_01);
			int totalRow = Integer.parseInt(merInAcc.getRowCount());
			String totalAmount = merInAcc.getTxnAmt();
			String firstLine = this.genFirstLine(totalRow, totalAmount);

			this.writeFileFirstLine(checkFile.toString(), firstLine);

			//写文件
			FileUtil.write(checkFile.toString(), chkOrderList, true, null, null, true);

			String fullName = checkFile.toString();
			File f = new File(fullName);    //方便测试..
			String localDir = f.getParent();
			String shortName = f.getName();

			//将文件ftp到dmz区
			try {
				String dmzJsFilePath = this.dmzFtpPath.replace("merId", merNo) + this.jsFilePath;
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> remote path : " + dmzJsFilePath);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> local path : " + localDir);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> local file name : " + fullName);

				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>uploading file : " + shortName);
				SftpUtil.ftpUploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword,
						remoteFileName, dmzJsFilePath, fullName, localDir);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>upload file : " + shortName + " completed!");

				//FTP上传商户对账文件
				//FtpUtil.uploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, "", dmzJsFilePath+shortName, fullName);
			} catch (Exception e) {
				log.error(">>>>>>>>>>>>>>>>>>>>>ftp upload check file to dmz zone failed!<<<<<<<<<<<<<<<<<<<<<<<<");
				e.printStackTrace();
			}
		}


		//应答
		CommonResponse commonResponse = new CommonResponse();

		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

		return commonResponse;

	}

	public void genCheckFile(String merNo, String merType) {
	}

	//查询所有一级商户
	public List<String> getMerList() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgType", Constans.ORG_TYPE_PLAT);

		List<String> merList = mchtOrgRelDao.selectMchtNoList("selectNonPlatMerNoList", params);

		return merList;
	}

	//查询所有平台商户
	public List<String> getPlatMerList() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgType", Constans.ORG_TYPE_PLAT);

		List<String> platMerList = mchtOrgRelDao.selectMchtNoList("selectPlatMerNoList", params);

		return platMerList;
	}


	public int getTotalResult(String merNo, String merType) {
		int count = 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stlResult", Constans.SETTLE_STATUS_SUCCESS_CLEARING);
		params.put("updateDate", batchDate);
		count = bthMerInAccDtlDao.count("countStlSuccOrderOfCurrDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>order-count-num  update @" + this.getBatchDate() + " is : " + count);
		return count;
	}

	/**
	 * 根据商户号查询商户订单信息
	 *
	 * @param merNo   ： 商户号
	 * @param merType ：商户类型
	 * @return
	 */
	public List<BthMerInAccDtl> getDataList(String merNo, String merType) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("createDate", this.getBatchDate());
		List<BthMerInAccDtl> inAccDtlList = null;
		if (Constans.MER_LEVLE_00.equals(merType)) {
			//查询平台商户订单数据
			params.put("platMerNo", merNo);
			inAccDtlList = bthMerInAccDtlDao.selectList("selectChkDataByPlatMerNo", params);
		} else {
			//查询一级商户订单数据
			params.put("merNo", merNo);
			inAccDtlList = bthMerInAccDtlDao.selectList("selectChkDataByMerNo", params);
		}
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>inAccDtlList size of merchant :" + merNo + " @" + this.batchDate + " is " + inAccDtlList.size());

		for(BthMerInAccDtl inAccDtl : inAccDtlList)
		{
			fundChannelMap(inAccDtl);  //新老渠道映射
		}
		return inAccDtlList;
	}

	public BthMerInAccDtl getFirstLineData(String merNo, String merType) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("createDate", this.getBatchDate());
		BthMerInAccDtl inAccDtl = null;
		if (Constans.MER_LEVLE_00.equals(merType)) {
			//查询平台商户订单数据
			params.put("platMerNo", merNo);
			inAccDtl = bthMerInAccDtlDao.selectOne("selectChkFileCountDataByPlatMerNo", params);
		} else {
			//查询一级商户订单数据
			params.put("merNo", merNo);
			inAccDtl = bthMerInAccDtlDao.selectOne("selectChkFileCountDataByMerNo", params);
		}
		return inAccDtl;
	}

	public void updateObject() {
		//更新二级商户结算状态
		log.info(">>>>>>>>>>>>>>>>>>>syncStlStatus @" + this.getBatchDate() + "completed..");
	}

	/**
	 * 将首行内容写入文件
	 *
	 * @param fileName ： 文件名
	 * @param content  ：文件首行内容
	 */
	private void writeFileFirstLine(String fileName, String content) {
		File file = new File(fileName);

		if (!file.exists()) {
			try {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (FileOutputStream fos = new FileOutputStream(fileName, false);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
				BufferedWriter bw = new BufferedWriter(osw)){
			bw.write(content);
			bw.write(System.getProperty("line.separator"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拼接首行内容
	 *
	 * @param rowCount ： 总行数
	 * @param totalAmt ：总金额
	 * @return ： 首行内容
	 */
	private String genFirstLine(int rowCount, String totalAmt) {
		String firstLineContent = "";
		StringBuffer rowCountStr = new StringBuffer("" + rowCount);
		while (rowCountStr.length() < 12) {
			rowCountStr.insert(0, "0");
		}

		StringBuffer iAmtStr = new StringBuffer("" + totalAmt);
		while (iAmtStr.length() < 12) {
			iAmtStr.insert(0, "0");
		}

		firstLineContent = rowCountStr.append("|").append(iAmtStr).append("|").toString();

		return firstLineContent;
	}

	/**
	 * 新老账号映射
	 * @param inAccDtl
	 */
	private void fundChannelMap(BthMerInAccDtl inAccDtl)
	{
		if(Constans.GAINS_CHANNEL_WX.equals(inAccDtl.getFundChannel()))
		{
			//微信支付
			inAccDtl.setFundChannel(Constans.OLD_CHANNEL_WX);
		}
		else if (Constans.GAINS_CHANNEL_ALI.equals(inAccDtl.getFundChannel()))
		{
			//支付宝
			inAccDtl.setFundChannel(Constans.OLD_CHANNEL_ALI);
		}
		else if (Constans.GAINS_CHANNEL_HZF.equals(inAccDtl.getFundChannel()))
		{
			//惠支付设置成本行
			inAccDtl.setFundChannel(Constans.OLD_CHANNEL_SXC);
		}
		else if (Constans.GAINS_CHANNEL_SXK.equals(inAccDtl.getFundChannel()))
		{
			//蜀信卡
			inAccDtl.setFundChannel(Constans.OLD_CHANNEL_SXC);
		}
		else if (Constans.GAINS_CHANNEL_LOAN.equals(inAccDtl.getFundChannel()))
		{
			//授信支付设置成蜀信卡
			inAccDtl.setFundChannel(Constans.OLD_CHANNEL_SXC);
		}
		else if (Constans.GAINS_CHANNEL_SXE.equals(inAccDtl.getFundChannel()))
		{
			//蜀信e
			inAccDtl.setFundChannel(Constans.OLD_CHANNEL_SXE);
		}
		else if (Constans.GAINS_CHANNEL_UNIONPAY.equals(inAccDtl.getFundChannel()))
		{
			//银联
			inAccDtl.setFundChannel(Constans.OLD_CHANNEL_UNIONPAY);
		}
		else if (Constans.GAINS_CHANNEL_POINT.equals(inAccDtl.getFundChannel()))
		{
			//积分
			inAccDtl.setFundChannel(Constans.OLD_CHANNEL_POINT);
		}
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
