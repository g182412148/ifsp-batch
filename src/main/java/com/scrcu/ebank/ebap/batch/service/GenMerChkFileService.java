package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OfflineCreatMerChkFileRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 生成商户对账文件
 * @author ydl
 *
 */
public interface GenMerChkFileService 
{
	/**
	 * 生成商户对账文件
	 */
	public CommonResponse genMerChkFile(BatchRequest request);
}
