package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.OrgRepeMergRequest;

public interface OrgRepeMergUpdateInterface {
	void updateOrgMethod(OrgRepeMergRequest req);

	String getDesc();
}
