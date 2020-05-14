package com.scrcu.ebank.ebap.batch.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.opencsv.CSVReader;
import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.message.IfspStringUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.BillDownloadRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BillDownloadResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoTxnTypeDict;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.common.utils.*;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.dao.test.WxBillOuterDao;
import com.scrcu.ebank.ebap.batch.service.BillDownloadService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBaseException;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class BillDownloadServiceImpl implements BillDownloadService {
	
	@Resource
	private BthAliFileDetDao bthAliFileDetDao;
	
	@Resource
	private BthUnionFileDetDao bthUnionFileDetDao;
	@Resource
	
	private DebitTranInfoDao debitTranInfoDao;
	
	@Resource
	private CreditTranInfoDao creditTranInfoDao;
	
	@Resource
	private BthPagyFileSumDao bthPagyFileSumDao;
	
	@Resource
	private BthPagyChkInfDao BthPagyChkInfDao;
	
	@Resource
	private TpamIbankTxnInfoDao tpamIbankTxnInfoDao;
	
	@Resource
	private PagyMchtInfoDao pagyMchtInfoDao;
	
	@Resource
	private KeepAccSoaService keepAccSoaService;

	/**
     * 银联品牌服务费
     */
	@Resource
    private BthUnionBrandFeeInfoDao bthUnionBrandFeeInfoDao;

	@Resource
	private UnionBillOuterDao unionBillOuterDao;
	@Resource
	private CoreBillInfoDao coreBillInfoDao;


    /**
     * 批量系统参数表
     */
    @Resource
    private BthSysParamInfoDao bthSysParamInfoDao;


	
	@Value("${debitLocalFileUrl}")
	private String debitLocalFileUrl;
	@Value("${tallyLocalFileUrl}")
	private String tallyLocalFileUrl;


	/**
	 * 批次插入的数量
	 */
	@Value("${wxBill.batchInsertCount}")
	private Integer wxBillBatchInsertCount;
	/**
	 * 批次插入的数量
	 */
	@Value("${coreBill.batchInsertCount}")
	private Integer coreBatchInsertCount;
	@Value("${unionBill.batchInsertCount}")
	private Integer unionBatchInsertCount;
	/**
	 * 工作线程数量
	 */
	@Value("${wxBill.threadCount}")
	private Integer wxBillThreadCount;
	@Resource
	private WxBillOuterDao wxBillOuterDao;

	/**
	 * 处理线程数量
	 */
	@Value("${unionBill.threadCount}")
	private Integer unionThreadCount;

	/**
	 * 处理线程数量
	 */
	@Value("${coreBill.threadCount}")
	private Integer coreThreadCount;
	/**
	 * 每个线程处理数据量
	 */
//	@Value("${unionBill.threadCapacity}")
//	private Integer unionThreadCapacity;


    @Resource
    private PagyBaseInfoDao  pagyBaseInfoDao;


	/**
	 * 线程池
	 */
	ExecutorService executorCore;

	/**
	 * 银联文件线程池
	 */
	ExecutorService unionExecutor;
	/*
	 * 微信账单下载相关参数
	 */
	@Value("${wxBill.ftpIp}")
	private String wxBillFtpIp;
	@Value("${wxBill.ftpPort}")
	private Integer wxBillFtpPort;
	@Value("${wxBill.ftpUserName}")
	private String wxBillFtpUserName;
	@Value("${wxBill.ftpPwd}")
	private String wxBillFtpPassword;
	@Value("${wxBill.ftpRootPath}")
	private String wxBillFtpRootPath;
	@Value("${wxBill.localRootPath}")
	private String wxBillLocalRootPath;

	/**
	 * 线程池
	 */
	ExecutorService executor;


	/**
     * 微信通道对账单下载
     * @param req
     * @return
     */
	@Override
	public BillDownloadResponse wxBillDownload(BillDownloadRequest req) {
		/*****获取请求参数值********/
		//交易日期
		Date txnDate = DateUtil.getDate(req.getSettleDate());
		log.info("交易日期: " + DateUtil.toDateString(txnDate));
		//对账日期 = 交易日期 + 1
		Date recoDate = DateUtil.getAfterDate(req.getSettleDate(), 1);
		log.info("对账日期: " + DateUtil.toDateString(recoDate));
        /*
         *  清理数据
         */
        //清空微信三方账单明细
		int clearCount = wxBillOuterDao.clear(recoDate);
		log.info("清理微信当日对账单明细完成, 数目:" + clearCount);


		// 待解析文件列表
		Map<String, File> fileMap = new HashMap();
        // 根据通道信息表存的微信三方大商户号来下载相应的微信对账文件
        List<PagyBaseInfo> pagyInfo = pagyBaseInfoDao.selectByPagyNo(Constans.WX_SYS_NO);
        for (PagyBaseInfo pagyBaseInfo : pagyInfo) {
            //清空微信三方账单汇总
            bthPagyFileSumDao.deleteBypagyNoAndDate(pagyBaseInfo.getPagyNo(), req.getSettleDate());
            log.info("清理微信当日对账单汇总完成");
            BthPagyChkInfDao.deleteBypagyNoAndDate(pagyBaseInfo.getPagyNo(), txnDate);
            log.info("清理下载文件记录完成");
            /*
             *  从FTP下载文件到本地
             */

            //远程文件存在路径:  根目录+日期
            File billFile = downloadFormSFTP(wxBillFtpIp, wxBillFtpPort, wxBillFtpUserName, wxBillFtpPassword,
                    getWxRemoteFilePath(wxBillFtpRootPath, req.getSettleDate()),
                    getWxRemoteFileName(pagyBaseInfo.getTpamPagyNo(), txnDate),
                    getWxLocalFilePath(wxBillLocalRootPath, req.getSettleDate()),txnDate);
            log.info("下载文件完成");
            /*
             * 记录下载成功记录
             */
            BthPagyChkInf bthPagyChkInf = new BthPagyChkInf();
            bthPagyChkInf.setPagyNo(pagyBaseInfo.getPagyNo());
            bthPagyChkInf.setChkDataDt(txnDate);
            bthPagyChkInf.setPagySysNo(pagyBaseInfo.getPagySysNo());
            bthPagyChkInf.setReqChkTm(new Date());
            bthPagyChkInf.setGetChkSt("02");//对账文件获取状态00=未执行01=下载中02=下载完成03=不存在对账文件04=需要重试类错误99=系统类错误,需人工干预
            bthPagyChkInf.setChkFilePath(billFile.getPath());
            bthPagyChkInf.setChkFileImpSt("00");//对账文件导入状态00=未导入01=导入中02=导入成功99=导入失败   ?
            bthPagyChkInf.setChkAcctSt("00");//00=未对账01=对账中02=对账完成99=对账异常
            bthPagyChkInf.setCrtTm(new Date());
            bthPagyChkInf.setLstUpdTm(new Date());
            BthPagyChkInfDao.insertSelective(bthPagyChkInf);
            log.info("保存文件下载记录完成");

            fileMap.put(pagyBaseInfo.getPagyNo(),billFile);
        }

        // 解析文件入库
        for (String pagyNo : fileMap.keySet()) {
            /*
             *  解析文件
             */
            //初始化线程池
            initPool();
            try {
                parseAndInsert(recoDate, fileMap.get(pagyNo), req,pagyNo);
            }finally {
                //销毁线程池
                destoryPool();
            }
            log.info("解析文件完成.");

        }


        // 查询对账日期下交易状态为撤销的,将其原交易的对账状态更新为无需参与对账
        int count = wxBillOuterDao.updateSrcByRevoked(recoDate);
        log.info("更新撤销订单的原订单为无需参与对账 , 条数为 : {}",count);


		/*
		 * 响应报文
		 */
		BillDownloadResponse resp = new BillDownloadResponse();
		resp.setDownFlag("1");
		resp.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		resp.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return resp;
	}

	/**
	 * 工作线程
	 *
	 * @param
	 */
	class Handler implements Callable<Integer> {

		private List<WxBillOuter> outerRecordList;

		public Handler(List<WxBillOuter> outerRecordList) {
			this.outerRecordList = outerRecordList;
		}

		@Override
		public Integer call() throws Exception {
			long sTime = System.currentTimeMillis();
			log.info("子线程处理开始时间[{}]ms",sTime);
			if (outerRecordList == null || outerRecordList.isEmpty()) {
				log.warn("微信账单明细为空,无需插入");
				return 0;
			} else {
				int count = wxBillOuterDao.insertBatch(outerRecordList);
				long eTime = System.currentTimeMillis();
				log.info("插入微信账单明细{}-{},数目{}, 耗时{}", outerRecordList.get(0).getTxnSsn(),
						outerRecordList.get(outerRecordList.size() - 1).getTxnSsn(), count, (eTime - sTime));
				return count;
			}
		}
	}

	/**
	 *  从SFTP下载指定文件, 没有文件时进行轮询等待
	 */
	private File downloadFormSFTP(String host, int port, String userName, String password,
							  String remoteFilePath, String remoteFileName, String localFilePath,Date txnDate){
		/*
		 *  检查本地环境
		 */
		//检查本地目录是否存在, 如果不存在创建一个
		File localPath = new File(localFilePath);
		if (!localPath.exists()) {
			log.info("创建本地磁盘目录:" + localFilePath);
			localPath.mkdirs();
		} else {
			if(!localPath.isDirectory()){
				log.info("本地磁盘目录已被文件占用,删除该文件并创建目录: " + localFilePath);
				localPath.delete();
				localPath.mkdirs();
			}
		}
		//检查本地文件是否存在, 删除该文件
		File localFile = new File(localFilePath + remoteFileName);
		log.info("本地目标文件:" + localFile.getPath());
		if(localFile.exists()){
			log.info("本地目标文件已存在,进行删除");
			localFile.delete();
		}
		/*
		 *  建立SFTP连接, 下载文件
		 */
		//远程文件完整路径
		String remoteFile = remoteFilePath + remoteFileName;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		String okFile = remoteFilePath + getAliOkFileNm(txnDate);
		log.info("远程目标文件:" + remoteFile);
		//判断文件是否存在, 如果不存在则轮询等待
		SftpUtil sftpUtil = new SftpUtil(host, userName, password, port);
		boolean exitFlag = checkWxFileExist(sftpUtil, remoteFile,okFile);
		//boolean exitFlag = checkBalFileExist(sftpUtil, remoteFile);
		if (exitFlag){
			ChannelSftp sftp = sftpUtil.connectSFTP();
			log.info("---------------------SFTP获取本行对账文件名------------------");
			sftpUtil.download(remoteFilePath, remoteFileName, localFilePath, localFile.getName(), sftp);
			log.info("---------------------SFTP断开连接------------------");
			sftpUtil.disconnected(sftp);
		}else {
			log.info("远程目录文件不存在");
			throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程目录文件不存在");
		}
		/*
		 * 检查本地文件是否已存在
		 */
		if(!localFile.exists()){
			throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"本地csv文件不存在");
		}
		return localFile;
	}


	/**
	 * 解析并记录文件
     * @param billFile
     * @param req
     * @param pagyNo
     */
	private void parseAndInsert(Date recoDate, File billFile, BillDownloadRequest req, String pagyNo){
		//对账明细
		List<WxBillOuter> outerRecordList = new ArrayList<>();
		//对账汇总
		List<BthPagyFileSum> bthPagyFileSumList = new ArrayList<>();
		//线程处理结果
		List<Future> futureList = new ArrayList<>();
		try(
				InputStreamReader in = new InputStreamReader(new FileInputStream(billFile), Charset.forName("UTF-8"));
				CSVReader reader = new CSVReader(in)
		)
		{
			String[] nextLine;
			boolean sumFlag = false; //汇总
			WxBillOuter outerRecord;
			int index = 0; //当前处理的行数
			while ((nextLine = reader.readNext()) != null) {
				if(index == 0){ //跳过表头处理
					index ++;
					continue;
				}else {
					if(!sumFlag){ //处理明细记录
						if(sumFlag = nextLine[0].equals("总交易单数")){ //汇总标题不处理
							index ++;
							continue;
						}
					}
					if(!sumFlag){
						outerRecord = new WxBillOuter(recoDate, nextLine);
						outerRecordList.add(outerRecord);
						/*
						 * 批量插入
						 */
						if(outerRecordList.size() == wxBillBatchInsertCount){
							Future future = executor.submit(new Handler(outerRecordList));
							futureList.add(future);
							//清理集合
							outerRecordList = new ArrayList<>();
						}
					}else {
						BthPagyFileSum bthPagyFileSum = new BthPagyFileSum();
						bthPagyFileSum.setPagySysNo(Constans.WX_SYS_NO);
						bthPagyFileSum.setPagyNo(pagyNo);
						bthPagyFileSum.setChkDataDt(req.getSettleDate());
						bthPagyFileSum.setTxnTotalCnt(new BigDecimal(ConstantUtil.removeSplitSymbol(nextLine[0])));
						bthPagyFileSum.setTxnTotalAmt(new BigDecimal(ConstantUtil.removeSplitSymbol(nextLine[1])).movePointRight(2));
						bthPagyFileSum.setRefTotalAmt(new BigDecimal(ConstantUtil.removeSplitSymbol(nextLine[2])).movePointRight(2));
						bthPagyFileSum.setFeeTotalAmt(new BigDecimal(ConstantUtil.removeSplitSymbol(nextLine[4])).movePointRight(2));
						bthPagyFileSum.setRecptAmt(new BigDecimal(ConstantUtil.removeSplitSymbol(nextLine[5])).movePointRight(2));
						bthPagyFileSum.setSumRmk("汇总信息");
						bthPagyFileSum.setCrtTm(IfspDateTime.getYYYYMMDDHHMMSS());
						bthPagyFileSumList.add(bthPagyFileSum);
					}
				}
				index ++;
			}
			/*
			 *
			 */
			if(outerRecordList.size() > 0){
				Future future = executor.submit(new Handler(outerRecordList));
				futureList.add(future);
			}
			/*
 			 * 插入汇总
			 */
			if(bthPagyFileSumList.size() > 0){
				bthPagyFileSumDao.insertSelectiveList(bthPagyFileSumList);
			}
			/*
			 * 获取明细处理结果
			 */
			for (Future future : futureList) {
				try {
					future.get(10, TimeUnit.MINUTES);
				} catch (Exception e) {
					log.error("子线程处理异常: ", e);
					//取消其他任务
					destoryPool();
					log.warn("其他子任务已取消.");
					//返回结果
					throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常:" + e.getMessage());
				}
			}
			log.info("保存微信对账明细完成, 数目:" + (index - 3));
			log.info("保存微信对账汇总完成");
		}catch (IfspBaseException e){
			throw e;
		}catch (Exception e){
			log.error("文件解析入库异常: ", e);
			throw new IfspSystemException(SystemConfig.getSysErrorCode(), "文件解析入库失败");
		}
	}

	/**
	 * 获取微信账单在FTP服务器上的路径
	 * @param rootPath
	 * @param txnDate
	 * @return
	 */
	private String getWxRemoteFilePath(String rootPath , String txnDate){
		return rootPath + txnDate + "/";
	}

	/**
	 * 获取微信账单文件名称
	 * @param bankMchtId
	 * @param txnDate
	 * @return
	 */
	private String getWxRemoteFileName(String bankMchtId , Date txnDate){
		return bankMchtId + "All" + new DateTime(txnDate).toString(DateTimeFormat.forPattern("yyyy-MM-dd")) + ".csv";
	}

	/**
	 * 获取微信账单在本地服务器上的路径
	 * @param rootPath
	 * @param txnDate
	 * @return
	 */
	private String getWxLocalFilePath(String rootPath , String txnDate){
		return rootPath + txnDate + "/";
	}

	private void initPool() {
		destoryPool();
		/*
		 * 构建
		 */
		log.info("====初始化线程池(start)====");
		executor = Executors.newFixedThreadPool(wxBillThreadCount, new ThreadFactory() {
			AtomicInteger atomic = new AtomicInteger();
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "wxBillHander_" + this.atomic.getAndIncrement());
			}
		});
		log.info("====初始化线程池(end)====");
	}


	private void destoryPool() {
		log.info("====销毁线程池(start)====");
		/*
		 * 初始化线程池
		 */
		if (executor != null) {
			log.info("线程池为null, 无需清理");
			/*
			 * 关闭线程池
			 */
			try {
				executor.shutdown();
				if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("awaitTermination interrupted: " + e);
				executor.shutdownNow();
			}
		}
		log.info("====销毁线程池(end)====");
	}


	/**
     * zfb通道对账单下载
     * @param request
     * @return
     */
	@Override
	public BillDownloadResponse aliBillDownload(BillDownloadRequest request,HashMap<Object, Object> hashMap) throws Exception {
		/*****获取请求参数值********/
		String pagyNo = request.getPagyNo();//通道编号
		String pagySysNo = request.getPagySysNo();//通道系统编号
		String settleDate = request.getSettleDate();// 清算日期
		Date strToDate = IfspDateTime.strToDate(settleDate, IfspDateTime.YYYY_MM_DD);
		/*********初始化响应对象***********/
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
		log.info("---------------------删除表数据------------------");
//		1.	根据清算日期和通道编号删除支付宝对账文件明细表和导入对账汇总信息表(BTH_PAGY_FILE_SUM)以及对账信息记录表(BTH_PAGY_CHK_INF)
		bthAliFileDetDao.deleteBypagyNoAndDate(pagyNo,settleDate);
		bthPagyFileSumDao.deleteBypagyNoAndDate(pagyNo,settleDate);
		BthPagyChkInfDao.deleteBypagyNoAndDate(pagyNo,strToDate);
//		2.	通过通道系统编号查询FTP配置信息表，获取下载对账单的ftp信息（ip，端口号，用户名，密码，对账单名称，对账单远程路径，对账单本地路径，本地文件名）
		//TODO 查询ftp信息表获取基本信息
		String ip=(String) hashMap.get("zfbBillDownIp");
		String port=(String) hashMap.get("zfbBillDownport");
		String userName=(String) hashMap.get("zfbUserName");
		String password=(String) hashMap.get("zfbPassWord");
		String remoteUrl=(String) hashMap.get("zfbRemoteUrl")+settleDate+"/";
		String remoteFileName=(String) hashMap.get("zfbRemoteFileName");
		remoteFileName = remoteFileName.replace("YYYYMMDD", settleDate);
		String remoteCsvFileName = remoteFileName.substring(0, remoteFileName.length()-8)+"_DETAILS.csv";
		String remoteCsvSumFileName = remoteFileName.substring(0, remoteFileName.length()-8)+"_SUMMARY.csv";
		String localFileUrl=(String) hashMap.get("zfbLocalFileUrl");
		String localFileName=(String) hashMap.get("zfbLocalFileName");
		localFileName = localFileName.replace("YYYYMMDD", settleDate);
		String localCsvFileName=localFileName.substring(0, localFileName.length()-8)+"_DETAILS.csv";
		String localCsvSumFileName=localFileName.substring(0, localFileName.length()-8)+"_SUMMARY.csv";

//		3.	插入对账信息记录表BTH_PAGY_CHK_INF（对账文件获取状态：未执行）
		BthPagyChkInf bthPagyChkInf = new BthPagyChkInf();
		bthPagyChkInf.setPagyNo(pagyNo);
		bthPagyChkInf.setChkDataDt(strToDate);
		bthPagyChkInf.setPagySysNo(pagySysNo);
		bthPagyChkInf.setReqChkTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		bthPagyChkInf.setGetChkSt("00");//对账文件获取状态00=未执行01=下载中02=下载完成03=不存在对账文件04=需要重试类错误99=系统类错误,需人工干预
//		bthPagyChkInf.setGetChkRspcode(null);
//		bthPagyChkInf.setGetChkRspmsg(null);
		bthPagyChkInf.setChkFilePath(localFileUrl+localFileName);
		bthPagyChkInf.setChkFileImpSt("00");//对账文件导入状态00=未导入01=导入中02=导入成功99=导入失败   ?
//		bthPagyChkInf.setChkFileWrtm(null);
//		bthPagyChkInf.setChkAcctSttm(null);
		bthPagyChkInf.setChkAcctSt("00");//00=未对账01=对账中02=对账完成99=对账异常
//		bthPagyChkInf.setChkAcctMsg(null);
//		bthPagyChkInf.setChkAcctEdtm(null);
//		bthPagyChkInf.setChkAcctRst(null);
//		bthPagyChkInf.setChkRmk(null);
		bthPagyChkInf.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		bthPagyChkInf.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		BthPagyChkInfDao.insertSelective(bthPagyChkInf);
//		4.	根据ftp信息下载对账文件，下载失败抛错中断（重新发起），下载成功则读取对账文件信息解析到微信对账文件明细表BTH_ALI_FILE_DET（如果解析失败抛错中断，等待重新发起），根据汇总信息加载到导入对账汇总信息表BTH_PAGY_FILE_SUM，入库。
//		4.1从ftp下载对账文件
		File filePath = new File(localFileUrl);
		// 如果文件夹不存在则创建
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("文件存放本地目录不存在");
			filePath.mkdirs();
		} else {
			log.info("文件存放本地目录存在");
		}

		String sftpFilePath = remoteUrl + remoteCsvFileName; // 远程明细文件目录
		log.info("远程明细文件目录路径为:" + sftpFilePath);
		String sftpSumFilePath = remoteUrl + remoteCsvSumFileName; // 远程汇总文件目录
		log.info("远程汇总文件目录路径为:" + sftpSumFilePath);
		String localCsvFilePath = localFileUrl + localCsvFileName; // 本地存放路径
		log.info("本地csv存放路径为:" + localCsvFilePath);
		String localCsvSumFilePath = localFileUrl + localCsvSumFileName; // 本地存放路径
		log.info("本地csv存放路径为:" + localCsvSumFilePath);

        File downFile = new File(localCsvFilePath);
        if(downFile.exists()){
            log.info("明细文件已存在");
            downFile.delete();
        }else{
            log.info("明细文件不存在,将下载");
        }
        File downSumFile = new File(localCsvSumFilePath);
        if(downSumFile.exists()){
            log.info("汇总文件已存在");
            downSumFile.delete();
        }else{
            log.info("汇总文件不存在,将下载");
        }


        SftpUtil sftpUtil = new SftpUtil(ip, userName, password, Integer.parseInt(port));
        // 等待文件是否存在
        boolean exitFlag = checkBalFileExist2(sftpUtil, sftpFilePath, sftpSumFilePath);

        if (exitFlag){
            ChannelSftp sftp = sftpUtil.connectSFTP();
            log.info("远程目录文件存在");
            log.info("---------------------SFTP获取本行对账文件名------------------");
            // 下载明细文件
            sftpUtil.download(remoteUrl, remoteCsvFileName, localFileUrl, localCsvFileName, sftp);
            // 下载汇总文件
            sftpUtil.download(remoteUrl, remoteCsvSumFileName, localFileUrl, localCsvSumFileName, sftp);
            log.info("---------------------SFTP断开连接------------------");
            sftpUtil.disconnected(sftp);

        }else {
            log.info("远程目录文件不存在");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程目录文件不存在");

        }


		
