package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.*;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.AccountUtil;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.UUIDCreator;
import com.scrcu.ebank.ebap.batch.service.ClearingExecutor;

import lombok.extern.slf4j.Slf4j;

@Service("unionpayClearingExecutor")
@Slf4j
public class UnionpayClearingExecutor implements ClearingExecutor {

	private static BigDecimal ZERO = new BigDecimal(0);
	private static BigDecimal MINUS_ONE = new BigDecimal(-1);
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	
	@Value("${subsidySubject}")
	private String subsidySubject;              //补贴科目  "53110023";
	@Value("${unionpaySubject}")
	private String subject;                     //银联科目
	@Value("${unionpaySeqNo}")
	private String seqNo;                       //银联内部帐序号
	
	@Resource
    private BthChkRsltInfoDao bthChkRsltInfoDao;    //对账成功结果信息
    
    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;   // 入账明细信息
    
    @Resource
    private PayOrderInfoDao payOrderInfoDao;           // 订单信息

	@Resource
	private PaySubOrderInfoDao paySubOrderInfoDao;           // 子订单信息
    
    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           // 商户基本信息
    
    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息
    
    @Resource
    private MchtSettlRateCfgDao mchtSettlRateCfgDao;   // 商户结算费率信息
    
    @Resource
    private MchtOrgRelDao mchtOrgRelDao;               // 商户组织关联信息
    
    @Resource
    private ParternBaseInfoDao parternBaseInfoDao;     //合作方信息
    
    @Resource
    private MchtGainsInfoDao mchtGainsInfoDao;      //分润信息表
    
    @Resource
    private BthSetCapitalDetailDao bthSetCapitalDetailDao;     // 清分表


