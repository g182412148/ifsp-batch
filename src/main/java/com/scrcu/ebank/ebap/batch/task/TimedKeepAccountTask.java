package com.scrcu.ebank.ebap.batch.task;


import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日间核心记账定时任务 :  每5分钟扫描一次记账表 , 将记账状态为 00 - 待记账的记录取出,更新为  01 - 记账中
 * 多线程调用本行通道完成记账 , 根据返回结果更新记账表
 *
 * @author ljy
 */
@Slf4j
public class TimedKeepAccountTask {

    @Resource
    private KeepAccSoaService keepAccSoaService;

    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    private static  ExecutorService executorService = Executors.newFixedThreadPool(40);

    public void coreKeepAccount() {
        log.info("======================================>>  TimedKeepAccountTask  Start ......");
        // 1.扫描记账表
        TimedKeepAccountTask timedKeepAccountTask = (TimedKeepAccountTask)IfspSpringContextUtils.getInstance().getBean("TimedKeepAccountTask");
        Map<String ,List<KeepAccInfo>> map = timedKeepAccountTask.scanTab();

        // 2.多线程调本行通道 , 一个订单一组
        if (IfspDataVerifyUtil.isNotEmptyMap(map)){
            Set<Map.Entry<String, List<KeepAccInfo>>> entries = map.entrySet();
            for (Map.Entry<String, List<KeepAccInfo>> entry : entries) {
                List<KeepAccInfo> value = getKeepAccInfos(entry);
                Runnable runnable = keepAccTask(value);
                executorService.execute(runnable);
            }
        }else {
            log.info("========================>>本次扫描没有发现需要记账的数据,处理结束<<===============================");
        }
        log.info("======================================>>  TimedKeepAccountTask  End ......");
    }

    /**
     * 记账排序
     * @param entry
     * @return
     */
    private List<KeepAccInfo> getKeepAccInfos(Map.Entry<String, List<KeepAccInfo>> entry) {
        List<KeepAccInfo> value = entry.getValue();
        // 按照序号排序入账   order by seq 升序
        Collections.sort(value,new Comparator<KeepAccInfo>() {
            @Override
            public int compare(KeepAccInfo o1, KeepAccInfo o2) {
                return Integer.valueOf(o1.getKeepAccSeq()) -Integer.valueOf(o2.getKeepAccSeq());
            }
        });
        return value;
    }

