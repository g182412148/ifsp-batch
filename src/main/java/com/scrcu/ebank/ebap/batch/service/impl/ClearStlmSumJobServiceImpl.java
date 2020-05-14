package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.ClearRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.*;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.ClearStlmSumJobService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 清分汇总
 * todo 当前未开展他行卡业务 , 只处理结算卡为本行卡的信息, 即 ACCOUNT_TYPE = '0'
 *
 * @author ljy
 */
@Slf4j
@Service
public class ClearStlmSumJobServiceImpl implements ClearStlmSumJobService
{

    /**
     * 商户入账汇总信息
     */
    @Resource
    private BthMerInAccDao bthMerInAccDao;


    /**
     * 商户入账明细信息
     */
    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息

    /**
     * 清分表
     */
    @Resource
    private BthSetCapitalDetailDao bthSetCapitalDetailDao;

    @Resource
    private BthMerTxnFeeDao bthMerTxnFeeDao;

    @Resource
    private MchtOrgRelDao mchtOrgRelDao;               // 商户组织关联信息

    @Resource
    private BthSetCapitalSumDao bthSetCapitalSumDao;

    private static BigDecimal ONE_HUNDRED = new BigDecimal(100);



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


    @Override
    public CommonResponse clearSumJob(ClearRequest clearRequest)
    {
        long start = System.currentTimeMillis();
        log.info("---------------------清分汇总开始---------------------");
        // 1.准备参数
        // 清算日期 (TWS传的是跑批日期 ,不是交易日期  ,和对账任务有区别)
        String stlmDate = clearRequest.getSettleDate();
        if (IfspDataVerifyUtil.isBlank(stlmDate))
        {
            stlmDate = IfspDateTime.getYYYYMMDD();
        }

        log.info("清算时间为： " + stlmDate);


        // 2.定义响应对象
        CommonResponse response = new CommonResponse();


        /**
         * ENTRY_TYPE
         * 01-商户入账(商户本金结算)
         *      分为本行和他行商户
         * 99-商户退款
         *
         * 02-手续费垫付（行内手续费垫付）      暂无使用
         * 03-商户手续费扣帐（商户手续费结算）  暂无使用
         *
         * 04-第三方手续费入账（第三方资金通道的手续费结算）
         *
         * 05-手续费分润（收单机构）
         * 06-手续费分润（发卡行）
         * 07-手续费分润（通存通兑）
         * 08-手续费分润（运营结构）
         * 09-手续费支出（收单机构）
         * 10-手续费支出（发卡行）
         * 11-手续费支出（电子银行中心/运营机构）
         * 12-手续费支出（电子银行补贴手续费支出）
         * 13-手续费支出（电子银行补贴手续费支出）
         *
         * 14-商户佣金收入
         *
         * 98-内部帐补保证金
         *
         */

        //确立批次号
        String capBatchNo = IfspDateTime.getYYYYMMDD();//IfspId.getUUID(32);


        /**
         * 清除清分时保存的缓存信息
         *
         * 目前暂不清除
         */
        clearCache();


        /**
         * 汇总表删除对账日期下所有数据
         * 清分表当天的全量更新为未处理;
         */
        clear(stlmDate, capBatchNo);
        log.info("汇总表删除对账日期下所有数据，清分表当天的全量更新为未处理，耗时【{}】", System.currentTimeMillis() - start);

        this.init(capBatchNo);
        log.info("批量数据初始化完成，耗时【{}】", System.currentTimeMillis() - start);
//        int count = getSumCount(capBatchNo);
//        log.info("统计待汇总的条数【{}】，耗时【{}】", count, System.currentTimeMillis() - start);
        //分组数量
//        int groupCount = (int) Math.ceil((double) count / threadCapacity);

        //初始化线程池
        initPool();

        try
        {
            //处理结果
            List<Future> futureList = new ArrayList<>();
            List<BthSetCapitalSum> bthSetCapitalSumList=null;
            List<BthSetCapitalSum> subList=new ArrayList<>();
            int pageIdx=1;
            int retry=3;
            // 汇总清分表
            Map<String, Object> map = new HashMap<>();
            while(true)
            {
                int minIndex = (pageIdx - 1) * threadCapacity*threadCount + 1;  //这里乘以线程数threadCount，一次多搂点
                int maxIndex = pageIdx * threadCapacity*threadCount;


                map.put("batchNo", capBatchNo);
                map.put("minIndex", minIndex);
                map.put("maxIndex", maxIndex);

//                bthMerInAccList = bthSetCapitalDetailDao.selectList("queryClearSumInfoMchtIn", map);

                long queryStart=System.currentTimeMillis();
                bthSetCapitalSumList=this.bthSetCapitalSumDao.selectList("queryBthSetCapitalSum",map);
                if(bthSetCapitalSumList==null||bthSetCapitalSumList.size()==0)
                {
                    log.info("待处理结果集bthSetCapitalSumList为空，循环退出，pageIdx【{}】，minIndex【{}】，maxIndex【{}】",pageIdx,minIndex,maxIndex);
                    break;
                }
                log.info("处理主线程查询待分片处理结果集，数量【{}】，本次查询耗时【{}】",bthSetCapitalSumList.size(),System.currentTimeMillis()-queryStart);
                if(pageIdx%10==0)
                {
                    System.gc();
                }
                log.info("处理第[{}]组数据", pageIdx);
                pageIdx++;

                int threadCapacityTemp=1;
                //拆成小块分给每个子线程
                for(Iterator<BthSetCapitalSum> it=bthSetCapitalSumList.iterator();it.hasNext();)
                {
                    subList.add(it.next());
                    it.remove();

                    if(threadCapacityTemp%threadCapacity==0)
                    {
                        Future future = executor.submit(new Handler(stlmDate, capBatchNo, subList));
                        futureList.add(future);

                        subList=new ArrayList<>();
                    }
                    threadCapacityTemp++;
                }

                if(subList!=null&&subList.size()>0)
                {
                    Future lastFuture = executor.submit(new Handler(stlmDate, capBatchNo, subList));
                    futureList.add(lastFuture);
                }

            }


            if(bthSetCapitalSumList!=null)
            {
                bthSetCapitalSumList.clear();
            }


            /**
             * 获取处理结果
             */
            for (Future future : futureList)
            {
                try
                {
                    future.get(10, TimeUnit.MINUTES);
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

        log.info("=====================清分汇总结果核验开始=================");
        BthMerTxnCountInfo sumDetail = bthMerInAccDtlDao.checkSumInAccAmtDtl(stlmDate);
        log.info("查询出清算日期-【{}】-的明细交易本金总笔数为【{}】笔，入账总金额为【{}】元",stlmDate,sumDetail.getTxnCount(),sumDetail.getTxnAmt());
//        Map<String,Object> map = new HashMap<>();
//        map.put("stlmDate",stlmDate);
//        map.put("stlmDate",stlmDate);
        BthMerInAcc bthMerInAcc = bthMerInAccDao.checkInAccAmt(stlmDate);
        log.info("查询出清算日期-【{}】-的汇总交易本金总笔数为【{}】笔，入账总金额为【{}】元",stlmDate,bthMerInAcc.getTxnCount(),bthMerInAcc.getInAcctAmt());
        if(sumDetail.getTxnCount()!=bthMerInAcc.getTxnCount() || !sumDetail.getTxnAmt().equals(bthMerInAcc.getInAcctAmt())){
            log.error("查询出清算日期-【{}】-汇总与明细的交易本金总笔数与总金额不匹配");
            response.setRespCode("9999");
            response.setRespMsg("商户本金总分核对异常，请手工检查");
            return response;
        }

        log.info("---------------------清分汇总结束---------------------，总耗时【{}】",System.currentTimeMillis()-start);

        return response;
    }

    /**
     * 工作线程
     *
     * @param <T>
     */
    class Handler<T> implements Callable<T>
    {

        //数据区间
        private DataInterval dataInterval;
        //批次号
        private String capBatchNo;
        //清算日期
        private String stlmDate;

        private List<BthSetCapitalSum> bthSetCapitalSumList;

        public Handler(String stlmDate, String capBatchNo, List<BthSetCapitalSum> bthSetCapitalSumList)
        {
            this.stlmDate = stlmDate;
            this.capBatchNo = capBatchNo;
            this.bthSetCapitalSumList = bthSetCapitalSumList;
        }

        public Handler(String stlmDate, String capBatchNo, DataInterval dataInterval)
        {
            this.stlmDate = stlmDate;
            this.capBatchNo = capBatchNo;
            this.dataInterval = dataInterval;
        }


        @Override
        public T call() throws Exception
        {
            long start=System.currentTimeMillis();
            log.debug("====处理{}数据(start)====", dataInterval);
            try
            {
                int size = bthSetCapitalSumList.size();
                log.info("本片线程待处理入账汇总记录总条数: {}", size);

                List<String> merIdList=bthSetCapitalSumList.stream().map(BthSetCapitalSum::getMerId).collect(Collectors.toList());
                Map<String, Object> parameter = new HashMap<String, Object>();
                parameter.put("merIdList", merIdList);
                parameter.put("batchNo", capBatchNo);

                List<BthMerTxnFee> bthMerTxnFeeList=bthMerTxnFeeDao.selectList("queryBthMerTxnFeeByMerIdList",parameter);
                Map<String, BthMerTxnFee> bthMerTxnFeeMap = bthMerTxnFeeList.stream().collect(
                        Collectors.toMap(BthMerTxnFee::getChlMerId, a -> a, (k1, k2) -> k1));
                log.info("查询本片线程数据处理范围内各商户交易笔数，总订单金额，集合数量【{}】，耗时【{}】",bthMerTxnFeeList.size(),System.currentTimeMillis()-start);

                // 初始化汇总列表,用于批量入库
                List<BthMerInAcc> accList = new ArrayList<>();
                List<String> merIdParamList=new ArrayList<>();
                List<BthSetCapitalSum> condList=new ArrayList<BthSetCapitalSum>();
                // sumDetail 为汇总的对象
                for (BthSetCapitalSum sumDetail : bthSetCapitalSumList)
                {
                    BthMerInAcc bthMerInAcc=dealSumObj(capBatchNo, sumDetail, stlmDate,bthMerTxnFeeMap);

                    if (BigDecimal.ZERO.compareTo(new BigDecimal(bthMerInAcc.getInAcctAmt())) == 0)
                    {
                        bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_SUCC);
                        bthMerInAcc.setHandleState(Constans.HANDLE_STATE_SUCC);
                        bthMerInAcc.setHandleMark("处理成功");

                        if (Constans.IN_ACCT_TYPE_MCHT.equals(bthMerInAcc.getInAcctType()))
                        {
                            merIdParamList.add(sumDetail.getMerId());
                            //更新清分
//                            updateCapDetailMer(capBatchNo, sumDetail.getMerId());
                            //更新明细
//                            updateMerInAccDtl(capBatchNo, stlmDate, sumDetail.getMerId());
                        }
                        else
                        {
                            condList.add(sumDetail);
                            //更新清分
//                            updateCapDetail(capBatchNo, sumDetail.getMerId(), sumDetail.getEntryType());
                        }
                    }

                    //他行手续费
                    if(Constans.ENTRY_TYPE_MER.equals(bthMerInAcc.getEntryType())&&Constans.SETTL_ACCT_TYPE_CROSS.equals(bthMerInAcc.getBrno())){
                        BigDecimal otherSettFee = calcMerFeeOtherSet(new BigDecimal(bthMerInAcc.getInAcctAmt()),bthMerInAcc.getChlMerId());
                        bthMerInAcc.setOtherSetlFee(otherSettFee.toString());
                        bthMerInAcc.setInAcctAmt(new BigDecimal(bthMerInAcc.getInAcctAmt()).subtract(otherSettFee).toString());

                        //增加一条他行手续费清分
                        sumDetail.setInAccountNo(null);
                        sumDetail.setInAccountNo(null);
                        MchtOrgRel  mchtOrgRel = getMerOrgId(bthMerInAcc.getChlMerId());
                        BthMerInAcc bthMerInAccFee=dealSumOtherSel(bthMerInAcc,mchtOrgRel);
                        accList.add(bthMerInAccFee);
                    }
                    accList.add(bthMerInAcc);
                }

                log.info("本片数据汇总计算结束，耗时【{}】",bthMerTxnFeeList.size(),System.currentTimeMillis()-start);

                //批量更新清分
                if(merIdParamList!=null&&merIdParamList.size()>0)
                {
                    updateCapDetailMerBatch(capBatchNo,merIdParamList,stlmDate);
                }
                log.info("批量更新商户入账清分结束，merIdParamList大小【{}】，耗时【{}】",merIdParamList.size(),System.currentTimeMillis()-start);

                if(merIdParamList!=null&&merIdParamList.size()>0)
                {
                    updateByMerIdList(capBatchNo,stlmDate,merIdParamList);
                }
                log.info("批量更新商户入账明细结束，merIdParamList大小【{}】,耗时【{}】",merIdParamList.size(),System.currentTimeMillis()-start);
                //批量更新清分
                if(condList!=null&&condList.size()>0)
                {
                    updateCapDetailBatch(capBatchNo, condList, stlmDate);
                }
                log.info("批量更新其他清分结束，condList大小【{}】,耗时【{}】",condList.size(),System.currentTimeMillis()-start);

                // 批量入库
                log.info("=================批量插入汇总记录 start=================");
                int count =0;
                if(accList!=null&&accList.size()>0)
                {
                    count = bthMerInAccDao.insertBatch(accList);
                }
                log.info("=================批量插入汇总记录 end，入库数:【{}】，耗时【{}】=================", count,System.currentTimeMillis()-start);

            }
            catch (Exception e)
            {
                log.error("对账未知异常:", e);
                throw e;
            }
            return null;
        }
    }

    /**
     * 统计待汇总的条数
     *
     * @param capBatchNo
     * @return
     */
    private int getSumCount(String capBatchNo)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("batchNo", capBatchNo);
        //统计待汇总的条数
        return bthSetCapitalDetailDao.count("countInAccSum", map);
    }


    /**
     * 清除缓存(清分任务缓存)
     */
    private void clearCache()
    {
        log.info("=============== 清理商户相关的缓存信息 start ================");
        // 清除商户信息缓存
        CacheMchtBaseInfo.clearCache();
        // 清除商户合同信息缓存
        CacheMchtContInfo.clearCache();
        // 清除分润信息缓存
        CacheMchtGainsInfo.clearCache();
        // 清除机构信息缓存
        CacheMchtOrgRel.clearCache();
        // 清除物流合作方缓存
        CacheParternBaseInfo.clearCache();
        //清楚服务商分润缓存
        CacheServiceGainsInfo.clearCache();
        //清楚服务商信息缓存
        CacheServiceBaseInfo.clearCache();
        log.info("=============== 清理商户相关的缓存信息 end ================");
    }


    /**
     * 删除当日汇总数据 , 恢复处理中清分数据为待处理
     *
     * @param stlmDate
     * @param capBatchNo
     */
    private void clear(String stlmDate, String capBatchNo)
    {
        //删除当日汇总数据
        bthMerInAccDao.deleteByStlmDate(stlmDate);

        // 更新清算日下清分表状态  ,设置批次号
        //因为在清算时，bth_set_capital_detail.clea_time字段记录的为当前日期，因此这里也取当前日期
        String today=IfspDateTime.getYYYYMMDD();
        updateAllCapital(stlmDate, capBatchNo);

        this.bthMerTxnFeeDao.clear();

        this.bthSetCapitalSumDao.clear();
    }

    /**
     * 初始化商户金额笔数到中间表
     * 99-商户退款与01-商户入账汇总到中间表，作为商户入账
     */
    public void init(String batchNo)
    {
        this.bthSetCapitalSumDao.initData(batchNo);
        this.bthMerTxnFeeDao.initData(batchNo);
    }


    /**
     * 全量更新待汇总的清分表状态
     *
     * @param cleaTime
     * @param capBatchNo
     */
    private void updateAllCapital(String cleaTime, String capBatchNo)
    {
        log.info("====================>>全量更新清分表 start<<====================");
        // 组装参数
        Map<String, Object> maps = new HashMap<>();
        // 设置统一的批次号
        maps.put("batchNo", capBatchNo);
        maps.put("cleaTime", cleaTime);
        // 修改清分表处理状态
        maps.put("dealResult", Constans.DEAL_RESULT_HANDING);
        maps.put("accountStauts", Constans.ACCOUNT_STATUS_NOT);

        bthSetCapitalDetailDao.update("updateAllCapitalByDate", maps);
        log.info("====================>>全量更新清分表 end<<====================");
    }


    /**
     * 处理汇总对象
     *
     * @param capBatchNo
     * @param sumDetail
     * @param stlmDate
     * @param bthMerTxnFeeMap
     */
    private BthMerInAcc dealSumObj(String capBatchNo, BthSetCapitalSum sumDetail, String stlmDate,Map<String,BthMerTxnFee> bthMerTxnFeeMap)
    {

        BthMerInAcc bthMerInAcc = new BthMerInAcc();
        // 本行卡
        //bthMerInAcc.setBrno(Constans.SETTL_ACCT_TYPE_PLAT);
        bthMerInAcc.setBrno(sumDetail.getAccountType());
        // 清算日期
        bthMerInAcc.setDateStlm(stlmDate);
        // 入账状态  1- 待入账  2- 入账成功  3- 入账失败
        bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_PRE);
        // 处理状态  0-未处理  1-处理中 2-处理成功 3-处理失败(参考清分表)
        bthMerInAcc.setHandleState(Constans.HANDLE_STATE_PRE);
        bthMerInAcc.setHandleMark("未处理");

        //20190920获取入账流水号,流水号必须与t+0的一样
        Map<String ,String> map = new HashMap<>();
        map.put("dateStlm", stlmDate);
        map.put("outAcctNo", bthMerInAcc.getOutAcctNo());
        map.put("inAcctNo", bthMerInAcc.getInAcctNo());
//        String txnSsn = bthMerInAccDao.getTxnSsn(map);
//        if(IfspDataVerifyUtil.isNotEmpty(txnSsn)){
//            bthMerInAcc.setTxnSsn(txnSsn);
//        }else{
//            // 入账流水号
//            bthMerInAcc.setTxnSsn(IfspId.getUUID(32));
//        }

        // 入账流水号
        if(IfspDataVerifyUtil.isNotBlank(sumDetail.getParternCode())){
            bthMerInAcc.setParternCode(sumDetail.getParternCode());
        }
        bthMerInAcc.setTxnSsn(IfspId.getUUID(32));
        // 商户号
        bthMerInAcc.setChlMerId(sumDetail.getMerId());
        // 商户名
        bthMerInAcc.setChlMerName(sumDetail.getMerName());
        // 二级商户号
        bthMerInAcc.setChlSubMerId(sumDetail.getSubMerId());
        // 二级商户名
        bthMerInAcc.setChlSubMerName(sumDetail.getSubMerName());
        // 出账账户账号
        bthMerInAcc.setOutAcctNo(sumDetail.getOutAccountNo());
        // 出账账户名称
        bthMerInAcc.setOutAcctName(sumDetail.getOutAccountName());
        // 出账账户机构
        bthMerInAcc.setOutAcctNoOrg(sumDetail.getOutAccoutOrg());
        // 借方标记
        if (IfspDataVerifyUtil.isBlank(sumDetail.getOutAccountNo()))
        {
            bthMerInAcc.setBorrowFlag(Constans.BORROW_FEE_TO_BE_ALLOCATED);
            if (Constans.ENTRY_TYPE_FEE_PAY_SD_ORG.equals(sumDetail.getEntryType())
                    || Constans.ENTRY_TYPE_FEE_PAY_OPEN_ORG.equals(sumDetail.getEntryType())
                    || Constans.ENTRY_TYPE_FEE_PAY_OPERATE_ORG.equals(sumDetail.getEntryType()))
            {
                bthMerInAcc.setBorrowFlag(Constans.BORROW_FEE_EXPENSE);
            }
        }
        else
        {
            bthMerInAcc.setBorrowFlag(Constans.BORROW_ALLOCATED_ACCT);
        }

        // 入账账户账号
        bthMerInAcc.setInAcctNo(sumDetail.getInAccountNo());
        // 入账账户名称
        bthMerInAcc.setInAcctName(sumDetail.getInAccountName());
        // 入账账户机构
        bthMerInAcc.setInAcctNoOrg(sumDetail.getInAccoutOrg());
        // 贷方标记
        if (IfspDataVerifyUtil.isBlank(sumDetail.getInAccountNo()))
        {
            bthMerInAcc.setLendFlag(Constans.LEND_FEE_INCOME);
        }
        else
        {
            bthMerInAcc.setLendFlag(Constans.LEND_ALLOCATED_ACCT);
        }

        // 入账金额
        bthMerInAcc.setInAcctAmt(String.valueOf(sumDetail.getTranAmount()));

        // 设置入账类型
        initInAcctType(sumDetail, bthMerInAcc);

        // 批次号
        bthMerInAcc.setBatchNo(capBatchNo);
        //入账分录类型
        bthMerInAcc.setEntryType(sumDetail.getEntryType());
        // 如果是商户入账 , 根据清分表订单号查询明细表,得到交易笔数 ,总订单金额
        if (Constans.IN_ACCT_TYPE_MCHT.equals(bthMerInAcc.getInAcctType()))
        {
            // 统计出手续费, 交易金额 , 交易笔数
//            BthMerInAccInfo inf = getBthMerInAccInfo(capBatchNo, sumDetail);
            BthMerTxnFee bthMerTxnFee=bthMerTxnFeeMap.get(sumDetail.getMerId());
            bthMerInAcc.setTxnAmt(String.valueOf(bthMerTxnFee.getTxnAmt()));
            bthMerInAcc.setFeeAmt(String.valueOf(bthMerTxnFee.getFeeAmt()));
            bthMerInAcc.setTxnCount(bthMerTxnFee.getTxnCount()==null?0:bthMerTxnFee.getTxnCount().intValue());
        }

        return bthMerInAcc;
    }


