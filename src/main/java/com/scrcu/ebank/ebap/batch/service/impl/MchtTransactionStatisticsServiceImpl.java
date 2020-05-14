package com.scrcu.ebank.ebap.batch.service.impl;


import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfoResp;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMchtPayStatisticsRequset;
import com.scrcu.ebank.ebap.batch.bean.request.TimeQuanTumRequest;
import com.scrcu.ebank.ebap.batch.bean.response.TimeQuanTumResponse;
import com.scrcu.ebank.ebap.batch.bean.vo.TimeQuanTumVO;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.dao.BthScheduleClusterDao;
import com.scrcu.ebank.ebap.batch.dao.MchtPayStatisticsInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.MchtTransactionStatisticsService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("mchtTransactionStatisticsService")
@Slf4j
public class MchtTransactionStatisticsServiceImpl implements MchtTransactionStatisticsService{

    @Resource
    private PayOrderInfoDao payOrderInfoDao;

    @Resource
    private MchtPayStatisticsInfoDao mchtPayStatisticsInfoDao;

    @Resource
    private BthScheduleClusterDao bthScheduleClusterDao;
    /** 每次最多统计的商户数量*/
    private final static int rownum = 50000;

    @Override
    public long getMchtPayStatistics() throws Exception {
    	
    	//统计条数
    	long count = 0L;
    	long timeMillis = 0L;
    	
        // 判断能否执行该定时任务
        if(!canExecute(Constans.TASK_HOUR_TXN)){
            return 0;
        }
        try {
            //格式化
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //初始化
            Calendar calendar = Calendar.getInstance();
            //获取当前时间
            Date date = new Date();
            //获取当前时间
            calendar.setTime(date);
            //设置当前时间整点,将分秒归0
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            //格式化
            String endTime = sim.format(calendar.getTime());
            //计算时间（两小时）
            calendar.add(Calendar.HOUR_OF_DAY, -2);
            //格式化开始时间
            String startTime = sim.format(calendar.getTime());
            //查询条件组装
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            int startPage = 0;
            int endPage = rownum;
            map.put("startPage", startPage);//初始化分页查询为1-rownum，后续每次查询条数+rownum
        	map.put("endPage", endPage);
        	
        	while(true){
        		//分页批量插入商户交易统计表
        		log.debug("【分页查询条件：[{}]】",map);
        		long startTimeMillis = System.currentTimeMillis();
        		long countNum = mchtPayStatisticsInfoDao.insertToMchtPayInfo(map);
        		count += countNum;//累加插入总条数
        		//批量插入商户交易统计表（人气和交易量）
        		timeMillis += System.currentTimeMillis()-startTimeMillis;//耗时计算
        		log.debug("【本次分页插入数据[{}]条，耗时[{}ms]】",countNum,timeMillis);
        		if(countNum<rownum){
        			break;
        		}
        		map.put("startPage", startPage+rownum);
        		map.put("endPage", endPage+rownum);
        	}
        	log.info("【当前[{}-{}]时间段共插入[{}]条数据，耗时：[{}ms]】",startTime,endTime,count,timeMillis);
        	
        } catch(Exception e){
        	log.error("【定时更新商户交易统计表异常】",e);
        } finally {
            // 无论是否异常,更新定时任务表
            Date date = IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS);
            bthScheduleClusterDao.updateExecuteStat(Constans.TASK_HOUR_TXN, InetAddress.getLocalHost().getHostAddress(), Constans.EXECUTE_STAT_OFF, date);
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

        // 获取整点时间
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        Date time = c.getTime();
        // 判断整点后是否有执行定时任务
        if (bthScheduleClusterDao.getTask(taskId,time) == 1){
            Date date = IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS);
            bthScheduleClusterDao.updateExecuteStat(taskId,InetAddress.getLocalHost().getHostAddress(),Constans.EXECUTE_STAT_ON,date);
            return true;
        }

        log.info("定时任务"+taskId+"已被其他服务器执行");
        return false;
    }



    @Override
    public List<MchtPayStatisticsInfoResp> queryMchtPayStatistics(QueryMchtPayStatisticsRequset requset){
        //将统计结果记录
        return mchtPayStatisticsInfoDao.selectMchtIdAndTime(requset.getMchtNo(), requset.getTime());
    }

    @Override
    public TimeQuanTumResponse timeQuanTum(TimeQuanTumRequest request) {
        TimeQuanTumResponse response = new TimeQuanTumResponse();
        String[] times = {"08:00","10:00","12:00","14:00","16:00","18:00","20:00","22:00"};

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        log.debug("查询商户号：" + request.getMchtId() + ", 开始日期：" + request.getStartDate() +
                ", 结束日期：" + request.getEndDate() + ", 时间段：" + request.getTimeQuanTum());
        List<MchtPayStatisticsInfoResp> timeQuanTumVOS = mchtPayStatisticsInfoDao.queryTimeQuanTum(request.getMchtId(), request.getStartDate(),
                request.getEndDate(), request.getTimeQuanTum());

        if (timeQuanTumVOS != null) {
//            response.setTimeQuanTums(timeQuanTumVOS);
            List<TimeQuanTumVO> timeQuanTums = new ArrayList<>();
            List<String> timeLists = Arrays.asList(times);
            for (String time : timeLists) {
                boolean flag = false;
                for (MchtPayStatisticsInfoResp timeQuanTumVO : timeQuanTumVOS) {
                    if (IfspDataVerifyUtil.equals(time, timeQuanTumVO.getStartTime())) {
                        flag = true;
                        TimeQuanTumVO timeQuanTum = new TimeQuanTumVO();
                        timeQuanTum.setChlMchtNo(timeQuanTumVO.getChlMchtNo());
                        timeQuanTum.setStartTime(timeQuanTumVO.getStartTime());
                        timeQuanTum.setEndTime(timeQuanTumVO.getEndTime());
                        timeQuanTum.setAmtCount(new BigDecimal(timeQuanTumVO.getAmtCount()));
                        timeQuanTum.setTransactionCount(Integer.parseInt(timeQuanTumVO.getTransactionCount()));
                        timeQuanTums.add(timeQuanTum);
                    }
                }
                if (!flag) {
                    TimeQuanTumVO timeQuanTum = new TimeQuanTumVO();
                    timeQuanTum.setChlMchtNo(request.getMchtId());
                    timeQuanTum.setStartTime(time);
                    timeQuanTum.setEndTime(time);
                    timeQuanTum.setAmtCount(new BigDecimal(0));
                    timeQuanTum.setTransactionCount(0);
                    timeQuanTums.add(timeQuanTum);
                }

            }
            response.setTimeQuanTums(timeQuanTums);
        }
        return response;
    }

}