    /**
     * 多线程任务去本行记账
     * @param value
     * @return
     */
    private Runnable keepAccTask(List<KeepAccInfo> value) {
        log.info("=================>>开始处理订单["+value.get(0).getOrderSsn()+"]记账<<====================");
        return new Runnable() {
                @Override
                public void run() {
                    Iterator<KeepAccInfo> iterator = value.iterator();
                    while (iterator.hasNext()){
                        KeepAccInfo info = iterator.next();
                        SoaParams params = new SoaParams();
                        initParam(params, info);

                        SoaResults results = keepAccSoaService.keepAcc(params);

                        // 根据返回结果作相应处理
                        if (RespEnum.RESP_SUCCESS.getCode().equals(results.getRespCode())) {
                            log.info("===================>>本行通道受理记账成功<<===========================");
                            info.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                            info.setPagyRespCode(results.getRespCode());
                            info.setPagyRespMsg(results.getRespMsg());
                            if (IfspDataVerifyUtil.isNotBlank(results.get("HDTellrSeqNum"))){
                                info.setReserved2(String.valueOf(results.get("HDTellrSeqNum")));
                            }
                            keepAccInfoDao.updateByPrimaryKeySelective(info);
                        } else if (RespEnum.RESP_TIMEOUT.getCode().equals(results.getRespCode())){
                            log.warn("===================>>调用本行通道记账接口超时<<===========================");
                            // 记为超时 , 待日终改为记账失败
                            info.setState(Constans.KEEP_ACCOUNT_STAT_TIMEOUT);
                            info.setPagyRespCode(results.getRespCode());
                            info.setPagyRespMsg(results.getRespMsg());
                            if (IfspDataVerifyUtil.isNotBlank(results.get("HDTellrSeqNum"))){
                                info.setReserved2(String.valueOf(results.get("HDTellrSeqNum")));
                            }
                            keepAccInfoDao.updateByPrimaryKeySelective(info);
                        }else {
                            log.error("========================>>本行通道调用失败,返回码["+results.getRespCode()+"],返回信息["+results.getRespMsg()+"]<<====================");
                            info.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);
                            info.setPagyRespCode(results.getRespCode());
                            info.setPagyRespMsg(results.getRespMsg());
                            if (IfspDataVerifyUtil.isNotBlank(results.get("HDTellrSeqNum"))){
                                info.setReserved2(String.valueOf(results.get("HDTellrSeqNum")));
                            }
                            keepAccInfoDao.updateByPrimaryKeySelective(info);
                        }
                    }
                }
        };
    }


    /**
     * 事务控制锁表,防止多台机器同时查到
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String ,List<KeepAccInfo>> scanTab()  {
        log.info("========================>>定时任务开始扫描记账表<<===============================");
        // 初始化记账map
        Map<String ,List<KeepAccInfo>> map = new HashMap<>(10);

        // 1.根据订单号 ,订单状态锁表( 确保一个订单下所有流水都取到   "BATCH_NUM "   = "count(0) group by order_ssn")
        Map<String ,Object> param = new HashMap<>(2);
        param.put("isSync",Constans.KEEP_ACCT_FLAG_ASYNC);
        param.put("state",Constans.KEEP_ACCOUNT_STAT_PRE);
        param.put("realTmFlag", Constans.noRealTmFlag);
        List<KeepAccInfo> list = keepAccInfoDao.selectList("selectByOrderSsnAndState", param);
        log.info("=====================>>本次需要记账条数["+list.size()+"]<<===========================");
        // 2.改为记账中 , 并且整理出 各个订单对应的流水
        if (IfspDataVerifyUtil.isNotEmptyList(list)) {
            Iterator<KeepAccInfo> iterator = list.iterator();
            KeepAccInfo info;
            String hostAddress;
            while (iterator.hasNext()) {
                info = iterator.next();
                // 判断金额是否是0 , 如果是 0 则直接改为记账成功
                if (0L == info.getTransAmt()){
                    keepAccInfoDao.updateByState(info.getCoreSsn(),Constans.KEEP_ACCOUNT_STAT_SUCCESS,info.getRegisterIp());
                }else {
                    info.setState(Constans.KEEP_ACCOUNT_STAT_IN);
                    try {
                        hostAddress = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException e) {
                        log.error("未知服务主机ip!!!",e);
                        hostAddress = "Unknown IP";
                    }
                    info.setRegisterIp(hostAddress);
                    // 分订单整理记账流水
                    if(map.containsKey(info.getOrderSsn())){
                        List<KeepAccInfo> list1 = map.get(info.getOrderSsn());
                        list1.add(info);
                    }else {
                        List<KeepAccInfo> list2 = new ArrayList<>();
                        list2.add(info);
                        map.put(info.getOrderSsn(),list2);
                    }
                    keepAccInfoDao.updateByState(info.getCoreSsn(),info.getState(),info.getRegisterIp());
                }

            }
        }

        return map;
    }


    /**
     * 组装参数
     * @param params
     * @param info
     */
    public static void initParam(SoaParams params,KeepAccInfo info) {
        //通道支付请求流水号
        params.put("pagyPayTxnSsn", info.getCoreSsn());
        //通道支付请求流水时间
        params.put("pagyPayTxnTm", IfspDateTime.getYYYYMMDDHHMMSS());
        //通道支付订单号
        params.put("pagyTxnSsn", info.getOrderSsn());
        //通道订单时间
        params.put("pagyTxnTm", info.getOrderTm());
        //借方账号
        params.put("dtAcctNo", info.getOutAccNo());
        //贷方账号
        params.put("ctAcctNo", info.getInAccNo());
        //交易描述
        params.put("txnDesc", info.getTxnDesc());
        //支付金额
        params.put("txnAmt", info.getTransAmt());
        //币种
        params.put("txnCcyType", Constans.CCY_TYPE);
        // 非必输,有就送
        //借方账户名称
        params.put("dtAcctNm", info.getOutAccNoName());
        //贷方账户名称
        params.put("ctAcctNm", info.getInAccNoName());
        //商户所在机构
        params.put("proxyOrg", info.getProxyOrg());
        // 摘要
        if (IfspDataVerifyUtil.isNotBlank(info.getMemo())){
            params.put("memo",info.getMemo());
        }
    }


}
