package com.scrcu.ebank.ebap.batch.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.scrcu.ebank.ebap.batch.bean.request.PagyFeeInAccRequest;
import com.scrcu.ebank.ebap.batch.bean.response.PagyFeeInAccResponse;
import com.scrcu.ebank.ebap.batch.dao.TpamAtWechatTxnInfoDao;
import com.scrcu.ebank.ebap.batch.service.PagyFeeInAccService;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class PagyFeeInAccServiceImpl implements PagyFeeInAccService {
	
	@Resource
	private TpamAtWechatTxnInfoDao tpamAtWechatTxnInfoDao;
	
	/**
     * 核心入账文件(通道手续费入账汇总)上传
     * @param request
     * @return
     */
	@Override
	public PagyFeeInAccResponse pagyFeeInAccUpload(PagyFeeInAccRequest request) {
		return null;
	}
	
	/**
     *  通道手续费入账申请
     * @param request
     * @return
     */
	@Override
	public PagyFeeInAccResponse pagyFeeInAccApply(PagyFeeInAccRequest request) {
		return null;
	}
	
	/**
     * 核心入账结果文件下载解析及本地状态更新
     * @param request
     * @return
     */
	@Override
	public PagyFeeInAccResponse pagyFeeInAccFeedback(PagyFeeInAccRequest request) {
		return null;
	}

}
