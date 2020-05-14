package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.FeeContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.FeeContrastResponse;

public interface FeeContrastService {

	FeeContrastResponse wxFeeContrast(FeeContrastRequest request);

	FeeContrastResponse zfbFeeContrast(FeeContrastRequest request);

	FeeContrastResponse unionFeeContrast(FeeContrastRequest request);

}
