package com.scrcu.ebank.ebap.batch.service;

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

/**
 * 名称：〈分店日统计Service〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 说明：<br>
 */
public interface AccountLiquidationQueryService {

	QueryDailyBillResponse queryDailyBill(QueryDailyBillRequest request);

	QueryMonthBillResponse queryMonthBill(QueryMonthBillRequest request);

	MonthTradeStatisticsResponse queryMonthTradeStatistics(MonthTradeStatisticsRequest request);

	DailyTradeStatisticsResponse DailyTradeStatistics(DailyTradeStatisticsRequest request);

	WeekCountQueryResponse WeekCountQuery(WeekCountQueryRequest request);

}