	@Override
	public CommonResponse channelClearing(BatchRequest request) throws Exception{
		return null;
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void execute(List<BthMerInAccDtl> inAccDtlList) throws Exception
	{
		long start=System.currentTimeMillis();
		List<String> mchtIdList = inAccDtlList.stream().map(BthMerInAccDtl::getChlMerId).collect(Collectors.toList());   //生成商户号为泛型的List
		List<String> subMchtIdList = inAccDtlList.stream().filter(x -> x.getChlSubMerId()!=null).map(BthMerInAccDtl::getChlSubMerId).filter(Objects::nonNull).collect(Collectors.toList());   //生成二级商户号为泛型的List
		mchtIdList.addAll(subMchtIdList);
		subMchtIdList.clear();

		this.initMerInfo(mchtIdList);
		this.initMerOrgInfo(mchtIdList);
		this.initMerStlInfo(mchtIdList);
		this.initServiceInfo(mchtIdList);
		log.info("批量初始化商户基本信息、机构信息、商户结算信息完成，耗时【{}】",System.currentTimeMillis()-start);

		List<BthSetCapitalDetail> bthSetCapitalDetails = new ArrayList<>();
		for(BthMerInAccDtl inAccDtl:inAccDtlList)
		{
			//当前不考虑先结算给总店再结算给分店的情况
			//如果有二级商户号则结算给二级商户，否则结算给一级商户
			MchtBaseInfo merInfo = null;
			MchtContInfo merSettleInfo = null;
			MchtGainsInfo gainsInfo = null;
			MchtGainsInfo parternInfo = null;
			ParternBaseInfo serviceInfo = null;
			ParternBaseInfo serInfo = null;
			List<MchtOrgRel> orgRelList = null;
			//二级商户直接结算给本商户
			if (!IfspDataVerifyUtil.isBlank(inAccDtl.getChlSubMerId()) && (Constans.STL_TO_MER.equals(inAccDtl.getStlType())
					|| Constans.STL_TO_MER_INDIREACTLY.equals(inAccDtl.getStlType())))
			{
				merInfo = this.getMerBaseInfo(inAccDtl.getChlSubMerId());
				merSettleInfo = this.getMerStlInfo(inAccDtl.getChlSubMerId());
				serviceInfo = this.getServiceBaseInfo(inAccDtl.getChlSubMerId());
			}
			else
			{
				//1)一级商户订单结算
				//2)分店商户结算个上级商户
				//3)分店先结算给上级商户再结算到本商户  ---目前做法是直接结算给本商户
				merInfo = this.getMerBaseInfo(inAccDtl.getChlMerId());
				merSettleInfo = this.getMerStlInfo(inAccDtl.getChlMerId());
				serviceInfo = this.getServiceBaseInfo(inAccDtl.getChlMerId());
			}
			log.info("查询商户分润比例");
			gainsInfo = this.getMerGainsInfo(inAccDtl.getFundChannel());      //根据渠道查询分润比例信息
			if (gainsInfo == null)
			{
				throw new NullPointerException("渠道:{" + inAccDtl.getFundChannel() + "}分润信息未配置!");
			}
			orgRelList = this.getMerOrgInfo(inAccDtl.getChlMerId());      //根据一级商户号查询商户机构信息

			String orgTypes = "";
			String parternId="";
			for (MchtOrgRel orgRel : orgRelList)
			{
				orgTypes += orgRel.getOrgType() + ",";
				switch (orgRel.getOrgType())
				{
					case "01":
						merInfo.setRpsOrgNo(orgRel.getOrgId());
						break;
					case "02":
						merInfo.setOpOrgNo(orgRel.getOrgId());
						merInfo.setMerOpType("01");                  //直营模式,运营机构
						break;
					case "03":
						merInfo.setOpOrgNo(orgRel.getOrgId());
						merInfo.setMerOpType("02");                  //委托模式,运营商
						parternId=orgRel.getOrgId();
						break;
					case "04":
						merInfo.setPlatPartnerCode(orgRel.getOrgId());  //商户所属平台合作方
						break;
					case "05":
						//inAccDtl.setLogisPartnerCode(logisPartnerCode);  //订单使用的物流合作方在订单信息中获取
						break;
				}
			}
			// 订单明细（本金）清分
			this.addCapitalDetail(inAccDtl, merInfo, merSettleInfo, "01", bthSetCapitalDetails);
			// 订单明细手续费收入清分
			if("02".equals(inAccDtl.getOrderType())){
				//退款
				String orderId="";
				if(inAccDtl.getTxnSeqId().length()==20){
					PayOrderInfo orderInfo = payOrderInfoDao.queryByTxnSeqId(inAccDtl.getTxnSeqId());
					orderId = orderInfo.getOrigOrderSsn();
				}else{
					PaySubOrderInfo subOrderInfo = paySubOrderInfoDao.selectByPrimaryKey(inAccDtl.getTxnSeqId());
					orderId = subOrderInfo.getOriDetailsId();
				}
				BthSetCapitalDetail bthDetail  = bthSetCapitalDetailDao.queryByOrderEntry(orderId,Constans.ENTRY_TYPE_FEE_GAINS_SERVICE_ORG);
				if(bthDetail!=null){
					merInfo.setOpOrgNo(bthDetail.getParternCode());
					merInfo.setMerOpType("02");
					parternInfo = this.getServiceInfo(bthDetail.getParternCode(),inAccDtl.getFundChannel());      //根据渠道查询服务商分润比例信息
					if (parternInfo == null)
					{
						throw new NullPointerException("渠道:{" + inAccDtl.getFundChannel() + "}分润信息未配置!");
					}
					this.assignMerFee(inAccDtl, merInfo, merSettleInfo, parternInfo, bthSetCapitalDetails,parternBaseInfoDao.selectParternBaseInfo(bthDetail.getParternCode()));
				}else{
					this.assignMerFee(inAccDtl, merInfo, merSettleInfo, gainsInfo, bthSetCapitalDetails,serInfo);
				}
			}else{
				//消费
				if (orgTypes.contains("03")) {
					parternInfo = this.getServiceInfo(parternId,inAccDtl.getFundChannel());      //根据渠道查询服务商分润比例信息
					if (parternInfo == null)
					{
						throw new NullPointerException("渠道:{" + inAccDtl.getFundChannel() + "}分润信息未配置!");
					}
					this.assignMerFee(inAccDtl, merInfo, merSettleInfo, parternInfo, bthSetCapitalDetails,serviceInfo);
				}else{
					this.assignMerFee(inAccDtl, merInfo, merSettleInfo, gainsInfo, bthSetCapitalDetails,serInfo);
				}
			}
		}

		log.info("当片订单清分计算完成，耗时【{}】",System.currentTimeMillis()-start);

		List<String> txnSeqIdList=inAccDtlList.stream().map(BthMerInAccDtl::getTxnSeqId).collect(Collectors.toList());

		//更新明细表
		if(txnSeqIdList!=null&&txnSeqIdList.size()>0)
		{
			this.updateOrderStlStatus(txnSeqIdList);
		}

		log.info("当片订单更新明细表完成，耗时【{}】",System.currentTimeMillis()-start);
		//批量插入清分表
		if (bthSetCapitalDetails.size() > 0)
		{
			int count = bthSetCapitalDetailDao.insertBatch(bthSetCapitalDetails);
			log.info("插入请分表【{}】条，耗时【{}】", count,System.currentTimeMillis()-start);
		}
		mchtIdList.clear();
		inAccDtlList.clear();
//        txnSeqIdList.clear();
		bthSetCapitalDetails.clear();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public void execute(BthMerInAccDtl inAccDtl) throws Exception{
		try{
			//当前不考虑先结算给总店再结算给分店的情况
			//如果有二级商户号则结算给二级商户，否则结算给一级商户
			MchtBaseInfo merInfo = null;
			MchtContInfo merSettleInfo = null;
			MchtGainsInfo gainsInfo = null;
			ParternBaseInfo serviceInfo = null;
			List<MchtOrgRel> orgRelList = null;
			//二级商户直接结算给本商户
			if(!IfspDataVerifyUtil.isBlank(inAccDtl.getChlSubMerId()) && (Constans.STL_TO_MER.equals(inAccDtl.getStlType())
					||Constans.STL_TO_MER_INDIREACTLY.equals(inAccDtl.getStlType())))
			{
				merInfo = this.getMerBaseInfo(inAccDtl.getChlSubMerId());
				merSettleInfo = this.getMerStlInfo(inAccDtl.getChlSubMerId());
			}
			else
			{
				//1)一级商户订单结算
				//2)分店商户结算个上级商户
				//3)分店先结算给上级商户再结算到本商户  ---目前做法是直接结算给本商户
				merInfo = this.getMerBaseInfo(inAccDtl.getChlMerId());
				merSettleInfo = this.getMerStlInfo(inAccDtl.getChlMerId());
			}

			gainsInfo = this.getMerGainsInfo(inAccDtl.getFundChannel());      //根据渠道查询分润比例信息
			if(gainsInfo == null)
			{
				throw new NullPointerException("渠道:{"+inAccDtl.getFundChannel()+"}分润信息未配置!");
			}
			orgRelList = this.getMerOrgInfo(inAccDtl.getChlMerId());      //根据一级商户号查询商户机构信息

			for(MchtOrgRel orgRel : orgRelList)
			{
				switch(orgRel.getOrgType())
				{
					case "01":
						merInfo.setRpsOrgNo(orgRel.getOrgId());
						break;
					case "02":
						merInfo.setOpOrgNo(orgRel.getOrgId());
						merInfo.setMerOpType("01");                  //直营模式,运营机构
						break;
					case "03":
						merInfo.setOpOrgNo(orgRel.getOrgId());
						merInfo.setMerOpType("02");                  //委托模式,运营商
						break;
					case "04":
						merInfo.setPlatPartnerCode(orgRel.getOrgId());  //商户所属平台合作方
						break;
					case "05":
						//inAccDtl.setLogisPartnerCode(logisPartnerCode);  //订单使用的物流合作方在订单信息中获取
						break;
				}
			}
			List<BthSetCapitalDetail> bthSetCapitalDetails = new ArrayList<>();
			// 订单明细（本金）清分
			this.addCapitalDetail(inAccDtl, merInfo, merSettleInfo, "01", bthSetCapitalDetails);
			// 订单明细手续费收入清分
			this.assignMerFee(inAccDtl, merInfo, merSettleInfo,gainsInfo, bthSetCapitalDetails,serviceInfo);
			//更新明细表
			this.updateOrderStlStatus(inAccDtl);
			//批量插入
			if(bthSetCapitalDetails.size()>0){
				int count = bthSetCapitalDetailDao.insertBatch(bthSetCapitalDetails);
				log.info("插入[{}]请分表[{}]条", inAccDtl.getTxnSeqId(), count);
			}
		}catch (Exception e){
			log.info("银联订单清分出现异常_[{}]",e.getMessage());
			throw new Exception(e);
		}


	}
	
//	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public void addCapitalDetail(BthMerInAccDtl settleInfo, MchtBaseInfo mgtMerInfo,
			MchtContInfo merSettleInfo, String cur, List<BthSetCapitalDetail> bthSetCapitalDetails)
	{

		// 如果为实时支付,日终不必处理本金
		if (Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING.equals(settleInfo.getStlStatus())
				&& Constans.ORDER_TYPE_CONSUME.equals(settleInfo.getOrderType())
				&& IfspDataVerifyUtil.isBlank(settleInfo.getCommissionAmt()))
		{
			return;
		}
		
		//实时结算退款(退款账户类型为结算账户)
		if (Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING.equals(settleInfo.getStlStatus())
				&& Constans.ORDER_TYPE_RETURN.equals(settleInfo.getOrderType())
				&& Constans.REFUND_ACC_TYPE_SETTLE.equals(settleInfo.getRefundAccType()))
		{
			return;
		}
		
		

		BthSetCapitalDetail detail = this.initCapitalDetail(settleInfo, mgtMerInfo, cur);
		detail.setAccountType(settleInfo.getSetlAcctType());

		BigDecimal merIncome = new BigDecimal(0);
		//TODO : confirm ---> merIncome = setlAmt = (支付金额+营销金额+红包金额+积分金额)*(1-行内扣率) - (支付金额+营销金额+红包金额+积分金额)*返佣比例
		//txnAmt = (支付金额+营销金额+红包金额+积分金额)
		//目前来说有营销就没有红包，有红包就没有营销，计算操作在PreClearing步骤中完成
		merIncome = new BigDecimal(settleInfo.getSetlAmt());

		// 消费 本金结算 从待清算账户转入到商户结算账户
		if (Constans.ORDER_TYPE_CONSUME.equals(settleInfo.getOrderType()))
		{
			//t+0的不再计算本金
			if(!Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING.equals(settleInfo.getStlStatus())) {
				//1)商户收入
				detail.setTranAmount(merIncome);

				// ***转出账户(商户待结算账户)****
				detail.setOutAccountNo(merSettleInfo.getLiqAcctNo());
				detail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());

				// ***转入账户(商户结算账户)****
				detail.setInAccountNo(merSettleInfo.getSettlAcctNo()); // 转入账户(商户结算账户)
				detail.setInAccountName(merSettleInfo.getSettlAcctName()); // 转入商户名
				detail.setInAccoutOrg(merSettleInfo.getSettlAcctOrgId()); // 转入商户机构号

				detail.setAccountType(merSettleInfo.getSettlAcctType());  //设置结算账户类型 0 - 本行, 1 - 他行
			}
			//2)反佣
			if(!IfspDataVerifyUtil.isBlank(settleInfo.getCommissionAmt()))
			{
				BigDecimal commAmt = new BigDecimal(settleInfo.getCommissionAmt());
				if(commAmt.compareTo(ZERO) == 1)
				{
					BthSetCapitalDetail commDetail = this.initCapitalDetail(settleInfo, mgtMerInfo, cur);
					commDetail.setTranAmount(new BigDecimal(settleInfo.getCommissionAmt()));
					commDetail.setEntryType(Constans.ENTRY_TYPE_COMM_IN);             //佣金收入
					
					// ***转出账户(商户待结算账户)****
					commDetail.setOutAccountNo(merSettleInfo.getLiqAcctNo());    
					commDetail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());   
					
					MchtContInfo commissionMer = null;
					//如果是二级商户订单,返佣给一级商户
					if(!IfspDataVerifyUtil.isBlank(settleInfo.getChlSubMerId()))
					{
						//查询父商户信息
						commissionMer = this.getMerStlInfo(mgtMerInfo.getParMchId());
					}
					//返佣给平台
					else
					{
						//查询平台商户信息
						commissionMer = this.getMerStlInfo(mgtMerInfo.getPlatPartnerCode());
						//commissionMer = this.getMerStlInfo(mgtMerInfo.getParMchId());
					}
					
					//上级商户返佣收入账户
					commDetail.setInAccountNo(commissionMer.getSettlAcctNo());            // 转入账户(商户结算账户)
					commDetail.setInAccountName(commissionMer.getSettlAcctName());        // 转入商户名
					commDetail.setInAccoutOrg(commissionMer.getSettlAcctOrgId());         // 转入商户机构号
					
					commDetail.setAccountType(commissionMer.getSettlAcctType());  //设置结算账户类型 0 - 本行, 1 - 他行
					
					//金额转换，分转换成元
					commDetail.setTranAmount(commDetail.getTranAmount().divide(ONE_HUNDRED));
					
//					bthSetCapitalDetailDao.insertSelective(commDetail);
					bthSetCapitalDetails.add(commDetail);
				}
				
			}
			
			//3)物流
			if(!IfspDataVerifyUtil.isBlank(settleInfo.getLogisFee()) && !"0".equals(settleInfo.getLogisFee()))
			{
				BigDecimal logisAmt = new BigDecimal(settleInfo.getLogisFee());
				if(settleInfo.getLogisFeeAmt() != null && !"0".equals(settleInfo.getLogisFeeAmt()))
				{
					logisAmt = logisAmt.subtract(new BigDecimal(settleInfo.getLogisFeeAmt()));
				}
				
				if(logisAmt.compareTo(ZERO) == 1)
				{
					BthSetCapitalDetail logisDetail = this.initCapitalDetail(settleInfo, mgtMerInfo, cur);
					logisDetail.setTranAmount(logisAmt);
					logisDetail.setEntryType(Constans.ENTRY_TYPE_MER);             //商户收入(物流收入)
					
					// ***转出账户(商户待结算账户)****
					logisDetail.setOutAccountNo(merSettleInfo.getLiqAcctNo());    
					logisDetail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());   
					
					//统一物流
					if(Constans.LOGIS_TYPE_BANK.equals(settleInfo.getLogisType()))
					{
						//查询物流合作方信息
						ParternBaseInfo partnerInfo = this.getPartnerInfo(settleInfo.getLogisPartnerCode());
						
						//物流公司结算信息
						logisDetail.setInAccountNo(partnerInfo.getAccountNo());          
						logisDetail.setInAccountName(partnerInfo.getAccountName());      
						//logisDetail.setInAccoutOrg(partnerInfo.getAccountOrg());        
					}
					//平台物流
					else
					{
						//查询平台商户信息
						MchtContInfo platMer = this.getMerStlInfo(mgtMerInfo.getPlatPartnerCode());
						
						//平台商户结算信息
						logisDetail.setInAccountNo(platMer.getSettlAcctNo());          
						logisDetail.setInAccountName(platMer.getSettlAcctName());      
						logisDetail.setInAccoutOrg(platMer.getSettlAcctOrgId());      
						
						logisDetail.setAccountType(platMer.getSettlAcctType());  //设置结算账户类型 0 - 本行, 1 - 他行
						
					}
					
					//金额转换，分转换成元
					logisDetail.setTranAmount(logisDetail.getTranAmount().divide(ONE_HUNDRED));
					
//					bthSetCapitalDetailDao.insertSelective(logisDetail);
					bthSetCapitalDetails.add(logisDetail);
				}
				
			}
			
