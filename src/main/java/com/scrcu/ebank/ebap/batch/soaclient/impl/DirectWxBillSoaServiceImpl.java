package com.scrcu.ebank.ebap.batch.soaclient.impl;

import org.springframework.stereotype.Service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.scrcu.ebank.ebap.batch.soaclient.DirectWxBillSoaService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;

import lombok.extern.log4j.Log4j;


/**
 * 直连微信对账单下载
 * @author ydl
 *
 */
@Service
@Log4j
public class DirectWxBillSoaServiceImpl implements DirectWxBillSoaService
{
	@SoaClient(name = "6020990001", version = "1.0.0", group = "602")
    private ISoaClient wxBillService;
	
	@Override
	public SoaResults downloadDirectWxBill(SoaParams params) 
	{
		SoaResults results;
		log.info("---------------调用微信对账单下载服务开始 [request: " + params + "---------------");
		results = wxBillService.invoke(params);
        log.info("---------------调用微信对账单下载服务结束 [response: " + results + "---------------");
        return results;
	}

}
