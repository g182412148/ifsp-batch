package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDailyTxnCount;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDayAmtSum;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.request.TxnSectionRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.MchtTxnCountService;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.scrcu.ebank.ebap.batch.common.dict.ChlNo.*;

/**
 * @author: ljy
 * @create: 2018-09-06 14:59
 */
@Service
@Slf4j
public class MchtTxnCountServiceImpl implements MchtTxnCountService {

    @Resource
    private PayOrderInfoDao payOrderInfoDao;

    @Resource
    private BthMerDailyTxnCountDao bthMerDailyTxnCountDao;

    @Resource
    private BthMerDayAmtSumDao bthMerDayAmtSumDao;

    @Resource
    private BthScheduleClusterDao bthScheduleClusterDao;


    @Override
    public Map<String, Object> queryMerTxnCount(TxnSectionRequest request) {



        List<Map<String,Object>> maplist = new ArrayList<>();


        List<Map<String, String>> amtList = request.getAmtList();
        for (Map<String, String> stringStringMap : amtList) {
            if (Integer.parseInt(stringStringMap.get("leftAmt")) % 100 != 0 ){
                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "左区间金额请输入100的倍数！");
            }
            if (Integer.parseInt(stringStringMap.get("rightAmt")) % 100 != 0 ){
                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "右区间金额请输入100的倍数！");
            }

            // 组装查询条件
            Map<String,Object> map = new HashMap<String,Object>(5);
            map.put("chlMerId",request.getChlMerId());
            map.put("startTime",request.getStartTime());
            map.put("endTime",request.getEndTime());
            map.put("startAmt",stringStringMap.get("leftAmt"));
            map.put("endAmt",stringStringMap.get("rightAmt"));
            List<BthMerDayAmtSum>  dtl = bthMerDayAmtSumDao.selectByTmAmt(map);

            // todo  渠道表控制显示支付渠道 (当前默认 支付宝 , 微信 , 银联 , 蜀信e必须显示  无该渠道交易则设为 0 )
            Map<String ,Integer> chlNoMap = new HashMap<>(4);
            chlNoMap.put(WECHAR.getCode(),0);
            chlNoMap.put(ALIPAY.getCode(),0);
            chlNoMap.put(SX_E.getCode(),0);
            chlNoMap.put(UNIONPAY.getCode(),0);
            chlNoMap.put(HUIPAY_PERSON.getCode(),0);

            Iterator<BthMerDayAmtSum> iterator = dtl.iterator();
            while (iterator.hasNext()){
                BthMerDayAmtSum next = iterator.next();
                // 存在渠道更新chlNoMap
                if (chlNoMap.containsKey(next.getFundChannel())){
                    chlNoMap.put(next.getFundChannel(),1);
                }
            }

            // 遍历map, 如果值为 0 ,则将该渠道支付金额,支付笔数赋值0给dtl
            Set<Map.Entry<String, Integer>> entries = chlNoMap.entrySet();
            for (Map.Entry<String, Integer> entry : entries) {
                if (entry.getValue().equals(0)){
                    BthMerDayAmtSum record = new BthMerDayAmtSum();
                    record.setTxnAmt(BigDecimal.ZERO);
                    record.setTxnCount(0L);
                    record.setFundChannel(entry.getKey());
                    record.setFundChannelNm(getDescByCode(entry.getKey()));
                    dtl.add(record);
                }
            }

            // 按照支付渠道编号排序
            Collections.sort(dtl, new Comparator<BthMerDayAmtSum>() {
                @Override
                public int compare(BthMerDayAmtSum o1, BthMerDayAmtSum o2) {
                    return Integer.parseInt(o1.getFundChannel())-Integer.parseInt(o2.getFundChannel());
                }
            });



