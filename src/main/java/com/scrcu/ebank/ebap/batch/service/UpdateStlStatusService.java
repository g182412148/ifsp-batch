package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;

public interface UpdateStlStatusService 
{
	/**
	 * 更新入账明细表结算状态
	 */
	public void updateStlStatus(BatchRequest request);
}
