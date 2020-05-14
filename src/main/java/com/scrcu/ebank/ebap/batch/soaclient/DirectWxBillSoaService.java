package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

public interface DirectWxBillSoaService
{
	/**
	 * 直连微信对账单下载
	 * @param params
	 * @return
	 */
	SoaResults downloadDirectWxBill(SoaParams params);

}
