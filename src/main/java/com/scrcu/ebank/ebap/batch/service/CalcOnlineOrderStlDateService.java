package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;

public interface CalcOnlineOrderStlDateService 
{
	/**
	 * 计算线上订单结算日期
	 */
	public void calcStlDate(BatchRequest request);
}