			//金额转换，分转换成元
			detail.setTranAmount(detail.getTranAmount().divide(ONE_HUNDRED));
//			bthSetCapitalDetailDao.insertSelective(detail);
			//bthSetCapitalDetails.add(detail);
			if(!Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING.equals(settleInfo.getStlStatus())){
				bthSetCapitalDetails.add(detail);
			}
		}
		// 退货 从商户结算账户转入到待结算账户 (注：退货时金额记负数，转出转入账户与消费时一样，方便轧差)
		else if (Constans.ORDER_TYPE_RETURN.equals(settleInfo.getOrderType()))
		{
			//1)退商户收入金额
			if (Constans.REFUND_ACC_TYPE_CLEANER.equals(settleInfo.getRefundAccType()))
			{
				detail.setTranAmount(merIncome.multiply(MINUS_ONE));  //退款记负金额
				detail.setEntryType(Constans.ENTRY_TYPE_PAY_MER);     //从商户结算账户退款
				
				//转出账户-商户待清算账户
				detail.setOutAccountNo(merSettleInfo.getLiqAcctNo()); 
				detail.setOutAccountName(merSettleInfo.getLiqAcctName()); 
				detail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId()); 

				//转入账户-商户结算账户
				detail.setInAccountNo(merSettleInfo.getSettlAcctNo());   
				detail.setInAccountName(merSettleInfo.getSettlAcctName());  
				detail.setInAccoutOrg(merSettleInfo.getSettlAcctOrgId()); 
				
				//金额转换，分转换成元
				detail.setTranAmount(detail.getTranAmount().divide(ONE_HUNDRED));
//				bthSetCapitalDetailDao.insertSelective(detail);
				bthSetCapitalDetails.add(detail);
			}
			else if (Constans.REFUND_ACC_TYPE_GUARANTEE.equals(settleInfo.getRefundAccType())&&Constans.SETTL_ACCT_TYPE_PLAT.equals(merSettleInfo.getSettlAcctType()))
			{
				//1.1)从内部帐补保证金(退款金额)
				/******************************* 补足保证金 **************************************/
				//TODO:交易金额  test confirm : txnAmt = 支付金额+营销金额+红包金额+积分金额
				detail.setTranAmount(new BigDecimal(settleInfo.getTxnAmt()));     //注意这里的金额为实际从保证金账户实际扣去的金额
				detail.setEntryType(Constans.ENTRY_TYPE_FOR_GUARANTEE);           //补足保证金

				//转出账户-商户待结算账户
				detail.setOutAccountNo(merSettleInfo.getLiqAcctNo());            
				detail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());             
				detail.setOutAccountName(merSettleInfo.getLiqAcctName());   

				//转入账户-商户保证金账户
				detail.setInAccountNo(merSettleInfo.getDepAcctNo()); 
				detail.setInAccountName(merSettleInfo.getDeptAcctName());
				detail.setInAccoutOrg(merSettleInfo.getDeptAcctOrgId()); 
				
				//1.2）结算账户退款(结算金额)：内部户-->结算账户记负金额
				BthSetCapitalDetail detail2 = this.initCapitalDetail(settleInfo, mgtMerInfo, cur);
				
				//转出账户-商户待清算账户
				detail2.setOutAccountNo(merSettleInfo.getLiqAcctNo()); 
				detail2.setOutAccountName(merSettleInfo.getLiqAcctName()); 
				detail2.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId()); 

				//转入账户-商户结算账户
				detail2.setInAccountNo(merSettleInfo.getSettlAcctNo());   
				detail2.setInAccountName(merSettleInfo.getSettlAcctName());  
				detail2.setInAccoutOrg(merSettleInfo.getSettlAcctOrgId()); 
				
				//交易金额
				detail2.setTranAmount(merIncome.multiply(MINUS_ONE));            //退款记负金额
				detail2.setEntryType(Constans.ENTRY_TYPE_PAY_MER);     //商户退款补足保证金

				//金额转换，分转换成元
				detail2.setTranAmount(detail2.getTranAmount().divide(ONE_HUNDRED));
//				bthSetCapitalDetailDao.insertSelective(detail2);
				bthSetCapitalDetails.add(detail2);
				
				//金额转换，分转换成元
				detail.setTranAmount(detail.getTranAmount().divide(ONE_HUNDRED));
