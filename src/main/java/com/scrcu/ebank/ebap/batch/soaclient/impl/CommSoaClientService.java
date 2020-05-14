package com.scrcu.ebank.ebap.batch.soaclient.impl;/*
 * Copyright (C), 2015-2018, 上海睿民互联网科技有限公司
 * Package com.scrcu.ebank.ebap.order.soaClient
 * Author:   shiyw
 * Date:     2018/7/3 下午10:08
 * Description: //模块目的、功能描述      
 * History: //修改记录
 *===============================================================================================
 *   author：          time：                             version：           desc：
 *   shiyw             2018/7/3下午10:08                     1.0                  
 *===============================================================================================
 */

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.utils.CharUtil;
import com.scrcu.ebank.ebap.batch.soaclient.SoaClientService;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.dubbo.factory.SoaClientFacotry;
import com.scrcu.ebank.ebap.exception.IfspTimeOutException;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


public abstract class CommSoaClientService implements SoaClientService {
    private Logger log = IfspLoggerFactory.getLogger(this.getClass());

    @Resource
    private SoaClientFacotry soaClientFacotry;
    /**
     * soa服务调用
     * @param param
     * @param soaCode
     * @return
     */
    public SoaResults invoke(SoaParams param, String soaCode, String version, String groupId) {
        long startTime = System.currentTimeMillis();
        log.info("SOA服务调用:[ 开始:[soa服务:["+soaCode+"]] ]");
        SoaResults soaResults = new SoaResults();
        Map<Object,Object > resultMap = new HashMap<Object,Object >();

        ISoaClient soaClient = soaClientFacotry.getSoaClient(soaCode,version,groupId);
        if(soaClient==null){
            log.error("SOA服务调用失败:[服务"+soaCode+"不存在]");
            resultMap.put(IfspRespCodeEnum.RESP_CODE_PARAM.getCode(), IfspRespCodeEnum.RESP_ERROR.getCode());
            resultMap.put(IfspRespCodeEnum.RESP_MSG_PARAM.getCode(), "SOA服务码找不到:["+soaCode+"]");
            soaResults.setDatas(resultMap);
            return soaResults;
        }
        try {
            soaResults = soaClient.invoke(param);
        } catch (IfspTimeOutException e) {
            log.warn("SOA服务调用超时:[服务"+soaCode+"]",e);
            resultMap.put(IfspRespCodeEnum.RESP_CODE_PARAM.getCode(), IfspRespCodeEnum.RESP_0021.getCode());
            resultMap.put(IfspRespCodeEnum.RESP_MSG_PARAM.getCode(), IfspRespCodeEnum.RESP_0021.getDesc());
            soaResults.setDatas(resultMap);
            return soaResults;
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("SOA服务调用失败:["+e.getMessage()+"]",e);
            resultMap.put(IfspRespCodeEnum.RESP_CODE_PARAM.getCode(), IfspRespCodeEnum.RESP_ERROR.getCode());
            resultMap.put(IfspRespCodeEnum.RESP_MSG_PARAM.getCode(), IfspRespCodeEnum.RESP_ERROR.getDesc()+":[SOA调用EXCEPTION]");
            soaResults.setDatas(resultMap);
            return soaResults;
        }
        if(soaResults == null|| IfspDataVerifyUtil.isBlank(soaResults.getRespCode())){
            resultMap.put(IfspRespCodeEnum.RESP_CODE_PARAM.getCode(), IfspRespCodeEnum.RESP_ERROR);
            resultMap.put(IfspRespCodeEnum.RESP_MSG_PARAM.getCode(), IfspRespCodeEnum.RESP_ERROR.getDesc()+":[soa服务应答空]");
            soaResults.setDatas(resultMap);
            return soaResults;
        }
        /**
         * 处理应答码
         */
        soaResults.getDatas().put(IfspRespCodeEnum.RESP_CODE_PARAM.getCode(),
                CharUtil.subStrRespCode((String)soaResults.getDatas().get(IfspRespCodeEnum.RESP_CODE_PARAM.getCode())));

        long endTime = System.currentTimeMillis();
        log.info("soaResults:["+ IfspFastJsonUtil.mapTOjson(soaResults)+"]");
        log.info("SOA服务调用:[ 结束 , 耗时"+(endTime-startTime)+"ms]");
        return soaResults;
    }

}
