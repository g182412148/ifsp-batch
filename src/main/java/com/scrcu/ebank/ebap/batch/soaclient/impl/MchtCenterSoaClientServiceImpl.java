package com.scrcu.ebank.ebap.batch.soaclient.impl;

import org.springframework.stereotype.Service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.soaclient.MchtCenterSoaClientService;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class MchtCenterSoaClientServiceImpl implements MchtCenterSoaClientService {

	
	@SoaClient(name = "001.QuerySubMchtInfo")
    private ISoaClient querySubMchtInfo;
	
	@Override
	public SoaResults querySubMchtInfo(SoaParams params) {
		log.info("查询商户分店信息开始, 请求报文:" + params);
        SoaResults result = null;
        try {
            result = querySubMchtInfo.invoke(params);
        } catch (Exception e) {
            log.info("商户分店信息查询结束, 发生错误", e);
        }
        if (result == null) {
            log.info("商户分店信息查询结果为空!");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "商户分店信息查询结果为空");
        } else if (!IfspDataVerifyUtil.equals(
        		"0000", result.getRespCode())) {
            throw new IfspBizException(result.getRespCode(), result.getRespMsg());
        }
        return result;
	}


    @SoaClient(name = "001.GetAreaNameByAreaNo")
    private ISoaClient getAreaNameByAreaNo;
    @Override
    public SoaResults getAreaNameByAreaNo(SoaParams params) {
        log.info("查询省市区名称信息开始, 请求报文:" + params);
        SoaResults result = null;
        try {
            result = getAreaNameByAreaNo.invoke(params);
        } catch (Exception e) {
            log.info("商户省市区名称查询结束, 发生错误", e);
        }
        if (result == null) {
            log.info("商户省市区名称查询结果为空!");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "没有查到对应地区的省市区名称");
        } else if (!IfspDataVerifyUtil.equals("0000", result.getRespCode())) {
            throw new IfspBizException(result.getRespCode(), result.getRespMsg());
        }
        return result;
    }

    @SoaClient(name = "001.GetLegalIfsOrg")
    private ISoaClient getLegalIfsOrg;
    @Override
    public SoaResults getLegalIfsOrg(SoaParams params) {
        log.info("查询法人机构信息开始, 请求报文:" + params);
        SoaResults result = null;
        try {
            result = getLegalIfsOrg.invoke(params);
        } catch (Exception e) {
            log.info("法人机构查询结束, 发生错误", e);
        }
        if (result == null) {
            log.info("法人机构查询结果为空!");
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "没有查到对应地区的法人机构");
        } else if (!IfspDataVerifyUtil.equals("0000", result.getRespCode())) {
            throw new IfspBizException(result.getRespCode(), result.getRespMsg());
        }
        return result;
    }

}
