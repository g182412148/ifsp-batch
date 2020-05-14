package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;

public interface AcctContrastService {

	AcctContrastResponse wxBillContrast(AcctContrastRequest request) throws Exception;

}
