package com.scrcu.ebank.ebap.batch.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.ruim.ifsp.utils.constant.IfspConstants;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.message.IfspBase64;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.message.IfspStringUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.common.dict.*;
import com.scrcu.ebank.ebap.batch.common.utils.*;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.HandleAndCreateFileService;
import com.scrcu.ebank.ebap.batch.service.MerRegService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: ljy
 * @create: 2018-10-23 15:56
 */
@Service
@Slf4j
public class MerRegServiceImpl implements MerRegService {

    /**
     * 本地注册文件生成路径
     */
    @Value("${LocalGenFilePath}")
    private String LocalGenFilePath;

    /**
     * 本地注册文件生成路径-备份目录
     */
    @Value("${LocalGenFilePathBak}")
    private String LocalGenFilePathBak;
    /**
     * 注册文件上传路径
     */
    @Value("${UNIONPAY_REG_PATH}")
    private String UNIONPAY_REG_PATH;
    /**
     * 商户注册文件名
     */
    @Value("${MerRegFileName}")
    private String MerRegFileName;

    /**
     * 注册文件上传目标IP
     */
    @Value("${UNIONPAY_REG_IP}")
    private String UNIONPAY_REG_IP;
    /**
     * 注册文件上传目标IP 用户
     */
    @Value("${UNIONPAY_REG_USER}")
    private String UNIONPAY_REG_USER;
    /**
     * 注册文件上传目标IP 密码
     */
    @Value("${UNIONPAY_REG_PWD}")
    private String UNIONPAY_REG_PASSWORD;

    /**
     * 注册文件上传目标IP 端口
     */
    @Value("${UNIONPAY_REG_PORT}")
    private String UNIONPAY_REG_PORT;

    /**
     * 注册反馈文件本地存放路径
     */
    @Value("${LocalCupsRegRtnFilePath}")
    private String LocalCupsRegRtnFilePath;


    /**
     * 注册反馈文件下载路径
     */
    @Value("${UNIONPAY_REG_REMOTE_PATH}")
    private String UNIONPAY_REG_REMOTE_PATH;

    /**
     * 注册反馈文件名称
     */
    @Value("${MerRegRtnFileName}")
    private String MerRegRtnFileName;


    /**
     * 银联商户注册信息
     */
    @Resource
    private TpamCupMchtInfoDao tpamCupMchtInfoDao;




    /**
     * 批量系统参数表
     */
    @Resource
    private BthSysParamInfoDao bthSysParamInfoDao;


    /**
     * 通道商户信息表
     */
    @Resource
    private PagyMchtInfoDao pagyMchtInfoDao;
    @Resource
    private IfsParamDao ifsParamDao;
    /**
     * 商户信息表
     */
    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;
    @Resource
    private MchtStaffInfoDao mchtStaffInfoDao;
    @Resource
    HandleAndCreateFileService handleAndCreateFileService;
    /**
     * 当日最大处理记录数
     */
    @Value("${MAXLINENUM}")
    private int MAXLINENUM;
    /**
     * 生成单个文件最大记录数,最多20000
     */
    private   final static int OCNCSYNNUM = 10000;//10000

    private final static String MCHTSYNNUM="0120";

