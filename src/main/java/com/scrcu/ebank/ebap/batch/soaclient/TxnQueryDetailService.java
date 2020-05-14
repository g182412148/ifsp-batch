package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

public interface TxnQueryDetailService {

    SoaResults txnQueryDetail(SoaParams soaParams);
}
