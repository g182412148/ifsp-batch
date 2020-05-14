package com.scrcu.ebank.ebap.batch.service;


import com.scrcu.ebank.ebap.batch.bean.request.InAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.response.InAcctResponse;

public interface BthMerInAccDtlServer {

	InAcctResponse selectUpdateDate (InAcctRequest request);
}
