package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.GetSettleDateRequest;
import com.scrcu.ebank.ebap.batch.bean.response.GetSettleDateResponse;
/**
 *名称：获取清算日期 <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
public interface DateService {
	/**
	 * 获取清算日期
	 * @param request
	 * @return
	 */
	GetSettleDateResponse getSettleDate(GetSettleDateRequest request);

}
