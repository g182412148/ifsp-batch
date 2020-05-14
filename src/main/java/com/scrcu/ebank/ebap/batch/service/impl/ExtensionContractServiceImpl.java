package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MchtAuthRequest;
import com.scrcu.ebank.ebap.batch.common.dict.MchtAuthHandTypeDict;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.service.ExtensionContractService;
import com.scrcu.ebank.ebap.batch.service.MchtAuthService;
import com.scrcu.ebank.ebap.batch.soaclient.ChnlCenterSoaClientService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>名称 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : zhangb </p>
 * <p>日期 : 2019-09-02  15:38 </p>
 */
@Service
@Slf4j
public class ExtensionContractServiceImpl implements ExtensionContractService {

    @Resource
    private MchtContInfoDao mchtContInfoDao;
    @Override
    public CommonResponse extCon(BatchRequest request) {

        String batchDate = request.getSettleDate();
        if (IfspDataVerifyUtil.isBlank(batchDate)) {
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
        }
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        try{
            Map<String,Object> soaParams = new HashMap<>();
            soaParams.put("contExpEate", DateUtil.parse(batchDate, "yyyyMMdd"));
            soaParams.put("months", "24");//续期两个年
            soaParams.put("autoRenewalFlag", "1");//是否自动续期 0否 1是'
            mchtContInfoDao.update("updateExtCon",soaParams);
            mchtContInfoDao.update("updateExtConTemp",soaParams);
        }catch (Exception e){
            log.error("延长商户合同时间处理异常: ", e);
            //返回结果
            commonResponse.setRespCode(SystemConfig.getSysErrorCode());
            commonResponse.setRespMsg("延长商户合同时间处理异常:" + e.getMessage());
        }
        return commonResponse;
    }
}
