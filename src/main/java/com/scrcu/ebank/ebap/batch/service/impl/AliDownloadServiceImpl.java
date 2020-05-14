package com.scrcu.ebank.ebap.batch.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.opencsv.CSVReader;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.AliBillOuter;
import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyFileSum;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSysParamInfo;
import com.scrcu.ebank.ebap.batch.bean.request.GetOrderInfoRequest;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.EncryptUtil;
import com.scrcu.ebank.ebap.batch.common.utils.SftpUtil;
import com.scrcu.ebank.ebap.batch.dao.AliBillOuterDao;
import com.scrcu.ebank.ebap.batch.dao.BthPagyFileSumDao;
import com.scrcu.ebank.ebap.batch.dao.BthSysParamInfoDao;
import com.scrcu.ebank.ebap.batch.service.BillsDownloadService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ljy
 * @date 2019-05-21
 */
@Service("aliDownloadService")
@Slf4j
public class AliDownloadServiceImpl implements BillsDownloadService {


    /**
     * 批次插入的数量
     */
    @Value("${aliBill.batchInsertCount}")
    private Integer batchInsertCount;

    /**
     * 工作线程数量
     */
    @Value("${aliBill.threadCount}")
    private Integer threadCount;


    @Resource
    private AliBillOuterDao aliBillOuterDao;

    @Resource
    private BthPagyFileSumDao  bthPagyFileSumDao;

    /**
     * 批量系统参数表
     */
    @Resource
    private BthSysParamInfoDao bthSysParamInfoDao;

    /*
     * 支付宝账单下载相关参数
     */
    @Value("${zfbBillDownIp}")
    private String zfbBillDownIp;
    @Value("${zfbBillDownport}")
    private Integer zfbBillDownport;
    @Value("${zfbUserName}")
    private String zfbUserName;
    @Value("${zfbPwd}")
    private String zfbPassWord;
    @Value("${zfbRemoteUrl}")
    private String zfbRemoteUrl;
    @Value("${zfbLocalFileUrl}")
    private String zfbLocalFileUrl;
    @Value("${zfbFilePre}")
    private String zfbFilePre;

    /**
     * 线程池
     */
    ExecutorService executor;


    @Override
    public CommonResponse billDownload(GetOrderInfoRequest req) {

        //获取的订单数据的交易日期
        Date txnDate = DateUtil.getDate(req.getSettleDate());
        log.info("交易日期: " + DateUtil.toDateString(txnDate));
        //计算对账日期 = 交易日期 + 1
        Date recoDate = DateUtil.getAfterDate(req.getSettleDate(),1);
        log.info("对账日期: " + DateUtil.toDateString(recoDate));

        /*
         *  清理数据
         */
        clear(recoDate,req.getSettleDate());

        //支付宝明细文件名
        String zfbDtlFileName = getZfbDtlFileName(req.getSettleDate());
        //支付宝汇总文件名
        String zfbSumFileName = getZfbSumFileName(req.getSettleDate());

        /*
         *  从SFTP下载文件到本地
         */
        //远程文件存在路径:  根目录+日期
        downloadAliFile(
            zfbBillDownIp,zfbBillDownport, zfbUserName,zfbPassWord,
            getAliRemoteFilePath(req.getSettleDate()),
            zfbDtlFileName,zfbSumFileName,getAliOkFileNm(txnDate)
        );
        log.info("下载文件完成");

        /**
         * 初始化线程池
         */
        initPool();

        //解析明细文件入库
        try {
            parseAndInsertDtl(recoDate,new File(zfbLocalFileUrl + zfbDtlFileName));
        } finally {
            //销毁线程池
            destoryPool();
        }
        log.info("解析支付宝明细文件完成.");


        //解析汇总文件
        parseAndInsertSum(req.getSettleDate(),new File(zfbLocalFileUrl + zfbSumFileName));
        log.info("解析支付宝汇总文件完成.");

        return new CommonResponse();
    }