//		4.3解析文件（以逗号分隔），将解析结果加载到BTH_ALI_FILE_DET
		log.info("------------ 解析业务明细文件(开始)------------------");
        try {
            //读取业务明细excel对账文件
            
            File file =  new File(localCsvFilePath);
            if(!file.exists()){
            	throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"文件csv不存在");
            }
            File sumFile =  new File(localCsvSumFilePath);
            if(!sumFile.exists()){
            	throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"汇总文件csv不存在");
            }
            //获取对账单成功
            //对账明细
            List<BthAliFileDet> bthAliFileDetList = new ArrayList<BthAliFileDet>();
            //对账汇总金额
            List<BthPagyFileSum> bthPagyFileSumList = new ArrayList<BthPagyFileSum>();
//            ByteArrayInputStream in = null;
            BigDecimal refAmt = new BigDecimal(0);

            List<String> list = new ArrayList<String>();
            List<String> sumList = new ArrayList<String>();
			/**
			 * 解析入库对账文件
			 */
			log.info("------------ 解析支付宝_业务明细文件(开始)------------------");
			//获取对账单文件流
            try (FileInputStream fis = new FileInputStream(localCsvFilePath);
				 InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
				 BufferedReader br = new BufferedReader(isr)){
                String stemp = null;
                try {
                    while ((stemp = br.readLine()) != null) {
                        list.add(stemp);
                    }
                    log.info("受理支付宝通道业务(对账单下载) 逻辑处理:[ 业务明细文件读取成功! ]");
                } catch (IOException e) {
                    log.error("受理支付宝通道业务(对账单下载) 逻辑处理:[ 业务明细文件读取失败!]",e);
                }
                br.close();//关闭输出流

                log.info("受理支付宝通道业务(对账单下载) 逻辑处理:[ 读取excel行数:["+list.size()+"] ]");
                for(int i = 5; i < list.size()- 4; i++){
                    /**将一行数据初始化到bthAliFileDet*/
                	bthAliFileDetList.add(initializeBillInfo(i, list, settleDate, pagyNo,pagySysNo,refAmt));
                }
                log.info("------------ 解析支付宝_业务明细文件(结束)------------------");
                
                /************汇总文件解析****************/
                log.info("------------ 解析支付宝_汇总文件(开始)------------------");
                String strTemp = null;
				//获取对账单文件流
				try (FileInputStream fisSum = new FileInputStream(localCsvSumFilePath);
					 InputStreamReader isrSum = new InputStreamReader(fisSum,"UTF-8");
					 BufferedReader brSum = new BufferedReader(isrSum)){
                    while ((strTemp = brSum.readLine()) != null) {
                        sumList.add(strTemp);
                    }
                    log.info("受理支付宝通道业务(对账单下载) 逻辑处理:[ 业务汇总文件读取成功! ]");
                } catch (IOException e) {
                    log.error("受理支付宝通道业务(对账单下载) 逻辑处理:[ 业务汇总文件读取失败!]",e);
                }
//                brSum.close();//关闭输出流

                log.info("受理支付宝通道业务(对账单下载) 逻辑处理:[ 读取excel行数:["+sumList.size()+"] ]");
                for(int i = sumList.size()- 3; i < sumList.size()- 2; i++){
                    /**将一行数据初始化到bthAliFileDet*/
                	bthPagyFileSumList.add(initializeSumBillInfo(i, sumList, settleDate, pagyNo,pagySysNo,refAmt));
                }
                log.info("------------ 解析支付宝_汇总文件(结束)------------------");

                log.info("------------ 业务明细文件内容入库(开始)------------------");
                try {
                    log.info("========获取支付宝对账文件初始化info成功 , 共 ["+bthAliFileDetList.size()+"] 条对账信息!开始插入对账单流水表");
                    updateAliBillData(pagyNo,settleDate, bthAliFileDetList, bthPagyFileSumList);
                    
                }catch (Exception e){
                    log.error("业务明细文件内容入库失败:", e);
                    billDownloadResponse.setRespCode("9999");
                    billDownloadResponse.setRespMsg("业务明细文件内容入库失败.");
                    return billDownloadResponse;
                }
                log.info("------------ 业务明细文件内容入库(结束)------------------");

            } catch (IOException e) {
                log.error("下载对账单失败:", e);
                throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"下载对账单失败");
            }

        }catch (Exception e){
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
//		5.	更新对账信息记录表BTH_PAGY_CHK_INF信息（对账文件获取状态：下载完成）
		bthPagyChkInf.setGetChkSt("02");
		BthPagyChkInfDao.updateByPrimaryKeySelective(bthPagyChkInf);
		
//		6.  根据结果补充响应对象
		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}


	/**
     * 银联通道对账单下载
     * @param request
     * @return
     */
	@Override
	public BillDownloadResponse unionBillDownload(BillDownloadRequest request,HashMap<Object, Object> hashMap) throws Exception {
		/*****获取请求参数值********/
		String pagyNo = request.getPagyNo();//通道编号
		String pagySysNo = request.getPagySysNo();//通道系统编号
		String settleDate = request.getSettleDate();// 清算日期
		/*********初始化响应对象***********/
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
		SimpleDateFormat form = new SimpleDateFormat( "yyyyMMdd");
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
		log.info("---------------------删除表数据------------------");
//		1.	根据清算日期和通道编号删除微信对账文件明细表(BTH_WX_FILE_DET)和导入对账汇总信息表(BTH_PAGY_FILE_SUM)以及对账信息记录表(BTH_PAGY_CHK_INF)
		bthUnionFileDetDao.deleteBypagyNoAndDate(pagyNo,settleDate);
		bthPagyFileSumDao.deleteBypagyNoAndDate(pagyNo,settleDate);
		BthPagyChkInfDao.deleteBypagyNoAndDate(pagyNo,form.parse(settleDate));
//		2.	通过通道系统编号查询FTP配置信息表，获取下载对账单的ftp信息（ip，端口号，用户名，密码，对账单名称，对账单远程路径，对账单本地路径，本地文件名）
		String ip=(String) hashMap.get("unionBillDownIp");
		String port=(String) hashMap.get("unionBillDownport");
		String userName=(String) hashMap.get("unionUserName");
		String password=(String) hashMap.get("unionPassWord");
		String remoteUrl=(String) hashMap.get("unionRemoteUrl")+settleDate+"/";
		String remoteFileName=(String) hashMap.get("unionRemoteFileName");
		remoteFileName = remoteFileName.replace("YYMMDD", settleDate.substring(2));
		String localFileUrl=(String) hashMap.get("unionLocalFileUrl");
		String localFileName=(String) hashMap.get("unionLocalFileName");
		localFileName = localFileName.replace("YYMMDD", settleDate.substring(2));
//		3.	插入对账信息记录表BTH_PAGY_CHK_INF（对账文件获取状态：未执行）
		BthPagyChkInf bthPagyChkInf = new BthPagyChkInf();
		bthPagyChkInf.setPagyNo(pagyNo);
		bthPagyChkInf.setChkDataDt(form.parse(settleDate));
		bthPagyChkInf.setPagySysNo(pagySysNo);
		bthPagyChkInf.setReqChkTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		bthPagyChkInf.setGetChkSt("00");//对账文件获取状态00=未执行01=下载中02=下载完成03=不存在对账文件04=需要重试类错误99=系统类错误,需人工干预
//		bthPagyChkInf.setGetChkRspcode(null);
//		bthPagyChkInf.setGetChkRspmsg(null);
		bthPagyChkInf.setChkFilePath(localFileUrl+localFileName);
		bthPagyChkInf.setChkFileImpSt("00");//对账文件导入状态00=未导入01=导入中02=导入成功99=导入失败   ?
//		bthPagyChkInf.setChkFileWrtm(null);
//		bthPagyChkInf.setChkAcctSttm(null);
		bthPagyChkInf.setChkAcctSt("00");//00=未对账01=对账中02=对账完成99=对账异常
//		bthPagyChkInf.setChkAcctMsg(null);
//		bthPagyChkInf.setChkAcctEdtm(null);
//		bthPagyChkInf.setChkAcctRst(null);
//		bthPagyChkInf.setChkRmk(null);
		bthPagyChkInf.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		bthPagyChkInf.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		BthPagyChkInfDao.insertSelective(bthPagyChkInf);
//		4.	根据ftp信息下载对账文件，下载失败抛错中断（重新发起），下载成功则读取对账文件信息解析到微信对账文件明细表BTH_WX_FILE_DET（如果解析失败抛错中断，等待重新发起），根据汇总信息加载到导入对账汇总信息表BTH_PAGY_FILE_SUM，入库。
//		4.1从ftp下载对账文件
		File filePath = new File(localFileUrl);
		// 如果文件夹不存在则创建
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("文件存放本地目录不存在");
			filePath.mkdirs();
		} else {
			log.info("文件存放本地目录存在");
		}

		String sftpFilePath = remoteUrl + remoteFileName; // 远程文件目录
		log.info("远程文件目录路径为:" + sftpFilePath);
		String localFilePath = localFileUrl + localFileName; // 本地存放路径
//		String localFilePath = "C:/Users/Administrator/Desktop/billTest/IND18062501ACOMN"; // 本地存放路径
		log.info("本地存放路径为:" + localFilePath);

        File downFile = new File(localFilePath);
        if(downFile.exists()){
            log.info("文件已存在");
            downFile.delete();
        }else{
            log.info("文件不存在,将下载");
        }



        SftpUtil sftpUtil = new SftpUtil(ip, userName, password, Integer.parseInt(port));

        // 等待文件是否存在
        boolean exitFlag = checkBalFileExist(sftpUtil, sftpFilePath);

        if (exitFlag){
            ChannelSftp sftp = sftpUtil.connectSFTP();
            log.info("远程目录文件存在");
            log.info("---------------------SFTP获取本行对账文件名------------------");
            sftpUtil.download(remoteUrl, remoteFileName, localFileUrl, localFileName, sftp);
            log.info("---------------------SFTP断开连接------------------");
            sftpUtil.disconnected(sftp);
        }else {
            log.info("远程目录文件不存在");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程目录文件不存在");

        }
		