    /**
     * 初始化入账类型
     * 与ClearingServiceImpl getEntryTypes方法对应!!!
     *
     * @param sumDetail
     * @param bthMerInAcc
     */
    private void initInAcctType(BthSetCapitalSum sumDetail, BthMerInAcc bthMerInAcc)
    {
        // 设置入账类型
        switch (sumDetail.getEntryType())
        {
            // 商户入账   ( 当分录类型为 99 -商户退款 时,看作 01-商户入账 ,汇总sql查询时与01一起汇总)
            case Constans.ENTRY_TYPE_MER:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_MCHT);
                break;
            // 通道还钱
            case Constans.ENTRY_TYPE_BRANCH_FEE:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_PAGY);
                break;
            // 手续费 收入
            case Constans.ENTRY_TYPE_FEE_GAINS_SD_ORG:
            case Constans.ENTRY_TYPE_FEE_GAINS_OPEN_ORG:
            case Constans.ENTRY_TYPE_FEE_GAINS_UNIVERSAL:
            case Constans.ENTRY_TYPE_FEE_GAINS_OPERATE_ORG:
            case Constans.ENTRY_TYPE_FEE_GAINS_SERVICE_ORG:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_FEE_2);
                break;
            // 手续费支出
            case Constans.ENTRY_TYPE_FEE_PAY_SD_ORG:
            case Constans.ENTRY_TYPE_FEE_PAY_OPEN_ORG:
            case Constans.ENTRY_TYPE_FEE_PAY_OPERATE_ORG:
            case Constans.ENTRY_TYPE_FEE_PAY_EBANK:
            case Constans.ENTRY_TYPE_FEE_GUARANTEE:
            case Constans.ENTRY_TYPE_FEE_PAY_SERVICE_ORG:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_FEE);
                break;
            // 日间记账失败
            case Constans.ENTRY_TYPE_FOR_ACCOUNT:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_DAY_FAIL);
                break;
            // 保证金
            case Constans.ENTRY_TYPE_FOR_GUARANTEE:
