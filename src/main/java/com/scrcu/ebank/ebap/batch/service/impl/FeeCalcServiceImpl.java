package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtSettlRateCfg;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtSettlRateCfgDao;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.FeeCalcService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class FeeCalcServiceImpl implements FeeCalcService 
{
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	@Resource
    private MchtSettlRateCfgDao mchtSettlRateCfgDao;         //商户结算费率配置表
	
	@Resource
    private PayOrderInfoDao payOrderInfoDao;           // 订单信息
    
    @Resource
    private PaySubOrderInfoDao paySubOrderInfoDao;           //子订单信息
    
    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息
    
	@Override
	public Map<String,Long> calcMerFee4Order(String orderSsn) 
	{
		return calcOrderMerFee(orderSsn);
	}

	@Override
	public Map<String,Long> calcMerFee4SubOrder(String subOrderSsn) 
	{
		return calcSubOrderMerFee(subOrderSsn);
	}

	private Map<String,Long> calcOrderMerFee(String orderSsn)
	{
		Map<String,Long> amtMap = new HashMap<String,Long>();
		
		long merFee = 0;
		long sltAmt = 0;
		long commissionFee = 0;
		//1)查询订单信息
		PayOrderInfo orderInfo = this.getOrderInfoByOrderSsn(orderSsn);
		BigDecimal payAmt = new BigDecimal(orderInfo.getPayAmt());
		BigDecimal couponAmt = new BigDecimal(0);    //营销总金额
		BigDecimal mchtIncentiveAmt = new BigDecimal(0);    //商户奖励金额
		if(orderInfo.getBankCouponAmt() != null)
		{
			couponAmt = new BigDecimal(orderInfo.getBankCouponAmt());
		}
		BigDecimal txnAmt = payAmt.add(couponAmt);
		//t+0商户奖励金20190925
		if(orderInfo.getMchtIncentiveAmt() != null)
		{
			mchtIncentiveAmt = new BigDecimal(orderInfo.getMchtIncentiveAmt());
			txnAmt = txnAmt.add(mchtIncentiveAmt);
		}
		
		String chnlNo = "";
		if(Constans.TXN_TYPE_O1000002.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_O1000003.equals(orderInfo.getTxnTypeNo()))
		{
			//主扫交易/微信公众号() 取CHL_NO
			chnlNo = orderInfo.getChlNo();
		}
		else
		{
			//被扫支付
			chnlNo = orderInfo.getAcptChlNo();     //如果是退款交易，渠道也取acptChlNo
		}
		
		//2)计算商户手续费
		if(Constans.TXN_TYPE_ONLINE_REFUND.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_REFUND.equals(orderInfo.getTxnTypeNo()))
		{
			//计算退款手续费
			merFee = calcMerFee4Order4Return(orderInfo,null).longValue();
			//计算退款佣金
			commissionFee = this.calcCommissionAmt4Return(orderInfo,null).longValue();
		}
		else
		{
			//计算支付手续费
			merFee = calcMerFee4Order(txnAmt,orderInfo.getMchtId(),orderInfo,chnlNo).longValue();
			//计算支付佣金
			commissionFee = this.calcCommissionAmt(txnAmt,orderInfo.getMchtId()).longValue();
		}
		
		sltAmt = txnAmt.longValue() - merFee - commissionFee;
		
		amtMap.put("stlAmt", sltAmt);
		amtMap.put("merFee", merFee);
		amtMap.put("commissionFee", commissionFee);
		
		return amtMap;
	}
	
	
	private Map<String,Long> calcSubOrderMerFee(String subOrderSsn)
	{
		Map<String,Long> amtMap = new HashMap<String,Long>();
		
		long merFee = 0;
		long sltAmt = 0;
		long commissionFee = 0;
		//1)查询订单信息
		PayOrderInfo orderInfo = this.getOrderInfoBySubOrderSsn(subOrderSsn);
		//2)查询子订单信息
		PaySubOrderInfo subOrderInfo = this.getSubOrderInfoBySubOrderSsn(subOrderSsn);
		
		
		BigDecimal payAmt = new BigDecimal(subOrderInfo.getPayAmount());
		BigDecimal couponAmt = new BigDecimal(0);   //营销活动
		if(subOrderInfo.getBankCouponAmt() != null && !"".equals(subOrderInfo.getBankCouponAmt()))
		{
			couponAmt = new BigDecimal(subOrderInfo.getBankCouponAmt());
		}
		BigDecimal txnAmt = payAmt.add(couponAmt).add(this.getMarketingAmt(subOrderInfo));
		
		//2)计算商户手续费
		if(Constans.TXN_TYPE_ONLINE_REFUND.equals(orderInfo.getTxnTypeNo()) || Constans.TXN_TYPE_REFUND.equals(orderInfo.getTxnTypeNo()))
		{
			//计算退款手续费
			merFee = calcMerFee4Order4Return(orderInfo,subOrderInfo).longValue();
			//计算退款佣金
			commissionFee = this.calcCommissionAmt4Return(orderInfo,subOrderInfo).longValue();
		}
		else
		{
			//计算支付手续费
			merFee = this.calMerFee4SubOrder(orderInfo,subOrderInfo).longValue();
			//计算支付佣金
			commissionFee = this.calcCommissionAmt(txnAmt,orderInfo.getMchtId()).longValue();
		}
		
		sltAmt = txnAmt.longValue() - merFee - commissionFee;
		
		amtMap.put("stlAmt", sltAmt);
		amtMap.put("merFee", merFee);
		amtMap.put("commissionFee", commissionFee);
		
		return amtMap;
	}
	/**
	 * 根据交易金额计算行内手续费
	 * @param txnAmt ： 交易金额
	 * @param merId ： 商户ID，查询商户费率
	 * @return
	 */
	private BigDecimal calcMerFee4Order(BigDecimal txnAmt,String merId, PayOrderInfo orderInfo,String chnlNo)
	{
		BigDecimal merFee = new BigDecimal(0);
		
		String accType = orderInfo.getAcctSubTypeId();
		/*if(accType == null )          //内管手续费配置规则修改 ： 20181105
		{
			accType = "*";
		}*/
		MchtSettlRateCfg stlRateInfo = this.getMerStlRateInfo(chnlNo, merId, accType);
		if(stlRateInfo == null)
		{
			//没配置费率信息,不收手续费
			return merFee;
		}
		
		String rateCalcType = stlRateInfo.getRateCalType();
		if(Constans.COMM_TYPE_FIX_AMT.equals(rateCalcType))
		{
			//按固定金额返佣
			if(stlRateInfo.getRateCalParam()!=null)
			{
				merFee = stlRateInfo.getRateCalParam().multiply(ONE_HUNDRED);   //参数单位为元
                if (txnAmt.compareTo(merFee) < 0){
                    log.info("商户["+merId+"]行内手续费["+merFee+"]大于支付金额["+txnAmt+"] , 手续费金额最多只收取订单金额, 即为 ["+txnAmt+"]分 .");
                    merFee = txnAmt;
                }
			}
		}
		else if(Constans.COMM_TYPE_BY_RATE.equals(rateCalcType))
		{
			//按比例返佣
			if(stlRateInfo.getRateCalParam()!=null)
			{
				merFee = stlRateInfo.getRateCalParam().multiply(txnAmt).divide(ONE_HUNDRED);;
				if(stlRateInfo.getMaxParam() != null)
				{
					//大于最大手续费
					if(merFee.compareTo(stlRateInfo.getMaxParam().multiply(ONE_HUNDRED)) == 1)
					{
						merFee = stlRateInfo.getMaxParam().multiply(ONE_HUNDRED);
					}
				}
				
				if(stlRateInfo.getMinParam() != null)
				{
					//小于最小手续费
					if(merFee.compareTo(stlRateInfo.getMinParam().multiply(ONE_HUNDRED)) == -1)
					{
						merFee = stlRateInfo.getMinParam().multiply(ONE_HUNDRED);
                        if (txnAmt.compareTo(merFee) < 0){
                            log.info("商户["+merId+"]行内手续费["+merFee+"]大于支付金额["+txnAmt+"] , 手续费金额最多只收取订单金额, 即为 ["+txnAmt+"]分 .");
                            merFee = txnAmt;
                        }
					}
				}
						
			}
		}
			
	
		//四舍五入，取整
		merFee = merFee.setScale(0, BigDecimal.ROUND_HALF_UP);
		
		return merFee;
	}
	
	/**
	 * 计算子订单的行内手续费(支付金额+红包金额+物流金额)*行内扣率
	 * @param subOrder
	 */
	private BigDecimal calMerFee4SubOrder(PayOrderInfo orderInfo,PaySubOrderInfo subOrder)
	{
		BigDecimal merFee = new BigDecimal(0);
		//支付金额
		BigDecimal payAmt = new BigDecimal(subOrder.getPayAmount());
		//红包金额
		BigDecimal bhAmt = this.getHbAmt(subOrder);
		
		//积分金额
		//BigDecimal pointAmt = this.getPointAmt(subOrder);   //BankCouponAmt包括了积分金额
		
		//BigDecimal logisFee = this.getLogisAmt(subOrder);
		//总金额
		BigDecimal txnAmt = payAmt.add(bhAmt);           // payAmt.add(bhAmt).add(logisFee);
		
		merFee = this.calcMerFee4Order(txnAmt, subOrder.getSubMchtId(), orderInfo,subOrder.getFundChannel());
		
		return merFee;
	}
	
	/**
	 * 根据红包Json信息计算红包金额(还是直接从保存的红包金额总费用总获取)
	 * @param subOrder
	 * @return
	 */
	private BigDecimal getHbAmt(PaySubOrderInfo subOrder)
	{
		BigDecimal hbAmt = new BigDecimal(0);
		if(subOrder.getBankCouponAmt() != null)
		{
			hbAmt = new BigDecimal(subOrder.getBankCouponAmt());
		}
		return hbAmt;
	}
	
	/**
	 * 根据交易金额计算返佣费用
	 * @param txnAmt ： 交易金额
	 * @param mchtId ： 商户ID
	 * @return
	 */
	private BigDecimal calcCommissionAmt(BigDecimal txnAmt,String mchtId)
	{
		BigDecimal commissionAmt = new BigDecimal(0);
		if(mchtId == null || mchtId == "")
		{
			return commissionAmt;
		}
		//查询商户结算信息
		MchtContInfo merStlInfo = this.getMerStlInfo(mchtId);
		if(merStlInfo != null)
		{
			String commType = merStlInfo.getCommType();
			if(Constans.COMM_TYPE_NONE.equals(commType))
			{
				//无返佣
				return commissionAmt;
			}
			else if(Constans.COMM_TYPE_FIX_AMT.equals(commType))
			{
				//按固定金额返佣
				if(merStlInfo.getCommParam()!=null)
				{
					commissionAmt = merStlInfo.getCommParam().multiply(ONE_HUNDRED);   //按固定金额收取佣金是，参数以元为单位
				}
			}
			else if(Constans.COMM_TYPE_BY_RATE.equals(commType))
			{
				//按比例返佣
				if(merStlInfo.getCommParam()!=null)
				{
					commissionAmt = merStlInfo.getCommParam().multiply(txnAmt).divide(ONE_HUNDRED);   //按百分比收取佣金是，参数为百分比
				}
			}
			
		}
	
		//四舍五入，取整
		commissionAmt = commissionAmt.setScale(0, BigDecimal.ROUND_HALF_UP);
		
		return commissionAmt;
	}
	
	private BigDecimal calcCommissionAmt4Return(PayOrderInfo orderInfo,PaySubOrderInfo subOrderInfo)
	{
		//实时结算商户退款处理与非实时退款相同，按顺序从商户退款账户扣除全部支付金额（payAmt+couponAmt）,不扣除佣金
		BigDecimal retCommFee = new BigDecimal(0);
		
		retCommFee = retCommFee.setScale(0, BigDecimal.ROUND_HALF_UP);
		return retCommFee;
	}
	
	private BigDecimal calcMerFee4Order4Return(PayOrderInfo orderInfo,PaySubOrderInfo subOrderInfo)
	{
		//实时结算商户退款处理与非实时退款相同，按顺序从商户退款账户扣除全部支付金额（payAmt+couponAmt）,不扣除手续费
		BigDecimal retFee = new BigDecimal(0);
		return retFee;
	}
	
	
	/**
	 * 根据商户号、渠道号、交易账户类型查询手续费费率信息
	 * @param chnlNo
	 * @param merId
	 * @param accType
	 * @return
	 */
	private MchtSettlRateCfg getMerStlRateInfo(String chnlNo,String merId,String accType)
	{
		MchtSettlRateCfg merRateInfo= null;
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("mchtId", merId);
		m.put("accChnlNo", chnlNo);
		m.put("acctType", accType);
		List<MchtSettlRateCfg> merRateInfoList = mchtSettlRateCfgDao.selectList("selectMerStlRateInfoByChnlAndAccType", m);
		
		if(merRateInfoList.size() > 0)
		{
			merRateInfo = merRateInfoList.get(0);
		}
		
		return merRateInfo;
	}
	
	/**
	 * 根据订单号查询订单信息
	 * @param orderSsn
	 * @return
	 */
	private PayOrderInfo getOrderInfoByOrderSsn(String orderSsn)
	{
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("orderSsn", orderSsn);
		PayOrderInfo orderInfo = payOrderInfoDao.selectOne("selectOrderInfoByReqOrderSsn", parameter);
		
		return orderInfo;
	}
	
	/**
	 * 根据子单号查询订单信息
	 * @param subOrderSsn
	 * @return
	 */
	private PayOrderInfo getOrderInfoBySubOrderSsn(String subOrderSsn)
	{
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("subOrderSsn", subOrderSsn);
		PayOrderInfo orderInfo = payOrderInfoDao.selectOne("selectOrderInfoBySubOrderSsn", parameter);
		
		return orderInfo;
	}
	
	/**
	 * 根据子单号查询子订单信息
	 * @param subOrderSsn
	 * @return
	 */
	private PaySubOrderInfo getSubOrderInfoBySubOrderSsn(String subOrderSsn)
	{
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("subOrderSsn", subOrderSsn);
		PaySubOrderInfo subOrderInfo = paySubOrderInfoDao.selectOne("selectSubOrderInfoBySubOrderNo", parameter);
		
		return subOrderInfo;
	}
	
	/**
	 * 根据商户号查询商户结算信息
	 * @param mchtId
	 * @return
	 */
	private MchtContInfo getMerStlInfo(String mchtId)
	{
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("mchtId", mchtId);
		MchtContInfo merInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);
		
		return merInfo;
	}
	
	/**
	 * 获得营销金额（银行营销 + 机构营销）
	 * @param subOrder
	 * @return
	 */
	private BigDecimal getMarketingAmt(PaySubOrderInfo subOrder)
	{
		BigDecimal marketingAmt = new BigDecimal(0);
		if(StringUtils.hasText(subOrder.getBankPayAmt()))
		{
			marketingAmt = marketingAmt.add(new BigDecimal(subOrder.getBankPayAmt()));
		}
		
		if(StringUtils.hasText(subOrder.getUserorgPayAmt()))
		{
			marketingAmt = marketingAmt.add(new BigDecimal(subOrder.getUserorgPayAmt()));
		}
		
		return marketingAmt;
	}

}
