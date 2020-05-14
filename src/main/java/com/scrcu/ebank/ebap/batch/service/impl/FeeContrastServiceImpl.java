package com.scrcu.ebank.ebap.batch.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.request.FeeContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.FeeContrastResponse;
import com.scrcu.ebank.ebap.batch.dao.BthChkRsltInfoDao;
import com.scrcu.ebank.ebap.batch.dao.BthPagyChkErrInfoDao;
import com.scrcu.ebank.ebap.batch.service.FeeContrastService;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class FeeContrastServiceImpl implements FeeContrastService {
	@Resource
	private BthChkRsltInfoDao bthChkRsltInfoDao;
	
//	@Resource
//	private BthPagyChkErrInfoDao bthPagyChkErrInfoDao;
	/**
     * 微信通道手续费对账
     * @param request
     * @return
     */
	@Override
	public FeeContrastResponse wxFeeContrast(FeeContrastRequest request) {
		/*****获取请求参数值********/
		String pagySysNo = request.getPagySysNo();//通道系统编号
		String settleDate = request.getSettleDate();// 清算日期
//		Date strToDate = IfspDateTime.strToDate(settleDate, IfspDateTime.YYYY_MM_DD);
		/*********初始化响应对象***********/
		FeeContrastResponse feeContrastResponse = new FeeContrastResponse();
//		1.	根据清算日期和通道系统编号查询对账结果表数据（对平流水）
		List<BthChkRsltInfo> bthChkRsltInfoList= bthChkRsltInfoDao.queryByPagySysNoAndDate(pagySysNo,settleDate);
//		2.	根据对账结果表数据（通道支付内部流水号）取得与之对应的BTH_PAGY_LOCAL_INFO和BTH_WX_FILE_DET信息。
		for (BthChkRsltInfo bthChkRsltInfo : bthChkRsltInfoList) {
			String pagyPayTxnSsn = bthChkRsltInfo.getPagyPayTxnSsn();//通道支付内部流水号
			
		}
//		3.	根据通道编号查询费率（费率来自哪？），根据BTH_PAGY_LOCAL_INFO的交易金额计算通道手续费。
		
//		4.	根据BTH_WX_FILE_DET的FEE_AMT（手续费金额）与计算得出的手续费比较
		
//		5.	不一致：插入差错表（1.三方手续费多？2.本地手续费多，忽略 不移除对账结果），一致则跳过。
		return feeContrastResponse;
	}
	
	/**
     * 支付宝通道手续费对账
     * @param request
     * @return
     */
	@Override
	public FeeContrastResponse zfbFeeContrast(FeeContrastRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * 银联通道手续费对账
     * @param request
     * @return
     */
	@Override
	public FeeContrastResponse unionFeeContrast(FeeContrastRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
