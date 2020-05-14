package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSysParamInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.AntiFraudSwitchEnum;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.batch.dao.BthSysParamInfoDao;
import com.scrcu.ebank.ebap.batch.service.AntiFraudService;
import com.scrcu.ebank.ebap.batch.soaclient.AntiFraudSoaService;
import com.scrcu.ebank.ebap.batch.task.AntiFraudTask;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 反欺诈服务 :开关控制是否调用反欺诈
 * 范围 : 新增的未处理的,需要入账的商户
 *
 * @author ljy
 * @date 2018-12-25 16:26
 */
@Service("antiFraudService")
@Slf4j
public class AntiFraudServiceImpl implements AntiFraudService {

    /**
     * 跑批日
     */
    private String batchDate;

    /**
     * 交易日
     */
    private String txnDate;

    /**
     * 商户汇总表数据访问对象
     */
    @Resource
    private BthMerInAccDao bthMerInAccDao;

    /**
     * 系统参数配置表
     */
    @Resource
    private BthSysParamInfoDao bthSysParamInfoDao;

    /**
     * 反欺诈服务
     */
    @Resource
    private AntiFraudSoaService antiFraudSoaService ;

    /**
     * 数据分页个数(初始化线程池时初始化)
     */
    private  int PAGE_SIZE ;

    /**
     * 反欺诈决策拒绝
     */
    private static final String AF_REFUSE = "2" ;


    /**
     * 线程池
     */
    ExecutorService executor;


    @Override
    public CommonResponse merInAccAntiFraud() {

        CommonResponse response = new CommonResponse();

        // 开关决定是否调用反欺诈 ,开关配置在数据库
        BthSysParamInfo flag = bthSysParamInfoDao.selectByParamCode("ANTI_FRAUD_FLAG");

        // 反欺诈开关打开 , 则调用反欺诈服务
        if (AntiFraudSwitchEnum.ANTI_FRAUD_SWITCH_ON.getCode().equals(flag.getParamInfo())){
            log.info("=============================>>  反欺诈调用开关为打开状态  , 调用反欺诈接口dataVisor start <<============================= ");

            /*
             * 初始化线程池与每个线程处理数
             */
            initPool();

            CountDownLatch cdl ;

            try {
                // 统计总条数
                int count = this.merAntiFraudCnt();
                // 分页数
                int pageCount = (int)Math.ceil((double) count / PAGE_SIZE);

                // 线程计数器
                cdl = new CountDownLatch(pageCount);

                log.info("待调反欺诈的入账汇总的条数为 : " + count + ", so the pageCount is : " +pageCount );
                int startIdx = 1;
                int endIdx ;

                // 分好页  ,一页  PAGE_SIZE  条  ,然后各页之间是多线程的  ,  每页各条的处理是单线程的 !!!!
                for (int pageIdx = 0; pageIdx < pageCount; pageIdx ++) {
                    //分页处理数据
                    endIdx = startIdx + PAGE_SIZE;
                    List<BthMerInAcc> recordList = this.merAntiFraudList(startIdx,endIdx);
                    AntiFraudTask antiTask = new AntiFraudTask(recordList,cdl,bthMerInAccDao,antiFraudSoaService);
                    executor.execute(antiTask);
                    startIdx += PAGE_SIZE;

                }

                // 等待其他线程执行
                cdl.await();
            } catch (Exception e) {
                log.error("调用上送反欺诈dataVisor接口异常....",e);
                response.setRespCode(RespConstans.RESP_FAIL.getCode());
                response.setRespMsg(RespConstans.RESP_FAIL.getDesc());
            } finally {
                //销毁线程池
                destoryPool();
            }

            log.info("=============================>>   调用反欺诈 end <<============================= ");
        }else {
            log.info("=============================>>  反欺诈调用开关为关闭状态  , 调用结束 <<============================= ");
        }

        return response;
    }

