package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.common.dict.ReRunFlagEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RealTmFlagEnum;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import com.scrcu.ebank.ebap.batch.dao.BthChkRsltInfoDao;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.dao.KeepAcctInfoDao;
import com.scrcu.ebank.ebap.batch.service.KeepAcctContrastService;

import lombok.extern.slf4j.Slf4j;

/**
 * 名称：〈通道对账〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年08月03日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
@Service
@Slf4j
public class KeepAcctContrastServiceImpl implements KeepAcctContrastService {

	@Resource
	private BthChkRsltInfoDao bthChkRsltInfoDao;

	@Resource
	private KeepAccInfoDao keepAccInfoDao;

	
	@Resource
	private KeepAcctInfoDao keepAcctInfoDao;

	@Override
	public AcctContrastResponse rsltKeepAccContrast(AcctContrastRequest request) throws Exception {
		log.info("---------------------补记订单记账流水任务开始--------------------");
		/***** 获取请求参数值 ********/
		AcctContrastResponse acctContrastResponse = new AcctContrastResponse();
		// 对账成功日期字符串型
		String chkSuccDt = IfspDateTime.plusTime(request.getSettleDate(), "yyyyMMdd", IfspTimeUnit.DAY, 1);

		// 还原对账结果表对账成功日期检测到未记账而更新状态的数据
		log.info("更新对账结果表 处理:[ -->将挂起状态更新为待清算stlm_st(00)... ]");
		restoreChkRsltInfo(chkSuccDt);

		log.info("================开始处理对账成功日期为[{}]下的对账结果表数据=================",chkSuccDt);

		// 按照对账成功日期来查询漏记账的订单号
		List<BthChkRsltInfo> bthChkRsltInfos = getBthChkRsltInfos(chkSuccDt);
		int size = bthChkRsltInfos.size();
		log.info("未记账的订单总条数为[{}]条",size);
		for (BthChkRsltInfo bthChkRsltInfo : bthChkRsltInfos) {
			log.info("================订单[{}]未在清算中心记账,根据订单中心记账信息补记 start============",bthChkRsltInfo.getOrderSsn());
			/* 根据订单中心记账表初始化清算中心记账表*/
			initKeepAccInfo(bthChkRsltInfo.getOrderSsn());
			log.info("================订单[{}]未在清算中心记账,根据订单中心记账信息补记 end  ============",bthChkRsltInfo.getOrderSsn());
			size-- ;
			log.info("剩余待补记账订单条数为[{}]",size);
		}
		log.info("---------------------补记订单记账流水任务结束--------------------");
		return acctContrastResponse;
	}

	/**
	 * 根据对账成功日期 , 清算状态还原数据状态为待清算
	 * @param chkSuccDt
	 */
	private void restoreChkRsltInfo(String chkSuccDt)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("chkSuccDt", chkSuccDt);
		params.put("stlmSt",Constans.STLM_ST_02);
		bthChkRsltInfoDao.update("restoreByChkSuccDt",params);
	}


	/**
	 * 拿出对账成功日期下没有记账的订单
	 * @param chkSuccDt
	 * @return
	 */
	private List<BthChkRsltInfo> getBthChkRsltInfos(String chkSuccDt)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("chkSuccDt", chkSuccDt);
		return bthChkRsltInfoDao.selectList("queryByChkSuccDt", params);
	}


	/**
	 * 根据订单中心记账表补清算中心记账表 , 补不到则挂起不清算
	 * @param orderSsn
	 */
	private void initKeepAccInfo(String orderSsn)
	{
		// 查询订单中心记账表初始化记账表
		List<KeepAcctInfo> acctInfos = keepAcctInfoDao.selectByOrderSsn(orderSsn);
		if (IfspDataVerifyUtil.isNotEmptyList(acctInfos)){
			log.info("===============订单[{}]待补记账流水[{}]条==============",orderSsn,acctInfos.size());
			SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
			for (KeepAcctInfo acctInfo : acctInfos) {
				//排除线下订单记商户账
				if(IfspDataVerifyUtil.equals("10", acctInfo.getKeepType())){
					continue;
				}

				KeepAccInfo keepAccInfo = new KeepAccInfo();
				//判断订单号线上线下
				if(IfspDataVerifyUtil.equals("00000000000000000000000000000000", acctInfo.getSubOrderSsn())){
					keepAccInfo.setOrderSsn(acctInfo.getOrderSsn());
				}else{
					keepAccInfo.setOrderSsn(acctInfo.getSubOrderSsn());
				}
				keepAccInfo.setCoreSsn(ConstantUtil.getRandomNum(20));
				keepAccInfo.setKeepAccTime(form.format(acctInfo.getOrderTm()));
				keepAccInfo.setInAccNo(acctInfo.getCredAcctNo());
				keepAccInfo.setOutAccNo(acctInfo.getDebtAcctNo());
                keepAccInfo.setKeepAccType(Constans.KEEP_ACC_TYPE_OTHER);
				// 直接状态置为失败清分时处理
				keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);
				keepAccInfo.setTransAmt(IfspDataVerifyUtil.isNotBlank(acctInfo.getTxnAmt())? new BigDecimal(acctInfo.getTxnAmt()).longValue() : 0L);
				keepAccInfo.setInAccNoName(acctInfo.getCredAcctName());
				keepAccInfo.setOutAccNoName(acctInfo.getDebtAcctName());
				keepAccInfo.setTxnDesc(acctInfo.getTxnDesc());
				keepAccInfo.setTxnCcyType(Constans.CCY_TYPE);
				//暂且用于标记是否为日终补账生成
				keepAccInfo.setReserved1("DAYNIGHT");
				keepAccInfo.setKeepAccSeq(Short.valueOf(acctInfo.getOrderSeq()));
				keepAccInfo.setChkSt(Constans.CHK_STATE_01);
				keepAccInfo.setChkRst(Constans.CHK_RST_00);
				keepAccInfo.setRerunFlag(ReRunFlagEnum.RE_RUN_FLAG_TRUE.getCode());
				keepAccInfo.setSubOrderSsn(acctInfo.getSubOrderSsn());
				keepAccInfo.setRealTmFlag(RealTmFlagEnum.REAL_TM_FALSE.getCode());
				// 唯一索引!!!
				keepAccInfo.setUniqueSsn(acctInfo.getUniqueSsn());
				// 默认批次0
                keepAccInfo.setVerNo("0");
				// 唯一索引冲突则不插入
				keepAccInfoDao.insertIgnoreExist(keepAccInfo);
			}
		}else {
			log.info("订单[{}]查不到记账记录,清算挂起",orderSsn);
			updChkRsltStlmSt(orderSsn);

		}
	}

	/**
	 * 根据订单号更新记对账成功表状态
	 * @param orderSsn
	 */
	private void updChkRsltStlmSt(String orderSsn)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("orderSsn", orderSsn);
		params.put("stlmSt", Constans.STLM_ST_02);
		bthChkRsltInfoDao.update("updChkRsltStlmSt",params);
	}

}
