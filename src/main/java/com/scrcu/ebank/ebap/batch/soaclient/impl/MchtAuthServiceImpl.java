package com.scrcu.ebank.ebap.batch.soaclient.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.bean.request.MchtAuthRequest;
import com.scrcu.ebank.ebap.batch.common.dict.MchtAuthHandTypeDict;
import com.scrcu.ebank.ebap.batch.service.MchtAuthService;
import com.scrcu.ebank.ebap.batch.soaclient.ChnlCenterSoaClientService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>名称 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : zhangb </p>
 * <p>日期 : 2019-09-02  15:38 </p>
 */
@Service
@Slf4j
public class MchtAuthServiceImpl implements MchtAuthService {
    @Resource
    private ChnlCenterSoaClientService chnlCenterSoaClientService;
    @Override
    public CommonResponse auth(MchtAuthRequest request) {
        log.info("实名认证处理任务开始,任务类型:{}", MchtAuthHandTypeDict.get(request.getAuthHandType()).getDesc());
        CommonResponse commonResponse = new CommonResponse();
        SoaParams soaParams = new SoaParams();
        soaParams.put("authHandType",request.getAuthHandType());
        soaParams.put("reqSsn",IfspId.getUUID32());//请求流水
        soaParams.put("reqTm",IfspDateTime.getYYYYMMDDHHMMSS());//请求时间
        SoaResults soaResults = chnlCenterSoaClientService.mchtAuth(soaParams);
        if (!StringUtils.equals(soaResults.getRespCode(), IfspRespCodeEnum.RESP_SUCCESS.getCode())) {
            log.info("实名认证调用通道中心失败[{}],[{}]",soaResults.getRespCode(),soaResults.getRespMsg());
            throw new IfspBizException(soaResults.getRespCode(), soaResults.getRespMsg());
        }
        log.info("实名认证处理任务结束,任务类型:{}", MchtAuthHandTypeDict.get(request.getAuthHandType()).getDesc());
            return commonResponse;

    }
}
