package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.PagyFeeInAccRequest;
import com.scrcu.ebank.ebap.batch.bean.response.PagyFeeInAccResponse;

public interface PagyFeeInAccService {

	PagyFeeInAccResponse pagyFeeInAccUpload(PagyFeeInAccRequest request);

	PagyFeeInAccResponse pagyFeeInAccApply(PagyFeeInAccRequest request);

	PagyFeeInAccResponse pagyFeeInAccFeedback(PagyFeeInAccRequest request);

}
