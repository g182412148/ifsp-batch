package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.vo.MerCommissionVo;
import com.scrcu.ebank.ebap.batch.dao.*;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.message.IfspStringUtil;
import com.scrcu.ebank.ebap.batch.bean.request.DailyTradeStatisticsRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MonthTradeStatisticsRequest;
import com.scrcu.ebank.ebap.batch.bean.request.QueryDailyBillRequest;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMonthBillRequest;
import com.scrcu.ebank.ebap.batch.bean.request.WeekCountQueryRequest;
import com.scrcu.ebank.ebap.batch.bean.response.DailyTradeStatisticsResponse;
import com.scrcu.ebank.ebap.batch.bean.response.MonthTradeStatisticsResponse;
import com.scrcu.ebank.ebap.batch.bean.response.QueryDailyBillResponse;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMonthBillResponse;
import com.scrcu.ebank.ebap.batch.bean.response.WeekCountQueryResponse;
import com.scrcu.ebank.ebap.batch.bean.vo.SubMchtInfoResultVo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.msg.MchtMsg;
import com.scrcu.ebank.ebap.batch.common.utils.CommonResponseUtils;
import com.scrcu.ebank.ebap.batch.service.AccountLiquidationQueryService;
import com.scrcu.ebank.ebap.batch.soaclient.MchtCenterSoaClientService;

import lombok.extern.slf4j.Slf4j;

/**
 * 名称：〈分店日统计ServiceImpl〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月12日 <br>
 * 说明：<br>
 */
@Service("accountLiquidationQueryServiceImpl")
@Slf4j
public class AccountLiquidationQueryServiceImpl implements AccountLiquidationQueryService {

	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;

	@Resource
	private BthMerInAccDao bthMerInAccDao;

	@Resource
	private MchtBaseInfoDao mchtBaseInfoDao;

	@Resource
	private MchtCenterSoaClientService mchtCenterSoaClientService;
	
	@Resource
	private BthSetCapitalDetailDao bthSetCapitalDetailDao;
	
	@Resource
	private BthMerDailyTxnCountDao bthMerDailyTxnCountDao;
	@Resource
	private MchtContInfoDao mchtContInfoDao;           //商户合同信息

	@Resource
	private PayOrderInfoDao payOrderInfoDao; //订单信息

