package com.scrcu.ebank.ebap.batch.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.DailyTradeStatisticsRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MonthTradeStatisticsRequest;
import com.scrcu.ebank.ebap.batch.bean.request.QueryDailyBillRequest;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMonthBillRequest;
import com.scrcu.ebank.ebap.batch.bean.request.WeekCountQueryRequest;
import com.scrcu.ebank.ebap.batch.bean.response.DailyTradeStatisticsResponse;
import com.scrcu.ebank.ebap.batch.bean.response.MonthTradeStatisticsResponse;
import com.scrcu.ebank.ebap.batch.bean.response.QueryDailyBillResponse;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMonthBillResponse;
import com.scrcu.ebank.ebap.batch.bean.response.WeekCountQueryResponse;
import com.scrcu.ebank.ebap.batch.service.AccountLiquidationQueryService;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;

import lombok.extern.slf4j.Slf4j;

/**
 * 名称：〈清算中心〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 说明：<br>
 */
@Controller
@Slf4j
public class AccountLiquidationQuery {
	
	@Resource
	private AccountLiquidationQueryService accountLiquidationQueryService;

//	@SOA("699.QueryDailyBill")
//    @Explain(name = "日账单查询接口", logLv = LogLevel.DEBUG)
//    public QueryDailyBillResponse QueryDailyBill(@IfspValid QueryDailyBillRequest request) {
//		QueryDailyBillResponse queryDailyBillResponse=accountLiquidationQueryService.queryDailyBill(request);
//        return queryDailyBillResponse;
//    }
//
//	@SOA("699.QueryMonthBill")
//    @Explain(name = "月账单查询接口", logLv = LogLevel.DEBUG)
//    public QueryMonthBillResponse QueryMonthBill(@IfspValid QueryMonthBillRequest request) {
//		QueryMonthBillResponse queryMonthBillResponse=accountLiquidationQueryService.queryMonthBill(request);
//        return queryMonthBillResponse;
//    }
//
//	@SOA("699.MonthTradeStatistics")
//	@Explain(name = "分店交易月查询接口", logLv = LogLevel.DEBUG)
//	public MonthTradeStatisticsResponse MonthTradeStatistics(@IfspValid MonthTradeStatisticsRequest request) {
//		MonthTradeStatisticsResponse monthTradeStatisticsResponse=accountLiquidationQueryService.queryMonthTradeStatistics(request);
//		return monthTradeStatisticsResponse;
//	}
//
//	@SOA("699.DailyTradeStatistics")
//	@Explain(name = "分店交易日查询接口", logLv = LogLevel.DEBUG)
//	public DailyTradeStatisticsResponse DailyTradeStatistics(@IfspValid DailyTradeStatisticsRequest request) {
//		DailyTradeStatisticsResponse dailyTradeStatisticsResponse=accountLiquidationQueryService.DailyTradeStatistics(request);
//		return dailyTradeStatisticsResponse;
//	}
//
//	@SOA("699.WeekCountQuery")
//	@Explain(name = "每周自然日交易金额走势接口", logLv = LogLevel.DEBUG)
//	public WeekCountQueryResponse WeekCountQuery(@IfspValid WeekCountQueryRequest request) {
//
//		return accountLiquidationQueryService.WeekCountQuery(request);
//	}
	
	
}