//		4.3解析文件（按照字符串长度截取），将解析结果加载到表
		log.info("------------ 解析业务明细文件(开始)------------------");
        try {
            // 查询出扫码支付的商户以过滤文件中其他商户
            Set<String> set = new HashSet<>();
            set.add("607");
            List<String> mchtNos = pagyMchtInfoDao.selectAllMchtNo(set);

            //读取业务明细excel对账文件
            //对账明细
            List<BthUnionFileDet> bthUnionFileDetList = new ArrayList<BthUnionFileDet>();
            //对账汇总金额
            List<BthPagyFileSum> bthPagyFileSumList = new ArrayList<BthPagyFileSum>();
//            ByteArrayInputStream in = null;

            int i = 0;// 记录行数
            int txnTotalCnt =0;
            BigDecimal txnTotalAmt = new BigDecimal(0);
            int refTotalCnt = 0;
            BigDecimal refTotalAmt = new BigDecimal(0);
            BigDecimal feeTotalAmt = new BigDecimal(0);
            BigDecimal recptAmt = new BigDecimal(0);
			//获取对账单文件流
			try (FileInputStream fis = new FileInputStream(localFilePath);
				InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
				BufferedReader br = new BufferedReader(isr)){

                /**
                 * 解析入库对账文件
                 */
                log.info("受理通道业务(下载对账单) 逻辑处理 ");
                while(br.ready()){
                    String tmpLine = br.readLine();
                    if(IfspStringUtil.isBlank(tmpLine)){
                        i++;
                        continue;
                    }
                    // 从银联COMN文件提取银联扫码支付流水
                    if("03".equals(tmpLine.substring(188, 190))||"04".equals(tmpLine.substring(188, 190))||"93".equals(tmpLine.substring(188, 190))||"94".equals(tmpLine.substring(188, 190))){
                        // 过滤受卡方标志码不在扫码支付通道表中的银联对账单流水
                        if (!mchtNos.contains(tmpLine.substring(127, 142).trim())){
                            i++;
                            continue;
                        }
                        BthUnionFileDet bthUnionFileDet = new BthUnionFileDet();
                    	bthUnionFileDet.setProxyInsCode(tmpLine.substring(0, 11)); //代理机构标识码	PROXY_INS_CODE
                    	bthUnionFileDet.setSendInsCode(tmpLine.substring(12, 23)); //发送机构标识码	SEND_INS_CODE
                    	bthUnionFileDet.setTraceNum(tmpLine.substring(24, 30)); //系统跟踪号	TRACE_NUM
                    	bthUnionFileDet.setTransDate(tmpLine.substring(31, 41)); //交易传输时间	TRANS_DATE
                    	bthUnionFileDet.setAcctNo(tmpLine.substring(42, 61)); //主账号	ACCT_NO
                    	bthUnionFileDet.setTransAmt(new BigDecimal(tmpLine.substring(62, 74))); //交易金额	TRANS_AMT
                    	bthUnionFileDet.setPartPmsAmt(tmpLine.substring(75, 87)); //部分代收时的承兑金额	PART_PMS_AMT
                    	bthUnionFileDet.setCustomerFee(tmpLine.substring(88, 100)); //持卡人交易手续费	CUSTOMER_FEE
                    	bthUnionFileDet.setMsgType(tmpLine.substring(101, 105)); //报文类型	MSG_TYPE
                    	if(tmpLine.substring(106, 112).startsWith("00")){
                    		bthUnionFileDet.setTransCode("10"); //交易类型码	TRANS_CODE   10=交易 11=撤销/冲正 12=退款
                    	}else if(tmpLine.substring(106, 112).startsWith("20")){
                    		bthUnionFileDet.setTransCode("12"); //交易类型码	TRANS_CODE
                    		refTotalCnt++;
                    		refTotalAmt = refTotalAmt.add(new BigDecimal(tmpLine.substring(62, 74)));
                    	}
                    	bthUnionFileDet.setMerType(tmpLine.substring(113, 117)); //商户类型	MER_TYPE
                    	bthUnionFileDet.setRecCardTerminalCode(tmpLine.substring(118, 126)); //受卡机终端标识码	REC_CARD_TERMINAL_CODE
                    	bthUnionFileDet.setRecCardCode(tmpLine.substring(127, 142)); //受卡方标志码	REC_CARD_CODE
                    	bthUnionFileDet.setRetrivalNo(tmpLine.substring(143, 155)); //检索参考号	RETRIVAL_NO
                    	bthUnionFileDet.setSevCdtCode(tmpLine.substring(156, 158)); //服务点条件码	SEV_CDT_CODE
                    	bthUnionFileDet.setAuthRespCode(tmpLine.substring(159, 165)); //授权应答码	AUTH_RESP_CODE
                    	bthUnionFileDet.setRecInsCode(tmpLine.substring(166, 177)); //接收机构标识码	REC_INS_CODE
                    	bthUnionFileDet.setOrgTraceNum(tmpLine.substring(178, 184)); //原始交易的系统跟踪号	ORG_TRACE_NUM
                    	bthUnionFileDet.setTransRespCode(tmpLine.substring(185, 187)); //交易返回码	TRANS_RESP_CODE
                    	bthUnionFileDet.setSevInputWay(tmpLine.substring(188, 191)); //服务点输入方式	SEV_INPUT_WAY
                    	String hearGetFee = tmpLine.substring(192, 204).trim().replaceAll("^[0]+", "").equals("")?"0":tmpLine.substring(192, 204).trim().replaceAll("^[0]+", "");
                    	bthUnionFileDet.setHearGetFee(new BigDecimal(hearGetFee)); //受理方应收手续费	HEAR_GET_FEE
                    	String hearPayFee = tmpLine.substring(205, 217).trim().replaceAll("^[0]+", "").equals("")?"0":tmpLine.substring(205, 217).trim().replaceAll("^[0]+", "");
                    	bthUnionFileDet.setHearPayFee(new BigDecimal(hearPayFee)); //受理方应付手续费	HEAR_PAY_FEE
                    	String routeSevFee = tmpLine.substring(218, 230).trim().substring(1).replaceAll("^[0]+", "").equals("")?"0":tmpLine.substring(218, 230).trim().substring(1).replaceAll("^[0]+", "");
                    	bthUnionFileDet.setRouteSevFee(new BigDecimal(routeSevFee)); //转接服务费	ROUTE_SEV_FEE
                    	bthUnionFileDet.setSeReverseFlg(tmpLine.substring(231, 232)); //单双转换标志	SE_REVERSE_FLG
                    	bthUnionFileDet.setCardSeq(tmpLine.substring(233, 236)); //卡片序列号	CARD_SEQ
                    	bthUnionFileDet.setTerLoadAbity(tmpLine.substring(237, 238)); //终端读取能力	TER_LOAD_ABITY
                    	bthUnionFileDet.setIcCdtCode(tmpLine.substring(239, 240)); //IC卡条件码	IC_CDT_CODE
                    	bthUnionFileDet.setOrgTransDate(tmpLine.substring(241, 251)); //原始交易日期时间	ORG_TRANS_DATE
                    	bthUnionFileDet.setSndCardInsCode(tmpLine.substring(252, 263)); //发卡机构标识码	SND_CARD_INS_CODE
                    	bthUnionFileDet.setTransRegion(tmpLine.substring(264, 265)); //交易地域标志	TRANS_REGION
                    	bthUnionFileDet.setTerminalType(tmpLine.substring(266, 268)); //终端类型	TERMINAL_TYPE
                    	bthUnionFileDet.setEciFlag(tmpLine.substring(269, 271)); //ECI标志	ECI_FLAG
                    	String scdPlusFee = tmpLine.substring(272, 284).trim().replaceAll("^[0]+", "").equals("")?"0":tmpLine.substring(272, 284).trim().replaceAll("^[0]+", "");
                    	bthUnionFileDet.setScdPlusFee(new BigDecimal(scdPlusFee)); //分期付款附加手续费	SCD_PLUS_FEE
                    	bthUnionFileDet.setOthMsg(tmpLine.substring(285, 299)); //其他信息	OTH_MSG
                    	bthUnionFileDet.setTransCard(tmpLine.substring(300, 319)); //转入卡卡号	TRANS_CARD
                    	bthUnionFileDet.setPeriodSum(tmpLine.substring(320, 322)); //分期付款期数	PERIOD_SUM
                    	bthUnionFileDet.setOrderId(tmpLine.substring(323, 363)); //订单号	ORDER_ID
                    	bthUnionFileDet.setBusPayMode(tmpLine.substring(364, 368)); //业务支付模式	BUS_PAY_MODE
                    	bthUnionFileDet.setReserved(tmpLine.substring(369, 500)); //保留使用	RESERVED
                    	bthUnionFileDet.setPagySysNo(pagySysNo); //通道系统编号	PAGY_SYS_NO
                    	bthUnionFileDet.setPagyNo(pagyNo); //通道编号	PAGY_NO
                    	bthUnionFileDet.setChkDataDt(settleDate); //对账日期	CHK_DATA_DT
                    	bthUnionFileDet.setChkAcctSt("00"); //对账状态	CHK_ACCT_ST
                    	bthUnionFileDet.setChkRst(""); //对账结果	CHK_RST
                    	bthUnionFileDet.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS()); //最后更新时间	LST_UPD_TM
                        // 计算手续费
                        BigDecimal feeAmt = getUnionFee(tmpLine.substring(192, 204).trim(), tmpLine.substring(205, 217).trim(), tmpLine.substring(218, 230).trim());
                        bthUnionFileDet.setCustomerFee(String.valueOf(feeAmt));

                        bthUnionFileDetList.add(bthUnionFileDet);
                    	i++;
                    	txnTotalCnt++;
                    	txnTotalAmt = txnTotalAmt.add(new BigDecimal(tmpLine.substring(62, 74)));
                    	feeTotalAmt = feeTotalAmt.add(feeAmt);
                    	recptAmt = txnTotalAmt.subtract(refTotalAmt);
                    }else{
                    	i++;
                    	continue;
                    }
                }

            } catch (IOException e) {
                log.error("下载对账单失败:", e);
                throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"下载对账单失败");
            }
            BthPagyFileSum bthPagyFileSum = new BthPagyFileSum();
            bthPagyFileSum.setPagySysNo(pagySysNo);
            bthPagyFileSum.setPagyNo(pagyNo);
			bthPagyFileSum.setChkDataDt(settleDate);
			bthPagyFileSum.setTxnTotalCnt(new BigDecimal(txnTotalCnt));
			bthPagyFileSum.setTxnTotalAmt(txnTotalAmt);
			bthPagyFileSum.setRefTotalCnt(new BigDecimal(refTotalCnt));
			bthPagyFileSum.setRefTotalAmt(refTotalAmt);
			bthPagyFileSum.setFeeTotalAmt(feeTotalAmt);
			bthPagyFileSum.setRecptAmt(recptAmt);
			bthPagyFileSum.setSumRmk("汇总信息");
            bthPagyFileSum.setCrtTm(IfspDateTime.getYYYYMMDDHHMMSS());
            bthPagyFileSumList.add(bthPagyFileSum);
            /**
             * 数据库操作
             */
            log.info("========获取银联对账文件成功 , 共 ["+bthUnionFileDetList.size()+"] 条对账信息!");
            updateUnionBillData(pagyNo,settleDate, bthUnionFileDetList, bthPagyFileSumList);
            
            billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        
        }catch (Exception e){
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
//		5.	更新对账信息记录表BTH_PAGY_CHK_INF信息（对账文件获取状态：下载完成）
		bthPagyChkInf.setGetChkSt("02");
		BthPagyChkInfDao.updateByPrimaryKeySelective(bthPagyChkInf);
		
//		6.  根据结果补充响应对象
		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}

	@Override
	public BillDownloadResponse unionAllBillDownload(BillDownloadRequest request, HashMap<Object, Object> hashMap) throws Exception {

		String qrcPagyNo = "607000000000001";
		String onlinPagyNo = "608000000000001";
		String qrcPagySysNo = "607";
		String onlinPagySysNo = "608";
		// 清算日期
		String settleDate = request.getSettleDate();

		//获取的订单数据的交易日期
		Date txnDate = getRecoDate(request.getSettleDate());
		//计算对账日期 = 交易日期 + 1
		Date recoDate = new DateTime(txnDate).plusDays(1).toDate();

		/*********初始化响应对象***********/
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
		SimpleDateFormat form = new SimpleDateFormat( "yyyyMMdd");
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
		log.info("---------------------删除银联文件表数据------------------");
		//1.根据清算日期和通道编号删除银联对账文件明细表(BTH_UNION_FILE_DET)和导入对账汇总信息表(BTH_PAGY_FILE_SUM)以及对账信息记录表(BTH_PAGY_CHK_INF)
		int countFile = unionBillOuterDao.clear(recoDate);
		log.info("删除银联文件明细表数据{}条",countFile);
		log.info("删除文件汇总表数据");
		bthPagyFileSumDao.deleteByPagySysNoAndDate(qrcPagySysNo+" ",settleDate);
		bthPagyFileSumDao.deleteByPagySysNoAndDate(onlinPagySysNo+" ",settleDate);
		log.info("删除对账结果表数据今日数据 ,通道[608]");
		BthPagyChkInfDao.deleteBypagyNoAndDate(qrcPagyNo,form.parse(settleDate));
//		BthPagyChkInfDao.deleteBypagyNoAndDate(onlinPagySysNo,form.parse(settleDate));
		//2.通过通道系统编号查询FTP配置信息表，获取下载对账单的ftp信息（ip，端口号，用户名，密码，对账单名称，对账单远程路径，对账单本地路径，本地文件名）
		String ip=String.valueOf(hashMap.get("unionBillDownIp"));
		String port=String.valueOf(hashMap.get("unionBillDownport"));
		String userName=String.valueOf(hashMap.get("unionUserName"));
		String userPwd=String.valueOf(hashMap.get("unionPwd"));
		String remoteUrl= hashMap.get("unionRemoteUrl")+settleDate+"/";
		String remoteFileName=String.valueOf( hashMap.get("unionRemoteFileName"));
		remoteFileName = remoteFileName.replace("YYMMDD", settleDate.substring(2));
		String localFileUrl=String.valueOf(hashMap.get("unionLocalFileUrl"));
		String localFileName=String.valueOf( hashMap.get("unionLocalFileName"));
		localFileName = localFileName.replace("YYMMDD", settleDate.substring(2));

		//3.插入对账信息记录表BTH_PAGY_CHK_INF（对账文件获取状态：未执行）
		BthPagyChkInf bthPagyChkInf = new BthPagyChkInf();
		bthPagyChkInf.setPagyNo(qrcPagyNo);
		bthPagyChkInf.setChkDataDt(form.parse(settleDate));
		bthPagyChkInf.setPagySysNo(qrcPagySysNo);
		bthPagyChkInf.setReqChkTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		bthPagyChkInf.setGetChkSt("00");//对账文件获取状态00=未执行01=下载中02=下载完成03=不存在对账文件04=需要重试类错误99=系统类错误,需人工干预
		bthPagyChkInf.setChkFilePath(localFileUrl+localFileName);
		bthPagyChkInf.setChkFileImpSt("00");//对账文件导入状态00=未导入01=导入中02=导入成功99=导入失败
		bthPagyChkInf.setChkAcctSt("00");//00=未对账01=对账中02=对账完成99=对账异常
		bthPagyChkInf.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		bthPagyChkInf.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		BthPagyChkInfDao.insertSelective(bthPagyChkInf);

		/**
		 *  4.	根据ftp信息下载对账文件，下载失败抛错中断（重新发起），
		 *     下载成功则读取对账文件信息解析到银联对账文件明细表BTH_UNION_FILE_DET（如果解析失败抛错中断，等待重新发起），
		 *     根据汇总信息加载到导入对账汇总信息表BTH_PAGY_FILE_SUM，入库。
		 */

		//	4.1 从ftp下载对账文件
		File filePath = new File(localFileUrl);
		// 如果文件夹不存在则创建
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("文件存放本地目录不存在");
			filePath.mkdirs();
		} else {
			log.info("文件存放本地目录存在");
		}
		// 远程文件目录
		String sftpFilePath = remoteUrl + remoteFileName;
		log.info("远程文件目录路径为:" + sftpFilePath);
		// 本地存放路径
		String localFilePath = localFileUrl + localFileName;
//		String localFilePath = "E:\\shrm\\批量优化\\" + localFileName;
		log.info("本地存放路径为:" + localFilePath);

		File downFile = new File(localFilePath);
		if(downFile.exists()){
			log.info("文件已存在");
			downFile.delete();
		}else{
			log.info("文件不存在,将下载");
		}



		SftpUtil sftpUtil = new SftpUtil(ip, userName, userPwd, Integer.parseInt(port));