//				bthSetCapitalDetailDao.insertSelective(detail);
				bthSetCapitalDetails.add(detail);
			}
			else if (Constans.REFUND_ACC_TYPE_SETTLE.equals(settleInfo.getRefundAccType())||(Constans.REFUND_ACC_TYPE_GUARANTEE.equals(settleInfo.getRefundAccType())&&Constans.SETTL_ACCT_TYPE_CROSS.equals(merSettleInfo.getSettlAcctType())))
			{
				//将结算账户多退的金额，退还给结算账户(手续费部分本不应该从商户结算账户退，但日间退款时所有金额都是从结算账户出)
				BigDecimal diffAmt = new BigDecimal(settleInfo.getSetlFeeAmt()).add(new BigDecimal(settleInfo.getLogisFee())).add(new BigDecimal(settleInfo.getCommissionAmt()));
				detail.setTranAmount(diffAmt);

				// ***转出账户(商户待结算账户)****
				detail.setOutAccountNo(merSettleInfo.getLiqAcctNo());            
				detail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());           
				detail.setOutAccountName(merSettleInfo.getLiqAcctName());

				if(Constans.REFUND_ACC_TYPE_SETTLE.equals(settleInfo.getRefundAccType())) {


					// ***转入账户(商户结算账户)****
					detail.setInAccountNo(merSettleInfo.getSettlAcctNo()); // 转入账户(商户结算账户)
					detail.setInAccountName(merSettleInfo.getSettlAcctName()); // 转入商户名
					detail.setInAccoutOrg(merSettleInfo.getSettlAcctOrgId()); // 转入商户机构号
					detail.setAccountType(merSettleInfo.getSettlAcctType());  //设置结算账户类型 0 - 本行, 1 - 他行
				} else if(Constans.REFUND_ACC_TYPE_GUARANTEE.equals(settleInfo.getRefundAccType())&&Constans.SETTL_ACCT_TYPE_CROSS.equals(merSettleInfo.getSettlAcctType())){
					// ***转入账户(商户结算账户)****
					detail.setInAccountNo(merSettleInfo.getDepAcctNo()); // 转入账户(商户结算账户)
					detail.setInAccountName(merSettleInfo.getDeptAcctName()); // 转入商户名
					detail.setInAccoutOrg(merSettleInfo.getDeptAcctOrgId()); // 转入商户机构号
					detail.setAccountType(Constans.SETTL_ACCT_TYPE_PLAT);  //设置结算账户类型 0 - 本行, 1 - 他行
				}
				
				//detail.setAccountType(merSettleInfo.getSettlAcctType());  //设置结算账户类型 0 - 本行, 1 - 他行
				
				//金额转换，分转换成元
				detail.setTranAmount(detail.getTranAmount().divide(ONE_HUNDRED));
//				bthSetCapitalDetailDao.insertSelective(detail);
				bthSetCapitalDetails.add(detail);
			}
			
			//2)退返佣
			if(!IfspDataVerifyUtil.isBlank(settleInfo.getCommissionAmt()))
			{
				BigDecimal commAmt = new BigDecimal(settleInfo.getCommissionAmt());
				if(commAmt.compareTo(ZERO) == 1)
				{
					BthSetCapitalDetail commDetail = this.initCapitalDetail(settleInfo, mgtMerInfo, cur);
					
					MchtContInfo commissionMer = null;
					//如果是二级商户订单,返佣给一级商户
					if(!IfspDataVerifyUtil.isBlank(settleInfo.getChlSubMerId()))
					{
						//查询父商户信息
						commissionMer = this.getMerStlInfo(mgtMerInfo.getParMchId());
					}
					//返佣给平台
					else
					{
						//查询平台商户信息
						commissionMer = this.getMerStlInfo(mgtMerInfo.getPlatPartnerCode());
						//commissionMer = this.getMerStlInfo(mgtMerInfo.getParMchId());
					}
					
					//上级商户返佣支出账户
					commDetail.setOutAccountNo(merSettleInfo.getLiqAcctNo());   
					commDetail.setOutAccountName(merSettleInfo.getLiqAcctName());
					commDetail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());   
					
					//转入账户-商户内部户
					commDetail.setInAccountNo(commissionMer.getSettlAcctNo());      
					commDetail.setInAccountName(commissionMer.getSettlAcctName());    
					commDetail.setInAccoutOrg(commissionMer.getSettlAcctOrgId()); 
					
					commDetail.setTranAmount(commAmt.multiply(MINUS_ONE));    //退款金额记负数
					commDetail.setEntryType(Constans.ENTRY_TYPE_COMM_IN);     //商户佣金收入
					
					//金额转换，分转换成元
					commDetail.setTranAmount(commDetail.getTranAmount().divide(ONE_HUNDRED));
					
//					bthSetCapitalDetailDao.insertSelective(commDetail);
					bthSetCapitalDetails.add(commDetail);
				}
				
			}
			
			//3)退物流
			if(!IfspDataVerifyUtil.isBlank(settleInfo.getLogisFee()) && !"0".equals(settleInfo.getLogisFee()))
			{
				BigDecimal logisAmt = new BigDecimal(settleInfo.getLogisFee());
				if(settleInfo.getLogisFeeAmt() != null && !"0".equals(settleInfo.getLogisFeeAmt()))
				{
					logisAmt = logisAmt.subtract(new BigDecimal(settleInfo.getLogisFeeAmt()));
				}
				
				if(logisAmt.compareTo(ZERO) == 1)
				{
					BthSetCapitalDetail logisDetail = this.initCapitalDetail(settleInfo, mgtMerInfo, cur);
					logisDetail.setTranAmount(logisAmt.multiply(MINUS_ONE));
					logisDetail.setEntryType(Constans.ENTRY_TYPE_MER);             //商户收入(物流收入)
					
					// ***转出账户(商户待结算账户)****
					logisDetail.setOutAccountNo(merSettleInfo.getLiqAcctNo());   
					logisDetail.setOutAccountName(merSettleInfo.getLiqAcctName());
					logisDetail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());   
					
					//统一物流
					if(Constans.LOGIS_TYPE_BANK.equals(settleInfo.getLogisType()))
					{
						//查询物流合作方信息
						ParternBaseInfo partnerInfo = this.getPartnerInfo(settleInfo.getLogisPartnerCode());
						
						//物流公司结算信息
						logisDetail.setInAccountNo(partnerInfo.getAccountNo());          
						logisDetail.setInAccountName(partnerInfo.getAccountName());      
						logisDetail.setInAccoutOrg(partnerInfo.getAccountOrg());        
					}
					//平台物流
					else
					{
						//查询平台商户信息
						MchtContInfo platMer = this.getMerStlInfo(mgtMerInfo.getPlatPartnerCode());
						
						//平台商户结算信息
						logisDetail.setInAccountNo(platMer.getSettlAcctNo());          
						logisDetail.setInAccountName(platMer.getSettlAcctName());      
						logisDetail.setInAccoutOrg(platMer.getSettlAcctOrgId());      
						
						logisDetail.setAccountType(platMer.getSettlAcctType());  //设置结算账户类型 0 - 本行, 1 - 他行
						
					}
					
					//金额转换，分转换成元
					logisDetail.setTranAmount(logisDetail.getTranAmount().divide(ONE_HUNDRED));
//					bthSetCapitalDetailDao.insertSelective(logisDetail);
					bthSetCapitalDetails.add(logisDetail);
				}
				
			}

		}
		
	}
	
	//手续费清分
