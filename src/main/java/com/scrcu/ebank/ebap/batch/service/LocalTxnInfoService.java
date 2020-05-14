package com.scrcu.ebank.ebap.batch.service;

import java.text.ParseException;

import com.scrcu.ebank.ebap.batch.bean.request.LocalTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.response.LocalTxnInfoResponse;
/**
 *名称：<通道流水抽取> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：通道流水抽取<br>
 */
public interface LocalTxnInfoService {
	/**
	 * 微信通道流水抽取
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	LocalTxnInfoResponse getWxAtTxnInfo(LocalTxnInfoRequest request) throws Exception;
	/**
	 * 支付宝通道流水抽取
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	LocalTxnInfoResponse getAliAtTxnInfo(LocalTxnInfoRequest request) throws Exception;
	/**
	 * 银联二维码流水抽取
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	LocalTxnInfoResponse getUnionTxnInfo(LocalTxnInfoRequest request) throws Exception;
	/**
	 * 本行通道流水抽取
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	LocalTxnInfoResponse getIbankTxnInfo(LocalTxnInfoRequest request) throws Exception;

    /**
     * 银联全渠道流水抽取
     * @param request
     * @return
     */
    LocalTxnInfoResponse getTotalUnionTxn(LocalTxnInfoRequest request) throws ParseException;
}
