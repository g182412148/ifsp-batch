package com.scrcu.ebank.ebap.batch.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetPagyTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.InAccMerVo;
import com.scrcu.ebank.ebap.batch.bean.vo.TxnCountVo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.ReRunFlagEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RealTmFlagEnum;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import com.scrcu.ebank.ebap.batch.common.utils.ExcelUtils;
import com.scrcu.ebank.ebap.batch.common.utils.SftpUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.TestEnvGenAccFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author: ljy
 * @create: 2018-11-06 10:37
 */
@Service
@Slf4j
public class TestEnvGenAccFileServiceImpl implements TestEnvGenAccFileService {

    @Resource
    private BthPagyLocalInfoDao bthPagyLocalInfoDao;
    @Resource
    private BthWxFileDetDao bthWxFileDetDao;
    @Resource
    private BthAliFileDetDao bthAliFileDetDao;
    @Resource
    private BthUnionFileDetDao bthUnionFileDetDao;
    @Resource
    private BthPagyChkInfDao bthPagyChkInfDao;

    @SoaClient(name = "699.testEnvMoveFile")
    private ISoaClient testEnvMoveFile;

    @Resource
    private BthBatchAccountFileDao bthBatchAccountFileDao;

    /**
     * 差错表
     */
    @Resource
    private BthPagyChkErrInfoDao bthPagyChkErrInfoDao;

    /**
     * 本行文件表
     */
    @Resource
    private DebitTranInfoDao debitTranInfoDao;

    /**
     * 对账结果表
     */
    @Resource
    private BthChkRsltInfoDao bthChkRsltInfoDao;

    /**
     * 明细表
     */
    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;


    /**
     * 订单记账表
     */
    @Resource
    private KeepAcctInfoDao keepAcctInfoDao;




    /**
     * 清算记账表
     */
    @Resource
    private KeepAccInfoDao keepAccInfoDao;


    /**
     * 汇总表
     */
    @Resource
    private BthMerInAccDao bthMerInAccDao;


    @Value("${bthRstPath}")
    private String bthRstPath;


    private static ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Override
    public CommonResponse genWxFile(GetPagyTxnInfoRequest request) {

        String pagySysNo = "605 ";
        String pagyNo = "605000000000001";

        CommonResponse response = new CommonResponse();
        log.info("STEP 1  根据清算日期 [{}]与通道号[{}] 删除微信文件明细表数据" , request.getSettleDate() , pagyNo);
        bthWxFileDetDao.deleteBypagyNoAndDate(pagyNo , request.getSettleDate());

        log.info("STEP 2  根据清算日期 [{}]与通道系统编号[{}] 查询本地流水表交易" , request.getSettleDate() , pagySysNo);
        List<BthPagyLocalInfo> bthPagyLocalInfos=bthPagyLocalInfoDao.selectAliByDateAndPagyno(request.getSettleDate(),pagySysNo);
        if (IfspDataVerifyUtil.isEmptyList(bthPagyLocalInfos)){
            log.info("该清算日[{}]无[{}]的通道交易",request.getSettleDate() , pagySysNo);
            return response;
        }
        log.info("STEP 3  遍历本地流水表 , 生成相应的对账文件信息");
        List<BthWxFileDet> bthWxFileDetList = new ArrayList<BthWxFileDet>();
        for (BthPagyLocalInfo bthPagyLocalInfo : bthPagyLocalInfos) {

            BthWxFileDet bthWxFileDet = new BthWxFileDet();

            bthWxFileDet.setOrderTp(bthPagyLocalInfo.getTxnChlAction());
            bthWxFileDet.setOrderNo(bthPagyLocalInfo.getPagyPayTxnSsn());
            if ("10".equals(bthPagyLocalInfo.getTxnChlAction())){
                bthWxFileDet.setTradeSt("SUCCESS");
                bthWxFileDet.setOrderAmt(new BigDecimal(bthPagyLocalInfo.getTxnAmt()));
            }else {
                bthWxFileDet.setTradeSt("REFUND");
                bthWxFileDet.setRefundAmt(new BigDecimal(bthPagyLocalInfo.getTxnAmt()));
                if ("02".equals(bthPagyLocalInfo.getTradeSt())){
                    bthWxFileDet.setRefundSt("PROCESSING");
                }else {
                    bthWxFileDet.setRefundSt("SUCCESS");
                }
            }
            // 通道手续费默认为 千分之6
            BigDecimal multiply = new BigDecimal(bthPagyLocalInfo.getTxnAmt()).multiply(new BigDecimal(0.006));
            BigDecimal feeAmt = multiply.setScale(0, BigDecimal.ROUND_HALF_UP);
            bthWxFileDet.setFeeAmt(feeAmt);

            bthWxFileDet.setPagySysNo(pagySysNo);
            bthWxFileDet.setPagyNo(pagyNo);
            bthWxFileDet.setChkDataDt(request.getSettleDate());
            bthWxFileDet.setChkAcctSt("00");
            bthWxFileDet.setChkRst("");
            bthWxFileDet.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS());
            bthWxFileDetList.add(bthWxFileDet);

        }
        log.info("STEP 4  微信文件明细表入库");
        bthWxFileDetDao.insertSelectiveList(bthWxFileDetList);

