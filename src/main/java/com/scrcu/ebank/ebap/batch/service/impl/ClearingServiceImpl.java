package com.scrcu.ebank.ebap.batch.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.KeepAccQryRequest;
import com.scrcu.ebank.ebap.batch.bean.request.KeepAccRevRequest;
import com.scrcu.ebank.ebap.batch.bean.request.NewDayTimeKeepAcctRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.msg.IBankMsg;
import com.scrcu.ebank.ebap.batch.common.utils.KeepAcctUtil;
import com.scrcu.ebank.ebap.batch.common.utils.SftpUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.ClearingService;
import com.scrcu.ebank.ebap.batch.soaclient.ClearingSoaService;
import com.scrcu.ebank.ebap.batch.soaclient.SoaClientService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
//@Transactional(propagation = Propagation.REQUIRED)
public class ClearingServiceImpl implements ClearingService
{

    @Resource
    private ClearingSoaService clearingSoaService;

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    @Resource
    private BthMerInAccDao bthMerInAccDao;

    @Resource
    private BthSetCapitalDetailDao bthSetCapitalDetailDao;

    @Resource
    private BthBatchAccountFileDao bthBatchAccountFileDao;

    @Resource
    private SoaClientService batchSoaClientService;


    /**
     * 批量系统参数表
     */
    @Resource
    private BthSysParamInfoDao bthSysParamInfoDao;

    /**
     * 服务商信息表
     */
    @Resource
    private ParternInfoDao parternInfoDao;

    // ~~~~~~~~~~~~~~~~~本行~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * 上传的文件目录
     */
    @Value("${inacctFilePath}")
    private String inacctFilePath;

    /**
     * 核心反馈文件目录
     */
    @Value("${accountRecvDir}")
    private String accountRecvDir;

    /**
     * 将处理后的文件备份到这个目录
     */
    @Value("${accountRecvDirBackUp}")
    private String accountRecvDirBackUp;

    // ~~~~~~~~~~~~~~~他行~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * 上传的文件目录
     */
    @Value("${otherInacctFilePath}")
    private String otherInacctFilePath;

    /**
     * 统一支付反馈文件目录
     */
    @Value("${otherAccountRecvDir}")
    private String otherAccountRecvDir;

    /**
     * 将处理后的文件备份到这个目录
     */
    @Value("${otherAccountRecvDirBackUp}")
    private String otherAccountRecvDirBackUp;

    /**
     * 将处理后的文件备份到这个目录
     */
    @Value("${distDir}")
    private String distDir;

    /**
     * 处理线程数量
     */
    @Value("${upd.threadCount}")
    private Integer updThreadCount;

    /**
     * 线程处理数量
     */
    @Value("${upd.batchInsertCount}")
    private Integer updBatchInsertCount;


    /**
     * 线程池
     */
//	ExecutorService executor;
    /**
     * 更新线程池
     */
    ExecutorService updExecutor;

    @Autowired
    private BthEnterAccountResultDao bthEnterAccountResultDao;

    @Resource
    private PayOrderInfoDao payOrderInfoDao;


