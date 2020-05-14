package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

public interface MchtCenterSoaClientService {

	SoaResults querySubMchtInfo(SoaParams params);

	SoaResults getAreaNameByAreaNo(SoaParams params);

	SoaResults getLegalIfsOrg(SoaParams params);

}
