package com.scrcu.ebank.ebap.batch.soaclient.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.soaclient.FillUnionTxnSoaService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.exception.IfspTimeOutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FillUnionTxnSoaServiceImpl implements FillUnionTxnSoaService {


    @SoaClient(name = "6071500001", version = "1.0.0", group = "607")
    private ISoaClient fillUnionTxn;



    @Override
    public SoaResults fillUnionTxn(SoaParams soaParams) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------补单开始 [request: " + soaParams + "---------------");
        try {
            results = fillUnionTxn.invoke(soaParams);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务6071500001]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [调用银联补单接口超时]");
            results.setDatas(resultMap);
            return results;
        } catch (Exception e) {
            log.error("SOA服务调用失败:["+e.getMessage()+"]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[SOA调用EXCEPTION]");
            results.setDatas(resultMap);
            return results;
        }
        if(results == null|| IfspDataVerifyUtil.isBlank(results.getRespCode())){
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL);
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[soa服务应答空]");
            results.setDatas(resultMap);
            return results;
        }
        log.info("---------------补单结束 [response: " + results + "---------------");
        return results;
    }
}
