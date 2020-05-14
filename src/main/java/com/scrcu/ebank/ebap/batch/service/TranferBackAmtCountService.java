package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 针对结算方式为先结算给上级商户，在结算给下级商户的结算模式
 * 在结算给上级商户的动作完成后，从上级商户的结算账户划款回一级商户待结算账户
 * step1:统计每个商户的应划账金额，写入转账信息表
 * @author ydl
 *
 */
public interface TranferBackAmtCountService 
{
	CommonResponse getTransferAmtInfo();
}
 