	@Override
	/**
	 * 日账单查询
	 */
	public QueryDailyBillResponse queryDailyBill(QueryDailyBillRequest request) {
		QueryDailyBillResponse queryDailyBillResponse = new QueryDailyBillResponse();

		// 初始化返回报文
		CommonResponseUtils.setCommonResp(request, queryDailyBillResponse);

		log.info("[AccountLiquidationQuery-QueryDailyBill]--商户号:" + request.getMchtId() + "-----日期:"
				+ request.getQryDate());
		// 校验商户号与日期
        if (validateReqMsg(request, queryDailyBillResponse))
        {
            return queryDailyBillResponse;
        }


        //------------------------------------------------获取商户规则-------------------------------------------------
        // 查分店 没有下级商户 有上级商户 二级商户
        // 总店 没有上级商户 有下级商户 一级商户
        List<MchtBaseInfoVo> mchtBaseInfoVoList = mchtBaseInfoDao.queryMchtTypeByMchtId(request.getMchtId());
        // 存在上级商户 规则0有1无
        String hasParMcht = "";
        // 能扩展下级商户 规则0有1无
        String hasSunMcht = "";

        for (int i = 0; i < mchtBaseInfoVoList.size(); i++) {
            MchtBaseInfoVo mchtBaseInfoVo = mchtBaseInfoVoList.get(i);
            if ("0003".equals(mchtBaseInfoVo.getRuleNo())) {
                hasParMcht = mchtBaseInfoVo.getRuleValue();
            }
            if ("0005".equals(mchtBaseInfoVo.getRuleNo())) {
                hasSunMcht = mchtBaseInfoVo.getRuleValue();
            }
        }
        //-------------------------------------------------------------------------------------------------------------

        log.info("===========================>>根据商户号[{}]与入账日期[{}]查询当日商户交易情况",request.getMchtId(),request.getQryDate());
        List<String> inAccStats = bthMerInAccDtlDao.queryInAccStatByMchtAndInAcctDate(request.getMchtId(),
                request.getQryDate());

        if (inAccStats == null || inAccStats.size() == 0) {
            // 如果该商户是总店 , 且分店有交易需要给上级返佣 , 应该体现返佣金额
            if (("1".equals(hasParMcht) && "0".equals(hasSunMcht)) ) {
                BigDecimal amt = new BigDecimal(0);
                List<BthMerInAccDtlVo> merInAccDtlVoList =bthMerInAccDtlDao.queryGSCommissionAmt(request.getMchtId(),request.getQryDate());
                if (IfspDataVerifyUtil.isNotEmptyList(merInAccDtlVoList)){
                    log.info("===========================>>商户号[{}]入账日期[{}]无交易 ,但存在分店返佣情况",request.getMchtId(),request.getQryDate());

                    for (int i = 0; i < merInAccDtlVoList.size(); i++) {
                        BthMerInAccDtlVo bthMerInAccDtlVo = merInAccDtlVoList.get(i);
                        if (Constans.ORDER_TYPE_CONSUME.equals(bthMerInAccDtlVo.getOrderType())){
                            amt=amt.add(new BigDecimal(bthMerInAccDtlVo.getCommissionAmt()));
                        }else {
                            amt=amt.subtract(new BigDecimal(bthMerInAccDtlVo.getCommissionAmt()));
                        }
                    }

                    queryDailyBillResponse.setTwoMchtOutCommissionAmt(amt);
                    queryDailyBillResponse.setTotalInAcctAmt(amt);
                    queryDailyBillResponse.setInAcctStat(Constans.IN_ACC_STAT_SUCC);
                }
            }

            queryDailyBillResponse.setRespMsg("[AccountLiquidationQuery-QueryDailyBill]--商户当天没有交易");
            queryDailyBillResponse.setRespMsg(RespConstans.RESP_MCHT_YESTERDAY_CURRENTDATEONEYEAR.getDesc());
            queryDailyBillResponse.setRespCode(RespConstans.RESP_MCHT_YESTERDAY_CURRENTDATEONEYEAR.getCode());
            return queryDailyBillResponse;
        }

		//各种总数
		List<BthMerInAccDtlVo> bthMerInAccDtlVoCountList=bthMerInAccDtlDao.queryAccCount(request.getMchtId(),request.getQryDate());
		
		BigDecimal totalRefundAmt=new BigDecimal(0);//退款金额
		BigDecimal totalTxnAmt=new BigDecimal(0);//交易总金额  =交易总金额-退款金额
		BigDecimal totalDiscountAmt=new BigDecimal(0);//营销补贴金额（商户出资）
		BigDecimal totalReceiptFeeAmt=new BigDecimal(0);//收款手续费金额 
		BigDecimal totalReturnFeeAmt=new BigDecimal(0);//退货手续费金额 
		BigDecimal totalInAcctAmt=new BigDecimal(0);//入账金额
		String incctime="";//入账时间
		String inAcctStat="0";//入账状态
		
		 BigDecimal twoMchtOutCommissionAmt=new BigDecimal(0);//二级商户／分店反佣金额
		 BigDecimal twoMchtInTxnAmt=new BigDecimal(0);//二级商户／分店交易入账至一级商户金额
		 BigDecimal OneToTwoInAcctAmt=new BigDecimal(0);//一级商户向二级商户／分店入账金额
		 BigDecimal platMchtOutCommissionAmt=new BigDecimal(0);//向平台商户反佣金额
		 BigDecimal oneMchtOutCommissionAmt=new BigDecimal(0);//向一级商户反佣金额
		 BigDecimal oneMchtInTxnAmt=new BigDecimal(0);//向一级商户入账金额
		if(bthMerInAccDtlVoCountList!=null &&bthMerInAccDtlVoCountList.size()>0){
			
			//订单类型  01:支付   02:退款
			for (int i = 0; i < bthMerInAccDtlVoCountList.size(); i++) {
				BthMerInAccDtlVo bthMerInAccDtlVo = bthMerInAccDtlVoCountList.get(i);
				
				if(Constans.ORDER_TYPE_CONSUME.equals(bthMerInAccDtlVo.getOrderType())){
					totalTxnAmt=totalTxnAmt.add(new BigDecimal(bthMerInAccDtlVo.getTxnAmt()));
					totalReceiptFeeAmt=new BigDecimal(bthMerInAccDtlVo.getSetlFeeAmt());
					totalDiscountAmt=new BigDecimal(bthMerInAccDtlVo.getMchtCouponAmt());
					incctime=bthMerInAccDtlVo.getInAcctDate();	
					oneMchtOutCommissionAmt=oneMchtOutCommissionAmt.add(new BigDecimal(bthMerInAccDtlVo.getCommissionAmt()));
				}else{
					totalRefundAmt=new BigDecimal(bthMerInAccDtlVo.getTxnAmt());
					totalReturnFeeAmt=new BigDecimal(bthMerInAccDtlVo.getSetlFeeAmt());
					if(IfspStringUtil.isBlank(incctime)){
						incctime=bthMerInAccDtlVo.getInAcctDate();
					}
					oneMchtOutCommissionAmt=oneMchtOutCommissionAmt.subtract(new BigDecimal(bthMerInAccDtlVo.getCommissionAmt()));
				}
			}
		}


		//如果是T+0商户，不管状态都显示金额
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("mchtId", request.getMchtId());
		MchtContInfo merStlInfo = mchtContInfoDao.selectOne("selectMerStlInfoByMchtId", m);
		boolean t0 = Constans.STL_TYPE_DN.equals(merStlInfo.getSettlCycleType())&&(merStlInfo.getSettlCycleParam() == 0);

		//如果入账状态存在待入账,不显示入账金额
		if (!inAccStats.contains(Constans.IN_ACC_STAT_PRE)||t0){
			log.info("当前入账日期不存在状态为待入账的订单.");
			if(t0){
				String inAccAmt = bthMerInAccDtlDao.countInAccAmtT0(request.getMchtId(), request.getQryDate());
				totalInAcctAmt = new BigDecimal(inAccAmt);
			}else {
				// 统计入账日期下入账金额
				String inAccAmt = bthMerInAccDtlDao.countInAccAmt(request.getMchtId(), request.getQryDate());
				totalInAcctAmt = new BigDecimal(inAccAmt);
			}
			// 成功，部分成功，失败
			if (inAccStats.contains(Constans.IN_ACC_STAT_FAIL)&&inAccStats.contains(Constans.IN_ACC_STAT_SUCC)){
				inAcctStat=Constans.IN_ACC_STAT_SCTIONSUCC;
			}else if(!inAccStats.contains(Constans.IN_ACC_STAT_FAIL)&&inAccStats.contains(Constans.IN_ACC_STAT_SUCC)) {
				inAcctStat=Constans.IN_ACC_STAT_SUCC;
			}else{
				inAcctStat=Constans.IN_ACC_STAT_FAIL;
			}
		}



		// 当为总店时 , 入账金额需加上分店向总店返佣的金额  , twoMchtOutCommissionAmt 存返佣的具体金额 ,如果为负说明分店退款交易需要总店返还原支付的返佣金额 )
		if (("1".equals(hasParMcht) && "0".equals(hasSunMcht)) ) {
            log.info("总店");
            // ----------------------------------------------- 入账金额需要加上 分店的返佣金额 ------------------------------------------------
            // 汇总今日总店旗下分店返佣金额
            List<BthMerInAccDtlVo> merInAccDtlVoList =bthMerInAccDtlDao.queryGSCommissionAmt(request.getMchtId(),request.getQryDate());
            for (int i = 0; i < merInAccDtlVoList.size(); i++) {
                BthMerInAccDtlVo bthMerInAccDtlVo = merInAccDtlVoList.get(i);
                if (Constans.ORDER_TYPE_CONSUME.equals(bthMerInAccDtlVo.getOrderType())){
                    twoMchtOutCommissionAmt=twoMchtOutCommissionAmt.add(new BigDecimal(bthMerInAccDtlVo.getCommissionAmt()));
                }else {
                    twoMchtOutCommissionAmt=twoMchtOutCommissionAmt.subtract(new BigDecimal(bthMerInAccDtlVo.getCommissionAmt()));
                }

            }
            // 入账金额加上下级返佣的金额
            totalInAcctAmt=totalInAcctAmt.add(twoMchtOutCommissionAmt);
            // ------------------------------------------------------------------------------------------------------------------------------

            //二级商户／分店返佣金额
            queryDailyBillResponse.setTwoMchtOutCommissionAmt(twoMchtOutCommissionAmt);
            //二级商户／分店交易入账至一级商户金额(当前没有这种场景,默认为0)
            queryDailyBillResponse.setTwoMchtInTxnAmt(twoMchtInTxnAmt);
            //一级商户向二级商户／分店入账金额(当前没有这种场景,默认为0)
            queryDailyBillResponse.setOneToTwoInAcctAmt(OneToTwoInAcctAmt);
            //向平台商户返佣金额(当前没有这种场景,默认为0)
            queryDailyBillResponse.setPlatMchtOutCommissionAmt(platMchtOutCommissionAmt);

		}else if ("0".equals(hasParMcht) && "1".equals(hasSunMcht)){

            log.info("分店");
            //向一级商户返佣金额
            queryDailyBillResponse.setOneMchtOutCommissionAmt(oneMchtOutCommissionAmt);
            //向一级商户入账金额 (当前没有这种场景,默认为0)
            queryDailyBillResponse.setOneMchtInTxnAmt(oneMchtInTxnAmt);
        }
		// 普通商户
		else if ("1".equals(hasParMcht) && "1".equals(hasSunMcht)) {
			log.info("普通商户");
		} else {
			queryDailyBillResponse.setRespMsg("[AccountLiquidationQuery-QueryDailyBill]--未知类型商户");
			queryDailyBillResponse.setRespMsg("未知类型商户");
			queryDailyBillResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			return queryDailyBillResponse;
		}

		int count = bthMerInAccDtlDao.queryCount(request.getMchtId(), request.getQryDate());
        // 总条数
		queryDailyBillResponse.setTotalSize(count);
        //交易总笔数
		queryDailyBillResponse.setTotalTxnCnt(count);

        // -----------------------------------------------分页明细数据  start------------------------------------------------------------
        int iPageNo = Integer.parseInt(request.getPageNo());
        if (iPageNo <= 0) {
            iPageNo = 1;
        }
        int iPageSize = Integer.parseInt(request.getPageSize());
        int pageSize = iPageNo * iPageSize;
        int pageNo = (iPageNo - 1) * iPageSize;

        // 明细
        List<BthMerInAccDtlVo> listByPage = bthMerInAccDtlDao.queryByMchtAndInAcctDate(request.getMchtId(),
                request.getQryDate(), pageNo, pageSize);
        // -----------------------------------------------分页明细数据  end----------------------------------------------------------------
        // --------------------------------------------组装列表部分   start--------------------------------------------------------------

		DailyBill[] dailyBillArr = new DailyBill[listByPage.size()];
		for (int i = 0; i < listByPage.size(); i++) {
			BthMerInAccDtlVo bthMerInAccDtlVo = listByPage.get(i);
			log.info("[AccountLiquidationQuery-QueryDailyBill]--商户入账明细流水号:" + bthMerInAccDtlVo.getTxnSeqId());
			DailyBill dailyBill = new DailyBill();
			dailyBill.setTxnSeqId(bthMerInAccDtlVo.getTxnSeqId()); // txnSeqId;流水号

			dailyBill.setInAcctStat(bthMerInAccDtlVo.getInAcctStat());//状态

			dailyBill.setTxnType(bthMerInAccDtlVo.getAcctTypeId() + bthMerInAccDtlVo.getTxnTypeNo()); // txnType;交易类型
			//被扫
	        if(Constans.TXN_TYPE_O1000001.equals(bthMerInAccDtlVo.getTxnTypeNo())){
	            
	            if(Constans.ORDER_CHL_NO_ALIPAY.equals(bthMerInAccDtlVo.getAcptChlNo())){
	            	dailyBill.setTxnType("支付宝被扫支付");
	            }else if(Constans.ORDER_CHL_NO_WECHAR.equals(bthMerInAccDtlVo.getAcptChlNo())){
	            	dailyBill.setTxnType("微信被扫支付");
	            }else if(Constans.ORDER_CHL_NO_SX_E.equals(bthMerInAccDtlVo.getAcptChlNo())){
	                dailyBill.setTxnType("蜀信e被扫支付");
	            } else if (Constans.ORDER_CHL_NO_UNIONPAY.equals(bthMerInAccDtlVo.getAcptChlNo())) {
	            	dailyBill.setTxnType("银联二维码被扫支付");
	            } else if (Constans.ORDER_CHL_NO_PER.equals(bthMerInAccDtlVo.getAcptChlNo())) {
					dailyBill.setTxnType("惠支付个人版被扫支付");
				} else if (Constans.ORDER_CHL_NO_MCHT.equals(bthMerInAccDtlVo.getAcptChlNo())) {
					dailyBill.setTxnType("惠支付商户版主扫支付");
				}else if (Constans.ORDER_CHL_NO_NIE.equals(bthMerInAccDtlVo.getAcptChlNo())) {
					dailyBill.setTxnType("蜀信e被扫支付");
				}
	        }
			else if(Constans.TXN_TYPE_O1000002.equals(bthMerInAccDtlVo.getTxnTypeNo())||Constans.TXN_TYPE_O1000003.equals(bthMerInAccDtlVo.getTxnTypeNo())){
	            //主扫--新蜀信e
	            if(Constans.ORDER_CHL_NO_ALIPAY.equals(bthMerInAccDtlVo.getChlNo())){
	                dailyBill.setTxnType("支付宝主扫支付");
	            }else if(Constans.ORDER_CHL_NO_WECHAR.equals(bthMerInAccDtlVo.getChlNo())){
	                dailyBill.setTxnType("微信主扫支付");
	            }else if(Constans.ORDER_CHL_NO_SX_E.equals(bthMerInAccDtlVo.getChlNo())){
	                dailyBill.setTxnType("蜀信e主扫支付");
	            }else if (Constans.ORDER_CHL_NO_UNIONPAY.equals(bthMerInAccDtlVo.getChlNo())) {
	                dailyBill.setTxnType("银联主扫支付");
	            }else if (Constans.ORDER_CHL_NO_PER.equals(bthMerInAccDtlVo.getChlNo())) {
					dailyBill.setTxnType("惠支付个人版主扫支付");
				}else if (Constans.ORDER_CHL_NO_MCHT.equals(bthMerInAccDtlVo.getChlNo())) {
					dailyBill.setTxnType("惠支付商户版主扫支付");
				}else if (Constans.ORDER_CHL_NO_NIE.equals(bthMerInAccDtlVo.getChlNo())) {
					dailyBill.setTxnType("蜀信e主扫支付");
				}

	        }else if(Constans.TXN_TYPE_REFUND.equals(bthMerInAccDtlVo.getTxnTypeNo())){
	            //退款
	        	dailyBill.setTxnType("退货");
	        }else{
	        	dailyBill.setTxnType("未知交易类型");
	        }
			dailyBill.setTxnAmt(new BigDecimal(bthMerInAccDtlVo.getOrderTxnAmt())); // txnAmt;交易金额

			dailyBill.setFeeAmt(new BigDecimal(bthMerInAccDtlVo.getSetlFeeAmt())); // feeAmt;手续费金额

            // 商户出资营销金额
            String discountAmt = IfspDataVerifyUtil.isBlank(bthMerInAccDtlVo.getMchtCouponAmt()) ? "0" : bthMerInAccDtlVo.getMchtCouponAmt();
            dailyBill.setDiscountAmt(new BigDecimal(discountAmt));
            // inAcctAmt;入账金额
			dailyBill.setInAcctAmt(new BigDecimal(bthMerInAccDtlVo.getSetlAmt()));
            //交易时间
			dailyBill.setTxnTime(bthMerInAccDtlVo.getOrderTm());

			dailyBillArr[i] = dailyBill;
		}
        // --------------------------------------------组装列表部分  end--------------------------------------------------------------

		queryDailyBillResponse.setDailyBill(dailyBillArr);
		queryDailyBillResponse.setTotalTxnAmt(totalTxnAmt.add(totalDiscountAmt));
		queryDailyBillResponse.setTotalDiscountAmt(totalDiscountAmt);
		queryDailyBillResponse.setTotalRefundAmt(totalRefundAmt);
		queryDailyBillResponse.setTotalReceiptFeeAmt(totalReceiptFeeAmt);
		queryDailyBillResponse.setTotalReturnFeeAmt(totalReturnFeeAmt);
		queryDailyBillResponse.setTotalInAcctAmt(totalInAcctAmt);
		queryDailyBillResponse.setInAcctStat(inAcctStat);
		queryDailyBillResponse.setIncctime(request.getQryDate());

        queryDailyBillResponse.setTotalSize(count);

		queryDailyBillResponse.setRespCode("0000");
		queryDailyBillResponse.setRespMsg("日账单查询成功");
		log.info("[AccountLiquidationQuery-QueryDailyBill]--返回参数:" + queryDailyBillResponse.toString());
		return queryDailyBillResponse;
	}

