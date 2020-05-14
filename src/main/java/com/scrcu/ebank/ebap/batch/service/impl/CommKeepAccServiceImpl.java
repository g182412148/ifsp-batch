package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.KeepAccRespEnum;
import com.scrcu.ebank.ebap.batch.service.CommKeepAccService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CommKeepAccServiceImpl implements CommKeepAccService {

    @Resource
    private KeepAccSoaService keepAccSoaService;

    /**
     * 查询单笔记账状态函数
     * @param pagyPayTxnSsn
     * @return
     */
    @Override
    public Map<String ,String> qrcKeepAccRst(String pagyPayTxnSsn) {

        Map<String ,String> map = new HashMap<>();
        SoaParams params = new SoaParams();
        //通道支付请求流水号
        params.put("pagyPayTxnSsn", pagyPayTxnSsn);
        SoaResults soaResults = keepAccSoaService.qrcKeepAccRst(params);
        // 如果通信成功
        if (KeepAccRespEnum.RESP_SUCCESS.getCode().equals(soaResults.getRespCode())){
            // 获取状态
            if (KeepAccRespEnum.RESP_SUCCESS.getCode().equals(soaResults.get("origRespCode"))){
                map.put("state",Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                map.put("respCode",String.valueOf(soaResults.get("origRespCode")));
                map.put("respMsg",String.valueOf(soaResults.get("origRespMsg")));
                return map;
            }else {
                map.put("state",Constans.KEEP_ACCOUNT_STAT_FAIL);
                map.put("respCode",String.valueOf(soaResults.get("origRespCode")));
                map.put("respMsg",String.valueOf(soaResults.get("origRespMsg")));
                return map;
            }
        }else {
            return null;
        }
    }
}
