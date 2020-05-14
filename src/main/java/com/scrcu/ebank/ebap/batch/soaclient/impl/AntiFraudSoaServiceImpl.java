package com.scrcu.ebank.ebap.batch.soaclient.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.soaclient.AntiFraudSoaService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.exception.IfspTimeOutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ljy
 * @date 2018-12-29 15:03
 */
@Service
@Slf4j
public class AntiFraudSoaServiceImpl implements AntiFraudSoaService {
    /**
     * 上送反欺诈
     */
    @SoaClient(name = "dataVisor")
    private ISoaClient dataVisor;

    /**
     * 上送反欺诈结果
     */
    @SoaClient(name = "dataVisorNotify")
    private ISoaClient dataVisorNotify;

    @Override
    public SoaResults dataVisor(SoaParams params) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------上送反欺诈开始 [request: " + params + "---------------");
        try {
            results = dataVisor.invoke(params);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务dataVisor]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [调用上送反欺诈接口超时]");
            results.setDatas(resultMap);
            return results;
        }

        if(results == null|| IfspDataVerifyUtil.isBlank(results.getRespCode())){
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL);
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[服务dataVisor应答为空]");
            results.setDatas(resultMap);
            return results;
        }

        log.info("---------------上送反欺诈结束 [response: " + results + "---------------");

        return results;
    }

    @Override
    public SoaResults dataVisorNotify(SoaParams params) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------上送反欺诈结果开始 [request: " + params + "---------------");
        try {
            results = dataVisorNotify.invoke(params);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务dataVisorNotify]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [调用上送反欺诈结果接口超时]");
            results.setDatas(resultMap);
            return results;
        }

        if(results == null|| IfspDataVerifyUtil.isBlank(results.getRespCode())){
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL);
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[服务dataVisorNotify应答为空]");
            results.setDatas(resultMap);
            return results;
        }

        log.info("---------------上送反欺诈结果结束 [response: " + results + "---------------");

        return results;
    }
}