    /**
     * 校验日期与商户号
     * @param request
     * @param queryDailyBillResponse
     * @return
     */
    private boolean validateReqMsg(QueryDailyBillRequest request, QueryDailyBillResponse queryDailyBillResponse) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime qstartDateTime = null;
        DateTime currentDateTime1 = DateTime.now();
        DateTime currentDateTime = DateTime.parse(currentDateTime1.toString("yyyyMMdd"));
        qstartDateTime = DateTime.parse(request.getQryDate(), format);
        // 查询结束时间不能大于当前时间yyyyMMdd
        if (currentDateTime.minusDays(1).compareTo(qstartDateTime) <= 0) {
            log.info("===================================查询日期大于系统时间");
            queryDailyBillResponse.setRespMsg(RespConstans.RESP_QUERYDATE_GT_CURRENTDATE.getDesc());
            queryDailyBillResponse.setRespCode(RespConstans.RESP_QUERYDATE_GT_CURRENTDATE.getCode());
            return true;
        }
        // 查询开始时间不能小于（当前时间 - 1年）
        if (qstartDateTime.compareTo(currentDateTime.minusYears(1)) >= 0) {
            System.out.println("===================================查询日期不能小于当前时间的一年");
            queryDailyBillResponse.setRespMsg(RespConstans.RESP_QUERYDATE_LT_CURRENTDATEONEYEAR.getDesc());
            queryDailyBillResponse.setRespCode(RespConstans.RESP_QUERYDATE_LT_CURRENTDATEONEYEAR.getCode());
            return true;
        }