    /**
     * 销毁线程池
     */
    private void destoryPool() {
        log.info("====销毁线程池(start)====");

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
     * 初始化线程池
     */
    private void initPool() {
        destoryPool();
        /*
         * 构建
         */
        log.info("====初始化线程池(start)====");
        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
            AtomicInteger atomic = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "aliBillHander_" + this.atomic.getAndIncrement());
            }
        });
        log.info("====初始化线程池(end)====");

    }


    /**
     *  AaliPay-YYYY-MM-DD.ok
     *  标记支付宝对账文件存在的ok文件
     * @param txnDate
     * @return
     */
    private String getAliOkFileNm(Date txnDate){
        return "AaliPay-"+new DateTime(txnDate).toString(DateTimeFormat.forPattern("yyyy-MM-dd"))+".ok";
    }


    /**
     * 支付宝远程文件目录
     * @param settleDate
     * @return
     */
    private String getAliRemoteFilePath(String settleDate){
        return zfbRemoteUrl+settleDate+"/";
    }



    /**
     * 获取支付宝明细文件名
      * @param settleDate
     * @return
     */
    private String getZfbDtlFileName(String settleDate) {
        return zfbFilePre+"_"+settleDate+"_DETAILS.csv";
    }


    /**
     * 获取支付宝汇总文件名
     * @param settleDate
     * @return
     */
    private String getZfbSumFileName(String settleDate) {
        return zfbFilePre+"_"+settleDate+"_SUMMARY.csv";
    }


    /**
     * 下载支付宝文件
     * @param ip
     * @param port
     * @param userName
     * @param password
     * @param remoteUrl
     * @param zfbDtlFileName
     * @param zfbSumFileName
     * @return
     */
    private void downloadAliFile(String ip, int port, String userName, String password,
                                             String remoteUrl, String zfbDtlFileName, String zfbSumFileName, String aliOkFile) {
        /**
         * 检查本地目录是否存在,不存在则创建
         * 检查本地文件是否存在,存在则删除
         */
        preLocalPath(zfbDtlFileName, zfbSumFileName);

        /**
         * 尝试去SFTP下载文件
         */
        SftpUtil sftpUtil = new SftpUtil(ip, userName, password, port);
        if (checkAliBalFileExist(sftpUtil, remoteUrl, aliOkFile)){
            ChannelSftp sftp = sftpUtil.connectSFTP();
            log.info("远程目录文件存在,将下载...");
            log.info("远程csv目录路径为:" + remoteUrl);
            log.info("本地csv存放路径为:" + zfbLocalFileUrl);
            // 下载明细文件
            sftpUtil.download(remoteUrl, zfbDtlFileName, zfbLocalFileUrl, zfbDtlFileName, sftp);
            sftpUtil.download(remoteUrl, zfbSumFileName, zfbLocalFileUrl, zfbSumFileName, sftp);
            sftpUtil.disconnected(sftp);

        }else {
            log.info("远程目录文件不存在");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"远程目录文件不存在");

        }

    }

    /**
     * 检查本地目录是否存在,不存在则创建
     * 检查本地文件是否存在,存在则删除
     * @param zfbDtlFileName
     * @param zfbSumFileName
     */
    private void preLocalPath(String zfbDtlFileName, String zfbSumFileName) {
        //检查文件是否存在
        File filePath = new File(zfbLocalFileUrl);
        if (!filePath.exists() && !filePath.isDirectory()) {
            log.info("文件存放本地目录不存在,自动创建...");
            filePath.mkdirs();
        }

        File aliDtlFile = new File(zfbLocalFileUrl + zfbDtlFileName);
        if(aliDtlFile.exists()){
            log.info("======>>删除已存在的支付宝明细文件...");
            aliDtlFile.delete();
        }

        File aliSumFile = new File(zfbLocalFileUrl + zfbSumFileName);
        if(aliSumFile.exists()){
            log.info("======>>删除已存在的支付宝汇总文件...");
            aliSumFile.delete();
        }
    }


    /**
     * 清除对账日下载的明细
     * @param recoDate
     * @param settleDate
     */
    private void clear(Date recoDate, String settleDate) {
        int clearCount = aliBillOuterDao.clear(recoDate);
        log.info("清理支付宝明细文件记录:"+clearCount);
        int sumCount = bthPagyFileSumDao.deleteBypagyNoAndDate("606000000000001", settleDate);
        log.info("清理支付宝汇总文件记录:"+sumCount);
    }


    /**
     * 解析支付宝明细文件入库
     * @param recoDate
     * @param billFile
     */
    private void parseAndInsertDtl(Date recoDate, File billFile){
        log.info("=======>解析支付宝明细文件入库start<<========");
        //对账明细
        List<AliBillOuter> outerRecordList = new ArrayList<>();

        //线程处理结果
        List<Future> futureList = new ArrayList<>();
        try(
                InputStreamReader in = new InputStreamReader(new FileInputStream(billFile), Charset.forName("UTF-8"));
                CSVReader reader = new CSVReader(in)
        )
        {
            String[] nextLine;
            AliBillOuter outerRecord;
            int index = 0; //当前处理的行数
            while ((nextLine = reader.readNext()) != null) {
                if(index <= 4 ){
                    //跳过表头处理
                    index++;
                    continue;
                }else if(index>4 &&nextLine[0].startsWith("#")){
                    // 跳过文件尾
                    break;
                }else {
                    outerRecord = new AliBillOuter(recoDate, nextLine);
                    outerRecordList.add(outerRecord);
                    /*
                     * 批量插入
                     */
                    if(outerRecordList.size() == batchInsertCount){
                        futureList.add(executor.submit(new AliHandler(outerRecordList)));
                        //清理集合
                        outerRecordList = new ArrayList<>();
                    }
                }
                index ++;
            }
            /*
             */
            if(outerRecordList.size() > 0){
                futureList.add(executor.submit(new AliHandler(outerRecordList)));
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

            log.info("保存支付宝对账明细完成, 数目:" + (index-5));

        }catch (Exception e){
            log.error("文件解析入库异常: ", e);
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "文件解析入库失败");
        }
    }

    /**
     * 解析支付宝汇总文件入库
     * @param settleDate
     * @param billFile
     */
    private void parseAndInsertSum(String settleDate, File billFile) {
        log.info("=======>解析支付宝汇总文件入库start<<========");
        //对账明细
        List<BthPagyFileSum> outerRecordList = new ArrayList<>();
        try(
                InputStreamReader in = new InputStreamReader(new FileInputStream(billFile), Charset.forName("UTF-8"));
                CSVReader reader = new CSVReader(in)
        )
        {
            String[] nextLine;
            BthPagyFileSum outerRecord;
            int index = 0; //当前处理的行数
            while ((nextLine = reader.readNext()) != null) {
                //跳过前四行开始读 , 读到"合计"解析入库 , 中断读文件
                if(index > 4  && nextLine[0].trim().equals("合计")  ){
                    outerRecord = new BthPagyFileSum(settleDate,nextLine);
                    outerRecordList.add(outerRecord);
                    bthPagyFileSumDao.insertSelectiveList(outerRecordList);
                    log.info("支付宝汇总信息入库成功.");
                    //读完合计行,跳过文件尾
                    break;
                }
                index ++;
            }

        }catch (Exception e){
            log.error("文件解析入库异常: ", e);
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "文件解析入库失败");
        }

    }


    /**
     * 工作线程
     */
    class AliHandler implements Callable<Integer>{

        private List<AliBillOuter> outerList;

        private AliHandler(List<AliBillOuter> outerList){
            this.outerList = outerList;
        }

        @Override
        public Integer call() throws Exception {

            long sTime = System.currentTimeMillis();
            log.info("子线程处理开始时间[{}]ms",sTime);
            if(outerList == null || outerList.isEmpty()){
                log.warn("支付宝账单明细为空,无需插入");
                return 0;
            }else {
                int count = aliBillOuterDao.insertBatch(outerList);
                long eTime = System.currentTimeMillis();
                log.info("插入支付宝账单明细[{}-{}],数目[{}], 耗时[{}]ms", outerList.get(0).getTxnSsn(),
                        outerList.get(outerList.size() - 1).getTxnSsn(), count, (eTime - sTime));
                return count;
            }
        }
    }



    /**
     * 检查Ok文件是否已经存在
     * @param sftpUtil
     * @param okPath
     * @return
     */
    private boolean checkAliBalFileExist(SftpUtil sftpUtil, String remoteUrl, String okPath) {
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
            if (sftpUtil.isFileExist(sftp, remoteUrl+okPath)) {
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
}
