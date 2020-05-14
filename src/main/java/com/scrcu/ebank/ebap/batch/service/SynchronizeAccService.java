package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.SynchronizeDataRequest;
import com.scrcu.ebank.ebap.batch.bean.response.SynchronizeDataResponse;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface SynchronizeAccService {

	CommonResponse synchronizeSettleAcc() throws Exception;

}
