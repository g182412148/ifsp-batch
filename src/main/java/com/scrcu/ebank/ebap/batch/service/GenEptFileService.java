package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 生成增值税系统文件
 * @author ydl
 *
 */
public interface GenEptFileService 
{
	/**
	 * 生成增值税系统文件
	 */
	public CommonResponse genEptChkFile(BatchRequest request);
}