    /**
     * 调用CH1730接口发起对账请求并获取核心对账文件文件名
     **/
    @Override
    public CommonResponse callCH1730()
    {

        CommonResponse commonResponse = new CommonResponse();

        // 去数据库查询状态为未处理的文件名字
        List<BthBatchAccountFile> bthBatchAccountFilelist = bthBatchAccountFileDao
                .queryByDealtatuss(Constans.FILE_STATUS_00, Constans.FILE_STATUS_01, Constans.FILE_STATUS_03);

        if (IfspDataVerifyUtil.isEmptyList(bthBatchAccountFilelist))
        {
            log.info("当前没有入账结果文件");
            commonResponse.setRespCode(RespConstans.RESP_NO_RESULTFILES.getCode());
            commonResponse.setRespMsg(RespConstans.RESP_NO_RESULTFILES.getDesc());
            return commonResponse;
        }

        // 检查入账文件是否已经传输完毕
        checkFileExist(bthBatchAccountFilelist);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 结果文件上传的目录
        String inPath = accountRecvDirBackUp;

        String otherInPath = otherAccountRecvDirBackUp;

        File inacctFile = new File(inPath);

        // 他行文件返回目录
        File otherInFile = new File(otherInPath);

        SoaResults result;
        SoaParams params = new SoaParams();

        List<BthBatchAccountFile> bthBatchAccountFileOkList = new ArrayList();

        log.info("文件上传的目录:" + inPath);
        log.info("他行文件上传的目录:" + otherInFile);
        boolean isDirectory = inacctFile.isDirectory();
        boolean otherIsDirectory = otherInFile.isDirectory();

        if (otherIsDirectory)
        {
            // 遍历他行上传文件夹下的所有文件
            File[] listFiles = otherInFile.listFiles();
            for (int i = 0; i < bthBatchAccountFilelist.size(); i++)
            {
                for (File file : listFiles)
                {
                    BthBatchAccountFile bthBatchAccountFile = bthBatchAccountFilelist.get(i);
                    if (!file.getName().contains(bthBatchAccountFile.getAccFileName() + ".ok"))
                    {
                        continue;
                    }
                    // 他行文件以 .txt 结尾
                    if (bthBatchAccountFile.getAccFileName().endsWith(".txt"))
                    {
                        // 处理中
                        bthBatchAccountFile.setDealStatus(Constans.FILE_STATUS_01);

                        bthBatchAccountFile.setDealFileClass(this.getClass().getName());
                        bthBatchAccountFileDao.updateByPrimaryKeySelective(bthBatchAccountFile);

                        // 调用统一支付接口
                        params = IBankMsg.unifyPay(params, bthBatchAccountFile);
                        log.info("他行unifyPay接口参数:" + params);
                        result = clearingSoaService.unifyPay(params);
                        log.info("调用他行unifyPay口参数返回的报文>>>>>>>>>>>>" + result);
                        if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode")))
                        {
                            throw new IfspBizException(RespConstans.RESP_FAIL.getCode(),
                                                       RespConstans.RESP_FAIL.getDesc());
                        }

                        if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
                                                       RespConstans.RESP_SUCCESS.getCode()))
                        {
                            log.info("=====================调用他行unifyPay失败================================");
                            // 03-核心处理失败
                            bthBatchAccountFile.setDealStatus(Constans.FILE_STATUS_03);

                            bthBatchAccountFileDao.updateByPrimaryKeySelective(bthBatchAccountFile);
                            continue;
                        }
                        else
                        {
                            log.info("=====================调用他行unifyPay成功================================");
                            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);
                            // 02-核心处理成功
                            bthBatchAccountFile.setDealStatus(Constans.FILE_STATUS_02);

                            bthBatchAccountFileDao.updateByPrimaryKeySelective(bthBatchAccountFile);
                        }
                    }
                }
            }
        }

        if (isDirectory)
        {
            // 遍历上传文件夹下的所有文件
            File[] listFiles = inacctFile.listFiles();
            for (int i = 0; i < bthBatchAccountFilelist.size(); i++)
            {
                for (File file : listFiles)
                {
                    BthBatchAccountFile bthBatchAccountFile = bthBatchAccountFilelist.get(i);
                    if (!file.getName().contains(bthBatchAccountFile.getAccFileName() + ".ok"))
                    {
                        continue;
                    }
                    // 他行文件以 .txt 结尾
                    if (bthBatchAccountFile.getAccFileName().endsWith(".txt"))
                    {
                        continue;
                    }
                    else
                    {
                        log.info("本行文件:" + bthBatchAccountFile.getAccFileName());
                        // 处理中
                        bthBatchAccountFile.setDealStatus(Constans.FILE_STATUS_01);
                        bthBatchAccountFile.setDealFileClass(this.getClass().getName());
                        bthBatchAccountFileDao.updateByPrimaryKeySelective(bthBatchAccountFile);
                        // 调用CH1730
                        params = IBankMsg.PagyFeeClearing(params, bthBatchAccountFilelist.get(i).getAccFileName());
                        log.info("CH1730接口参数:" + params);
                        result = clearingSoaService.clearingSoa(params);
                        log.info("调用CH1730接口参数返回的报文>>>>>>>>>>>>" + result);
                        if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode")))
                        {
                            throw new IfspBizException(RespConstans.RESP_FAIL.getCode(),
                                                       RespConstans.RESP_FAIL.getDesc());
                        }

                        if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
                                                       RespConstans.RESP_SUCCESS.getCode()))
                        {
                            log.info("=====================调用CH1730失败================================");
                            // 03-核心处理失败
                            bthBatchAccountFile.setDealStatus(Constans.FILE_STATUS_03);
                            bthBatchAccountFileDao.updateByPrimaryKeySelective(bthBatchAccountFile);
                            continue;
                        }
                        else
                        {
                            log.info("=====================调用CH1730成功================================");
                            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);
                            bthBatchAccountFileOkList.add(bthBatchAccountFile);

                            bthBatchAccountFile.setBkFileName(
                                    accountRecvDir + bthBatchAccountFile.getAccFileName() + ".dow.ok");
                            bthBatchAccountFile.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                            // 02-核心处理成功
                            bthBatchAccountFile.setDealStatus(Constans.FILE_STATUS_02);
                            bthBatchAccountFileDao.updateByPrimaryKeySelective(bthBatchAccountFile);
                        }
                    }
                }
            }
        }

        commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        return commonResponse;
    }

    /**
     * 更新本行或者他行状态
     *
     * @return
     */
    @Override
    public CommonResponse updateStat()
    {
        CommonResponse commonResponse = new CommonResponse();
//		accountRecvDir = "E:\\shrm\\批量优化\\";
        // 去数据库查询状态为未处理的文件名字
        List<BthBatchAccountFile> bthBatchAccountFilelist = bthBatchAccountFileDao
                .queryByDealtatus(Constans.FILE_STATUS_02);

        if (IfspDataVerifyUtil.isEmptyList(bthBatchAccountFilelist))
        {
            log.info("当前没有入账结果文件");
            commonResponse.setRespCode(RespConstans.RESP_NO_RESULTFILES.getCode());
            commonResponse.setRespMsg(RespConstans.RESP_NO_RESULTFILES.getDesc());
            return commonResponse;
        }


        // ---------------------------------------------------轮循查询本行文件是否存在-------------------------------------------------------
        // 每分钟查询文件是否已经存在 (他行文件第二天到  ,此处不轮循)
        // 尝试15次 , 每次间隔 2分钟
        int chkNum = 15;
        poll:
        do
        {

            for (int i = 0; i < bthBatchAccountFilelist.size(); i++)
            {
                BthBatchAccountFile bthBatchAccountFile = bthBatchAccountFilelist.get(i);
                // 仅检查本行文件
                if (Constans.FILE_IN_ACC.equals(bthBatchAccountFile.getFileType()))
                {
                    File iBank = new File(accountRecvDir + bthBatchAccountFile.getAccFileName() + ".dow");
                    File iBankOK = new File(accountRecvDir + bthBatchAccountFile.getAccFileName() + ".dow.ok");

                    if (!(iBank.exists() && iBankOK.exists()))
                    {
                        log.info("入账结果文件[{}]不存在", bthBatchAccountFile.getAccFileName() + ".dow.ok");
                        break;
                    }
                }
                if (i == bthBatchAccountFilelist.size() - 1)
                {
                    log.info("本行文件已传输完毕,等待更新...");
                    break poll;
                }

            }

            chkNum--;

            try
            {
                log.info("等待文件传输  ,时间为 2min ,剩余次数 [{}]", chkNum);
                Thread.sleep(120000);
            }
            catch (InterruptedException e)
            {
                log.error("睡眠异常", e);
            }

        } while (chkNum > 0);
        // -----------------------------------------------------------------------------------------------------------------------------------


        for (int i = 0; i < bthBatchAccountFilelist.size(); i++)
        {
            BthBatchAccountFile bthBatchAccountFile = bthBatchAccountFilelist.get(i);

            // 他行
            if (bthBatchAccountFile.getAccFileName().endsWith(".txt"))
            {

                String multiBankFileName = "R" + bthBatchAccountFile.getAccFileName().substring(1);
                File multiBank = new File(otherAccountRecvDir + multiBankFileName);
                File multiBankOK = new File(otherAccountRecvDir + multiBankFileName + ".ok");

                if (!multiBank.exists())
                {
                    log.info("入账结果文件" + otherAccountRecvDir + multiBankFileName + "不存在");
//					commonResponse.setRespCode(RespConstans.RESP_ACCOUNT_NORESULTFILE.getCode());
//					commonResponse.setRespMsg(RespConstans.RESP_ACCOUNT_NORESULTFILE.getCode());
                    continue;
                }
                if (!multiBankOK.exists())
                {
                    log.info("入账结果文件" + otherAccountRecvDir + multiBankFileName + ".ok" + "不存在");
                    continue;
                }

                log.info("他行文件全路径>>>>>>>>>>>>>>>>>>" + otherAccountRecvDir + multiBankFileName);
                // 2.1到共享目录读取反馈标记文件，如果存在读取并解析结果文件(按照“|#|”进行分割)，根据结果更新商户入账信息表，商户入账明细表
                List<SubMerCapMultibank> parseMultiBank = parseMultiBank(multiBank, commonResponse);
                // 2.核心入账结果文件下载解析及本地状态更新
                CommonResponse response = updateMultiBankLocalStat(commonResponse, parseMultiBank);
                log.info(">>>>>>>>>>>>>>>>>>>返回结果响应码" + response.getRespCode() + "返回信息:" + response.getRespMsg());
                if (response != null && "0000".equals(response.getRespCode()))
                {
                    bthBatchAccountFile.setDealStatus(Constans.FILE_STATUS_04);// 04-处理完成
                    bthBatchAccountFileDao.updateByPrimaryKeySelective(bthBatchAccountFile);
                    try
                    {
                        FileUtil.copyFile(multiBank, new File(otherAccountRecvDirBackUp));
                        FileUtil.copyFile(multiBankOK, new File(otherAccountRecvDirBackUp));

                        if (multiBank.isFile())
                        {
                            if (multiBank.exists())
                            {
                                multiBank.delete();
                                multiBankOK.delete();
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        log.error("结果文件备份失败 文件名:" + multiBank.getName());
                        e.printStackTrace();
                    }
                }
            }
            // 本行
            else
            {
                File localFile = new File(accountRecvDir + bthBatchAccountFile.getAccFileName() + ".dow");
                File localFileOK = new File(accountRecvDir + bthBatchAccountFile.getAccFileName() + ".dow.ok");
                if (!localFile.exists())
                {
                    log.info("入账结果文件" + accountRecvDir + bthBatchAccountFile.getAccFileName() + ".dow" + "不存在");
                    continue;
                }
                if (!localFileOK.exists())
                {
                    log.info("入账结果文件" + accountRecvDir + bthBatchAccountFile.getAccFileName() + ".dow.ok" + "不存在");
                    continue;
                }

                log.info("文件全路径>>>>>>>>>>>>>>>>>>" + accountRecvDir + bthBatchAccountFile.getAccFileName() + ".dow.ok");
                // 2.1到共享目录读取反馈标记文件，如果存在读取并解析结果文件(按照“|#|”进行分割)，根据结果更新商户入账信息表，商户入账明细表

                CommonResponse response = null;
                try
                {
                    updInitPool();
                    response = parseCapitalSummary(localFile, commonResponse);
                }
                finally
                {
                    destoryUpdPool();
                }
                // 2.核心入账结果文件下载解析及本地状态更新
//				CommonResponse response = updateLocalStatNew(commonResponse, parseCapitalSummary);
                log.info(">>>>>>>>>>>>>>>>>>>返回结果响应码" + response.getRespCode() + "返回信息:" + response.getRespMsg());
                if (response != null && "0000".equals(response.getRespCode()))
                {
//                    04-处理完成
                    bthBatchAccountFile.setDealStatus(Constans.FILE_STATUS_04);
                    bthBatchAccountFileDao.updateByPrimaryKeySelective(bthBatchAccountFile);
                    try
                    {
                        FileUtil.copyFile(localFile, new File(accountRecvDirBackUp));
                        FileUtil.copyFile(localFileOK, new File(accountRecvDirBackUp));
                        if (localFile.isFile())
                        {
                            if (localFile.exists())
                            {
                                localFile.delete();
                                if (localFileOK.exists())
                                {
                                    localFileOK.delete();
                                }
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        log.error("结果文件备份失败 文件名:" + localFile.getName());
                        e.printStackTrace();
                    }
                }
            }

        }

        log.info("------------------查询处理服务商支出入账信息开始---------------------------");
        long start = System.currentTimeMillis();
        List<BthMerInAcc> expendList = bthMerInAccDao.queryServeMchtInAcct(Constans.IN_ACCT_TYPE_FEE,
                Constans.IN_ACC_STAT_FAIL,"17");
        List<BthMerInAcc> expendListSucc = new ArrayList<>();//通过记账成功的集合
        //申明批量同步记账list
        List<NewDayTimeKeepAcctVo> keepAcctList = new ArrayList<>();
        List<BthSetCapitalDetail> bthSetCapitalDetailResultList = new ArrayList<>();
        if(IfspDataVerifyUtil.isNotEmptyList(expendList)){
            //1、分离出list中的服务商编号放入缓存中
            List<String> parternCodeList = expendList.stream().map(BthMerInAcc::getParternCode).collect(
                    Collectors.toList());
            List<String> orderSsnList = expendList.stream().map(BthMerInAcc::getTxnSsn).collect(
                    Collectors.toList());//查询入账流水号list
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("orderSsnList", orderSsnList);
            List<BthSetCapitalDetail> bthSetCapitalDetailList_3 = bthSetCapitalDetailDao.selectList("queryByCapitalDetailByTxnSsnList_3",
                    parameter);//查询服务商手续费支出清分明细
            log.info("批量查询queryByCapitalDetailByTxnSsnList_3，结果数量【{}】，耗时【{}】", bthSetCapitalDetailList_3.size(),
                    System.currentTimeMillis() - start);
            /**
             * Reserved3字段中存放txn_ssn连接串
             */
            Map<String, List<BthSetCapitalDetail>> bthSetCapitalDetailMap_3 = bthSetCapitalDetailList_3.stream().collect(
                    Collectors.groupingBy(BthSetCapitalDetail::getReserved3));
            //2、批量查询出服务商信息
            List<ParternInfo> parternInfoList = parternInfoDao.selectParternList(parternCodeList);
            SoaResults soaResults = null;
            SoaResults soaQryResults = null;
            SoaResults soaResultsRecovery = null;
            if(IfspDataVerifyUtil.isNotEmptyList(parternInfoList)) {
                String orderTm = IfspDateTime.getYYYYMMDDHHMMSS();
                Map<String, ParternInfo> parternInfoMap = parternInfoList.stream().collect(
                        Collectors.toMap(ParternInfo::getParternCode, a -> a, (k1, k2) -> k1));
                for (BthMerInAcc bthMerInAcc : expendList) {
                    //对入账失败的服务商手续费支出收入进行处理
                    //1、更换汇总表中出资账户，将出资账户从结算账户改为保证金账户
                    bthMerInAcc.setOutAcctNo(parternInfoMap.get(bthMerInAcc.getParternCode()).getDepAcctNo());//出资账户号-->保证金账户
                    bthMerInAcc.setOutAcctNoOrg(parternInfoMap.get(bthMerInAcc.getParternCode()).getDepOrg());//出资账户机构号-->保证金账户开户机构
                    bthMerInAcc.setOutAcctName("");
                    NewDayTimeKeepAcctRequest request = buildKeepAccInfo(bthMerInAcc,orderTm);
                    //2、开始同步记账，根据记账结果，对应更新
                    log.info("服务商手续费支出入账失败处理:[ 重记账请求报文:[" + IfspFastJsonUtil.tojson(request) + "] ]");
                    soaResults = batchSoaClientService.invoke(new SoaParams(IfspFastJsonUtil.objectTomap(request)),
                            "009.bthSynKeepAccount","","");
                    log.info("服务商手续费支出入账失败处理:[ 重记账结果:[txnSsn:[" + bthMerInAcc.getTxnSsn() + "],respCode:[" + soaResults.getRespCode() + "] ] ]");
                    //调用失败打印日志,不更新入账结果
                    if(IfspDataVerifyUtil.equals(IfspRespCodeEnum.RESP_ERROR.getCode(), soaResults.getRespCode())){
                        log.info("服务商手续费支出入账失败处理: 入账流水号[{}]调用记账失败。。。", bthMerInAcc.getTxnSsn());
                    }else {
                        if (KeepAcctUtil.isKeepSuc(soaResults)) {//如果记账成功，更新入账汇总流水为成功
                            bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_SUCC);// 1.代表入账成功
                            bthMerInAcc.setStatMark("通过批量记账成功");// 成功原因不用插入
                            // bthMerInAcc.setHandleState("2");
                            bthMerInAcc.setHandleMark("处理成功");
                            bthMerInAcc.setHandleState(Constans.HANDLE_STATE_SUCC);// 处理成功2
                            bthMerInAcc.setInAcctTime(IfspDateTime.getYYYYMMDDHHMMSS());// 入账时间
                            bthMerInAcc.setUpdateTime(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(),
                                    IfspDateTime.YYYYMMDDHHMMSS));
                            bthMerInAcc.setCoreRespCode("0000");
                            bthMerInAcc.setCoreRespMsg("");
                            expendListSucc.add(bthMerInAcc);
                        }else if(KeepAcctUtil.isKeepErr(soaResults)){//如果记账失败，不做任何处理
                            log.info("服务商手续费支出入账失败处理: 入账流水号[{}]记账失败。。。", bthMerInAcc.getTxnSsn());
                        }else{//如果记账超时，先查询记账是否成功，再发起冲正
                            KeepAccQryRequest qryRequest = buildQryKeepAccInfo(bthMerInAcc,orderTm);
                            log.info("服务商手续费支出入账失败处理:[ 记账查询请求报文:[" + IfspFastJsonUtil.tojson(qryRequest) + "] ]");
                            soaQryResults = batchSoaClientService.invoke(new SoaParams(IfspFastJsonUtil.objectTomap(qryRequest)),
                                    "009.bthKeepAccountRstQry","","");
                            log.info("服务商手续费支出入账失败处理:[ 记账查询结果:[txnSsn:[" + bthMerInAcc.getTxnSsn() + "],respCode:[" + soaQryResults.getRespCode() + "] ] ]");
                            //查询到记账成功更新入账汇总流水为成功
                            if(KeepAcctUtil.isKeepSuc(soaQryResults)){
                                //1、更换汇总表中出资账户，将出资账户从结算账户改为保证金账户
                                bthMerInAcc.setOutAcctNo(parternInfoMap.get(bthMerInAcc.getParternCode()).getDepAcctNo());//出资账户号-->保证金账户
                                bthMerInAcc.setOutAcctNoOrg(parternInfoMap.get(bthMerInAcc.getParternCode()).getDepOrg());//出资账户机构号-->保证金账户开户机构
                                bthMerInAcc.setOutAcctName("");
                                bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_SUCC);// 1.代表入账成功
                                bthMerInAcc.setStatMark("通过批量记账成功");// 成功原因不用插入
                                // bthMerInAcc.setHandleState("2");
                                bthMerInAcc.setHandleMark("处理成功");
                                bthMerInAcc.setHandleState(Constans.HANDLE_STATE_SUCC);// 处理成功2
                                bthMerInAcc.setInAcctTime(IfspDateTime.getYYYYMMDDHHMMSS());// 入账时间
                                bthMerInAcc.setUpdateTime(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(),
                                        IfspDateTime.YYYYMMDDHHMMSS));
                                bthMerInAcc.setCoreRespCode("0000");
                                bthMerInAcc.setCoreRespMsg("");
                                expendListSucc.add(bthMerInAcc);
                            }else{
                                //冲正
                                KeepAccRevRequest keepAccRevRequest = buildReverseKeepAccInfo(bthMerInAcc,orderTm);
                                log.info("服务商手续费支出入账失败处理:[ 记账冲正请求报文:[" + IfspFastJsonUtil.tojson(keepAccRevRequest) + "] ]");
                                soaResultsRecovery = batchSoaClientService.invoke(new SoaParams(IfspFastJsonUtil.objectTomap(keepAccRevRequest)),
                                        "009.bthKeepAccountReverse","","");
                                log.info("服务商手续费支出入账失败处理:[ 记账冲正结果:[txnSsn:[" + bthMerInAcc.getTxnSsn() + "],respCode:[" + soaResultsRecovery.getRespCode() + "] ] ]");
                            }
                        }
                    }
                    List<BthSetCapitalDetail> bthSetCapitalDetails = bthSetCapitalDetailMap_3.get(
                            bthMerInAcc.getTxnSsn());//bthSetCapitalDetailDao.queryByAccNo(outAcctNo, inAcctNo, pch);
                    if(bthSetCapitalDetails==null)
                    {
                        log.error("bthSetCapitalDetailMap_1.get(bthMerInAcc.getTxnSsn());为空，bthMerInAcc.getTxnSsn()=【{}】",bthMerInAcc.getTxnSsn());
                        continue;
                    }

                    for (BthSetCapitalDetail bthSetCapitalDetail : bthSetCapitalDetails)
                    {

                        if ("1".equals(bthMerInAcc.getInAcctStat()))
                        {
                            bthSetCapitalDetail.setOutAccoutOrg(bthMerInAcc.getOutAcctNoOrg());
                            bthSetCapitalDetail.setOutAccountNo(bthMerInAcc.getOutAcctNo());
                            bthSetCapitalDetail.setOutAccountName(bthMerInAcc.getOutAcctName());
                            bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
                            bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
                            bthSetCapitalDetail.setDealRemark("处理成功");
                            bthSetCapitalDetail.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                            bthSetCapitalDetailResultList.add(bthSetCapitalDetail);
                        }

                    }
                }

            }
        }
        long sTimeAcc = System.currentTimeMillis();
        log.debug("============服务商手续费入账汇总表待更新记录为[{}]============", expendListSucc.size());
        if(expendListSucc!=null&&expendListSucc.size()>0)
        {
            bthMerInAccDao.batchUpdateForPartern(expendListSucc);
        }

        long eTimeAcc = System.currentTimeMillis();
        log.info("服务商手续费更新商户入账汇总表完成，耗时[{}]", eTimeAcc - sTimeAcc);

        long sTimeSet = System.currentTimeMillis();
        log.debug("============服务商手续费清分表待更新记录为[{}]============", bthSetCapitalDetailResultList.size());
        List<BthSetCapitalDetail> updList = new ArrayList<>();
        for (BthSetCapitalDetail bthSetCapitalDetail : bthSetCapitalDetailResultList)
        {
            updList.add(bthSetCapitalDetail);
            if (updList.size() == updBatchInsertCount)//清分明细数据较多，先每200条提交一次
            {
                bthSetCapitalDetailDao.batchUpdateForPartern(updList);
                updList = new ArrayList<>();
            }
        }
        if (IfspDataVerifyUtil.isNotEmptyList(updList))
        {
            bthSetCapitalDetailDao.batchUpdateForPartern(updList);
        }

        long eTimeSet = System.currentTimeMillis();
        log.info("服务商手续费更新清分表完成，耗时[{}]", eTimeSet - sTimeSet);
        log.info("------------------查询处理服务商支出入账信息结束---------------------------耗时【{}】" ,System.currentTimeMillis() - start);

        log.info("------------------商户入账成功---------------------------");
        commonResponse.setRespMsg("商户入账成功");
        commonResponse.setRespCode("0000");
        return commonResponse;

    }

    /**
     * 批量同步记账报文组装
     * @param bthMerInAcc
     * @param orderTm
     * @return
     */
    private NewDayTimeKeepAcctRequest buildKeepAccInfo(BthMerInAcc bthMerInAcc,String orderTm){
        NewDayTimeKeepAcctRequest request = new NewDayTimeKeepAcctRequest();
        request.setReqSsn(UUID.randomUUID().toString().replace("-", ""));
        request.setReqTm(IfspDateTime.getYYYYMMDDHHMMSS());
        request.setOrderSsn(bthMerInAcc.getTxnSsn());//这里由于不是订单，所以存入入账流水号
        request.setOrderTm(orderTm);//这里由于不是订单，所以存入一个固定时间
        request.setVerNo("0");
        //内部集合组装-开始
        List<NewDayTimeKeepAcctVo> keepAcctList = new ArrayList<NewDayTimeKeepAcctVo>();
        NewDayTimeKeepAcctVo vo = new NewDayTimeKeepAcctVo();
        vo.setInAccType(Constans.MCHT_WAIT_SETTLE);
        vo.setInAccNo(bthMerInAcc.getInAcctNo());
        vo.setOutAccType(Constans.MCHT_BACK);
        vo.setOutAccNo(bthMerInAcc.getOutAcctNo());
        vo.setTxnDesc("服务商手续费支出");
        vo.setTransAmt(new BigDecimal(bthMerInAcc.getInAcctAmt()).multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString());//因为汇总表里存放金额为元，所以这里要将元转化为分放入
        vo.setTxnCcyType("156");
        vo.setMemo("手续费支出");
        vo.setUniqueSsn(UUID.randomUUID().toString().replace("-", ""));
        vo.setProxyOrg(bthMerInAcc.getInAcctNoOrg());
        vo.setSeq("1");
        keepAcctList.add(vo);
        //内部集合组装-结束
        request.setKeepAcctList(keepAcctList);
        return request;
    }

    /**
     * 批量同步记账查询报文组装
     * @param bthMerInAcc
     * @param orderTm
     * @return
     */
    private KeepAccQryRequest buildQryKeepAccInfo(BthMerInAcc bthMerInAcc,String orderTm){
        KeepAccQryRequest request = new KeepAccQryRequest();
        request.setOrderSsn(bthMerInAcc.getTxnSsn());//这里存入入账流水号
        request.setOrderTm(orderTm);
        return request;
    }

    /**
     * 批量记账冲正报文组装
     * @param bthMerInAcc
     * @param orderTm
     * @return
     */
    private KeepAccRevRequest buildReverseKeepAccInfo(BthMerInAcc bthMerInAcc,String orderTm){
        KeepAccRevRequest request = new KeepAccRevRequest();
        request.setOrderSsn(bthMerInAcc.getTxnSsn());//这里存入入账流水号
        request.setOrderTm(orderTm);
        request.setVerNo("0");
        return request;
    }


    /**
     * 更新本行本地状态
     *
     * @param commonResponse
     * @param parseCapitalSummary
     * @return
     */
    public CommonResponse updateLocalStatNew(CommonResponse commonResponse,
                                             List<CapitalSummaryDownLoadPo> parseCapitalSummary)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        List<BthMerInAcc> bthMerInAccList = new ArrayList<>();
        List<BthSetCapitalDetail> bthSetCapitalDetailList = new ArrayList<>();
        List<BthMerInAccDtl> bthMerInAccDtlList = new ArrayList<>();
        if (parseCapitalSummary == null)
        {
            commonResponse.setRespCode(RespConstans.RESP_CLEARING_RESULT.getCode());
            commonResponse.setRespMsg(RespConstans.RESP_CLEARING_RESULT.getDesc());
            throw new IfspBizException(RespConstans.RESP_CLEARING_RESULT.getCode(),
                                       RespConstans.RESP_CLEARING_RESULT.getDesc());
        }
