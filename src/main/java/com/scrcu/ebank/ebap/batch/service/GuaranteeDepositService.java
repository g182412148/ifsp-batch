package com.scrcu.ebank.ebap.batch.service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 *补保证金
 * @author ydl
 *
 */
public interface GuaranteeDepositService
{


	CommonResponse guaDep(BatchRequest request);

}
