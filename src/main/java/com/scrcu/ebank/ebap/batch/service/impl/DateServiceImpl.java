package com.scrcu.ebank.ebap.batch.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.scrcu.ebank.ebap.batch.bean.request.GetSettleDateRequest;
import com.scrcu.ebank.ebap.batch.bean.response.GetSettleDateResponse;
import com.scrcu.ebank.ebap.batch.service.DateService;

import lombok.extern.slf4j.Slf4j;
/**
 *名称：<获取清算日期> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Service
@Slf4j
public class DateServiceImpl implements DateService{
	/**
	 * 获取清算日期
	 */
	@Override
	public GetSettleDateResponse getSettleDate(GetSettleDateRequest request) {
		log.info("---------------------任务开始------------------");
		GetSettleDateResponse getSettleDateResponse = new GetSettleDateResponse();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");  
        Date date=new Date();  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        date = calendar.getTime();  
        log.debug("清算日期是： [ " + sdf.format(date) + " ]");
        getSettleDateResponse.setDate(sdf.format(date));
        log.info("---------------------任务结束------------------");
		return getSettleDateResponse;
	}

}