//		/**
//		 * 多线程更新表
//		 */
//		//处理结果
//		List<Future> futureList = new ArrayList<>();
//		initPool();
//		Future future0 = executor.submit(new Handler(bthMerInAccList));
//		futureList.add(future0);
//		Future future1 = executor.submit(new Handler(bthSetCapitalDetailList));
//		futureList.add(future1);
//		Future future2 = executor.submit(new Handler(bthMerInAccDtlList));
//		futureList.add(future2);

        /*
         * 获取处理结果
         */
//		log.info("获取处理结果。。。。。。");
//		for (Future future : futureList) {
//			try {
//				future.get(10, TimeUnit.MINUTES);
//			} catch (Exception e) {
//				log.error("对账线程处理异常: ", e);
//				//取消其他任务
//				executor.shutdownNow();
//				log.warn("其他子任务已取消.");
//				//返回结果
//				throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
//			}
//		}


        return commonResponse;
    }

    /**
     * 根据入账类型映射分录类型
     * 与ClearStlmSumJobServiceImpl initInAcctType方法对应 !!!
     *
     * @param inAcctType
     * @return
     */
    private Set<String> getEntryTypes(String inAcctType)
    {
        // 清分表分录类型
        Set<String> entryType = new HashSet<>();
        // 设置入账类型
        switch (inAcctType)
        {
            // 商户入账   ( 当分录类型为 99 -商户退款 时,看作 01-商户入账 ,汇总sql查询时与01一起汇总)
            case Constans.IN_ACCT_TYPE_MCHT:
                entryType.add(Constans.ENTRY_TYPE_MER);
                entryType.add(Constans.ENTRY_TYPE_PAY_MER);
                break;
            // 通道还钱
            case Constans.IN_ACCT_TYPE_PAGY:
                entryType.add(Constans.ENTRY_TYPE_BRANCH_FEE);
                break;
            // 手续费 收入
            case Constans.IN_ACCT_TYPE_FEE_2:
                entryType.add(Constans.ENTRY_TYPE_FEE_GAINS_SD_ORG);
                entryType.add(Constans.ENTRY_TYPE_FEE_GAINS_OPEN_ORG);
                entryType.add(Constans.ENTRY_TYPE_FEE_GAINS_UNIVERSAL);
                entryType.add(Constans.ENTRY_TYPE_FEE_GAINS_OPERATE_ORG);
                break;
            // 手续费支出
            case Constans.IN_ACCT_TYPE_FEE:
                entryType.add(Constans.ENTRY_TYPE_FEE_PAY_SD_ORG);
                entryType.add(Constans.ENTRY_TYPE_FEE_PAY_OPEN_ORG);
                entryType.add(Constans.ENTRY_TYPE_FEE_PAY_OPERATE_ORG);
                entryType.add(Constans.ENTRY_TYPE_FEE_PAY_EBANK);
                entryType.add(Constans.ENTRY_TYPE_FEE_GUARANTEE);
                break;
            // 日间记账失败
            case Constans.IN_ACCT_TYPE_DAY_FAIL:
                entryType.add(Constans.ENTRY_TYPE_FOR_ACCOUNT);
                break;
            // 保证金
            case Constans.IN_ACCT_TYPE_GUARANTEE:
                entryType.add(Constans.ENTRY_TYPE_FOR_GUARANTEE);
//                entryType.add(Constans.ENTRY_TYPE_MER_FOR_GUARANTEE);
                break;
            // 商户佣金收入
            case Constans.IN_ACCT_TYPR_COMMISSION:
                entryType.add(Constans.ENTRY_TYPE_COMM_IN);
                break;
            default:
                // 没有这种状态 ,不予处理
                log.error("商户入账汇总表无此类型[{}]", inAcctType);
        }
        return entryType;
    }

    /**
     * 跟新他行本地状态
     *
     * @param commonResponse
     * @param parseMultiBank
     * @return
     */
    public CommonResponse updateMultiBankLocalStat(CommonResponse commonResponse,
                                                   List<SubMerCapMultibank> parseMultiBank)
    {
        List<BthMerInAcc> bthMerInAccList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        List<BthSetCapitalDetail> bthSetCapitalDetailList = new ArrayList<>();
        List<BthMerInAccDtl> bthMerInAccDtlList = new ArrayList<>();
        if (parseMultiBank == null)
        {
            commonResponse.setRespCode(RespConstans.RESP_CLEARING_RESULT.getCode());
            commonResponse.setRespMsg(RespConstans.RESP_CLEARING_RESULT.getDesc());
            throw new IfspBizException(RespConstans.RESP_CLEARING_RESULT.getCode(),
                                       RespConstans.RESP_CLEARING_RESULT.getDesc());
        }
        for (int i = 0; i < parseMultiBank.size(); i++)
        {
            // 通过唯一索引查询商户入账信息表
            BthMerInAcc bthMerInAcc = bthMerInAccDao.queryByTxnSSn(parseMultiBank.get(i).getId());
            if (bthMerInAcc != null)
            {
                if ("1".equals(parseMultiBank.get(i).getDealResultCode()))
                {
                    bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_SUCC);// 1.代表入账成功
                    bthMerInAcc.setStatMark(" ");//成功原因不加,内管展示
                    bthMerInAcc.setHandleState(Constans.HANDLE_STATE_SUCC);// 2
                    // 处理成功
                    bthMerInAcc.setHandleMark("处理成功");
                    bthMerInAcc.setUpdateTime(
                            IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                    // 入账文件返回码
                    bthMerInAcc.setCoreRespCode(parseMultiBank.get(i).getDealResultCode());
                    // 入账文件返回码描述
                    bthMerInAcc.setCoreRespMsg(parseMultiBank.get(i).getDealResultRemark());
                }
                else
                {
                    bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_FAIL);// 入账失败2
                    bthMerInAcc.setStatMark(parseMultiBank.get(i).getDealResultRemark());
                    bthMerInAcc.setHandleState(Constans.HANDLE_STATE_FAIL);// 处理失败3
                    bthMerInAcc.setHandleMark("处理失败");
                    bthMerInAcc.setUpdateTime(
                            IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                    // 入账文件返回码
                    bthMerInAcc.setCoreRespCode(parseMultiBank.get(i).getDealResultCode());
                    // 入账文件返回码描述
                    bthMerInAcc.setCoreRespMsg(parseMultiBank.get(i).getDealResultRemark());
                }
                bthMerInAccList.add(bthMerInAcc);

                // 通过批次号+商户号+入账类型     + 转出账户+ 转入账户 + 转出机构 + 转入机构      来更新清分表状态
                // 商户号
                String chlMerId = bthMerInAcc.getChlMerId();
                // 批次号
                String pch = bthMerInAcc.getBatchNo();
                // 入账类型
                String inAcctType = bthMerInAcc.getInAcctType();
                Set<String> entryType = getEntryTypes(inAcctType);


                // 转出账户
                String outAcctNo = bthMerInAcc.getOutAcctNo();
                // 转入账户
                String inAcctNo = bthMerInAcc.getInAcctNo();
                // 转出机构
                String outAcctNoOrg = bthMerInAcc.getOutAcctNoOrg();
                // 转入机构
                String inAcctNoOrg = bthMerInAcc.getInAcctNoOrg();

                // 日间补记账类型没有存商户号 , 更新的时候按照转入转出账号来更新(此类型转入转出账户一定不为空)
                if (IfspDataVerifyUtil.isBlank(chlMerId))
                {
                    List<BthSetCapitalDetail> bthSetCapitalDetails =
                            bthSetCapitalDetailDao.queryByAccNo(outAcctNo, inAcctNo, pch);

                    for (BthSetCapitalDetail bthSetCapitalDetail : bthSetCapitalDetails)
                    {
                        if ("1".equals(bthMerInAcc.getInAcctStat()))
                        {
                            bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
                            bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
                            bthSetCapitalDetail.setDealRemark("处理成功");
                        }
                        else if ("2".equals(bthMerInAcc.getInAcctStat()))
                        {
                            bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_FAILE);
                            bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_FAILE);
                            bthSetCapitalDetail.setDealRemark(parseMultiBank.get(i).getDealResultRemark());
                        }
                        bthSetCapitalDetail.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                        bthSetCapitalDetailList.add(bthSetCapitalDetail);
                    }


                }
                else
                {
                    // entryType 不为空 , 才去查询
                    if (entryType != null && !entryType.isEmpty())
                    {
                        // 跟新清分表状态 00未入账 01入账成功 02入账失败
                        List<BthSetCapitalDetail> bthSetCapitalDetails = bthSetCapitalDetailDao
                                .querybthMerInAccDtlByWhere(chlMerId, entryType, pch, outAcctNo, inAcctNo, outAcctNoOrg,
                                                            inAcctNoOrg);

                        for (int j = 0; j < bthSetCapitalDetails.size(); j++)
                        {
                            BthSetCapitalDetail bthSetCapitalDetail = bthSetCapitalDetails.get(j);
                            // 商户入账明细表
                            if (Constans.ENTRY_TYPE_MER.equals(bthSetCapitalDetail.getEntryType())
                                    || Constans.ENTRY_TYPE_PAY_MER.equals(bthSetCapitalDetail.getEntryType()))
                            {
                                // 00-未结算,01-结算中,02-结算失败,03-结算成功
                                // 0：未入账1：入账成功2：入账失败
                                List<BthMerInAccDtl> bthMerInAccDtls = bthMerInAccDtlDao
                                        .queryByTxnSeqIdAndPch(bthSetCapitalDetail.getOrderId());
                                for (int k = 0; k < bthMerInAccDtls.size(); k++)
                                {

                                    BthMerInAccDtl bthMerInAccDtl = bthMerInAccDtls.get(k);

                                    if ("1".equals(bthMerInAcc.getInAcctStat()))
                                    {
                                        bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_CLEARING);// 03
                                        bthMerInAccDtl.setInAcctStat(Constans.IN_ACC_STAT_SUCC);// 1

                                    }
                                    else if ("2".equals(bthMerInAcc.getInAcctStat()))
                                    {
                                        bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_FAILE_CLEARING);// 02
                                        bthMerInAccDtl.setInAcctStat(Constans.IN_ACC_STAT_FAIL);// 2

                                    }
                                    bthMerInAccDtl.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                                    bthMerInAccDtlList.add(bthMerInAccDtl);
                                }
                            }

                            if ("1".equals(bthMerInAcc.getInAcctStat()))
                            {
                                bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
                                bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
                                bthSetCapitalDetail.setDealRemark("处理成功");
                            }
                            else if ("2".equals(bthMerInAcc.getInAcctStat()))
                            {
                                bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_FAILE);
                                bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_FAILE);
                                bthSetCapitalDetail.setDealRemark(parseMultiBank.get(i).getDealResultRemark());
                            }
                            bthSetCapitalDetail.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                            bthSetCapitalDetailList.add(bthSetCapitalDetail);
                        }

                    }

                }
            }
        }

        if (bthMerInAccList != null && bthMerInAccList.size() > 0)
        {
            for (int i = 0; i < bthMerInAccList.size(); i++)
            {
                int rows = bthMerInAccDao.updateByPrimaryKeySelective(bthMerInAccList.get(i));
            }
        }

        if (bthSetCapitalDetailList != null && bthSetCapitalDetailList.size() > 0)
        {
            for (int i = 0; i < bthSetCapitalDetailList.size(); i++)
            {
                int rows = bthSetCapitalDetailDao.updateByPrimaryKeySelective(bthSetCapitalDetailList.get(i));
            }
        }

        if (bthMerInAccDtlList != null && bthMerInAccDtlList.size() > 0)
        {
            for (int i = 0; i < bthMerInAccDtlList.size(); i++)
            {
                int rows = bthMerInAccDtlDao.updateByTxnSeqId(bthMerInAccDtlList.get(i));
            }
        }
        return commonResponse;
    }

    /**
     * 解析本行下载核心入账结果文件
     *
     * @param file
     * @return
     */
    public CommonResponse parseCapitalSummary(File file, CommonResponse commonResponse)
    {
        long start = System.currentTimeMillis();
        List<String> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, "GBK");
             BufferedReader br = new BufferedReader(isr);)
        {
            String line = null;
            while ((line = br.readLine()) != null)
            {
                line = line.replace(" ", "");
                list.add(line);
            }
        }
        catch (IOException e)
        {
            log.error("核心入账文件读取失败，文件路径【{}】",file.getAbsolutePath(),e);
        }
        log.info("读取核心入账结果文件结束，耗时【{}】", System.currentTimeMillis() - start);
        // 解析核心入账结果文件
        List<CapitalSummaryDownLoadPo> cList = new ArrayList<>();
        //线程处理结果
        List<Future> futureList = new ArrayList<>();
        int index = 0;
        int batCount = 0;

