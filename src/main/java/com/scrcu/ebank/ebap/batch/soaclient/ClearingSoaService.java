package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

public interface ClearingSoaService {

	SoaResults clearingSoa(SoaParams params);

	SoaResults unifyPay(SoaParams params);

	SoaResults unifyPayOtherSel(SoaParams params);

	SoaResults unifyPayQue(SoaParams params);

}
