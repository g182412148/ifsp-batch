package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 描述 </br>
 *
 * @author M.chen
 * 2019/6/13 15:08
 */
public interface EcifService {
    CommonResponse ecifUpdateMchtInfo(BatchRequest request);
}