//		等待文件是否存在
		boolean exitFlag = checkBalFileExist(sftpUtil, sftpFilePath);

		if (exitFlag){
			ChannelSftp sftp = sftpUtil.connectSFTP();
			log.info("远程目录文件存在");
			log.info("---------------------SFTP获取本行对账文件名------------------");
			sftpUtil.download(remoteUrl, remoteFileName, localFileUrl, localFileName, sftp);
			log.info("---------------------SFTP断开连接------------------");
			sftpUtil.disconnected(sftp);
		}else {
			log.info("远程目录文件不存在");
			throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程目录文件不存在");

		}

		// 查询出银联全渠道商户号 [PAGY_BASE_INFO   PAGY_SYS_NO  CHAR(3)]
		Set<String> set = new HashSet<>();
		set.add("608");
		List<String> mchtNosTotalUn = pagyMchtInfoDao.selectAllMchtNo(set);
		// 查询出扫码支付的商户以过滤文件中其他商户
		Set<String> setQrc = new HashSet<>();
		setQrc.add("607");
		List<String> mchtQrc = pagyMchtInfoDao.selectAllMchtNo(setQrc);

		//对账汇总金额
		List<BthPagyFileSum> bthPagyFileSumList = new ArrayList<BthPagyFileSum>();
		/** 线上交易临时数据 **/
		int txnTotalCnt =0;
		BigDecimal txnTotalAmt = new BigDecimal(0);
		int refTotalCnt = 0;
		BigDecimal refTotalAmt = new BigDecimal(0);
		BigDecimal feeTotalAmt = new BigDecimal(0);
		BigDecimal recptAmt ;

		/** 线下交易临时数据 **/
		int i = 0;// 记录行数
		int count = 0;
		int qrcTxnTotalCnt =0;
		BigDecimal qrcTxnTotalAmt = new BigDecimal(0);
		int qrcRefTotalCnt = 0;
		BigDecimal qrcRefTotalAmt = new BigDecimal(0);
		BigDecimal qrcFeeTotalAmt = new BigDecimal(0);
		BigDecimal qrcRecptAmt = new BigDecimal(0);
		//	4.2解析文件（按照字符串长度截取），将解析结果加载到表
		log.info("------------ 解析业务明细文件(开始)------------------");
		//处理结果
		List<Future> futureList = new ArrayList<>();
		initPoolUnion();
		try {
			//读取业务明细对账文件
			int fileRowCount = (int)FileUtil.getFileRowCount(localFilePath, BthUnionFileDetVO.class);
			List<BthUnionFileDetVO> dataList =
					FileUtil.readFileToList(localFilePath, BthUnionFileDetVO.class, 0, fileRowCount);
			List<UnionBillOuter> unionBillOuterList = new ArrayList<>();
			for (BthUnionFileDetVO bthUnionFileDetVO : dataList) {
				//线下
				if (mchtQrc.contains(bthUnionFileDetVO.getRecCardCode())){
					log.info("线下_流水号TXN_SSN["+bthUnionFileDetVO.getProxyInsCode()+bthUnionFileDetVO.getSendInsCode()+bthUnionFileDetVO.getTraceNum()+bthUnionFileDetVO.getTransDate()+"]，消息类型码MSG_TYPE["+bthUnionFileDetVO.getMsgType()+"]，交易类型码TRANS_CODE["+bthUnionFileDetVO.getTransCode()+"]");
					// 从银联COMN文件提取银联扫码支付流水
					if("03".equals(bthUnionFileDetVO.getSevInputWay().substring(0, 2))||"04".equals(bthUnionFileDetVO.getSevInputWay().substring(0, 2))||"93".equals(bthUnionFileDetVO.getSevInputWay().substring(0, 2))||"94".equals(bthUnionFileDetVO.getSevInputWay().substring(0, 2))) {
						// 过滤受卡方标志码不在扫码支付通道表中的银联对账单流水
						if (!mchtQrc.contains(bthUnionFileDetVO.getRecCardCode())) {
							i++;
							continue;
						}
						// 受理方应收手续费
						String hearGetFee = bthUnionFileDetVO.getHearGetFee();
						// 受理方应付手续费
						String hearPayFee = bthUnionFileDetVO.getHearPayFee();
						// 转接服务费
						String routeSevFee = bthUnionFileDetVO.getRouteSevFee();
						// 去掉第一位的字母
						bthUnionFileDetVO.setRouteSevFee(bthUnionFileDetVO.getRouteSevFee().substring(1).replaceAll("^[0]+", "").equals("")?"0":bthUnionFileDetVO.getRouteSevFee().substring(1).replaceAll("^[0]+", ""));
						UnionBillOuter unionBillOuter = new UnionBillOuter();

						ReflectionUtil.copyProperties(bthUnionFileDetVO, unionBillOuter);
						if("0200".equals(bthUnionFileDetVO.getMsgType()) && bthUnionFileDetVO.getTransCode().startsWith("00")){
							//0200 00X000-支付，银联交易根据报文类型码+交易类型码判断交易类型
							bthUnionFileDetVO.setTransCode(RecoTxnTypeDict.PAY.getCode()); //交易类型码	TRANS_CODE   10=交易 11=撤销/冲正 12=退款
							unionBillOuter.setRecoState(RecoStatusDict.READY.getCode());
						}else if("0220".equals(bthUnionFileDetVO.getMsgType()) && bthUnionFileDetVO.getTransCode().startsWith("20")){
							//0220 20X000-退款
							bthUnionFileDetVO.setTransCode(RecoTxnTypeDict.REFUND.getCode()); //交易类型码	TRANS_CODE
							qrcRefTotalCnt++;
							qrcRefTotalAmt = qrcRefTotalAmt.add(new BigDecimal(bthUnionFileDetVO.getTransAmt()));
							unionBillOuter.setRecoState(RecoStatusDict.READY.getCode());
						}else if("0420".equals(bthUnionFileDetVO.getMsgType())){
							//0420-冲正
							bthUnionFileDetVO.setTransCode(RecoTxnTypeDict.REVOKE.getCode());
							unionBillOuter.setRecoState(RecoStatusDict.SKIP.getCode());
						}else if("0200".equals(bthUnionFileDetVO.getMsgType()) && bthUnionFileDetVO.getTransCode().startsWith("20")){
							//0200 20X000-撤销
							bthUnionFileDetVO.setTransCode(RecoTxnTypeDict.REVOKE.getCode());
							unionBillOuter.setRecoState(RecoStatusDict.READY.getCode());
						}else{
							//其它交易类型未知，按之前逻辑跳过后面的对账
							log.error("线下_流水号TXN_SSN["+bthUnionFileDetVO.getOrderId()+"]交易类型未知跳过对账");
							unionBillOuter.setRecoState(RecoStatusDict.SKIP.getCode());
						}
						String scdPlusFee = bthUnionFileDetVO.getScdPlusFee().trim().replaceAll("^[0]+", "").equals("")?"0":bthUnionFileDetVO.getScdPlusFee().trim().replaceAll("^[0]+", "");
						unionBillOuter.setScdPlusFee(new BigDecimal(scdPlusFee)); //分期付款附加手续费	SCD_PLUS_FEE
						unionBillOuter.setTxnSsn(unionBillOuter.getProxyInsCode()+unionBillOuter.getSendInsCode()+unionBillOuter.getTraceNum()+unionBillOuter.getTransDate());
						unionBillOuter.setRecoDate(recoDate);
						unionBillOuter.setPagyNo(qrcPagySysNo); //通道编号	PAGY_NO
//						unionBillOuter.setRecoState(RecoStatusDict.READY.getCode()); //对账状态	CHK_ACCT_ST
						unionBillOuter.setChkRst(""); //对账结果	CHK_RST
						unionBillOuter.setUpdDate(recoDate); //最后更新时间	LST_UPD_TM
						// 计算手续费
						BigDecimal feeAmt = getUnionFee(hearGetFee, hearPayFee, routeSevFee);
						unionBillOuter.setCustomerFee(String.valueOf(feeAmt));
						unionBillOuter.setTxnState("00");
						unionBillOuter.setTxnType(bthUnionFileDetVO.getTransCode());
						unionBillOuterList.add(unionBillOuter);

						i++;
						qrcTxnTotalCnt++;
						qrcTxnTotalAmt = qrcTxnTotalAmt.add(new BigDecimal(bthUnionFileDetVO.getTransAmt()));
						qrcFeeTotalAmt = qrcFeeTotalAmt.add(feeAmt);
						qrcRecptAmt = qrcTxnTotalAmt.subtract(qrcRefTotalAmt);

					}else{
						i++;
						continue;
					}


				}else if (mchtNosTotalUn.contains(bthUnionFileDetVO.getRecCardCode())){//线上
					log.info("线上_流水号TXN_SSN["+bthUnionFileDetVO.getOrderId()+"]，消息类型码MSG_TYPE["+bthUnionFileDetVO.getMsgType()+"]，交易类型码TRANS_CODE["+bthUnionFileDetVO.getTransCode()+"]");
					// 受理方应收手续费
					String hearGetFee = bthUnionFileDetVO.getHearGetFee();
					// 受理方应付手续费
					String hearPayFee = bthUnionFileDetVO.getHearPayFee();
					// 转接服务费
					String routeSevFee = bthUnionFileDetVO.getRouteSevFee();
					// 去掉第一位的字母
					bthUnionFileDetVO.setRouteSevFee(bthUnionFileDetVO.getRouteSevFee().substring(1).replaceAll("^[0]+", "").equals("")?"0":bthUnionFileDetVO.getRouteSevFee().substring(1).replaceAll("^[0]+", ""));
					UnionBillOuter unionBillOuter = new UnionBillOuter();
					ReflectionUtil.copyProperties(bthUnionFileDetVO , unionBillOuter);
					if("0200".equals(bthUnionFileDetVO.getMsgType()) && bthUnionFileDetVO.getTransCode().startsWith("00")){
						//0200 00X000-支付，银联交易根据报文类型码+交易类型码判断交易类型
						bthUnionFileDetVO.setTransCode(RecoTxnTypeDict.PAY.getCode()); //交易类型码	TRANS_CODE   10=交易 11=撤销/冲正 12=退款
						unionBillOuter.setRecoState(RecoStatusDict.READY.getCode());
					}else if("0220".equals(bthUnionFileDetVO.getMsgType()) && bthUnionFileDetVO.getTransCode().startsWith("20")){
						//0220 20X000-退款
						bthUnionFileDetVO.setTransCode(RecoTxnTypeDict.REFUND.getCode()); //交易类型码	TRANS_CODE
						qrcRefTotalCnt++;
						qrcRefTotalAmt = qrcRefTotalAmt.add(new BigDecimal(bthUnionFileDetVO.getTransAmt()));
						unionBillOuter.setRecoState(RecoStatusDict.READY.getCode());
					}else if("0420".equals(bthUnionFileDetVO.getMsgType())){
						//0420-冲正
						bthUnionFileDetVO.setTransCode(RecoTxnTypeDict.REVOKE.getCode());
						unionBillOuter.setRecoState(RecoStatusDict.SKIP.getCode());
					}else if("0200".equals(bthUnionFileDetVO.getMsgType()) && bthUnionFileDetVO.getTransCode().startsWith("20")){
						//0200 20X000-撤销
						bthUnionFileDetVO.setTransCode(RecoTxnTypeDict.REVOKE.getCode());
						unionBillOuter.setRecoState(RecoStatusDict.READY.getCode());
					}else{
						//其它交易类型未知，按之前逻辑跳过后面的对账
						log.error("线上_流水号TXN_SSN["+bthUnionFileDetVO.getOrderId()+"]交易类型未知跳过对账");
						unionBillOuter.setRecoState(RecoStatusDict.SKIP.getCode());
					}
					txnTotalCnt ++;
					unionBillOuter.setRecoDate(recoDate);
					unionBillOuter.setTxnSsn(bthUnionFileDetVO.getOrderId());
					unionBillOuter.setPagyNo(onlinPagySysNo);
					unionBillOuter.setTxnType(bthUnionFileDetVO.getTransCode());
					unionBillOuter.setTxnSsn(bthUnionFileDetVO.getOrderId());
					//对账状态	CHK_ACCT_ST
//					unionBillOuter.setRecoState(RecoStatusDict.READY.getCode());
					//对账结果	CHK_RST
					unionBillOuter.setChkRst("");
					// 银联收的手续费 (存在CustomerFee中)
					BigDecimal feeAmt = getUnionFee(hearGetFee, hearPayFee, routeSevFee);
					unionBillOuter.setCustomerFee(String.valueOf(feeAmt));

					//最后更新时间	LST_UPD_TM
					unionBillOuter.setUpdDate(recoDate);
					unionBillOuter.setTxnState("00");
					unionBillOuterList.add(unionBillOuter);

					// 计算汇总正交易金额(包含退款金额,不减去)
					txnTotalAmt = txnTotalAmt.add(unionBillOuter.getTransAmt());

					// 反交易
					if ("0220".equals(unionBillOuter.getMsgType()) && bthUnionFileDetVO.getTransCode().startsWith("20")){
						// 计算汇总反交易金额
						refTotalAmt = refTotalAmt.add(unionBillOuter.getTransAmt());
						refTotalCnt++ ;
					}

					// 手续费金额汇总
					feeTotalAmt =getTottalFee(feeTotalAmt,hearGetFee,hearPayFee,routeSevFee);
				}else{
					continue;
				}
				if(IfspDataVerifyUtil.isNotEmptyList(unionBillOuterList)){
					/*
						 * 批量插入
						 */
					if(unionBillOuterList.size() == unionBatchInsertCount){
						Future future = unionExecutor.submit(new UnionHandler(unionBillOuterList));
						futureList.add(future);
						//清理集合
						unionBillOuterList = new ArrayList<UnionBillOuter>();
					}
				}

			}
			/*
			 * 批量插入
			 */
			if(unionBillOuterList != null && unionBillOuterList.size()>0){
				Future future = unionExecutor.submit(new UnionHandler(unionBillOuterList));
				futureList.add(future);
			}
			/*
			 * 获取处理结果
			 */
			log.info("获取处理结果。。。。。。");
			for (Future future : futureList) {
				try {
					future.get(10, TimeUnit.MINUTES);
				} catch (Exception e) {
					log.error("子线程处理异常: ", e);
					//取消其他任务
					destoryPoolUnion();
					log.warn("其他子任务已取消.");
					//返回结果
					throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
				}
			}


			log.info("========获取银联对账文件成功 , 共 ["+txnTotalCnt+"] 条对账信息!");

			/** 线上汇总 **/
			// 实收金额
			recptAmt = txnTotalAmt.subtract(refTotalAmt);
			BthPagyFileSum bthPagyFileSum = new BthPagyFileSum();
			bthPagyFileSum.setPagySysNo(onlinPagySysNo);
			bthPagyFileSum.setPagyNo(onlinPagyNo);
			bthPagyFileSum.setChkDataDt(settleDate);
			bthPagyFileSum.setTxnTotalCnt(new BigDecimal(txnTotalCnt));
			bthPagyFileSum.setTxnTotalAmt(txnTotalAmt);
			bthPagyFileSum.setRefTotalCnt(new BigDecimal(refTotalCnt));
			bthPagyFileSum.setRefTotalAmt(recptAmt);
			bthPagyFileSum.setFeeTotalAmt(feeTotalAmt);
			bthPagyFileSum.setRecptAmt(recptAmt);
			bthPagyFileSum.setSumRmk("汇总信息");
			bthPagyFileSum.setCrtTm(IfspDateTime.getYYYYMMDDHHMMSS());
			bthPagyFileSumList.add(bthPagyFileSum);
			/** 线下汇总 **/
			// 实收金额
			qrcRecptAmt = qrcTxnTotalAmt.subtract(qrcRefTotalAmt);
			BthPagyFileSum qrcBthPagyFileSum = new BthPagyFileSum();
			qrcBthPagyFileSum.setPagySysNo(qrcPagySysNo);
			qrcBthPagyFileSum.setPagyNo(qrcPagyNo);
			qrcBthPagyFileSum.setChkDataDt(settleDate);
			qrcBthPagyFileSum.setTxnTotalCnt(new BigDecimal(qrcTxnTotalCnt));
			qrcBthPagyFileSum.setTxnTotalAmt(qrcTxnTotalAmt);
			qrcBthPagyFileSum.setRefTotalCnt(new BigDecimal(qrcRefTotalCnt));
			qrcBthPagyFileSum.setRefTotalAmt(qrcRecptAmt);
			qrcBthPagyFileSum.setFeeTotalAmt(qrcFeeTotalAmt);
			qrcBthPagyFileSum.setRecptAmt(qrcRecptAmt);
			qrcBthPagyFileSum.setSumRmk("汇总信息");
			qrcBthPagyFileSum.setCrtTm(IfspDateTime.getYYYYMMDDHHMMSS());
			bthPagyFileSumList.add(qrcBthPagyFileSum);

			/**
			 * 数据库操作
			 */
//			log.info("========获取银联对账文件成功 , 共 ["+unionBillOuterList.size()+"] 条对账信息!");
//			insertUnionBillAllData(unionBillOuterList, bthPagyFileSumList);
			if(IfspDataVerifyUtil.isNotEmptyList(bthPagyFileSumList)){
				log.info("通道汇总文件信息入库...");
				bthPagyFileSumDao.insertSelectiveList(bthPagyFileSumList);
				log.info("通道汇总文件信息入库成功.");
			}

			billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
			billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

		}catch (Exception e){
			log.error("解析业务明细文件失败:", e);
			throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
		}finally {
			destoryPoolUnion();
		}
		log.info("------------ 解析业务明细文件(结束)------------------");
		//	5.	更新对账信息记录表BTH_PAGY_CHK_INF信息（对账文件获取状态：下载完成）
		bthPagyChkInf.setGetChkSt("02");
		BthPagyChkInfDao.updateByPrimaryKeySelective(bthPagyChkInf);

		//	6.  根据结果补充响应对象
		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}

	@Override
	public BillDownloadResponse coreBillFileDownload(BillDownloadRequest request) throws Exception {
		/*****获取请求参数值********/
		//获取的订单数据的交易日期
		Date txnDate = getRecoDate(request.getSettleDate());
		//计算对账日期 = 交易日期 + 1
		Date recoDate = new DateTime(txnDate).plusDays(1).toDate();
		String settleDate = request.getSettleDate();// 清算日期
		Date strToDate = IfspDateTime.strToDate(settleDate, IfspDateTime.YYYY_MM_DD);
		/*********初始化响应对象***********/
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
		log.info("---------------------删除表数据------------------");
//		1.	根据对账日期删除借记卡对账文件明细表(CORE_BILL_INFO)
		coreBillInfoDao.clear(recoDate);
		BthPagyChkInfDao.deleteBypagyNoAndDate("604bbbbbbbbbbbb",strToDate);
//		2.	获取借记卡对账文件存储信息
		//调本行下载本行借记卡对账单
		SoaParams params = new SoaParams();
		String timeStamp = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
		params.put("chkEntryDt", settleDate); //对账日期
		params.put("chkEntryClsCd", "EBAP002"); //对账分类编码
		params.put("docNm", "052" + timeStamp + "RPS001_check.txt"); //文件名
		SoaResults result;

		result = keepAccSoaService.debitBill(params);
		Map<Object, Object> datas = result.getDatas();
		if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode"))) {
			throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		}

		if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
				RespConstans.RESP_SUCCESS.getCode())) {
		} else {
			log.info("====================="+datas+"=========================");
			log.info("=====================成功================================");
		}
		String localFileUrl=tallyLocalFileUrl;
		String localFileName=(String) datas.get("docNm");//zip

		//todo 测试使用
//		localFileName = "05220190602223414046RPS001_check.txt";
//		localFileUrl = "E:\\shrm\\批量优化\\";

		log.info("本行记账反馈文件名[{}]", localFileName);
		// 记录文件到表里
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
		BthPagyChkInf bthPagyChkInf = new BthPagyChkInf();
		bthPagyChkInf.setPagyNo("604bbbbbbbbbbbb");
		bthPagyChkInf.setChkDataDt(strToDate);
		bthPagyChkInf.setPagySysNo(request.getPagySysNo());
		bthPagyChkInf.setReqChkTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		bthPagyChkInf.setGetChkSt("00");
		bthPagyChkInf.setChkFilePath(localFileUrl+localFileName);
		bthPagyChkInf.setChkFileImpSt("00");
		bthPagyChkInf.setChkAcctSt("00");
		bthPagyChkInf.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		bthPagyChkInf.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
		BthPagyChkInfDao.insertSelective(bthPagyChkInf);


//		4.	根据存储路径信息读取文件并解析入库
		//============================================================
		File filePath = new File(localFileUrl);
		// 如果文件夹不存在则创建
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("文件存放本地目录不存在");
			filePath.mkdirs();
		} else {
			log.info("文件存放本地目录存在");
		}

		String localFilePath = localFileUrl + localFileName; // 本地存放路径
		String localFlagFilePath = localFilePath+".ok"; // 本地存放路径
		//本地存放路径
		log.info("本地存放路径为:" + localFilePath);
		int c = 3;
		do {
			if (!new File(localFlagFilePath).exists()) {
				log.info("------------ 本行记账ok文件不存在------------------");
				Thread.sleep(300000);

				c--;
				log.info("=====================标记文件不存在检查剩余次数[" + c + "]================================");
				continue;
			} else {
				log.info("=====================本行记账ok文件检查到存在================================");
				break;
			}
		} while (c > 0);

		// 本地存在直接读取，不存在中断
		if (!new File(localFlagFilePath).exists()) {
			log.info("------------ 尝试检查3次本行记账ok文件仍不存在不存在------------------");
			billDownloadResponse.setDownFlag("0");
			billDownloadResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			billDownloadResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
			return billDownloadResponse;
		}
		// 本地存在直接读取，不存在中断
		if (!new File(localFilePath).exists()) {
			log.info("------------ 本行记账文件不存在------------------");
			billDownloadResponse.setDownFlag("0");
			billDownloadResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			billDownloadResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
			return billDownloadResponse;

		}

		//4.3解析文件（以“|#|”分隔），将解析结果加载到DebitTranInfo
		log.info("------------ 解析业务明细文件(开始)------------------");
		int count = 0;
		initPoolCore();
		try {
			//获取对账单成功
			//对账明细
			List<CoreBillInfo> coreBillInfoList = new ArrayList<CoreBillInfo>();
//			ByteArrayInputStream in = null;
			//处理结果
			List<Future> futureList = new ArrayList<>();
			try (FileInputStream fis = new FileInputStream(localFilePath);
				 InputStreamReader isr = new InputStreamReader(fis,"GBK");
				 BufferedReader br = new BufferedReader(isr)){
				//获取对账单文件流

				/**
				 * 按“|#|”截取,解析入库对账文件
				 */
				int i = 0;

				log.info("受理通道业务(下载对账单) 逻辑处理 ");
				while (br.ready()) {
					String tmpLine = br.readLine();
					if(IfspStringUtil.isBlank(tmpLine)){
						i++;
						continue;
					}
					CoreBillInfo coreBillInfo = new CoreBillInfo();
					coreBillInfo.setChannelNo(tmpLine.split("\\|#\\|", -1)[0]); // 渠道号 CHANNEL_NO
					coreBillInfo.setChannelDate(tmpLine.split("\\|#\\|", -1)[1]); // 渠道日期
					coreBillInfo.setChannelSeq(tmpLine.split("\\|#\\|", -1)[2]); // 渠道流水号
					coreBillInfo.setTxnDate(tmpLine.split("\\|#\\|", -1)[3]); // 交易日期 TXN_DATE
					coreBillInfo.setTellerSeq(tmpLine.split("\\|#\\|", -1)[4]); // 柜员流水号 TELLER_SEQ
					coreBillInfo.setTxnState(tmpLine.split("\\|#\\|", -1)[5]); // 交易状态 TXN_STATUS
					coreBillInfo.setPlatformDate(tmpLine.split("\\|#\\|", -1)[6]); // 平台日期
					coreBillInfo.setPlatformSeq(tmpLine.split("\\|#\\|", -1)[7]); // 平台流水号
					coreBillInfo.setTxnCode(tmpLine.split("\\|#\\|", -1)[8]); // 交易号 TXN_CODE
					coreBillInfo.setTxnOrg(tmpLine.split("\\|#\\|", -1)[9]); // 交易机构号 TXN_ORG
					coreBillInfo.setTxnTeller(tmpLine.split("\\|#\\|", -1)[10]); // 交易柜员号 TXN_TELLER
					coreBillInfo.setTxnCur(tmpLine.split("\\|#\\|", -1)[11]); // 币种 TXN_CUR
					coreBillInfo.setPayAccount(tmpLine.split("\\|#\\|", -1)[12]); // 账号 PAY_ACCOUNT
					coreBillInfo.setReceiveAccount(tmpLine.split("\\|#\\|", -1)[13]); // 对方账号
					coreBillInfo.setTxnAmount(new BigDecimal(tmpLine.split("\\|#\\|", -1)[14]).movePointRight(2)); // 交易金额 TXN_AMOUNT
					coreBillInfo.setReserved1("98"); // 备用 RESERVED1  记账类
					coreBillInfo.setRecoState(RecoStatusDict.READY.getCode()); // 对账状态

					coreBillInfo.setRecoDate(recoDate);
					coreBillInfo.setTxnSsn(coreBillInfo.getChannelSeq());
					coreBillInfo.setChkRst(""); // 对账结果 CHK_RST
					coreBillInfo.setUpdTm(recoDate); // 最后更新时间 LST_UPD_TM
					coreBillInfoList.add(coreBillInfo);

					/*
						 * 批量插入
						 */
//					if(coreBillInfoList.size() == batchInsertCount){
//						coreBillInfoDao.insertBatch(coreBillInfoList);
//						coreBillInfoList.clear();
//					}
					/*
						 * 批量插入 batchInsertCount
						 */
					if(coreBillInfoList.size() == coreBatchInsertCount){
						Future future = executorCore.submit(new CoreHandler(coreBillInfoList));
						futureList.add(future);
						//清理集合
//						coreBillInfoList.clear();
						coreBillInfoList = new ArrayList<CoreBillInfo>();
					}

//					log.info("受理通道业务(下载对账单) 逻辑处理 :[ 读取信息 :[" + tmpLine + "] , 列数:[" + tmpLine.split("\\|#\\|",-1).length + "]]");

					i++;
					count++;
				}
				if(coreBillInfoList!=null && coreBillInfoList.size()>0){
					Future future = executorCore.submit(new CoreHandler(coreBillInfoList));
					futureList.add(future);
				}
				/*
				 * 获取处理结果
				 */
				log.info("获取处理结果。。。。。。");
				for (Future future : futureList) {
					try {
						future.get(10, TimeUnit.MINUTES);
					} catch (Exception e) {
						log.error("子线程处理异常: ", e);
						//取消其他任务
						destoryPoolCore();
						log.warn("其他子任务已取消.");
						//返回结果
						throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
					}
				}
			} catch (IOException e) {
				log.error("下载对账单失败:", e);
				throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"下载对账单失败");
			}

			/**
			 * 数据库操作
			 */
			log.info("========获取本行记账文件成功 , 共 ["+count+"] 条对账信息!");
