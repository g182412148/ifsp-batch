package com.scrcu.ebank.ebap.batch.soaclient.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.soaclient.TxnQueryDetailService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.exception.IfspTimeOutException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j
public class TxnQueryDetailServiceImpl implements TxnQueryDetailService {

    /**
     * 用户交易详情查询
     */
    @SoaClient(name = "txnDetailQuery")
    private ISoaClient txnDetailQuery;

    @Override
    public SoaResults txnQueryDetail(SoaParams soaParams) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------用户交易详情查询开始 [request: " + soaParams + "---------------");
        try {
            results = txnDetailQuery.invoke(soaParams);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务txnDetailQuery]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [调用订单系统用户交易详情查询接口超时]");
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
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[soa服务应答空]");
            results.setDatas(resultMap);
            return results;
        }
        log.info("---------------用户交易详情查询结束 [response: " + results + "---------------");
        return results;
    }
}