//	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public void assignMerFee(BthMerInAccDtl settleInfo, MchtBaseInfo merInfo,
			MchtContInfo merSettleInfo,MchtGainsInfo gainsInfo, List<BthSetCapitalDetail> bthSetCapitalDetails,ParternBaseInfo serviceInfo)
	{
		BigDecimal totalRatio = new BigDecimal(0);
		BigDecimal actualRatio = new BigDecimal(0);
		//TODO : 确认手续费金额 = (支付金额+营销金额+红包金额+积分金额)*行内扣率
		//本行支付时物流不收手续费、第三方支付时物流手续费=第三方渠道手续费?
		BigDecimal merFeeAmt = new BigDecimal(settleInfo.getSetlFeeAmt());   //行内手续费金额
		
		//行内手续费=商户手续费+物流手续费
		if(settleInfo.getLogisFeeAmt() != null && !"0".equals(settleInfo.getLogisFeeAmt()))
		{
			merFeeAmt = merFeeAmt.add(new BigDecimal(settleInfo.getLogisFeeAmt()));
		}
				
		BigDecimal channelFeeAmt = new BigDecimal(settleInfo.getTramFeeAmt());   //行内手续费金额
		BigDecimal feeIncome = merFeeAmt.subtract(channelFeeAmt);
		if(feeIncome.compareTo(ZERO) == 1)
		{
			BigDecimal curOrgFee = new BigDecimal(0);
			//当前已经分配出去的手续费金额  --解决因为四舍五入导致的总分配手续费大于merFeeAmt问题
			BigDecimal curTotalFee = new BigDecimal(0);  
			
			List<ProfitsInfo> gains = new ArrayList<ProfitsInfo>();
			//收单机构分润比例
			if(gainsInfo.getCellectOrgDist() != null && gainsInfo.getCellectOrgDist().compareTo(UnionpayClearingExecutor.ZERO) ==1)
			{
				totalRatio = totalRatio.add(gainsInfo.getCellectOrgDist());
				ProfitsInfo gain = new ProfitsInfo();
				gain.setOrgType("01"); 
				gain.setProfitOrg(merInfo.getRpsOrgNo());
				gain.setRatio(gainsInfo.getCellectOrgDist());
				gains.add(gain);
			}
			
			//运营机构分润比例/运营机构可能为空..
			if(gainsInfo.getProxyOrgDist() != null && gainsInfo.getProxyOrgDist().compareTo(UnionpayClearingExecutor.ZERO) ==1 && (merInfo.getOpOrgNo()!= null))
			{
				totalRatio = totalRatio.add(gainsInfo.getProxyOrgDist()); 
				ProfitsInfo gain = new ProfitsInfo();
				gain.setOrgType("02"); 
				gain.setProfitOrg(merInfo.getOpOrgNo());
				gain.setRatio(gainsInfo.getProxyOrgDist());
				gains.add(gain);
			}
			
			//计算每个机构分润所得
			int index = 0;
			for(ProfitsInfo gain : gains)
			{
				actualRatio = gain.getRatio().divide(totalRatio, 4, BigDecimal.ROUND_HALF_UP);
				curOrgFee = feeIncome.multiply(actualRatio); // **结算金额精确到小数点后几位？？
				curOrgFee = curOrgFee.setScale(0, BigDecimal.ROUND_HALF_UP);
				
				//确保不多分手续费
				if(curTotalFee.add(curOrgFee).doubleValue() >= feeIncome.doubleValue())
				{
					curOrgFee = feeIncome.subtract(curTotalFee);
				}
				
				//确保手续费分完，无剩余
				if(gains.size() == index +1)
				{
					if(curTotalFee.add(curOrgFee).doubleValue() <= feeIncome.doubleValue())
					{
						curOrgFee = feeIncome.subtract(curTotalFee);
					}
				}
				
				gain.setProfitAmt(curOrgFee);
				curTotalFee = curTotalFee.add(curOrgFee);
				
				index ++;
			}
			
			// 2、遍历分润机构，进行手续费分润
			for (ProfitsInfo gain : gains)
			{
				this.addMerFeeDetail(settleInfo, merInfo, merSettleInfo, gain, "", bthSetCapitalDetails,serviceInfo);
			}
		}
		else
		{
			//渠道费率>行内费率
			BigDecimal subSidyAmt = feeIncome.multiply(MINUS_ONE);
			this.addSubsidyDetail(settleInfo,merInfo,merSettleInfo,subSidyAmt, "", bthSetCapitalDetails);
		}
		
		//渠道手续费补账
		this.payChannelFee(settleInfo, merInfo, merSettleInfo, channelFeeAmt, "", bthSetCapitalDetails);
		
		//品牌服务费补账
		if(!IfspDataVerifyUtil.isBlank(settleInfo.getBrandFee()) && !"0".equals(settleInfo.getBrandFee()))
		{
			BigDecimal brandFee = new BigDecimal(settleInfo.getBrandFee());
			this.payBrandFee(settleInfo, merInfo, merSettleInfo,brandFee, bthSetCapitalDetails);
		}
	}
	
	/**
	 * 添加一条资金明细记录(手续费)
	 * 
	 */
//	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public void addMerFeeDetail(BthMerInAccDtl settleInfo, MchtBaseInfo merInfo, MchtContInfo merSettleInfo,
			ProfitsInfo gain, String entryType, List<BthSetCapitalDetail> bthSetCapitalDetails,ParternBaseInfo serviceInfo)
	{
		boolean isRealTime = false; // MER_JS1_STATUS状态04代表实时结算
		if (Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING.equals(settleInfo.getStlStatus()))
		{
			isRealTime = true;
		}
		

		BthSetCapitalDetail detail = this.initCapitalDetail(settleInfo, merInfo, "01");
		BthSetCapitalDetail bthDetail = null;
		

		// 消费-->从待结算账户（或者手续费待分配账户）转入到分润机构结算账户
		if (Constans.ORDER_TYPE_CONSUME.equals(settleInfo.getOrderType()))
		{
			detail.setTranAmount(gain.getProfitAmt());
			// 转出账户
			detail.setOutAccountNo(merSettleInfo.getLiqAcctNo());          // 转出账户  
			detail.setOutAccountName(merSettleInfo.getLiqAcctName());     // 转出账户名
			detail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());      // 转出账户机构号  

			// ***转入账户(商户结算账户)****

			// 分录流水类型:01-商户入账(商户本金结算)；02-手续费分润（行内手续费进行分润）;03-手续费垫付（行内手续费垫付）04-商户手续费扣帐（商户手续费结算）；05-第三方手续费入账（第三方资金通道的手续费结算）
			switch (gain.getOrgType())
			{
				case "01": // 收单机构
					detail.setEntryType(Constans.ENTRY_TYPE_FEE_GAINS_SD_ORG); // 05-手续费分润（收单机构）
					detail.setInAccountNo(gain.getProfitAcc()); // 转入账户(商户结算账户)
					detail.setInAccoutOrg(gain.getProfitOrg()); // 转入商户机构号
					break;
				case "02": // 电子银行中心（**运营机构？）
					if(serviceInfo!=null){
						detail.setEntryType(Constans.ENTRY_TYPE_FEE_GAINS_SERVICE_ORG); // 16-手续费分润（服务商）
						detail.setInAccountNo(serviceInfo.getAccountNo()); // 转入账户(服务商结算账户)
						detail.setInAccoutOrg(serviceInfo.getAccountOrg()); // 转入服务商机构号
						detail.setParternCode(serviceInfo.getParternCode());
					}else{
						detail.setEntryType(Constans.ENTRY_TYPE_FEE_GAINS_OPERATE_ORG); // 08-手续费分润（运营结构）
						detail.setInAccountNo(gain.getProfitAcc()); // 转入账户(商户结算账户)
						detail.setInAccoutOrg(gain.getProfitOrg()); // 转入商户机构号
					}
					break;
			}
		}
		else if (Constans.ORDER_TYPE_RETURN.equals(settleInfo.getOrderType()))
		{
			String orderId = "";
			switch (gain.getOrgType())
			{
				case "01": // 收单机构
					detail.setEntryType(Constans.ENTRY_TYPE_FEE_PAY_SD_ORG);      // 09-手续费支出（收单机构）
					detail.setOutAccountNo(gain.getProfitAcc()); // 转出账户(商户结算账户)
					detail.setOutAccoutOrg(gain.getProfitOrg()); // 转出商户机构号
					break;
				case "02": // 运营机构
					if(serviceInfo!=null){
						detail.setEntryType(Constans.ENTRY_TYPE_FEE_PAY_SERVICE_ORG);  // 17-手续费支出（服务商）
						detail.setOutAccountNo(serviceInfo.getAccountNo()); // 转入账户(服务商结算账户)
						detail.setOutAccoutOrg(serviceInfo.getAccountOrg()); // 转入服务商机构号
						detail.setParternCode(serviceInfo.getParternCode());
					}else{
						detail.setEntryType(Constans.ENTRY_TYPE_FEE_PAY_OPERATE_ORG);  // 11-手续费支出（电子银行中心/运营机构）
						detail.setOutAccountNo(gain.getProfitAcc()); // 转出账户(商户结算账户)
						detail.setOutAccoutOrg(gain.getProfitOrg()); // 转出商户机构号
					}
					break;
			}
			
			detail.setTranAmount(gain.getProfitAmt());

			// ***转出账户(商户待结算账户)****

			// ***转入账户(机构分润结算账户)****
			detail.setInAccountNo(merSettleInfo.getLiqAcctNo());       // 转入账户(商户结算账户)
			detail.setInAccoutOrg(merSettleInfo.getLiqAcctOrgId());           // 转入商户机构号
			detail.setInAccountName(merSettleInfo.getLiqAcctName());   //转入商户名
		}

		// 新增资金结算明细记录
		//金额转换，分转换成元
		detail.setTranAmount(detail.getTranAmount().divide(ONE_HUNDRED));
				
//		bthSetCapitalDetailDao.insertSelective(detail);
		bthSetCapitalDetails.add(detail);
	}
	
	/**
	 * 手续费补贴
	 * @param settleInfo
	 * @param merInfo
	 * @param merSettleInfo
	 * @param subSidyAmt
	 * @param entryType
	 */