//			log.debug("入账明细信息入库...");
//			coreBillInfoDao.insertBatch(coreBillInfoList);
//			log.debug("入账明细信息入库完成.");

		}catch (Exception e){
			log.error("解析业务明细文件失败:", e);
			throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
		}
		log.info("------------ 解析业务明细文件(结束)------------------");

//		6.  根据结果补充响应对象
		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}

	/**
     * 本行借记卡核心对账单下载及加载
     * @param request
     * @return
     */
	@Override
	public BillDownloadResponse debitBillDownload(BillDownloadRequest request) throws Exception {
		/*****获取请求参数值********/
		String settleDate = request.getSettleDate();// 清算日期
        Date strToDate = IfspDateTime.strToDate(settleDate, IfspDateTime.YYYY_MM_DD);
        /*********初始化响应对象***********/
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
		log.info("---------------------删除表数据------------------");
//		1.	根据清算日期和通道编号删除借记卡对账文件明细表(DEBIT_TRAN_INFO)
		debitTranInfoDao.deleteByDate(settleDate);
		BthPagyChkInfDao.deleteBypagyNoAndDate("604aaaaaaaaaaaa",strToDate);
//		2.	获取借记卡对账文件存储信息
		//调本行下载本行借记卡对账单
		SoaParams params = new SoaParams();
		String timeStamp = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
		params.put("chkEntryDt", settleDate); //对账日期
        params.put("chkEntryClsCd", "EBAP001"); //对账分类编码
        params.put("docNm", "052" + timeStamp + "RPS001_check.txt"); //文件名
		SoaResults result;
		result = keepAccSoaService.debitBill(params);
		Map<Object, Object> datas = result.getDatas();
		if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode"))) {
			throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		}
		
		if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
				RespConstans.RESP_SUCCESS.getCode())) {
		} else {
			log.info("====================="+datas+"=========================");
			log.info("=====================成功================================");
		}
		String localFileUrl=debitLocalFileUrl;
		String localFileName=(String) datas.get("docNm");//zip
        log.info("本行对账反馈文件名[{}]",localFileName);
        // 记录文件到表里
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
        BthPagyChkInf bthPagyChkInf = new BthPagyChkInf();
        bthPagyChkInf.setPagyNo("604aaaaaaaaaaaa");
        bthPagyChkInf.setChkDataDt(strToDate);
        bthPagyChkInf.setPagySysNo(request.getPagySysNo());
        bthPagyChkInf.setReqChkTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        bthPagyChkInf.setGetChkSt("00");
        bthPagyChkInf.setChkFilePath(localFileUrl+localFileName);
        bthPagyChkInf.setChkFileImpSt("00");
        bthPagyChkInf.setChkAcctSt("00");
        bthPagyChkInf.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        bthPagyChkInf.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        BthPagyChkInfDao.insertSelective(bthPagyChkInf);


