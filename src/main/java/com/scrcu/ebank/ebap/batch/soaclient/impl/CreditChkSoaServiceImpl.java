package com.scrcu.ebank.ebap.batch.soaclient.impl;

import org.springframework.stereotype.Service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.scrcu.ebank.ebap.batch.soaclient.CreditChkSoaService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class CreditChkSoaServiceImpl implements CreditChkSoaService 
{
	@SoaClient(name = "6040980005", version = "1.0.0", group = "604")
    private ISoaClient creditBillService;

	@Override
	public SoaResults downloadCreditBill(SoaParams params) 
	{
		SoaResults results;
		log.info("---------------调用信用卡对账单下载服务开始 [request: " + params + "---------------");
		results = creditBillService.invoke(params);
        log.info("---------------调用信用卡对账单下载服务结束 [response: " + results + "---------------");
        return results;
	}

}
