package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

/**
 * @author ljy
 * @date 2018-12-29 15:03
 */
public interface AntiFraudSoaService {
    /**
     * 上送反欺诈
     * @param params 请求参数
     * @return soaResults
     */
    SoaResults dataVisor(SoaParams params);

    /**
     * 上送反欺诈结果
     * @param params 请求参数
     * @return soaResults
     */
    SoaResults dataVisorNotify(SoaParams params);
}
