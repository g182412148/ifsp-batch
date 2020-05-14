package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

public interface FillUnionTxnSoaService {

    SoaResults fillUnionTxn(SoaParams soaParams);
}
