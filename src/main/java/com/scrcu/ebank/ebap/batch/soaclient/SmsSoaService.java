package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

public interface SmsSoaService
{
	/**
	 * 提示短信发送
	 * @param params
	 * @return
	 */
	SoaResults sendMsg(SoaParams params);

}