        return response;
    }


    @Override
    public CommonResponse genAliFile(GetPagyTxnInfoRequest request) {

        String pagySysNo = "606 ";
        String pagyNo = "606000000000001";

        CommonResponse response = new CommonResponse();
        log.info("STEP 1  根据清算日期 [{}]与通道号[{}] 删除支付宝文件明细表数据" , request.getSettleDate() , pagyNo);
        bthAliFileDetDao.deleteBypagyNoAndDate(pagyNo , request.getSettleDate());

        log.info("STEP 2  根据清算日期 [{}]与通道系统编号[{}] 查询本地流水表交易" , request.getSettleDate() , pagySysNo);
        List<BthPagyLocalInfo> bthPagyLocalInfos=bthPagyLocalInfoDao.selectAliByDateAndPagyno(request.getSettleDate(),pagySysNo);
        if (IfspDataVerifyUtil.isEmptyList(bthPagyLocalInfos)){
            log.info("该清算日[{}]无[{}]的通道交易",request.getSettleDate() , pagySysNo);
            return response;
        }
        log.info("STEP 3  遍历本地流水表 , 生成相应的对账文件信息");
        List<BthAliFileDet> bthAliDetList = new ArrayList<BthAliFileDet>();
        for (BthPagyLocalInfo bthPagyLocalInfo : bthPagyLocalInfos) {

            BthAliFileDet bthAliFileDet = new BthAliFileDet();

            bthAliFileDet.setOrderTp(bthPagyLocalInfo.getTxnChlAction());
            bthAliFileDet.setOrderNo(bthPagyLocalInfo.getPagyPayTxnSsn());

            // 订单金额 (文件中存在正负 )
            bthAliFileDet.setOrderAmt(new BigDecimal(bthPagyLocalInfo.getTxnAmt()));

            // 通道手续费默认为 千分之6
            BigDecimal multiply = new BigDecimal(bthPagyLocalInfo.getTxnAmt()).multiply(new BigDecimal(0.006));
            BigDecimal feeAmt = multiply.setScale(0, BigDecimal.ROUND_HALF_UP);
            bthAliFileDet.setFeeAmt(feeAmt);

            bthAliFileDet.setPagySysNo(pagySysNo);
            bthAliFileDet.setPagyNo(pagyNo);
            bthAliFileDet.setChkDataDt(request.getSettleDate());
            bthAliFileDet.setChkAcctSt("00");
            bthAliFileDet.setChkRst("");
            bthAliFileDet.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS());
            bthAliDetList.add(bthAliFileDet);

        }
        log.info("STEP 4  支付宝文件明细表入库");
        bthAliFileDetDao.insertSelectiveList(bthAliDetList);

        return response;
    }

    @Override
    public CommonResponse genUnionQrcFile(GetPagyTxnInfoRequest request) {

        String pagySysNo = "607 ";
        String pagyNo = "607000000000001";

        CommonResponse response = new CommonResponse();
        log.info("STEP 1  根据清算日期 [{}]与通道号[{}] 删除银联二维码文件明细表数据" , request.getSettleDate() , pagyNo);
        bthUnionFileDetDao.deleteByPagySysNoAndDate(pagySysNo , request.getSettleDate() );

        log.info("STEP 2  根据清算日期 [{}]与通道系统编号[{}] 查询本地流水表交易" , request.getSettleDate() , pagySysNo);
        List<BthPagyLocalInfo> bthPagyLocalInfos=bthPagyLocalInfoDao.selectAliByDateAndPagyno(request.getSettleDate(),pagySysNo);
        if (IfspDataVerifyUtil.isEmptyList(bthPagyLocalInfos)){
            log.info("该清算日[{}]无[{}]的通道交易",request.getSettleDate() , pagySysNo);
            return response;
        }
        log.info("STEP 3  遍历本地流水表 , 生成相应的对账文件信息");
        List<BthUnionFileDet> bthUnionDetList = new ArrayList<BthUnionFileDet>();
        for (BthPagyLocalInfo bthPagyLocalInfo : bthPagyLocalInfos) {

            BthUnionFileDet record = new BthUnionFileDet();

            record.setTransCode(bthPagyLocalInfo.getTxnChlAction());

            // 清算主键
            String key = bthPagyLocalInfo.getTpamTxnSsn();

            record.setProxyInsCode(key.substring(0,8));
            record.setSendInsCode(key.substring(8,16));
            record.setTraceNum(key.substring(16,22));
            record.setTransDate(key.substring(22));

            // 交易金额
            record.setTransAmt(new BigDecimal(bthPagyLocalInfo.getTxnAmt()));
            // 交易类型
            record.setTransCode(bthPagyLocalInfo.getTxnChlAction());

            // 通道手续费默认为 千分之6
            BigDecimal multiply = new BigDecimal(bthPagyLocalInfo.getTxnAmt()).multiply(new BigDecimal(0.006));
            BigDecimal feeAmt = multiply.setScale(0, BigDecimal.ROUND_HALF_UP);
            record.setCustomerFee(String.valueOf(feeAmt));

            record.setPagySysNo(pagySysNo);
            record.setPagyNo(pagyNo);
            record.setChkDataDt(request.getSettleDate());
            record.setChkAcctSt("00");
            record.setChkRst("");
            record.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS());
            bthUnionDetList.add(record);

        }
        log.info("STEP 4  银联二维码文件明细表入库");
        bthUnionFileDetDao.insertSelectiveList(bthUnionDetList);

        return response;

    }




    @Override
    public CommonResponse genUnionAllChnlFile(GetPagyTxnInfoRequest request) {
        // 通道系统编号
        String pagySysNo = "608 ";
        // 通道号
        String pagyNo = "608000000000001";

        CommonResponse response = new CommonResponse();
        log.info("STEP 1  根据清算日期 [{}]与通道号[{}] 删除银联全渠道文件明细表数据" , request.getSettleDate() , pagyNo);
        bthUnionFileDetDao.deleteByPagySysNoAndDate(pagySysNo , request.getSettleDate() );

        log.info("STEP 2  根据清算日期 [{}]与通道系统编号[{}] 查询本地流水表交易" , request.getSettleDate() , pagySysNo);
        List<BthPagyLocalInfo> bthPagyLocalInfos=bthPagyLocalInfoDao.selectAliByDateAndPagyno(request.getSettleDate(),pagySysNo);
        if (IfspDataVerifyUtil.isEmptyList(bthPagyLocalInfos)){
            log.info("该清算日[{}]无[{}]的通道交易",request.getSettleDate() , pagySysNo);
            return response;
        }
        log.info("STEP 3  遍历本地流水表 , 生成相应的对账文件信息");
        List<BthUnionFileDet> bthUnionDetList = new ArrayList<BthUnionFileDet>();
        for (BthPagyLocalInfo bthPagyLocalInfo : bthPagyLocalInfos) {

            BthUnionFileDet record = new BthUnionFileDet();

            //  ------------------  造数 (实际是以OrderId 进行对账) --------------------
            String key = bthPagyLocalInfo.getPagyPayTxnSsn();
            record.setProxyInsCode(key.substring(0,8));
            record.setSendInsCode(key.substring(8,16));
            record.setTraceNum(key.substring(16,22));
            record.setTransDate(key.substring(22));
            //  -----------------------------------------------------------------------


            // 订单号
            record.setOrderId(bthPagyLocalInfo.getPagyPayTxnSsn());
            // 渠道交易类型
            if ("10".equals(bthPagyLocalInfo.getTxnChlAction())){
                record.setTransCode("000000");
            }else {
                record.setTransCode("200000");
            }

            record.setTransAmt(new BigDecimal(bthPagyLocalInfo.getTxnAmt()));

            // 通道手续费默认为 千分之6
            BigDecimal multiply = new BigDecimal(bthPagyLocalInfo.getTxnAmt()).multiply(new BigDecimal(0.006));
            BigDecimal feeAmt = multiply.setScale(0, BigDecimal.ROUND_HALF_UP);
            record.setCustomerFee(String.valueOf(feeAmt));

            record.setPagySysNo(pagySysNo);
            record.setPagyNo(pagyNo);
            record.setChkDataDt(request.getSettleDate());
            record.setChkAcctSt("00");
            record.setChkRst("");
            record.setLstUpdTm(IfspDateTime.getYYYYMMDDHHMMSS());
            bthUnionDetList.add(record);

        }

        log.info("STEP 4  银联全渠道文件明细表入库");
        bthUnionFileDetDao.insertSelectiveList(bthUnionDetList);

        return response;
    }

    @Override
    public CommonResponse moveFile(GetPagyTxnInfoRequest request) {
        CommonResponse response = new CommonResponse();
        // 通道号
        Map<String , Object>  mapParam = new HashMap<>();
        mapParam.put("pagyNo1","604aaaaaaaaaaaa");
        mapParam.put("pagyNo2","604bbbbbbbbbbbb");
        mapParam.put("settleDate",request.getSettleDate());
        mapParam.put("getChkSt","00");

        // 根据日期和通道号查询 文件下载表
        List<BthPagyChkInf> list = bthPagyChkInfDao.selectByPagyNoAndDate(mapParam);
        for (BthPagyChkInf bthPagyChkInf : list) {
            // 先看本地有没有 ,没有就去远程下载
            File f = new File(bthPagyChkInf.getChkFilePath());
            if (!f.exists()) {
                String[] split = bthPagyChkInf.getChkFilePath().split("/");
                String fileNm = split[split.length-1];
                log.info("文件[{}] 不存在 , 将去远程下载... ",fileNm);

                SftpUtil sftpUtil = new SftpUtil("10.0.192.78", "dfsrs", "dfsrs", 22);
                ChannelSftp sftp = sftpUtil.connectSFTP();


                // 判断远程目录文件是否存在  远程路径 : /data/home/dfsrs/print/RPS
                if (sftpUtil.isFileExist(sftp, "/data/home/dfsrs/print/RPS/"+fileNm+".ok")) {
                    log.info("远程目录明细文件存在");
                    log.info("---------------------SFTP获取本行文件------------------");
                    try {
                        log.info("远程目录:/data/home/dfsrs/print/RPS/ ");
                        sftp.cd("/data/home/dfsrs/print/RPS/");
                        log.info("本地目录:/data/exchange/ebap/batch/send/");
                        sftp.lcd("/data/exchange/ebap/batch/send/");
                        sftp.get(fileNm,fileNm);
                        sftp.get(fileNm+".ok",fileNm+".ok");
                    } catch (Exception e) {
                        throw new IfspBizException("9999",e.getMessage());
                    }
                    log.info("---------------------SFTP断开连接------------------");
                    sftpUtil.disconnected(sftp);
                }else {
                    log.info("远程目录明细文件不存在 ,请稍后重试!!!");
                    sftpUtil.disconnected(sftp);
                    throw new IfspBizException("2222","远程目录明细文件不存在 ,请稍后重试!!!");
                }

                // 更新文件表状态
                bthPagyChkInf.setGetChkSt("02");
                bthPagyChkInfDao.updateByPrimaryKeySelective(bthPagyChkInf);
            }
        }


        return response;
    }

    @Override
    public CommonResponse testEnvMoveFilePerTm(GetPagyTxnInfoRequest request) throws InterruptedException {
        CommonResponse response = new CommonResponse();
        SoaParams params = new SoaParams();
        params.put("settleDate",request.getSettleDate());
        SoaResults soaResults ;

        int i = 5;
        do {
            i --;
            soaResults =   testEnvMoveFile.invoke(params);
            if (!"0000".equals(soaResults.getRespCode())) {
                log.info("剩余调接口移动文件次数[{}]",i);
                log.info("sleep 2min....");
                if (i > 0){
                    Thread.sleep(120000);
                }

            }else {
                log.info("文件移动成功!!!");
                break;
            }

        } while (i > 0);




        return response;
    }

    @Override
    public CommonResponse testEnvMoveInaccFile(GetPagyTxnInfoRequest request) throws ExecutionException, InterruptedException {
        CommonResponse response = new CommonResponse();
        // 文件路径
        String localPath = "/data/exchange/ebap/batch/recv/" ;
        String remotePath = "/data/eall/elecbank/out.succ/" ;

        Map<String , Object> map = new HashMap<>(2);
        map.put("dealStatus",Constans.FILE_STATUS_02);
        map.put("fileType",Constans.FILE_IN_ACC);
        // 查询出需要移动的文件名
        List<BthBatchAccountFile> list = bthBatchAccountFileDao.selectList("accF_selectByState", map);
        List<String> fileNms = new ArrayList<>();
        // 整理出本地不存在的
        for (BthBatchAccountFile bthBatchAccountFile : list) {
            File f = new File(localPath+bthBatchAccountFile.getAccFileName()+".dow.ok");
            if (!f.exists()){
                fileNms.add(bthBatchAccountFile.getAccFileName());
                log.info("localPath : "+localPath+bthBatchAccountFile.getAccFileName()+".dow.ok 不存在 ,待下载...");
            }
        }

        // 尝试四次 去远程目录下载文件  , 全部下载成功提前结束下载任务
        int i = 4 ;
        SftpUtil sftpUtil;
        sftpUtil = new SftpUtil("10.16.10.118", "ftprs", "ftprs", 22);
        do {
            Iterator<String> iterator = fileNms.iterator();
            log.info("===========================>>  待下载文件个数为 : "+ fileNms.size());

            List<Future<Boolean>> futurelist = new ArrayList();
            List<String> fileDeal = new ArrayList<>();
            while (iterator.hasNext()) {
                String fileNm = iterator.next();
                Callable<Boolean> callable = new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        // 建立连接
                        ChannelSftp sftp = sftpUtil.connectSFTP();
                        if (sftpUtil.isFileExist(sftp, remotePath+fileNm+".dow.ok")) {
                            try {
                                sftp.cd(remotePath);
                                sftp.lcd(localPath);
                                sftp.get(fileNm+ ".dow",fileNm+ ".dow");
                                sftp.get(fileNm+ ".dow.ok",fileNm+ ".dow.ok");
                                log.info("============================>>文件[{}]获取到本地成功 , 从待下载集合中移除",fileNm);
                                sftp.disconnect();
                                fileDeal.add(fileNm);
                                return true;
                            } catch (SftpException e) {
                                log.error("============================>> 文件下载异常!!!");
                                sftp.disconnect();
                                throw new IfspBizException("9999",e.getMessage());
                            }
                        }else {
                            log.info("=========================>> remotePath : {}"+fileNm+".dow.ok 不存在", remotePath);
                            return false;
                        }
                    }
                };

                Future<Boolean> submit = executorService.submit(callable);
                futurelist.add(submit);
            }

            // 统一阻塞
            for (Future<Boolean> booleanFuture : futurelist) {
                try {
                    booleanFuture.get(5000L, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    log.error("任务被中断! {}",e);
                    booleanFuture.cancel(true);
                } catch (ExecutionException e) {
                    log.error("任务内部抛出异常未受检异常:  {}",e);
                    booleanFuture.cancel(true);
                } catch (TimeoutException e) {
                    log.error("任务超时! {}" ,e);
                    booleanFuture.cancel(true);
                }
            }


            fileNms.removeAll(fileDeal);
            i -- ;

            if ( i == 0 &&  fileNms.size() > 0){
                throw new IfspBizException("9999" ,"反馈文件下载失败!!!");
            }

            if (fileNms.size() > 0){
                log.info("============================>> 剩余尝试下载次数[{}]" ,i);
                log.info("============================>> 等待核心传输文件 sleep 2 min...");
                try {
                    Thread.sleep(120000);
                } catch (InterruptedException e) {
                    log.error("============================>> 睡眠异常...");
                }
            }


        } while ( fileNms.size() > 0 && i > 0 );

        return response;
    }

    @Override
    public CommonResponse prdEnvBthRstFile(BatchRequest request) {

        CommonResponse response = new CommonResponse();
        // 传的日期
        String runDate = request.getSettleDate() ;

        if (IfspDataVerifyUtil.isBlank(request.getSettleDate())){
            runDate = IfspDateTime.getYYYYMMDD();

        }

        // 失败记录
        List<InAccMerVo> failList = new ArrayList<>();

        Map<String , Object> param = new HashMap<>();
        param.put("inAcctStat",Constans.IN_ACC_STAT_FAIL);
        param.put("inAcctType",Constans.IN_ACCT_TYPE_MCHT);

        // 查询

        // 以商户号 ,日期排序
        List<BthMerInAcc> bthMerInAccs = bthMerInAccDao.selectList("prdCountFails", param);

        for (BthMerInAcc bthMerInAcc : bthMerInAccs) {
            List<InAccMerVo> records = bthMerInAccDtlDao.prdCountFailRecord(bthMerInAcc.getBatchNo(), bthMerInAcc.getChlMerId() , bthMerInAcc.getStatMark());
            failList.addAll(records);
        }

        // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=     统计线上  start  =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
        // 总笔数
        int totalCountOnLine = 0;

        // 总金额
        BigDecimal totalAmtOnLine = BigDecimal.ZERO;

        //  各个通道入账成功统计(笔数 , 金额)
        List<TxnCountVo> countList = bthMerInAccDtlDao.txnCountAmtSumGroupByPagyNoOnline(runDate);
        for (TxnCountVo txnCountVo : countList) {
            // 资金渠道映射
            txnCountVo.setFundChannel("线上"+getFundChannelNm(txnCountVo.getFundChannel()));
            totalCountOnLine += Integer.parseInt(txnCountVo.getTxnCount()) ;
            totalAmtOnLine = totalAmtOnLine.add(new BigDecimal(txnCountVo.getAmt()));
        }

        // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=     统计线上   end =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=


        // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=     统计线下  start  =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
        // 总笔数
        int totalCountOffLine = 0;

        // 总金额
        BigDecimal totalAmtOffLine = BigDecimal.ZERO;

        //  各个通道入账成功统计(笔数 , 金额)
        List<TxnCountVo> countList2 = bthMerInAccDtlDao.txnCountAmtSumGroupByPagyNo(runDate);
        for (TxnCountVo txnCountVo : countList2) {
            // 资金渠道映射
            txnCountVo.setFundChannel("线下"+getFundChannelNm(txnCountVo.getFundChannel()));
            totalCountOffLine += Integer.parseInt(txnCountVo.getTxnCount()) ;
            totalAmtOffLine = totalAmtOffLine.add(new BigDecimal(txnCountVo.getAmt()));
        }

        // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=     统计线下   end =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=


        // 总计笔数
        int totalCount =  totalCountOffLine + totalCountOnLine;
        // 总计金额
        BigDecimal totalAmt = totalAmtOffLine.add(totalAmtOnLine);


        // 今日入账成功的商户数量
        int merNum = bthMerInAccDtlDao.selectForMerNum(runDate);

        // 最后入账时间
        String inAccEndTm =   bthMerInAccDtlDao.selectInAccEndTm(runDate);


        List<TxnCountVo> countListTotal = new ArrayList<>();
        // 线上分渠道统计
        countListTotal.addAll(countList);
        // 线上小计
        TxnCountVo txnCountVo1 = new TxnCountVo("小计: ",String.valueOf(totalCountOnLine) , String.valueOf(totalAmtOnLine));
        countListTotal.add(txnCountVo1);
        // 线下分渠道统计
        countListTotal.addAll(countList2);
        // 线下小计
        TxnCountVo txnCountVo2 = new TxnCountVo("小计: ",String.valueOf(totalCountOffLine) , String.valueOf(totalAmtOffLine));
        countListTotal.add(txnCountVo2);
        // 总计
        TxnCountVo txnCountVo3 = new TxnCountVo("总计: ",String.valueOf(totalCount) , String.valueOf(totalAmt));
        countListTotal.add(txnCountVo3);

        try {

            File filePath = new File(bthRstPath);
            // 如果文件夹不存在则创建
            if (!filePath.exists() && !filePath.isDirectory()) {
                log.info("目录[{}]不存在 ,将创建...",bthRstPath);
                filePath.mkdirs();
            } else {
                log.info("目录[{}]存在",bthRstPath);
            }
            ExcelUtils.createExcel(failList ,countListTotal,merNum,inAccEndTm,bthRstPath,""+runDate+"商户入账.xlsx" );
        } catch (IllegalAccessException e) {
            log.error("生成商户入账记录.xlsx失败!!!");
            e.printStackTrace();
            response.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            response.setRespMsg(IfspRespCodeEnum.RESP_ERROR.getDesc());
        }

        log.info("跑批日[{}]入账成功情况: 总共有[{}]个商家 , 总交易笔数为[{}] , 总入账金额为[{}] , 入账结束时间为[{}]",runDate,merNum, totalCount,totalAmt,inAccEndTm );

        return response;
    }

    @Override
    public CommonResponse prdSupplementAcc(GetPagyTxnInfoRequest request) {

        CommonResponse response = new CommonResponse();

        SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");


        List<KeepAcctInfo> keepAcctInfos = keepAcctInfoDao.querryAll();

        for (KeepAcctInfo keepAcctInfo : keepAcctInfos) {
            KeepAccInfo record = new KeepAccInfo();
            record.setCoreSsn(ConstantUtil.getRandomNum(20));
            record.setOrderSsn(keepAcctInfo.getOrderSsn());
            record.setKeepAccTime(form.format(keepAcctInfo.getOrderTm()));
            record.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);

            record.setKeepAccType(Constans.KEEP_ACC_TYPE_OTHER);

            record.setInAccNo(keepAcctInfo.getCredAcctNo());
            record.setInAccNoName(keepAcctInfo.getCredAcctName());
            record.setOutAccNo(keepAcctInfo.getDebtAcctNo());
            record.setOutAccNoName(keepAcctInfo.getDebtAcctName());
            record.setTransAmt(Long.valueOf(keepAcctInfo.getTxnAmt()));
            record.setChkSt(Constans.CHK_STATE_01);
            record.setChkRst(Constans.CHK_RST_00);
            record.setTxnDesc(keepAcctInfo.getTxnDesc());
            record.setTxnCcyType(Constans.CCY_TYPE);
            record.setReserved1("DAYNIGHT");
            record.setRerunFlag(ReRunFlagEnum.RE_RUN_FLAG_TRUE.getCode());
            record.setSubOrderSsn(keepAcctInfo.getSubOrderSsn());
            record.setRealTmFlag(RealTmFlagEnum.REAL_TM_FALSE.getCode());
            record.setUniqueSsn(keepAcctInfo.getUniqueSsn());
            keepAccInfoDao.insert(record);
        }






        return response;
    }


    /**
     * 渠道映射
     * @param fundChannel
     * @return
     */
    public String getFundChannelNm(String fundChannel){
        switch (fundChannel){
            case "01":
                return "微信";
            case "02":
                return "支付宝";
            case "26":
                return "惠支付个人版";
            case "05":
                return "蜀信卡";
            case "06":
                return "授信支付";
            case "07":
                return "蜀信E";
            case "08":
                return "银联";
            case "10":
                return "积分";
            default:
                return "未知渠道";
        }
    }


}