//	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public void addSubsidyDetail(BthMerInAccDtl settleInfo, MchtBaseInfo merInfo, MchtContInfo merSettleInfo,
			BigDecimal subSidyAmt, String entryType, List<BthSetCapitalDetail> bthSetCapitalDetails)
	{
		BthSetCapitalDetail detail = this.initCapitalDetail(settleInfo, merInfo, "01");
		
		// 消费-->从待结算账户（或者手续费待分配账户）转入到分润机构结算账户
		if (Constans.ORDER_TYPE_CONSUME.equals(settleInfo.getOrderType()))
		{
			/***
			 * 消费订单：
			 * 当渠道费率>行内扣率时：
			 *   借：电子银行手续费补贴支出53110023    支付金额*（渠道费率-行内扣率）
			 *   贷：商户待清算账户   支付金额*（渠道费率-行内扣率）
			 * 
			 * **/

			detail.setTranAmount(subSidyAmt);
			//转出账户:手续费补贴支出账户
			detail.setOutAccountNo(AccountUtil.genInnerAccount(merInfo.getRpsOrgNo(), this.getSubsidySubject()));        
			detail.setOutAccoutOrg(merInfo.getRpsOrgNo());      

			//商户内部帐
			detail.setInAccountNo(merSettleInfo.getLiqAcctNo());       // 转入账户(商户结算账户)
			detail.setInAccoutOrg(merSettleInfo.getLiqAcctOrgId());           // 转入商户机构号
			detail.setInAccountName(merSettleInfo.getLiqAcctName());   //转入商户名
			
			detail.setEntryType(Constans.ENTRY_TYPE_FEE_PAY_EBANK); // 12-手续费支出（电子银行补贴手续费支出）
		}
		else if (Constans.ORDER_TYPE_RETURN.equals(settleInfo.getOrderType()))
		{
			/***
			 * 退款订单：
			 * 当渠道费率>行内扣率时：
			 *   借：电子银行手续费补贴支出53110023    支付金额*（渠道费率-行内扣率）(红字)
			 *   贷：商户待清算账户   支付金额*（渠道费率-行内扣率）(红字)
			 * 
			 * **/
			
			subSidyAmt = subSidyAmt.multiply(MINUS_ONE);             //红字
			detail.setTranAmount(subSidyAmt);
			detail.setEntryType(Constans.ENTRY_TYPE_FEE_PAY_EBANK);  // 12-手续费支出（电子银行补贴手续费支出）
			
			//转出账户:手续费补贴支出账户
			detail.setOutAccountNo(AccountUtil.genInnerAccount(merInfo.getRpsOrgNo(), this.getSubsidySubject()));        
			detail.setOutAccoutOrg(merInfo.getRpsOrgNo());      

			//商户内部帐
			detail.setInAccountNo(merSettleInfo.getLiqAcctNo());       // 转入账户(商户结算账户)
			detail.setInAccoutOrg(merSettleInfo.getLiqAcctOrgId());           // 转入商户机构号
			detail.setInAccountName(merSettleInfo.getLiqAcctName());   //转入商户名
		}

		//金额转换，分转换成元
		detail.setTranAmount(detail.getTranAmount().divide(ONE_HUNDRED));
				
//		bthSetCapitalDetailDao.insertSelective(detail);
		bthSetCapitalDetails.add(detail);
	}
	
	/**
	 * 退渠道手续费
	 * @param settleInfo
	 * @param merInfo
	 * @param merSettleInfo
	 * @param entryType
	 */
//	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public void payChannelFee(BthMerInAccDtl settleInfo, MchtBaseInfo merInfo, MchtContInfo merSettleInfo,
			BigDecimal channelFee, String entryType, List<BthSetCapitalDetail> bthSetCapitalDetails)
	{
		//迁移数据不退渠道手续费（渠道手续费已经在日间交易时扣除）
		if(Constans.CHNL_FEE_FLAG_NO.equals(settleInfo.getChnlFeeFlag()))
		{
			return;
		}
				
		BthSetCapitalDetail detail = this.initCapitalDetail(settleInfo, merInfo, "01");
		
		// 消费-->从待结算账户（或者手续费待分配账户）转入到分润机构结算账户
		if (Constans.ORDER_TYPE_CONSUME.equals(settleInfo.getOrderType()))
		{
			/***
			 * 补渠道手续费
			 * 借：商户待清算账户            退款金额*银联通道手续费比例；
			 * 贷：银联清算账户               退款金额*银联通道手续费比例；
			 * 
			 * **/
			detail.setTranAmount(channelFee);
			
			//商户内部帐
			detail.setOutAccountNo(merSettleInfo.getLiqAcctNo());       // 转入账户(商户结算账户)
			detail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());           // 转入商户机构号
			detail.setOutAccountName(merSettleInfo.getLiqAcctName());   //转入商户名
			
			//转入账户:银联清算账户  
			detail.setInAccountNo(AccountUtil.genInnerAccountWithSeq(merInfo.getRpsOrgNo(), this.getSubject(),this.getSeqNo()));        
			detail.setInAccoutOrg(merInfo.getRpsOrgNo());      

			
			
			detail.setEntryType(Constans.ENTRY_TYPE_BRANCH_FEE); // 04-第三方手续费入账
		}
		else if (Constans.ORDER_TYPE_RETURN.equals(settleInfo.getOrderType()))
		{
			/***
			 *退渠道手续费
			 * 借：商户待清算账户            退款金额*银联通道手续费比例；
			 * 贷：银联清算账户               退款金额*银联通道手续费比例；
			 * 
			 * **/
			
			channelFee = channelFee.multiply(MINUS_ONE);             //退款记红字
			detail.setTranAmount(channelFee);
			detail.setEntryType(Constans.ENTRY_TYPE_BRANCH_FEE);     // 04-第三方手续费入账）
			
			//商户内部帐
			detail.setOutAccountNo(merSettleInfo.getLiqAcctNo());       // 转入账户(商户结算账户)
			detail.setOutAccoutOrg(merSettleInfo.getLiqAcctOrgId());           // 转入商户机构号
			detail.setOutAccountName(merSettleInfo.getLiqAcctName());   //转入商户名
			
			//转出账户:银联清算账户  
			detail.setInAccountNo(AccountUtil.genInnerAccountWithSeq(merInfo.getRpsOrgNo(), this.getSubject(),this.getSeqNo()));        
			detail.setInAccoutOrg(merInfo.getRpsOrgNo());      
		}

		//金额转换，分转换成元
		detail.setTranAmount(detail.getTranAmount().divide(ONE_HUNDRED));
//		bthSetCapitalDetailDao.insertSelective(detail);
		bthSetCapitalDetails.add(detail);
	}
	
	/**
	 * 银联品牌费服务费
	 * @param settleInfo
	 * @param merInfo
	 * @param merSettleInfo
	 * @param subSidyAmt ： 品牌服务费
	 */