//        this.bthEnterAccountResultDao.clear();    //清空核心入账结果表
        if (list != null || list.size() > 0)
        {
            for (String lineRPS : list)
            {
                String[] rPSArr = lineRPS.split("\\|#\\|");
                if (rPSArr != null)
                {
                    int i = 0;
					CapitalSummaryDownLoadPo capitalSummaryDownLoadPo = new CapitalSummaryDownLoadPo();

//                    BthEnterAccountResult capitalSummaryDownLoadPo = new BthEnterAccountResult();
//                    capitalSummaryDownLoadPo.setId(UUIDCreator.randomUUID().toString());
                    capitalSummaryDownLoadPo.setTranOrg(rPSArr[i++]);// 机构
                    capitalSummaryDownLoadPo.setFeeCatalog(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setBorrowFlag(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setOutAccoutOrg(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setOutAccountNo(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setLendFlag(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setInAccoutOrg(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setInAccountNo(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setTransAmount(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setTransCur(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setBillFlag(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setSummaryCode(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setSummary(rPSArr[i++]);
                    capitalSummaryDownLoadPo.setReserved(rPSArr[i++]);
                    String dealResultCode = rPSArr[i++];
                    capitalSummaryDownLoadPo.setDealResultCode(dealResultCode);
                    if ("0000".equals(dealResultCode))
                    {
                        capitalSummaryDownLoadPo.setDealResultRemark("");
                    }
                    else
                    {
                        capitalSummaryDownLoadPo.setDealResultRemark(rPSArr[i++]);
                    }
                    index++;
                    cList.add(capitalSummaryDownLoadPo);
                    if (cList.size() == updBatchInsertCount)
                    {
                        batCount++;
                        Future future = updExecutor.submit(new UpdHandler(cList, batCount));
                        futureList.add(future);
                        //清理集合
                        cList = new ArrayList<>();
                    }
                }

            }
            if (cList.size() > 0)
            {
                batCount++;
                Future future = updExecutor.submit(new UpdHandler(cList, batCount));
                futureList.add(future);
            }
            /*
             * 获取明细处理结果
             */
            for (Future future : futureList)
            {
                try
                {
                    future.get(10, TimeUnit.MINUTES);
                }
                catch (Exception e)
                {
                    log.error("子线程处理异常: ", e);
                    //取消其他任务
                    destoryUpdPool();
                    log.warn("其他子任务已取消.");
                    //返回结果
                    throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常:" + e.getMessage());
                }
            }
//			log.info("记录入账汇总完成, 总数量【{}】" , (index - 3));
            log.info("核心入账结果入库结束，总数量【{}】，耗时【{}】", index, System.currentTimeMillis() - start);
//			log.info("更新入账汇总完成");
        }
        return commonResponse;
    }

    /**
     * 解析他行入账结果文件
     *
     * @param file
     * @param commonResponse
     * @return
     */
    public List<SubMerCapMultibank> parseMultiBank(File file, CommonResponse commonResponse)
    {

        List<String> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, "GBK");
             BufferedReader br = new BufferedReader(isr);)
        {

            String line = null;
            while ((line = br.readLine()) != null)
            {
                line = line.replace(" ", "");
                list.add(line);
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        list.remove(0);
        // 解析杭外核心入账结果文件
        List<SubMerCapMultibank> cList = new ArrayList<>();
        for (String lineRPS : list)
        {
            String[] rPSArr = lineRPS.split("\\|#\\|");
            if (rPSArr != null)
            {
                int i = 0;
                SubMerCapMultibank subMerCapMultibank = new SubMerCapMultibank();
                subMerCapMultibank.setSeqNo(rPSArr[i++]);// 机构
                subMerCapMultibank.setOutAccountNo(rPSArr[i++]);
                subMerCapMultibank.setOutAccountName(rPSArr[i++]);
                subMerCapMultibank.setOutAccoutOrg(rPSArr[i++]);
                subMerCapMultibank.setInAccountNo(rPSArr[i++]);
                subMerCapMultibank.setInAccountName(rPSArr[i++]);
                subMerCapMultibank.setInAccoutOrg(rPSArr[i++]);
                subMerCapMultibank.setTransCur(rPSArr[i++]);
                subMerCapMultibank.setTxnAmt(rPSArr[i++]);
                subMerCapMultibank.setLendFlag(rPSArr[i++]);
                subMerCapMultibank.setFeeCatalog(rPSArr[i++]);
                subMerCapMultibank.setFeeAmt(rPSArr[i++]);
                subMerCapMultibank.setSummaryCode(rPSArr[i++]);
                subMerCapMultibank.setSummary(rPSArr[i++]);
                subMerCapMultibank.setIsScrcuBank(rPSArr[i++]);
                subMerCapMultibank.setId(rPSArr[i++]);
                subMerCapMultibank.setReserved2(rPSArr[i++]);
                subMerCapMultibank.setDealSuccessTime(rPSArr[i++]);
                subMerCapMultibank.setDealSuccessTxnSsn(rPSArr[i++]);
                subMerCapMultibank.setDealResultCode(rPSArr[i++]);
                subMerCapMultibank.setDealResultRemark(rPSArr[i++]);
                cList.add(subMerCapMultibank);
            }
        }
        return cList;
    }

    public CommonResponse downLoadFile(String distDir, String localDir)
    {
        CommonResponse response = new CommonResponse();
        List<BthBatchAccountFile> list = bthBatchAccountFileDao.queryByDealtatus("02");

        // 本地存在就跳过，不存在就去ftp下载
        if (list == null || list.size() == 0)
        {
            log.info("没有需要下载的文件");
            return response;
        }
        String distFileName = "";
        SftpUtil sftpUtil = new SftpUtil("10.16.10.118", "ftprs", "ftprs", Integer.parseInt("22"));
        ChannelSftp sftp = sftpUtil.connectSFTP();
        for (int i = 0; i < list.size(); i++)
        {
            try
            {
                BthBatchAccountFile bthBatchAccountFile = list.get(i);
                // 如果本地文件存在就跳过,不存在去ftp下载
                File localFileName = new File(localDir + bthBatchAccountFile.getAccFileName());
                File localFileNameOK = new File(localDir + bthBatchAccountFile.getAccFileName() + ".ok");
                if (localFileName.exists())
                {
                    log.info(">>>>>>>>>>文件" + localFileName + "存在");
                    continue;
                }
                if (localFileNameOK.exists())
                {
                    log.info(">>>>>>>>>>OK文件" + localFileName + "存在");
                    continue;
                }
                log.info(">>>>>>>>>>文件" + bthBatchAccountFile.getAccFileName() + "不存在");
                // 判断远程目录中文件存不存在

                if (sftpUtil.isFileExist(sftp, distDir))
                { // 判断远程目录文件是否存在
                    log.info("远程目录文件存在");
                    distFileName = distDir + bthBatchAccountFile.getAccFileName() + ".dow";
                    if (sftpUtil.isFileExist(sftp, distFileName))
                    {
                        log.info("远程文件" + distFileName + "存在");
                        sftpUtil.download(distDir, bthBatchAccountFile.getAccFileName() + ".dow", localDir,
                                          bthBatchAccountFile.getAccFileName() + ".dow", sftp);
                        if (sftpUtil.isFileExist(sftp, distFileName + ".ok"))
                        {
                            log.info("远程文件" + distFileName + ".ok存在");
                            sftpUtil.download(distDir, bthBatchAccountFile.getAccFileName() + ".dow", localDir,
                                              bthBatchAccountFile.getAccFileName() + ".dow.ok", sftp);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                log.info("远程目录文件不存在");
                e.printStackTrace();
            }
            finally
            {
                sftpUtil.disconnected(sftp);
            }

        }

        return response;
    }


    /**
     * 轮循查询本行文件是否已被文件传输平台传输完毕
     *
     * @param bthBatchAccountFilelist
     */
    private void checkFileExist(List<BthBatchAccountFile> bthBatchAccountFilelist)
    {

        String checkFilePath = this.accountRecvDirBackUp;

        BthSysParamInfo paramInfo = bthSysParamInfoDao.selectByParamCode("CHECK_INACCFILE_TIME");

        // 检查文件时间次数参数
        String spaceFlag;
        if (IfspDataVerifyUtil.isNotBlank(paramInfo) && IfspDataVerifyUtil.isNotBlank(paramInfo.getParamInfo()))
        {
            spaceFlag = paramInfo.getParamInfo();
        }
        else
        {
            spaceFlag = "30000,40";
        }

        String[] split = spaceFlag.split(",");
        // 间隔时间
        long spaceTm = Long.parseLong(split[0]);
        // 尝试次数
        int tryNum = Integer.parseInt(split[1]);

        poll:
        do
        {

            for (int i = 0; i < bthBatchAccountFilelist.size(); i++)
            {
                BthBatchAccountFile bthBatchAccountFile = bthBatchAccountFilelist.get(i);
                // 仅检查本行文件 ,他行业务还未开展
                if (Constans.FILE_IN_ACC.equals(bthBatchAccountFile.getFileType()))
                {
                    File iBankOK = new File(checkFilePath + bthBatchAccountFile.getAccFileName() + ".ok");

                    if (!iBankOK.exists())
                    {
                        log.info("入账文件[{}]还未传输成功!!", bthBatchAccountFile.getAccFileName() + ".ok");
                        break;
                    }
                }
                if (i == bthBatchAccountFilelist.size() - 1)
                {
                    log.info("入账文件已传输完毕,等待更新...");
                    break poll;
                }

            }

            tryNum--;

            try
            {
                log.info("等待文件传输  ,时间为 [{}] ms,剩余次数 [{}]", spaceTm, tryNum);
                Thread.sleep(spaceTm);
            }
            catch (InterruptedException e)
            {
                log.error("睡眠异常", e);
            }

        } while (tryNum > 0);


    }


    /**
     * 状态更新工作线程
     *
     * @param <T>
     */
//	class Handler<T> implements Callable<T> {
//
//		private List<Object> bthMerInAccList;
//		private List<Object> bthSetCapitalDetailList;
//		private List<Object> bthMerInAccDtlList;
//
//		public Handler(List<Object> bthList) {
//			if(IfspDataVerifyUtil.isNotEmpty(bthList)&&(bthList.size()>0)){
//				if(bthList.get(0) instanceof BthMerInAcc){
//					this.bthMerInAccList = bthList;
//				}else if(bthList.get(0) instanceof BthSetCapitalDetail){
//					this.bthSetCapitalDetailList = bthList;
//				}else if(bthList.get(0) instanceof BthMerInAccDtl){
//					this.bthMerInAccDtlList = bthList;
//				}
//
//			}
//		}
//
//		@Override
//		public T call() throws Exception {
//			try {
//				if (bthMerInAccList != null && bthMerInAccList.size() > 0) {
//					long sTime = System.currentTimeMillis();
//					for (int i = 0; i < bthMerInAccList.size(); i++) {
//						int rows = bthMerInAccDao.updateByPrimaryKeySelective((BthMerInAcc)bthMerInAccList.get(i));
//					}
//					long eTime = System.currentTimeMillis();
//					log.info("更新商户入账汇总表完成，耗时[{}]", eTime-sTime);
//				}
//
//				if (bthSetCapitalDetailList != null && bthSetCapitalDetailList.size() > 0) {
//					long sTime = System.currentTimeMillis();
//					for (int i = 0; i < bthSetCapitalDetailList.size(); i++) {
//						int rows = bthSetCapitalDetailDao.updateByPrimaryKeySelective((BthSetCapitalDetail)bthSetCapitalDetailList.get(i));
//					}
//					long eTime = System.currentTimeMillis();
//					log.info("更新清分表完成，耗时[{}]", eTime-sTime);
//				}
//
//				if (bthMerInAccDtlList != null && bthMerInAccDtlList.size() > 0) {
//					long sTime = System.currentTimeMillis();
//					for (int i = 0; i < bthMerInAccDtlList.size(); i++) {
//						int rows = bthMerInAccDtlDao.updateByTxnSeqId((BthMerInAccDtl)bthMerInAccDtlList.get(i));
//					}
//					long eTime = System.currentTimeMillis();
//					log.info("更新商户入账明细表完成，耗时[{}]", eTime-sTime);
//				}
//			}catch (Exception e){
//				log.error("更新结算状态异常:", e);
//				log.debug("====更新结算状态(error end)====");
//				throw e;
//			}
//			return null;
//		}
//	}

    /**
     * 状态更新工作线程
     *
     * @param <T>
     */
    class UpdHandler<T> implements Callable<T>
    {

        private List<CapitalSummaryDownLoadPo> parseCapitalSummary;
//        private List<BthEnterAccountResult> bthEnterAccountResultList;
        private int index = 0;

        public UpdHandler(List<CapitalSummaryDownLoadPo> parseCapitalSummary, int index)
        {
            this.parseCapitalSummary = parseCapitalSummary;
            this.index = index;
        }

//        public UpdHandler(List<CapitalSummaryDownLoadPo> parseCapitalSummary,
//                          List<BthEnterAccountResult> bthEnterAccountResultList, int index)
//        {
//            this.parseCapitalSummary = parseCapitalSummary;
//            this.bthEnterAccountResultList = bthEnterAccountResultList;
//            this.index = index;
//        }


        @Override
        public T call() throws Exception
        {
            long start = System.currentTimeMillis();
            if (parseCapitalSummary == null)
            {
                log.info("没有需要处理的数据");
                return null;
            }
            log.info("入账状态更新批量第【{}】片任务处理开始", index);
            List<BthSetCapitalDetail> bthSetCapitalDetailResultList = new ArrayList<>();
            List<BthMerInAccDtl> bthMerInAccDtlResultList = new ArrayList<>();
            /**
             * 生成orderSSn为类型的List，用来批量查询BthMerInAcc
             * CapitalSummaryDownLoadPo中的Reserved即为order_Ssn，也是txn_ssn
             */
            List<String> orderSsnList = parseCapitalSummary.stream().map(CapitalSummaryDownLoadPo::getReserved).collect(
                    Collectors.toList());
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("orderSsnList", orderSsnList);
            parameter.put("startDate",IfspDateTime.plusTime(IfspDateTime.getYYYYMMDD(),"yyyyMMdd", IfspTimeUnit.DAY,-10));
            parameter.put("endDate",IfspDateTime.getYYYYMMDD());
            List<BthMerInAcc> bthMerInAccList = bthMerInAccDao.selectList("queryByTxnSSnList", parameter);
            Map<String, BthMerInAcc> bthMerInAccMap = bthMerInAccList.stream().collect(
                    Collectors.toMap(BthMerInAcc::getTxnSsn, a -> a, (k1, k2) -> k1));
            log.info("批量查询商户入账信息表结束并整理成Map<String, BthMerInAcc>结构，结果数量【{}】，耗时【{}】", bthMerInAccList.size(),
                     System.currentTimeMillis() - start);


            List<BthSetCapitalDetail> bthSetCapitalDetailList_1 = bthSetCapitalDetailDao.selectList("queryByCapitalDetailByTxnSsnList_1",
                                                                                                    parameter);

            log.info("批量查询queryByCapitalDetailByTxnSsnList_1，结果数量【{}】，耗时【{}】", bthSetCapitalDetailList_1.size(),
                     System.currentTimeMillis() - start);
            /**
             * Reserved2字段中存放in_acct_stat入账状态
             * Reserved3字段中存放txn_ssn
             */
            Map<String, List<BthSetCapitalDetail>> bthSetCapitalDetailMap_1 = bthSetCapitalDetailList_1.stream().collect(
                    Collectors.groupingBy(BthSetCapitalDetail::getReserved3));


            List<BthSetCapitalDetail> bthSetCapitalDetailList_2 = bthSetCapitalDetailDao.selectList("queryByCapitalDetailByTxnSsnList_2",
                                                                                              parameter);
            log.info("批量查询queryByCapitalDetailByTxnSsnList_2，结果数量【{}】，耗时【{}】", bthSetCapitalDetailList_2.size(),
                     System.currentTimeMillis() - start);
            /**
             * Reserved3字段中存放txn_ssn连接串
             */
            Map<String, List<BthSetCapitalDetail>> bthSetCapitalDetailMap_2 = bthSetCapitalDetailList_2.stream().collect(
                    Collectors.groupingBy(BthSetCapitalDetail::getReserved3));

            for (int i = 0; i < parseCapitalSummary.size(); i++)
            {
                BthMerInAcc bthMerInAcc = bthMerInAccMap.get(parseCapitalSummary.get(i).getReserved());
                if (bthMerInAcc != null)
                {
                    if ("0000".equals(parseCapitalSummary.get(i).getDealResultCode()))
                    {
                        bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_SUCC);// 1.代表入账成功
                        bthMerInAcc.setStatMark(" ");// 成功原因不用插入
                        // bthMerInAcc.setHandleState("2");
                        bthMerInAcc.setHandleMark("处理成功");
                        bthMerInAcc.setHandleState(Constans.HANDLE_STATE_SUCC);// 处理成功2
                        bthMerInAcc.setInAcctTime(IfspDateTime.getYYYYMMDDHHMMSS());// 入账时间
                        bthMerInAcc.setUpdateTime(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(),
                                                                         IfspDateTime.YYYYMMDDHHMMSS));
                        // 入账文件返回码
                        bthMerInAcc.setCoreRespCode(parseCapitalSummary.get(i).getDealResultCode());
                        // 入账文件返回码描述
                        bthMerInAcc.setCoreRespMsg(parseCapitalSummary.get(i).getDealResultRemark());

                    }
                    else
                    {
                        bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_FAIL);// 入账失败2
                        bthMerInAcc.setStatMark(parseCapitalSummary.get(i).getDealResultRemark());
                        // bthMerInAcc.setHandleState("3");
                        bthMerInAcc.setHandleMark("处理失败");
                        bthMerInAcc.setHandleState(Constans.HANDLE_STATE_FAIL);// 处理失败3
                        bthMerInAcc.setUpdateTime(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(),
                                                                         IfspDateTime.YYYYMMDDHHMMSS));
                        // 入账文件返回码
                        bthMerInAcc.setCoreRespCode(parseCapitalSummary.get(i).getDealResultCode());
                        // 入账文件返回码描述
                        bthMerInAcc.setCoreRespMsg(parseCapitalSummary.get(i).getDealResultRemark());
                    }


                    // 通过批次号+商户号+入账类型     + 转出账户+ 转入账户 + 转出机构 + 转入机构      来更新清分表状态
                    // 商户号
                    String chlMerId = bthMerInAcc.getChlMerId();
                    // 批次号
                    String pch = bthMerInAcc.getBatchNo();
                    // 入账类型
                    String inAcctType = bthMerInAcc.getInAcctType();
                    Set<String> entryType = getEntryTypes(inAcctType);

                    // 转出账户
                    String outAcctNo = bthMerInAcc.getOutAcctNo();
                    // 转入账户
                    String inAcctNo = bthMerInAcc.getInAcctNo();
                    // 转出机构
                    String outAcctNoOrg = bthMerInAcc.getOutAcctNoOrg();
                    // 转入机构
                    String inAcctNoOrg = bthMerInAcc.getInAcctNoOrg();

                    // 日间补记账类型没有存商户号 , 更新的时候按照转入转出账号来更新(此类型转入转出账户一定不为空)
                    if (IfspDataVerifyUtil.isBlank(chlMerId))
                    {
                        List<BthSetCapitalDetail> bthSetCapitalDetails = bthSetCapitalDetailMap_1.get(
                                bthMerInAcc.getTxnSsn());//bthSetCapitalDetailDao.queryByAccNo(outAcctNo, inAcctNo, pch);
                        if(bthSetCapitalDetails==null)
                        {
                            log.error("bthSetCapitalDetailMap_1.get(bthMerInAcc.getTxnSsn());为空，bthMerInAcc.getTxnSsn()=【{}】",bthMerInAcc.getTxnSsn());
                            continue;
                        }

                        for (BthSetCapitalDetail bthSetCapitalDetail : bthSetCapitalDetails)
                        {
                            if ("1".equals(bthMerInAcc.getInAcctStat()))
                            {
                                bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
                                bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
                                bthSetCapitalDetail.setDealRemark("处理成功");
                            }
                            else if ("2".equals(bthMerInAcc.getInAcctStat()))
                            {
                                bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_FAILE);
                                bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_FAILE);
                                bthSetCapitalDetail.setDealRemark(parseCapitalSummary.get(i).getDealResultRemark());
                            }
                            bthSetCapitalDetail.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                            bthSetCapitalDetailResultList.add(bthSetCapitalDetail);
                        }


                    }
                    else
                    {
                        // entryType 不为空 , 才去查询
                        if (entryType != null && !entryType.isEmpty())
                        {
                            long sTimeSet = System.currentTimeMillis();
                            // 跟新清分表状态 00未入账 01入账成功 02入账失败
//                            List<BthSetCapitalDetail> bthSetCapitalDetails = bthSetCapitalDetailDao.querybthMerInAccDtlByWhere(chlMerId, entryType, pch, outAcctNo, inAcctNo,outAcctNoOrg, inAcctNoOrg);
                            List<BthSetCapitalDetail> bthSetCapitalDetails=bthSetCapitalDetailMap_2.get(bthMerInAcc.getTxnSsn());
                            if(bthSetCapitalDetails==null)
                            {
                                log.error("bthSetCapitalDetailMap_2.get(bthMerInAcc.getTxnSsn())为空，bthMerInAcc.getTxnSsn()=【{}】",bthMerInAcc.getTxnSsn());
                                continue;
                            }
                            long eTimeSet = System.currentTimeMillis();
//								log.debug("======当前处理第{}条数据耗时[{}]", i, eTimeSet-sTimeSet);
                            for (int j = 0; j < bthSetCapitalDetails.size(); j++)
                            {
                                /**
                                 * 判断日期，清分表和汇总变的日期一致才更新，保证一个商户历史入账数据更新正确性
                                 * 测试暂时注释
                                 */
                                BthSetCapitalDetail bthSetCapitalDetail = bthSetCapitalDetails.get(j);
                                if (!IfspDataVerifyUtil.equals(bthSetCapitalDetail.getCleaTime(),
                                                               bthMerInAcc.getDateStlm()))
                                {
                                    log.info("商户号[{}]跳过清算日期不一致数据[{}]", chlMerId, bthSetCapitalDetail.getId());
                                    continue;
                                }
                                // 清分表类型为支付/退款 ,需要更新明细表
                                if (Constans.ENTRY_TYPE_MER.equals(bthSetCapitalDetail.getEntryType())
                                        || Constans.ENTRY_TYPE_PAY_MER.equals(bthSetCapitalDetail.getEntryType()))
                                {

                                    // 00-未结算,01-结算中,02-结算失败,03-结算成功
                                    // 0：未入账1：入账成功2：入账失败
//                                    List<BthMerInAccDtl> bthMerInAccDtls = bthMerInAccDtlDao.queryByTxnSeqIdAndPch(bthSetCapitalDetail.getOrderId());
                                    BthMerInAccDtl bthMerInAccDtl = new BthMerInAccDtl();
                                    if ("1".equals(bthMerInAcc.getInAcctStat()))
                                    {
                                        //20191014如果入账状态为成功，说明T0本金已入账，不更新时间
                                        if (!Constans.IN_ACC_STAT_SUCC.equals(bthSetCapitalDetail.getReserved2())) //入账状态   //bthMerInAccDtls.get(0).getInAcctStat()
                                        {
                                            // 更新成真正的入账时间
                                            bthMerInAccDtl.setInAcctDate(IfspDateTime.getYYYYMMDD());
                                        }
                                        bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_CLEARING);// 03
                                        bthMerInAccDtl.setInAcctStat(Constans.IN_ACC_STAT_SUCC);// 1

//                                        if("01".equals(bthMerInAccDtl.getOrderType())){//支付时，更新订单表支付入账标识
//                                            PayOrderInfo orderInfo = new PayOrderInfo();
//                                            orderInfo.setOrderSsn(bthMerInAccDtl.getTxnSeqId());
//                                            orderInfo.setInAcctFlag(Constans.IN_ACC_STAT_SUCC);
//                                            payOrderInfoDao.updateInAcctFlag(orderInfo);
//                                        }
                                    }
                                    else if ("2".equals(bthMerInAcc.getInAcctStat()))
                                    {
                                        bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_FAILE_CLEARING);// 02
                                        bthMerInAccDtl.setInAcctStat(Constans.IN_ACC_STAT_FAIL);// 2

                                    }
                                    bthMerInAccDtl.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                                    bthMerInAccDtl.setTxnSeqId(bthSetCapitalDetail.getOrderId());//主键更新条件
                                    bthMerInAccDtlResultList.add(bthMerInAccDtl);
                                }

                                if ("1".equals(bthMerInAcc.getInAcctStat()))
                                {
                                    bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
                                    bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
                                    bthSetCapitalDetail.setDealRemark("处理成功");
                                }
                                else if ("2".equals(bthMerInAcc.getInAcctStat()))
                                {
                                    bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_FAILE);
                                    bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_FAILE);
                                    bthSetCapitalDetail.setDealRemark(
                                            parseCapitalSummary.get(i).getDealResultRemark());
                                }
                                bthSetCapitalDetail.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                                bthSetCapitalDetailResultList.add(bthSetCapitalDetail);
                            }

                        }
                    }

                }
            }

            long sTimeAcc = System.currentTimeMillis();
            log.debug("============入账汇总表待更新记录为[{}]============", bthMerInAccList.size());
            if(bthMerInAccList!=null&&bthMerInAccList.size()>0)
            {
                bthMerInAccDao.batchUpdate(bthMerInAccList);
            }

            long eTimeAcc = System.currentTimeMillis();
            log.info("更新商户入账汇总表完成，耗时[{}]", eTimeAcc - sTimeAcc);

            long sTimeSet = System.currentTimeMillis();
            log.debug("============清分表待更新记录为[{}]============", bthSetCapitalDetailResultList.size());
            List<BthSetCapitalDetail> updList = new ArrayList<>();
            for (BthSetCapitalDetail bthSetCapitalDetail : bthSetCapitalDetailResultList)
            {
                updList.add(bthSetCapitalDetail);
                if (updList.size() == updBatchInsertCount)
                {
                    bthSetCapitalDetailDao.batchUpdate(updList);
                    updList = new ArrayList<>();
                }
            }
            if (IfspDataVerifyUtil.isNotEmptyList(updList))
            {
                bthSetCapitalDetailDao.batchUpdate(updList);
            }

            long eTimeSet = System.currentTimeMillis();
            log.info("更新清分表完成，耗时[{}]", eTimeSet - sTimeSet);

            long sTimeDtl = System.currentTimeMillis();
            log.debug("============入账明细表待更新记录为[{}]============", bthMerInAccDtlResultList.size());
            List<BthMerInAccDtl> updDtlList = new ArrayList<>();
            for (BthMerInAccDtl bthMerInAccDtl : bthMerInAccDtlResultList)
            {
                updDtlList.add(bthMerInAccDtl);
                if (updDtlList.size() == updBatchInsertCount)
                {
                    bthMerInAccDtlDao.batchUpdate(updDtlList);
                    updDtlList = new ArrayList<>();
                }
            }
            if (IfspDataVerifyUtil.isNotEmptyList(updDtlList))
            {
                bthMerInAccDtlDao.batchUpdate(updDtlList);
            }
            long eTimeDtl = System.currentTimeMillis();
            log.info("更新商户入账明细表完成，耗时[{}]", eTimeDtl - sTimeDtl);
            return null;
        }

        //		@Override
        public T call_bak() throws Exception
        {
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                List<BthMerInAcc> bthMerInAccList = new ArrayList<>();
                List<BthSetCapitalDetail> bthSetCapitalDetailList = new ArrayList<>();
                List<BthMerInAccDtl> bthMerInAccDtlList = new ArrayList<>();
                if (parseCapitalSummary == null)
                {
                    log.info("没有需要处理的数据");
                    return null;
                }
                for (int i = 0; i < parseCapitalSummary.size(); i++)
                {
                    long sTimeIn = System.currentTimeMillis();
                    // 通过唯一索引查询商户入账信息表
                    BthMerInAcc bthMerInAcc = bthMerInAccDao.queryByTxnSSn(parseCapitalSummary.get(i).getReserved());
                    long eTimeIn = System.currentTimeMillis();
//					log.debug("当前处理第{}条数据耗时[{}]", i, eTimeIn-sTimeIn);
                    if (bthMerInAcc != null)
                    {
                        if ("0000".equals(parseCapitalSummary.get(i).getDealResultCode()))
                        {
                            bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_SUCC);// 1.代表入账成功
                            bthMerInAcc.setStatMark(" ");// 成功原因不用插入
                            // bthMerInAcc.setHandleState("2");
                            bthMerInAcc.setHandleMark("处理成功");
                            bthMerInAcc.setHandleState(Constans.HANDLE_STATE_SUCC);// 处理成功2
                            bthMerInAcc.setInAcctTime(IfspDateTime.getYYYYMMDDHHMMSS());// 入账时间
                            bthMerInAcc.setUpdateTime(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(),
                                                                             IfspDateTime.YYYYMMDDHHMMSS));
                            // 入账文件返回码
                            bthMerInAcc.setCoreRespCode(parseCapitalSummary.get(i).getDealResultCode());
                            // 入账文件返回码描述
                            bthMerInAcc.setCoreRespMsg(parseCapitalSummary.get(i).getDealResultRemark());

                        }
                        else
                        {
                            bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_FAIL);// 入账失败2
                            bthMerInAcc.setStatMark(parseCapitalSummary.get(i).getDealResultRemark());
                            // bthMerInAcc.setHandleState("3");
                            bthMerInAcc.setHandleMark("处理失败");
                            bthMerInAcc.setHandleState(Constans.HANDLE_STATE_FAIL);// 处理失败3
                            bthMerInAcc.setUpdateTime(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(),
                                                                             IfspDateTime.YYYYMMDDHHMMSS));
                            // 入账文件返回码
                            bthMerInAcc.setCoreRespCode(parseCapitalSummary.get(i).getDealResultCode());
                            // 入账文件返回码描述
                            bthMerInAcc.setCoreRespMsg(parseCapitalSummary.get(i).getDealResultRemark());
                        }
                        bthMerInAccList.add(bthMerInAcc);

                        // 通过批次号+商户号+入账类型     + 转出账户+ 转入账户 + 转出机构 + 转入机构      来更新清分表状态
                        // 商户号
                        String chlMerId = bthMerInAcc.getChlMerId();
                        // 批次号
                        String pch = bthMerInAcc.getBatchNo();
                        // 入账类型
                        String inAcctType = bthMerInAcc.getInAcctType();
                        Set<String> entryType = getEntryTypes(inAcctType);

                        // 转出账户
                        String outAcctNo = bthMerInAcc.getOutAcctNo();
                        // 转入账户
                        String inAcctNo = bthMerInAcc.getInAcctNo();
                        // 转出机构
                        String outAcctNoOrg = bthMerInAcc.getOutAcctNoOrg();
                        // 转入机构
                        String inAcctNoOrg = bthMerInAcc.getInAcctNoOrg();


                        // 日间补记账类型没有存商户号 , 更新的时候按照转入转出账号来更新(此类型转入转出账户一定不为空)
                        if (IfspDataVerifyUtil.isBlank(chlMerId))
                        {

                            List<BthSetCapitalDetail> bthSetCapitalDetails =
                                    bthSetCapitalDetailDao.queryByAccNo(outAcctNo, inAcctNo, pch);

                            for (BthSetCapitalDetail bthSetCapitalDetail : bthSetCapitalDetails)
                            {
                                if ("1".equals(bthMerInAcc.getInAcctStat()))
                                {
                                    bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
                                    bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
                                    bthSetCapitalDetail.setDealRemark("处理成功");
                                }
                                else if ("2".equals(bthMerInAcc.getInAcctStat()))
                                {
                                    bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_FAILE);
                                    bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_FAILE);
                                    bthSetCapitalDetail.setDealRemark(parseCapitalSummary.get(i).getDealResultRemark());
                                }
                                bthSetCapitalDetail.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                                bthSetCapitalDetailList.add(bthSetCapitalDetail);
                            }


                        }
                        else
                        {
                            // entryType 不为空 , 才去查询
                            if (entryType != null && !entryType.isEmpty())
                            {
                                long sTimeSet = System.currentTimeMillis();
                                // 跟新清分表状态 00未入账 01入账成功 02入账失败
                                List<BthSetCapitalDetail> bthSetCapitalDetails = bthSetCapitalDetailDao
                                        .querybthMerInAccDtlByWhere(chlMerId, entryType, pch, outAcctNo, inAcctNo,
                                                                    outAcctNoOrg, inAcctNoOrg);

                                long eTimeSet = System.currentTimeMillis();
//								log.debug("======当前处理第{}条数据耗时[{}]", i, eTimeSet-sTimeSet);
                                for (int j = 0; j < bthSetCapitalDetails.size(); j++)
                                {
                                    /**
                                     * 判断日期，清分表和汇总变的日期一致才更新，保证一个商户历史入账数据更新正确性
                                     * 测试暂时注释
                                     */
                                    BthSetCapitalDetail bthSetCapitalDetail = bthSetCapitalDetails.get(j);
                                    if (!IfspDataVerifyUtil.equals(bthSetCapitalDetail.getCleaTime(),
                                                                   bthMerInAcc.getDateStlm()))
                                    {
                                        log.info("商户号[{}]跳过清算日期不一致数据[{}]", chlMerId, bthSetCapitalDetail.getId());
                                        continue;
                                    }
                                    // 清分表类型为支付/退款 ,需要更新明细表
                                    if (Constans.ENTRY_TYPE_MER.equals(bthSetCapitalDetail.getEntryType())
                                            || Constans.ENTRY_TYPE_PAY_MER.equals(bthSetCapitalDetail.getEntryType()))
                                    {

                                        // 00-未结算,01-结算中,02-结算失败,03-结算成功
                                        // 0：未入账1：入账成功2：入账失败
                                        List<BthMerInAccDtl> bthMerInAccDtls = bthMerInAccDtlDao.queryByTxnSeqIdAndPch(
                                                bthSetCapitalDetail.getOrderId());
                                        BthMerInAccDtl bthMerInAccDtl = new BthMerInAccDtl();
                                        if ("1".equals(bthMerInAcc.getInAcctStat()))
                                        {
                                            //20191014如果入账状态为成功，说明T0本金已入账，不更新时间
                                            if (!Constans.IN_ACC_STAT_SUCC.equals(
                                                    bthMerInAccDtls.get(0).getInAcctStat()))
                                            {
                                                // 更新成真正的入账时间
                                                bthMerInAccDtl.setInAcctDate(IfspDateTime.getYYYYMMDD());
                                            }
                                            bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_SUCCESS_CLEARING);// 03
                                            bthMerInAccDtl.setInAcctStat(Constans.IN_ACC_STAT_SUCC);// 1

                                        }
                                        else if ("2".equals(bthMerInAcc.getInAcctStat()))
                                        {
                                            bthMerInAccDtl.setStlStatus(Constans.SETTLE_STATUS_FAILE_CLEARING);// 02
                                            bthMerInAccDtl.setInAcctStat(Constans.IN_ACC_STAT_FAIL);// 2

                                        }
                                        bthMerInAccDtl.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                                        bthMerInAccDtl.setTxnSeqId(bthSetCapitalDetail.getOrderId());//主键更新条件
                                        bthMerInAccDtlList.add(bthMerInAccDtl);
                                    }

                                    if ("1".equals(bthMerInAcc.getInAcctStat()))
                                    {
                                        bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_SUCCESS);
                                        bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_SUCCESS);
                                        bthSetCapitalDetail.setDealRemark("处理成功");
                                    }
                                    else if ("2".equals(bthMerInAcc.getInAcctStat()))
                                    {
                                        bthSetCapitalDetail.setAccountStauts(Constans.ACCOUNT_STATUS_FAILE);
                                        bthSetCapitalDetail.setDealResult(Constans.DEAL_RESULT_FAILE);
                                        bthSetCapitalDetail.setDealRemark(
                                                parseCapitalSummary.get(i).getDealResultRemark());
                                    }
                                    bthSetCapitalDetail.setUpdateDate(IfspDateTime.getYYYYMMDDHHMMSS());
                                    bthSetCapitalDetailList.add(bthSetCapitalDetail);
                                }

                            }
                        }
                    }
                }
                long sTimeAcc = System.currentTimeMillis();
                log.debug("============入账汇总表待更新记录为[{}]============", bthMerInAccList.size());
                bthMerInAccDao.batchUpdate(bthMerInAccList);
                long eTimeAcc = System.currentTimeMillis();
                log.info("更新商户入账汇总表完成，耗时[{}]", eTimeAcc - sTimeAcc);

                long sTimeSet = System.currentTimeMillis();
                log.debug("============清分表待更新记录为[{}]============", bthSetCapitalDetailList.size());
                List<BthSetCapitalDetail> updList = new ArrayList<>();
                for (BthSetCapitalDetail bthSetCapitalDetail : bthSetCapitalDetailList)
                {
                    updList.add(bthSetCapitalDetail);
                    if (updList.size() == updBatchInsertCount)
                    {
                        bthSetCapitalDetailDao.batchUpdate(updList);
                        updList = new ArrayList<>();
                    }
                }
                if (IfspDataVerifyUtil.isNotEmptyList(updList))
                {
                    bthSetCapitalDetailDao.batchUpdate(updList);
                }

                long eTimeSet = System.currentTimeMillis();
                log.info("更新清分表完成，耗时[{}]", eTimeSet - sTimeSet);

                long sTimeDtl = System.currentTimeMillis();
                log.debug("============入账明细表待更新记录为[{}]============", bthMerInAccDtlList.size());
                List<BthMerInAccDtl> updDtlList = new ArrayList<>();
                for (BthMerInAccDtl bthMerInAccDtl : bthMerInAccDtlList)
                {
                    updDtlList.add(bthMerInAccDtl);
                    if (updDtlList.size() == updBatchInsertCount)
                    {
                        bthMerInAccDtlDao.batchUpdate(updDtlList);
                        updDtlList = new ArrayList<>();
                    }
                }
                if (IfspDataVerifyUtil.isNotEmptyList(updDtlList))
                {
                    bthMerInAccDtlDao.batchUpdate(updDtlList);
                }
                long eTimeDtl = System.currentTimeMillis();
                log.info("更新商户入账明细表完成，耗时[{}]", eTimeDtl - sTimeDtl);

            }
            catch (Exception e)
            {
                log.error("更新结算状态异常:", e);
                log.debug("====更新结算状态(error end)====");
                throw e;
            }
            return null;
        }
    }

    //	@PostConstruct
