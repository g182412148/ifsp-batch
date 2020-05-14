package com.scrcu.ebank.ebap.batch.soaclient.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.soaclient.ChnlCenterSoaClientService;
import com.scrcu.ebank.ebap.batch.soaclient.MchtCenterSoaClientService;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChnlCenterSoaClientServiceImpl implements ChnlCenterSoaClientService {

	
    @SoaClient(name = "mchtAuth", version = "1.0.0", group = "600",timeout=18000000) //超时时间：5小时
    private ISoaClient mchtAuth;
	
	@Override
	public SoaResults mchtAuth(SoaParams params) {
        log.info("商户实名认证处理开始, req: " + params);
        SoaResults soaResult = null;
        try {
            soaResult = mchtAuth.invoke(params);
            log.info("商户实名认证处理  resp: " + soaResult);
            if (soaResult == null) {
                log.info("商户实名认证处理结果为空!");
                throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "商户实名认证处理结果为空");
            }else if (!IfspDataVerifyUtil.equals("0000", soaResult.getRespCode())) {
                throw new IfspBizException(soaResult.getRespCode(), soaResult.getRespMsg());
            }
        }  catch (Exception e) {
            log.error("商户实名认证处理 error:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "商户实名认证处接口调用失败");
        }
        return soaResult;

	}
}
