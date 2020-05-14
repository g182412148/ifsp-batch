package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.OrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.bean.request.SelectOrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.bean.response.SelectOrgRepeMergResponse;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface OrgRepeMergService {

	CommonResponse orgRepeMerg(OrgRepeMergRequest req);

	SelectOrgRepeMergResponse selectOrgRepeMerg(SelectOrgRepeMergRequest req);

}
