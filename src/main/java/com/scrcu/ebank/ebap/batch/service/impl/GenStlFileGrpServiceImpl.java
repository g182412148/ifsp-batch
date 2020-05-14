package com.scrcu.ebank.ebap.batch.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.constant.IfspConstants;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.GenerateStlFileRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.msg.IBankMsg;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.GenStlFileGrpService;
import com.scrcu.ebank.ebap.batch.soaclient.ClearingSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBaseException;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  任务名称:入账文件生成
 *
 *  从商户入账汇总表取数,根据入账类型(手续费入账,商户入账,通道入账)生成对应的入账文件  (HANDLE_STATE 为 0 与 3)
 *
 * @author ljy
 */
@Slf4j
@Service
public class GenStlFileGrpServiceImpl implements GenStlFileGrpService {


    @Value("${inacctFilePath}")
    private String inacctFilePath;

    @Value("${otherInacctFilePath}")
    private String otherInacctFilePath;

    @Resource
    private BthMerInAccDao bthMerInAccDao;

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    @Resource
    private BthBatchAccountFileDao bthBatchAccountFileDao;

    @Resource
    private BthSetCapitalDetailDao bthSetCapitalDetailDao;

    @Resource
    private ClearingSoaService clearingSoaService;

    /**
     * 批量系统参数表
     */
    @Resource
    private BthSysParamInfoDao bthSysParamInfoDao;

    /**
     * 线程池
     */
    ExecutorService executor;

    /**
     * 处理线程数量
     */
    @Value("${clearSum.threadCount}")
    private Integer threadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${clearSum.threadCapacity}")
    private Integer threadCapacity;

    /**
     * 线程处理数量
     */
    @Value("${upd.batchInsertCount}")
    private Integer updBatchInsertCount;

    @Override
    public CommonResponse generateStlFileGrp(GenerateStlFileRequest request) {

        // 1. 本行文件路径
        String fileLocalPath = inacctFilePath;
        log.info("本行文件存放目录: " +fileLocalPath);
        File dir = new File(fileLocalPath);
        if (!dir.exists()) {
            log.error("本行共享目录不存在!!!");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"本行共享目录["+fileLocalPath+"]不存在!!!");
        }


        // 3. 定义响应对象
        CommonResponse response = new CommonResponse();


        // 定义待上送到文件的 list
        List<BthMerInAcc> preTotalInAccList = new ArrayList<>();

        // 商户待清算账户为贷方的记录
        List<BthMerInAcc> mchtPreList = new ArrayList<>();

        // 其他待入账记录
        List<BthMerInAcc> othPreList = new ArrayList<>();

        log.info("==========================生成本行入账文件start===============================");
        // 待入账记录 , 未排序 (本行)
        List<BthMerInAcc> bthMerInAccList = getPreInAccRec();

