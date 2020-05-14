package com.scrcu.ebank.ebap.batch.soaclient.impl;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.exception.IfspTimeOutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;


import java.util.HashMap;
import java.util.Map;

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
@Slf4j
public class KeepAccSoaServiceImpl implements KeepAccSoaService {

    @SoaClient(name = "6040980001", version = "1.0.0", group = "604")
    private ISoaClient keepAcc;
    
    @SoaClient(name = "6040980002", version = "1.0.0", group = "604")
    private ISoaClient debitBill;

    @SoaClient(name = "6040980005", version = "1.0.0", group = "604")
    private ISoaClient ibankRevKeepAcc;

    @SoaClient(name = "009.onceKeepAccount")
    private ISoaClient onceKeepAccount;

    @SoaClient(name = "009.onceKeepAccountReverse")
    private ISoaClient onceKeepAccountReverse;

    @SoaClient(name = "onceSupplyAcc")
    private ISoaClient orderOnceSupplyAcc;

    /**
     * 本行记账查询接口
     */
    @SoaClient(name = "6040130002", version = "1.0.0", group = "604")
    private ISoaClient qrcKeepAccRst;

	@Override
	public SoaResults keepAcc(SoaParams soaParams) {

        Map<Object,Object > resultMap = new HashMap<>(2);
		SoaResults results = new SoaResults();
        log.info("---------------记账开始 [request: " + soaParams + "---------------");
        try {
            results = keepAcc.invoke(soaParams);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务6040980001]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [调用本行通道记账接口超时]");
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
        log.info("---------------记账结束 [response: " + results + "---------------");
        return results;
	}
	
	@Override
	public SoaResults debitBill(SoaParams soaParams) {
		SoaResults results;
		log.debug("---------------请求核心对账文件开始 [request: " + soaParams + "---------------");
		results = debitBill.invoke(soaParams);
		log.debug("---------------请求核心对账文件结束 [response: " + results + "---------------");
		return results;
	}

    @Override
    public SoaResults onceKeepAcc(SoaParams soaParams) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------记账开始 [request: " + soaParams + "---------------");
        try {
            results = onceKeepAccount.invoke(soaParams);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务009.onceKeepAccount]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [单笔记账接口超时]");
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
        log.info("---------------记账结束 [response: " + results + "---------------");
        return results;
    }



    @Override
    public SoaResults qrcKeepAccRst(SoaParams soaParams) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------记账结果查询开始 [request: " + soaParams + "---------------");
        try {
            results = qrcKeepAccRst.invoke(soaParams);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务6040130001]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [调用记账查询接口超时]");
            results.setDatas(resultMap);
            return results;
        }catch (Exception e) {
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
        log.info("---------------记账结果查询结束 [response: " + results + "---------------");
        return results;
    }

    @Override
    public SoaResults ibankRevkeepAcc(SoaParams soaParams) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------调用本行通道冲正开始 [request: " + soaParams + "---------------");
        try {
            results = ibankRevKeepAcc.invoke(soaParams);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务6040980005]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [调用本行通道冲正接口超时]");
            results.setDatas(resultMap);
            log.info("---------------调用本行通道冲正结束 [response: " + results + "---------------");
            return results;
        } catch (Exception e) {
            log.error("SOA服务调用失败:["+e.getMessage()+"]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[SOA调用EXCEPTION]");
            results.setDatas(resultMap);
            log.info("---------------调用本行通道冲正结束 [response: " + results + "---------------");
            return results;
        }
        if(results == null|| IfspDataVerifyUtil.isBlank(results.getRespCode())){
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL);
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[soa服务应答空]");
            results.setDatas(resultMap);
            log.info("---------------调用本行通道冲正结束 [response: " + results + "---------------");
            return results;
        }
        log.info("---------------调用本行通道冲正结束 [response: " + results + "---------------");
        return results;
    }

    @Override
    public SoaResults onceRevKeepAcc(SoaParams soaParams) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------单笔冲正开始 [request: " + soaParams + "---------------");
        try {
            results = onceKeepAccountReverse.invoke(soaParams);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务009.onceKeepAccountReverse]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [单笔冲正接口超时]");
            results.setDatas(resultMap);
            log.info("---------------单笔冲正结束 [response: " + results + "---------------");
            return results;
        } catch (Exception e) {
            log.error("SOA服务调用失败:["+e.getMessage()+"]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_NULL.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_NULL.getDesc()+":[SOA调用EXCEPTION]");
            results.setDatas(resultMap);
            log.info("---------------单笔冲正结束 [response: " + results + "---------------");
            return results;
        }
        if(results == null|| IfspDataVerifyUtil.isBlank(results.getRespCode())){
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_NULL);
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_NULL.getDesc()+":[soa服务应答空]");
            results.setDatas(resultMap);
            log.info("---------------单笔冲正结束 [response: " + results + "---------------");
            return results;
        }
        log.info("---------------单笔冲正结束 [response: " + results + "---------------");
        return results;
    }

    @Override
    public SoaResults supplyAcc(SoaParams soaParams) {
        Map<Object,Object > resultMap = new HashMap<>(2);
        SoaResults results = new SoaResults();
        log.info("---------------单笔补记账开始 [request: " + soaParams + "---------------");
        try {
            results = orderOnceSupplyAcc.invoke(soaParams);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务 onceSupplyAcc]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_TIMEOUT.getDesc() +": [单笔补记账接口超时]");
            results.setDatas(resultMap);
            log.info("---------------单笔补记账结束 [response: " + results + "---------------");
            return results;
        } catch (Exception e) {
            log.error("SOA服务调用失败:["+e.getMessage()+"]",e);
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL.getCode());
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[SOA调用EXCEPTION]");
            results.setDatas(resultMap);
            log.info("---------------单笔补记账结束 [response: " + results + "---------------");
            return results;
        }
        if(results == null|| IfspDataVerifyUtil.isBlank(results.getRespCode())){
            resultMap.put(RespEnum.RESP_CODE_PARAM.getCode(), RespEnum.RESP_FAIL);
            resultMap.put(RespEnum.RESP_MSG_PARAM.getCode(), RespEnum.RESP_FAIL.getDesc()+":[soa服务应答空]");
            results.setDatas(resultMap);
            log.info("---------------单笔补记账结束 [response: " + results + "---------------");
            return results;
        }
        log.info("---------------单笔补记账结束 [response: " + results + "---------------");
        return results;
    }
}
