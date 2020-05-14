package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.constant.IfspConstants;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSysParamInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.GenerateStlFileRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.msg.IBankMsg;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.GenOtherStlService;
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
 *  任务名称:他行入账
 *
 *  从商户入账汇总表取数,(HANDLE_STATE 为 0 与 3)
 *
 * @author ljy
 */
@Slf4j
@Service
public class GenOtherStlServiceImpl implements GenOtherStlService {


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
    private List<BthMerInAcc> getOtherPreInAccRec(int minIndex,int maxIndex)
    {
        Map<String,Object> param = new HashMap<>();
        param.put("handStPre",Constans.HANDLE_STATE_PRE);
        param.put("handStFail",Constans.HANDLE_STATE_FAIL);
        param.put("brno",Constans.SETTL_ACCT_TYPE_CROSS);
        param.put("otherSelType","0");
        param.put("otherSelTypeFail","3");
        param.put("minIndex",minIndex);
        param.put("maxIndex",maxIndex);
        return bthMerInAccDao.selectList("selectOtherPreInAccRecBatch", param);
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


    @Override
    public CommonResponse otherSel(GenerateStlFileRequest request) {
        long start = System.currentTimeMillis();
        CommonResponse response = new CommonResponse();
        log.info("==========================他行入账start===============================");
        /**
         *
         * 清理他行临时表;
         */
        cleaOther();
        this.init();
        log.info("初始化中间表，耗时【{}】", System.currentTimeMillis() - start);
        //初始化线程池
        initPool();

        try
        {
            //处理结果
            List<Future> futureList = new ArrayList<>();
            List<BthMerInAcc> bthMerInAccList=null;
            List<BthMerInAcc> subList=new ArrayList<>();
            int pageIdx=1;
            int retry=1;
            // 汇总清分表
            Map<String, Object> map = new HashMap<>();
            while(true) {
                int minIndex = (pageIdx - 1) *threadCapacity * threadCount + 1;  //这里乘以线程数threadCount，一次多搂点
                int maxIndex = pageIdx *threadCapacity * threadCount;
                long queryStart=System.currentTimeMillis();
                bthMerInAccList=getOtherPreInAccRec(minIndex,maxIndex);
                if(bthMerInAccList==null||bthMerInAccList.size()==0)
                {
                    log.info("待处理结果集bthMerInAccList为空，循环退出，pageIdx【{}】，minIndex【{}】，maxIndex【{}】",pageIdx,minIndex,maxIndex);
                    break;
                }
                log.info("处理主线程查询待分片处理结果集，数量【{}】，本次查询耗时【{}】",bthMerInAccList.size(),System.currentTimeMillis()-queryStart);
                if(pageIdx%10==0)
                {
                    System.gc();
                }
                log.info("处理第[{}]组数据", pageIdx);
                pageIdx++;

                int threadCapacityTemp=1;
                //拆成小块分给每个子线程
                for(Iterator<BthMerInAcc> it=bthMerInAccList.iterator();it.hasNext();)
                {
                    subList.add(it.next());
                    it.remove();

                    if(threadCapacityTemp%threadCapacity==0)
                    {
                        Future future = executor.submit(new Handler(subList));
                        futureList.add(future);

                        subList=new ArrayList<>();
                    }
                    threadCapacityTemp++;
                }

                if(subList!=null&&subList.size()>0)
                {
                    Future lastFuture = executor.submit(new Handler(subList));
                    futureList.add(lastFuture);
                }
            }


            if(bthMerInAccList!=null)
            {
                bthMerInAccList.clear();
            }


            /**
             * 获取处理结果
             */
            for (Future future : futureList)
            {
                try
                {
                    future.get(30, TimeUnit.MINUTES);
                }
                catch (Exception e)
                {
                    log.error("对账线程处理异常: ", e);
                    //取消其他任务
                    destoryPool();
                    log.warn("其他子任务已取消.");
                    //返回结果
                    throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常" + e.getMessage());
                }
            }
        }
        finally
        {
            //释放线程池
            destoryPool();
        }
//        SoaResults result = null;
//        // 待入账记录 , 未排序 (他行)
//        List<BthMerInAcc> bthMerInAccList = getOtherPreInAccRec();
//        SoaParams params = new SoaParams();
//        Map<String,Object> map = new HashMap();
//        try{
//        for (BthMerInAcc bthMerInAcc : bthMerInAccList) {
//
//            // 过滤发生金额为0的记录  (不会出现 ,此处预防)
//            if (BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt()))==0){
//                //直接更新为成功
//                Map<String,Object> mapAcc = new HashMap();
//                //更新汇总状态
//                mapAcc.put("handState",Constans.HANDLE_STATE_SUCC);//01
//                mapAcc.put("handlemark","处理成功");//处理中
//                mapAcc.put("otherSelType",Constans.OTHER_SEL_TYPE_IN_ACC_SUCC);//已受理
//                mapAcc.put("inAcctStat",Constans.IN_ACC_STAT_SUCC);//入账成功
//                mapAcc.put("inAcctTime",IfspDateTime.getYYYYMMDD());//入账成功
//                mapAcc.put("txnSsn",bthMerInAcc.getTxnSsn());
//                //更新受理结果
//                bthMerInAccDao.update("updateOtherMerInAcc",mapAcc);
//                //更新清分明细
//                Map<String,Object> mapDetail = new HashMap();
//                mapDetail.put("dealResult",Constans.DEAL_RESULT_SUCCESS);//
//                mapDetail.put("accountStauts",Constans.ACCOUNT_STATUS_SUCCESS);//
//                mapDetail.put("dealRemark","处理成功");//处理成功
//                mapDetail.put("capBatchNo",bthMerInAcc.getBatchNo());//
//                mapDetail.put("merId",bthMerInAcc.getChlMerId());//
//                //更新受理结果
//                bthSetCapitalDetailDao.update("updateCapDetailMerOther",mapDetail);
//                //更新入账明细
//                Map<String,Object> mapAccDtl = new HashMap();
//                mapAccDtl.put("inAcctDate",IfspDateTime.getYYYYMMDD());//
//                mapAccDtl.put("inAcctStat",Constans.IN_ACC_STAT_SUCC);//
//                mapAccDtl.put("stlStatus",Constans.SETTLE_STATUS_SUCCESS_CLEARING);//
//                mapAccDtl.put("capBatchNo",bthMerInAcc.getBatchNo());//
//                mapAccDtl.put("merId",bthMerInAcc.getChlMerId());//
//                mapAccDtl.put("inAcctDate",IfspDateTime.getYYYYMMDD());
//                //更新受理结果
//                bthMerInAccDtlDao.update("updateByCapBatchNoOther",mapAccDtl);
//                continue;
//            }
//
//            // 指定账户不支持转出金额为负数  将借贷关系反转   (借方是商户待清算且入账金额为负数时, 一般对应的贷方是商户结算账户  ,故 LendFlag 一定是指定账户[1] ,此处能反转  )
//            // todo 是否存在借方是商户待清算 ,而贷方账户为空且入账金额为负数的情况 ??  存在会导致当日入账余额不足问题 , 次日会自动成功
//            if (BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt()))>0 && Constans.BORROW_ALLOCATED_ACCT.equals(bthMerInAcc.getLendFlag())){
//                converObj(bthMerInAcc);
//            }
//            // 调用统一支付接口
//            params = IBankMsg.unifyPayOtherSel(params, bthMerInAcc);
//            log.info("他行unifyPay接口参数:" + params);
//            result = clearingSoaService.unifyPayOtherSel(params);
//            log.info("调用他行unifyPay口参数返回的报文>>>>>>>>>>>>" + result);
//            if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode")))
//            {
//                throw new IfspBizException(RespConstans.RESP_FAIL.getCode(),
//                        RespConstans.RESP_FAIL.getDesc());
//            }
//
//            if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
//                    RespConstans.RESP_SUCCESS.getCode()))
//            {
//                //respMsg":"UPP001|无可用汇路"CORE_RESP_CODE IN_ACCT_STAT
//                log.info("=====================调用他行unifyPay失败================================");
//                map.put("handState",Constans.HANDLE_STATE_FAIL);//01
//                map.put("handlemark","处理失败");//处理失败
//                map.put("otherSelType",Constans.OTHER_SEL_TYPE_IN_FLAG);//受理失败
//                map.put("coreRespMsg",((String) result.get("respMsg")).split("\\|")[1]);
//                map.put("coreRespCode",((String) result.get("respMsg")).split("\\|")[0]);
//                map.put("statMark",((String) result.get("respMsg")).split("\\|")[1]);
//                map.put("txnSsn",bthMerInAcc.getTxnSsn());
//                //更新受理结果
//                bthMerInAccDao.update("updateOtherMerInAcc",map);
//                continue;
//            }
//            else
//            {
//                log.info("=====================调用他行unifyPay成功================================");
//                map.put("handState",Constans.HANDLE_STATE_IN);//01
//                map.put("handlemark","处理中");//处理中
//                map.put("otherSelType",Constans.OTHER_SEL_TYPE_IN_SUCC);//已受理
//                map.put("txnSsn",bthMerInAcc.getTxnSsn());
//                //更新受理结果
//                bthMerInAccDao.update("updateOtherMerInAcc",map);
//            }
//
//
//        }
//        }catch (IfspBaseException e) {
//            log.error("处理失败:", e.getCode() + "|" + e.getMessage());
//            response.setRespCode(e.getCode());
//            response.setRespMsg(e.getMessage());
//        } catch (Exception e) {
//            log.error("未知错误:", e);
//            response.setRespCode(SystemConfig.getSysErrorCode());
//            response.setRespMsg(SystemConfig.getSysErrorMsg());
//        }

        return response;
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






    @Override
    public CommonResponse otherSelUpdateState(GenerateStlFileRequest request) {

        // 响应报文
        CommonResponse response = new CommonResponse();
        //循环查询入账结果并更新
        /*  */
        String spaceFlag = "300000,3";

        // 数据库配置的参数信息
        BthSysParamInfo bthSysParamInfo = bthSysParamInfoDao.selectByParamCode("OTHER_ACC_CHK");
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
           //查询他行结果
            List<BthMerInAcc> bthMerInAccList = getOtherPreInAccRecState();
            SoaParams params = new SoaParams();
            for (BthMerInAcc bthMerInAcc : bthMerInAccList) {

                // 过滤发生金额为0的记录  (不会出现 ,此处预防)
                if (BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt())) == 0) {
                    continue;
                }


                //调用统一支付查询
                SoaResults result = null;
                params = IBankMsg.unifyPayOtherSel(params, bthMerInAcc);
                log.info("他行unifyPay查询接口参数:" + params);
                result = clearingSoaService.unifyPayQue(params);
                log.info("调用他行unifyPay查询口参数返回的报文>>>>>>>>>>>>" + result);
                if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode")))
                {
                    throw new IfspBizException(RespConstans.RESP_FAIL.getCode(),
                            RespConstans.RESP_FAIL.getDesc());
                }

                if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
                        RespConstans.RESP_SUCCESS.getCode()))
                {
                    log.info("=====================调用他行unifyPay查询失败================================");

                    continue;
                }
                else
                {
                    log.info("=====================调用他行unifyPay查询成功================================");
//                    "0-失败
//                    1-成功
//                    3-交易处理中
//                    4-无此交易
//                    5-次日到帐已退回客户帐"

                    if(IfspDataVerifyUtil.equals((String) result.get("bizCurrStat"),"0")||IfspDataVerifyUtil.equals((String) result.get("bizCurrStat"),"5")){//"0-失败
                        Map<String,Object> mapAcc = new HashMap();
                        //更新汇总状态
                        mapAcc.put("handState",Constans.HANDLE_STATE_FAIL);//01
                        mapAcc.put("handlemark","处理失败");//处理
                        mapAcc.put("otherSelType",Constans.OTHER_SEL_TYPE_IN_ACC_FLAG);//
                        mapAcc.put("inAcctStat",Constans.IN_ACC_STAT_FAIL);//
                        //mapAcc.put("inAcctTime",IfspDateTime.getYYYYMMDD());//入账成功
                        mapAcc.put("txnSsn",bthMerInAcc.getTxnSsn());
                        //更新受理结果
                        bthMerInAccDao.update("updateOtherMerInAcc",mapAcc);
                        //更新清分明细
                        Map<String,Object> mapDetail = new HashMap();
                        mapDetail.put("dealResult",Constans.DEAL_RESULT_FAILE);//
                        mapDetail.put("accountStauts",Constans.ACCOUNT_STATUS_FAILE);//
                        mapDetail.put("dealRemark","处理失败");//原因
                        mapDetail.put("capBatchNo",bthMerInAcc.getBatchNo());//
                        mapDetail.put("merId",bthMerInAcc.getChlMerId());//
                        //更新受理结果
                        bthSetCapitalDetailDao.update("updateCapDetailMerOther",mapDetail);
                        //更新入账明细
                        Map<String,Object> mapAccDtl = new HashMap();
                        // mapAccDtl.put("inAcctDate",IfspDateTime.getYYYYMMDD());//
                        mapAccDtl.put("inAcctStat",Constans.IN_ACC_STAT_FAIL);//
                        mapAccDtl.put("stlStatus",Constans.SETTLE_STATUS_FAILE_CLEARING);//
                        mapAccDtl.put("capBatchNo",bthMerInAcc.getBatchNo());//
                        mapAccDtl.put("merId",bthMerInAcc.getChlMerId());//
                        //更新受理结果
                        bthMerInAccDtlDao.update("updateByCapBatchNoOther",mapDetail);
                    }else if(IfspDataVerifyUtil.equals((String) result.get("bizCurrStat"),"1")){//1-成功

                        Map<String,Object> mapAcc = new HashMap();
                        //更新汇总状态
                        mapAcc.put("handState",Constans.HANDLE_STATE_SUCC);//01
                        mapAcc.put("handlemark","处理成功");//处理中
                        mapAcc.put("otherSelType",Constans.OTHER_SEL_TYPE_IN_ACC_SUCC);//已受理
                        mapAcc.put("inAcctStat",Constans.IN_ACC_STAT_SUCC);//入账成功
                        mapAcc.put("inAcctTime",IfspDateTime.getYYYYMMDD());//入账成功
                        mapAcc.put("txnSsn",bthMerInAcc.getTxnSsn());
                        //更新受理结果
                        bthMerInAccDao.update("updateOtherMerInAcc",mapAcc);
                        //更新清分明细
                        Map<String,Object> mapDetail = new HashMap();
                        mapDetail.put("dealResult",Constans.DEAL_RESULT_SUCCESS);//
                        mapDetail.put("accountStauts",Constans.ACCOUNT_STATUS_SUCCESS);//
                        mapDetail.put("dealRemark","处理成功");//处理成功
                        mapDetail.put("capBatchNo",bthMerInAcc.getBatchNo());//
                        mapDetail.put("merId",bthMerInAcc.getChlMerId());//
                        //更新受理结果
                        bthSetCapitalDetailDao.update("updateCapDetailMerOther",mapDetail);
                        //更新入账明细
                        Map<String,Object> mapAccDtl = new HashMap();
                        mapAccDtl.put("inAcctDate",IfspDateTime.getYYYYMMDD());//
                        mapAccDtl.put("inAcctStat",Constans.IN_ACC_STAT_SUCC);//
                        mapAccDtl.put("stlStatus",Constans.SETTLE_STATUS_SUCCESS_CLEARING);//
                        mapAccDtl.put("capBatchNo",bthMerInAcc.getBatchNo());//
                        mapAccDtl.put("merId",bthMerInAcc.getChlMerId());//
                        mapAccDtl.put("inAcctDate",IfspDateTime.getYYYYMMDD());
                        //更新受理结果
                        bthMerInAccDtlDao.update("updateByCapBatchNoOther",mapAccDtl);
                    }else if(IfspDataVerifyUtil.equals((String) result.get("bizCurrStat"),"4")){
                        Map<String,Object> mapAcc = new HashMap();
                        //更新汇总状态
                        mapAcc.put("handState",Constans.HANDLE_STATE_PRE);//01
                        mapAcc.put("handlemark","未处理");//处理中
                        mapAcc.put("otherSelType",Constans.OTHER_SEL_TYPE_IN_FLAG);//已受理
                        mapAcc.put("txnSsn",bthMerInAcc.getTxnSsn());
                    }

                }
            }
            try {
                Thread.sleep(waitInterval);
            } catch (InterruptedException e) {
                log.error("等待下次查询异常...");
                response.setRespCode(SystemConfig.getSysErrorCode());
                response.setRespMsg(e.getMessage());
                return response;
            }
            tryCount -- ;


        } while (tryCount >= 0);

        return response;
    }

    /**
     * 工作线程
     *
     * @param <T>
     */
    class Handler<T> implements Callable<T> {
        private List<BthMerInAcc> bthMerInAccList;

        public Handler(List<BthMerInAcc> bthMerInAccList) {
            this.bthMerInAccList = bthMerInAccList;
        }

        @Override
        public T call() throws Exception {
            try {
                SoaParams params = new SoaParams();
                Map<String, Object> map = new HashMap();
                SoaResults result = null;
                List<String> txnSsnList = new ArrayList<>();
                for (BthMerInAcc bthMerInAcc : bthMerInAccList) {

                    // 过滤发生金额为0的记录  (不会出现 ,此处预防)
                    if (BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt())) == 0) {
                        //直接更新为成功
                        Map<String, Object> mapAcc = new HashMap();
                        //更新汇总状态
                        mapAcc.put("handState", Constans.HANDLE_STATE_SUCC);//01
                        mapAcc.put("handlemark", "处理成功");//处理中
                        mapAcc.put("otherSelType", Constans.OTHER_SEL_TYPE_IN_ACC_SUCC);//已受理
                        mapAcc.put("inAcctStat", Constans.IN_ACC_STAT_SUCC);//入账成功
                        mapAcc.put("inAcctTime", IfspDateTime.getYYYYMMDD());//入账成功
                        mapAcc.put("txnSsn", bthMerInAcc.getTxnSsn());
                        //更新受理结果
                        bthMerInAccDao.update("updateOtherMerInAcc", mapAcc);
                        //更新清分明细
                        Map<String, Object> mapDetail = new HashMap();
                        mapDetail.put("dealResult", Constans.DEAL_RESULT_SUCCESS);//
                        mapDetail.put("accountStauts", Constans.ACCOUNT_STATUS_SUCCESS);//
                        mapDetail.put("dealRemark", "处理成功");//处理成功
                        mapDetail.put("capBatchNo", bthMerInAcc.getBatchNo());//
                        mapDetail.put("merId", bthMerInAcc.getChlMerId());//
                        //更新受理结果
                        bthSetCapitalDetailDao.update("updateCapDetailMerOther", mapDetail);
                        //更新入账明细
                        Map<String, Object> mapAccDtl = new HashMap();
                        mapAccDtl.put("inAcctDate", IfspDateTime.getYYYYMMDD());//
                        mapAccDtl.put("inAcctStat", Constans.IN_ACC_STAT_SUCC);//
                        mapAccDtl.put("stlStatus", Constans.SETTLE_STATUS_SUCCESS_CLEARING);//
                        mapAccDtl.put("capBatchNo", bthMerInAcc.getBatchNo());//
                        mapAccDtl.put("merId", bthMerInAcc.getChlMerId());//
                        mapAccDtl.put("inAcctDate", IfspDateTime.getYYYYMMDD());
                        //更新受理结果
                        bthMerInAccDtlDao.update("updateByCapBatchNoOther", mapAccDtl);
                        continue;
                    }
                    // 调用统一支付接口
                    params = IBankMsg.unifyPayOtherSel(params, bthMerInAcc);
                    log.info("他行unifyPay接口参数:" + params);
                    result = clearingSoaService.unifyPayOtherSel(params);
                    log.info("调用他行unifyPay口参数返回的报文>>>>>>>>>>>>" + result);
                    if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode"))) {
                        throw new IfspBizException(RespConstans.RESP_FAIL.getCode(),
                                RespConstans.RESP_FAIL.getDesc());
                    }

                    if (!IfspDataVerifyUtil.equals((String) result.get("respCode"),
                            RespConstans.RESP_SUCCESS.getCode())) {
                        //respMsg":"UPP001|无可用汇路"CORE_RESP_CODE IN_ACCT_STAT
                        log.info("=====================调用他行unifyPay失败================================");
                        map.put("handState", Constans.HANDLE_STATE_FAIL);//01
                        map.put("handlemark", "处理失败");//处理失败
                        map.put("otherSelType", Constans.OTHER_SEL_TYPE_IN_FLAG);//受理失败
                        map.put("coreRespMsg", ((String) result.get("respMsg")).split("\\|")[1]);
                        //map.put("coreRespCode", ((String) result.get("respMsg")).split("\\|")[0]);
                        map.put("statMark", ((String) result.get("respMsg")).split("\\|")[1]);
                        map.put("txnSsn", bthMerInAcc.getTxnSsn());
                        //更新受理结果
                        bthMerInAccDao.update("updateOtherMerInAcc", map);
                        continue;
                    } else {
                        log.info("=====================调用他行unifyPay成功================================");
//                        map.put("handState", Constans.HANDLE_STATE_IN);//01
//                        map.put("handlemark", "处理中");//处理中
//                        map.put("otherSelType", Constans.OTHER_SEL_TYPE_IN_SUCC);//已受理
//                        map.put("txnSsn", bthMerInAcc.getTxnSsn());
//                        //更新受理结果
//                        bthMerInAccDao.update("updateOtherMerInAcc", map);
                        txnSsnList.add( bthMerInAcc.getTxnSsn());

                    }


                }
                updateOtherMerInAccSu(txnSsnList);
            } catch (IfspBaseException e) {
                log.error("处理失败:", e.getCode() + "|" + e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("未知错误:", e);
                throw e;
            }
            return null;
        }

    }
            /**
             * 初始化线程池
             */
            private void initPool()
            {
                destoryPool();

                /**
                 * 构建线程池
                 */
                log.info("====初始化线程池(start)====");
                executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory()
                {
                    AtomicInteger atomic = new AtomicInteger();

                    @Override
                    public Thread newThread(Runnable r)
                    {
                        return new Thread(r, "genOtherStl_" + this.atomic.getAndIncrement());
                    }
                });
                log.info("====初始化线程池(end)====");

            }

    /**
     * 销毁线程池
     */
    private void destoryPool()
    {
        log.info("====销毁线程池(start)====");
        /*
         * 初始化线程池
         */
        if (executor != null)
        {
            log.info("线程池为null, 无需清理");
            /*
             * 关闭线程池
             */
            try
            {
                executor.shutdown();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                {
                    executor.shutdownNow();
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("awaitTermination interrupted: " + e);
                executor.shutdownNow();
            }
        }
        log.info("====销毁线程池(end)====");
    }

    public void updateOtherMerInAccSu(List<String> txnSsnList){
        if(txnSsnList != null&&txnSsnList.size()>0 ){
            Map<String, Object> map = new HashMap();
            map.put("handState", Constans.HANDLE_STATE_IN);//01
            map.put("handlemark", "处理中");//处理中
            map.put("otherSelType", Constans.OTHER_SEL_TYPE_IN_SUCC);//已受理
            map.put("txnSsnList", txnSsnList);
            //更新受理结果
            bthMerInAccDao.update("updateOtherMerInAccBatch", map);
        }

    }

    /**
     * 清理他行中间表
     *
     *
     *
     */
    private void cleaOther()
    {

        bthMerInAccDao.cleaOther();
    }

    public void init()
    {
        bthMerInAccDao.initOtherData();
    }

}