//	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
	public void payBrandFee(BthMerInAccDtl settleInfo, MchtBaseInfo merInfo, MchtContInfo merSettleInfo,BigDecimal subSidyAmt, List<BthSetCapitalDetail> bthSetCapitalDetails)
	{
		BthSetCapitalDetail detail = this.initCapitalDetail(settleInfo, merInfo, "01");
		detail.setEntryType(Constans.ENTRY_TYPE_FEE_PAY_EBANK); // 12-手续费支出（电子银行补贴手续费支出）
		
		//消费
		if (Constans.ORDER_TYPE_CONSUME.equals(settleInfo.getOrderType()))
		{
			/***
			 * 消费订单：
			 *   借：电子银行手续费补贴支出53110023    品牌服务费
			 *   贷：银联机构内部户                                品牌服务费
			 * 
			 * **/

			detail.setTranAmount(subSidyAmt);
			//转出账户:手续费补贴支出账户
			detail.setOutAccountNo(AccountUtil.genInnerAccount(merInfo.getRpsOrgNo(), this.getSubsidySubject()));        
			detail.setOutAccoutOrg(merInfo.getRpsOrgNo());      

			//转入账户:银联清算账户  
			detail.setInAccountNo(AccountUtil.genInnerAccountWithSeq(merInfo.getRpsOrgNo(), this.getSubject(),this.getSeqNo()));        
			detail.setInAccoutOrg(merInfo.getRpsOrgNo());  
			
			detail.setEntryType(Constans.ENTRY_TYPE_FEE_PAY_EBANK); // 12-手续费支出（电子银行补贴手续费支出）
		}
		else if (Constans.ORDER_TYPE_RETURN.equals(settleInfo.getOrderType()))
		{
			/***
			 * 退款订单：
			 *   借：电子银行手续费补贴支出53110023    品牌服务费 (红字)
			 *   贷：银联机构内部户                                品牌服务费 (红字)
			 * 
			 * **/
			
			subSidyAmt = subSidyAmt.multiply(MINUS_ONE);             //红字
			detail.setTranAmount(subSidyAmt);
			detail.setEntryType(Constans.ENTRY_TYPE_FEE_PAY_EBANK);  // 12-手续费支出（电子银行补贴手续费支出）
			
			//转出账户:手续费补贴支出账户
			detail.setOutAccountNo(AccountUtil.genInnerAccount(merInfo.getRpsOrgNo(), this.getSubsidySubject()));        
			detail.setOutAccoutOrg(merInfo.getRpsOrgNo());      

			//转入账户:银联清算账户  
			detail.setInAccountNo(AccountUtil.genInnerAccountWithSeq(merInfo.getRpsOrgNo(), this.getSubject(),this.getSeqNo()));        
			detail.setInAccoutOrg(merInfo.getRpsOrgNo());  
		}

		//金额转换，分转换成元
		detail.setTranAmount(detail.getTranAmount().divide(ONE_HUNDRED));
				
//		bthSetCapitalDetailDao.insertSelective(detail);
		bthSetCapitalDetails.add(detail);
	}
	
	
	
	/**
	 * 初始化本金资金明细
	 * 
	 * @param cur
	 * @return
	 */
	private BthSetCapitalDetail initCapitalDetail(BthMerInAccDtl settleInfo, MchtBaseInfo mgtMerInfo, String cur)
	{
		Date d = new Date();
		String curDate = DateUtil.format(d, "yyyyMMdd");
		String merId = mgtMerInfo.getMchtId();

		BthSetCapitalDetail detail = new BthSetCapitalDetail();
		detail.setId(UUIDCreator.randomUUID().toString());
		detail.setCleaTime(curDate); // 清算日期
		detail.setOrderId(settleInfo.getTxnSeqId());    //订单流水号(扫码为订单号，线上未子订单号) 
		detail.setMerId(mgtMerInfo.getMchtId());
		detail.setMerName(mgtMerInfo.getMchtName()); // 商户名称
		detail.setAccountType(Constans.SETTL_ACCT_TYPE_PLAT);
		detail.setTranAmount(ZERO);

		detail.setTransCur(cur); // 交易币种
		// 分录流水类型:01-商户入账(商户本金结算)；02-手续费分润（行内手续费进行分润）；03-手续费垫付（行内手续费垫付）04-商户手续费扣帐（商户手续费结算）；05-第三方手续费入账（第三方资金通道的手续费结算）
		detail.setEntryType(Constans.ENTRY_TYPE_MER);

		detail.setTranType(settleInfo.getOrderType());    // 交易类型:01-消费 02-退货
		detail.setFundChannel(Constans.CHANNEL_TYPE_UNIONPAY);     // 资金通道:03-银联记账

		// 处理状态:00-未处理 01-处理中 02-处理成功 03-处理失败
		detail.setDealResult(Constans.DEAL_RESULT_NOT);
		// 入账状态：00 -未入账 01 - 入账成功 02 - 入账失败
		detail.setAccountStauts(Constans.ACCOUNT_STATUS_NOT);
		detail.setCreateDate(DateUtil.format(new Date(), "yyyyMMddHHmmss")); // 创建时间

		detail.setTransCur("01"); // 交易币种
		detail.setMerOrderId(settleInfo.getTxnSeqId());

		//初始化批次号：日期加商户号
		String batchNo = curDate + (merId.length()>15?merId.substring(0,15):merId);

		return detail;
	}

	@Override
	/**
	 * 查询商户基本信息 ：
	 * @param mchtId ： 商户号
	 * @return
	 */
	public MchtBaseInfo getMerBaseInfo(String mchtId) {
		
        MchtBaseInfo merInfo ;
        if (IfspDataVerifyUtil.isNotBlank(CacheMchtBaseInfo.getCache(mchtId))){
            merInfo = CacheMchtBaseInfo.getCache(mchtId);
        }else {
            Map<String,Object> m = new HashMap<String,Object>();
            m.put("mchtId", mchtId);
            merInfo = mchtBaseInfoDao.selectOne("selectMerInfoByMchtId", m);
            if (IfspDataVerifyUtil.isNotBlank(merInfo)){
                // 加入缓存
                CacheMchtBaseInfo.addCache(mchtId,merInfo);
            }
        }

		return merInfo;
	}

	@Override
	/**
	 * 查询商户结算信息 ：
	 * @param mchtId ： 商户号
	 * @return
	 */
	public MchtContInfo getMerStlInfo(String mchtId) {
		
        MchtContInfo merStlInfo ;
        if (IfspDataVerifyUtil.isNotBlank(CacheMchtContInfo.getCache(mchtId))){
            merStlInfo = CacheMchtContInfo.getCache(mchtId);
        }else {
            Map<String,Object> m = new HashMap<String,Object>();
            m.put("mchtId", mchtId);
            merStlInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);
            if (IfspDataVerifyUtil.isNotBlank(merStlInfo)){
                // 加入缓存
                CacheMchtContInfo.addCache(mchtId,merStlInfo);
            }
        }

		return merStlInfo;
	}

	@Override
	/**
	 * 查询服务商基本信息 ：
	 * @param mchtId ： 商户号
	 * @return
	 */
	public ParternBaseInfo getServiceBaseInfo(String mchtId) {

		ParternBaseInfo serviceInfo ;
		if (IfspDataVerifyUtil.isNotBlank(CacheServiceBaseInfo.getCache(mchtId))){
			serviceInfo = CacheServiceBaseInfo.getCache(mchtId);
		}else {
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("mchtNo", mchtId);
			serviceInfo = parternBaseInfoDao.selectOne("selectServiceByMchtId", m);
			if (IfspDataVerifyUtil.isNotBlank(serviceInfo)){
				// 加入缓存
				CacheServiceBaseInfo.addCache(mchtId,serviceInfo);
			}
		}

		return serviceInfo;

	}

	@Override
	/**
	 * 查询商户机构信息（收单机构、运营机构）
	 * @param mchtId ：商户号
	 * @return
	 */
	public List<MchtOrgRel> getMerOrgInfo(String mchtId) 
	{
        List<MchtOrgRel> orgList;
        if (IfspDataVerifyUtil.isNotEmptyList(CacheMchtOrgRel.getCache(mchtId))){
            orgList = CacheMchtOrgRel.getCache(mchtId);
        }else {
            Map<String,Object> m = new HashMap<String,Object>();
            m.put("mchtId", mchtId);
            orgList   =  mchtOrgRelDao.selectList("selectMchtOrgRelByMerId", m);
            if (IfspDataVerifyUtil.isNotEmptyList(orgList)){
                // 加入缓存
                CacheMchtOrgRel.addCache(mchtId,orgList);
            }
        }

		return orgList;
	}
	
	/**
	 * 查询合作方信息(物流)
	 * @param partnerCode
	 * @return
	 */
	public ParternBaseInfo getPartnerInfo(String partnerCode)
	{
        List<ParternBaseInfo> partnerInfoList ;
        if (IfspDataVerifyUtil.isNotEmptyList(CacheParternBaseInfo.getCache(partnerCode))){
            partnerInfoList = CacheParternBaseInfo.getCache(partnerCode);
        }else {
            Map<String,Object> m = new HashMap<String,Object>();
            m.put("partnerCode", partnerCode);
            partnerInfoList = parternBaseInfoDao.selectList("selectPartnerInfoByCode", m);
            if (IfspDataVerifyUtil.isNotEmptyList(partnerInfoList)){
                // 加入缓存
                CacheParternBaseInfo.addCache(partnerCode,partnerInfoList);
            }
        }

		if(partnerInfoList.size() < 1)
		{
			//TODO:查询不到合作方信息抛异常？
		}
		return partnerInfoList.get(0);
	}

	@Override
	/**
	 * 查询渠道分润信息
	 * @param channelNo
	 * @return
	 */
	public MchtGainsInfo getMerGainsInfo(String channelNo) {
		MchtGainsInfo gainsInfo = null;
        List<MchtGainsInfo> gainsInfoList ;
        if (IfspDataVerifyUtil.isNotEmptyList(CacheMchtGainsInfo.getCache(channelNo))){
            gainsInfoList = CacheMchtGainsInfo.getCache(channelNo);
        }else {
            Map<String,Object> parameter = new HashMap<String,Object>();
            parameter.put("accChnlNo", channelNo);
            gainsInfoList = mchtGainsInfoDao.selectList("selectGainsInfoByChnl", parameter);
            if (IfspDataVerifyUtil.isNotEmptyList(gainsInfoList)){
                // 加入缓存
                CacheMchtGainsInfo.addCache(channelNo, gainsInfoList);
            }
        }
		if(gainsInfoList.size()>0)
		{
			gainsInfo = gainsInfoList.get(0);
		}
		return gainsInfo;
	}

	@Override
	/**
	 * 查询服务商渠道分润信息
	 * @param channelNo
	 * @return
	 */
	public MchtGainsInfo getServiceInfo(String parternId,String channelNo) {
		MchtGainsInfo gainsInfo = null;
		List<MchtGainsInfo> gainsInfoList ;
		if (IfspDataVerifyUtil.isNotEmptyList(CacheServiceGainsInfo.getCache(parternId,channelNo))){
			gainsInfoList = CacheServiceGainsInfo.getCache(parternId,channelNo);
		}else {
			Map<String,Object> parameter = new HashMap<String,Object>();
			parameter.put("accChnlNo", channelNo);
			parameter.put("parternId",parternId);
			gainsInfoList = mchtGainsInfoDao.selectList("selectServiceInfoByChnl", parameter);
			if (IfspDataVerifyUtil.isNotEmptyList(gainsInfoList)){
				// 加入缓存
				CacheServiceGainsInfo.addCache(parternId,channelNo, gainsInfoList);
			}
		}

		if(gainsInfoList.size()>0)
		{
			gainsInfo = gainsInfoList.get(0);
		}
		return gainsInfo;
	}

	public int updateOrderStlStatus(BthMerInAccDtl inAccDtl)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("txnSeqId", inAccDtl.getTxnSeqId());
		params.put("stlStatus", Constans.SETTLE_STATUS_CLEARING);    //01-清分中
		return bthMerInAccDtlDao.update("updateOrderStlStatusByTxnSeqId", params);
	}

	public String getSubsidySubject() {
		return subsidySubject;
	}

	public void setSubsidySubject(String subsidySubject) {
		this.subsidySubject = subsidySubject;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	/**
	 * 批量修改入账明细表状态
	 */
	@Override
	public int updateStatus(String pagyNo,String stlmDate)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stlStatusNew", Constans.SETTLE_STATUS_CLEARING);    //01-清分中
		params.put("stlStatusOld", Constans.SETTLE_STATUS_NOT_CLEARING);    //00-初始化状态
		params.put("stlStatusOldOL", Constans.SETTLE_STATUS_SUCCESS_NOTCLEARING);    //04-结算成功手续费未清分
		params.put("pagyNo", pagyNo);
		params.put("stlmDate", stlmDate);

		return bthMerInAccDtlDao.update("updateOrderStlStatusBatch", params);
	}

	/**
	 * 将清分明细临时表中的数据插入正式表
	 */
	@Override
	public int insertFromTempTable(String pagyNo)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pagyNo", pagyNo);
		return bthSetCapitalDetailDao.update("insertFromTempTable", params);
	}

	/**
	 * 暂不使用
	 */
	@Override
	public void init(String pagyNo,String stlmDate)
	{
//		bthMerInAccDtlDao.delete("clearIdRecord",null);
		Map<String,Object> params=new HashMap<>();
		params.put("pagyNo", pagyNo);
		params.put("stlmDate", stlmDate);
		bthMerInAccDtlDao.delete("clearBthMerInAccDtlTemp",params);

		bthMerInAccDtlDao.insert("initBthMerInAccDtlTemp",params);
//		bthSetCapitalDetailDao.delete("clearCapitalDetailTemp",params);
	}
	/**
	 * 批量初始化商户基本信息数据到缓存
	 *
	 * @param mchtIdList
	 * @return
	 */
	private void initMerInfo(List<String> mchtIdList)
	{
		for (Iterator<String> it = mchtIdList.iterator(); it.hasNext(); )
		{
			//将缓存中已存在的商户id从mchtIdList中移出
			if (IfspDataVerifyUtil.isNotBlank(CacheMchtBaseInfo.getCache(it.next())))
			{
				it.remove();
			}
		}
		if (mchtIdList != null && mchtIdList.size() > 0)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("mchtIdList", mchtIdList);
			List<MchtBaseInfo> mchtBaseInfoList = mchtBaseInfoDao.selectList("selectMerInfoByMchtIdList", m);
			for (Iterator<MchtBaseInfo> it = mchtBaseInfoList.iterator(); it.hasNext(); )
			{
				MchtBaseInfo mchtBaseInfo = it.next();
				if (mchtBaseInfo != null)
				{
					CacheMchtBaseInfo.addCache(mchtBaseInfo.getMchtId(), mchtBaseInfo);
				}
				it.remove();
			}
		}
	}

	/**
	 * 批量初始化服务商基本信息数据到缓存
	 *
	 * @param mchtIdList
	 * @return
	 */
	private void initServiceInfo(List<String> mchtIdList)
	{
		for (Iterator<String> it = mchtIdList.iterator(); it.hasNext(); )
		{
			//将缓存中已存在的商户id从mchtIdList中移出
			if (IfspDataVerifyUtil.isNotBlank(CacheServiceBaseInfo.getCache(it.next())))
			{
				it.remove();
			}
		}
		if (mchtIdList != null && mchtIdList.size() > 0)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("mchtIdList", mchtIdList);
			List<ParternBaseInfo> mchtSerInfoList = parternBaseInfoDao.selectList("selectPartnerInfoIdList", m);
			for (Iterator<ParternBaseInfo> it = mchtSerInfoList.iterator(); it.hasNext(); )
			{
				ParternBaseInfo serviceBaseInfo = it.next();
				if (serviceBaseInfo != null)
				{
					CacheServiceBaseInfo.addCache(serviceBaseInfo.getMchtNo(), serviceBaseInfo);
				}
				it.remove();
			}
		}
	}

	/**
	 * 查询商户机构信息（收单机构、运营机构）批量初始化
	 *
	 * @param mchtIdList
	 * @return
	 */
	private void initMerOrgInfo(List<String> mchtIdList)
	{
		for (Iterator<String> it = mchtIdList.iterator(); it.hasNext(); )
		{
			//将缓存中已存在的商户id从mchtIdList中移出
			if (IfspDataVerifyUtil.isNotEmptyList(CacheMchtOrgRel.getCache(it.next())))
			{
				it.remove();
			}
		}
		if (mchtIdList != null && mchtIdList.size() > 0)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("mchtIdList", mchtIdList);
//            List<MchtContInfo> mchtContInfoList= mchtContInfoDao.selectList("selectMerInfoByMchtIdList", m);
			List<MchtOrgRel> orgList = mchtOrgRelDao.selectList("selectMchtOrgRelByMerIdList", m);
			Map<String, List<MchtOrgRel>> orgListMap = orgList.stream().collect(
					Collectors.groupingBy(MchtOrgRel::getMchtNo));

			CacheMchtOrgRel.addAllCache(orgListMap);
			orgListMap.clear();
			orgList.clear();
		}
	}

	/**
	 * 批量初始化商户号查询商户结算信息到缓存
	 *
	 * @param mchtIdList
	 * @return
	 */
	private void initMerStlInfo(List<String> mchtIdList)
	{
		for (Iterator<String> it = mchtIdList.iterator(); it.hasNext(); )
		{
			//将缓存中已存在的商户id从mchtIdList中移出
			if (IfspDataVerifyUtil.isNotBlank(CacheMchtContInfo.getCache(it.next())))
			{
				it.remove();
			}
		}
		if (mchtIdList != null && mchtIdList.size() > 0)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("mchtIdList", mchtIdList);
			List<MchtContInfo> mchtContInfoList = mchtContInfoDao.selectList("selectMerStlInfoByMchtIdList", m);
			for (Iterator<MchtContInfo> it = mchtContInfoList.iterator(); it.hasNext(); )
			{
				MchtContInfo mchtContInfo = it.next();
				if (mchtContInfo != null)
				{
					CacheMchtContInfo.addCache(mchtContInfo.getMchtId(), mchtContInfo);
				}
				it.remove();
			}
		}
	}

	public int updateOrderStlStatus(List<String> txnSeqIdList)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("txnSeqIdList", txnSeqIdList);
		params.put("stlStatus", Constans.SETTLE_STATUS_CLEARING);    //01-清分中
		return bthMerInAccDtlDao.update("updateOrderStlStatusByTxnSeqIdList", params);
	}
}
