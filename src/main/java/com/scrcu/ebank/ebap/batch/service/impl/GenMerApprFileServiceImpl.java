package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;

import com.jcraft.jsch.ChannelSftp;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.common.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfoTemp;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfoTemp;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfoTemp;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoTempDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoTempDao;
import com.scrcu.ebank.ebap.batch.dao.MchtOrgRelDao;
import com.scrcu.ebank.ebap.batch.dao.MchtStaffInfoTempDao;
import com.scrcu.ebank.ebap.batch.service.GenMerApprFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenMerApprFileServiceImpl implements GenMerApprFileService {
	
	private String batchDate;
	
	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;
	
	@Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           //商户基本信息
	
	@Resource
    private MchtOrgRelDao mchtOrgRelDao;               //商户组织关联表
	@Resource
    private MchtBaseInfoTempDao mchtBaseInfoTempDao;   //商户基本信息临时表
	@Resource
    private MchtContInfoTempDao mchtContInfoTempDao;   //商户结算信息临时表
	
	@Resource
	private MchtStaffInfoTempDao  mchtStaffInfoTempDao;  //查询商户联系人信息
	
	
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
    
    @Value("${approveRstPath}")
    private String jsFilePath;
/**
	 * 服务商生成审核文件
	 */
	public void serMerApprFile(){
		//1)生成服务商户审核文件
		List<String> serMerList = this.getSerMerList();
		for(String serMerNo : serMerList)
		{
			List<MchtBaseInfoTemp> approveList = this.getDataList(serMerNo, Constans.MER_LEVLE_03);
			if(approveList.size() == 0)
			{
				log.info(">>>>>>>>>>>>>>>>>>>>ser serMer : " + serMerNo+ " has no approve info. @"+this.getBatchDate());
				continue;
			}

			List<MchtBaseInfoTemp> approveList2 = new ArrayList<MchtBaseInfoTemp>();
			for(MchtBaseInfoTemp tmpInfo:approveList)
			{
				tmpInfo.setApproveType("01");            //01 - 基本信息审核

				//设置商户级别20190429
				//商户级别：00-一级商户；01-二级商户 ref : 商城接入规范v3.1-商城版
				if(tmpInfo.getMchtId() != null && tmpInfo.getMchtId().length() > 15)
				{
					tmpInfo.setMchtNat("01");    //二级商户
				}
				else
				{
					tmpInfo.setMchtNat("00");    //一级商户
				}

				//设置审核状态
				tmpInfo.setApproveResult(tmpInfo.getExamResult());
				if(Constans.AUDIT_ST_PASS.equals(tmpInfo.getExamResult()))
				{
					tmpInfo.setApproveResult(Constans.APPROVE_RESULT_AGREE);
				}
				else if(Constans.AUDIT_ST_REFUSE.equals(tmpInfo.getExamResult()) || Constans.AUDIT_ST_NEEDMOREINFO.equals(tmpInfo.getExamResult()))
				{
					tmpInfo.setApproveResult(Constans.APPROVE_RESULT_REFUSE);
				}

				//查询结算信息
				MchtContInfoTemp stlInfo = this.getMerStlInfo(tmpInfo.getMchtId());

				//结算账户信息
				tmpInfo.setSettlAcctNo(stlInfo.getSettlAcctNo());
				tmpInfo.setSettlAcctName(stlInfo.getSettlAcctName());
				tmpInfo.setSettlAcctOrgId(stlInfo.getSettlAcctOrgId());
				tmpInfo.setAcctNat(stlInfo.getSettlAcctType());

				//设置结算周期
				tmpInfo.setSettlCycleParam(stlInfo.getSettlCycleParam()+"");

				//查询商户联系电话
				MchtStaffInfoTemp staff = this.getStaffInfo(tmpInfo.getMchtId());
				if(staff != null)
				{
					tmpInfo.setPhone(staff.getStaffPhone());
					tmpInfo.setMobilePhone(staff.getStaffPhone());
					tmpInfo.setContactName(staff.getStaffName());
				}

				MchtBaseInfoTemp stlInfoAppr = new MchtBaseInfoTemp();
				ReflectionUtil.copyProperties(tmpInfo , stlInfoAppr);
				stlInfoAppr.setApproveType("02");        //02 - 结算信息审核
				approveList2.add(tmpInfo);
				//approveList2.add(stlInfoAppr);

			}

			log.info(">>>>>>>>>>>>>>>>>>>>gen approve file for serMer : " + serMerNo);

			//对账文件存放路径
			String remoteFileName = "";
			StringBuffer checkFile = new StringBuffer(merChkFilePath);
			checkFile.append(batchDate).append("/").append(serMerNo).append("/");
			checkFile.append(serMerNo).append("_SER_APPRROVERESULT_").append(batchDate).append(".txt");
			remoteFileName =serMerNo+"_SER_APPRROVERESULT_"+batchDate;

			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate approve file:"+checkFile.toString());

			//写文件
			FileUtil.write(checkFile.toString(), approveList2, false, null, null,false,"1.1");
			//不用将文件ftp到dmz区
		}
	}
	@Override
	public CommonResponse genMerApprFile(BatchRequest request) {
		String batchDate = request.getSettleDate();
    	if(IfspDataVerifyUtil.isBlank(batchDate))
    	{
    		batchDate = DateUtil.format(new Date(), "yyyyMMdd");
    	}
    	
		this.setBatchDate(batchDate);

		try {
			Thread.sleep(1000*60*3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>genMerApprFile service of "+this.batchDate+" executing ");
		
		//1)生成平台商户审核文件
		List<String> platMerList = this.getPlatMerList();
		for(String platMerNo : platMerList)
		{
			List<MchtBaseInfoTemp> approveList = this.getDataList(platMerNo, Constans.MER_LEVLE_00);
			if(approveList.size() == 0)
			{
				log.info(">>>>>>>>>>>>>>>>>>>>plat mer : " + platMerNo+ " has no approve info. @"+this.getBatchDate());
				continue;
			}
			
			List<MchtBaseInfoTemp> approveList2 = new ArrayList<MchtBaseInfoTemp>();
			for(MchtBaseInfoTemp tmpInfo:approveList)
			{
				tmpInfo.setApproveType("01");            //01 - 基本信息审核

				//设置商户级别20190429
				//商户级别：00-一级商户；01-二级商户 ref : 商城接入规范v3.1-商城版
				if(tmpInfo.getMchtId() != null && tmpInfo.getMchtId().length() > 15)
				{
					tmpInfo.setMchtNat("01");    //二级商户
				}
				else
				{
					tmpInfo.setMchtNat("00");    //一级商户
				}
				
				//设置审核状态
				tmpInfo.setApproveResult(tmpInfo.getExamResult());
				if(Constans.AUDIT_ST_PASS.equals(tmpInfo.getExamResult()))
				{
					tmpInfo.setApproveResult(Constans.APPROVE_RESULT_AGREE);
				}
				else if(Constans.AUDIT_ST_REFUSE.equals(tmpInfo.getExamResult()) || Constans.AUDIT_ST_NEEDMOREINFO.equals(tmpInfo.getExamResult()))
				{
					tmpInfo.setApproveResult(Constans.APPROVE_RESULT_REFUSE);
				}
					
				//查询结算信息
				MchtContInfoTemp stlInfo = this.getMerStlInfo(tmpInfo.getMchtId());
				
				//结算账户信息
				tmpInfo.setSettlAcctNo(stlInfo.getSettlAcctNo());
				tmpInfo.setSettlAcctName(stlInfo.getSettlAcctName());
				tmpInfo.setSettlAcctOrgId(stlInfo.getSettlAcctOrgId());
				tmpInfo.setAcctNat(stlInfo.getSettlAcctType());
				
				//设置结算周期
				tmpInfo.setSettlCycleParam(stlInfo.getSettlCycleParam()+"");
				
				//查询商户联系电话
				MchtStaffInfoTemp staff = this.getStaffInfo(tmpInfo.getMchtId());
				if(staff != null)
				{
					tmpInfo.setPhone(staff.getStaffPhone());
					tmpInfo.setMobilePhone(staff.getStaffPhone());
				}
				
				MchtBaseInfoTemp stlInfoAppr = new MchtBaseInfoTemp();
				ReflectionUtil.copyProperties(tmpInfo , stlInfoAppr);
				stlInfoAppr.setApproveType("02");        //02 - 结算信息审核
				approveList2.add(tmpInfo);
				approveList2.add(stlInfoAppr);
			}
			
			log.info(">>>>>>>>>>>>>>>>>>>>gen approve file for plat mer : " + platMerNo);
			
			//对账文件存放路径
			String remoteFileName = "";
			StringBuffer checkFile = new StringBuffer(merChkFilePath);
			checkFile.append(batchDate).append("/").append(platMerNo).append("/");
			checkFile.append(platMerNo).append("_APPROVERESULT_").append(batchDate);
			remoteFileName =platMerNo+"_APPROVERESULT_"+batchDate;
			
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate approve file:"+checkFile.toString());
			
			//写文件
			FileUtil.write(checkFile.toString(), approveList2, false, null, null,false);
			
			//将文件ftp到dmz区
			try
			{
				String fullName = checkFile.toString();
				File apprFile = new File(fullName);    //方便测试..
				
				String localDir = apprFile.getParent();
				String shortName = apprFile.getName();
				
				String dmzJsFilePath =  this.dmzFtpPath.replace("merId", platMerNo)  + this.jsFilePath;
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> remote path : " + dmzJsFilePath);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> local path : " + localDir);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> local file name : " + fullName);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>uploading file : " + shortName);
				/*SftpUtil.ftpUploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword,
						remoteFileName, dmzJsFilePath, fullName,localDir);*/

				SftpUtil sftpUtil = new SftpUtil(dmzFtpHost, dmzFtpUserName, dmzFtpPassword, Integer.parseInt(dmzFtpPort));

				ChannelSftp sftp = null;

				sftp = sftpUtil.connectSFTP();

				if(sftp == null)
				{
					log.info(">>>>>>>>>>>>同步建立连接中...");
					sftp = sftpUtil.connectSFTPWithNoTimeOut();
					log.info(">>>>>>>>>>>>同步建立连接成功！");
				}
				log.info("---------------------SFTP上传文件------------------");
				sftpUtil.upload(dmzJsFilePath, localDir, fullName, sftp);
				log.info("---------------------SFTP断开连接------------------");
				sftpUtil.disconnected(sftp);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>upload file : " + shortName + " completed!");
				
				//FTP上传审核文件
				//FtpUtil.uploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, "", dmzJsFilePath+shortName, fullName);
			}
			catch(Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>>>ftp upload approve file to dmz zone failed!<<<<<<<<<<<<<<<<<<<<<<<<");
				e.printStackTrace();
			}
			
		}
		
		//2)生成商城商户审核文件
		log.info(">>>>>>>>>>>>>>>>>>>>gen approve file for normal merchants>>>>>>>>>>>>>");
		try {
			Thread.sleep(1000*60*1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<String> merList = this.getMerList();
		for(String merNo : merList)
		{
			List<MchtBaseInfoTemp> approveList = this.getDataList(merNo, Constans.MER_LEVLE_01);
			
			if(approveList.size() == 0)
			{
				log.info(">>>>>>>>>>>>>>>>>>>>normal mer : " + merNo+ " has no approve info. @"+this.getBatchDate());
				continue;
			}
		
			List<MchtBaseInfoTemp> approveList2 = new ArrayList<MchtBaseInfoTemp>();
			for(MchtBaseInfoTemp tmpInfo:approveList)
			{
				tmpInfo.setApproveType("01");            //01 - 基本信息审核
				//设置审核状态
				tmpInfo.setApproveResult(tmpInfo.getExamResult());
				if(Constans.AUDIT_ST_PASS.equals(tmpInfo.getExamResult()))
				{
					tmpInfo.setApproveResult(Constans.APPROVE_RESULT_AGREE);
				}
				else if(Constans.AUDIT_ST_REFUSE.equals(tmpInfo.getExamResult()) || Constans.AUDIT_ST_NEEDMOREINFO.equals(tmpInfo.getExamResult()))
				{
					tmpInfo.setApproveResult(Constans.APPROVE_RESULT_REFUSE);
				}
				//查询结算信息
				MchtContInfoTemp stlInfo = this.getMerStlInfo(tmpInfo.getMchtId());
				
				//结算账户信息
				tmpInfo.setSettlAcctNo(stlInfo.getSettlAcctNo());
				tmpInfo.setSettlAcctName(stlInfo.getSettlAcctName());
				tmpInfo.setSettlAcctOrgId(stlInfo.getSettlAcctOrgId());
				tmpInfo.setAcctNat(stlInfo.getSettlAcctType());
				
				//设置结算周期
				tmpInfo.setSettlCycleParam(stlInfo.getSettlCycleParam()+"");
				
				//查询商户联系电话
				MchtStaffInfoTemp staff = this.getStaffInfo(tmpInfo.getMchtId());
				if(staff != null)
				{
					tmpInfo.setPhone(staff.getStaffPhone());
					tmpInfo.setMobilePhone(staff.getStaffPhone());
				}
				
				
				MchtBaseInfoTemp stlInfoAppr = new MchtBaseInfoTemp();
				ReflectionUtil.copyProperties(tmpInfo , stlInfoAppr);
				stlInfoAppr.setApproveType("02");        //02 - 结算信息审核
				approveList2.add(tmpInfo);
				approveList2.add(stlInfoAppr);
			}
			log.info(">>>>>>>>>>>>>>>>>>>>gen approve file for normal mer : " + merNo);
			
			//对账文件存放路径
			String remoteFileName = "";
			StringBuffer checkFile = new StringBuffer(merChkFilePath);
			checkFile.append(batchDate).append("/").append(merNo).append("/");
			checkFile.append(merNo).append("_APPROVERESULT_").append(batchDate);
			remoteFileName =merNo+"_APPROVERESULT_"+batchDate;
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate approve file:"+checkFile.toString());
			
			//写文件
			FileUtil.write(checkFile.toString(), approveList2, false, null, null,false);
			
			//将文件ftp到dmz区
			try
			{
				String fullName = checkFile.toString();
				
				File apprFile = new File(fullName);    //方便测试..
				
				String localDir = apprFile.getParent();
				String shortName = apprFile.getName();
				
				String dmzJsFilePath =  this.dmzFtpPath.replace("merId", merNo)  + this.jsFilePath;
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> remote path : " + dmzJsFilePath);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> local path : " + localDir);
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> local file name : " + fullName);
				
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>uploading file : " + shortName);
				
				SftpUtil.ftpUploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword,
						remoteFileName, dmzJsFilePath, fullName,localDir);
				
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>upload file : " + shortName + " completed!");
				
				//FTP上传审核文件
				//FtpUtil.uploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, "", dmzJsFilePath+shortName, fullName);
			}
			catch(Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>>>ftp upload approve file to dmz zone failed!<<<<<<<<<<<<<<<<<<<<<<<<");
				e.printStackTrace();
			}
		}
		log.info(">>>>>>>>>>>>>>>>>>>>gen approve file serMerApprFile >>>>>>>>>>>>>start");
		serMerApprFile();
		log.info(">>>>>>>>>>>>>>>>>>>>gen approve file serMerApprFile >>>>>>>>>>>>>end");

		//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
				
		return commonResponse;
		
	}
	
	
	//查询所有一级商户
	public List<String> getMerList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("orgType", Constans.ORG_TYPE_PLAT);
		
		List<String> merList = mchtOrgRelDao.selectMchtNoList("selectNonPlatMerNoList2", params);
		
		return merList;
	}
	
	//查询所有平台商户
	public List<String> getPlatMerList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("orgType", Constans.ORG_TYPE_PLAT);
		
		List<String> platMerList = mchtOrgRelDao.selectMchtNoList("selectPlatMerNoList2", params);
		
		return platMerList;
	}


	//查询所有服务商商户
	public List<String> getSerMerList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("orgType", Constans.ORG_TYPE_OPBYMER);

		List<String> platMerList = mchtOrgRelDao.selectMchtNoList("selectPlatMerNoList2", params);

		return platMerList;
	}
	public int getTotalResult(String merNo,String merType)
	{
		int count = 0;
		
		return count;
	}
	
	/**
	 * 根据商户号查询商户订单信息
	 * @param merNo ： 商户号
	 * @param merType ：商户类型
	 * @return
	 */
	public List<MchtBaseInfoTemp> getDataList(String merNo,String merType)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		
		params.put("auditDate", this.getBatchDate()+"%");

		Date batchDate = IfspDateTime.parseDate(this.getBatchDate(),IfspDateTime.YYYYMMDD);  //最后订单日期
		params.put("preAuditDate", this.getBatchDate()+"%");
		if(batchDate != null){
			Calendar cal = Calendar.getInstance();  //当前日期
			cal.setTime(batchDate);
			cal.add(Calendar.DATE,-1);  //当前日期前一天
			params.put("preAuditDate",IfspDateTime.getYYYYMMDD(cal.getTime())+"%");

		}

		List<MchtBaseInfoTemp> approveList = null;
		if(Constans.MER_LEVLE_00.equals(merType))
		{
			//查询平台商户订单数据
			params.put("platMerNo", merNo);
			approveList = mchtBaseInfoTempDao.selectList("selectApproveDataByPlatMerNo", params);
		}else if (Constans.MER_LEVLE_03.equals(merType)){
			//查询服务商商户
			params.put("orgId", merNo);
			params.put("orgType","03");
			approveList = mchtBaseInfoTempDao.selectList("selectApproveDataBySerMerNo", params);
		}else
		{
			//查询一级商户订单数据
			params.put("merNo", merNo);
			approveList = mchtBaseInfoTempDao.selectList("selectApproveDataByMerNo", params);
		}
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>approveList size of merchant :" + merNo + " @" + this.batchDate +" is " + approveList.size());
		return approveList;
	}
	
	
	/**
	 * 根据商户号查询商户订单信息
	 * @param merNo ： 商户号
	 * @param merType ：商户类型
	 * @return
	 */
	public MchtContInfoTemp getMerStlInfo(String merNo)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		
		params.put("auditDate", this.getBatchDate()+"%");
		params.put("mchtId", merNo);
		
		MchtContInfoTemp stlInfo =  new MchtContInfoTemp(); 
		List<MchtContInfoTemp> stlInfoList = mchtContInfoTempDao.selectList("selectMerTmpStlInfo", params);
		if(stlInfoList != null && stlInfoList.size() > 0)
		{
			stlInfo = stlInfoList.get(0);
			if(Constans.SETTL_ACCT_TYPE_PLAT.equals(stlInfo.getSettlAcctType()))
			{
				stlInfo.setSettlAcctType(Constans.ACC_TYPE_SCRCU);
			}
			else if(Constans.SETTL_ACCT_TYPE_CROSS.equals(stlInfo.getSettlAcctType()))
			{
				stlInfo.setSettlAcctType(Constans.ACC_TYPE_OTHER);
			}
		}

		return stlInfo;
	}
	
	/**
	 * 查询商户员工信息
	 * @param mchtId
	 * @return
	 */
	public MchtStaffInfoTemp getStaffInfo(String mchtId)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		
		params.put("mchtId", mchtId);
		params.put("staffRole",Constans.STAFF_ROLE_MANAGER);
		
		List<MchtStaffInfoTemp> staffList = mchtStaffInfoTempDao.selectList("selectStaffByMchtIdAndRole", params);
		
		if(staffList != null && staffList.size() > 0)
		{
			return staffList.get(0);
		}
		
		return null;
	}
	
	public void updateObject()
	{
		//更新二级商户结算状态
		log.info(">>>>>>>>>>>>>>>>>>>syncStlStatus @"+this.getBatchDate() + "completed..");
	}

	public String getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}

}