//		4.	根据存储路径信息读取文件并解析入库
		//============================================================
		File filePath = new File(localFileUrl);
		// 如果文件夹不存在则创建
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("文件存放本地目录不存在");
			filePath.mkdirs();
		} else {
			log.info("文件存放本地目录存在");
		}

		String localFilePath = localFileUrl + localFileName; // 本地存放路径 billTest
        String localFlagFilePath = localFilePath+".ok"; // 本地存放路径
        log.info("本地存放路径为:" + localFilePath);
		log.info("本地标记存放路径为:" + localFlagFilePath);
		//检查标记文件
		int c = 3;
		do {
			if (!new File(localFlagFilePath).exists()) {
				log.info("------------ 本行对账ok文件不存在------------------");
				Thread.sleep(300000);
//				Thread.sleep(1000);
				
				c--;
				log.info("=====================标记文件不存在检查剩余次数[" + c + "]================================");
				continue;
			} else {
				log.info("=====================本行对账ok文件检查到存在================================");
				break;
			}
		} while (c > 0);
		
		// 本地存在直接读取，不存在中断
		if (!new File(localFlagFilePath).exists()) {
			log.info("------------ 尝试检查3次本行对账ok文件仍不存在不存在------------------");
			billDownloadResponse.setDownFlag("0");
			billDownloadResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			billDownloadResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
			return billDownloadResponse;
		}
		//本地存在直接读取，不存在中断
		if(!new File(localFilePath).exists()){
			log.info("------------ 本行借记卡对账文件不存在------------------");
			billDownloadResponse.setDownFlag("0");
			billDownloadResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			billDownloadResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
			return billDownloadResponse;

		}
		
		//4.3解析文件（以“|#|”分隔），将解析结果加载到DebitTranInfo
		log.info("------------ 解析业务明细文件(开始)------------------");
        try {
            //获取对账单成功
            //对账明细
            List<DebitTranInfo> debitTranInfoList = new ArrayList<DebitTranInfo>();
//            ByteArrayInputStream in = null;

			//获取对账单文件流
			try (FileInputStream fis = new FileInputStream(localFilePath);
				 InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
					BufferedReader br = new BufferedReader(isr);){

                /**
                 * 按“|#|”截取,解析入库对账文件
                 */
                int i = 0;
                log.info("受理通道业务(下载对账单) 逻辑处理 ");
				while (br.ready()) {
					String tmpLine = br.readLine();
                    if(IfspStringUtil.isBlank(tmpLine)){
                        i++;
                        continue;
                    }
					DebitTranInfo debitTranInfo = new DebitTranInfo();
					debitTranInfo.setChannelNo(tmpLine.split("\\|#\\|",-1)[0]); // 渠道号 CHANNEL_NO
					debitTranInfo.setChannelDate(tmpLine.split("\\|#\\|",-1)[1]); // 渠道日期
					debitTranInfo.setChannelSeq(tmpLine.split("\\|#\\|",-1)[2]); // 渠道流水号
					debitTranInfo.setTxnDate(tmpLine.split("\\|#\\|",-1)[3]); // 交易日期 TXN_DATE
					debitTranInfo.setTellerSeq(tmpLine.split("\\|#\\|",-1)[4]); // 柜员流水号 TELLER_SEQ
					debitTranInfo.setTxnStatus(tmpLine.split("\\|#\\|",-1)[5]); // 交易状态 TXN_STATUS
					debitTranInfo.setPlatformDate(tmpLine.split("\\|#\\|",-1)[6]); // 平台日期
					debitTranInfo.setPlatformSeq(tmpLine.split("\\|#\\|",-1)[7]); // 平台流水号
					debitTranInfo.setTxnCode(tmpLine.split("\\|#\\|",-1)[8]); // 交易号 TXN_CODE
					debitTranInfo.setTxnOrg(tmpLine.split("\\|#\\|",-1)[9]); // 交易机构号 TXN_ORG
					debitTranInfo.setTxnTeller(tmpLine.split("\\|#\\|",-1)[10]); // 交易柜员号 TXN_TELLER
					debitTranInfo.setTxnCur(tmpLine.split("\\|#\\|",-1)[11]); // 币种 TXN_CUR
					debitTranInfo.setPayAccount(tmpLine.split("\\|#\\|",-1)[12]); // 账号 PAY_ACCOUNT
					debitTranInfo.setReceiveAccount(tmpLine.split("\\|#\\|",-1)[13]); // 对方账号
					debitTranInfo.setTxnAmount(new BigDecimal(tmpLine.split("\\|#\\|",-1)[14]).movePointRight(2)); // 交易金额 TXN_AMOUNT
//					debitTranInfo.setReserved1(tmpLine.split("\\|#\\|",-1)[15]); // 备用 RESERVED1
					debitTranInfo.setPagySysNo(request.getPagySysNo()); // 通道系统编号 PAGY_SYS_NO
					debitTranInfo.setPagyNo(request.getPagyNo()); // 通道编号 PAGY_NO
					debitTranInfo.setChkDataDt(settleDate); // 对账日期 CHK_DATA_DT
					debitTranInfo.setChkAcctSt("00"); // 对账状态 CHK_ACCT_ST
					debitTranInfo.setChkRst(""); // 对账结果 CHK_RST
					debitTranInfo.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS()); // 最后更新时间 LST_UPD_TM
					debitTranInfoList.add(debitTranInfo);

					log.info("受理通道业务(下载对账单) 逻辑处理 :[ 读取信息 :[" + tmpLine + "] , 列数:[" + tmpLine.split("\\|#\\|",-1).length + "]]");

					i++;
				}
            } catch (IOException e) {
                log.error("下载对账单失败:", e);
                throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"下载对账单失败");
            }

            /**
             * 数据库操作
             */
            log.info("========获取本行对账文件成功 , 共 ["+debitTranInfoList.size()+"] 条对账信息!");
            updateDebitBillData(settleDate, debitTranInfoList);
        
        }catch (Exception e){
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
//		5.	更新对账信息记录表BTH_PAGY_CHK_INF信息（对账文件获取状态：下载完成）
//		bthPagyChkInf.setGetChkSt("02");
//		BthPagyChkInfDao.updateByPrimaryKeySelective(bthPagyChkInf);
		
//		6.  根据结果补充响应对象
		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}
	
	


	/**
     * 本行贷记卡核心对账单下载及加载
     * @param request
     * @return
     */
	@Override
	public BillDownloadResponse creditBillDownload(BillDownloadRequest request) throws Exception {
		/*****获取请求参数值********/
		String settleDate = request.getSettleDate();// 清算日期
		/*********初始化响应对象***********/
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
		log.info("---------------------删除表数据------------------");
//		1.	根据清算日期和通道编号删除微信对账文件明细表(creditTranInfo)
		creditTranInfoDao.deleteByDate(settleDate);
		//TODO
//		2.	组参数调用下载贷记卡对账文件接口，获取对账文件路径
		String localFilePath = "C:/Users/Administrator/Desktop/billTest/CREDIT-CHK-FILE-052-20171110110700329.TXT"; // 本地存放路径
		//本地存在直接读取，不存在就去ftp下载
		if(!new File(localFilePath).exists()){
			log.info("------------ 本行借记卡对账文件不存在------------------");
			billDownloadResponse.setDownFlag("0");
			billDownloadResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			billDownloadResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
			return billDownloadResponse;

		}
		
//		4.3解析文件（以|分隔），将解析结果加载到CreditTranInfo
		log.info("------------ 解析业务明细文件(开始)------------------");
        try {
            //对账明细
            List<CreditTranInfo> creditTranInfoList = new ArrayList<CreditTranInfo>();
//            ByteArrayInputStream in = null;

            try(FileInputStream fis = new FileInputStream(localFilePath);
				InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
				BufferedReader br = new BufferedReader(isr)) {
                //获取对账单文件流

                /**
                 * 按“|”截取,解析入库对账文件
                 */
                int i = 0;
                log.info("受理通道业务(下载对账单) 逻辑处理 ");
				while (br.ready()) {
					String tmpLine = br.readLine();
					CreditTranInfo creditTranInfo = new CreditTranInfo();
					
					creditTranInfo.setChannelNo(tmpLine.split("\\|", -1)[0]); // 渠道号 CHANNEL_NO
					creditTranInfo.setChannelDate(tmpLine.split("\\|", -1)[1]); // 渠道日期
					creditTranInfo.setChannelSeq(tmpLine.split("\\|", -1)[2]); // 渠道流水号
					creditTranInfo.setTxndate(tmpLine.split("\\|", -1)[3]); // 交易日期 TXNDATE
					creditTranInfo.setPlatformSeq(tmpLine.split("\\|", -1)[4]); // 交易流水号
					creditTranInfo.setTxnStatus(tmpLine.split("\\|", -1)[5]); // 交易状态 TXN_STATUS
					creditTranInfo.setTxnCode(tmpLine.split("\\|", -1)[6]); // 交易号 TXN_CODE
					creditTranInfo.setTxnOrg(tmpLine.split("\\|", -1)[7]); // 交易机构号 TXN_ORG
					creditTranInfo.setTxnTeller(tmpLine.split("\\|", -1)[8]); // 交易柜员号 TXN_TELLER
					creditTranInfo.setTxnCur(tmpLine.split("\\|", -1)[9]); // 币种 TXN_CUR
					creditTranInfo.setPayAccount(tmpLine.split("\\|", -1)[10]); // 付款账号 PAY_ACCOUNT
					creditTranInfo.setReceiveAccount(tmpLine.split("\\|", -1)[11]); // 收款账号
					String txnAmount = "".equals(tmpLine.split("\\|", -1)[12].trim())?"0":tmpLine.split("\\|", -1)[12].trim();
					creditTranInfo.setTxnAmount(new BigDecimal(txnAmount).movePointRight(2)); // 交易金额 TXN_AMOUNT
					creditTranInfo.setReserved1(tmpLine.split("\\|", -1)[13]); // 备用 RESERVED1
					creditTranInfo.setPagySysNo(request.getPagySysNo()); // 通道系统编号 PAGY_SYS_NO
					creditTranInfo.setPagyNo(request.getPagyNo()); // 通道编号 PAGY_NO
					creditTranInfo.setChkDataDt(settleDate); // 对账日期 CHK_DATA_DT
					creditTranInfo.setChkAcctSt("00"); // 对账状态 CHK_ACCT_ST
					creditTranInfo.setChkRst(""); // 对账结果 CHK_RST
					creditTranInfo.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS()); // 最后更新时间 LST_UPD_TM
					creditTranInfoList.add(creditTranInfo);

					log.info("受理通道业务(下载对账单) 逻辑处理 :[ 读取汇总信息 :[" + tmpLine + "] , 列数:[" + tmpLine.split("\\|",-1).length + "]]");

					i++;
				}
            } catch (IOException e) {
                log.error("下载对账单失败:", e);
                throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"下载对账单失败");
            }

            /**
             * 数据库操作
             */
            log.info("========获取贷记卡对账文件成功 , 共 ["+creditTranInfoList.size()+"] 条对账信息!");
            updateCreditBillData(settleDate, creditTranInfoList);
        
        }catch (Exception e){
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
		
//		6.  根据结果补充响应对象
		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}
	
	/**
	 * 贷记卡信息入库
	 * @param settleDate
	 * @param creditTranInfoList
	 */
	private void updateCreditBillData(String settleDate, List<CreditTranInfo> creditTranInfoList) {
		log.debug("清空入账明细信息...");
		debitTranInfoDao.deleteByDate(settleDate);
        log.debug("清空入账明细信息完成.");
        log.debug("入账明细信息入库...");
        creditTranInfoDao.insertSelectiveList(creditTranInfoList);
        log.debug("入账明细信息入库完成.");
	}

    
    /**
     * 解析支付宝明细信息
     * @param i
     * @param list
     * @param refAmt 
     * @return
     * @throws Exception 
     */
    private BthAliFileDet initializeBillInfo(int i, List<String> list, String settleDate, 
    		String pagyNo,String pagySysNo, BigDecimal refAmt) throws Exception {
      //CSV格式文件为逗号分隔符文件，这里根据逗号切分
        int a = list.get(i).toString().split(",").length;
        log.info("每行数据的列数：" + list.get(i).toString().split(",").length);
        if(!(a<30)){
        	log.error("受理支付宝通道业务(对账单下载) 逻辑处理失败 : [ 对账文件该行数据列数超过30列 ] ");
        	throw new Exception(" 对账文件该行数据列数超过30列");
        }
        /**将一行数据初始化到BthAliFileDet*/
        BthAliFileDet bthAliFileDet = new BthAliFileDet(); 
        if("交易".equals(list.get(i).toString().split(",")[2])){
        	bthAliFileDet.setOrderTp("10");                 //订单类型	ORDER_TP10=交易11=撤销/冲正12=退款
        	bthAliFileDet.setOrderNo(list.get(i).toString().split(",")[1].trim());                //订单号	ORDER_NO
        	bthAliFileDet.setAliOrderNo(list.get(i).toString().split(",")[0].trim());                   //支付宝订单号	ALI_ORDER_NO
        }else if("退款".equals(list.get(i).toString().split(",")[2])){
        	bthAliFileDet.setOrderTp("12");                 //订单类型	ORDER_TP10=交易11=撤销/冲正12=退款
        	bthAliFileDet.setOrderNo(list.get(i).toString().split(",")[21].trim());  
        	bthAliFileDet.setSrcOrderNo(list.get(i).toString().split(",")[1].trim());                  //原订单号	SRC_ORDER_NO
        	refAmt = refAmt.add(new BigDecimal(list.get(i).toString().split(",")[11].trim()));
        }else{
        	bthAliFileDet.setOrderTp("11");                 //订单类型	ORDER_TP10=交易11=撤销/冲正12=退款
        	bthAliFileDet.setOrderNo(list.get(i).toString().split(",")[21].trim());  
        	bthAliFileDet.setSrcOrderNo(list.get(i).toString().split(",")[1].trim());                  //原订单号	SRC_ORDER_NO
        	refAmt = refAmt.add(new BigDecimal(list.get(i).toString().split(",")[11].trim()));
        }
        bthAliFileDet.setSrcAliOrderNo("");                   //原支付宝订单号	SRC_ALI_ORDER_NO
        bthAliFileDet.setBusiTp(list.get(i).toString().split(",")[2]);               //业务类型	BUSI_TP
        bthAliFileDet.setProdNm(list.get(i).toString().split(",")[3]);                      //商品名称	PROD_NM
        bthAliFileDet.setCrtTm(IfspDateTime.getYYYYMMDDHHMMSS(IfspDateTime.getYYYYMMDDHHMMSS(list.get(i).toString().split(",")[4])));                     //创建时间	CRT_TM
        bthAliFileDet.setEndTm(IfspDateTime.getYYYYMMDDHHMMSS(IfspDateTime.getYYYYMMDDHHMMSS(list.get(i).toString().split(",")[5])));                    //完成时间	END_TM
        bthAliFileDet.setStoreId(list.get(i).toString().split(",")[6]);              //门店编号	STORE_ID
        bthAliFileDet.setStoreNm(list.get(i).toString().split(",")[7]);             //门店名称	STORE_NM
        bthAliFileDet.setOperatorId(list.get(i).toString().split(",")[8]);                     //操作员	OPERATOR_ID
        bthAliFileDet.setTerminalId(list.get(i).toString().split(",")[9]);                //终端号	TERMINAL_ID
        bthAliFileDet.setBuyerLoginId(list.get(i).toString().split(",")[10]);            //支付宝交易账户	BUYER_LOGIN_ID
        bthAliFileDet.setOrderAmt(new BigDecimal(list.get(i).toString().split(",")[11]).movePointRight(2));                   //订单金额	ORDER_AMT
        bthAliFileDet.setRecepitAmt(new BigDecimal(list.get(i).toString().split(",")[12]).movePointRight(2));                //商户实收金额	RECEPIT_AMT
        bthAliFileDet.setAliRedAmt(new BigDecimal(list.get(i).toString().split(",")[13]).movePointRight(2));         //支付宝红包金额	ALI_RED_AMT
        bthAliFileDet.setPointAmt(new BigDecimal(list.get(i).toString().split(",")[14]).movePointRight(2));        //集分宝金额	POINT_AMT
        bthAliFileDet.setAliDctAmt(new BigDecimal(list.get(i).toString().split(",")[15]).movePointRight(2));        //支付宝优惠金额	ALI_DCT_AMT
        bthAliFileDet.setMchDctAmt(new BigDecimal(list.get(i).toString().split(",")[16]).movePointRight(2));       //商家优惠金额	MCH_DCT_AMT
        bthAliFileDet.setVoucherAmt(new BigDecimal(list.get(i).toString().split(",")[17]).movePointRight(2));                 //券核销金额	VOUCHER_AMT
        bthAliFileDet.setVoucherNm(list.get(i).toString().split(",")[18]);            //券名称	VOUCHER_NM
        bthAliFileDet.setMchRedAmt(new BigDecimal(list.get(i).toString().split(",")[19]).movePointRight(2));             //商家红包金额	MCH_RED_AMT
        bthAliFileDet.setPccPayAmt(new BigDecimal(list.get(i).toString().split(",")[20]).movePointRight(2));                  //卡消费金额	PCC_PAY_AMT
        bthAliFileDet.setFeeAmt(new BigDecimal(list.get(i).toString().split(",")[22]).movePointRight(2).abs());                 //服务费金额	FEE_AMT           
        bthAliFileDet.setNetAmt(new BigDecimal(list.get(i).toString().split(",")[23]).movePointRight(2));                //实收净额	NET_AMT           
        bthAliFileDet.setSubMchId(list.get(i).toString().split(",")[24]);        //商户识别号	SUB_MCH_ID        
        bthAliFileDet.setTxnTp(list.get(i).toString().split(",")[25]);               //交易方式	TXN_TP            
        bthAliFileDet.setRmk(list.get(i).toString().split(",")[26]);          //备注	RMK                   
        bthAliFileDet.setPagySysNo(pagySysNo);      //通道系统编号	PAGY_SYS_NO       
        bthAliFileDet.setPagyNo(pagyNo);            //通道编号	PAGY_NO           
        bthAliFileDet.setOrderAmtTmp(new BigDecimal(list.get(i).toString().split(",")[11]).movePointRight(2));      //计算用订单金额	ORDER_AMT_TMP     
        bthAliFileDet.setFeeAmtTmp(new BigDecimal(list.get(i).toString().split(",")[22]).movePointRight(2));     //计算用手续费	FEE_AMT_TMP       
        bthAliFileDet.setChkDataDt(settleDate);        //对账日期	CHK_DATA_DT       
        bthAliFileDet.setChkAcctSt("00");      //对账状态	CHK_ACCT_ST  00=未对账         01=已对账     
        bthAliFileDet.setChkRst("");             //对账结果	CHK_RST           
        bthAliFileDet.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS());            //最后更新时间	LST_UPD_TM        
        
        return bthAliFileDet;
    }
    
    /**
     * 明细及汇总入库
     * @param pagyNo
     * @param settleDate
     * @param bthAliFileDetList
     * @param bthPagyFileSumList
     */
    private void updateAliBillData(String pagyNo, String settleDate, List<BthAliFileDet> bthAliFileDetList,
			List<BthPagyFileSum> bthPagyFileSumList) {
    	 log.debug("清空入账明细信息...");
    	 bthAliFileDetDao.deleteBypagyNoAndDate(pagyNo,settleDate);
 		 bthPagyFileSumDao.deleteBypagyNoAndDate(pagyNo,settleDate);
         log.debug("清空入账明细信息完成.");
         log.debug("入账明细信息入库...");
         bthAliFileDetDao.insertSelectiveList(bthAliFileDetList);
         log.debug("入账明细信息入库完成.");
         if(bthPagyFileSumList.size()>0){
         	log.debug("入组汇总信息入库...");
         	bthPagyFileSumDao.insertSelectiveList(bthPagyFileSumList);
         	log.debug("入组汇总信息入库成功.");
         }
	}
    
    /**
     * 支付宝汇总文件解析
     * @param i
     * @param list
     * @param settleDate
     * @param pagyNo
     * @param pagySysNo
     * @param refAmt
     * @return
     * @throws Exception
     */
    private BthPagyFileSum initializeSumBillInfo(int i, List<String> list, String settleDate, String pagyNo,
			String pagySysNo, BigDecimal refAmt) throws Exception {
    	//CSV格式文件为逗号分隔符文件，这里根据逗号切分
        int a = list.get(i).toString().split(",").length;
        log.info("每行数据的列数：" + list.get(i).toString().split(",").length);
        if(!(a<12)){
        	log.error("受理支付宝通道业务(对账单下载) 逻辑处理失败 : [ 对账文件该行数据列数超过12列 ] ");
        	throw new Exception(" 对账文件该行数据列数超过12列");
        }
        BthPagyFileSum bthPagyFileSum = new BthPagyFileSum();
        bthPagyFileSum.setPagySysNo(pagySysNo);                                                   //通道系统编号
        bthPagyFileSum.setPagyNo(pagyNo);                                                         //通道编号
        bthPagyFileSum.setChkDataDt(settleDate);                                                  //对账日期
        bthPagyFileSum.setTxnTotalCnt(new BigDecimal(list.get(i).toString().split(",")[2].trim()));   //交易总笔数
        bthPagyFileSum.setTxnTotalAmt(new BigDecimal(list.get(i).toString().split(",")[4].trim()).movePointRight(2));   //交易总金额
        bthPagyFileSum.setRefTotalCnt(new BigDecimal(list.get(i).toString().split(",")[3].trim()));                                         //退款/撤销总笔数
        bthPagyFileSum.setRefTotalAmt(refAmt.movePointRight(2));   //退款/撤销总金额
        bthPagyFileSum.setFeeTotalAmt(new BigDecimal(list.get(i).toString().split(",")[9].trim()).movePointRight(2));   //手续费总金额
        bthPagyFileSum.setRecptAmt(new BigDecimal(list.get(i).toString().split(",")[10].trim()).movePointRight(2));      //实收金额
        bthPagyFileSum.setSumRmk("汇总信息");                                                     //汇总描述
        bthPagyFileSum.setCrtTm(IfspDateTime.getYYYYMMDDHHMMSS());                                //创建时间
        return bthPagyFileSum;
	}
    
    /**
     * 计算银联手续费总额
     * @param feeTotalAmt
     * @param hearGetFee
     * @param hearPayFee
     * @param routeSevFee
     * @return
     */
    private BigDecimal getTottalFee(BigDecimal feeTotalAmt, String hearGetFee, String hearPayFee, String routeSevFee) {
    	String routeFee = routeSevFee.substring(1).replaceAll("^[0]+", "").equals("")?"0":routeSevFee.substring(1).replaceAll("^[0]+", "");
        if(routeSevFee.startsWith("D")){
        	BigDecimal feeAmt= new BigDecimal(hearGetFee).add(new BigDecimal(hearPayFee)).subtract(new BigDecimal(routeFee));
//        	log.debug("银联手续费为"+feeAmt.toString());
        	feeTotalAmt= feeTotalAmt.add(feeAmt);
        }else if(routeSevFee.startsWith("C")){
        	BigDecimal feeAmt= new BigDecimal(hearGetFee).add(new BigDecimal(hearPayFee)).add(new BigDecimal(routeFee));
//        	log.debug("银联手续费为"+feeAmt.toString());
        	feeTotalAmt= feeTotalAmt.add(feeAmt);
        }else{
        	//非C和D时转接费为0
        	BigDecimal feeAmt= new BigDecimal(hearGetFee).add(new BigDecimal(hearPayFee));
//        	log.debug("银联手续费为"+feeAmt.toString());
        	feeTotalAmt= feeTotalAmt.add(feeAmt);
        }
        return feeTotalAmt;
	}



    /**
     * 计算银联手续费
     * @param hearGetFee
     * @param hearPayFee
     * @param routeSevFee
     * @return
     */
    private BigDecimal getUnionFee( String hearGetFee, String hearPayFee, String routeSevFee) {
        BigDecimal feeAmt ;
        String routeFee = routeSevFee.substring(1).replaceAll("^[0]+", "").equals("")?"0":routeSevFee.substring(1).replaceAll("^[0]+", "");
        if(routeSevFee.startsWith("D")){
            feeAmt= new BigDecimal(hearGetFee).add(new BigDecimal(hearPayFee)).subtract(new BigDecimal(routeFee));
//            log.debug("银联手续费为"+feeAmt);

        }else if(routeSevFee.startsWith("C")){
            feeAmt= new BigDecimal(hearGetFee).add(new BigDecimal(hearPayFee)).add(new BigDecimal(routeFee));
//            log.debug("银联手续费为"+feeAmt);

        }else{
            //非C和D时转接费为0
            feeAmt= new BigDecimal(hearGetFee).add(new BigDecimal(hearPayFee));
//            log.debug("银联手续费为"+feeAmt);
        }
        return feeAmt;

    }




    /**
     * 银联对账明细文件及汇总信息入库
     * @param pagyNo
     * @param settleDate
     * @param bthUnionFileDetList
     * @param bthPagyFileSumList
     */
	private void updateUnionBillData(String pagyNo, String settleDate, List<BthUnionFileDet> bthUnionFileDetList,
			List<BthPagyFileSum> bthPagyFileSumList) {
		log.debug("清空入账明细信息...");
        bthUnionFileDetDao.deleteBypagyNoAndDate(pagyNo,settleDate);
        bthPagyFileSumDao.deleteBypagyNoAndDate(pagyNo,settleDate);
        log.debug("清空入账明细信息完成.");
        log.debug("入账明细信息入库...");
        bthUnionFileDetDao.insertSelectiveList(bthUnionFileDetList);
        log.debug("入账明细信息入库完成.");
        if(bthPagyFileSumList.size()>0){
        	log.debug("入组汇总信息入库...");
        	bthPagyFileSumDao.insertSelectiveList(bthPagyFileSumList);
        	log.debug("入组汇总信息入库成功.");
        }
		
	}

	/**
	 * 借记卡对账文件入库
	 * @param settleDate
	 * @param debitTranInfoList
	 */
	private void updateDebitBillData(String settleDate, List<DebitTranInfo> debitTranInfoList) {
		log.debug("清空入账明细信息...");
		debitTranInfoDao.deleteByDate(settleDate);
        log.debug("清空入账明细信息完成.");
        log.debug("入账明细信息入库...");
        debitTranInfoDao.insertSelectiveList(debitTranInfoList);
        log.debug("入账明细信息入库完成.");
	}

	@Override
	public BillDownloadResponse debitTallyDownload(BillDownloadRequest request) throws Exception {
		try {
			Thread.sleep(1000 * 60); // 防止与获取对账文件同时请求造成获取文件名相同（参数一样），发生文件覆盖

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*****获取请求参数值********/
		String settleDate = request.getSettleDate();// 清算日期
        Date strToDate = IfspDateTime.strToDate(settleDate, IfspDateTime.YYYY_MM_DD);
		/*********初始化响应对象***********/
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
		log.info("---------------------删除表数据------------------");
//		1.	根据清算日期和通道编号删除借记卡对账文件明细表(DEBIT_TRAN_INFO)
		debitTranInfoDao.deleteTallyByDate(settleDate);
        BthPagyChkInfDao.deleteBypagyNoAndDate("604bbbbbbbbbbbb",strToDate);
//		2.	获取借记卡对账文件存储信息
		//调本行下载本行借记卡对账单
		SoaParams params = new SoaParams();
		String timeStamp = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
		params.put("chkEntryDt", settleDate); //对账日期
        params.put("chkEntryClsCd", "EBAP002"); //对账分类编码
        params.put("docNm", "052" + timeStamp + "RPS001_check.txt"); //文件名
		SoaResults result;

		result = keepAccSoaService.debitBill(params);
		Map<Object, Object> datas = result.getDatas();
		if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode"))) {
			throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		}
		
		if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
				RespConstans.RESP_SUCCESS.getCode())) {
		} else {
			log.info("====================="+datas+"=========================");
			log.info("=====================成功================================");
		}
		String localFileUrl=tallyLocalFileUrl;
		String localFileName=(String) datas.get("docNm");//zip


        log.info("本行记账反馈文件名[{}]",localFileName);
        // 记录文件到表里
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
        BthPagyChkInf bthPagyChkInf = new BthPagyChkInf();
        bthPagyChkInf.setPagyNo("604bbbbbbbbbbbb");
        bthPagyChkInf.setChkDataDt(strToDate);
        bthPagyChkInf.setPagySysNo(request.getPagySysNo());
        bthPagyChkInf.setReqChkTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        bthPagyChkInf.setGetChkSt("00");
        bthPagyChkInf.setChkFilePath(localFileUrl+localFileName);
        bthPagyChkInf.setChkFileImpSt("00");
        bthPagyChkInf.setChkAcctSt("00");
        bthPagyChkInf.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        bthPagyChkInf.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        BthPagyChkInfDao.insertSelective(bthPagyChkInf);


//		4.	根据存储路径信息读取文件并解析入库
		//============================================================
		File filePath = new File(localFileUrl);
		// 如果文件夹不存在则创建
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("文件存放本地目录不存在");
			filePath.mkdirs();
		} else {
			log.info("文件存放本地目录存在");
		}
		
		String localFilePath = localFileUrl + localFileName; // 本地存放路径
//		String localFilePath = localFileUrl + "05220180920191641600RPS001_check.txt"; // 本地存放路径
		String localFlagFilePath = localFilePath+".ok"; // 本地存放路径