//            case Constans.ENTRY_TYPE_MER_FOR_GUARANTEE:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_GUARANTEE);
                break;
            // 商户佣金收入
            case Constans.ENTRY_TYPE_COMM_IN:
                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPR_COMMISSION);
                break;
            default:
                // 没有这种状态 ,不予处理
                log.error("没有[" + sumDetail.getEntryType() + "]对应的分录流水类型!!!");

        }
    }


    /**
     * 汇总金额为0 , 直接将状态更新为成功(非商户入账清分)
     *
     * @param capBatchNo
     * @param merId
     * @param entryType
     */
    private void updateCapDetail(String capBatchNo, String merId, String entryType)
    {
        log.info("====================>>更新清分表 start<<====================");

        // 清分表MAP
        Map<String, Object> capitalMap = new HashMap<>(6);
        // 查询参数
        capitalMap.put("capBatchNo", capBatchNo);
        capitalMap.put("merId", merId);
        capitalMap.put("entryType", entryType);
        // 更新参数
        capitalMap.put("accountStauts", Constans.ACCOUNT_STATUS_SUCCESS);
        capitalMap.put("dealResult", Constans.DEAL_RESULT_SUCCESS);
        capitalMap.put("dealRemark", "处理成功");
        bthSetCapitalDetailDao.update("updateCapDetail", capitalMap);
        log.info("====================>>更新清分表 end<<====================");
    }

    /**
     * 汇总金额为0 , 直接将状态更新为成功(非商户入账清分)
     * 批量更新
     *
     * @param capBatchNo
     * @param condList
     */
    private void updateCapDetailBatch(String capBatchNo, List<BthSetCapitalSum> condList, String stlmDate)
    {
        log.info("====================>>更新清分表 start<<====================");

        // 清分表MAP
        Map<String, Object> capitalMap = new HashMap<>(6);
        // 查询参数
        capitalMap.put("capBatchNo", capBatchNo);
        capitalMap.put("condList", condList);
        // 更新参数
        capitalMap.put("accountStauts", Constans.ACCOUNT_STATUS_SUCCESS);
        capitalMap.put("dealResult", Constans.DEAL_RESULT_SUCCESS);
        capitalMap.put("dealRemark", "处理成功");
        capitalMap.put("updateDate", IfspDateTime.getYYYYMMDDHHMMSS());
        capitalMap.put("startDate",IfspDateTime.plusTime(stlmDate,"yyyyMMdd",IfspTimeUnit.DAY,-10));
        capitalMap.put("endDate",stlmDate);
        bthSetCapitalDetailDao.update("updateCapDetailBatch", capitalMap);
        log.info("====================>>更新清分表 end<<====================");
    }


    /**
     * 汇总金额为0 , 直接将状态更新为成功(商户入账清分)
     *
     * @param capBatchNo
     * @param merId
     */
    private void updateCapDetailMer(String capBatchNo, String merId)
    {
        log.info("====================>>更新清分表 start<<====================");

        // 清分表MAP
        Map<String, Object> capitalMap = new HashMap<>(5);
        // 查询参数
        capitalMap.put("capBatchNo", capBatchNo);
        capitalMap.put("merId", merId);
        // 更新参数
        capitalMap.put("accountStauts", Constans.ACCOUNT_STATUS_SUCCESS);
        capitalMap.put("dealResult", Constans.DEAL_RESULT_SUCCESS);
        capitalMap.put("dealRemark", "处理成功");
        capitalMap.put("updateDate", IfspDateTime.getYYYYMMDDHHMMSS());
        bthSetCapitalDetailDao.update("updateCapDetailMer", capitalMap);
        log.info("====================>>更新清分表 end<<====================");
    }

    /**
     * 汇总金额为0 , 直接将状态更新为成功(商户入账清分)
     * 批量更新
     * @param capBatchNo
     * @param merIdList
     */
    private void updateCapDetailMerBatch(String capBatchNo, List<String> merIdList,String stlmDate)
    {
        log.info("====================>>更新清分表 start<<====================");

        // 清分表MAP
        Map<String, Object> capitalMap = new HashMap<>(5);
        // 查询参数
        capitalMap.put("capBatchNo", capBatchNo);
        capitalMap.put("merIdList", merIdList);
        // 更新参数
        capitalMap.put("accountStauts", Constans.ACCOUNT_STATUS_SUCCESS);
        capitalMap.put("dealResult", Constans.DEAL_RESULT_SUCCESS);
        capitalMap.put("dealRemark", "处理成功");
        capitalMap.put("startDate",IfspDateTime.plusTime(stlmDate,"yyyyMMdd",IfspTimeUnit.DAY,-10));
        capitalMap.put("endDate",stlmDate);
        bthSetCapitalDetailDao.update("updateCapDetailMerBatch", capitalMap);
        log.info("====================>>更新清分表 end<<====================");
    }

    /**
     * 汇总类型为商户入账 , 更新明细表
     *
     * @param capBatch
     * @param stlmDate
     * @param merId
     */
    private void updateMerInAccDtl(String capBatch, String stlmDate, String merId)
    {
        log.info("====================>>更新明细表 start<<====================");
        // 根据订单号更新入账明细表,插入批次号, 如果入账金额为0 ,更新为入账成功
        Map<String, Object> inAccDtlMap = new HashMap<>();

        //查询参数
        inAccDtlMap.put("merId", merId);
        inAccDtlMap.put("capBatchNo", capBatch);

        // 更新参数
        inAccDtlMap.put("inAcctDate", stlmDate);
        inAccDtlMap.put("inAcctStat", Constans.IN_ACC_STAT_SUCC);
        inAccDtlMap.put("stlStatus", Constans.SETTLE_STATUS_SUCCESS_CLEARING);

        bthMerInAccDtlDao.update("updateByCapBatchNo", inAccDtlMap);

        log.info("====================>>更新明细表 end<<====================");
    }

    /**
     * 汇总类型为商户入账 , 更新明细表
     * 批量更新
     * @param capBatch
     * @param stlmDate
     * @param merIdList
     */
    private void updateByMerIdList(String capBatch, String stlmDate, List<String> merIdList)
    {
        log.info("====================>>更新明细表 start<<====================");
        // 根据订单号更新入账明细表,插入批次号, 如果入账金额为0 ,更新为入账成功
        Map<String, Object> inAccDtlMap = new HashMap<>();

        String stlmDateStart=IfspDateTime.plusTime(stlmDate, "yyyyMMdd", IfspTimeUnit.DAY, -31);
        //查询参数
        inAccDtlMap.put("merIdList", merIdList);
        inAccDtlMap.put("capBatchNo", capBatch);
        inAccDtlMap.put("stlmDateStart", stlmDateStart);
        inAccDtlMap.put("stlmDateEnd", stlmDate);

        // 更新参数
        inAccDtlMap.put("inAcctDate", stlmDate);
        inAccDtlMap.put("inAcctStat", Constans.IN_ACC_STAT_SUCC);
        inAccDtlMap.put("stlStatus", Constans.SETTLE_STATUS_SUCCESS_CLEARING);

        bthMerInAccDtlDao.update("updateByMerIdList", inAccDtlMap);

        log.info("====================>>更新明细表 end<<====================");
    }

    private  MchtContInfo getMerStlInfo(String mchtId) {

        MchtContInfo merStlInfo ;
        if (IfspDataVerifyUtil.isNotBlank(CacheMchtContInfo.getCache(mchtId))){
            merStlInfo = CacheMchtContInfo.getCache(mchtId);
        }else {
            Map<String,Object> m = new HashMap<String,Object>();
            m.put("mchtId", mchtId);
            merStlInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);
            if (IfspDataVerifyUtil.isNotBlank(merStlInfo)){
                // 加入缓存
                CacheMchtContInfo.addCache(mchtId,merStlInfo);
            }
        }


        return merStlInfo;
    }

    /**
     * 统计交易金额,手续费,交易笔数
     *
     * @param capBatchNo
     * @param sumDetail
     * @return
     */
    private BthMerInAccInfo getBthMerInAccInfo(String capBatchNo, BthSetCapitalSum sumDetail)
    {
        // 组装参数
        Map<String, Object> inAccDtlMap = new HashMap<>();
        // 查询参数
        inAccDtlMap.put("merId", sumDetail.getMerId());
        inAccDtlMap.put("entryType", "01");
        inAccDtlMap.put("entryType2", "99");
        inAccDtlMap.put("capBatchNo", capBatchNo);

        // 根据订单号汇总交易金额 , 商户手续费
        BthMerInAccInfo bthMerInAccInfo = bthMerInAccDtlDao.sumTxnAmtFeeAmt(inAccDtlMap);

        return bthMerInAccInfo;
    }

    private BthMerInAcc dealSumOtherSel(BthMerInAcc bthMerInAccOrg, MchtOrgRel mchtOrgRel)
    {

        BthMerInAcc bthMerInAcc = new BthMerInAcc();
        // 本行卡
        bthMerInAcc.setBrno(Constans.SETTL_ACCT_TYPE_PLAT);
        // 清算日期
        bthMerInAcc.setDateStlm(bthMerInAccOrg.getDateStlm());
        // 入账状态  1- 待入账  2- 入账成功  3- 入账失败
        bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_PRE);
        // 处理状态  0-未处理  1-处理中 2-处理成功 3-处理失败(参考清分表)
        bthMerInAcc.setHandleState(Constans.HANDLE_STATE_PRE);
        bthMerInAcc.setHandleMark("未处理");


        // 入账流水号
        bthMerInAcc.setTxnSsn(IfspId.getUUID(32));
        // 商户号
        bthMerInAcc.setChlMerId(bthMerInAccOrg.getChlMerId());
        // 商户名
        bthMerInAcc.setChlMerName(bthMerInAccOrg.getChlMerName());
        // 二级商户号
        bthMerInAcc.setChlSubMerId(bthMerInAccOrg.getChlSubMerId());
        // 二级商户名
        bthMerInAcc.setChlSubMerName(bthMerInAccOrg.getChlSubMerName());
        // 出账账户账号
        bthMerInAcc.setOutAcctNo(bthMerInAccOrg.getOutAcctNo());
        // 出账账户名称
        bthMerInAcc.setOutAcctName(bthMerInAccOrg.getOutAcctName());
        // 出账账户机构
        bthMerInAcc.setOutAcctNoOrg(bthMerInAccOrg.getOutAcctNoOrg());
        // 借方标记
        if (IfspDataVerifyUtil.isBlank(bthMerInAccOrg.getOutAcctNo()))
        {
            bthMerInAcc.setBorrowFlag(Constans.BORROW_FEE_TO_BE_ALLOCATED);
            if (Constans.ENTRY_TYPE_FEE_PAY_SD_ORG.equals(bthMerInAccOrg.getEntryType())
                    || Constans.ENTRY_TYPE_FEE_PAY_OPEN_ORG.equals(bthMerInAccOrg.getEntryType())
                    || Constans.ENTRY_TYPE_FEE_PAY_OPERATE_ORG.equals(bthMerInAccOrg.getEntryType()))
            {
                bthMerInAcc.setBorrowFlag(Constans.BORROW_FEE_EXPENSE);
            }
        }
        else
        {
            bthMerInAcc.setBorrowFlag(Constans.BORROW_ALLOCATED_ACCT);
        }


        // 入账账户账号
        bthMerInAcc.setInAcctNo(null);
        // 入账账户名称
        bthMerInAcc.setInAcctName(null);
        // 入账账户机构
        bthMerInAcc.setInAcctNoOrg(mchtOrgRel.getOrgId());
        // 贷方标记他行手续费
        bthMerInAcc.setLendFlag(Constans.LEND_FEE_INCOME);


        // 入账金额
        bthMerInAcc.setInAcctAmt(String.valueOf(bthMerInAccOrg.getOtherSetlFee()));

        // 设置入账类型
        bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_FEE_2);

        // 批次号
        bthMerInAcc.setBatchNo(bthMerInAccOrg.getBatchNo());
        //入账分录类型
        bthMerInAcc.setEntryType("18");

        return bthMerInAcc;
    }

    /**
     * 查询收单机构
     * @return
     */
    public MchtOrgRel getMerOrgId(String chlMerId)
    {
        Map<String,Object> params = new HashMap<String,Object>();

        params.put("merId",chlMerId);

        MchtOrgRel mchtOrgRel = mchtOrgRelDao.selectOne("selectOrgByMchtId",params);

        return mchtOrgRel;
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
                return new Thread(r, "clearSumHander_" + this.atomic.getAndIncrement());
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

    /**
     * 根据交易金额计算他行手续费
     *

     * @return
     */
    private BigDecimal calcMerFeeOtherSet (BigDecimal txnAmt, String merId)
    {
        BigDecimal merFee = new BigDecimal(0);
        txnAmt = txnAmt.multiply(ONE_HUNDRED);//转为分

        MchtContInfo merSettleInfo = getMerStlInfo(merId);
        if (merSettleInfo == null)
        {
            //没配置费率信息,不收手续费
            return merFee;
        }

        String rateCalcType = merSettleInfo.getOtherSettFeeType();
        if (Constans.COMM_TYPE_FIX_AMT.equals(rateCalcType))
        {
            //按固定金额返佣
            if (merSettleInfo.getOtherSettFee() != null)
            {
                merFee = merSettleInfo.getOtherSettFee().multiply(ONE_HUNDRED);   //参数单位为元
                if (txnAmt.compareTo(merFee) < 0)
                {
                    log.info(
                            "商户[" + merId + "]行内手续费[" + merFee + "]大于支付金额[" + txnAmt + "] , 手续费金额最多只收取订单金额, 即为 [" + txnAmt + "]分 .");
                    merFee = txnAmt;
                }
            }
        }
        else if (Constans.COMM_TYPE_BY_RATE.equals(rateCalcType))
        {
            //按比例返佣
            if (merSettleInfo.getOtherSettFee() != null)
            {
                merFee = merSettleInfo.getOtherSettFee().multiply(txnAmt).divide(ONE_HUNDRED);
                if (merSettleInfo.getMaxOtherSettFee() != null)
                {
                    //大于最大手续费
                    if (merFee.compareTo(merSettleInfo.getMaxOtherSettFee().multiply(ONE_HUNDRED)) == 1)
                    {
                        merFee = merSettleInfo.getMaxOtherSettFee().multiply(ONE_HUNDRED);
                    }
                }

                if (merSettleInfo.getMinOtherSettFee() != null)
                {
                    //小于最小手续费
                    if (merFee.compareTo(merSettleInfo.getMinOtherSettFee().multiply(ONE_HUNDRED)) == -1)
                    {
                        merFee = merSettleInfo.getMinOtherSettFee().multiply(ONE_HUNDRED);
                    }
                }

                //手续费不能大于交易金额
                if (txnAmt.compareTo(merFee) < 0)
                {
                    log.info(
                            "商户[" + merId + "]行内手续费[" + merFee + "]大于支付金额[" + txnAmt + "] , 手续费金额最多只收取订单金额, 即为 [" + txnAmt + "]分 .");
                    merFee = txnAmt;
                }
            }
        }


        //四舍五入，取整
        merFee = merFee.setScale(0, BigDecimal.ROUND_HALF_UP).divide(ONE_HUNDRED);

        return merFee;
    }

}