        for (BthMerInAcc bthMerInAcc : bthMerInAccList) {

            // 过滤发生金额为0的记录  (不会出现 ,此处预防)
            if (BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt()))==0){
                continue;
            }

            // 指定账户不支持转出金额为负数  将借贷关系反转   (借方是商户待清算且入账金额为负数时, 一般对应的贷方是商户结算账户  ,故 LendFlag 一定是指定账户[1] ,此处能反转  )
            // todo 是否存在借方是商户待清算 ,而贷方账户为空且入账金额为负数的情况 ??  存在会导致当日入账余额不足问题 , 次日会自动成功
            if (BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt()))>0 && Constans.BORROW_ALLOCATED_ACCT.equals(bthMerInAcc.getLendFlag())){
                converObj(bthMerInAcc);
            }

            // 判断贷方是否是待清算账户 , 如果是则先处理
            if (IfspDataVerifyUtil.isNotBlank(bthMerInAcc.getInAcctNo()) && bthMerInAcc.getInAcctNo().contains("26210824")
                    &&BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt()))<0 ){
                mchtPreList.add(bthMerInAcc);
            }else {
                othPreList.add(bthMerInAcc);
            }

        }

        preTotalInAccList.addAll(mchtPreList);
        preTotalInAccList.addAll(othPreList);

        log.info("入账汇总待写入文件条数 : [{}] ",preTotalInAccList.size());

        // 文件名定义
        String fileNameIBank = genFileName("F");
        // 文件路径
        String inAccFile =fileLocalPath+fileNameIBank;
        File file = new File(inAccFile);
        file.setWritable(true);


        FileOutputStream f = null;
        OutputStreamWriter fileWriter = null ;

        try {
            StringBuffer sbf ;
            f = new FileOutputStream(file,false);
            fileWriter = new OutputStreamWriter(f, IfspConstants.GBK_ENCODING);

            Iterator<BthMerInAcc> iterator = preTotalInAccList.iterator();
            while (iterator.hasNext()){
                BthMerInAcc record = iterator.next();
                sbf = new StringBuffer();
                appendTxt(sbf,record,Constans.SETTL_ACCT_TYPE_PLAT,0);
                fileWriter.write(sbf.toString());
                fileWriter.write("\r\n");
                fileWriter.flush();

            }



            // 组装批量控制文件表
            BthBatchAccountFile bbaf = initBthBatchAccountFile(fileNameIBank, Constans.FILE_IN_ACC,"00", null, 0);
            // 事务控制: 先更新入账汇总表状态, 再将文件信息插入批量控制文件表
            GenStlFileGrpService genStlFileGrpServiceImpl = (GenStlFileGrpService)IfspSpringContextUtils.getInstance().getBean("genStlFileGrpServiceImpl");
            genStlFileGrpServiceImpl.updStorage(preTotalInAccList,bbaf);

        }  catch (IOException e) {
            log.error("生成入账文件异常!!",e);
            response.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            response.setRespMsg("生成入账文件异常");
            return response;

        } finally {
            if (fileWriter != null ){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                   log.error("OutputStreamWriter close IOException: "+ e.getMessage());
                }
            }
            if (f != null ){
                try {
                    f.close();
                } catch (IOException e) {
                    log.error("FileOutputStream close IOException: "+ e.getMessage());
                }
            }
        }

        //创建“.ok”文件  ,以此通知核心文件已经生成完毕
        String okFileName = inAccFile+".ok";
        createFile(okFileName);

        log.info("==========================生成本行入账文件end===============================");





