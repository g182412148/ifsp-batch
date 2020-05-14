package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface ClearingService {



	CommonResponse callCH1730();

	CommonResponse updateStat();


}
