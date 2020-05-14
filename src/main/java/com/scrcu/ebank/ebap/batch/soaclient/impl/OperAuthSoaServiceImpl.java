package com.scrcu.ebank.ebap.batch.soaclient.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.soaclient.MchtCenterSoaClientService;
import com.scrcu.ebank.ebap.batch.soaclient.OperAuthSoaService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Service
@Log4j
public class OperAuthSoaServiceImpl implements OperAuthSoaService {

	
	@SoaClient(name = "6040060001", version = "1.0.0", group = "604")
    private ISoaClient operAuth;
	
	@Override
	public SoaResults operAuth(SoaParams params) {
		log.info("删除飞信通进件权限, 请求报文:" + params);
        SoaResults result = null;
        try {
            result = operAuth.invoke(params);
        } catch (Exception e) {
            log.info("删除飞信通进件权限, 发生错误:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"删除飞信通进件权限, 发生错误:" + e);
        }
        log.info("删除飞信通进件权限, 返回报文:" + params);
        return result;
	}

}