//	private void initPool() {
//		log.info("====初始化线程池(start)====");
//        /*
//         * 初始化线程池
//         */
//		if (executor != null) {
//            /*
//             * 关闭线程池
//             */
//			try {
//				executor.shutdown();
//				if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
//					executor.shutdownNow();
//				}
//			} catch (InterruptedException e) {
//				System.out.println("awaitTermination interrupted: " + e);
//				executor.shutdownNow();
//			}
//		}
//        /*
//         * 构建
//         */
//		executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
//			AtomicInteger atomic = new AtomicInteger();
//
//			@Override
//			public Thread newThread(Runnable r) {
//				return new Thread(r, "unionRecoHander_" + this.atomic.getAndIncrement());
//			}
//		});
//		log.info("====初始化线程池(end)====");
//	}
    @PostConstruct
    private void updInitPool()
    {
        destoryUpdPool();
        log.info("====初始化线程池(start)====");
        /*
         * 初始化线程池
         */
        if (updExecutor != null)
        {
            /*
             * 关闭线程池
             */
            try
            {
                updExecutor.shutdown();
                if (!updExecutor.awaitTermination(10, TimeUnit.SECONDS))
                {
                    updExecutor.shutdownNow();
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("awaitTermination interrupted: " + e);
                updExecutor.shutdownNow();
            }
        }
        /*
         * 构建
         */
        updExecutor = Executors.newFixedThreadPool(updThreadCount, new ThreadFactory()
        {
            AtomicInteger atomic = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "updHander_" + this.atomic.getAndIncrement());
            }
        });
        log.info("====updHander线程数为[{}]====", updThreadCount);
        log.info("====初始化线程池(end)====");
    }
//	private void destoryPool() {
//		log.info("====销毁线程池(start)====");
//		/*
//		 * 初始化线程池
//		 */
//		if (executor != null) {
//			log.info("线程池为null, 无需清理");
//			/*
//			 * 关闭线程池
//			 */
//			try {
//				executor.shutdown();
//				if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
//					executor.shutdownNow();
//				}
//			} catch (InterruptedException e) {
//				System.out.println("awaitTermination interrupted: " + e);
//				executor.shutdownNow();
//			}
//		}
//		log.info("====销毁线程池(end)====");
//	}

    private void destoryUpdPool()
    {
        log.info("====销毁线程池(start)====");
        /*
         * 初始化线程池
         */
        if (updExecutor != null)
        {
            log.info("线程池为null, 无需清理");
            /*
             * 关闭线程池
             */
            try
            {
                updExecutor.shutdown();
                if (!updExecutor.awaitTermination(10, TimeUnit.SECONDS))
                {
                    updExecutor.shutdownNow();
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("awaitTermination interrupted: " + e);
                updExecutor.shutdownNow();
            }
        }
        log.info("====销毁线程池(end)====");
    }

}
