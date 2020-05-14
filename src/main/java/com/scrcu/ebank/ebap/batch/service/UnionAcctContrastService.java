package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;

public interface UnionAcctContrastService {

	AcctContrastResponse unionBillContrast(AcctContrastRequest request) throws Exception;

	AcctContrastResponse unionBillContrastNew(AcctContrastRequest request) throws Exception;

}
