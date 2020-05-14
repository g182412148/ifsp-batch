package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

/**
 * @author ljy
 * @date 2018-12-29 15:03
 */
public interface ChnlCenterSoaClientService {

    SoaResults mchtAuth(SoaParams params);
}