        if (request.getMchtId().length() > 32) {
            queryDailyBillResponse.setRespMsg("[AccountLiquidationQuery-QueryDailyBill]--商户号长度大于32位!");
            return true;
        }
        return false;
    }

    @Override
	/**
	 * 月账单查询
	 */
	public QueryMonthBillResponse queryMonthBill(QueryMonthBillRequest request) {

	    // 账单查询是否包含退款交易的标志 ,  该请求值不为空表示不包含  , 查询记录时只统计正交易
        String txnFlag = IfspDataVerifyUtil.isBlank(request.getTxnFlag())?null:request.getTxnFlag();
        QueryMonthBillResponse queryMonthBillResponse = new QueryMonthBillResponse();
		// 初始化返回报文
		CommonResponseUtils.setCommonResp(request, queryMonthBillResponse);
		log.info("[AccountLiquidationQuery-QueryDailyBill]--商户号:" + request.getMchtId() + "-----开始日期:"
				+ request.getStartDate() + "------结束日期" + request.getEndDate());
		if (request.getMchtId().length() > 32) {
			queryMonthBillResponse.setRespMsg("[AccountLiquidationQuery-QueryDailyBill]--商户号长度大于32位!");
			queryMonthBillResponse.setRespMsg(RespConstans.RESP_MCHTSIZE_TOOLONG.getDesc());
			queryMonthBillResponse.setRespCode(RespConstans.RESP_MCHTSIZE_TOOLONG.getCode());
			return queryMonthBillResponse;
		}

		List<MonthlyBill> monthlyBillList = new ArrayList<>();
		if (IfspDataVerifyUtil.isNotBlank(request.getIsStatistics())) {
			monthlyBillList = bthMerInAccDtlDao.queryByMchtAndTxnTm(request.getMchtId(),
					request.getStartDate(), request.getEndDate(), txnFlag);
            updateMonthlyBillList(request, monthlyBillList, txnFlag);

        } else {
			monthlyBillList = bthMerInAccDtlDao.queryByMchtAndMonth(request.getMchtId(),
					request.getStartDate(), request.getEndDate(), txnFlag);
            updateMonthlyBillListByInAcctDate(request, monthlyBillList, txnFlag);
        }
		if (monthlyBillList == null || monthlyBillList.size() == 0) {
			queryMonthBillResponse.setRespMsg(RespConstans.RESP_MCHT_YESTERDAY_CURRENTDATEONEYEAR.getDesc());
			queryMonthBillResponse.setRespCode(RespConstans.RESP_MCHT_YESTERDAY_CURRENTDATEONEYEAR.getCode());
			return queryMonthBillResponse;
		}

        // 给monthlyBillList按日期排序
        Collections.sort(monthlyBillList, new Comparator<MonthlyBill>() {
            @Override
            public int compare(MonthlyBill o1, MonthlyBill o2) {
                return Integer.valueOf(o1.getTxnTime()) -  Integer.valueOf(o2.getTxnTime()) ;
            }
        });

		BthMerInAccDtlVo bthMerInAccDtlVo = bthMerInAccDtlDao.queryTotalTxnAmtAndTotalTxnCountByMchtAndMonth(
				request.getMchtId(), request.getStartDate(), request.getEndDate(), txnFlag);
		if (bthMerInAccDtlVo == null) {
			queryMonthBillResponse.setTotalTxnAmt("0");
			queryMonthBillResponse.setTotalTxnCount(0);
		}else {
            queryMonthBillResponse.setTotalTxnAmt(bthMerInAccDtlVo.getTotalTxnAmt());
            queryMonthBillResponse.setTotalTxnCount(bthMerInAccDtlVo.getTotalTxnCount());
        }


		queryMonthBillResponse.setMonthlyBill(monthlyBillList);
		queryMonthBillResponse.setRespCode("0000");
		queryMonthBillResponse.setRespMsg("月账单查询成功");
		return queryMonthBillResponse;
	}

    /**
     * 根据入账时间查询返佣信息
     * @param request
     * @param monthlyBillList
     * @param txnFlag
     */
    private void updateMonthlyBillListByInAcctDate(QueryMonthBillRequest request, List<MonthlyBill> monthlyBillList, String txnFlag) {
        // 处理 :    根据商户号 , 开始日期 , 结束时间查询返佣情况  , 插入list
        List<MerCommissionVo> subList = bthMerInAccDtlDao.selectCommissionAmtBySubMerIdAndInAcctDate(request.getMchtId(),
                request.getStartDate(), request.getEndDate(),txnFlag);
        for (MerCommissionVo merCommissionVo : subList) {
            boolean matchFlag = false;
            for (MonthlyBill monthlyBill : monthlyBillList) {
                // 总店有交易
                if (merCommissionVo.getInAcctDate().equals(monthlyBill.getTxnTime())){
                    // 匹配到 , 入账金额加上查询出来的返佣金额
                    matchFlag = true;
                    monthlyBill.setInAcctAmt(monthlyBill.getInAcctAmt().add(merCommissionVo.getCommissionAmt()));
                }else {
                    continue;
                }
            }
            // 没有匹配到  , 新建实体插入list
            if (!matchFlag){
                MonthlyBill monthlyBill = new MonthlyBill();
                monthlyBill.setTxnTime(merCommissionVo.getInAcctDate());
                // 分店返佣金额即为入账金额
                monthlyBill.setInAcctAmt(merCommissionVo.getCommissionAmt());
                monthlyBill.setInAcctStat(Constans.IN_ACC_STAT_SUCC);
                monthlyBill.setTxnAmt(BigDecimal.ZERO);
                monthlyBill.setTxnCount(0);
                monthlyBillList.add(monthlyBill);
            }
        }
    }

    /**
     * 根据交易时间查询返佣信息
     * @param request
     * @param monthlyBillList
     * @param txnFlag
     */
    private void updateMonthlyBillList(QueryMonthBillRequest request, List<MonthlyBill> monthlyBillList, String txnFlag) {
        // 处理 :   根据商户号 , 开始日期 , 结束时间查询返佣情况  , 插入list ,
        List<MerCommissionVo> subList = bthMerInAccDtlDao.selectCommissionAmtBySubMerIdAndTxnTm(request.getMchtId(),
                request.getStartDate(), request.getEndDate(), txnFlag);
        for (MerCommissionVo merCommissionVo : subList) {
            boolean matchFlag = false;
            for (MonthlyBill monthlyBill : monthlyBillList) {
                // 总店有交易
                if (merCommissionVo.getInAcctDate().equals(monthlyBill.getTxnTime())){
                   // 匹配到 , 入账金额加上查询出来的返佣金额
                    matchFlag = true;
                    monthlyBill.setInAcctAmt(monthlyBill.getInAcctAmt().add(merCommissionVo.getCommissionAmt()));
                }else {
                    continue;
                }
            }
            // 没有匹配到  , 新建实体插入list
            if (!matchFlag){
                MonthlyBill monthlyBill = new MonthlyBill();
                monthlyBill.setTxnTime(merCommissionVo.getInAcctDate());
                // 分店返佣金额即为入账金额
                monthlyBill.setInAcctAmt(merCommissionVo.getCommissionAmt());
                monthlyBill.setInAcctStat(Constans.IN_ACC_STAT_SUCC);
                monthlyBill.setTxnAmt(BigDecimal.ZERO);
                monthlyBill.setTxnCount(0);
                monthlyBillList.add(monthlyBill);
            }
        }
    }

    /**
	 * 分店交易月统计接口
	 */
	@Override
	public MonthTradeStatisticsResponse queryMonthTradeStatistics(MonthTradeStatisticsRequest request) {
		log.info("[AccountLiquidationQuery-queryMonthTradeStatistics]--用户号:" + request.getUserId() + "---开始日期:"
				+ request.getStartDate() + "---结束日期:" + request.getEndDate() + "---商户号:" + request.getMchtId());
		MonthTradeStatisticsResponse monthTradeStatisticsResponse = new MonthTradeStatisticsResponse();
		// 初始化返回报文
		CommonResponseUtils.setCommonResp(request, monthTradeStatisticsResponse);

		String startDate = request.getStartDate();
		String endDate = request.getEndDate();
		/*
		 * 控制查询时间
		 */
		if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
			DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
			DateTime currentDateTime1 = DateTime.now();
			DateTime qstartDateTime = null;
			DateTime qendDateTime = null;
			DateTime currentDateTime = DateTime.parse(currentDateTime1.toString("yyyyMMdd"));// 去掉时分秒后的当前日期
			// System.out.println(currentDateTime.);
			// IfspDateTime.plusTime(orgTime, orgTimeFormat, unit, nums)//推时间
			if (StringUtils.isNotBlank(startDate)) {
				log.info("===================================startDate:" + startDate);
				qstartDateTime = DateTime.parse(startDate, format);
			}
			if (StringUtils.isNotBlank(endDate)) {
				qendDateTime = DateTime.parse(endDate, format);
				// 查询结束时间不能大于当前时间yyyyMMdd
				if (currentDateTime.minusDays(1).compareTo(qendDateTime) <= 0) {
					log.info("===================================查询结束日期大于系统时间");
					// throw new Exception("查询结束日期大于系统时间");
					monthTradeStatisticsResponse.setRespMsg(RespConstans.RESP_ENDTIME_GT_CURRENTTIMEYESTERDAY.getDesc());
					monthTradeStatisticsResponse.setRespCode(RespConstans.RESP_ENDTIME_GT_CURRENTTIMEYESTERDAY.getCode());
					return monthTradeStatisticsResponse;
				}
			}
			if (!StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
				log.info("===================================endDate:" + currentDateTime.toString("yyyyMMdd"));
				endDate = currentDateTime.toString("yyyyMMdd");
				qendDateTime = DateTime.parse(endDate, format);
			}
			if (!StringUtils.isBlank(endDate) && StringUtils.isBlank(startDate)) {
				log.info("===================================startDate"
						+ qendDateTime.minusMonths(3).toString("yyyyMMdd"));
				startDate = qendDateTime.minusMonths(3).toString("yyyyMMdd");
				qstartDateTime = DateTime.parse(startDate, format);
			}
			if(qstartDateTime!=null && qendDateTime!=null){
				// 开始时间不能大于结束时间
				if (qstartDateTime.compareTo(qendDateTime) >= 1) {
					log.info("===================================开始时间大于结束时间");
					// throw new Exception("开始时间不能大于结束时间");
					monthTradeStatisticsResponse.setRespMsg(RespConstans.RESP_STARTTIME_GT_ENDTIME.getDesc());
					monthTradeStatisticsResponse.setRespCode(RespConstans.RESP_STARTTIME_GT_ENDTIME.getCode());
					return monthTradeStatisticsResponse;
				}

				// 开始时间和结束时间的间隔不能大于三个月
				if (qstartDateTime.plusMonths(3).compareTo(qendDateTime) < 0) {
					log.info("===================================开始时间和结束时间的间隔不能大于一个月");
					// throw new Exception("开始时间和结束时间的间隔不能大于三个月");
					monthTradeStatisticsResponse.setRespMsg(RespConstans.RESP_QUERYTIME_GT_ONEMONTH.getDesc());
					monthTradeStatisticsResponse.setRespCode(RespConstans.RESP_QUERYTIME_GT_ONEMONTH.getCode());
					return monthTradeStatisticsResponse;
				}
			}

		}
