package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;

/**
 * 订单状态同步服务：将入账明细表中的结算状态同步到订单表
 * @author ydl
 *
 */
public interface StlStatusSyncService 
{
	/**
	 * 同步结算状态
	 */
	public void syncStlStatus(BatchRequest request);
}
