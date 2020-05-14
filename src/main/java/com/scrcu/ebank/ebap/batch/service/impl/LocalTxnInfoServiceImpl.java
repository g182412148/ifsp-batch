package com.scrcu.ebank.ebap.batch.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyLocalInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtAlipayTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamCnuniopayQrcTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamIbankTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamUpacpTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.request.LocalTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.response.LocalTxnInfoResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.dao.BthPagyLocalInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PagyTxnInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.batch.dao.TpamAtAlipayTxnInfoDao;
import com.scrcu.ebank.ebap.batch.dao.TpamAtWechatTxnInfoDao;
import com.scrcu.ebank.ebap.batch.dao.TpamCnuniopayQrcTxnInfoDao;
import com.scrcu.ebank.ebap.batch.dao.TpamIbankTxnInfoDao;
import com.scrcu.ebank.ebap.batch.dao.TpamWechatDcTxnInfoDao;
import com.scrcu.ebank.ebap.batch.service.LocalTxnInfoService;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LocalTxnInfoServiceImpl implements LocalTxnInfoService {
	@Resource
    private BthPagyLocalInfoDao bthPagyLocalInfoDao;
	
	@Resource
	private TpamAtWechatTxnInfoDao tpamAtWechatTxnInfoDao;
	
	@Resource
	private TpamWechatDcTxnInfoDao tpamWechatDcTxnInfoDao;          //微信H5支付数据抽取
	
	@Resource
	private TpamAtAlipayTxnInfoDao tpamAtAlipayTxnInfoDao;
	
	@Resource
	private TpamCnuniopayQrcTxnInfoDao tpamCnuniopayQrcTxnInfoDao;
	
	@Resource
	private TpamIbankTxnInfoDao tpamIbankTxnInfoDao;
	
	@Resource
	private PagyTxnInfoDao pagyTxnInfoDao;
	
	@Resource
	private PayOrderInfoDao payOrderInfoDao;
	
	/**
     * 微信通道流水抽取
     * @param request
     * @return
     */
	@Override
	public LocalTxnInfoResponse getWxAtTxnInfo(LocalTxnInfoRequest request) throws Exception {
		/** 初始化返回报文对象 */
		LocalTxnInfoResponse localWxResponse = new LocalTxnInfoResponse();
		BthPagyLocalInfo bthPagyLocalInfo = new BthPagyLocalInfo();
		ArrayList<BthPagyLocalInfo> list = new ArrayList<BthPagyLocalInfo>();
		/** 获取请求对象值 */
		String pagySysNo = request.getPagySysNo();
		String settleDate = request.getSettleDate();
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
//		1.	根据通道系统编号和清算日期（TXN_REQ_TM）删除BTH_PAGY_LOCAL_INFO本地通道交易明细对账表数据
		log.info("---------------------删除本地流水表数据------------------");
		bthPagyLocalInfoDao.deleteBypagySysNoAndPagyDate(pagySysNo,settleDate);
		
//		2.	根据清算日期（TPAM_TXN_TM）查询交易成功及处理中的微信流水表(TPAM_AT_WECHAT_TXN_INFO)信息
		log.info("---------------------获取流水成功交易表数据------------------");
		//定义每页条数
		//查询总条数
//		for(i=1 ; i<total;i++){
//			//分页查询
//			
//		}
		List<TpamAtWechatTxnInfoVo> tpamAtWechatTxnInfoList = tpamAtWechatTxnInfoDao.selectByDateAndState(settleDate);
		
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("settleDate", settleDate);
		List<TpamAtWechatTxnInfoVo> tpamWechatDcTxnInfoList = tpamWechatDcTxnInfoDao.selectList("selectWechatDcByDateAndState", param);
		tpamAtWechatTxnInfoList.addAll(tpamWechatDcTxnInfoList);
		
		if(IfspDataVerifyUtil.isEmptyList(tpamAtWechatTxnInfoList)){
            log.info("没有对应日期数据");
            localWxResponse.setLocalTxnInfoList(null);
            localWxResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
    		localWxResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
            return localWxResponse;
        }
//		3.	将微信流水表信息加载到本地通道交易明细对账表中并插入数据库（未对账）
		log.info("---------------------插入数据------------------");
		for (TpamAtWechatTxnInfoVo tpamAtWechatTxnInfo : tpamAtWechatTxnInfoList) {
			bthPagyLocalInfo.setPagyPayTxnSsn(tpamAtWechatTxnInfo.getPagyPayTxnSsn());//通道支付内部流水号
			bthPagyLocalInfo.setPagyPayTxnTm(tpamAtWechatTxnInfo.getPagyPayTxnTm());// 通道支付内部流水时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setPagyTxnSsn(tpamAtWechatTxnInfo.getPagyTxnSsn());// 通道支付内部订单号
			bthPagyLocalInfo.setPagyTxnTm(tpamAtWechatTxnInfo.getPagyTxnTm());// 通道支付内部订单时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setOrigPagyTxnSsn(tpamAtWechatTxnInfo.getOrigPagyPayTxnSsn());// 原通道支付内部订单号
			bthPagyLocalInfo.setOrigPagyTxnTm(tpamAtWechatTxnInfo.getOrigPagyPayTxnTm());// 原通道支付内部订单时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setPagySysNo(tpamAtWechatTxnInfo.getPagySysNo());// 通道系统编号
			bthPagyLocalInfo.setPagySysSoaNo(tpamAtWechatTxnInfo.getPagySysSoaNo());// 通道系统服务编号(交易类型)
			bthPagyLocalInfo.setPagySysSoaVersion(tpamAtWechatTxnInfo.getPagySysSoaVersion());// 通道服务版本号
			bthPagyLocalInfo.setPagyNo(tpamAtWechatTxnInfo.getPagyNo());// 通道编号
			bthPagyLocalInfo.setPagyMchtNo(tpamAtWechatTxnInfo.getPagyMchtNo());// 通道商户编号
			bthPagyLocalInfo.setTpamTxnSsn(tpamAtWechatTxnInfo.getTpamTxnSsn());// 第三方通道流水号
			bthPagyLocalInfo.setTpamTxnTm(tpamAtWechatTxnInfo.getTpamTxnTm());// 第三方交易时间
			bthPagyLocalInfo.setTpamOrigTxnSsn(tpamAtWechatTxnInfo.getTpamOrigTxnSsn());// 退款类交易
			bthPagyLocalInfo.setTpamTxnTypeNo(tpamAtWechatTxnInfo.getTpamTxnTypeNo());// 第三方通道交易类型
			bthPagyLocalInfo.setTpamTxnAmt(tpamAtWechatTxnInfo.getTpamTxnAmt()==null?0:tpamAtWechatTxnInfo.getTpamTxnAmt());// 订单金额
			bthPagyLocalInfo.setTxnChlAction(tpamAtWechatTxnInfo.getPagySysSoaActionFlag());// 渠道交易行为 10=支付类11=撤销/冲正类;12=退款类型
			bthPagyLocalInfo.setAcctType(tpamAtWechatTxnInfo.getTpamAcctTypeNo());// 支付账户类型
			bthPagyLocalInfo.setAcctSubType(tpamAtWechatTxnInfo.getTpamAcctSubTypeNo());// 支付账户子类型
			bthPagyLocalInfo.setCardType(tpamAtWechatTxnInfo.getTpamCardTypeNo());// 支付卡种：00=全卡种;01=借记卡;02=贷记卡;	
			bthPagyLocalInfo.setAcctNo(tpamAtWechatTxnInfo.getTpamAcctNo());// 支付账户
			bthPagyLocalInfo.setTxnAmt(tpamAtWechatTxnInfo.getTpamTxnAmt()==null?0:tpamAtWechatTxnInfo.getTpamTxnAmt());// 渠道交易金额
			String tpamSettleFee = tpamAtWechatTxnInfo.getTpamSettleFee()==null?"0":tpamAtWechatTxnInfo.getTpamSettleFee();
			bthPagyLocalInfo.setTxnFeeAmt(Long.parseLong(tpamSettleFee));// 支付手续费金额(分）
			bthPagyLocalInfo.setChkSt("00");//对账状态00=未对账01=已对账
			bthPagyLocalInfo.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));// 创建时间
			bthPagyLocalInfo.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));// 最后更新时间
			bthPagyLocalInfo.setTxnReqSsn(tpamAtWechatTxnInfo.getTxnReqSsn());// 渠道请求流水号
			bthPagyLocalInfo.setTxnReqTm(tpamAtWechatTxnInfo.getTxnReqTm());// 渠道请求流水时间
			bthPagyLocalInfo.setOrigTxnReqSsn(tpamAtWechatTxnInfo.getOrigTxnReqSsn());// 渠道原请求流水号
			bthPagyLocalInfo.setOrigTxnReqTm(tpamAtWechatTxnInfo.getOrigTxnReqTm());// 渠道原请求流水时间
			bthPagyLocalInfo.setPagyProdId(tpamAtWechatTxnInfo.getProdId());// 通道产品编号
			bthPagyLocalInfo.setPagyProdTxnId(tpamAtWechatTxnInfo.getTxnType());// 通道产品交易编号/通道路由编号

            // 根据返回码来设置状态 , 0000 表示交易成功  , 其他都是处理中
			if ("0000".equals(tpamAtWechatTxnInfo.getPagyRespCode())){

				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_00);
			}else {
				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_02);
			}

			int k = -1;
			try {
				k = bthPagyLocalInfoDao.insertSelective(bthPagyLocalInfo);
			} catch (Exception e) {
				log.error("插入本地通道交易明细对账表失败", e);
				throw new IfspBizException(RespConstans.RESP_INSERT_ERR.getCode(), "插入本地通道交易明细对账表失败");
			}
			if (k != -1) {
				log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn()+ ",插入本地成功！");
			} else {
				log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn() + ",插入本地失败！");
			}
			list.add(bthPagyLocalInfo);
		}
		localWxResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		localWxResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return localWxResponse;
	}
	/**
     * zfb通道流水抽取
     * @param request
     * @return
     */
	@Override
	public LocalTxnInfoResponse getAliAtTxnInfo(LocalTxnInfoRequest request) throws Exception {
		/** 初始化返回报文对象 */
		LocalTxnInfoResponse localAliResponse = new LocalTxnInfoResponse();
		BthPagyLocalInfo bthPagyLocalInfo = new BthPagyLocalInfo();
		ArrayList<BthPagyLocalInfo> list = new ArrayList<BthPagyLocalInfo>();
		/** 获取请求对象值 */
		String pagySysNo = request.getPagySysNo();
		String settleDate = request.getSettleDate();
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
//		1.  根据通道编号和清算日期（TXN_REQ_TM）删除BTH_PAGY_LOCAL_INFO本地通道交易明细对账表数据
		log.info("---------------------删除本地流水表数据------------------");
		bthPagyLocalInfoDao.deleteBypagySysNoAndPagyDate(pagySysNo,settleDate);
		
//		2.	根据清算日期（TPAM_TXN_TM）查询交易成功的支付宝流水表(TPAM_AT_ALIPAY_TXN_INFO)信息
		log.info("---------------------获取流水成功交易表数据------------------");
		List<TpamAtAlipayTxnInfoVo> tpamAtAlipayTxnInfoList = tpamAtAlipayTxnInfoDao.selectByDateAndState(settleDate);
		if(IfspDataVerifyUtil.isEmptyList(tpamAtAlipayTxnInfoList)){
            log.info("没有对应日期数据");
            localAliResponse.setLocalTxnInfoList(null);
            localAliResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            localAliResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
            return localAliResponse;
        }
//		3.	将支付宝流水表信息加载到本地通道交易明细对账表中并插入数据库（未对账）
		log.info("---------------------插入数据------------------");
		for (TpamAtAlipayTxnInfoVo tpamAtAlipayTxnInfo : tpamAtAlipayTxnInfoList) {
			bthPagyLocalInfo.setPagyPayTxnSsn(tpamAtAlipayTxnInfo.getPagyPayTxnSsn());//通道支付内部流水号
			bthPagyLocalInfo.setPagyPayTxnTm(tpamAtAlipayTxnInfo.getPagyPayTxnTm());// 通道支付内部流水时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setPagyTxnSsn(tpamAtAlipayTxnInfo.getPagyTxnSsn());// 通道支付内部订单号
			bthPagyLocalInfo.setPagyTxnTm(tpamAtAlipayTxnInfo.getPagyTxnTm());// 通道支付内部订单时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setOrigPagyTxnSsn(tpamAtAlipayTxnInfo.getOrigPagyPayTxnSsn());// 原通道支付内部订单号
			bthPagyLocalInfo.setOrigPagyTxnTm(tpamAtAlipayTxnInfo.getOrigPagyPayTxnTm());// 原通道支付内部订单时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setPagySysNo(tpamAtAlipayTxnInfo.getPagySysNo());// 通道系统编号
			bthPagyLocalInfo.setPagySysSoaNo(tpamAtAlipayTxnInfo.getPagySysSoaNo());// 通道系统服务编号(交易类型)
			bthPagyLocalInfo.setPagySysSoaVersion(tpamAtAlipayTxnInfo.getPagySysSoaVersion());// 通道服务版本号
			bthPagyLocalInfo.setPagyNo(tpamAtAlipayTxnInfo.getPagyNo());// 通道编号
			bthPagyLocalInfo.setPagyMchtNo(tpamAtAlipayTxnInfo.getPagyMchtNo());// 通道商户编号
			bthPagyLocalInfo.setTpamTxnSsn(tpamAtAlipayTxnInfo.getTpamTxnSsn());// 第三方通道流水号
			bthPagyLocalInfo.setTpamTxnTm(tpamAtAlipayTxnInfo.getTpamTxnTm());// 第三方交易时间
			bthPagyLocalInfo.setTpamOrigTxnSsn(tpamAtAlipayTxnInfo.getTpamOrigTxnSsn());// 退款类交易
			bthPagyLocalInfo.setTpamTxnTypeNo(tpamAtAlipayTxnInfo.getTpamTxnTypeNo());// 第三方通道交易类型
			bthPagyLocalInfo.setTpamTxnAmt(tpamAtAlipayTxnInfo.getTpamTxnAmt()==null?0:tpamAtAlipayTxnInfo.getTpamTxnAmt());// 订单金额
			bthPagyLocalInfo.setTxnChlAction(tpamAtAlipayTxnInfo.getPagySysSoaActionFlag());// 渠道交易行为 10=支付类11=撤销/冲正类;12=退款类型
			bthPagyLocalInfo.setAcctType(tpamAtAlipayTxnInfo.getTpamAcctTypeNo());// 支付账户类型
			bthPagyLocalInfo.setAcctSubType(tpamAtAlipayTxnInfo.getTpamAcctSubTypeNo());// 支付账户子类型
			bthPagyLocalInfo.setCardType(tpamAtAlipayTxnInfo.getTpamCardTypeNo());// 支付卡种：00=全卡种;01=借记卡;02=贷记卡;	
			bthPagyLocalInfo.setAcctNo(tpamAtAlipayTxnInfo.getTpamAcctNo());// 支付账户
			bthPagyLocalInfo.setTxnAmt(tpamAtAlipayTxnInfo.getTpamTxnAmt()==null?0:tpamAtAlipayTxnInfo.getTpamTxnAmt());// 渠道交易金额
			String tpamSettleFee = tpamAtAlipayTxnInfo.getTpamSettleFee()==null?"0":tpamAtAlipayTxnInfo.getTpamSettleFee();
			bthPagyLocalInfo.setTxnFeeAmt(Long.parseLong(tpamSettleFee));// 支付手续费金额(分）
			bthPagyLocalInfo.setChkSt("00");//对账状态00=未对账01=已对账
			bthPagyLocalInfo.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));// 创建时间
			bthPagyLocalInfo.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));// 最后更新时间
			bthPagyLocalInfo.setTxnReqSsn(tpamAtAlipayTxnInfo.getTxnReqSsn());// 渠道请求流水号
			bthPagyLocalInfo.setTxnReqTm(tpamAtAlipayTxnInfo.getTxnReqTm());// 渠道请求流水时间
			bthPagyLocalInfo.setOrigTxnReqSsn(tpamAtAlipayTxnInfo.getOrigTxnReqSsn());// 渠道原请求流水号
			bthPagyLocalInfo.setOrigTxnReqTm(tpamAtAlipayTxnInfo.getOrigTxnReqTm());// 渠道原请求流水时间
			bthPagyLocalInfo.setPagyProdId(tpamAtAlipayTxnInfo.getProdId());// 通道产品编号
			bthPagyLocalInfo.setPagyProdTxnId(tpamAtAlipayTxnInfo.getTxnType());// 通道产品交易编号/通道路由编号

			// 根据返回码来设置状态 , 0000 表示交易成功  , 其他都是处理中
			if ("0000".equals(tpamAtAlipayTxnInfo.getPagyRespCode())){

				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_00);
			}else {
				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_02);
			}

			int k = -1;
			try {
				k = bthPagyLocalInfoDao.insertSelective(bthPagyLocalInfo);
			} catch (Exception e) {
				log.error("插入本地通道交易明细对账表失败", e);
				throw new IfspBizException(RespConstans.RESP_INSERT_ERR.getCode(), "插入本地通道交易明细对账表失败");
			}
			if (k != -1) {
				log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn()+ ",插入本地成功！");
			} else {
				log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn() + ",插入本地失败！");
			}
			list.add(bthPagyLocalInfo);
		}
		localAliResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		localAliResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return localAliResponse;
	}
	/**
     * 银联二维码流水抽取
     * @param request
     * @return
	 * @throws Exception 
     */
	@Override
	public LocalTxnInfoResponse getUnionTxnInfo(LocalTxnInfoRequest request) throws Exception {
		/** 初始化返回报文对象 */
		LocalTxnInfoResponse localUnionResponse = new LocalTxnInfoResponse();
		BthPagyLocalInfo bthPagyLocalInfo = new BthPagyLocalInfo();
		ArrayList<BthPagyLocalInfo> list = new ArrayList<BthPagyLocalInfo>();
		/** 获取请求对象值 */
		String pagySysNo = request.getPagySysNo();
		String settleDate = request.getSettleDate();
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
//		1.  根据通道编号和清算日期（TXN_REQ_TM）删除BTH_PAGY_LOCAL_INFO本地通道交易明细对账表数据
		log.info("---------------------删除本地流水表数据------------------");
		bthPagyLocalInfoDao.deleteBypagySysNoAndPagyDate(pagySysNo,settleDate);
		
//		2.	根据清算日期（TPAM_TXN_TM）查询交易成功的银联流水表(TPAM_CNUNIOPAY_QRC_TXN_INFO)信息
		log.info("---------------------获取流水成功交易表数据------------------");
		List<TpamCnuniopayQrcTxnInfoVo> tpamCnuniopayQrcTxnInfoList = tpamCnuniopayQrcTxnInfoDao.selectByDateAndState(settleDate);
		if(IfspDataVerifyUtil.isEmptyList(tpamCnuniopayQrcTxnInfoList)){
            log.info("没有对应日期数据");
            localUnionResponse.setLocalTxnInfoList(null);
            localUnionResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            localUnionResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
            return localUnionResponse;
        }
//		3.	将银联流水表信息加载到本地通道交易明细对账表中并插入数据库（未对账）
		log.info("---------------------插入数据------------------");
		for (TpamCnuniopayQrcTxnInfoVo tpamCnuniopayQrcTxnInfo : tpamCnuniopayQrcTxnInfoList) {
			bthPagyLocalInfo.setPagyPayTxnSsn(tpamCnuniopayQrcTxnInfo.getPagyPayTxnSsn());//通道支付内部流水号
			bthPagyLocalInfo.setPagyPayTxnTm(tpamCnuniopayQrcTxnInfo.getPagyPayTxnTm());// 通道支付内部流水时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setPagyTxnSsn(tpamCnuniopayQrcTxnInfo.getPagyTxnSsn());// 通道支付内部订单号
			bthPagyLocalInfo.setPagyTxnTm(tpamCnuniopayQrcTxnInfo.getPagyTxnTm());// 通道支付内部订单时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setOrigPagyTxnSsn(tpamCnuniopayQrcTxnInfo.getOrigPagyPayTxnSsn());// 原通道支付内部订单号
			bthPagyLocalInfo.setOrigPagyTxnTm(tpamCnuniopayQrcTxnInfo.getOrigPagyPayTxnTm());// 原通道支付内部订单时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setPagySysNo(tpamCnuniopayQrcTxnInfo.getPagySysNo());// 通道系统编号
			bthPagyLocalInfo.setPagySysSoaNo(tpamCnuniopayQrcTxnInfo.getPagySysSoaNo());// 通道系统服务编号(交易类型)
			bthPagyLocalInfo.setPagySysSoaVersion(tpamCnuniopayQrcTxnInfo.getPagySysSoaVersion());// 通道服务版本号
			bthPagyLocalInfo.setPagyNo(tpamCnuniopayQrcTxnInfo.getPagyNo());// 通道编号
			bthPagyLocalInfo.setPagyMchtNo(tpamCnuniopayQrcTxnInfo.getPagyMchtNo());// 通道商户编号
			bthPagyLocalInfo.setTpamTxnSsn(tpamCnuniopayQrcTxnInfo.getTpamSettleKey());// 第三方通道流水号   存放清算主键用于对账
			bthPagyLocalInfo.setTpamTxnTm(tpamCnuniopayQrcTxnInfo.getTpamTxnTm());// 第三方交易时间
			bthPagyLocalInfo.setTpamOrigTxnSsn(tpamCnuniopayQrcTxnInfo.getTpamOrigTxnSsn());// 退款类交易
			bthPagyLocalInfo.setTpamTxnTypeNo(tpamCnuniopayQrcTxnInfo.getTpamTxnTypeNo());// 第三方通道交易类型
			bthPagyLocalInfo.setTpamTxnAmt(tpamCnuniopayQrcTxnInfo.getTpamTxnAmt()==null?0:tpamCnuniopayQrcTxnInfo.getTpamTxnAmt());// 订单金额
			if("30".equals(tpamCnuniopayQrcTxnInfo.getPagySysSoaActionFlag())){//30通知类有清算主键认为支付类
				bthPagyLocalInfo.setTxnChlAction("10");// 渠道交易行为 10=支付类11=撤销/冲正类;12=退款类型
			}else{
				bthPagyLocalInfo.setTxnChlAction(tpamCnuniopayQrcTxnInfo.getPagySysSoaActionFlag());// 渠道交易行为 10=支付类11=撤销/冲正类;12=退款类型
			}
			bthPagyLocalInfo.setAcctType(tpamCnuniopayQrcTxnInfo.getTpamAcctTypeNo());// 支付账户类型
			bthPagyLocalInfo.setAcctSubType(tpamCnuniopayQrcTxnInfo.getTpamAcctSubTypeNo());// 支付账户子类型
			bthPagyLocalInfo.setCardType(tpamCnuniopayQrcTxnInfo.getTpamPayCardType());// 支付卡种：01=借记卡;02=贷记卡;	
			bthPagyLocalInfo.setAcctNo(tpamCnuniopayQrcTxnInfo.getTpamAcctNo());// 支付账户
			bthPagyLocalInfo.setTxnAmt(tpamCnuniopayQrcTxnInfo.getTpamTxnAmt()==null?0:tpamCnuniopayQrcTxnInfo.getTpamTxnAmt());// 渠道交易金额
			String tpamSettleFee = tpamCnuniopayQrcTxnInfo.getTpamSettleFee()==null?"0":tpamCnuniopayQrcTxnInfo.getTpamSettleFee();
			bthPagyLocalInfo.setTxnFeeAmt(Long.parseLong(tpamSettleFee));// 支付手续费金额(分）
			bthPagyLocalInfo.setChkSt("00");//对账状态00=未对账01=已对账
			bthPagyLocalInfo.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));// 创建时间
			bthPagyLocalInfo.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));// 最后更新时间
			bthPagyLocalInfo.setTxnReqSsn(tpamCnuniopayQrcTxnInfo.getTxnReqSsn());// 渠道请求流水号
			bthPagyLocalInfo.setTxnReqTm(tpamCnuniopayQrcTxnInfo.getTxnReqTm());// 渠道请求流水时间
			bthPagyLocalInfo.setOrigTxnReqSsn(tpamCnuniopayQrcTxnInfo.getOrigTxnReqSsn());// 渠道原请求流水号
			bthPagyLocalInfo.setOrigTxnReqTm(tpamCnuniopayQrcTxnInfo.getOrigTxnReqTm());// 渠道原请求流水时间
			bthPagyLocalInfo.setPagyProdId(tpamCnuniopayQrcTxnInfo.getProdId());// 通道产品编号
			bthPagyLocalInfo.setPagyProdTxnId(tpamCnuniopayQrcTxnInfo.getTxnType());// 通道产品交易编号/通道路由编号
			// 根据返回码来设置状态 , 0000 表示交易成功  , 其他都是处理中
			if ("0000".equals(tpamCnuniopayQrcTxnInfo.getPagyRespCode())){

				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_00);
			}else {
				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_02);
			}
			int k = -1;
			try {
				k = bthPagyLocalInfoDao.insertSelective(bthPagyLocalInfo);
			} catch (Exception e) {
				log.error("插入本地通道交易明细对账表失败", e);
				throw new IfspBizException(RespConstans.RESP_INSERT_ERR.getCode(), "插入本地通道交易明细对账表失败");
			}
			if (k != -1) {
				log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn()+ ",插入本地成功！");
			} else {
				log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn() + ",插入本地失败！");
			}
			list.add(bthPagyLocalInfo);
		}
		localUnionResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		localUnionResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return localUnionResponse;
	}
	/**
     * 本行通道流水抽取
     * @param request
     * @return
	 * @throws Exception 
     */
	@Override
	public LocalTxnInfoResponse getIbankTxnInfo(LocalTxnInfoRequest request) throws Exception {
		/** 初始化返回报文对象 */
		LocalTxnInfoResponse localUnionResponse = new LocalTxnInfoResponse();
		BthPagyLocalInfo bthPagyLocalInfo = new BthPagyLocalInfo();
		ArrayList<BthPagyLocalInfo> list = new ArrayList<BthPagyLocalInfo>();
		/** 获取请求对象值 */
		String pagySysNo = request.getPagySysNo();
		String settleDate = request.getSettleDate();
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");
//		1.  根据通道编号和清算日期（TXN_REQ_TM）删除BTH_PAGY_LOCAL_INFO本地通道交易明细对账表数据
		log.info("---------------------删除本地流水表数据------------------");
		bthPagyLocalInfoDao.deleteBypagySysNoAndDate(pagySysNo,settleDate);
		
//		2.	根据清算日期（TPAM_TXN_TM）查询交易成功的本行流水表(TPAM_IBANK_TXN_INFO)信息
		log.info("---------------------获取流水成功交易表数据------------------");
		List<TpamIbankTxnInfoVo> tpamIbankTxnInfoList = tpamIbankTxnInfoDao.selectByDateAndState(settleDate);
		if(IfspDataVerifyUtil.isEmptyList(tpamIbankTxnInfoList)){
            log.info("没有对应日期数据");
            localUnionResponse.setLocalTxnInfoList(null);
            localUnionResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            localUnionResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
            return localUnionResponse;
        }
//		3.	将本行流水表信息加载到本地通道交易明细对账表中并插入数据库（未对账）
		log.info("---------------------插入数据------------------");
		for (TpamIbankTxnInfoVo tpamIbankTxnInfo : tpamIbankTxnInfoList) {
			bthPagyLocalInfo.setPagyPayTxnSsn(tpamIbankTxnInfo.getPagyPayTxnSsn());//通道支付内部流水号
			bthPagyLocalInfo.setPagyPayTxnTm(tpamIbankTxnInfo.getPagyPayTxnTm());// 通道支付内部流水时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setPagyTxnSsn(tpamIbankTxnInfo.getPagyTxnSsn());// 通道支付内部订单号
			bthPagyLocalInfo.setPagyTxnTm(tpamIbankTxnInfo.getPagyTxnTm());// 通道支付内部订单时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setOrigPagyTxnSsn(tpamIbankTxnInfo.getOrigPagyPayTxnSsn());// 原通道支付内部订单号
			bthPagyLocalInfo.setOrigPagyTxnTm(tpamIbankTxnInfo.getOrigPagyPayTxnTm());// 原通道支付内部订单时间(格式：yyyyMMddHHmmss)
			bthPagyLocalInfo.setPagySysNo(tpamIbankTxnInfo.getPagySysNo());// 通道系统编号
			bthPagyLocalInfo.setPagySysSoaNo(tpamIbankTxnInfo.getPagySysSoaNo());// 通道系统服务编号(交易类型)
			bthPagyLocalInfo.setPagySysSoaVersion(tpamIbankTxnInfo.getPagySysSoaVersion());// 通道服务版本号
			bthPagyLocalInfo.setPagyNo(tpamIbankTxnInfo.getPagyNo());// 通道编号
			bthPagyLocalInfo.setPagyMchtNo(tpamIbankTxnInfo.getPagyMchtNo());// 通道商户编号
			bthPagyLocalInfo.setTpamTxnSsn(tpamIbankTxnInfo.getPagyPayTxnSsn());// 第三方通道流水号
			bthPagyLocalInfo.setTpamTxnTm(tpamIbankTxnInfo.getTpamTxnTm());// 第三方交易时间
			bthPagyLocalInfo.setTpamOrigTxnSsn(tpamIbankTxnInfo.getTpamOrigTxnSsn());// 退款类交易
			bthPagyLocalInfo.setTpamTxnTypeNo(tpamIbankTxnInfo.getTpamTxnTypeNo());// 第三方通道交易类型
			bthPagyLocalInfo.setTpamTxnAmt(tpamIbankTxnInfo.getTpamTxnAmt()==null?0:tpamIbankTxnInfo.getTpamTxnAmt());// 订单金额
			bthPagyLocalInfo.setTxnChlAction(tpamIbankTxnInfo.getPagySysSoaActionFlag());// 渠道交易行为 10=支付类11=撤销/冲正类;12=退款类型
			bthPagyLocalInfo.setAcctType(tpamIbankTxnInfo.getTpamAcctOutTypeNo());// 支付账户类型
			bthPagyLocalInfo.setAcctSubType(tpamIbankTxnInfo.getTpamAcctOutSubTypeNo());// 支付账户子类型
			bthPagyLocalInfo.setCardType(tpamIbankTxnInfo.getTpamCardOutTypeNo());// 支付卡种：00=全卡种;01=借记卡;02=贷记卡;	
			bthPagyLocalInfo.setAcctNo(tpamIbankTxnInfo.getTpamAcctOutNo());// 支付账户
			bthPagyLocalInfo.setTxnAmt(tpamIbankTxnInfo.getTpamTxnAmt()==null?0:tpamIbankTxnInfo.getTpamTxnAmt());// 渠道交易金额
			String tpamSettleFee = tpamIbankTxnInfo.getTpamSettleFee()==null?"0":tpamIbankTxnInfo.getTpamSettleFee();
			bthPagyLocalInfo.setTxnFeeAmt(Long.parseLong(tpamSettleFee));// 支付手续费金额(分）
			bthPagyLocalInfo.setChkSt("00");//对账状态00=未对账01=已对账
			bthPagyLocalInfo.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));// 创建时间
			bthPagyLocalInfo.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));// 最后更新时间
			bthPagyLocalInfo.setTxnReqSsn(tpamIbankTxnInfo.getTxnReqSsn());// 渠道请求流水号
			bthPagyLocalInfo.setTxnReqTm(tpamIbankTxnInfo.getTxnReqTm());// 渠道请求流水时间
			bthPagyLocalInfo.setOrigTxnReqSsn(tpamIbankTxnInfo.getOrigTxnReqSsn());// 渠道原请求流水号
			bthPagyLocalInfo.setOrigTxnReqTm(tpamIbankTxnInfo.getOrigTxnReqTm());// 渠道原请求流水时间
			bthPagyLocalInfo.setPagyProdId(tpamIbankTxnInfo.getProdId());// 通道产品编号
			bthPagyLocalInfo.setPagyProdTxnId(tpamIbankTxnInfo.getTxnType());// 通道产品交易编号/通道路由编号
			// 根据返回码来设置状态 , 0000 表示交易成功  , 其他都是处理中
			if ("0000".equals(tpamIbankTxnInfo.getPagyRespCode())){

				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_00);
			}else {
				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_02);
			}
			int k = -1;
			try {
				k = bthPagyLocalInfoDao.insertSelective(bthPagyLocalInfo);
			} catch (Exception e) {
				log.error("插入本地通道交易明细对账表失败", e);
				throw new IfspBizException(RespConstans.RESP_INSERT_ERR.getCode(), "插入本地通道交易明细对账表失败");
			}
			if (k != -1) {
				log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn()+ ",插入本地成功！");
			} else {
				log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn() + ",插入本地失败！");
			}
			list.add(bthPagyLocalInfo);
		}
		localUnionResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		localUnionResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		return localUnionResponse;
	}


    /**
     * 银联全渠道流水抽取
     * @param request
     * @return
     */
    @Override
    public LocalTxnInfoResponse getTotalUnionTxn(LocalTxnInfoRequest request) throws ParseException {
        /** 初始化返回报文对象 */
        LocalTxnInfoResponse localUnionResponse = new LocalTxnInfoResponse();
        BthPagyLocalInfo bthPagyLocalInfo = new BthPagyLocalInfo();
        /** 获取请求对象值 */
        String pagySysNo = request.getPagySysNo();
        String settleDate = request.getSettleDate();
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss");

        log.info("---------------------STEP1 删除本地流水表数据 , 通道为 ["+pagySysNo+"] , 流水交易发生日期为 ["+settleDate+"]------------------");
        bthPagyLocalInfoDao.deleteBypagySysNoAndPagyDate(pagySysNo,settleDate);

        log.info("---------------------STEP2 查询银联全渠道本地交易流水------------------");
        List<TpamUpacpTxnInfoVo> tpamUpacpTxnInfoVoList = tpamCnuniopayQrcTxnInfoDao.scanTpamUpacpTxnInfo(settleDate);
        if(IfspDataVerifyUtil.isEmptyList(tpamUpacpTxnInfoVoList)){
            log.info("交易发生日期 ["+settleDate+"]没有对应数据 , 抽取流水任务结束");
            localUnionResponse.setLocalTxnInfoList(null);
            localUnionResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
            localUnionResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
            return localUnionResponse;
        }
        log.info("---------------------STEP3 将数据插入本地通道交易明细表------------------");
        for (TpamUpacpTxnInfoVo tpamUpacpTxnInfoVo : tpamUpacpTxnInfoVoList) {
            // 通道支付内部流水号
            bthPagyLocalInfo.setPagyPayTxnSsn(tpamUpacpTxnInfoVo.getPagyPayTxnSsn());
            // 通道支付内部流水时间(格式：yyyyMMddHHmmss)
            bthPagyLocalInfo.setPagyPayTxnTm(tpamUpacpTxnInfoVo.getPagyPayTxnTm());
            // 通道支付内部订单号
            bthPagyLocalInfo.setPagyTxnSsn(tpamUpacpTxnInfoVo.getPagyTxnSsn());
            // 通道支付内部订单时间(格式：yyyyMMddHHmmss)
            bthPagyLocalInfo.setPagyTxnTm(tpamUpacpTxnInfoVo.getPagyTxnTm());
            // 原通道支付内部订单号
            bthPagyLocalInfo.setOrigPagyTxnSsn(tpamUpacpTxnInfoVo.getOrigPagyPayTxnSsn());
            // 原通道支付内部订单时间(格式：yyyyMMddHHmmss)
            bthPagyLocalInfo.setOrigPagyTxnTm(tpamUpacpTxnInfoVo.getOrigPagyPayTxnTm());
            // 通道系统编号
            bthPagyLocalInfo.setPagySysNo(tpamUpacpTxnInfoVo.getPagySysNo());
            // 通道系统服务编号(交易类型)
            bthPagyLocalInfo.setPagySysSoaNo(tpamUpacpTxnInfoVo.getPagySysSoaNo());
            // 通道服务版本号
            bthPagyLocalInfo.setPagySysSoaVersion(tpamUpacpTxnInfoVo.getPagySysSoaVersion());
            // 通道编号
            bthPagyLocalInfo.setPagyNo(tpamUpacpTxnInfoVo.getPagyNo());
            // 通道商户编号
            bthPagyLocalInfo.setPagyMchtNo(tpamUpacpTxnInfoVo.getPagyMchtNo());
            // 银联全渠道通道流水号
            bthPagyLocalInfo.setTpamTxnSsn(tpamUpacpTxnInfoVo.getTpamTxnSsn());
            // 第三方交易时间
            bthPagyLocalInfo.setTpamTxnTm(tpamUpacpTxnInfoVo.getTpamTxnTm());
            // 退款类交易
              bthPagyLocalInfo.setTpamOrigTxnSsn(tpamUpacpTxnInfoVo.getTpamOrigTxnSsn());
            // 第三方通道交易类型
            bthPagyLocalInfo.setTpamTxnTypeNo(tpamUpacpTxnInfoVo.getTpamTxnTypeNo());
            // 订单金额
             bthPagyLocalInfo.setTpamTxnAmt(tpamUpacpTxnInfoVo.getTpamTxnAmt()==null?0:tpamUpacpTxnInfoVo.getTpamTxnAmt());
            //  10 - 支付     12 - 退款
             bthPagyLocalInfo.setTxnChlAction(tpamUpacpTxnInfoVo.getPagySysSoaActionFlag());
            // 支付账户类型
            bthPagyLocalInfo.setAcctType(tpamUpacpTxnInfoVo.getTpamAcctTypeNo());
            // 支付账户子类型
            bthPagyLocalInfo.setAcctSubType(tpamUpacpTxnInfoVo.getTpamAcctSubTypeNo());
            // 支付卡种：01=借记卡;02=贷记卡;
            bthPagyLocalInfo.setCardType(tpamUpacpTxnInfoVo.getTpamPayCardType());
            // 支付账户
            bthPagyLocalInfo.setAcctNo(tpamUpacpTxnInfoVo.getTpamAcctNo());
            bthPagyLocalInfo.setTxnAmt(tpamUpacpTxnInfoVo.getTpamTxnAmt()==null?0:tpamUpacpTxnInfoVo.getTpamTxnAmt());
            // 渠道交易金额
            String tpamSettleFee = tpamUpacpTxnInfoVo.getTpamSettleFee()==null?"0":tpamUpacpTxnInfoVo.getTpamSettleFee();
            // 支付手续费金额(分）
            bthPagyLocalInfo.setTxnFeeAmt(Long.parseLong(tpamSettleFee));
            //对账状态00=未对账01=已对账
            bthPagyLocalInfo.setChkSt("00");
            // 创建时间
            bthPagyLocalInfo.setCrtTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
            // 最后更新时间
            bthPagyLocalInfo.setLstUpdTm(formatter.parse(IfspDateTime.getYYYYMMDDHHMMSS()));
            // 渠道请求流水号
            bthPagyLocalInfo.setTxnReqSsn(tpamUpacpTxnInfoVo.getTxnReqSsn());
            // 渠道请求流水时间
            bthPagyLocalInfo.setTxnReqTm(tpamUpacpTxnInfoVo.getTxnReqTm());
            // 渠道原请求流水号
            bthPagyLocalInfo.setOrigTxnReqSsn(tpamUpacpTxnInfoVo.getOrigTxnReqSsn());
            // 渠道原请求流水时间
            bthPagyLocalInfo.setOrigTxnReqTm(tpamUpacpTxnInfoVo.getOrigTxnReqTm());
            // 通道产品编号
            bthPagyLocalInfo.setPagyProdId(tpamUpacpTxnInfoVo.getProdId());
            // 通道产品交易编号/通道路由编号
            bthPagyLocalInfo.setPagyProdTxnId(tpamUpacpTxnInfoVo.getTxnType());
			// 根据返回码来设置状态 , 0000 表示交易成功  , 其他都是处理中
			if ("0000".equals(tpamUpacpTxnInfoVo.getPagyRespCode())){

				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_00);
			}else {
				bthPagyLocalInfo.setTradeSt(Constans.TRADE_ST_02);
			}
            int k = -1;
            try {
                k = bthPagyLocalInfoDao.insertSelective(bthPagyLocalInfo);
            } catch (Exception e) {
                log.error("插入本地通道交易明细对账表失败", e);
                throw new IfspBizException(RespConstans.RESP_INSERT_ERR.getCode(), "插入本地通道交易明细对账表失败");
            }
            if (k != -1) {
                log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn()+ ",插入本地成功！");
            } else {
                log.info("通道支付内部流水号:" + bthPagyLocalInfo.getPagyPayTxnSsn() + ",插入本地失败！");
            }
        }
        localUnionResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        localUnionResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        return localUnionResponse;
    }

}
