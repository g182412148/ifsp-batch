package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface GenMerApprFileService 
{
	/**
	 * 生成商户审核文件
	 */
	public CommonResponse genMerApprFile(BatchRequest request);
}
