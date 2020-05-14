package com.scrcu.ebank.ebap.batch.soaclient.impl;

import org.springframework.stereotype.Service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.scrcu.ebank.ebap.batch.soaclient.ClearingSoaService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;

import lombok.extern.log4j.Log4j;

/**
 *名称：<本行通道soa服务实现类> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/30 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Service
@Log4j
public class ClearingSoaServiceImpl implements ClearingSoaService {
    @SoaClient(name = "6040980003", version = "1.0.0", group = "604")
    private ISoaClient pagyFee;

    @SoaClient(name = "6040980004", version = "1.0.0", group = "604")
    private ISoaClient unifyPay;

	@SoaClient(name = "6040980006", version = "1.0.0", group = "604")
	private ISoaClient unifyPayOtherSel;

	@SoaClient(name = "6040980007", version = "1.0.0", group = "604")
	private ISoaClient unifyPayQue;

	@Override
	public SoaResults clearingSoa(SoaParams params) {
		SoaResults results;
		log.debug("---------------调用CH1730开始 [request: " + params + "---------------");
		results = pagyFee.invoke(params);
        log.debug("---------------调用CH1730结束 [response: " + results + "---------------");
        return results;
	}


	@Override
	public SoaResults unifyPay(SoaParams params) {
		SoaResults results;
		log.debug("---------------调用unifyPay开始 [request: " + params + "---------------");
		results = unifyPay.invoke(params);
        log.debug("---------------调用unifyPay结束 [response: " + results + "---------------");
        return results;
	}

	@Override
	public SoaResults unifyPayOtherSel(SoaParams params) {
		SoaResults results;
		log.debug("---------------调用unifyPay开始 [request: " + params + "---------------");
		results = unifyPayOtherSel.invoke(params);
		log.debug("---------------调用unifyPay结束 [response: " + results + "---------------");
		return results;
	}
	@Override
	public SoaResults unifyPayQue(SoaParams params) {
		SoaResults results;
		log.debug("---------------调用unifyPay开始 [request: " + params + "---------------");
		results = unifyPayQue.invoke(params);
		log.debug("---------------调用unifyPay结束 [response: " + results + "---------------");
		return results;
	}

	
}
