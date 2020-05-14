package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MchtAuthRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 延长商户合同
 * @author ydl
 *
 */
public interface ExtensionContractService
{


	CommonResponse extCon(BatchRequest request);
}
