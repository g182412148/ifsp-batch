package com.scrcu.ebank.ebap.batch.service;

import java.text.ParseException;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
/**
 *名称：<直连微信对账单下载> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *说明：对账单下载<br>
 */
public interface DirectWxBillDownloadService {
	/**
	 * 微信对账单下载
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	CommonResponse wxBillDownload(BatchRequest request) throws Exception;
}
