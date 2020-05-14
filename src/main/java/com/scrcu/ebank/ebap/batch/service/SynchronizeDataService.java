package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.SynchronizeDataRequest;
import com.scrcu.ebank.ebap.batch.bean.response.SynchronizeDataResponse;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface SynchronizeDataService {

	CommonResponse synchronizeStaff() throws Exception;

	CommonResponse synchronizeOrg()throws Exception;

	SynchronizeDataResponse synchronizeOrgCorpId(SynchronizeDataRequest request) throws Exception;

	Boolean canExecute(String taskId) throws Exception;

}
