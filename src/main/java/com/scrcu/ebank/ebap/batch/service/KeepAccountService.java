package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.KeepAccountRequest;
import com.scrcu.ebank.ebap.batch.bean.response.KeepAccountResponse;

public interface KeepAccountService {

	KeepAccountResponse nightKeepAccount(KeepAccountRequest request);

}
