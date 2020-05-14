package com.scrcu.ebank.ebap.batch.service;

import java.text.ParseException;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
/**
 *名称：<贷记卡支付对账> <br>
 *功能：<贷记卡支付对账单下载> <br>
 *方法：<方法简述 - 方法描述> <br>
 *说明：对账单下载<br>
 */
public interface CreditPayChkService {
	/**
	 * 贷记卡支付对账
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	CommonResponse creditPayChk(BatchRequest request) throws Exception;
}
