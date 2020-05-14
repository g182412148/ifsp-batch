package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface SendMsg4InvalidAccService {

	CommonResponse sendMsg4InvalidAcc(BatchRequest request) throws Exception;

}
