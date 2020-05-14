package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.message.IfspBase64;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.common.utils.EncryptUtil;
import com.scrcu.ebank.ebap.batch.soaclient.OperAuthSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jcraft.jsch.ChannelSftp;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.request.SynchronizeDataRequest;
import com.scrcu.ebank.ebap.batch.bean.response.SynchronizeDataResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.SftpUtil;
import com.scrcu.ebank.ebap.batch.dao.BthScheduleClusterDao;
import com.scrcu.ebank.ebap.batch.dao.IfsOrgDao;
import com.scrcu.ebank.ebap.batch.dao.IfsStaffDao;
import com.scrcu.ebank.ebap.batch.dao.IfsStaffOrgRelDao;
import com.scrcu.ebank.ebap.batch.dao.IfsStaffRoleRelDao;
import com.scrcu.ebank.ebap.batch.service.SynchronizeDataService;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class SynchronizeDataServiceImpl implements SynchronizeDataService {
	@Value("${synchronizeFileIp}")
	private String synchronizeFileIp;
    @Value("${synchronizeFilePort}")
    private String synchronizeFilePort;
    @Value("${synchronizeFileUser}")
    private String synchronizeFileUser;
    @Value("${synchronizeFilePwd}")
    private String synchronizeFilePassWord;
    @Value("${synchronizeFileRemoteUrl}")
    private String synchronizeFileRemoteUrl;
    @Value("${synchronizeFileRemoteUserFile}")
    private String synchronizeFileRemoteUserFile;
    @Value("${synchronizeFileRemoteUnitFile}")
    private String synchronizeFileRemoteUnitFile;
    @Value("${synchronizeFileRemoteFlagName}")
    private String synchronizeFileRemoteFlagName;
    @Value("${synchronizeFileLocalFileUrl}")
    private String synchronizeFileLocalFileUrl;
    @Value("${synchronizeFileLocalUserFile}")
    private String synchronizeFileLocalUserFile;
    @Value("${synchronizeFileLocalUnitFile}")
    private String synchronizeFileLocalUnitFile;
	@Resource
	private IfsStaffDao ifsStaffDao;
	@Resource
	private IfsStaffRoleRelDao ifsStaffRoleRelDao;
	@Resource
	private IfsStaffOrgRelDao ifsStaffOrgRelDao;
	@Resource
	private IfsOrgDao ifsOrgDao;
	@Resource
	private BthScheduleClusterDao bthScheduleClusterDao;

	@Resource
	private OperAuthSoaService operAuthSoaService;
	/**
	 * 同步文件下载及解析
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public CommonResponse synchronizeStaff() throws Exception {
		CommonResponse commonResponse = new CommonResponse();

		log.info("---------------------获取Ftp配置------------------");
		//获取当前时间
		Date date = new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
		String currDate = form.format(date).substring(0, 8);
		log.info("------------------执行日期为："+currDate+"-----------------");
		String ip = synchronizeFileIp;
		String port = synchronizeFilePort;
		String userName =synchronizeFileUser;
		String password = synchronizeFilePassWord;
		String remoteUrl = synchronizeFileRemoteUrl;
		String remoteFileName = synchronizeFileRemoteUserFile;
		remoteFileName = remoteFileName.replace("YYYYMMDD", currDate);
		String flagFileName = synchronizeFileRemoteFlagName;
		flagFileName = flagFileName.replace("YYYYMMDD", currDate);

		String localFileUrl = synchronizeFileLocalFileUrl;
		String localFileName = synchronizeFileLocalUserFile;
		localFileName = localFileName.replace("YYYYMMDD", currDate);
		// 本地存放变更文件的路径
		log.info("文件存放本地路径:" + localFileUrl);
		log.info("本地文件名:" + localFileName);
		log.info("变更文件远程所在路径:" + remoteUrl);
		log.info("远程变更文件名:" + remoteFileName);
		log.info("远程标记文件名:" + flagFileName);
		log.info("SFTP服务器地址：[ 服务器地址(IP)：" + ip + " , 服务器端口(PORT)：" + port + " ]");

		log.info("---------------------判断本地文件目录是否存在------------------");

		File filePath = new File(localFileUrl);
		// 如果文件夹不存在则创建
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("目录不存在");
			filePath.mkdirs();
		} else {
			log.info("目录存在");
		}
		String sftpFilePath = remoteUrl + remoteFileName; // 远程文件目录
		String sftpFlagFilePath = remoteUrl + flagFileName; // 远程文件目录
		String localFilePath = localFileUrl+localFileName;
		// 1.从变更文件中转区即ftp下载ok文件
		// 1.1 ok文件下载不成功（1.正常，当天没有发生变更事件，忽略不处理 2.不正常，下载异常中断，异常处理）
		if (!new File(localFilePath).exists()) {
			SftpUtil sftpUtil = new SftpUtil(ip, userName, password, Integer.parseInt(port));
			ChannelSftp sftp = sftpUtil.connectSFTP();

			if (sftpUtil.isFileExist(sftp, sftpFlagFilePath)) { // 判断远程目录文件是否存在
				log.info("远程标记文件存在");
			} else {
				log.info("远程标记文件不存在");
				sftpUtil.disconnected(sftp);
				throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程标记文件不存在");
			}

			if (sftpUtil.isFileExist(sftp, sftpFilePath)) { // 判断远程目录文件是否存在
				log.info("远程目录文件存在");
				log.info("---------------------SFTP获取本行对账文件名------------------");
				sftpUtil.download(remoteUrl, remoteFileName, localFileUrl, localFileName, sftp);
				log.info("---------------------SFTP断开连接------------------");
				sftpUtil.disconnected(sftp);
			} else {
				log.info("远程目录文件不存在");
				sftpUtil.disconnected(sftp);
				throw new IfspBizException("远程目录文件不存在");
			}

		}
		//////////////////////////////////////////////////////////////////////
		// 1.2 ok文件下载成功，下载员工/机构变更文件。下载成功，解析变更文件（按照“@#@”分隔符方式解析）。
		// 4.2将解析的字段更新到对应员工表或者机构表
		log.info("---------------------读取文件内容------------------");
		log.info("下载到本地变更文件目录路径为：:" + localFilePath);
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(localFilePath), Charset.forName("GBK")));){

			String str = null;
			Integer sum = 0;//更新总数
			Integer success = 0;//成功数量
			while ((str = br.readLine()) != null) {
				//总共有多少条数据
				sum++;
				IfsStaff ifsStaff = new IfsStaff();
				try {
					String[] strarr = str.split("@#@", -1);
					ifsStaff.setTlrId(strarr[2]); // 操作员编号
					log.info("操作员号:" + ifsStaff.getTlrId() + "开始更新");
					ifsStaff.setTlrName(strarr[1]); // 操作员名称
					ifsStaff.setTlrEmail(strarr[12]); // 邮箱
					if ("6".equals(strarr[0])) {//所属机构变更
						// 根据原机构编号和操作员编号更新人员机构关联表
						List<IfsStaffOrgRel> ifsStaffOrgRelList = ifsStaffOrgRelDao.selectByTlrId(strarr[2]);
						if (ifsStaffOrgRelList.size() == 0) {//没有关联关系的
							IfsStaffOrgRel ifsStaffOrgRelNew = new IfsStaffOrgRel();
							ifsStaffOrgRelNew.setTlrId(strarr[2]);
							ifsStaffOrgRelNew.setBrId(strarr[14]);
							ifsStaffOrgRelDao.insert(ifsStaffOrgRelNew);
						} else if (ifsStaffOrgRelList.size() == 1) {//只有一个机构的
							ifsStaffOrgRelDao.delete(strarr[2], ifsStaffOrgRelList.get(0).getBrId());
							IfsStaffOrgRel ifsStaffOrgRelNew = new IfsStaffOrgRel();
							ifsStaffOrgRelNew.setTlrId(strarr[2]);
							ifsStaffOrgRelNew.setBrId(strarr[14]);
							ifsStaffOrgRelDao.insert(ifsStaffOrgRelNew);
						} else if (ifsStaffOrgRelList.size() > 1) {//有多个机构
							//原机构是否存在
							boolean flag = true;
							String brId = "";
							for (IfsStaffOrgRel ifsStaffOrgRel : ifsStaffOrgRelList) {
								if (IfspDataVerifyUtil.equals(strarr[14], ifsStaffOrgRel.getBrId())) {
									brId = ifsStaffOrgRel.getBrId();
									flag = false;
								}
							}
							if (flag) {
								throw new IfspBizException("操作员<" + strarr[2] + ">有多个机构，原机构<" + strarr[16] + ">机构关联关系不存在，请联系技术人员手动修改");
							}
							ifsStaffOrgRelDao.delete(strarr[2], brId);
							IfsStaffOrgRel ifsStaffOrgRelNew = new IfsStaffOrgRel();
							ifsStaffOrgRelNew.setTlrId(strarr[2]);
							ifsStaffOrgRelNew.setBrId(strarr[14]);
							ifsStaffOrgRelDao.insert(ifsStaffOrgRelNew);
						}

						//更新机构角色关联关系
						List<IfsStaffRoleRel> ifsStaffRoleRelList = ifsStaffRoleRelDao.selectByTlrId(strarr[2]);
						if (ifsStaffRoleRelList.size() == 0) {//没有关联关系
							IfsStaffRoleRel ifsStaffRoleRelNew = new IfsStaffRoleRel();
							ifsStaffRoleRelNew.setTlrId(strarr[2]);
							ifsStaffRoleRelNew.setBrId(strarr[14]);
							ifsStaffRoleRelNew.setFlag("1");
							ifsStaffRoleRelNew.setRoleId("9999");
							ifsStaffRoleRelDao.insert(ifsStaffRoleRelNew);
						} else if (ifsStaffRoleRelList.size() == 1) {//只有一条关联关系
							ifsStaffRoleRelDao.delete(strarr[2], ifsStaffRoleRelList.get(0).getRoleId(), ifsStaffRoleRelList.get(0).getBrId());
							IfsStaffRoleRel ifsStaffRoleRelNew = new IfsStaffRoleRel();
							ifsStaffRoleRelNew.setTlrId(strarr[2]);
							ifsStaffRoleRelNew.setBrId(strarr[14]);
							ifsStaffRoleRelNew.setFlag("1");
							ifsStaffRoleRelNew.setRoleId("9999");
							ifsStaffRoleRelDao.insert(ifsStaffRoleRelNew);
						} else if (ifsStaffRoleRelList.size() > 1) {//有多条关联关系
							boolean flag = true;//原机构是否存在
							String brId = "";
							String roleId = "";
							for (IfsStaffRoleRel ifsStaffRoleRel : ifsStaffRoleRelList) {
								if (IfspDataVerifyUtil.equals(strarr[14], ifsStaffRoleRel.getBrId())) {
									brId = ifsStaffRoleRel.getBrId();
									roleId = ifsStaffRoleRel.getRoleId();
									flag = false;
								}
							}
							if (flag) {
								throw new IfspBizException("操作员<" + strarr[2] + ">有多个机构，原机构<" + strarr[16] + ">角色关联关系不存在，请联系技术人员手动修改");
							}
							ifsStaffRoleRelDao.delete(strarr[2], roleId, brId);
							IfsStaffRoleRel ifsStaffRoleRelNew = new IfsStaffRoleRel();
							ifsStaffRoleRelNew.setTlrId(strarr[2]);
							ifsStaffRoleRelNew.setBrId(strarr[14]);
							ifsStaffRoleRelNew.setFlag("1");
							ifsStaffRoleRelNew.setRoleId("9999");
							ifsStaffRoleRelDao.insert(ifsStaffRoleRelNew);
						}


					} else if ("0".equals(strarr[0]) || "".equals(strarr[0])) {//新增操作员
						// TODO 插入操作员机构关联表
						IfsStaffOrgRel ifsStaffOrgRel = new IfsStaffOrgRel();
						ifsStaffOrgRel.setBrId(strarr[14]);
						ifsStaffOrgRel.setTlrId(strarr[2]);
						ifsStaffOrgRelDao.insert(ifsStaffOrgRel);
						// TODO 需要给默认角色，操作员角色关联表
						IfsStaffRoleRel ifsStaffRoleRel = new IfsStaffRoleRel();
						ifsStaffRoleRel.setTlrId(strarr[2]);
						ifsStaffRoleRel.setRoleId("9999");
						ifsStaffRoleRel.setBrId(strarr[14]);
						ifsStaffRoleRel.setFlag("1");
						ifsStaffRoleRelDao.insert(ifsStaffRoleRel);
					}
					// 赋予初始默认密码111111
					ifsStaff.setTlrPw("96e79218965eb72c92a549dd5a330112"); // 密码
					ifsStaff.setQuitFlag("0"); // 有效标志 0已签退1已登录
					ifsStaff.setPwErrTimes(Short.parseShort("0")); // 有效标志 0已签退1已登录
					if ("1".equals(strarr[13])) {
						ifsStaff.setTlrState("00"); // 标志位 00有效01无效 02冻结
					} else if ("2".equals(strarr[13])) {
						ifsStaff.setTlrState("02"); // 标志位 00有效01无效 02冻结
					} else {
						ifsStaff.setTlrState("01"); // 标志位 00有效01无效 02冻结
					}
					ifsStaff.setIsLock("0"); /// 是否锁定
					ifsStaff.setCrtTlr("sys"); // 最后更新操作员
					ifsStaff.setUpdTlr("sys"); // 操作员
					ifsStaff.setWorkNo(strarr[2]); // 工号
					ifsStaff.setTlrPhone(strarr[11]); // 手机号
					ifsStaff.setTlrCertNo(strarr[4]); // 身份证号
					log.info("Tlrno:" + ifsStaff.getTlrId());
					// 根据变更标识新增修改
					if ("0".equals(strarr[0]) || "".equals(strarr[0])) {
						// 3.如果员工/机构变更文件变更标识为0，将解析的字段初始化到员工表或者机构表,插入库中
						log.info("---------------------插入数据到本行核心流水表------------------");
						int k = ifsStaffDao.insertSelective(ifsStaff);
						if (k != -1) {
							log.info("操作员号:" + ifsStaff.getTlrId() + "插入结束,状态为：插入成功！");
						} else {
							log.info("操作员号:" + ifsStaff.getTlrId() + "插入结束,状态为：插入失败！");
						}
					} else {
						// 4.非0情况下,更新。
						int k = ifsStaffDao.updateByPrimaryKeySelective(ifsStaff);
						if (k != -1) {
							log.info("操作员号:" + ifsStaff.getTlrId() + "更新结束，状态为：更新成功！");
						} else {
							log.info("操作员号:" + ifsStaff.getTlrId() + "更新结束，状态为：更新失败！");
						}
					}

					/*
					 * 调接口删除飞信通进件权限
					 */
					if ("6".equals(strarr[0])) {
						Map<Object, Object> params = new HashMap<>();
						params.put("tlrId", strarr[2]);
						params.put("oprType", "1");
						SoaParams soaParams = new SoaParams();
						soaParams.setDatas(params);
						SoaResults soaResults = operAuthSoaService.operAuth(soaParams);
						if (!IfspRespCodeEnum.RESP_SUCCESS.getCode().equals(soaResults.getRespCode())) {
							throw new IfspBizException(soaResults.getRespCode(),soaResults.getRespMsg());
						}
					}

					SynchronizeStaffInfo synchronizeStaffInfo = new SynchronizeStaffInfo();
					synchronizeStaffInfo.setId(IfspId.getUUID32());
					synchronizeStaffInfo.setTlrId(ifsStaff.getTlrId());
					synchronizeStaffInfo.setSynchrState("00");
					synchronizeStaffInfo.setSynchrTime(new Date());
					ifsStaffDao.insert(synchronizeStaffInfo);
					success++;
				} catch (Exception e) {
					log.info(e.toString());
					log.info("操作员号:" + ifsStaff.getTlrId() + "更新结束，状态为：更新失败！");
					SynchronizeStaffInfo synchronizeStaffInfo = new SynchronizeStaffInfo();
					synchronizeStaffInfo.setId(IfspId.getUUID32());
					synchronizeStaffInfo.setTlrId(ifsStaff.getTlrId());
					synchronizeStaffInfo.setSynchrState("01");
					synchronizeStaffInfo.setSynchrTime(new Date());
					synchronizeStaffInfo.setBakInfo(str);
					ifsStaffDao.insert(synchronizeStaffInfo);
				}
			}
			br.close();
			log.info("操作员文件条数为:" + sum + ",同步成功条数为:" + success);
			log.debug("同步操作员执行结束!!!!!!!");

			commonResponse.setRespCode("0000");
			commonResponse.setRespMsg("交易成功");
			return commonResponse;
		} catch (Exception e) {
			commonResponse.setRespCode("9999");
			commonResponse.setRespMsg("交易失败");
			return commonResponse;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public CommonResponse synchronizeOrg() throws Exception {
		CommonResponse commonResponse = new CommonResponse();

		log.info("---------------------获取Ftp配置------------------");
		Date date = new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
		String currDate = form.format(date).substring(0, 8);
		log.info("------------------执行日期为："+currDate+"-----------------");
		String ip = synchronizeFileIp;
		String port = synchronizeFilePort;
		String userName = synchronizeFileUser;
		String password = synchronizeFilePassWord;
		String remoteUrl = synchronizeFileRemoteUrl;
		String remoteFileName = synchronizeFileRemoteUnitFile;
		remoteFileName = remoteFileName.replace("YYYYMMDD", currDate);
		String flagFileName =synchronizeFileRemoteFlagName;
		flagFileName = flagFileName.replace("YYYYMMDD", currDate);

		String localFileUrl = synchronizeFileLocalFileUrl;
		String localFileName = synchronizeFileLocalUnitFile;
		localFileName = localFileName.replace("YYYYMMDD", currDate);
		// 本地存放变更文件的路径
		log.info("文件存放本地路径:" + localFileUrl);
		log.info("本地文件名:" + localFileName);
		log.info("变更文件远程所在路径:" + remoteUrl);
		log.info("远程变更文件名:" + remoteFileName);
		log.info("远程标记文件名:" + flagFileName);
		log.info("SFTP服务器地址：[ 服务器地址(IP)：" + ip + " , 服务器端口(PORT)：" + port + " ]");

		log.info("---------------------判断本地文件目录是否存在------------------");

		File filePath = new File(localFileUrl);
		// 如果文件夹不存在则创建
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("目录不存在");
			filePath.mkdirs();
		} else {
			log.info("目录存在");
		}
		String sftpFilePath = remoteUrl + remoteFileName; // 远程文件目录
		String sftpFlagFilePath = remoteUrl + flagFileName; // 远程文件目录
		String localFilePath = localFileUrl+localFileName;
		// 1.从变更文件中转区即ftp下载ok文件
		// 1.1 ok文件下载不成功（1.正常，当天没有发生变更事件，忽略不处理 2.不正常，下载异常中断，异常处理）
		if (!new File(localFilePath).exists()) {
			SftpUtil sftpUtil = new SftpUtil(ip, userName, password, Integer.parseInt(port));
			ChannelSftp sftp = sftpUtil.connectSFTP();

			if (sftpUtil.isFileExist(sftp, sftpFlagFilePath)) { // 判断远程目录文件是否存在
				log.info("远程标记文件存在");
			} else {
				log.info("远程标记文件不存在");
				sftpUtil.disconnected(sftp);
				throw new IfspBizException("远程标记文件不存在");
			}

			if (sftpUtil.isFileExist(sftp, sftpFilePath)) { // 判断远程目录文件是否存在
				log.info("远程目录文件存在");
				log.info("---------------------SFTP获取本行对账文件名------------------");
				sftpUtil.download(remoteUrl, remoteFileName, localFileUrl, localFileName, sftp);
				log.info("---------------------SFTP断开连接------------------");
				sftpUtil.disconnected(sftp);
			} else {
				log.info("远程目录文件不存在");
				sftpUtil.disconnected(sftp);
				throw new IfspBizException("远程目录文件不存在");
			}

		}
		//////////////////////////////////////////////////////////////////////
		// 1.2 ok文件下载成功，下载员工/机构变更文件。下载成功，解析变更文件（按照“@#@”分隔符方式解析）。
		// 4.2将解析的字段更新到对应员工表或者机构表
		log.info("---------------------读取文件内容------------------");

		log.info("下载到本地变更文件目录路径为：:" + localFilePath);
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(localFilePath), Charset.forName("GBK")));){
			String str = null;
			String[] strs = null;
			Integer sum = 0;
			Integer success = 0;
			while ((str = br.readLine()) != null) {
				sum++;
				IfsOrg ifsOrg = new IfsOrg();
				try {
					strs = str.split("@#@", -1);
					ifsOrg.setBrId(strs[1]);// 机构编码
					log.info("机构:" + ifsOrg.getBrId() + "变更开始");
					ifsOrg.setBrName(strs[2]);// 机构全称
					ifsOrg.setBrSimName(strs[3]);// 机构简称(增加)
					ifsOrg.setBrLvl(strs[5].substring(0, 1));// 机构级别代码
					ifsOrg.setParBrId(strs[16]);// 直接上级机构编码
					if ("1".equals(strs[22])) {
						ifsOrg.setBrState("00");// 状态00-启用;01-停用;02-已删除;
					} else {
						ifsOrg.setBrState("02");// 状态00-启用;01-停用;02-已删除;
					}
					ifsOrg.setUpdDt(IfspDateTime.strToDate(strs[23], IfspDateTime.YYYY_MM_DD));// 变更时间
					ifsOrg.setCrtDt(IfspDateTime.strToDate(strs[23], IfspDateTime.YYYY_MM_DD));// 创建时间
					ifsOrg.setAreaCode(strs[24]);// 行政区划代码
					ifsOrg.setBrType(strs[25]);// 机构类型
					ifsOrg.setBrPbocId(strs[27]);// 联行行号（银行联行号）
					ifsOrg.setCorpFlag(strs[29]);// 法人标识
					ifsOrg.setBrFic(strs[36]);// 银行机构代码（金融机构编码）
					if ("0".equals(strs[0]) || "".equals(strs[0])) {// 新增
						log.info("---------------------插入数据到本服务器数据库中------------------");
						// 用于区分机构是否通过平台修改过机构信息
						ifsOrg.setUpdFlag("0");// 0未修改1已修改
						ifsOrg.setBrLmName(strs[8]);// 负责人
						ifsOrg.setBrLmAddr(strs[9]);// 地址
						ifsOrg.setBrTel(strs[11]);// 办公电话
						ifsOrg.setBrLmPhone(strs[12]);// 负责人手机
						ifsOrg.setFax(strs[13]);// 传真
						ifsOrg.setBrZipCode(strs[10]);// 邮政编号
						// 查询上级机构为法人机构的机构信息(倒序),执行全量文件时需要将法人机构赋值去掉，并且执行完毕后执行synchronizeOrgCorpId
						List<IfsOrg> ifsOrgList = ifsOrgDao.selectCorpById(ifsOrg.getParBrId());
						if (!ifsOrgList.isEmpty()) {
							// 是-》更新
							IfsOrg ifsOrgVo = ifsOrgList.get(0);
							ifsOrg.setCorpId(ifsOrgVo.getBrId());
						}

						int count = ifsOrgDao.insert(ifsOrg);
						if (count != -1) {
							log.info("新增机构:" + ifsOrg.getBrId() + ",插入成功！");
						} else {
							log.info("新增机构:" + ifsOrg.getBrId() + ",插入失败！");
						}
					} else {// 机构信息变更
						IfsOrg ifsOrgOld = ifsOrgDao.selectByPrimaryKey(ifsOrg.getBrId());
						if ("0".equals(ifsOrgOld.getUpdFlag())) {
							ifsOrg.setBrLmName(strs[8]);// 负责人
							ifsOrg.setBrLmAddr(strs[9]);// 地址
							ifsOrg.setBrTel(strs[11]);// 办公电话
							ifsOrg.setBrLmPhone(strs[12]);// 负责人手机
							ifsOrg.setFax(strs[13]);// 传真
							ifsOrg.setBrZipCode(strs[10]);// 邮政编号
							int b = ifsOrgDao.update(ifsOrg);
							if (b != -1) {
								log.info("机构:" + ifsOrg.getBrId() + ",信息变更成功");
							} else {
								log.info("机构:" + ifsOrg.getBrId() + ",信息变更失败");
							}
						} else {
							int b = ifsOrgDao.update(ifsOrg);
							if (b != -1) {
								log.info("机构:" + ifsOrg.getBrId() + ",信息变更成功");
							} else {
								log.info("机构:" + ifsOrg.getBrId() + ",信息变更失败");
							}
						}
					}
					SynchronizeOrgInfo synchronizeOrgInfo = new SynchronizeOrgInfo();
					synchronizeOrgInfo.setId(IfspId.getUUID32());
					synchronizeOrgInfo.setBrId(ifsOrg.getBrId());
					synchronizeOrgInfo.setSynchrState("00");
					synchronizeOrgInfo.setSynchrTime(new Date());
					ifsOrgDao.insert(synchronizeOrgInfo);
					success++;
				} catch (Exception e) {
					SynchronizeOrgInfo synchronizeOrgInfo = new SynchronizeOrgInfo();
					synchronizeOrgInfo.setId(IfspId.getUUID32());
					synchronizeOrgInfo.setBrId(ifsOrg.getBrId());
					synchronizeOrgInfo.setSynchrState("01");
					synchronizeOrgInfo.setSynchrTime(new Date());
					synchronizeOrgInfo.setBakInfo(str);
					ifsOrgDao.insert(synchronizeOrgInfo);
				}
			}
			br.close();
			log.info("机构文件条数为:" + sum + "同步成功条数为:" + success);
			log.debug("同步机构执行结束!!!!!!!");
			commonResponse.setRespCode("0000");
			commonResponse.setRespMsg("交易成功");
			return commonResponse;
		} catch (Exception e) {
			commonResponse.setRespCode("9999");
			commonResponse.setRespMsg("交易失败");
			return commonResponse;
		}
	}

	// 第一次读取全量机构文件时 给法人机构字段赋值用
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public SynchronizeDataResponse synchronizeOrgCorpId(SynchronizeDataRequest request) throws Exception {
		SynchronizeDataResponse synchronizeDataResponse = new SynchronizeDataResponse();
		log.info("---------------------更新数据到本服务器数据库中------------------");
		// 查询所有机构
		List<IfsOrg> ifsOrgs = ifsOrgDao.selectAll();
		for (IfsOrg ifsOrg : ifsOrgs) {
			// 查询上级机构为法人机构的机构信息(倒序)
			List<IfsOrg> ifsOrgList = ifsOrgDao.selectCorpById(ifsOrg.getBrId());
			if (!ifsOrgList.isEmpty()) {
				// 是-》更新
				IfsOrg ifsOrgVo = ifsOrgList.get(0);
				ifsOrg.setCorpId(ifsOrgVo.getBrId());
				int b = ifsOrgDao.update(ifsOrg);
				if (b != -1) {
					log.info("机构:" + ifsOrg.getBrId() + ",信息变更成功");
				} else {
					log.info("机构:" + ifsOrg.getBrId() + ",信息变更失败");
				}
			}

		}
		return synchronizeDataResponse;
	}

	@Override
	public Boolean canExecute(String taskId) throws Exception {
		int max = 10000;
		SecureRandom srand = new SecureRandom();
		int min = (int) Math.round(srand.nextDouble() * 8000);
		long sleepTime = Math.round(srand.nextDouble() * (max - min));
        if (sleepTime < 1000L){
            sleepTime += 1000L;
        }
		log.info("定时任务" + taskId + " 睡了：" + sleepTime + "毫秒");
		Thread.sleep(sleepTime);

		// 获取整点时间
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 15);
		c.set(Calendar.SECOND, 0);
		Date time = c.getTime();
//		SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
//		Date time = IfspDateTime.strToDate(form.format(new Date()), IfspDateTime.YYYY_MM_DD_HH_MM_SS);
		// 判断整点后是否有执行定时任务
		if (bthScheduleClusterDao.getTask(taskId, time) == 1) {
			Date date = IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS);
			bthScheduleClusterDao.updateExecuteStat(taskId, InetAddress.getLocalHost().getHostAddress(),
					Constans.EXECUTE_STAT_ON, date);
			return true;
		}

		log.info("定时任务" + taskId + "已被其他服务器执行");
		return false;
	}
}
