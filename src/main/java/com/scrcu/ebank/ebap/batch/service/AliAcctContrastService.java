package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface AliAcctContrastService {
    /**
     * 支付宝对账
     * @param request
     * @return
     */
    CommonResponse aliBillContrast(AcctContrastRequest request) ;

}