    /**
     * 初始化线程池与每个线程处理数
     */
    private void initPool() {
        destoryPool();

        //查询库里的线程数
        BthSysParamInfo threadCountBean = bthSysParamInfoDao.selectByParamCode("ANTI_FRAUD_THREAD_COUNT");

        //默认200线程 , 线程容量为 50
        String threadInfo = "200,50";
        if (IfspDataVerifyUtil.isNotBlank(threadCountBean)){
            threadInfo = threadCountBean.getParamInfo();
        }

        String[] split = threadInfo.split(",");

        // 线程数
        int threadCount = Integer.parseInt(split[0]);
        // 线程容量
        PAGE_SIZE =  Integer.parseInt(split[1]);
        log.info("====反欺诈数据库配置的线程数为:[{}] , 每个线程处理数量为:[{}]====",threadCount,PAGE_SIZE);


        /*
         * 构建
         */
        log.info("====初始化线程池(start)====");
        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
            AtomicInteger atomic = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "antiFraudInAccHander_" + this.atomic.getAndIncrement());
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
        }else {
            log.info("线程池为null, 无需清理");
        }
        log.info("====销毁线程池(end)====");
    }


    /**
     *
     * 统计待调反欺诈的入账汇总的数据（从入账汇总表取数） , 用于进行分页处理
     */
    private int merAntiFraudCnt()
    {
        Map<String,Object> params = new HashMap<>(3);
        params.put("inAccType",Constans.IN_ACCT_TYPE_MCHT);
        params.put("handleState",Constans.HANDLE_STATE_PRE);
        params.put("handleState1",Constans.HANDLE_STATE_FAIL);
        return bthMerInAccDao.count("merAntiFraudCount", params);
    }



    /***
     * 统计待调反欺诈的入账汇总的数据，从入账汇总表取数
     * @param startIdx : 分页起始行
     * @param endIdx ：  分页结束行
     * @return record
     */
    private List<BthMerInAcc> merAntiFraudList(int startIdx, int endIdx)
    {
        // 查询出未处理的商户入账记录
        Map<String, Object>  unDealtMerInAcc = new HashMap<>(5);
        unDealtMerInAcc.put("inAccType",Constans.IN_ACCT_TYPE_MCHT);
        unDealtMerInAcc.put("handleState",Constans.HANDLE_STATE_PRE);
        unDealtMerInAcc.put("handleState1",Constans.HANDLE_STATE_FAIL);
        unDealtMerInAcc.put("startIdx",startIdx);
        unDealtMerInAcc.put("endIdx",endIdx);
        return bthMerInAccDao.selectList("merAntiFraudRecord", unDealtMerInAcc);
    }








    @Override
    public CommonResponse merInAccRstAntiFraud(BatchRequest request) {

        CommonResponse response = new CommonResponse();

        String batchDate = request.getSettleDate();
        if(IfspDataVerifyUtil.isBlank(batchDate))
        {
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
            txnDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
        }


        // 开关决定是否调用反欺诈
        BthSysParamInfo flag = bthSysParamInfoDao.selectByParamCode("ANTI_FRAUD_FLAG");

        // 反欺诈开关打开 , 则调用反欺诈服务
        if (AntiFraudSwitchEnum.ANTI_FRAUD_SWITCH_ON.getCode().equals(flag.getParamInfo())) {
            log.info("=============================>>  反欺诈调用开关为打开状态  , 调用上送反欺诈结果接口dataVisorNotify start <<============================= ");


            /*
             * 初始化线程池
             */
            initPool();

            CountDownLatch cdl ;

            try {
                // 统计总条数
                int count = this.merAntiFraudResCnt(batchDate);
                // 分页数
                int pageCount = (int)Math.ceil((double) count / PAGE_SIZE);

                // 线程计数器
                cdl = new CountDownLatch(pageCount);

                log.info("已调过反欺诈的入账汇总数条为 : " + count + ", so the pageCount is : " +pageCount );
                int startIdx = 1;
                int endIdx ;

                // 分好页  ,一页  PAGE_SIZE  条  ,然后各页之间是多线程的  ,  每页各条的处理是单线程的 !!!!
                for (int pageIdx = 0; pageIdx < pageCount; pageIdx ++) {
                    //分页处理数据
                    endIdx = startIdx + PAGE_SIZE;

                    List<BthMerInAcc> recordList = this.merAntiFraudResList(startIdx,endIdx,batchDate);

                    AntiFraudResTask antiTask = new AntiFraudResTask(recordList,cdl);

                    executor.execute(antiTask);
                    startIdx += PAGE_SIZE;

                }

                // 等待其他线程执行
                cdl.await();
            } catch (Exception e) {
                log.error("调用上送反欺诈结果接口dataVisorNotify异常....",e);
                response.setRespCode(RespConstans.RESP_FAIL.getCode());
                response.setRespMsg(RespConstans.RESP_FAIL.getDesc());
            } finally {
                // 回收资源
                destoryPool();
            }


        }else {
            log.info("=============================>>  反欺诈调用开关为关闭状态  , 调用结束 <<============================= ");
        }

        return response;
    }

    /**
     * 统计已调过反欺诈的入账汇总的数据（从入账汇总表取数） , 用于进行分页处理
     * @param batchDate 跑批日
     * @return count
     */
    private int merAntiFraudResCnt(String batchDate)
    {
        Map<String,Object> params = new HashMap<>(2);
        params.put("startDate",DateUtil.getDate(batchDate));
        params.put("endDate",DateUtil.getAfterDate(batchDate,1));
        return bthMerInAccDao.count("merAntiFraudResCount", params);
    }



    /***
     * 统计已调反欺诈的入账汇总的数据，从入账汇总表取数
     * @param startIdx : 分页起始行
     * @param endIdx ：  分页结束行
     * @param batchDate 跑批日
     * @return record
     */
    private List<BthMerInAcc> merAntiFraudResList(int startIdx, int endIdx, String batchDate)
    {
        // 查询出未处理的商户入账记录
        Map<String, Object>  unDealtMerInAcc = new HashMap<>(4);
        unDealtMerInAcc.put("startDate",DateUtil.getDate(batchDate));
        unDealtMerInAcc.put("endDate",DateUtil.getAfterDate(batchDate,1));
        unDealtMerInAcc.put("startIdx",startIdx);
        unDealtMerInAcc.put("endIdx",endIdx);
        return bthMerInAccDao.selectList("merAntiFraudResRecord", unDealtMerInAcc);
    }


    /**
     * 上送反欺诈结果任务
     */
    class AntiFraudResTask implements Runnable{

        /**
         * 汇总表实体
         */
        private List<BthMerInAcc> bthMerInAccList;

        /**
         * 线程计数器
         */
        private CountDownLatch cdl;


        private AntiFraudResTask (List<BthMerInAcc> bthMerInAccList , CountDownLatch cdl){
            this.bthMerInAccList = bthMerInAccList ;
            this.cdl = cdl ;

        }

        @Override
        public void run() {
            for (BthMerInAcc bthMerInAcc : bthMerInAccList) {
                SoaParams reqParam = initReqParam(bthMerInAcc);
                SoaResults results = antiFraudSoaService.dataVisorNotify(reqParam);

                // 不关心反欺诈结果 ,记录一下返回报文
                BthMerInAcc record = new BthMerInAcc();
                //联合主键
                record.setDateStlm(bthMerInAcc.getDateStlm());
                record.setTxnSsn(bthMerInAcc.getTxnSsn());
                // TxnCount 为 number型 , 若为空会默认设成0 ,此处要避免 !!!
                record.setTxnCount(bthMerInAcc.getTxnCount());
                // 上送反欺诈结果返回报文
                record.setAntiFraudResData(results.toString());
                // 最后更时间
                record.setUpdateTime(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                bthMerInAccDao.updateByPrimaryKeySelective(record);

            }

            cdl.countDown();
        }


        /**
         * 组装上送反欺诈结果报文
         * @param bthMerInAcc 商户入账汇总表对象
         * @return paramMap
         */
        private SoaParams initReqParam(BthMerInAcc bthMerInAcc) {
            SoaParams paramMap = new SoaParams();

            // 传商户号
            paramMap.put("accountLogin", bthMerInAcc.getChlMerId());
            // 传商户号
            paramMap.put("merchantNo", bthMerInAcc.getChlMerId());

            // 入账金额
            paramMap.put("tradeAmount",bthMerInAcc.getInAcctAmt());

            // 请求流水号
            paramMap.put("visorRespSsn", bthMerInAcc.getAntiFraudRespSsn());

            // 交易类型
            paramMap.put("visorTxnType","trade_mkcol");

            // 风险决策结果
            String antiFraudFinalDecision = bthMerInAcc.getAntiFraudFinalDecision();
            paramMap.put("finalDecision", antiFraudFinalDecision);

            /*决策结果:  0-通过，1-增强认证，2-拒绝
            如果之前的决策结果为拒绝 ,则该流水没有去入账 ,默认状态为失败 ; 否则看实际的入账状态
            结算状态为成功不需要传失败码 与失败原因*/
            if (AF_REFUSE.equals(antiFraudFinalDecision)){
                // 结算状态  0 成功，1 失败
                paramMap.put("state","1");
                // 失败码
                paramMap.put("pagyRespCode","1002");
                // 失败原因
                paramMap.put("pagyRespMsg","上送反欺诈被拒绝");

            }else {
                // 入账状态
                String inAcctStat = bthMerInAcc.getInAcctStat();
                if (Constans.IN_ACC_STAT_SUCC.equals(inAcctStat)){
                    // 结算状态  0 成功，1 失败
                    paramMap.put("state","0");

                }else {
                    // 结算状态  0 成功，1 失败
                    paramMap.put("state","1");
                    // 失败码
                    paramMap.put("pagyRespCode",bthMerInAcc.getCoreRespCode());
                    // 失败原因
                    paramMap.put("pagyRespMsg",bthMerInAcc.getCoreRespMsg());

                }

            }

            return paramMap;

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



