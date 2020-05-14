package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.PagyTxnSsnRequest;
import com.scrcu.ebank.ebap.batch.bean.response.TpamTxnSsnResponse;
import com.scrcu.ebank.ebap.batch.common.dict.AcctTypeId;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.PagyServiceChargeRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import com.scrcu.ebank.ebap.batch.dao.PagyServiceChargeDao;
import com.scrcu.ebank.ebap.batch.service.PagyServiceChargeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 名称：〈计算手续费〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月12日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
@Service("pagyServiceChargeService")
@Slf4j
public class PagyServiceChargeServiceImpl implements PagyServiceChargeService {
	
    @Resource
    private PagyServiceChargeDao pagyServiceChargeDao;
	

	@Override
	public CommonResponse wXCalculateServiceCharge(PagyServiceChargeRequest request) throws Exception {

	    //交易日期
        Date chkErrDt = DateUtil.getDate(request.getSettleDate());
        //获取对账日期
        Date recoDate = DateUtil.getAfterDate(request.getSettleDate(), 1);
        //通道号
        String pagyNo = "605000000000001";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        CommonResponse response = new CommonResponse();
		BigDecimal fee_min = new BigDecimal(0);
        BigDecimal fee_max = new BigDecimal(Double.MAX_VALUE);
		//先清理要重跑的对账差错信息
		log.info("---------------------清理差错数据--------------------");
		pagyServiceChargeDao.deleteByDateErrTpAndPagySysNo(chkErrDt, Constans.ERR_TP_03,Constans.WX_SYS_NO+" ");
		
		//根据清算日期和通道系统编号(CHAR(4)  第四位为空格) 查询对账结果表通道流水号
        log.info("-------------------根据清算日期和通道系统编号查询待计算微信手续费条数--------------------");
        List<String> pagyPayTxnSsnList = pagyServiceChargeDao.queryByPagySysNoAndSuccDate(Constans.WX_SYS_NO+" ",recoDate);
        if(pagyPayTxnSsnList==null ||pagyPayTxnSsnList.size()==0){
            response.setRespCode("0000");
            response.setRespMsg("通道对账成功结果表数据为空!");
            return response;
        }
        log.info("-------------------查询到"+pagyPayTxnSsnList.size()+"条数据--------------------");

        log.info("-------------------查询通道费率配置信息--------------------");
        PagyFeeCfgDet pagyFeeCfgDet = pagyServiceChargeDao.queryPagyFeeCfgByPagyNo(pagyNo);
        if (IfspDataVerifyUtil.isBlank(pagyFeeCfgDet) ||
                !("01".equals(pagyFeeCfgDet.getFeeTp())||"02".equals(pagyFeeCfgDet.getFeeTp()))){
            response.setRespCode("0000");
            response.setRespMsg("无效的费率配置!");
            return response;
        }


        for (String pagyPayTxnSsn : pagyPayTxnSsnList) {
            WxBillOuter outer = pagyServiceChargeDao.queryWxBillOuter(pagyPayTxnSsn);
            if (IfspDataVerifyUtil.isBlank(outer)){
                BthWxFileDet oldOuter = pagyServiceChargeDao.queryWxByOrderNo(pagyPayTxnSsn);
                if (IfspDataVerifyUtil.isBlank(oldOuter)){
                    log.warn("找不到微信三方数据,跳过流水[{}]",pagyPayTxnSsn);
                    continue;
                }
                log.info("流水[{}]存在于老微信文件表中...",pagyPayTxnSsn);
                outer = new WxBillOuter();
                outer.setTxnSsn(pagyPayTxnSsn);
                outer.setTxnAmt(oldOuter.getOrderTp().equals("10")?oldOuter.getOrderAmt():oldOuter.getRefundAmt());
                outer.setFeeAmt(oldOuter.getFeeAmt());
                outer.setTxnTime(IfspDateTime.strToDate(oldOuter.getTxnTm(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
            }

            log.info("微信对账文件收取手续费金额为"+outer.getFeeAmt()+"分");

            // 交易总金额(分)
            BigDecimal txnAmt = outer.getTxnAmt();
            //计算所得微信手续费
            BigDecimal tpamFeeAmtByCal ;

            //每笔的固定金额  01
            if ("01".equals(pagyFeeCfgDet.getFeeTp())) {
                tpamFeeAmtByCal = pagyFeeCfgDet.getFeeAmt();
            //按照比率来计算  02
            } else {
                tpamFeeAmtByCal = txnAmt.multiply(pagyFeeCfgDet.getFeeRate()).movePointLeft(2).setScale(0, BigDecimal.ROUND_HALF_UP);
                //最小手续费
                if (IfspDataVerifyUtil.isNotBlank(pagyFeeCfgDet.getFeeMinAmt())) {
                    fee_min = pagyFeeCfgDet.getFeeMinAmt();
                }
                //最大手续费
                if (IfspDataVerifyUtil.isNotBlank(pagyFeeCfgDet.getFeeMaxAmt())) {
                    fee_max = pagyFeeCfgDet.getFeeMaxAmt();
                }
                // 计算所得手续费小于配置的最小手续费或者大于配置的最大手续费就取配置的
                if (tpamFeeAmtByCal.abs().compareTo(fee_min) < 0) {
                    tpamFeeAmtByCal = pagyFeeCfgDet.getFeeMinAmt();
                } else if (tpamFeeAmtByCal.abs().compareTo(fee_max) > 0) {
                    tpamFeeAmtByCal = pagyFeeCfgDet.getFeeMaxAmt();
                }
            }

            log.info("计算的手续费金额为"+tpamFeeAmtByCal+"分");
            // 计算所得手续费金额与对账文件中金额不一致 , 插入差错
            if (!tpamFeeAmtByCal.equals(outer.getFeeAmt())) {
                BthPagyChkErrInfo bthPagyChkErrInfo = new BthPagyChkErrInfo();
                bthPagyChkErrInfo.setChkErrSsn(ConstantUtil.getRandomNum(32));// 内部操作流水号
                bthPagyChkErrInfo.setPagyPayTxnSsn(pagyPayTxnSsn);
                bthPagyChkErrInfo.setPagyTxnTm(outer.getTxnTime());
                bthPagyChkErrInfo.setPagySysNo(Constans.WX_SYS_NO);
                bthPagyChkErrInfo.setPagySysSoaVersion("1.0.0");
                bthPagyChkErrInfo.setChkErrDt(sdf.parse(request.getSettleDate()));// 对账失败日期
                bthPagyChkErrInfo.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));// 创建时间
                bthPagyChkErrInfo.setErrDesc("通道手续费金额不一致,计算的手续费【"+tpamFeeAmtByCal+"分】与文件【"+outer.getFeeAmt()+"分】不符");// 差错描述
                bthPagyChkErrInfo.setErrTp(Constans.ERR_TP_03);// 差错类型
                bthPagyChkErrInfo.setProcDesc("未处理");// 处理描述
                bthPagyChkErrInfo.setProcSt(Constans.PROC_ST_01);// 处理状态
                bthPagyChkErrInfo.setTpamTxnAmt(txnAmt.longValue());// 第三方通道交易金额
                pagyServiceChargeDao.saveBthPagyChkErrInfo(bthPagyChkErrInfo);
            }
        }

        return response;
	}


	@Override
	public CommonResponse aLICalculateServiceCharge(PagyServiceChargeRequest request) throws Exception {


        //交易日期
        Date chkErrDt = DateUtil.getDate(request.getSettleDate());
        //获取对账日期
        Date recoDate = DateUtil.getAfterDate(request.getSettleDate(), 1);
        //支付宝通道号
        String pagyNo = "606000000000001";


		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        CommonResponse response = new CommonResponse();
		BigDecimal fee_min = new BigDecimal(0);
        BigDecimal fee_max = new BigDecimal(Double.MAX_VALUE);

		// 先清理要重跑的对账差错信息
		log.info("---------------------清理数据【差错】--------------------");
        pagyServiceChargeDao.deleteByDateErrTpAndPagySysNo(chkErrDt, Constans.ERR_TP_03,Constans.ALI_SYS_NO+" ");

        //根据清算日期和通道系统编号(CHAR(4)  第四位为空格) 查询对账结果表通道流水号
        log.info("-------------------根据清算日期和通道系统编号查询待计算支付宝手续费条数--------------------");
        List<String> pagyPayTxnSsnList = pagyServiceChargeDao.queryByPagySysNoAndSuccDate(Constans.ALI_SYS_NO+" ",recoDate);
        if(pagyPayTxnSsnList==null ||pagyPayTxnSsnList.size()==0){
            response.setRespCode("0000");
            response.setRespMsg("通道对账成功结果表数据为空!");
            return response;
        }
        log.info("-------------------查询到"+pagyPayTxnSsnList.size()+"条数据--------------------");

        log.info("-------------------查询通道费率配置信息--------------------");
        PagyFeeCfgDet pagyFeeCfgDet = pagyServiceChargeDao.queryPagyFeeCfgByPagyNo(pagyNo);
        if (IfspDataVerifyUtil.isBlank(pagyFeeCfgDet) ||
                !("01".equals(pagyFeeCfgDet.getFeeTp())||"02".equals(pagyFeeCfgDet.getFeeTp()))){
            response.setRespCode("0000");
            response.setRespMsg("无效的费率配置!");
            return response;
        }

        for (String pagyPayTxnSsn : pagyPayTxnSsnList) {
            AliBillOuter outer = pagyServiceChargeDao.queryAliBillOuter(pagyPayTxnSsn);

            if (IfspDataVerifyUtil.isBlank(outer)){
                BthAliFileDet oldOuter = pagyServiceChargeDao.queryAliFileDetByOrderNo(pagyPayTxnSsn);
                if (IfspDataVerifyUtil.isBlank(oldOuter)){
                    log.warn("找不到支付宝三方数据,跳过流水[{}]",pagyPayTxnSsn);
                    continue;
                }
                log.info("流水[{}]存在于老支付宝文件表中...",pagyPayTxnSsn);
                outer = new AliBillOuter();
                outer.setTxnSsn(pagyPayTxnSsn);
                outer.setTxnAmt(oldOuter.getOrderAmt().abs());
                outer.setTxnFeeAmt(oldOuter.getFeeAmt().abs());
                outer.setTxnTime(IfspDateTime.strToDate(oldOuter.getCrtTm(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
            }

            log.info("支付宝对账文件收取手续费金额为"+outer.getTxnFeeAmt()+"分");

            // 交易总金额(分)
            BigDecimal txnAmt = outer.getTxnAmt();
            //计算所得微信手续费
            BigDecimal tpamFeeAmtByCal ;

            //每笔的固定金额  01
            if ("01".equals(pagyFeeCfgDet.getFeeTp())) {
                tpamFeeAmtByCal = pagyFeeCfgDet.getFeeAmt();
                //按照比率来计算  02
            } else {
                tpamFeeAmtByCal = txnAmt.multiply(pagyFeeCfgDet.getFeeRate()).movePointLeft(2).setScale(0, BigDecimal.ROUND_HALF_UP);
                //最小手续费
                if (IfspDataVerifyUtil.isNotBlank(pagyFeeCfgDet.getFeeMinAmt())) {
                    fee_min = pagyFeeCfgDet.getFeeMinAmt();
                }
                //最大手续费
                if (IfspDataVerifyUtil.isNotBlank(pagyFeeCfgDet.getFeeMaxAmt())) {
                    fee_max = pagyFeeCfgDet.getFeeMaxAmt();
                }
                // 计算所得手续费小于配置的最小手续费或者大于配置的最大手续费就取配置的
                if (tpamFeeAmtByCal.abs().compareTo(fee_min) < 0) {
                    tpamFeeAmtByCal = pagyFeeCfgDet.getFeeMinAmt();
                } else if (tpamFeeAmtByCal.abs().compareTo(fee_max) > 0) {
                    tpamFeeAmtByCal = pagyFeeCfgDet.getFeeMaxAmt();
                }
            }

            log.info("计算的手续费金额为"+tpamFeeAmtByCal+"分");
            // 计算所得手续费金额与对账文件中金额不一致 , 插入差错
            if (!tpamFeeAmtByCal.equals(outer.getTxnFeeAmt())) {
                BthPagyChkErrInfo bthPagyChkErrInfo = new BthPagyChkErrInfo();
                bthPagyChkErrInfo.setChkErrSsn(ConstantUtil.getRandomNum(32));// 内部操作流水号
                bthPagyChkErrInfo.setPagyPayTxnSsn(pagyPayTxnSsn);
                bthPagyChkErrInfo.setPagyTxnTm(outer.getTxnTime());
                bthPagyChkErrInfo.setPagySysNo(Constans.ALI_SYS_NO);
                bthPagyChkErrInfo.setPagySysSoaVersion("1.0.0");
                bthPagyChkErrInfo.setChkErrDt(sdf.parse(request.getSettleDate()));// 对账失败日期
                bthPagyChkErrInfo.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));// 创建时间
                bthPagyChkErrInfo.setErrDesc("通道手续费金额不一致,计算的手续费【"+tpamFeeAmtByCal+"分】与文件【"+outer.getTxnFeeAmt()+"分】不符");// 差错描述
                bthPagyChkErrInfo.setErrTp(Constans.ERR_TP_03);// 差错类型
                bthPagyChkErrInfo.setProcDesc("未处理");// 处理描述
                bthPagyChkErrInfo.setProcSt(Constans.PROC_ST_01);// 处理状态
                bthPagyChkErrInfo.setTpamTxnAmt(txnAmt.longValue());// 第三方通道交易金额
                pagyServiceChargeDao.saveBthPagyChkErrInfo(bthPagyChkErrInfo);
            }
        }

        return response;
	
	}

    //pamTxnSsnResponse queryPaygTxnSsn(PagyTxnSsnRequest reqData);
    @Override
    public TpamTxnSsnResponse queryPaygTxnSsn(PagyTxnSsnRequest message) {
        TpamTxnSsnResponse tpamTxnSsnResponse = new TpamTxnSsnResponse();
        String pagyTxnSsn = message.getPagyTxnSsn();
        String acctypeId = message.getAcctTypeId();
        Map<String, String> result = new HashMap<>();
        //payPagyCoreTxnInfoDao.getPagyTxnInfo(pagyTxnSsn);
        if(IfspDataVerifyUtil.equals(acctypeId, AcctTypeId.TYPE_1.getCode())&&IfspDataVerifyUtil.equals(message.getUnionType(), AcctTypeId.UNION_2.getCode())){
            result = pagyServiceChargeDao.getUpacpOrderId(pagyTxnSsn);
        }else if(IfspDataVerifyUtil.equals(acctypeId, AcctTypeId.TYPE_1.getCode())&&IfspDataVerifyUtil.equals(message.getUnionType(), AcctTypeId.UNION_1.getCode())){
            result = pagyServiceChargeDao.getCupQrcOrderId(pagyTxnSsn);
        }else if(IfspDataVerifyUtil.equals(acctypeId, AcctTypeId.TYPE_2.getCode())){
            result = pagyServiceChargeDao.getAlipayOrderId(pagyTxnSsn);
        }else if(IfspDataVerifyUtil.equals(acctypeId, AcctTypeId.TYPE_3.getCode())){
            result = pagyServiceChargeDao.getWechatOrderId(pagyTxnSsn);
        }
        log.info("result--tpam_txn_ssn"+result.get("TPAMTXNSSN"));
        if (IfspDataVerifyUtil.isNotBlank(result.get("TPAMTXNSSN"))){
            tpamTxnSsnResponse.setTpamTxnSsn(result.get("TPAMTXNSSN"));
            tpamTxnSsnResponse.setRespCode("0000");
            tpamTxnSsnResponse.setRespMsg("成功返回tpamTxnSsn!");
        }else {
            log.info("result--tpam_txn_ssn为空");
            tpamTxnSsnResponse.setTpamTxnSsn("");
            tpamTxnSsnResponse.setRespCode("0000");
            tpamTxnSsnResponse.setRespMsg("返回为空!");
        }
        return tpamTxnSsnResponse;
    }
//	@Override
//	public CommonResponse yLCalculateServiceCharge(PagyServiceChargeRequest request) throws Exception {
////		log.info("计算微信手续费-入参通道系统编号:"+request.getPagySysNo()+"---清算日期:"+request.getSettleDate());
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        CommonResponse response = new CommonResponse();
//		BigDecimal fee = new BigDecimal(0);
//		BigDecimal fee_min = new BigDecimal(0);
//        BigDecimal fee_max = new BigDecimal(Double.MAX_VALUE);
//		// 先清理要重跑的对账差错信息
//		log.info("---------------------清理数据【差错】--------------------");
//		pagyServiceChargeDao.deletePagyChkErrInfoBySettleDateAndErrTp(request.getSettleDate(), Constans.ERR_TP_03);
//
//		// 1.根据清算日期和通道系统编号查询对账结果表数据（对平流水）BTH_CHK_RSLT_INFO
//		List<BthChkRsltInfo> chkRsltInfoList = pagyServiceChargeDao
//				.selectBthChkRsltInfoByPagySysNoAndSettleDate(request.getPagySysNo(), request.getSettleDate());
//
//		if(chkRsltInfoList==null ||chkRsltInfoList.size()==0){
//            response.setRespCode("0000");
//            response.setRespMsg("通道对账成功结果表数据为空!");
//            return response;
//		}
//
//		// 2.根据对账结果表数据（通道支付内部流水号）取得与之对应的BTH_PAGY_LOCAL_INFO和BTH_WX_FILE_DET信息。
//
//		log.info("-------------------查询到"+chkRsltInfoList.size()+"条数据--------------------");
//		for (int i = 0; i < chkRsltInfoList.size(); i++) {
//			BthChkRsltInfo bthChkRsltInfo = chkRsltInfoList.get(i);
//			String pagyPayTxnSsn = bthChkRsltInfo.getPagyPayTxnSsn();// 通道支付内部流水号
//			BthPagyLocalInfo pagyLocalInfo = pagyServiceChargeDao.queryLocalInfoByPagyPayTxnSsn(pagyPayTxnSsn);// 通道支付内部流水号
//			if(pagyLocalInfo!=null){
//				String tpamTxnSsn = pagyLocalInfo.getTpamTxnSsn();
//				BthUnionFileDet bthUnionFileDet = pagyServiceChargeDao.queryUnionFileDetByOrderNo(tpamTxnSsn.substring(0, 8),tpamTxnSsn.substring(8, 16),tpamTxnSsn.substring(16, 22),tpamTxnSsn.substring(22, 32));// 通道支付内部流水号
//				if(bthUnionFileDet==null){
//                    response.setRespCode("9999");
//                    response.setRespMsg("通道对账明细表数据为空!");
//                    return response;
//				}
//
//				String pagyNo = pagyLocalInfo.getPagyNo();// 通道编号
//				String pagySysNo = pagyLocalInfo.getPagySysNo();// 通道系统编号
//				String cardType = pagyLocalInfo.getCardType();// 支持卡种
//				// 3.根据通道编号查询费率，根据BTH_PAGY_LOCAL_INFO的交易金额计算通道手续费。
//				// 确定当前一笔流水的费率计算方式
//				if(IfspDataVerifyUtil.isBlank(cardType)){
//					cardType="01";
//				}
//				log.info("-------------------查询通道费率配置信息--------------------");
////				PagyFeeCfgDet pagyFeeCfgDet = pagyServiceChargeDao
////						.queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardType(pagyNo, pagySysNo, cardType);
//
//				PagyFeeCfgDet pagyFeeCfgDet = pagyServiceChargeDao
//						.queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardTypeAndOrderSsn(pagyNo, pagySysNo, cardType,bthChkRsltInfo.getOrderSsn());
//				if (pagyFeeCfgDet != null) {
//
//					Long txnAmt = pagyLocalInfo.getTxnAmt();// 交易总金额(分)
//					String feeTp = pagyFeeCfgDet.getFeeTp();// 00=不收费/01=固定/02=比率
//					if ("01".equals(feeTp)) {
//						BigDecimal feeAmt = pagyFeeCfgDet.getFeeAmt();// 每笔的固定金额
//						pagyLocalInfo.setTxnFeeAmt(feeAmt.longValue());
//					} else if ("02".equals(feeTp)) {
//						BigDecimal feeRate = pagyFeeCfgDet.getFeeRate();// 获得比率
//						BigDecimal txnAmtBig = new BigDecimal(txnAmt);
//						BigDecimal txnFeeAmtBig = txnAmtBig.multiply(feeRate).movePointLeft(2);
//						txnFeeAmtBig = txnFeeAmtBig.setScale(0, BigDecimal.ROUND_HALF_UP);
//						pagyLocalInfo.setTxnFeeAmt(txnFeeAmtBig.longValue());
//						if (IfspDataVerifyUtil.isNotBlank(pagyFeeCfgDet.getFeeMinAmt())) {
//							fee_min = pagyFeeCfgDet.getFeeMinAmt();
//						}
//						if (IfspDataVerifyUtil.isNotBlank(pagyFeeCfgDet.getFeeMaxAmt())) {
//							fee_max = pagyFeeCfgDet.getFeeMaxAmt();
//						}
//						// 手续费小于等于最小金额
//						if (txnFeeAmtBig.abs().compareTo(fee_min) <= 0) {
//							pagyLocalInfo.setTxnFeeAmt(pagyFeeCfgDet.getFeeMinAmt().longValue());
//						} else if (txnFeeAmtBig.abs().compareTo(fee_max) >= 0) {
//							// 手续费大于等于最大金额
//							pagyLocalInfo.setTxnFeeAmt(pagyFeeCfgDet.getFeeMaxAmt().longValue());
//						}
//					}
//					fee =getUnionFee(bthUnionFileDet.getHearGetFee(),bthUnionFileDet.getHearPayFee(),bthUnionFileDet.getRouteSevFee(),bthUnionFileDet.getTransCode());
//					log.info("银联对账文件收取手续费金额为"+fee+"分");
//					log.info("计算的手续费金额为"+pagyLocalInfo.getTxnFeeAmt().toString()+"分");
//					// 4.根据BTH_WX_FILE_DET的FEE_AMT（手续费金额）与计算得出的手续费比较
//					if (pagyLocalInfo.getTxnFeeAmt().longValue()!=fee.longValue()) {
//						// 5.不一致：插入差错表（1.三方手续费多？2.本地手续费多，忽略 不移除对账结果），一致则跳过。
//						BthPagyChkErrInfo bthPagyChkErrInfo = new BthPagyChkErrInfo();
//						BeanUtils.copyProperties(pagyLocalInfo, bthPagyChkErrInfo);
//
//						bthPagyChkErrInfo.setChkErrSsn(ConstantUtil.getRandomNum(32));// 内部操作流水号
//						bthPagyChkErrInfo.setChkErrDt(sdf.parse(request.getSettleDate()));// 对账失败日期
//						try {
//							bthPagyChkErrInfo.setChkDataDt(sdf.parse(request.getSettleDate()));// 对账数据日期
//						} catch (ParseException e) {
//							e.printStackTrace();
//						}
//						bthPagyChkErrInfo.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));// 创建时间
//						bthPagyChkErrInfo.setErrDesc("通道手续费金额不一致,计算的手续费【"+pagyLocalInfo.getTxnFeeAmt().toString()+"分】与文件【"+fee+"分】不符");// 差错描述
//						bthPagyChkErrInfo.setErrTp(Constans.ERR_TP_03);// 差错类型
//						bthPagyChkErrInfo.setProcDesc("未处理");// 处理描述
//						bthPagyChkErrInfo.setProcSt(Constans.PROC_ST_01);// 处理状态
//						// bthPagyChkErrInfo.setTpamSetAmt();//第三方通道交易清算金额
//						bthPagyChkErrInfo.setTpamTxnAmt(pagyLocalInfo.getTpamTxnAmt());// 第三方通道交易金额
//						bthPagyChkErrInfo.setTpamTxnFeeAmt(pagyLocalInfo.getTxnFeeAmt());// 第三方通道交易手续费
//						bthPagyChkErrInfo.setTpamTxnSsn(tpamTxnSsn);// 第三方通道流水号
//						pagyServiceChargeDao.saveBthPagyChkErrInfo(bthPagyChkErrInfo);
//
//					}
//				}else{
//                    response.setRespCode("9999");
//                    response.setRespMsg("查询通道费率配置信息为空!");
//                    return response;
//                }
//			}else{
//                response.setRespCode("9999");
//                response.setRespMsg("查询通道流水本地表为空!");
//                return response;
//			}
//
//		}
//        response.setRespCode("0000");
//        response.setRespMsg("执行成功!");
//        return response;
//    }
//
//	/**
//     * 计算银联手续费
//     * @param hearGetFee
//     * @param hearPayFee
//     * @param routeSevFee
//	 * @param transCode
//     * @return
//     */
//    private BigDecimal getUnionFee(BigDecimal hearGetFee, BigDecimal hearPayFee, BigDecimal routeSevFee, String transCode) {
//    	BigDecimal feeAmt =new BigDecimal(0);
//        if("10".equals(transCode)){
//        	feeAmt= hearGetFee.add(hearPayFee).subtract(routeSevFee);
//        }else if("12".equals(transCode)){
//        	feeAmt= hearGetFee.add(hearPayFee).add(routeSevFee);
//        }
//        return feeAmt;
//	}
}