            // 保存查询结果
            Map<String,Object> buildMap= new HashMap<>(3);
            buildMap.put("dtl",dtl);
            buildMap.put("leftAmt",stringStringMap.get("leftAmt"));
            buildMap.put("rightAmt",stringStringMap.get("rightAmt"));
            maplist.add(buildMap);

        }

        // 将支持的渠道传给门户  , 以作展示
        Map<String , String>  chlNoMapInfo = new HashMap<>(4);
        chlNoMapInfo.put(WECHAR.getCode(),getDescByCode(WECHAR.getCode()));
        chlNoMapInfo.put(ALIPAY.getCode(),getDescByCode(ALIPAY.getCode()));
        chlNoMapInfo.put(SX_E.getCode(),getDescByCode(SX_E.getCode()));
        chlNoMapInfo.put(UNIONPAY.getCode(),getDescByCode(UNIONPAY.getCode()));
        chlNoMapInfo.put(HUIPAY_PERSON.getCode(),getDescByCode(HUIPAY_PERSON.getCode()));


        // 设置返回参数
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("maplist", maplist);
        respMap.put("chlNoMapInfo",chlNoMapInfo);
        respMap.put("respCode", SystemConfig.getSuccessCode());
        respMap.put("respMsg", SystemConfig.getSuccessMsg());
        return respMap;
    }


    @Override
    public int merDailyTxnCount() throws Exception {

        // 判断能否执行该定时任务
        if(!canExecute(Constans.TASK_DAY_TXN)){
            return 0;
        }

        List<BthMerDailyTxnCount> list = null;
        String txnDate = null;
        int i = 0;
        try {
            //格式化
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            String endTm = sim.format(calendar.getTime());
            calendar.add(Calendar.HOUR_OF_DAY,-24);
            String startTm = sim.format(calendar.getTime());

            // 统计正交易(主扫+公众号支付 O1000002   O1000003   ,支付渠道取的是   [发起渠道:CHL_NO] )
            list = payOrderInfoDao.selectDayTxnGroupByMcht(startTm,endTm);

            // 统计正交易(被扫   O1000001  支付渠道取的是      [受理渠道: ACPT_CHL_NO] )
            List<BthMerDailyTxnCount> list2 = payOrderInfoDao.selectDayTxnGroupByMcht2(startTm,endTm);

            // 统计退款与撤销   (O1100001   O1200001     支付渠道取的是      [受理渠道: ACPT_CHL_NO])
            List<BthMerDailyTxnCount> list3 = payOrderInfoDao.selectDayTxnGroupByMcht3(startTm,endTm);

            // 汇总正交易
            for (BthMerDailyTxnCount bthMerDailyTxnCount : list) {
                // 迭代list2 , 如果匹配则合并入list并从lsit2移除
                Iterator<BthMerDailyTxnCount> iterator2 = list2.iterator();
                while (iterator2.hasNext()){
                    BthMerDailyTxnCount next2 = iterator2.next();
                    if (bthMerDailyTxnCount.getMchtId().equals(next2.getMchtId())
                            &&bthMerDailyTxnCount.getFundChannel().equals(next2.getFundChannel())){
                        // 笔数相加
                        bthMerDailyTxnCount.setTxnCount(bthMerDailyTxnCount.getTxnCount()+next2.getTxnCount());
                        // 金额相加
                        bthMerDailyTxnCount.setTxnAmt(bthMerDailyTxnCount.getTxnAmt().add(next2.getTxnAmt()));
                        // 移除
                        iterator2.remove();
                    }

                }
            }
            // 未匹配的插入list
            Iterator<BthMerDailyTxnCount> iterator2 = list2.iterator();
            while (iterator2.hasNext()){
                BthMerDailyTxnCount next = iterator2.next();
                list.add(next);
            }

            // 汇总正反交易
            for (BthMerDailyTxnCount bthMerDailyTxnCount : list) {
                // 迭代list3 , 如果匹配则合并入list并从lsit3移除
                Iterator<BthMerDailyTxnCount> iterator3 = list3.iterator();
                while (iterator3.hasNext()){
                    BthMerDailyTxnCount next3 = iterator3.next();
                    if (bthMerDailyTxnCount.getMchtId().equals(next3.getMchtId())
                            &&bthMerDailyTxnCount.getFundChannel().equals(next3.getFundChannel())){
                        // 笔数相加
                        bthMerDailyTxnCount.setTxnCount(bthMerDailyTxnCount.getTxnCount()+next3.getTxnCount());
                        // 金额相加
                        bthMerDailyTxnCount.setTxnAmt(bthMerDailyTxnCount.getTxnAmt().add(next3.getTxnAmt()));
                        // 移除
                        iterator3.remove();
                    }

                }
            }
            // 未匹配的插入list
            Iterator<BthMerDailyTxnCount> iterator3 = list3.iterator();
            while (iterator3.hasNext()){
                BthMerDailyTxnCount next = iterator3.next();
                list.add(next);
            }


            // 交易日期 (当前时间减一天)
            txnDate = IfspDateTime.plusTime(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYYMMDD, IfspTimeUnit.DAY, -1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(new Date());
            i = calendar2.get(Calendar.DAY_OF_WEEK)-1;
            // 交易日期的星期 (当前时间减一天)
            if(i == 0){
                i = 6;
            }else {
                i = i - 1 ;
            }

            // 增加事务
            MchtTxnCountService mchtTxn = (MchtTxnCountService)IfspSpringContextUtils.getInstance().getBean("mchtTxnCountServiceImpl");
            return mchtTxn.executeDayTxn(list,txnDate,i);
        } finally {

            // 无论是否异常,更新定时任务表
            Date date = IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS);
            bthScheduleClusterDao.updateExecuteStat(Constans.TASK_DAY_TXN, InetAddress.getLocalHost().getHostAddress(), Constans.EXECUTE_STAT_OFF, date);

        }
    }


    /**
     * 商户日交易统计入库
     * @param list
     * @param txnDate
     * @param i
     * @throws Exception
     */

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public int executeDayTxn(List<BthMerDailyTxnCount> list, String txnDate,int i) throws Exception {

        Iterator<BthMerDailyTxnCount> iterator = list.iterator();
        //统计条数
        int count = 0;

        while (iterator.hasNext()){
            BthMerDailyTxnCount next = iterator.next();
            next.setFundChannelNm(getDescByCode(next.getFundChannel()));
            next.setTxnDate(txnDate);
            next.setWeekFlag(String.valueOf(i));
            bthMerDailyTxnCountDao.insert(next);
            count++;
        }
        return count;
    }



    @Override
    public int merAmtSection() throws Exception {

        // 判断能否执行该定时任务
        if(!canExecute(Constans.TASK_DAY_TXN_AMT_SECTION)){
            return 0;
        }

        Map<String,BthMerDayAmtSum> saveMap = null;
        try {
            //格式化
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            String endTm = sim.format(calendar.getTime());
            calendar.add(Calendar.HOUR_OF_DAY,-24);
            String startTm = sim.format(calendar.getTime());

            // 存放记录
            saveMap = new HashMap<>();

            // 查询出昨日所有交易数据 以支付金额从小到大排序
            List<PayOrderInfo> list = payOrderInfoDao.selectByTime(startTm,endTm);

            Iterator<PayOrderInfo> iterator = list.iterator();
            while (iterator.hasNext()){
                PayOrderInfo next = iterator.next();

                BigDecimal amt = new BigDecimal(next.getPayAmt());
                // 处理金额分转元
                next.setPayAmt(String.valueOf(amt.movePointLeft(2)));

                // 支付金额
                String payAmt = next.getPayAmt();
                BigDecimal payAmtBig = new BigDecimal(payAmt);

                // 正反交易 ,交易金额都是正的 , 判断金额分段正反方式相同  ,但汇总金额是正交易减去反交易
                int i = payAmtBig.intValue() / 100;
                // 设置金额区间 左闭右开 eg : [0,100)
                // 金额左区间
                long leftAmt = i*100;
                // 金额右区间
                long rightAmt = i*100+100;

                // 获取支付渠道
                String chlNo = "";
                //(主扫+公众号支付 O1000002   O1000003   ,支付渠道取的是   [发起渠道:CHL_NO] )
                if (Constans.TXN_TYPE_O1000002.equals(next.getTxnTypeNo())||Constans.TXN_TYPE_O1000003.equals(next.getTxnTypeNo())){
                    chlNo = next.getChlNo();

                // (被扫   O1000001    统计退款与撤销 O1100001   O1200001     支付渠道取的是    [受理渠道: ACPT_CHL_NO])
                }else if (Constans.TXN_TYPE_O1000001.equals(next.getTxnTypeNo())){
                    chlNo = next.getAcptChlNo();
                }else if (Constans.TXN_TYPE_CANCEL.equals(next.getTxnTypeNo())||Constans.TXN_TYPE_REFUND.equals(next.getTxnTypeNo())){
                    chlNo = next.getAcptChlNo();
                    // 此处设置  : 退款金额为负!!!!!
                    next.setPayAmt("-"+next.getPayAmt());
                }else {
                    continue;
                }

                // 交易日期
                String txnDate = IfspDateTime.plusTime(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYYMMDD, IfspTimeUnit.DAY, -1);

                // 商户号,交易日期,支付渠道号,金额左区间,金额右区间组成key
                String key = next.getMchtId()+"-"+txnDate+"-"+chlNo+"-"+leftAmt+"-"+rightAmt;

                if (saveMap.containsKey(key)){
                    BthMerDayAmtSum bthMerDayAmtSum = saveMap.get(key);
                    bthMerDayAmtSum.setTxnCount(bthMerDayAmtSum.getTxnCount()+1L);
                    bthMerDayAmtSum.setTxnAmt(bthMerDayAmtSum.getTxnAmt().add(new BigDecimal(next.getPayAmt())));

                }else {
                    BthMerDayAmtSum bthMerDayAmtSum = new BthMerDayAmtSum();
                    bthMerDayAmtSum.setId(IfspId.getUUID(32));
                    bthMerDayAmtSum.setTxnDate(txnDate);
                    bthMerDayAmtSum.setMchtId(next.getMchtId());
                    bthMerDayAmtSum.setFundChannel(chlNo);
                    bthMerDayAmtSum.setFundChannelNm(getDescByCode(chlNo));
                    bthMerDayAmtSum.setLeftAmt(leftAmt);
                    bthMerDayAmtSum.setRightAmt(rightAmt);
                    bthMerDayAmtSum.setTxnAmt(new BigDecimal(next.getPayAmt()));
                    bthMerDayAmtSum.setTxnCount(1L);

                    saveMap.put(key,bthMerDayAmtSum);
                }

            }
            MchtTxnCountService mchtDayAmt = (MchtTxnCountService)IfspSpringContextUtils.getInstance().getBean("mchtTxnCountServiceImpl");
            return mchtDayAmt.executeDayAmtSection(saveMap);

        } finally {
            // 无论是否异常 ,更新定时任务表
            Date date = IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS);
            bthScheduleClusterDao.updateExecuteStat(Constans.TASK_DAY_TXN_AMT_SECTION,InetAddress.getLocalHost().getHostAddress(),Constans.EXECUTE_STAT_OFF, date);
        }

    }


    /**
     * 商户日交易金额分段统计入库
     * @param saveMap
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public int  executeDayAmtSection(Map<String, BthMerDayAmtSum> saveMap) throws Exception {

        int count = 0;
        Set<Map.Entry<String, BthMerDayAmtSum>> entries = saveMap.entrySet();
        for (Map.Entry<String, BthMerDayAmtSum> entry : entries) {
            bthMerDayAmtSumDao.insert(entry.getValue());
            count++;
        }
        return count;
    }





    @Override
    public Boolean canExecute(String taskId) throws Exception {
        int max = 10000;
        SecureRandom srand = new SecureRandom();
        int min = (int) Math.round(srand.nextDouble()*8000);
        long sleepTime = Math.round(srand.nextDouble()*(max-min));
        if (sleepTime < 1000L){
            sleepTime += 1000L;
        }
        log.info("定时任务"+taskId+" 睡了："+ sleepTime + "毫秒");
        Thread.sleep(sleepTime);

        // 获取当天零点时间
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        Date time = c.getTime();
        // 判断0点后是否有执行定时任务
        if (bthScheduleClusterDao.getTask(taskId,time) == 1){
            Date date = IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS);
            bthScheduleClusterDao.updateExecuteStat(taskId,InetAddress.getLocalHost().getHostAddress(),Constans.EXECUTE_STAT_ON,date);
            return true;
        }

        log.info("定时任务"+taskId+"已被其他服务器执行");
        return false;
    }


    public static void main(String[] args) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        // 交易日期的星期 (当前时间减一天)
        int i = calendar2.get(Calendar.DAY_OF_WEEK) - 1;
        System.out.println(i);
    }

}
