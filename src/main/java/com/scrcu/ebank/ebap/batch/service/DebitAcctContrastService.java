package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface DebitAcctContrastService {

    CommonResponse debitBillContrast(AcctContrastRequest request) throws Exception;

}
