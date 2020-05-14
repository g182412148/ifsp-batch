package com.scrcu.ebank.ebap.batch.service;

import java.util.Map;

public interface FeeCalcService 
{
	/**
	 * 计算订单手续费
	 * @param orderSsn
	 * @return
	 */
	public Map<String,Long> calcMerFee4Order(String orderSsn);
	
	/**
	 * 计算子订单手续费
	 * @param subOrderSsn
	 * @return
	 */
	public Map<String,Long> calcMerFee4SubOrder(String subOrderSsn);
	
}