//		// 根据商户号查询所有分店
//		 List<MchtBaseInfo> mchtBaseInfoList =
//		 mchtBaseInfoDao.querySubbranchByMchtId(request.getMchtId());
//		 查询
		SoaResults result;
		SoaParams params = new SoaParams();
//
		List<SubMchtInfoResultVo> subMchtList = new ArrayList<>();
//
		params = MchtMsg.querySubMchtInfoMmg(params, request);
		SoaResults results = mchtCenterSoaClientService.querySubMchtInfo(params);
		Map<Object, Object> datas = results.getDatas();
		log.info(">>>>>>>>>>>>>>>>>>请求结果:" + JSON.toJSONString(results));
		// 获取分店信息
		List listFirst = (List) datas.get("subMchtInfoList");
		for (Object obj : listFirst) {
			SubMchtInfoResultVo resultVo = IfspFastJsonUtil.mapTobean(IfspFastJsonUtil.objectTomap(obj),
					SubMchtInfoResultVo.class);
			subMchtList.add(resultVo);
		}
		
		if(subMchtList.size()==0){
			monthTradeStatisticsResponse.setRespCode(RespConstans.RESP_NO_SUBBRANCH.getCode());
			monthTradeStatisticsResponse.setRespCode(RespConstans.RESP_NO_SUBBRANCH.getDesc());
			return monthTradeStatisticsResponse;
		}

		PagnResult pagnResult = IfspFastJsonUtil.mapTobean(IfspFastJsonUtil.objectTomap(datas.get("pagnResult")),
				PagnResult.class);

		if (pagnResult != null) {
			monthTradeStatisticsResponse.setPagnResult(pagnResult);
		}