    @Override
    public CommonResponse genMerRegFile(MerRegRequest request) throws IOException, ParseException {
        //----------------------------------------  1.基础参数初始化  -------------------------------------------------
        CommonResponse response = new CommonResponse();
        String stlmDate = request.getSettleDate();
        //String today = IfspDateTime.plusTime(stlmDate, IfspDateTime.YYYYMMDD, IfspTimeUnit.DAY, 1);

        //本地注册文件路径
        String localRegFilePath = LocalGenFilePath;
        //本地注册文件备份路径
        String localRegFilePathBak = LocalGenFilePathBak;

        //FTP银联商户注册文件路径
        String ftpCupsRegFilePath = UNIONPAY_REG_PATH;
        //当前生成文件的日期，更新返回结果的时候需使用
        String today = IfspDateTime.plusTime(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYYMMDD, IfspTimeUnit.DAY, -1);
        log.info("today=========>"+today);
        String curDate =  today.substring(2);
        log.info("curDate=========>"+curDate);
        //商户注册文件名称
        String merRegFileName = MerRegFileName.replace("YYMMDD",curDate);

        //----------------------------------------  2.生成商户注册文件  ------------------------------------------------
        File localFilePath =new File(localRegFilePath);
        if (!localFilePath.exists()){
            log.info("本地系统生成目录不存在");
            localFilePath.mkdirs();
        }
        File localFileBakPath =new File(localRegFilePathBak);
        if (!localFileBakPath.exists()){
            log.info("本地系统生成备份目录不存在");
            localFileBakPath.mkdirs();
        }
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMddHHmmss");

        //每日处理记录数
        /**
         * zb 添加从参数管理中取参数配置信息
         */
        int maxLineNum = MAXLINENUM;
        IfsParam ifsParam = ifsParamDao.selectByParamKey(MCHTSYNNUM);
        log.info("=========================>> 查询配置参数:{} ",IfspFastJsonUtil.tojson(ifsParam));
        if(ifsParam!=null) {
            String queryMchtSynNum = ifsParam.getParamValue();
            try {
                maxLineNum = Integer.parseInt(queryMchtSynNum);
            } catch (Exception e) {
                log.info("{}配置值不正确，不使用参数配置处理！", MCHTSYNNUM);
            }
        }
        if(maxLineNum<=0){
            log.info("处理记录参数配置不为正数，不处理，直接结束任务");
            return response;
        }

        //查询需要处理的通道商户信息
        List<String> stateList = new ArrayList<>();
        stateList.add(UpMchtSynStateDict.INIT.getCode());//OCNCSYNNUM //maxLineNum
        //查询1W条
        Page page = PageHelper.startPage(1, OCNCSYNNUM);
        List<PagyMchtInfo> pagyMchtInfos = pagyMchtInfoDao.selectByState(stateList);

//        // 需要新增的商户
//        tpamCupMchtInfos = tpamCupMchtInfoDao.selectBytTimeAdd(beginTime,endTime);
//        log.info("时间段[{}]-[{}]新增的商户有[{}]个",beginTime ,endTime ,tpamCupMchtInfos.size());
//
//        // 需要修改的商户
//        tpamCupMchtInfos2 = tpamCupMchtInfoDao.selectBytTimeUpdate(beginTime,endTime);
//        if (IfspDataVerifyUtil.isNotEmptyList(tpamCupMchtInfos2)){
//            log.info("时间段[{}]-[{}]修改的商户有[{}]个",beginTime ,endTime ,tpamCupMchtInfos2.size());
//            tpamCupMchtInfos.addAll(tpamCupMchtInfos2);
//        }

        if (IfspDataVerifyUtil.isNotEmptyList(pagyMchtInfos)){
            // 从远程FTP获取批次号  ,在此基础上累加
             int max = 0;

            SftpUtil sftpUtil = new SftpUtil(UNIONPAY_REG_IP, UNIONPAY_REG_USER, UNIONPAY_REG_PASSWORD, 22);
            ChannelSftp sftp = sftpUtil.connectSFTP();
            try {
                Vector ls = sftp.ls(ftpCupsRegFilePath);

                for (Object l : ls) {
                    Map<String, Object> stringObjectMap = IfspFastJsonUtil.objectTomap(l);
                    String fileName = stringObjectMap.get("filename").toString();
                    if (14 == fileName.length()&&fileName.startsWith(merRegFileName.substring(0,9))
                            && fileName.endsWith(merRegFileName.substring(11))){
                        if (Integer.parseInt(fileName.substring(9,11)) >=  max)
                        {
                            max = Integer.parseInt(fileName.substring(9,11))+1;
                            if (max > 99){
                                sftpUtil.disconnected(sftp);
                                throw new IfspBizException("9999","当日已达最大批次, 无法生成注册文件!!!");
                            }
                        }
                    }
                }
                sftpUtil.disconnected(sftp);
                log.info("=========================>> 生成文件批次号为 : {} ",IfspStringUtil.leftPad(max+"", 2,"0"));

            } catch (SftpException e) {
                log.error("从远程SFTP获取批次号 异常",e);
                throw new IfspBizException("9999" ,"从远程SFTP获取批次号异常!!!" );
            }
            long total = page.getTotal();

            log.info("查询需同步商户总记录数{}",total);
            log.info("查询参数配置需同步的商户数{}",pagyMchtInfos.size());
            //每个文件记录数最多20000 , fileCount为需要生成的文件个数
            //pagyMchtInfos.size()%OCNCSYNNUM==0?pagyMchtInfos.size()/OCNCSYNNUM:pagyMchtInfos.size()/OCNCSYNNUM+1;
            String merRF ;
            int handlerCount = 0; //已处理多少条
            while(handlerCount<maxLineNum&&pagyMchtInfos.size()>0){

                if((handlerCount+pagyMchtInfos.size())>maxLineNum){
                    pagyMchtInfos = pagyMchtInfos.subList(0,maxLineNum-handlerCount);
                 }
                    //文件名
                    merRF = merRegFileName.replace("??", IfspStringUtil.leftPad(max + "", 2, "0"));
                    max++;
                    try {
                        handleAndCreateFileService.handle(localRegFilePath + merRF, pagyMchtInfos, curDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handlerCount=handlerCount+pagyMchtInfos.size();

                if(pagyMchtInfos.size() <OCNCSYNNUM||handlerCount>=maxLineNum){  //没有数据可处理或都已处理完设置记录数，直接跳出循环
                    break;
                }
                 page = PageHelper.startPage(1, OCNCSYNNUM);
                pagyMchtInfos = pagyMchtInfoDao.selectByState(stateList);
            }
        }else {
            log.info("注册时间{} - {}内没有商户需要上传...");
        }
        //----------------------------------------  3.上传商户注册文件到FTP  ------------------------------------------------
        SftpUtil sftpUtil = new SftpUtil(UNIONPAY_REG_IP, UNIONPAY_REG_USER, UNIONPAY_REG_PASSWORD, 22);
        ChannelSftp sftp2 = sftpUtil.connectSFTP();
        try {
            log.info("上传SFTP商户注册文件路径：{}", ftpCupsRegFilePath);
            log.info("本地商户注册文件路径：{}", localRegFilePath);


            //查询所有待上传的文件

            File localRegFileDirectory = new File(localRegFilePath);
            if(localRegFileDirectory.isDirectory()){
                for(String fileName : localRegFileDirectory.list()){
                    log.info("上传商户注册文件名称：{}", localRegFilePath+fileName);
                    //上传ftp
                    sftpUtil.ftpUpload(ftpCupsRegFilePath, localRegFilePath, localRegFilePath+fileName, sftp2);
                    //移动文件到备份目录
                    log.info("移动商户注册文件名称到备份目录：{}", localRegFilePathBak+fileName);
                    new File(localRegFilePath+fileName).renameTo(new File(localRegFilePathBak+fileName));
                }

            }
            sftpUtil.disconnected(sftp2);
        } catch (Exception e) {
            e.printStackTrace();
            sftpUtil.disconnected(sftp2);
          //  log.info("上传商户注册文件[{}]到SFTP失败:{}", ftpCupsRegFilePath+merRF, e);
            throw new IfspBizException("9999", "上传商户注册文件到FTP失败");
        }
        return response;
    }
    /**
     * 生成文件实体
     * @param tpamCupMchtInfo
     * @return
     */
    private MerRegInfo getMerRegInfo(TpamCupMchtInfo tpamCupMchtInfo) {
        MerRegInfo merRegInfo = new MerRegInfo();
        ReflectionUtil.copyProperties(tpamCupMchtInfo,merRegInfo);

        // 营业证明文件类型  netMchntSvcTp
        switch (tpamCupMchtInfo.getNetMchntSvcTp()){
            case "2":
                merRegInfo.setNetMchntSvcTp("01");
                break;
            case "6":
                merRegInfo.setNetMchntSvcTp("02");
                break;
            default:
                log.error("错误的营业证明文件类型!!!");
        }

        // 商户服务类型(默认值11)
        merRegInfo.setMchntSvcTp("11");
        // 企业性质(不填)
        merRegInfo.setEtpsAttr("");
        // 商户拓展方式(默认值1)
        merRegInfo.setRecnclTp("1");
        // 收单外包服务机构(不填)
        merRegInfo.setPrincipalNm("");
        // 商户开票开户银行名称(不填)
        merRegInfo.setCooking("");
        // 商户开票账号(不填)
        merRegInfo.setMchntIcp("");
        // 商户开票账户名称(不填)
        merRegInfo.setTrafficLine("");
        // 是否申请优惠价格(不填)
        merRegInfo.setDirectAcqSettleIn("");
        // 商户现场注册标识码(不填)
        merRegInfo.setPaySysSettleNo1("");
        // 特殊计费类型(默认值0)
        merRegInfo.setSpecDiscTp("0");
        // 特殊计费档次(默认值0)
        merRegInfo.setSpecDiscLvl("0");
        // 借记卡发卡银联分润算法(不填)
        merRegInfo.setAllotAlgo("");
        // 贷记卡发卡银联分润算法(不填)
        merRegInfo.setAllotCd("");
        // 商户手续费(不填)
        merRegInfo.setMchntDiscDetIndex("");
        // 单笔限额(默认值99)
        merRegInfo.setSingleAtLimit("99");
        // 单笔限额说明(不填)
        merRegInfo.setSingleAtLimitDesc("");
        // 单卡单日累计限额(默认值99)
        merRegInfo.setSingleCardDayAtLimit("99");
        // 单卡单日累计限额说明(不填)
        merRegInfo.setSingleCardDayAtLimitDesc("");
        //总分店标志(不填)
        merRegInfo.setHdqrsBranchIn("");
        // 总店商户代码(不填)
        merRegInfo.setHdqrsMchntCd("");
        //渠道接入商代码(不填)
        merRegInfo.setChnlMchntCd("");
        //是否开通免密免签(不填)
        merRegInfo.setMccApplRule("");
        //品牌(不填)
        merRegInfo.setMasterPwd("");
        return merRegInfo;
    }

    @Override
    public CommonResponse getMerRegRtnFile(MerRegRequest request) throws IOException {
        //----------------------------------------  1.基础参数初始化  -------------------------------------------------
        CommonResponse response = new CommonResponse();
        String stlmDate = request.getSettleDate();
        // 解析两天前文件
        String returnDay = IfspDateTime.plusTime(stlmDate, IfspDateTime.YYYYMMDD, IfspTimeUnit.DAY, -3);

        log.info("解析日期:{}上送的注册文件",returnDay);

        //本地反馈注册文件路径
        String localCupsRegRtnFilePath = LocalCupsRegRtnFilePath;
        //FTP银联商户反馈注册文件路径
        String ftpCupsRegRtnFilePath = UNIONPAY_REG_REMOTE_PATH;
        //当前取的结果文件日期，更新返回结果的时候需使用
        String returnDayStr =  returnDay.substring(2);
        //商户注册文件名称
        String merRegFileName = MerRegRtnFileName.replace("YYMMDD",returnDayStr);
        File localFilePath =new File(localCupsRegRtnFilePath);
        if (!localFilePath.exists()){
            log.info("本地系统生成目录不存在");
            localFilePath.mkdirs();
        }
        //----------------------------------------  2.下载商户注册反馈文件  ------------------------------------------------
        SftpUtil sftpUtil = new SftpUtil(UNIONPAY_REG_IP, UNIONPAY_REG_USER, UNIONPAY_REG_PASSWORD, 22);
        ChannelSftp sftp = sftpUtil.connectSFTP();

        if (sftpUtil.isFileExist(sftp, ftpCupsRegRtnFilePath+merRegFileName)) {
        } else {
            log.info("=========================>> 银联商户注册反馈文件【{}】不存在 ,解析结束",merRegFileName);
            sftpUtil.disconnected(sftp);
            return response;
        }

        try {
            sftpUtil.download(ftpCupsRegRtnFilePath,merRegFileName , localCupsRegRtnFilePath , merRegFileName,sftp );
            sftpUtil.disconnected(sftp);
        } catch (Exception e) {
            log.error("=========================>> SFTP下载银联商户注册反馈文件[{}]失败:{}", ftpCupsRegRtnFilePath+merRegFileName, e);
            sftpUtil.disconnected(sftp);
            throw new  IfspBizException("9999", "SFTP下载银联商户注册反馈文件失败");
        }
        //----------------------------------------  3.解析商户注册反馈文件  ------------------------------------------------
        File file=new File(localCupsRegRtnFilePath + merRegFileName);
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis,IfspConstants.GBK_ENCODING);
             BufferedReader br  = new BufferedReader(isr)){
            String line = null;
            String[] splits = new String[0];
            int i = 0;
            boolean upFlag = false; //执行更新为成功标志
            while ((line = br.readLine()) != null) {
                if (!line.contains(",")) {
                    continue;
                } else {
                    splits = line.split(",");
                    if (i == 0) {
                        log.info("=========================>> 文件【{}】内共有【{}】个商户送往银联注册,其中【{}】个商户注册成功,【{}】个商户注册失败", merRegFileName, splits[1], splits[2], splits[3]);
                        i++;
                        if(Integer.parseInt(splits[2])>0){
                            upFlag = true;
                        }
                        continue;
                    }
                    i++;
                    //处理失败的记录

                    if (splits.length < 3 || splits[2] == null || splits[2].trim().length() == 0) {
                        log.info("=========================>> 文件【{}】,第【{}】行银联商户号不存在,不更新状态。[]", merRegFileName, i, line);
                    }


                    String mchntCd = splits[2];
                    String errStr = "";
                    if (splits.length >= 63) {
                        errStr = splits[62];
                    }

                    boolean successFlag = false;

                    if(errStr.indexOf("新增的商户已存在")>-1){
                        //查询后设置为成功
                        TpamCupMchtInfo tpamCupMchtInfo = tpamCupMchtInfoDao.selectByMchtId(mchntCd);
                        if(tpamCupMchtInfo==null){
                            successFlag = false;
                        }else{
                            if(IfspDataVerifyUtil.equals(tpamCupMchtInfo.getChnlId(),"15")) {
                                //设置银联商户同步状态为同步完成，新增状态
                                pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO",mchntCd, returnDayStr, "POSV注册");
                                pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO_OLD",mchntCd, returnDayStr, "POSV注册");
                                successFlag = true;
                            }else{
                                //设置银联商户同步状态为同步完成，新增状态
                                pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO",mchntCd, returnDayStr, "新增的商户已存在");
                                pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO_OLD",mchntCd, returnDayStr, "新增的商户已存在");
                                successFlag = true;
                            }
                        }
                        }
                    if(errStr.indexOf("新增的商户已存在")>-1){
                        //查询后设置为成功
                        TpamCupMchtInfo tpamCupMchtInfo = tpamCupMchtInfoDao.selectByMchtId(mchntCd);
                        if(tpamCupMchtInfo==null){
                            successFlag = false;
                        }else{
                            if(IfspDataVerifyUtil.equals(tpamCupMchtInfo.getChnlId(),"15")) {
                                //设置银联商户同步状态为同步完成，新增状态
                                pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO",mchntCd, returnDayStr, "POSV注册");
                                pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO_OLD",mchntCd, returnDayStr, "POSV注册");
                                successFlag = true;
                            }else{
                                //设置银联商户同步状态为同步完成，新增状态
                                pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO",mchntCd, returnDayStr, "新增的商户已存在");
                                pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO_OLD",mchntCd, returnDayStr, "新增的商户已存在");
                                successFlag = true;
                            }
                        }
                    }
                    if(!successFlag){
                        //通过银联商户号更新
                        int n = pagyMchtInfoDao.updateUpMchtSynRes("PAGY_MCHT_INFO",mchntCd, returnDayStr, UpMchtSynStateDict.SYN_ERR.getCode(), CharUtil.cutStr(splits[0]+"|"+splits[1]+"|"+errStr,IfspConstants.UTF_8_ENCODING,500));
                         n = n+pagyMchtInfoDao.updateUpMchtSynRes("PAGY_MCHT_INFO_OLD",mchntCd, returnDayStr, UpMchtSynStateDict.SYN_ERR.getCode(), CharUtil.cutStr(splits[0]+"|"+splits[1]+"|"+errStr,IfspConstants.UTF_8_ENCODING,500));
                        if (n != 1) {
                            log.info("=========================>>文件[{}],银联商户号[{}]更新结果不为1,更新记录数[{}]", merRegFileName, mchntCd, n);
                        }
                    }

                }
            }
            br.close();
            if (upFlag) {
                int n = pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO",null, returnDayStr, "");
                n = n + pagyMchtInfoDao.successUpdateUpMchtSynRes("PAGY_MCHT_INFO_OLD",null, returnDayStr, "");
                log.info("=========================>>文件[{}],更新结果同步成功,更新记录数[{}]", merRegFileName, n);
               int count= pagyMchtInfoDao.updateSynXwState(returnDayStr);
                log.info("=========================>>文件[{}],更新小微商户注销成功,更新记录数[{}]", merRegFileName, count);


            } else {
                log.info("=========================>>文件[{}],不更新结果同步成功", merRegFileName);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return response;
    }

//    public static void main(String[] args) {
//        System.out.println(Integer.parseInt("2147483648"));
//    }
}
