package com.scrcu.ebank.ebap.batch.soaclient.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.scrcu.ebank.ebap.batch.soaclient.DirectWxBillSoaService;
import com.scrcu.ebank.ebap.batch.soaclient.SmsSoaService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;


/**
 * 短信发送
 *
 */
@Service
@Log4j
public class SmsSoaServiceImpl implements SmsSoaService
{
	@SoaClient(name = "6040050004", version = "1.0.0", group = "604")
    private ISoaClient sendMsgServive;

	@Override
	public SoaResults sendMsg(SoaParams params) {
		SoaResults results;
		results = sendMsgServive.invoke(params);
		return results;
	}
}