//       todo 他行入账文件生成
//        // 7. 筛选出他行商户入账的部分,生成商户入账文件(入账类型: 01表示商户入账,结算账户账户类型 : 0代表本行 1代表他行)
//        List<BthMerInAcc> merOtherList = bthMerInAccDao.selectByInAcctTypeFlag(Constans.IN_ACCT_TYPE_MCHT);
//        if (IfspDataVerifyUtil.isNotEmptyList(merOtherList)) {
//
//            // 他行文件路径
//            String fileLocalPath2 = this.otherInacctFilePath;
//            File dir2 = new File(fileLocalPath);
//            if (!dir2.exists()) {
//                log.error("他行共享目录不存在!!!");
//                throw new IfspBizException("9999","他行共享目录["+fileLocalPath2+"]不存在!!!");
//            }
//
//
//            log.info("==========================生成商户入账文件:结算账户为他行start===============================");
//            StringBuffer mchtInText1 ;
//            FileOutputStream f5 ;
//            try {
//
//                String fileName = genFileName("U");
//                String mchtFile1 =fileLocalPath2+fileName;
//                f5 = new FileOutputStream(mchtFile1,false);
//                fileWriter = new OutputStreamWriter(f5, "GBK");
//                int i= 1;
//                //  统计总金额
//                BigDecimal sumFileAmt = BigDecimal.ZERO;
//                for (BthMerInAcc bthMerInAcc : merOtherList) {
//                    // 过滤发生金额为0的记录
//                    if (BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt()))==0){
//                        continue;
//                    }
//                    mchtInText1 = new StringBuffer();
//                    appendTxt(mchtInText1,bthMerInAcc,"1",i);
//                    fileWriter.write(mchtInText1.toString());
//                    fileWriter.write("\r\n");
//                    fileWriter.flush();
//                    i++;
//                    // 总金额
//                    sumFileAmt = sumFileAmt.add(new BigDecimal(bthMerInAcc.getInAcctAmt()));
//                }
//
//                fileWriter.close();
//                f5.close();
//                fileWriter = null;
//                //创建“.ok”文件  ,以此通知核心文件已经生成完毕
//                String okFileName = mchtFile1+".ok";
//                createFile(okFileName);
//                // 组装批量控制文件表
//                BthBatchAccountFile f = initBthBatchAccountFile(fileName, Constans.FILE_OTHER_IN_ACC,"03",sumFileAmt,i-1);
//                // 事务控制: 先更新入账汇总表状态, 再将文件信息插入批量控制文件表
//                GenStlFileGrpService genStlFileGrpServiceImpl = (GenStlFileGrpService)IfspSpringContextUtils.getInstance().getBean("genStlFileGrpServiceImpl");
//                genStlFileGrpServiceImpl.updStorage(merOtherList,f);
//            } catch (IOException e) {
//                log.error("生成商户入账文件异常!!",e);
//                response.setRespCode("9999");
//                response.setRespMsg("生成商户入账文件异常");
//                return response;
//            }
//
//
//            log.info("==========================生成商户入账文件:结算账户为他行start===============================");
//        }

        return response;
    }



    /**
     * 获取待入账的记录
     * @return
     */
    private List<BthMerInAcc> getPreInAccRec()
    {
        Map<String,Object> param = new HashMap<>();
        param.put("handStPre",Constans.HANDLE_STATE_PRE);
        param.put("handStFail",Constans.HANDLE_STATE_FAIL);
        param.put("brno",Constans.SETTL_ACCT_TYPE_PLAT);
        return bthMerInAccDao.selectList("selectPreInAccRec", param);
    }

    /**
     * 获取他行待入账的记录
     * @return
     */
    private List<BthMerInAcc> getOtherPreInAccRec()
    {
        Map<String,Object> param = new HashMap<>();
        param.put("handStPre",Constans.HANDLE_STATE_PRE);
        param.put("handStFail",Constans.HANDLE_STATE_FAIL);
        param.put("brno",Constans.SETTL_ACCT_TYPE_CROSS);
        param.put("otherSelType","0");
        param.put("otherSelTypeFail","3");
        return bthMerInAccDao.selectList("selectOtherPreInAccRec", param);
    }

    /**
     * 获取他行待更新状态的记录
     * @return
     */
    private List<BthMerInAcc> getOtherPreInAccRecState()
    {
        Map<String,Object> param = new HashMap<>();
        param.put("handStPre",Constans.HANDLE_STATE_IN);
        param.put("handStFail",Constans.HANDLE_STATE_IN);
        param.put("brno",Constans.SETTL_ACCT_TYPE_CROSS);
        param.put("otherSelType","1");
        param.put("otherSelTypeFail","1");
        return bthMerInAccDao.selectList("selectOtherPreInAccRec", param);
    }






    /**
     * 先更新入账汇总表状态, 再将文件信息插入批量控制文件表
     * @param list
     * @param f
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void updStorage(List<BthMerInAcc> list, BthBatchAccountFile f) {
        Iterator<BthMerInAcc> iterator = list.iterator();
        List<BthMerInAcc> upList=new ArrayList();
        int tempCount=1;
        while (iterator.hasNext()){
            BthMerInAcc next = iterator.next();
            // 只更新处理状态和处理描述 !
            BthMerInAcc record = new BthMerInAcc();
            record.setDateStlm(next.getDateStlm());
            record.setTxnSsn(next.getTxnSsn());

            record.setHandleState(Constans.HANDLE_STATE_IN);
            record.setHandleMark("处理中");
            record.setUpdateTime(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
            // TxnCount 为 number型 , 若为空会默认设成0 ,此处要避免 !!!
            record.setTxnCount(next.getTxnCount());
            upList.add(record);
            if(tempCount%updBatchInsertCount==0)
            {
                bthMerInAccDao.batchUpdateHandleState(upList);
                upList.clear();
            }
            tempCount++;
            //bthMerInAccDao.updateByPrimaryKeySelective(record);

        }
        if(upList!=null&&upList.size()>0)
        {
            bthMerInAccDao.batchUpdateHandleState(upList);
            upList.clear();
        }

        // 再将文件信息插入批量控制文件表
        bthBatchAccountFileDao.insertSelective(f);
    }


    public static SoaParams unifyPay(SoaParams params, BthBatchAccountFile bthBatchAccountFile ) {
        log.info("-----------组装统一支付接口报文开始-----------");

        params.put("payPathCd","1002" );//支付汇路   1001-大额 1002-小额 1003-网银互联 1010-农信银 1110-四川支付 9001-行内支付 2001-智能汇路
        params.put("pltfBizTyp", "A100");//业务类型  A100：普通贷记
        params.put("pltfBizKind", "02102");//业务种类  02102：普通贷记
        params.put("debtCrdtInd", "1");//借贷标识  1-贷 2-借
        params.put("totlCnt", bthBatchAccountFile.getFileCount());//明细总笔数
        params.put("totlAmt", bthBatchAccountFile.getFileAmt().setScale(2));//明细总金额
        params.put("Bat_Doc_Nm", bthBatchAccountFile.getAccFileName());//文件名 S(1位) + 机构号 + 交易日期(8位) + 渠道号 + 渠道流水号 + .txt   机构号9996  渠道号052  渠道流水号20位随机
        log.info("-----------组装统一支付接口报文结束-----------");
        return params;
    }


    /**
     * 初始化批量文件控制表
     * @param fileName
     * @param fileType
     * @param fileSort
     * @param sumFileAmt
     * @param rows
     * @return
     */
    private static BthBatchAccountFile initBthBatchAccountFile(String fileName, String fileType, String fileSort, BigDecimal sumFileAmt, int rows) {
        BthBatchAccountFile accountFile = new BthBatchAccountFile();
        // 主键
        accountFile.setId(IfspId.getUUID(32));
        //记账文件名
        accountFile.setAccFileName(fileName);
        // 他行文件需要统计文件总金额与文件内借贷笔数
        if (Constans.FILE_TYPE_OTHER_MCHT.equals(fileSort)){
            // 记录行数
            accountFile.setFileCount(String.valueOf(rows));
            // 文件总金额
            accountFile.setFileAmt(sumFileAmt.setScale(2));
        }
        accountFile.setFileType(fileType);
        accountFile.setDealStatus(Constans.FILE_DEAL_STATUS_NOT);
        accountFile.setGenFileClass("com.scrcu.ebank.ebap.batch.service.impl.GenStlFileGrpServiceImpl");
        // 入账日期
        accountFile.setAccDate(IfspDateTime.getYYYYMMDD());
        // 创建时间
        accountFile.setCreateDate(IfspDateTime.getYYYYMMDDHHMMSS());
        /**
         *  预留字段1 :存的是生成文件种类  00-通道入账文件 01-本行商户入账文件 02-手续费入账文件 03-他行商户入账文件
         *  方便调用结算接口按照实际的划账顺序入账
         */
        accountFile.setReserved1(fileSort);
        return accountFile;

    }


    /**
     * 生成核心记账文件文件名
     * @param type ：文件类型，F-一级商户结算文件，S-二级商户结算文件,C:对账文件,G：补足保证金结算文件
     * 				 CC - 信用卡对账文件, U-统一支付记账文件
     * @return
     */
    public static synchronized String genFileName(String type){
        String fileName = "";
        String fileType = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timeStamp = sdf.format(new Date());


        if("C".equals(type))
        {
            fileName = "052"+timeStamp+"RPS001"+"_check.txt";
        }
        else if("CC".equals(type))
        {
            fileName = "CREDIT-CHK-FILE-052-"+timeStamp+".TXT";
        }
        else if("U".equals(type))
        {
            //S(1位) + 渠道号 + 交易日期(8位) + 渠道流水号 + .txt
            //16 - 31 位为流水号
            fileName = "S052"+timeStamp.substring(0, 8)+"RPS"+timeStamp.substring(2)+".txt";
        }
        else if("G".equals(type))
        {
            //文件传输平台目前只支持类型为F/S的结算文件的传输，为避免文件传输平台的改造，这里使用固定值F
            //只是文件名的长度与一级商户结算文件不同
            fileName = "RPS"+"052"+"F"+timeStamp.substring(0, 14)+fileType;
        }
        else if("ST".equals(type))
        {
            //生成一级商户转账文件（用于二级商户手动结算：文件方式）
            fileName = "RPS"+"052"+"S"+timeStamp.substring(0, 14)+fileType;
        }
        else if("RT".equals(type))
        {
            //生成二级商户结算余额回退（用于二级商户手动结算：文件方式）
            fileName = "RPS"+"052"+"S"+timeStamp.substring(0, 15)+fileType;
        }
        else
        {
            fileName = "RPS"+"052"+type+timeStamp+fileType;
        }


        return fileName;
    }




    /**
     * 创建空文件
     * @author caog
     */
    public static void createFile(String fileName){

        String charSet = "UTF-8";
        try(FileOutputStream fos = new FileOutputStream(fileName);
        OutputStreamWriter osw = new OutputStreamWriter(fos,charSet);
                BufferedWriter bw =  new BufferedWriter(osw)) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 指定账户不支持转出金额为负数
     * @param bthMerInAcc
     */
    public void converObj(BthMerInAcc bthMerInAcc){
        //如果借方是电子银行补贴科目 , 则不管金额正负, 都不反转借贷(应核心报表要求)
        if(bthMerInAcc.getOutAcctNo().contains("53110023")){
            return;
        }

        //原账户信息
        String outOrg = bthMerInAcc.getOutAcctNoOrg();
        String outAccNo = bthMerInAcc.getOutAcctNo();
        String lendFlag = bthMerInAcc.getLendFlag();     //指定账户

        String inOrg = bthMerInAcc.getInAcctNoOrg();
        String inAccNo = bthMerInAcc.getInAcctNo();
        String borrowFlag = bthMerInAcc.getBorrowFlag();

        // 转出账户
        bthMerInAcc.setOutAcctNoOrg(inOrg);
        bthMerInAcc.setOutAcctNo(inAccNo);
        bthMerInAcc.setLendFlag(borrowFlag);        //指定账户

        // 转入账户
        bthMerInAcc.setInAcctNoOrg(outOrg);
        bthMerInAcc.setInAcctNo(outAccNo);
        bthMerInAcc.setBorrowFlag(lendFlag);        //指定账户
        // 将金额变为正
        bthMerInAcc.setInAcctAmt((new BigDecimal(bthMerInAcc.getInAcctAmt()).multiply(new BigDecimal(-1))).toString());

        // 账户名(入账文件中暂未使用)
        String outAcctName = bthMerInAcc.getOutAcctName();
        String inAcctName = bthMerInAcc.getInAcctName();
        bthMerInAcc.setOutAcctName(inAcctName);
        bthMerInAcc.setInAcctName(outAcctName);


    }

    /**
     * 拼接文件内容
     * @param sb
     * @param bthMerInAcc
     * @param flag  0-本行 1-他行
     * @param i
     */
    public void appendTxt(StringBuffer sb,BthMerInAcc bthMerInAcc,String flag,int i){

        String desc ;
        switch (bthMerInAcc.getInAcctType()){
            case Constans.IN_ACCT_TYPE_PAGY:
                desc = "通道入账";
                break;
            case Constans.IN_ACCT_TYPE_MCHT:
                desc = "商户入账";
                break;
            case Constans.IN_ACCT_TYPE_FEE:
                desc = "手续费入账支出";
                break;
            case Constans.IN_ACCT_TYPE_GUARANTEE:
                desc = "保证金入账";
                break;
            case Constans.IN_ACCT_TYPR_COMMISSION:
                desc = "商户返佣";
                break;
            case Constans.IN_ACCT_TYPE_DAY_FAIL:
                desc = "日间记账失败入账";
                break;
            case Constans.IN_ACCT_TYPE_FEE_2:
                desc = "手续费入账收入";
                break;
            default:
                desc = "" ;
        }


        // 本行
        if ("0".equals(flag)){
            String outAcctNoOrg = bthMerInAcc.getOutAcctNoOrg();
            String inAcctNoOrg = bthMerInAcc.getInAcctNoOrg();
            String tranOrg = IfspDataVerifyUtil.isBlank(outAcctNoOrg) ? (IfspDataVerifyUtil.isBlank(inAcctNoOrg) ? "9996" : inAcctNoOrg) : outAcctNoOrg;
            sb.append(tranOrg).append("|##|")// 机构   该机构作为分录的交易机构
                    .append("4201").append("|##|")// 费用编号      默认

                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getBorrowFlag())?"":bthMerInAcc.getBorrowFlag()).append("|##|")// 借方标志  1- 指定账户   2-手续费待分配  3-手续费支出  ??
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getOutAcctNoOrg())?"":bthMerInAcc.getOutAcctNoOrg()).append("|##|")// 借方机构
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getOutAcctNo())?"":bthMerInAcc.getOutAcctNo()).append("|##|")// 借方账号


                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getLendFlag())?"":bthMerInAcc.getLendFlag()).append("|##|")// 贷方标志  1- 指定账户  2-手续费待分配      3-手续费收入
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getInAcctNoOrg())?"":bthMerInAcc.getInAcctNoOrg()).append("|##|")// 贷方机构    如果为商户,为空
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getInAcctNo())?"":bthMerInAcc.getInAcctNo()).append("|##|")// 贷方账号  可为客户帐，也可为内部账

                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getInAcctAmt())?"":bthMerInAcc.getInAcctAmt()).append("|##|")// 发生金额  不能为0，可为正数和负数（只能是数字）（可以以+ - 打头）

                    .append("01").append("|##|")// 币种  不输默认为人民币01
                    .append("").append("|##|")// 钞汇标志
                    .append("200").append("|##|")// 摘要码
                    .append(desc).append("|##|")// 摘要     商户入账/手续费分润
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getTxnSsn())?"":bthMerInAcc.getTxnSsn()).append("|##|") ; // reserved1 备用字段，收单送唯一索引以便根据结果更新状态
        }else { // 他行文件
            // 手续费
            String handFee = IfspDataVerifyUtil.isBlank(bthMerInAcc.getMisc2())? "0":bthMerInAcc.getMisc2();

            sb.append(i).append("|#|") // 交易序号 m

                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getOutAcctNo())?"":bthMerInAcc.getOutAcctNo()).append("|#|")// 付款人账号 m
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getOutAcctName())?"":bthMerInAcc.getOutAcctName()).append("|#|")// 付款人户名 m
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getOutAcctNoOrg())?"":bthMerInAcc.getOutAcctNoOrg()).append("|#|")// 付款人机构 m

                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getInAcctNo())?"":bthMerInAcc.getInAcctNo()).append("|#|")// 收款人账号 m
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getInAcctName())?"":bthMerInAcc.getInAcctName()).append("|#|")// 收款人名称 m
                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getInAcctNoOrg())?"":bthMerInAcc.getInAcctNoOrg()).append("|#|")// 收款人行行号/机构号 m

                    .append("").append("|#|")// 币种  m  样例文件值为空

                    .append(IfspDataVerifyUtil.isBlank(bthMerInAcc.getInAcctAmt())?"":bthMerInAcc.getInAcctAmt()).append("|#|")// 交易金额 m

                    .append("1").append("|#|")// 收费现转标志 0-现金 1-转账 m
                    .append("4201").append("|#|")// 费用编号 c
                    // 手续费金额 c
                    .append(handFee).append("|#|")
                    .append("200").append("|#|")// 摘要码 c
                    .append(desc).append("|#|")  // 摘要 c
                    .append("1").append("|#|")   // 行内行外标志  0-行内, 1-行外 m
                    .append(bthMerInAcc.getTxnSsn()).append("|#|")   // 备用字段  c
                    .append("").append("|#|") ; // 备用字段  c
        }

    }




}
