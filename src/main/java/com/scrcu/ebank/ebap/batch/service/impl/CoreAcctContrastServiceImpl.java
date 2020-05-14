package com.scrcu.ebank.ebap.batch.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.dao.*;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.DebitTranInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.request.KeepAccountRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.common.msg.IBankMsg;
import com.scrcu.ebank.ebap.batch.service.CoreAcctContrastService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class CoreAcctContrastServiceImpl implements CoreAcctContrastService {

	@Resource
	private CoreRecoErrDao coreRecoErrDao;

//	@Resource
//	private KeepAccInfoDao keepAccInfoDao;
//
//	@Resource
//	private KeepRecoInfoDao keepRecoInfoDao;
//
//	@Resource
//	private CoreBillInfoDao coreBillInfoDao;
	
	@Override
	public AcctContrastResponse keepAccCoreContrast(AcctContrastRequest request) throws Exception {
		long start=System.currentTimeMillis();
		/********* 初始化响应对象 ***********/
		AcctContrastResponse acctContrastResponse = new AcctContrastResponse();
		/***** 获取请求参数值 ********/
		String settleDate = request.getSettleDate();// 交易日期

		log.info("---------------------核心vs记账表对账开始，交易日期【{}】--------------------",settleDate);
		String recoDate = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
//		String doubtDate = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, -1);
//		log.info("doubtDate:" + doubtDate);
//		/***
//		 * 2.还原核心记账流水表数据（考虑是否只还原前一天数据）
//		 */
//		int recoveryCount = coreBillInfoDao.recovery(IfspDateTime.parseDate(recoDate,"yyyyMMdd"));
//		log.info("还原对账文件明细表数据{}条", recoveryCount);
////        int recoveryCountDubious = coreBillInfoDao.recoveryDubious(recoDate);
////        log.info("还原对账文件明细表前一天可疑数据数据{}条", recoveryCountDubious);
//		/***
//		 * 3.还原核心记账流水表数据（考虑是否只还原前一天数据）
//		 */
//		int keepCount = keepRecoInfoDao.recovery(IfspDateTime.parseDate(recoDate,"yyyyMMdd"));
//		log.info("还原记账对账流水表数据{}条", keepCount);
//		int keepCountDubious = keepRecoInfoDao.recoveryDubious(IfspDateTime.parseDate(recoDate,"yyyyMMdd"));
//		log.info("还原记账对账流水表可疑数据{}条", keepCountDubious);
//		/**
//		 * 4.还原记账表数据keep_acc_info 当天数据（交易日）
//		 */
//		int keepRecCount = keepAccInfoDao.recovery(settleDate);
//		log.info("还原记账表数据{}条", keepRecCount);
//		/**
//		 * 4.1还原记账表数据keep_acc_info 前一天可疑数据（交易日前一天）
//		 */
//		int keepRecCountDou = keepAccInfoDao.recovery(doubtDate);
//		log.info("还原核心记账流水表[{}]可疑数据{}条", doubtDate, keepRecCountDou);

		int clear = coreRecoErrDao.clear(IfspDateTime.parseDate(recoDate,"yyyyMMdd"));
		log.info("清理差错表数据【{}】条，耗时【{}】", clear,System.currentTimeMillis()-start);

		int count=coreRecoErrDao.updateCoreDubiousOrError(recoDate);
		log.info("处理核心可疑、单边记录【{}】条，耗时【{}】", count,System.currentTimeMillis()-start);

		count=coreRecoErrDao.updateLocalDubiousOrError(recoDate);
		log.info("处理本地可疑、单边记录【{}】条，耗时【{}】", count,System.currentTimeMillis()-start);

		count=coreRecoErrDao.insertCoreError(recoDate);
		log.info("插入核心查错记录【{}】条，耗时【{}】", count,System.currentTimeMillis()-start);

		count=coreRecoErrDao.insertLocalError(recoDate);
		log.info("插入本地查错记录【{}】条，耗时【{}】", count,System.currentTimeMillis()-start);

		count=coreRecoErrDao.updateCoreSuccess(recoDate);
		log.info("处理核心对平数据【{}】条，耗时【{}】", count,System.currentTimeMillis()-start);

		count=coreRecoErrDao.updateLocalSuccess(recoDate);
		log.info("处理本地对平数据【{}】条，耗时【{}】", count,System.currentTimeMillis()-start);

		String orderTmStart=IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, -31)+"000000";
		String orderTmEnd=settleDate+"999999";
		count=coreRecoErrDao.updateAccInfo(recoDate,orderTmStart,orderTmEnd);
		log.info("处理记账表数据【{}】条，耗时【{}】", count,System.currentTimeMillis()-start);

		log.info("---------------------核心vs记账表对账结束，交易日期【{}】，总耗时【{}】--------------------",settleDate,System.currentTimeMillis()-start);

		return acctContrastResponse;
	}
	


}