//		String localFlagFilePath = localFileUrl + "60420180830EBAP002_check.txt.ok"; // 本地存放路径
//		 String localFilePath ="H:/data/exchange/ebap/batch/recv/debitBill/05220180901150340081RPS001_check.txt";
//		 String localFlagFilePath ="H:/data/exchange/ebap/batch/recv/debitBill/05220180901150340081RPS001_check.txt.ok";
		// // 本地存放路径
		log.info("本地存放路径为:" + localFilePath);
		int c = 3;
		do {
			if (!new File(localFlagFilePath).exists()) {
				log.info("------------ 本行记账ok文件不存在------------------");
				Thread.sleep(300000);
//				Thread.sleep(1000);
				
				c--;
				log.info("=====================标记文件不存在检查剩余次数[" + c + "]================================");
				continue;
			} else {
				log.info("=====================本行记账ok文件检查到存在================================");
				break;
			}
		} while (c > 0);
		
		// 本地存在直接读取，不存在中断
		if (!new File(localFlagFilePath).exists()) {
			log.info("------------ 尝试检查3次本行记账ok文件仍不存在不存在------------------");
			billDownloadResponse.setDownFlag("0");
			billDownloadResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			billDownloadResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
			return billDownloadResponse;
		}
		// 本地存在直接读取，不存在中断
		if (!new File(localFilePath).exists()) {
			log.info("------------ 本行记账文件不存在------------------");
			billDownloadResponse.setDownFlag("0");
			billDownloadResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			billDownloadResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
			return billDownloadResponse;

		}
		
		//4.3解析文件（以“|#|”分隔），将解析结果加载到DebitTranInfo
		log.info("------------ 解析业务明细文件(开始)------------------");
        try {
            //获取对账单成功
            //对账明细
            List<DebitTranInfo> debitTranInfoList = new ArrayList<DebitTranInfo>();

			//获取对账单文件流
			try (FileInputStream fis = new FileInputStream(localFilePath);
				 InputStreamReader isr = new InputStreamReader(fis,"GBK");
				 BufferedReader br = new BufferedReader(isr);){

                /**
                 * 按“|#|”截取,解析入库对账文件
                 */
                int i = 0;
                log.info("受理通道业务(下载对账单) 逻辑处理 ");
				while (br.ready()) {
					String tmpLine = br.readLine();
                    if(IfspStringUtil.isBlank(tmpLine)){
                        i++;
                        continue;
                    }
					DebitTranInfo debitTranInfo = new DebitTranInfo();
					debitTranInfo.setChannelNo(tmpLine.split("\\|#\\|",-1)[0]); // 渠道号 CHANNEL_NO
					debitTranInfo.setChannelDate(tmpLine.split("\\|#\\|",-1)[1]); // 渠道日期
					debitTranInfo.setChannelSeq(tmpLine.split("\\|#\\|",-1)[2]); // 渠道流水号
					debitTranInfo.setTxnDate(tmpLine.split("\\|#\\|",-1)[3]); // 交易日期 TXN_DATE
					debitTranInfo.setTellerSeq(tmpLine.split("\\|#\\|",-1)[4]); // 柜员流水号 TELLER_SEQ
					debitTranInfo.setTxnStatus(tmpLine.split("\\|#\\|",-1)[5]); // 交易状态 TXN_STATUS
					debitTranInfo.setPlatformDate(tmpLine.split("\\|#\\|",-1)[6]); // 平台日期
					debitTranInfo.setPlatformSeq(tmpLine.split("\\|#\\|",-1)[7]); // 平台流水号
					debitTranInfo.setTxnCode(tmpLine.split("\\|#\\|",-1)[8]); // 交易号 TXN_CODE
					debitTranInfo.setTxnOrg(tmpLine.split("\\|#\\|",-1)[9]); // 交易机构号 TXN_ORG
					debitTranInfo.setTxnTeller(tmpLine.split("\\|#\\|",-1)[10]); // 交易柜员号 TXN_TELLER
					debitTranInfo.setTxnCur(tmpLine.split("\\|#\\|",-1)[11]); // 币种 TXN_CUR
					debitTranInfo.setPayAccount(tmpLine.split("\\|#\\|",-1)[12]); // 账号 PAY_ACCOUNT
					debitTranInfo.setReceiveAccount(tmpLine.split("\\|#\\|",-1)[13]); // 对方账号
					debitTranInfo.setTxnAmount(new BigDecimal(tmpLine.split("\\|#\\|",-1)[14]).movePointRight(2)); // 交易金额 TXN_AMOUNT
					debitTranInfo.setReserved1("98"); // 备用 RESERVED1  记账类
					debitTranInfo.setChkDataDt(settleDate); // 对账日期 CHK_DATA_DT
					debitTranInfo.setChkAcctSt("00"); // 对账状态 CHK_ACCT_ST
					debitTranInfo.setChkRst(""); // 对账结果 CHK_RST
					debitTranInfo.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS()); // 最后更新时间 LST_UPD_TM
					debitTranInfoList.add(debitTranInfo);

					log.info("受理通道业务(下载对账单) 逻辑处理 :[ 读取信息 :[" + tmpLine + "] , 列数:[" + tmpLine.split("\\|#\\|",-1).length + "]]");

					i++;
				}
            } catch (IOException e) {
                log.error("下载对账单失败:", e);
                throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"下载对账单失败");
            }

            /**
             * 数据库操作
             */
            log.info("========获取本行记账文件成功 , 共 ["+debitTranInfoList.size()+"] 条对账信息!");
            updateTallyBillData(settleDate, debitTranInfoList);
        
        }catch (Exception e){
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
		
//		6.  根据结果补充响应对象
		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}

	/**
	 * 借记卡对账文件入库
	 * @param settleDate
	 * @param debitTranInfoList
	 */
	private void updateTallyBillData(String settleDate, List<DebitTranInfo> debitTranInfoList) {
		log.debug("清空入账明细信息...");
		debitTranInfoDao.deleteTallyByDate(settleDate);
        log.debug("清空入账明细信息完成.");
        log.debug("入账明细信息入库...");
        debitTranInfoDao.insertSelectiveList(debitTranInfoList);
        log.debug("入账明细信息入库完成.");
	}
	
	/**
     * 银联品牌服务费下载解析
     * @param request
     * @return
     */
	@Override
	public BillDownloadResponse brandFeeBillDownload(MerRegRequest request, HashMap<Object, Object> hashMap) throws Exception {
		// 1. 初始化
		String settleDate = request.getSettleDate();
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
        // FTP配置信息 （ip，端口号，用户名，密码，对账单名称，对账单远程路径，对账单本地路径，本地文件名）
		String ip=(String) hashMap.get("unionFeeDownIp");
		String port=(String) hashMap.get("unionFeeDownport");
		String userName=(String) hashMap.get("unionFeeUserName");
		String password=(String) hashMap.get("unionFeePassWord");
		String remoteUrl=(String) hashMap.get("unionFeeRemoteUrl");
		String remoteFileName=(String) hashMap.get("unionFeeRemoteFileName");
		remoteFileName = remoteFileName.replace("YYYYMMDD", settleDate);
		String localFileUrl=(String) hashMap.get("unionFeeLocalFileUrl");
		String localFileName=(String) hashMap.get("unionFeeLocalFileName");
		localFileName = localFileName.replace("YYYYMMDD", settleDate);
        // 查询出银联全渠道商户号 [PAGY_BASE_INFO   PAGY_SYS_NO  CHAR(3)]
        Set<String> pagySysNo = new HashSet<>();
        pagySysNo.add("607");
        pagySysNo.add("608");
        List<String> mchtNos = pagyMchtInfoDao.selectAllMchtNo(pagySysNo);


        // 2. 整理目录
		File filePath = new File(localFileUrl);
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("文件存放本地目录不存在");
			filePath.mkdirs();
		} else {
			log.info("文件存放本地目录存在");
		}

        // 远程文件目录
		String sftpFilePath = remoteUrl + remoteFileName;
		log.info("远程文件目录路径为:" + sftpFilePath);
        // 本地存放路径
		String localFilePath = localFileUrl + localFileName;
		log.info("本地存放路径为:" + localFilePath);


        File downFile = new File(localFilePath);
        if(downFile.exists()){
            log.info("文件已存在, 删除并重新下载..  ");
            downFile.delete();
        }else{
            log.info("文件不存在,将下载.. ");
        }

        SftpUtil sftpUtil = new SftpUtil(ip, userName, password, Integer.parseInt(port));

        // 等待文件是否存在
        boolean exitFlag = checkBalFileExist(sftpUtil, sftpFilePath);

        if (exitFlag){
            ChannelSftp sftp = sftpUtil.connectSFTP();
            log.info("远程目录文件存在");
            log.info("---------------------SFTP获取本行对账文件名------------------");
            sftpUtil.download(remoteUrl, remoteFileName, localFileUrl, localFileName, sftp);
            log.info("---------------------SFTP断开连接------------------");
            sftpUtil.disconnected(sftp);
        }else {
            log.info("远程目录文件不存在");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程目录文件不存在");
        }

		
        // 3. 解析文件
		log.info("------------ 解析业务明细文件(开始)------------------");
        try {
            int fileRowCount = (int)FileUtil.getFileRowCount(localFilePath, BthUnionBrandFeeInfo.class);
            List<BthUnionBrandFeeInfo> dataList =
                    FileUtil.readFileToList(localFilePath, BthUnionBrandFeeInfo.class, 0, fileRowCount);
            List<BthUnionBrandFeeInfo> bthUnionBrandFeeInfoList = new ArrayList<>();
            for (BthUnionBrandFeeInfo bthUnionBrandFeeInfo : dataList) {
                if (!mchtNos.contains(bthUnionBrandFeeInfo.getRecCardCode())){
                    continue;
                }
                // 主键编号
                bthUnionBrandFeeInfo.setId(IfspId.getUUID32());
                // 清算日期
                bthUnionBrandFeeInfo.setStlmDate(settleDate);
                bthUnionBrandFeeInfoList.add(bthUnionBrandFeeInfo);
            }

            // 数据库操作
            log.info("========获取银联品牌服务费对账文件成功 , 共 ["+bthUnionBrandFeeInfoList.size()+"] 条信息!");
            updateUnionBrandFee(settleDate, bthUnionBrandFeeInfoList);

        }catch (Exception e){
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException("9999","解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
		
		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}

	/**
	 * 银联品牌服务费下载解析new
	 * @param request
	 * @return
	 */
	@Override
	public BillDownloadResponse brandFeeBillDownloadNew(MerRegRequest request, HashMap<Object, Object> hashMap) throws Exception {
		// 1. 初始化
		String settleDate = request.getSettleDate();
		BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
		// FTP配置信息 （ip，端口号，用户名，密码，对账单名称，对账单远程路径，对账单本地路径，本地文件名）
		String ip=(String) hashMap.get("unionFeeDownIp");
		String port=(String) hashMap.get("unionFeeDownport");
		String userName=(String) hashMap.get("unionFeeUserName");
		String password=(String) hashMap.get("unionFeePassWord");
		String remoteUrl=(String) hashMap.get("unionFeeRemoteUrl");
		String remoteFileName=(String) hashMap.get("unionFeeRemoteFileName");
		remoteFileName = remoteFileName.replace("YYYYMMDD", settleDate);
		String localFileUrl=(String) hashMap.get("unionFeeLocalFileUrl");
		String localFileName=(String) hashMap.get("unionFeeLocalFileName");
		localFileName = localFileName.replace("YYYYMMDD", settleDate);
		// 查询出银联全渠道商户号 [PAGY_BASE_INFO   PAGY_SYS_NO  CHAR(3)]
		Set<String> pagySysNo = new HashSet<>();
		pagySysNo.add("607");
		pagySysNo.add("608");
		List<String> mchtNos = pagyMchtInfoDao.selectAllMchtNo(pagySysNo);


		// 2. 整理目录
		File filePath = new File(localFileUrl);
		if (!filePath.exists() && !filePath.isDirectory()) {
			log.info("文件存放本地目录不存在");
			filePath.mkdirs();
		} else {
			log.info("文件存放本地目录存在");
		}

		// 远程文件目录
		String sftpFilePath = remoteUrl + remoteFileName;
		log.info("远程文件目录路径为:" + sftpFilePath);
		// 本地存放路径
		String localFilePath = localFileUrl + localFileName;
		log.info("本地存放路径为:" + localFilePath);


		File downFile = new File(localFilePath);
		if(downFile.exists()){
			log.info("文件已存在, 删除并重新下载..  ");
			downFile.delete();
		}else{
			log.info("文件不存在,将下载.. ");
		}

		SftpUtil sftpUtil = new SftpUtil(ip, userName, password, Integer.parseInt(port));

		// 等待文件是否存在
		boolean exitFlag = checkBalFileExist(sftpUtil, sftpFilePath);

		if (exitFlag){
			ChannelSftp sftp = sftpUtil.connectSFTP();
			log.info("远程目录文件存在");
			log.info("---------------------SFTP获取本行对账文件名------------------");
			sftpUtil.download(remoteUrl, remoteFileName, localFileUrl, localFileName, sftp);
			log.info("---------------------SFTP断开连接------------------");
			sftpUtil.disconnected(sftp);
		}else {
			log.info("远程目录文件不存在");
			throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程目录文件不存在");
		}


		// 3. 解析文件
		log.info("------------ 解析业务明细文件(开始)------------------");
		try {
			int fileRowCount = (int)FileUtil.getFileRowCount(localFilePath, BthUnionBrandFeeInfo.class);
			List<BthUnionBrandFeeInfo> dataList =
					FileUtil.readFileToList(localFilePath, BthUnionBrandFeeInfo.class, 0, fileRowCount);
			List<BthUnionBrandFeeInfo> bthUnionBrandFeeInfoList = new ArrayList<>();
			for (BthUnionBrandFeeInfo bthUnionBrandFeeInfo : dataList) {
				if (!mchtNos.contains(bthUnionBrandFeeInfo.getRecCardCode())){
					continue;
				}
				// 主键编号
				bthUnionBrandFeeInfo.setId(IfspId.getUUID32());
				// 清算日期
				bthUnionBrandFeeInfo.setStlmDate(settleDate);
				bthUnionBrandFeeInfoList.add(bthUnionBrandFeeInfo);
			}

			// 数据库操作
			log.info("========获取银联品牌服务费对账文件成功 , 共 ["+bthUnionBrandFeeInfoList.size()+"] 条信息!");
			/**
			 * todo 更新银联文件表
			 */


//			updateUnionBrandFee(settleDate, bthUnionBrandFeeInfoList);

		}catch (Exception e){
			log.error("解析业务明细文件失败:", e);
			throw new IfspBizException("9999","解析业务明细文件失败");
		}
		log.info("------------ 解析业务明细文件(结束)------------------");

		billDownloadResponse.setDownFlag("1");
		billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return billDownloadResponse;
	}

    /**
     * 银联品牌服务费入库
     * @param settleDate
     * @param bthUnionBrandFeeInfoList
     */
    private void updateUnionBrandFee(String settleDate, List<BthUnionBrandFeeInfo> bthUnionBrandFeeInfoList) {
        log.info("---------------------删除银联品牌服务费表数据 , stlmDate: [{}]------------------",settleDate);
        bthUnionBrandFeeInfoDao.deleteByStlmDate(settleDate);
        log.info("---------------------银联品牌服务费表数据入库 , stlmDate: [{}]------------------",settleDate);
        bthUnionBrandFeeInfoDao.insertList(bthUnionBrandFeeInfoList);
    }


    /**
     *   一共涉及三张表的增删改 ,分别是 : BTH_UNION_FILE_DET   BTH_PAGY_FILE_SUM   BTH_PAGY_CHK_INF
     *   ! PAGY_SYS_NO  CHAR(4)
     * @param request
     * @param hashMap
     * @return
     */
    @Override
    public BillDownloadResponse upacpBillDownload(BillDownloadRequest request, Map<Object, Object> hashMap) throws ParseException {
        // 设置默认值
        String pagyNo = request.getPagyNo();
        String pagySysNo = request.getPagySysNo();
        // 清算日期
        String settleDate = request.getSettleDate();
        /*********初始化响应对象***********/
        BillDownloadResponse billDownloadResponse = new BillDownloadResponse();
        SimpleDateFormat form = new SimpleDateFormat( "yyyyMMdd");
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
        log.info("---------------------删除银联文件表数据------------------");
        //1.根据清算日期和通道编号删除银联对账文件明细表(BTH_UNION_FILE_DET)和导入对账汇总信息表(BTH_PAGY_FILE_SUM)以及对账信息记录表(BTH_PAGY_CHK_INF)
        bthUnionFileDetDao.deleteByPagySysNoAndDate(pagySysNo,settleDate);
        log.info("删除文件汇总表数据");
        bthPagyFileSumDao.deleteByPagySysNoAndDate(pagySysNo,settleDate);
        log.info("删除对账结果表数据今日数据 ,通道[608]");
        BthPagyChkInfDao.deleteBypagyNoAndDate(pagyNo,form.parse(settleDate));
        //2.通过通道系统编号查询FTP配置信息表，获取下载对账单的ftp信息（ip，端口号，用户名，密码，对账单名称，对账单远程路径，对账单本地路径，本地文件名）
        String ip=String.valueOf(hashMap.get("unionBillDownIp"));
        String port=String.valueOf(hashMap.get("unionBillDownport"));
        String userName=String.valueOf(hashMap.get("unionUserName"));
        String userPwd=String.valueOf(hashMap.get("unionPwd"));
        String remoteUrl= hashMap.get("unionRemoteUrl")+settleDate+"/";
        String remoteFileName=String.valueOf( hashMap.get("unionRemoteFileName"));
        remoteFileName = remoteFileName.replace("YYMMDD", settleDate.substring(2));
        String localFileUrl=String.valueOf(hashMap.get("unionLocalFileUrl"));
        String localFileName=String.valueOf( hashMap.get("unionLocalFileName"));
        localFileName = localFileName.replace("YYMMDD", settleDate.substring(2));

        //3.插入对账信息记录表BTH_PAGY_CHK_INF（对账文件获取状态：未执行）
        BthPagyChkInf bthPagyChkInf = new BthPagyChkInf();
        bthPagyChkInf.setPagyNo(pagyNo);
        bthPagyChkInf.setChkDataDt(form.parse(settleDate));
        bthPagyChkInf.setPagySysNo(pagySysNo);
        bthPagyChkInf.setReqChkTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        bthPagyChkInf.setGetChkSt("00");//对账文件获取状态00=未执行01=下载中02=下载完成03=不存在对账文件04=需要重试类错误99=系统类错误,需人工干预
        bthPagyChkInf.setChkFilePath(localFileUrl+localFileName);
        bthPagyChkInf.setChkFileImpSt("00");//对账文件导入状态00=未导入01=导入中02=导入成功99=导入失败
        bthPagyChkInf.setChkAcctSt("00");//00=未对账01=对账中02=对账完成99=对账异常
        bthPagyChkInf.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        bthPagyChkInf.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
        BthPagyChkInfDao.insertSelective(bthPagyChkInf);

        /**
         *  4.	根据ftp信息下载对账文件，下载失败抛错中断（重新发起），
         *     下载成功则读取对账文件信息解析到银联对账文件明细表BTH_UNION_FILE_DET（如果解析失败抛错中断，等待重新发起），
         *     根据汇总信息加载到导入对账汇总信息表BTH_PAGY_FILE_SUM，入库。
         */

        //	4.1 从ftp下载对账文件
        File filePath = new File(localFileUrl);
        // 如果文件夹不存在则创建
        if (!filePath.exists() && !filePath.isDirectory()) {
            log.info("文件存放本地目录不存在");
            filePath.mkdirs();
        } else {
            log.info("文件存放本地目录存在");
        }
        // 远程文件目录
        String sftpFilePath = remoteUrl + remoteFileName;
        log.info("远程文件目录路径为:" + sftpFilePath);
        // 本地存放路径
        String localFilePath = localFileUrl + localFileName;
        log.info("本地存放路径为:" + localFilePath);

        File downFile = new File(localFilePath);
        if(downFile.exists()){
            log.info("文件已存在");
            downFile.delete();
        }else{
            log.info("文件不存在,将下载");
        }



        SftpUtil sftpUtil = new SftpUtil(ip, userName, userPwd, Integer.parseInt(port));


        // 等待文件是否存在
        boolean exitFlag = checkBalFileExist(sftpUtil, sftpFilePath);

        if (exitFlag){
            ChannelSftp sftp = sftpUtil.connectSFTP();
            log.info("远程目录文件存在");
            log.info("---------------------SFTP获取本行对账文件名------------------");
            sftpUtil.download(remoteUrl, remoteFileName, localFileUrl, localFileName, sftp);
            log.info("---------------------SFTP断开连接------------------");
            sftpUtil.disconnected(sftp);
        }else {
            log.info("远程目录文件不存在");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程目录文件不存在");

        }

        // 查询出银联全渠道商户号 [PAGY_BASE_INFO   PAGY_SYS_NO  CHAR(3)]
        Set<String> set = new HashSet<>();
        set.add("608");
        List<String> mchtNosTotalUn = pagyMchtInfoDao.selectAllMchtNo(set);

        //对账汇总金额
        List<BthPagyFileSum> bthPagyFileSumList = new ArrayList<BthPagyFileSum>();

        int txnTotalCnt =0;
        BigDecimal txnTotalAmt = new BigDecimal(0);
        int refTotalCnt = 0;
        BigDecimal refTotalAmt = new BigDecimal(0);
        BigDecimal feeTotalAmt = new BigDecimal(0);
        BigDecimal recptAmt ;

        //	4.2解析文件（按照字符串长度截取），将解析结果加载到表
        log.info("------------ 解析业务明细文件(开始)------------------");
        try {
            //读取业务明细对账文件
            int fileRowCount = (int)FileUtil.getFileRowCount(localFilePath, BthUnionFileDetVO.class);
            List<BthUnionFileDetVO> dataList =
                    FileUtil.readFileToList(localFilePath, BthUnionFileDetVO.class, 0, fileRowCount);
            List<BthUnionFileDet> bthUnionFileDetList = new ArrayList<>();
            for (BthUnionFileDetVO bthUnionFileDetVO : dataList) {
                if (!mchtNosTotalUn.contains(bthUnionFileDetVO.getRecCardCode())){
                    continue;
                }
                // 受理方应收手续费
                String hearGetFee = bthUnionFileDetVO.getHearGetFee();
                // 受理方应付手续费
                String hearPayFee = bthUnionFileDetVO.getHearPayFee();
                // 转接服务费
                String routeSevFee = bthUnionFileDetVO.getRouteSevFee();
                // 去掉第一位的字母
                bthUnionFileDetVO.setRouteSevFee(routeSevFee.substring(1));
                BthUnionFileDet bthUnionFileDet = new BthUnionFileDet();
                ReflectionUtil.copyProperties(bthUnionFileDetVO , bthUnionFileDet);
                txnTotalCnt ++;
                //通道系统编号	PAGY_SYS_NO
                bthUnionFileDet.setPagySysNo(pagySysNo);
                //通道编号	PAGY_NO
                bthUnionFileDet.setPagyNo(pagyNo);
                //对账日期	CHK_DATA_DT
                bthUnionFileDet.setChkDataDt(settleDate);
                //对账状态	CHK_ACCT_ST
                bthUnionFileDet.setChkAcctSt("00");
                //对账结果	CHK_RST
                bthUnionFileDet.setChkRst("");
                // 银联收的手续费 (存在CustomerFee中)
                BigDecimal feeAmt = getUnionFee(hearGetFee, hearPayFee, routeSevFee);
                bthUnionFileDet.setCustomerFee(String.valueOf(feeAmt));

                //最后更新时间	LST_UPD_TM
                bthUnionFileDet.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS());
                bthUnionFileDetList.add(bthUnionFileDet);

                // 计算汇总正交易金额(包含退款金额,不减去)
                txnTotalAmt = txnTotalAmt.add(bthUnionFileDet.getTransAmt());

                // 反交易
                if (bthUnionFileDet.getTransCode().startsWith("20")){
                    // 计算汇总反交易金额
                    refTotalAmt = refTotalAmt.add(bthUnionFileDet.getTransAmt());
                    refTotalCnt++ ;
                }

                // 手续费金额汇总
                feeTotalAmt =getTottalFee(feeTotalAmt,hearGetFee,hearPayFee,routeSevFee);


            }
            log.info("========获取银联对账文件成功 , 共 ["+txnTotalCnt+"] 条对账信息!");

            // 实收金额
            recptAmt = txnTotalAmt.subtract(refTotalAmt);

            BthPagyFileSum bthPagyFileSum = new BthPagyFileSum();
            bthPagyFileSum.setPagySysNo(pagySysNo);
            bthPagyFileSum.setPagyNo(pagyNo);
            bthPagyFileSum.setChkDataDt(settleDate);
            bthPagyFileSum.setTxnTotalCnt(new BigDecimal(txnTotalCnt));
            bthPagyFileSum.setTxnTotalAmt(txnTotalAmt);
            bthPagyFileSum.setRefTotalCnt(new BigDecimal(refTotalCnt));
            bthPagyFileSum.setRefTotalAmt(recptAmt);
            bthPagyFileSum.setFeeTotalAmt(feeTotalAmt);
            bthPagyFileSum.setRecptAmt(recptAmt);
            bthPagyFileSum.setSumRmk("汇总信息");
            bthPagyFileSum.setCrtTm(IfspDateTime.getYYYYMMDDHHMMSS());
            bthPagyFileSumList.add(bthPagyFileSum);

            /**
             * 数据库操作
             */
            log.info("========获取银联对账文件成功 , 共 ["+bthUnionFileDetList.size()+"] 条对账信息!");
            insertUnionBillData(bthUnionFileDetList, bthPagyFileSumList);

            billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

        }catch (Exception e){
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
        //	5.	更新对账信息记录表BTH_PAGY_CHK_INF信息（对账文件获取状态：下载完成）
        bthPagyChkInf.setGetChkSt("02");
        BthPagyChkInfDao.updateByPrimaryKeySelective(bthPagyChkInf);

        //	6.  根据结果补充响应对象
        billDownloadResponse.setDownFlag("1");
        billDownloadResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        billDownloadResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        return billDownloadResponse;
    }


    private void insertUnionBillData(List<BthUnionFileDet> bthUnionFileDetList, List<BthPagyFileSum> bthPagyFileSumList) {
        if(IfspDataVerifyUtil.isNotEmptyList(bthUnionFileDetList)){
            log.info("银联文件明细入库...");
            bthUnionFileDetDao.insertSelectiveList(bthUnionFileDetList);
            log.info("银联文件明细入库完成.");
        }

        if(IfspDataVerifyUtil.isNotEmptyList(bthPagyFileSumList)){
            log.info("通道汇总文件信息入库...");
            bthPagyFileSumDao.insertSelectiveList(bthPagyFileSumList);
            log.info("通道汇总文件信息入库成功.");
        }


    }

	private void insertUnionBillAllData(List<UnionBillOuter> unionBillOuterList, List<BthPagyFileSum> bthPagyFileSumList) {
		if(IfspDataVerifyUtil.isNotEmptyList(unionBillOuterList)){
			log.info("银联文件明细入库...");
			unionBillOuterDao.insertBatch(unionBillOuterList);
			log.info("银联文件明细入库完成.");
		}

		if(IfspDataVerifyUtil.isNotEmptyList(bthPagyFileSumList)){
			log.info("通道汇总文件信息入库...");
			bthPagyFileSumDao.insertSelectiveList(bthPagyFileSumList);
			log.info("通道汇总文件信息入库成功.");
		}


	}


    /**
     * 检查对账文件是否已经存在
     * @param sftpUtil
     * @param fileCompPath
     * @return
     */
    private boolean checkBalFileExist(SftpUtil sftpUtil , String fileCompPath ){

        // 尝试连接远程查看文件是否存在
        // 默认是半小时检查异常 , 尝试次数为10分钟
        String spaceFlag = "1800000,10";

        // 数据库配置的参数信息
        BthSysParamInfo bthSysParamInfo = bthSysParamInfoDao.selectByParamCode("BAL_ACC_FILE_CHK");
        if (IfspDataVerifyUtil.isNotBlank(bthSysParamInfo) && IfspDataVerifyUtil.isNotBlank(bthSysParamInfo.getParamInfo())){
            // 数据库配了 ,以数据库为准
            spaceFlag = bthSysParamInfo.getParamInfo() ;
        }

        String[] split = spaceFlag.split(",");

        // 间隔时间
        long waitInterval = Long.parseLong(split[0]);
        // 尝试次数
        int  tryCount =   Integer.parseInt(split[1]);


        do {

            ChannelSftp sftp = sftpUtil.connectSFTP();



            // 判断远程目录文件是否存在
            if (sftpUtil.isFileExist(sftp, fileCompPath)) {
                // 断开连接
                sftp.disconnect();
                return true;
            }else {
                // 断开连接
                sftp.disconnect();

                if (tryCount > 0 ) {
                    log.info("远程对账文件不存在 ,等待[{}]ms后重试 ,  剩余次数 : [{}] " ,waitInterval,tryCount );
                    try {
                        Thread.sleep(waitInterval);
                    } catch (InterruptedException e) {
                        log.error("等待对账文件睡眠异常...");
                    }
                }

                tryCount -- ;
            }


        } while (tryCount >= 0);

        return false;

    }


    /**
     * 检查对账文件是否已经存在 (支付宝明细与汇总文件)
     * @param sftpUtil
     * @param sftpFilePath
     * @param sftpSumFilePath
     * @return
     */
    private boolean checkBalFileExist2(SftpUtil sftpUtil, String sftpFilePath, String sftpSumFilePath) {

        // 尝试连接远程查看文件是否存在
        // 默认是半小时检查异常 , 尝试次数为10分钟
        String spaceFlag = "1800000,10";

        // 数据库配置的参数信息
        BthSysParamInfo bthSysParamInfo = bthSysParamInfoDao.selectByParamCode("BAL_ACC_FILE_CHK");
        if (IfspDataVerifyUtil.isNotBlank(bthSysParamInfo) && IfspDataVerifyUtil.isNotBlank(bthSysParamInfo.getParamInfo())){
            // 数据库配了 ,以数据库为准
            spaceFlag = bthSysParamInfo.getParamInfo() ;
        }

        String[] split = spaceFlag.split(",");

        // 间隔时间
        long waitInterval = Long.parseLong(split[0]);
        // 尝试次数
        int  tryCount =   Integer.parseInt(split[1]);


        do {

            ChannelSftp sftp = sftpUtil.connectSFTP();

            // 判断远程目录文件是否存在
            if (sftpUtil.isFileExist(sftp, sftpFilePath) && sftpUtil.isFileExist(sftp, sftpSumFilePath)) {
                // 断开连接
                sftp.disconnect();
                return true;
            }else {
                // 断开连接
                sftp.disconnect();

                if (tryCount > 0 ) {
                    log.info("远程对账文件不存在 ,等待[{}]ms后重试 ,  剩余次数 : [{}] " ,waitInterval,tryCount );
                    try {
                        Thread.sleep(waitInterval);
                    } catch (InterruptedException e) {
                        log.error("等待对账文件睡眠异常...");
                    }
                }


                tryCount -- ;
            }


        } while (tryCount >= 0);

        return false;


    }

	/**
	 * 获取对账日期
	 * @param dateStr
	 * @return
	 */
	private Date getRecoDate(String dateStr){
		if(StringUtils.isBlank(dateStr)){
			throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期为空");
		}
		try{
			return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").toDate(); //todo 改成常数
		}catch (Exception e){
			log.error("对账日期格式错误: ", e);
			throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期格式错误");
		}
	}


	/**
	 * 记账表状态更新工作线程
	 *
	 * @param <T>
	 */
	class CoreHandler<T> implements Callable<Integer> {

		private List<CoreBillInfo> coreBillInfoList;

		public CoreHandler(List<CoreBillInfo> coreBillInfoList) {
			this.coreBillInfoList = coreBillInfoList;
		}

		@Override
		public Integer call() throws Exception {

			long sTime = System.currentTimeMillis();
			log.info("子线程处理开始时间[{}]ms",sTime);
			if (coreBillInfoList == null || coreBillInfoList.isEmpty()) {
				log.warn("核心记账文件明细为空,无需插入");
				return 0;
			} else {
				int count = coreBillInfoDao.insertBatch(coreBillInfoList);
				long eTime = System.currentTimeMillis();
				log.info("插入核心记账文件明细{}-{},数目{}, 耗时{}", coreBillInfoList.get(0).getTxnSsn(),
						coreBillInfoList.get(coreBillInfoList.size() - 1).getTxnSsn(), count, (eTime - sTime));
				return count;
			}
		}
	}

	/**
	 * 解析银联对账文件工作线程
	 *
	 * @param <T>
	 */
	class UnionHandler<T> implements Callable<Integer> {

		private List<UnionBillOuter> unionBillInfoList;

		public UnionHandler(List<UnionBillOuter> unionBillInfoList) {
			this.unionBillInfoList = unionBillInfoList;
		}

		@Override
		public Integer call() throws Exception {

			long sTime = System.currentTimeMillis();
			log.info("子线程处理开始时间[{}]ms",sTime);
			if (unionBillInfoList == null || unionBillInfoList.isEmpty()) {
				log.warn("银联对账文件为空 ,无需插入");
				return 0;
			} else {
				int count = unionBillOuterDao.insertBatch(unionBillInfoList);
				long eTime = System.currentTimeMillis();
				log.info("插入银联对账文件{}-{},数目{}, 耗时{}", unionBillInfoList.get(0).getTxnSsn(),
						unionBillInfoList.get(unionBillInfoList.size() - 1).getTxnSsn(), count, (eTime - sTime));
				return count;
			}
		}
	}

//	@PostConstruct
	private void initPoolCore() {
		destoryPoolCore();
		log.info("====初始化线程池(start)====");
        /*
         * 初始化线程池
         */
		if (executorCore != null) {
            /*
             * 关闭线程池
             */
			try {
				executorCore.shutdown();
				if(!executorCore.awaitTermination(10, TimeUnit.SECONDS)){
					executorCore.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("awaitTermination interrupted: " + e);
				executorCore.shutdownNow();
			}
		}
        /*
         * 构建
         */
		executorCore = Executors.newFixedThreadPool(coreThreadCount, new ThreadFactory() {
			AtomicInteger atomic = new AtomicInteger();

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "coreBillHander_" + this.atomic.getAndIncrement());
			}
		});
		log.info("====初始化线程池(end)====");
	}

	private void destoryPoolCore() {
		log.info("====销毁线程池(start)====");
		/*
		 * 初始化线程池
		 */
		if (executorCore != null) {
			log.info("线程池为null, 无需清理");
			/*
			 * 关闭线程池
			 */
			try {
				executorCore.shutdown();
				if(!executorCore.awaitTermination(10, TimeUnit.SECONDS)){
					executorCore.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("awaitTermination interrupted: " + e);
				executorCore.shutdownNow();
			}
		}
		log.info("====销毁线程池(end)====");
	}

	private void initPoolUnion() {
		destoryPoolUnion();
		log.info("====初始化线程池(start)====");
        /*
         * 初始化线程池
         */
		if (unionExecutor != null) {
            /*
             * 关闭线程池
             */
			try {
				unionExecutor.shutdown();
				if(!unionExecutor.awaitTermination(10, TimeUnit.SECONDS)){
					unionExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("awaitTermination interrupted: " + e);
				unionExecutor.shutdownNow();
			}
		}
        /*
         * 构建
         */
		unionExecutor = Executors.newFixedThreadPool(unionThreadCount, new ThreadFactory() {
			AtomicInteger atomic = new AtomicInteger();

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "unionBillHander_" + this.atomic.getAndIncrement());
			}
		});
		log.info("====初始化线程池(end)====");
	}

	private void destoryPoolUnion() {
		log.info("====销毁线程池(start)====");
		/*
		 * 初始化线程池
		 */
		if (unionExecutor != null) {
			log.info("线程池为null, 无需清理");
			/*
			 * 关闭线程池
			 */
			try {
				unionExecutor.shutdown();
				if(!unionExecutor.awaitTermination(10, TimeUnit.SECONDS)){
					unionExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("awaitTermination interrupted: " + e);
				unionExecutor.shutdownNow();
			}
		}
		log.info("====销毁线程池(end)====");
	}

	/**
	 * 检查Ok文件是否已经存在
	 * @param sftpUtil
	 * @param okPath
	 * @return
	 */
	private boolean checkWxFileExist(SftpUtil sftpUtil, String remoteUrl, String okPath) {
        /*
            尝试连接远程查看文件是否存在
            默认是半小时检查异常 , 尝试次数为10次  */
		String spaceFlag = "1800000,10";

		// 数据库配置的参数信息
		BthSysParamInfo bthSysParamInfo = bthSysParamInfoDao.selectByParamCode("BAL_ACC_FILE_CHK");
		if (IfspDataVerifyUtil.isNotBlank(bthSysParamInfo) && IfspDataVerifyUtil.isNotBlank(bthSysParamInfo.getParamInfo())){
			// 数据库配了 ,以数据库为准
			spaceFlag = bthSysParamInfo.getParamInfo() ;
		}

		String[] split = spaceFlag.split(",");

		// 间隔时间
		long waitInterval = Long.parseLong(split[0]);
		// 尝试次数
		int  tryCount =   Integer.parseInt(split[1]);

		do {
			ChannelSftp sftp = sftpUtil.connectSFTP();
			// 判断远程目录文件是否存在
			if (sftpUtil.isFileExist(sftp, okPath)) {
				// 断开连接
				sftp.disconnect();
				return true;
			}else {
				// 断开连接
				sftp.disconnect();

				if (tryCount > 0 ) {
					log.info("远程对账ok文件[{}]不存在 ,等待[{}]ms后重试 ,  剩余次数 : [{}] " ,okPath,waitInterval,tryCount );
					try {
						Thread.sleep(waitInterval);
					} catch (InterruptedException e) {
						log.error("等待对账文件睡眠异常...");
					}
				}
				tryCount -- ;
			}

		} while (tryCount >= 0);

		return false;
	}

	private String getAliOkFileNm(Date txnDate){
		return "WeChat-"+new DateTime(txnDate).toString(DateTimeFormat.forPattern("yyyy-MM-dd"))+".ok";
	}



}