//		SubMchtInfoResultVo s=new SubMchtInfoResultVo();
//		s.setMchtId("11825101080000556");
//		s.setMchtSimName("分店一");
//		subMchtList.add(s);
//
//		SubMchtInfoResultVo s2=new SubMchtInfoResultVo();
//		s2.setMchtId("68845117020000559");
//		s2.setMchtSimName("分店二");
//		subMchtList.add(s2);

		// 初始化分店月统计信息
		List<MonthlyStatis> monthlyStatisInitList = new ArrayList<MonthlyStatis>();
		if (subMchtList != null || subMchtList.size() > 0) {
			for (int i = 0; i < subMchtList.size(); i++) {
				SubMchtInfoResultVo subMchtInfoResultVo = subMchtList.get(i);
				MonthlyStatis monthlyStatis = new MonthlyStatis();
				monthlyStatis.setMchtId(subMchtInfoResultVo.getMchtId());
				monthlyStatis.setSubMchtName(subMchtInfoResultVo.getMchtSimName());
				monthlyStatis.setMonthTxnAmt(new BigDecimal(0));
				monthlyStatis.setMonthTxnAmt(new BigDecimal(0));
				monthlyStatis.setMonthCnt(0);
				monthlyStatisInitList.add(monthlyStatis);
			}
		}
		monthTradeStatisticsResponse.setTotalTxnAmt(new BigDecimal(0));
		monthTradeStatisticsResponse.setTotalInAcctAmt(new BigDecimal(0));
		monthTradeStatisticsResponse.setTotalCnt(0);
		if (monthlyStatisInitList.size() > 0) {
			monthTradeStatisticsResponse.setMonthlyStatis(monthlyStatisInitList);
		}


		BigDecimal totalTxnAmt = new BigDecimal(0);// 总交易金额
        BigDecimal totalMchtCouponAmt = new BigDecimal(0);// 总营销金额(商户出资)
		BigDecimal totalInAcctAmt ;// 总收款金额(总交易金额减去营销金额(商户出资))
		int totalCnt = 0;// 总笔数


		List<MonthlyStatis> monthlyStatisList = new ArrayList<MonthlyStatis>();
		// 分店月统计列表==============================================================
		for (int i = 0; i < subMchtList.size(); i++) {
			SubMchtInfoResultVo subMchtInfoResultVo = subMchtList.get(i);
			// 根据商户号查询和时间查询分店统计
			BthMerInAccDtlVo bthMerInAccDtl = bthMerInAccDtlDao.queryMerInAccDtlByMchtIdAndTime(
					subMchtInfoResultVo.getMchtId(), request.getStartDate(), request.getEndDate());
			MonthlyStatis monthlyStatis = new MonthlyStatis();
			monthlyStatis.setMchtId(subMchtInfoResultVo.getMchtId());
			monthlyStatis.setSubMchtName(subMchtInfoResultVo.getMchtSimName());
			// 原订单金额(不包括退款 )
			monthlyStatis.setMonthTxnAmt(new BigDecimal(bthMerInAccDtl.getTxnAmt()));
            // 入账金额( 包括了退款 )
			monthlyStatis.setMonthInAcctAmt(new BigDecimal(bthMerInAccDtl.getSetlAmt()));
            // 交易笔数(不包括退款)
			monthlyStatis.setMonthCnt(bthMerInAccDtl.getTotalTxnCount());
//			totalTxnAmt = totalTxnAmt.add(new BigDecimal(bthMerInAccDtl.getTxnAmt()));
//			// 营销金额（商户出资）
//            totalMchtCouponAmt = totalMchtCouponAmt.add(new BigDecimal(bthMerInAccDtl.getMchtCouponAmt()));
//			totalCnt += bthMerInAccDtl.getTotalTxnCount();
			
			monthlyStatisList.add(monthlyStatis);
		}
		//这几段代码留给后人去优化吧，没空动了，涉及太多~
		//查询综合计算值(不分页查询)  商户号取前16位，前16位为去除总店外的所有分店
		BthMerInAccDtlVo queryMerSum = bthMerInAccDtlDao.queryMerSum(subMchtList.get(0).getMchtId().substring(0,16).concat("%"), request.getStartDate(), request.getEndDate());
		totalTxnAmt = new BigDecimal(queryMerSum.getTxnAmt()) ;
		totalMchtCouponAmt = new BigDecimal(queryMerSum.getMchtCouponAmt());
		totalCnt = queryMerSum.getTotalTxnCount();
		
		// 总收款金额(总交易金额减去营销金额(商户出资))
        totalInAcctAmt = totalTxnAmt.subtract(totalMchtCouponAmt);
		monthTradeStatisticsResponse.setTotalCnt(totalCnt);
		monthTradeStatisticsResponse.setTotalInAcctAmt(totalInAcctAmt);
		monthTradeStatisticsResponse.setTotalTxnAmt(totalTxnAmt);
		monthTradeStatisticsResponse.setMonthlyStatis(monthlyStatisList);
		monthTradeStatisticsResponse.setRespCode("0000");
		monthTradeStatisticsResponse.setRespMsg("查询分店月交易统计成功");
		return monthTradeStatisticsResponse;
	}

	@Override
	public DailyTradeStatisticsResponse DailyTradeStatistics(DailyTradeStatisticsRequest request) {
		log.info("[AccountLiquidationQuery-DailyTradeStatistics]--开始日期:" + request.getStartDate() + "---结束日期:"
				+ request.getEndDate() + "---商户号:" + request.getMchtId());
		DailyTradeStatisticsResponse response = new DailyTradeStatisticsResponse();
		String startDate = request.getStartDate();

		String endDate = request.getEndDate();
		/*
		 * 控制查询时间
		 */
		if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
			DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
			DateTime currentDateTime1 = DateTime.now();
			DateTime qstartDateTime = null;
			DateTime qendDateTime = null;
			DateTime currentDateTime = DateTime.parse(currentDateTime1.toString("yyyyMMdd"));// 去掉时分秒后的当前日期
			// System.out.println(currentDateTime.);
			// IfspDateTime.plusTime(orgTime, orgTimeFormat, unit, nums)//推时间
			if (StringUtils.isNotBlank(startDate)) {
				log.info("===================================startDate:" + startDate);
				qstartDateTime = DateTime.parse(startDate, format);
			}
			if (StringUtils.isNotBlank(endDate)) {
				qendDateTime = DateTime.parse(endDate, format);
				// 查询结束时间不能大于当前时间yyyyMMdd
				if (currentDateTime.minusDays(1).compareTo(qendDateTime) <= 0) {
					log.info("===================================查询结束日期大于系统时间");
					response.setRespMsg(RespConstans.RESP_ENDTIME_GT_CURRENTTIMEYESTERDAY.getDesc());
					response.setRespCode(RespConstans.RESP_ENDTIME_GT_CURRENTTIMEYESTERDAY.getCode());
					return response;
				}
			}
			if (!StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
				log.info("===================================endDate:" + currentDateTime.toString("yyyyMMdd"));
				endDate = currentDateTime.toString("yyyyMMdd");
				qendDateTime = DateTime.parse(endDate, format);
			}
			if (!StringUtils.isBlank(endDate) && StringUtils.isBlank(startDate)) {
				log.info("===================================startDate"
						+ qendDateTime.minusMonths(3).toString("yyyyMMdd"));
				startDate = qendDateTime.minusMonths(3).toString("yyyyMMdd");
				qstartDateTime = DateTime.parse(startDate, format);
			}
			if(qendDateTime!=null && qstartDateTime!=null){
				// 开始时间不能大于结束时间
				if (qstartDateTime.compareTo(qendDateTime) >= 1) {
					log.info("===================================开始时间大于结束时间");
					response.setRespMsg(RespConstans.RESP_STARTTIME_GT_ENDTIME.getDesc());
					response.setRespCode(RespConstans.RESP_STARTTIME_GT_ENDTIME.getCode());

					return response;
				}
				// 开始时间和结束时间的间隔不能大于一个月
				if (qstartDateTime.plusMonths(1).compareTo(qendDateTime) < 0) {
					log.info("===================================开始时间和结束时间的间隔不能大于一个月");
					response.setRespMsg(RespConstans.RESP_QUERYTIME_GT_ONEMONTH.getDesc());
					response.setRespCode(RespConstans.RESP_QUERYTIME_GT_ONEMONTH.getCode());
					return response;
				}
			}



		}
		// 根据商户号查询商户基本信息
		MchtBaseInfo mchtBaseInfo = mchtBaseInfoDao.queryById(request.getMchtId());
		if (mchtBaseInfo == null) {
			response.setRespCode(RespConstans.RESP_NO_MCHT.getCode());
			response.setRespMsg(RespConstans.RESP_NO_MCHT.getDesc());
			return response;
		}

		// 根据商户号查询和时间查询分店交易明细
		List<DailyStatis> dailyList = bthMerInAccDtlDao.queryDailyStatis(mchtBaseInfo.getMchtId(),
				request.getStartDate(), request.getEndDate());

		if (dailyList == null && dailyList.size() == 0) {
			response.setRespCode(RespConstans.RESP_NO_TRANSACTION_RECORDS.getCode());
			response.setRespMsg(RespConstans.RESP_NO_TRANSACTION_RECORDS.getDesc());
			return response;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		List<DailyStatis> dailyStatisList = new ArrayList<>();
		for (int i = 0; i < dailyList.size(); i++) {
			try {
				DailyStatis dailyStatis = dailyList.get(i);
				// yyyyMMdd
				Date txnDateBefore = sdf.parse(dailyStatis.getTxnDate());
				// yyyy-MM-dd
				String txnDateAfter = sdf2.format(txnDateBefore);
				dailyStatis.setTxnDate(txnDateAfter);
				dailyStatisList.add(dailyStatis);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		response.setSubMchtName(mchtBaseInfo.getMchtName());
		response.setDailyStatis(dailyStatisList);
		response.setRespCode("0000");
		response.setRespMsg("查询分店日交易统计成功");
		return response;
	}

	/**
	 * 每周自然日交易金额走势
	 */
	@Override
	public WeekCountQueryResponse WeekCountQuery(WeekCountQueryRequest request) {
		log.info("[AccountLiquidationQuery-DailyTradeStatistics]--开始日期:" + request.getStartDate() + "---结束日期:"
				+ request.getEndDate() + "---商户号:" + request.getMchtId());
		WeekCountQueryResponse response=new WeekCountQueryResponse();
		
		
		List<WeekDay> weekDays=bthMerDailyTxnCountDao.queryWeekCountAll(request.getMchtId(),request.getStartDate(),request.getEndDate(),request.getWookDate());
		
		if(weekDays==null ||weekDays.size()==0){
			log.info("每周自然日交易金额走势>>>>>>>>>>>list为空");
			response.setRespCode(RespConstans.RESP_NO_WEEK_DEAL.getCode());
			response.setRespMsg(RespConstans.RESP_NO_WEEK_DEAL.getDesc());
			return response;
		}
		
		response.setWeekDays(weekDays);
		
		return response;
	}

}