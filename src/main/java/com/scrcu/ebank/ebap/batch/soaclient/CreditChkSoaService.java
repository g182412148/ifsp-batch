package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

public interface CreditChkSoaService
{
	/**
	 * 行用卡支付对账单下载
	 * @param params
	 * @return
	 */
	SoaResults downloadCreditBill(SoaParams params);

}
