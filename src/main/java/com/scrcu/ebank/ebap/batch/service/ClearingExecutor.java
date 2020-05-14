package com.scrcu.ebank.ebap.batch.service;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface ClearingExecutor 
{
	public CommonResponse channelClearing(BatchRequest request) throws Exception;

	void init(String pagyNo,String stlmDate);
    /**
     * 根据订单编号（分片批量）进行清分
     */
    void execute(List<BthMerInAccDtl> inAccDtlList) throws Exception;
    /**
     * 根据订单编号进行清分
     */
    void execute(BthMerInAccDtl inAccDtl) throws Exception;
	
	/**
	 * 查询商户基本信息 ：
	 * @param mchtId ： 商户号
	 * @return
	 */
	MchtBaseInfo getMerBaseInfo(String mchtId);

	/**
	 * 查询服务商基本信息 ：
	 * @param mchtId ： 商户号
	 * @return
	 */
	ParternBaseInfo getServiceBaseInfo(String mchtId);
	
	/**
	 * 查询商户结算信息 ：
	 * @param mchtId ： 商户号
	 * @return
	 */
	MchtContInfo getMerStlInfo(String mchtId);
	
	/**
	 * 查询商户机构信息
	 * @param mchtId ：商户号
	 * @return
	 */
	List<MchtOrgRel> getMerOrgInfo(String mchtId);
	
	/**
	 * 查询渠道分润信息
	 * @param channelNo
	 * @return
	 */
	MchtGainsInfo getMerGainsInfo(String channelNo);

	/**
	 * 查询服务商渠道分润信息
	 * @param channelNo
	 * @return
	 */
	MchtGainsInfo getServiceInfo(String parternId,String channelNo);

	int updateStatus(String pagyNo,String stlmDate);

	public int insertFromTempTable(String pagyNo);
}
