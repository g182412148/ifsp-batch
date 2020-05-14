package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.text.ParseException;

public interface FillUnionTxnService {
    CommonResponse fillUnionTxn(BatchRequest request) throws ParseException;
}
