package com.scrcu.ebank.ebap.batch.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.request.BillDownloadRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BillDownloadResponse;
/**
 *名称：<通道对账单下载> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/21 <br>
 *作者：lijingbo <br>
 *说明：通道对账单下载<br>
 */
public interface BillDownloadService {
	/**
	 * 微信对账单下载
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	BillDownloadResponse wxBillDownload(BillDownloadRequest request) throws Exception;
	/**
	 * 支付宝对账单下载
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	BillDownloadResponse aliBillDownload(BillDownloadRequest request, HashMap<Object, Object> hashMap) throws Exception;
	/**
	 * 银联通道对账单下载
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	BillDownloadResponse unionBillDownload(BillDownloadRequest request, HashMap<Object, Object> hashMap) throws Exception;

	/**
	 * 银联对账单下载 add 20190514 yangqi
	 * @param request
	 * @param hashMap
	 * @return
	 * @throws Exception
	 */
	BillDownloadResponse unionAllBillDownload(BillDownloadRequest request,HashMap<Object, Object> hashMap) throws Exception;
	/**
	 * 核心记账文件下载及加载
	 * @param request
	 * @return
	 * @throws Exception
	 */
	BillDownloadResponse coreBillFileDownload(BillDownloadRequest request) throws Exception;

	/**
	 * 本行借记卡核心对账单下载及加载
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	BillDownloadResponse debitBillDownload(BillDownloadRequest request) throws Exception;
	/**
	 * 本行贷记卡核心对账单下载及加载
	 * @param request
	 * @return
	 * @throws Exception
	 */
	BillDownloadResponse creditBillDownload(BillDownloadRequest request)throws Exception;
	/**
	 * 核心记账文件下载及加载
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	BillDownloadResponse debitTallyDownload(BillDownloadRequest request) throws Exception;
	/**
	 * 银联品牌服务费文件下载及加载
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	BillDownloadResponse brandFeeBillDownload(MerRegRequest request, HashMap<Object, Object> hashMap) throws Exception;

	/**
	 * 银联品牌服务费文件下载及加载new
	 * @param request
	 * @return
	 * @throws Exception
	 */
	BillDownloadResponse brandFeeBillDownloadNew(MerRegRequest request, HashMap<Object, Object> hashMap) throws Exception;


	/**
     * 银联全渠道文件下载及加载
     * @param request
     * @param map
     * @return
     */
    BillDownloadResponse upacpBillDownload(BillDownloadRequest request, Map<Object, Object> map) throws ParseException;
}
