package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import com.scrcu.ebank.ebap.batch.common.utils.ReflectionUtil;
import com.scrcu.ebank.ebap.batch.service.AcctContrastService;
import com.scrcu.ebank.ebap.exception.IfspBizException;

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
public class AcctContrastServiceImpl implements AcctContrastService {
	@Resource
    private BthWxFileDetDao bthWxFileDetDao;
	
	@Resource
	private BthAliFileDetDao bthAliFileDetDao;
	
	@Resource
	private BthUnionFileDetDao bthUnionFileDetDao;
	@Resource
	
	private DebitTranInfoDao debitTranInfoDao;
	
	@Resource
	private BthChkRsltInfoDao bthChkRsltInfoDao;
	
	@Resource
	private BthPagyChkErrInfoDao bthPagyChkErrInfoDao;
	
	@Resource
	private BthPagyLocalInfoDao bthPagyLocalInfoDao;
	
	@Resource
	private PayOrderInfoDao payOrderInfoDao;
	
	@Resource
	private KeepAccInfoDao keepAccInfoDao;
	
	@Resource
	private TpamAtWechatTxnInfoDao tpamAtWechatTxnInfoDao;
	
	@Resource
	private MchtSettlRateCfgDao mchtSettlRateCfgDao;
	
	@Resource
	private PagyTxnInfoDao pagyTxnInfoDao;

	@Resource
	private BthChkUnequalInfoDao bthChkUnequalInfoDao;

    /**
     * 挂账表
     */
	@Resource
	private BthMerHangAccDao bthMerHangAccDao;


    @SoaClient(name = "mchtPayQuery")
    private ISoaClient merpayQuery;
	
	/**
     * 微信通道对账单
     * @param request
     * @return
	 * @throws Exception 
     */
	@Override
	public AcctContrastResponse wxBillContrast(AcctContrastRequest request) throws Exception {
         log.info("---------------------微信通道系统vs微信对账开始--------------------");
         /*****获取请求参数值********/
 		 String pagySysNo = request.getPagySysNo();//通道系统编号
 		 String settleDate = request.getSettleDate();// 清算日期
 		 
 		 /*********初始化响应对象***********/
 		 AcctContrastResponse acctContrastResponse = new AcctContrastResponse();
         log.info("date:"+settleDate);
         String doubtDate=IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, -1);
         log.info("doubtDate:"+doubtDate);
         String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
         log.info("chkSuccDt:"+chkSuccDt);
         //1.	根据清算日期和通道系统编号删除对账结果表【清算中心】和对账差错表【清算中心】
         log.info("---------------------删除通道对账结果表和通道对账差错表里清算日期为当前系统日期T-1的流水------------------");
         bthChkRsltInfoDao.deleteAliByStlmDateAndPagySysNo(chkSuccDt,pagySysNo);
         bthPagyChkErrInfoDao.deleteByStlmDateAndPagySysNo(settleDate,pagySysNo);


         // ------------------      modify by ljy   date: 20190422      start  ------------------------------------------------
         log.info("-------------------删除对账不平表清算日[{}]跑进去的微信数据------------------",settleDate);
         // 这里不用 request.getPagySysNo() (因为传的值带空格, eg: "605 " ) ,对账不平表是后面建的 ,类型为 CHAR3 ,所以这里不用空格
         bthChkUnequalInfoDao.deleteByChkUeqDtAndPagySysNo(chkSuccDt,"605");
        // ------------------      modify by ljy   date: 20190422      end      -----------------------------------------------



         //2.	根据清算日期和通道系统编号查询微信对账文件明细表【清算中心】和本地通道交易明细对账表【清算中心】，更新状态为未对账,对账结果还原
         //List<BthPagyLocalInfo> bthPagyLocalInfos=bthPagyLocalInfoDao.selectAliByDateAndPagyno(settleDate,pagySysNo);
         //20181111修改，对账添加微信直连数据（通道602）
         Map<String, Object> param = new HashMap<String, Object>();
         param.put("pagySysNo", pagySysNo);
         param.put("settleDate", settleDate);
         List<BthPagyLocalInfo> bthPagyLocalInfos=bthPagyLocalInfoDao.selectList("selectWxLocalInfoByDateAndPagyno",param);
         log.info("---------------------更新本地通道交易明细对账表"+bthPagyLocalInfos.size()+"条---------------------");
         if(bthPagyLocalInfos!=null){
             for(BthPagyLocalInfo bthPagyLocalInfo:bthPagyLocalInfos){
            	 bthPagyLocalInfo.setChkSt(Constans.CHK_STATE_00);
            	 bthPagyLocalInfo.setChkRst("");
                 bthPagyLocalInfo.setDubiousFlag("");
            	 bthPagyLocalInfoDao.updateByPrimaryKeySelective(bthPagyLocalInfo);
             }
         }
         List<BthWxFileDet> bthWxFileDets=bthWxFileDetDao.selectByDate(settleDate);
         log.info("---------------------更新微信对账文件明细表"+bthWxFileDets.size()+"条---------------------");
         if(bthWxFileDets!=null){
             for(BthWxFileDet bthWxFileDet:bthWxFileDets){
            	 bthWxFileDet.setChkAcctSt(Constans.CHK_STATE_00);
            	 bthWxFileDet.setChkRst("");
            	 bthWxFileDet.setDubiousFlag("");
            	 bthWxFileDetDao.updateByPrimaryKeySelective(bthWxFileDet);
             }
         } 
         //TODO 前天可疑三方/本地流水信息表状态（ChkAcctSt，ChkRst）还原
         Map<String,Object> msg = new HashMap<String,Object>();
         msg.put("settleDate", doubtDate);
         msg.put("pagySysNo", pagySysNo);
         msg.put("dubiousFlag", Constans.DUBIOUS_FLAG_01);
         List<BthPagyLocalInfo> bthPagyLocalDoubtInfos=bthPagyLocalInfoDao.selectList("queryThreeLocalByFlagAndDate",msg);
         log.info("---------------------更新可疑本地通道交易明细对账表"+bthPagyLocalDoubtInfos.size()+"条---------------------");
         if(bthPagyLocalDoubtInfos!=null){
             for(BthPagyLocalInfo bthPagyLocalDoubtInfo:bthPagyLocalDoubtInfos){
            	 bthPagyLocalDoubtInfo.setChkSt(Constans.CHK_STATE_00);
            	 bthPagyLocalDoubtInfo.setChkRst(Constans.CHK_RST_02);
            	 bthPagyLocalInfoDao.updateByPrimaryKeySelective(bthPagyLocalDoubtInfo);
             }
         }
         Map<String,Object> msgs = new HashMap<String,Object>();
         msgs.put("settleDate", doubtDate);
         msgs.put("dubiousFlag", Constans.DUBIOUS_FLAG_01);
         List<BthWxFileDet> bthWxDoubtFileDets=bthWxFileDetDao.selectList("queryWxFileByFlagAndDate",msgs);
         log.info("---------------------更新可疑微信对账文件明细表"+bthWxDoubtFileDets.size()+"条---------------------");
         if(bthWxFileDets!=null){
             for(BthWxFileDet bthWxDoubtFileDet:bthWxDoubtFileDets){
            	 bthWxDoubtFileDet.setChkAcctSt(Constans.CHK_STATE_00);
            	 bthWxDoubtFileDet.setChkRst(Constans.CHK_RST_02);
            	 bthWxFileDetDao.updateByPrimaryKeySelective(bthWxDoubtFileDet);
             }
         }

         // 针对撤销交易 , 直接置为 已对账  , 对账结果为忽略账
        Map<String,Object> revokedMap = new HashMap<>(2);
        revokedMap.put("settleDate", settleDate);
        revokedMap.put("tradeSt", "REVOKED");
        List<BthWxFileDet> revokedLists=bthWxFileDetDao.selectList("queryRevokedDets",revokedMap);
        log.info("---------------------发现微信对账文件撤销交易"+revokedLists.size()+"条---------------------");
        if (IfspDataVerifyUtil.isNotEmptyList(revokedLists)){
            for (BthWxFileDet revokedList : revokedLists) {
                log.info("==========================>> 更新撤销订单[{}]为已对账  , 忽略账 <<=============================",revokedList.getOrderNo());
                bthWxFileDetDao.updateChkStRstByOrderNo(revokedList.getOrderNo());
                log.info("==========================>> 更新撤销订单的原订单[{}]为已对账  , 忽略账 <<=============================",revokedList.getSrcOrderNo());
                bthWxFileDetDao.updateChkStRstByOrderNo(revokedList.getSrcOrderNo());
            }
        }

        log.info("删除对账挂起表于清算日期[{}]录入的记录",settleDate);
        bthMerHangAccDao.deleteBySettleDateIn(settleDate);
        log.info("还原对账挂起表于清算日期[{}]修改的记录 ,状态重新置为挂起",settleDate);
        bthMerHangAccDao.updateHangStBySettleDateOut(settleDate);


         // 保存处理中的对账结果 (只可能是退款)
        List<BthChkRsltInfo> list = new ArrayList<>();

         log.info("---------------------微信可疑流水对账开始---------------------");
         wechatDoubt(settleDate,doubtDate,pagySysNo,list);
         log.info("---------------------通道系统可疑流水对账开始---------------------");
         systemDoubt(settleDate,doubtDate,pagySysNo,list);
         log.info("---------------------未对账流水对账开始---------------------");
         wechatVsSystem(settleDate,pagySysNo,list);
         log.info("---------------------剩余通道对账表未匹配流水开始---------------------");
         systemVsWechat(settleDate,pagySysNo);


        // 处理对账挂起表, 调交易接口 ,如果查到终态 , 录入对账结果表
        dealHangRecord(settleDate);
        // 处理中的对账结果表记录调交易接口, 如果还是中间态则录入对账挂起表(查询当前日期是否有原交易, 如果有一并录入对账挂起表) ,如果是终态则录入对账结果表
        dealRstMidSt(settleDate, list);

        return acctContrastResponse;
	}

    /**
     * 处理当前日期对账结果状态为退款处理中的记录
     * @param settleDate
     * @param list
     * @throws ParseException
     */
    private void dealRstMidSt(String settleDate, List<BthChkRsltInfo> list) throws ParseException {
        if (IfspDataVerifyUtil.isNotEmptyList(list)){
            log.info("开始处理交易日 [ "+settleDate+"]对账结果交易状态为处理中的记录");
            log.info("交易日 [ "+settleDate+"]对账结果存在处理中的条数为:"+list.size());
            // 对账成功日期
            String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
            log.info("chkSuccDt:"+chkSuccDt);

            for (BthChkRsltInfo bthChkRsltInfo : list) {
                // 根据订单号查询出订单流水
                log.info("STEP 1.根据订单号["+bthChkRsltInfo.getOrderSsn()+"]查询出订单流水");
                PayOrderInfo payOrderInfo = payOrderInfoDao.queryByTxnSeqId(bthChkRsltInfo.getOrderSsn());

                if (IfspDataVerifyUtil.isBlank(payOrderInfo)){
                    log.error("根据订单号[{}]查询订单失败",bthChkRsltInfo.getOrderSsn());
                    throw new IfspBizException(RespConstans.RESP_QUERY_ERR.getCode(), "根据订单号查询订单失败!!!");
                }

                log.info("STEP 2.根据订单信息组装报文,调用订单查询交易状态接口");
                SoaParams params = new SoaParams();
                params.put("txnTypeNo","O1300002");
                params.put("origReqSsn",payOrderInfo.getReqOrderSsn());
                params.put("chlNo","01");
                params.put("mchtNo",payOrderInfo.getMchtId());
                SoaResults result = merpayQuery.invoke(params);
                // 查到终态 , 录入对账结果表进行清分
                if ("00".equals(result.get("orderStat"))){
                    log.info("STEP 3.订单号["+payOrderInfo.getOrderSsn()+"]查询到终态成功.");
                    bthChkRsltInfo.setStlmSt(Constans.STLM_ST_00);
                    bthChkRsltInfo.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                    bthChkRsltInfo.setCrtTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                    bthChkRsltInfo.setLstUpdTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                    bthChkRsltInfo.setChkSt(Constans.CHK_STATE_00);
                    bthChkRsltInfo.setChkRst("");
                    log.info("订单["+payOrderInfo.getOrderSsn()+"]录入对账结果表");
                    bthChkRsltInfoDao.insert(bthChkRsltInfo);

                }else if ("09".equals(result.get("orderStat"))){
                    log.info("STEP 3.订单号["+payOrderInfo.getOrderSsn()+"]查询到终态为失败, 既不录入对账结果表,也不录入挂账表,认为失败不处理");

                }else {
                    log.info("STEP 3.订单号["+payOrderInfo.getOrderSsn()+"]未查到终态 ");
                    BthMerHangAcc record=new BthMerHangAcc();
                    ReflectionUtil.copyProperties(bthChkRsltInfo,record);
                    record.setHangSt(Constans.HANG_ST_00);
                    record.setSettleDateIn(settleDate);
                    record.setCrtTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                    record.setLstUpdTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                    log.info("订单["+payOrderInfo.getOrderSsn()+"]录入挂账表");
                    bthMerHangAccDao.insert(record);

                    // 查询原交易是否今天清分  ,是则一并录入挂账表
                    log.info("查询订单号["+payOrderInfo.getOrderSsn()+"]的原交易是否是今日入账");
                    Map<String ,Object> param = new HashMap<>();
                    param.put("orderSsn",payOrderInfo.getOrigOrderSsn());
                    param.put("chkSuccDate",IfspDateTime.getYYYYMMDD());
                    BthChkRsltInfo bthChkRsltInfo1 = bthChkRsltInfoDao.selectOne("selectByOrderSsnDate", param);
                    if (IfspDataVerifyUtil.isNotBlank(bthChkRsltInfo1)){
                        log.info("订单号["+payOrderInfo.getOrderSsn()+"]的原交易查到是今日入账, 和退款保持一致录入挂账表, 并从对账结果表中删除...");
                        BthMerHangAcc record1=new BthMerHangAcc();
                        ReflectionUtil.copyProperties(bthChkRsltInfo1,record1);
                        record1.setHangSt(Constans.HANG_ST_00);
                        record1.setSettleDateIn(settleDate);
                        record1.setCrtTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                        record1.setLstUpdTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                        log.info("原交易["+bthChkRsltInfo1.getOrderSsn()+"]录入挂账表");
                        bthMerHangAccDao.insert(record1);
                        // 删除对账结果表正交易记录
                        log.info("将原交易["+bthChkRsltInfo1.getOrderSsn()+"]从对账结果表中删除");
                        bthChkRsltInfoDao.deleteByPrimaryKey(bthChkRsltInfo1.getPagyPayTxnSsn());
                    }
                }
            }
            log.info("交易日 [ "+settleDate+"]对账结果交易状态为处理中的记录处理完毕.");
        }
    }

    /**
     * 处理对账挂起表
     * @throws ParseException
     * @param settleDate
     */
    private void dealHangRecord(String settleDate) throws ParseException {
        List<BthMerHangAcc> hangList = bthMerHangAccDao.selectByHangSt(Constans.HANG_ST_00);
        if (IfspDataVerifyUtil.isNotEmptyList(hangList)){
            log.info("开始处理挂账表中状态为挂账中的记录");
            // 对账成功日期
            String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
            log.info("chkSuccDt:"+chkSuccDt);

            for (BthMerHangAcc bthMerHangAcc : hangList) {
                log.info("STEP 1.根据挂账表里的订单号["+bthMerHangAcc.getOrderSsn()+"]查询订单");
                PayOrderInfo payOrderInfo = payOrderInfoDao.queryByTxnSeqId(bthMerHangAcc.getOrderSsn());
                if (IfspDataVerifyUtil.isBlank(payOrderInfo)){
                    throw new IfspBizException("9999", "根据挂账表订单号["+bthMerHangAcc.getOrderSsn()+"]查询订单信息失败！");
                }
                log.info("STEP 2.根据订单信息组装报文,调用订单查询交易状态接口");
                SoaParams params = new SoaParams();
                params.put("txnTypeNo","O1300002");
                params.put("origReqSsn",payOrderInfo.getReqOrderSsn());
                params.put("chlNo","01");
                params.put("mchtNo",payOrderInfo.getMchtId());
                SoaResults result = merpayQuery.invoke(params);

                // 查到终态成功 , 再查询出原交易一并录入对账结果表进行清分 ,  更新挂账状态
                if ("00".equals(result.get("orderStat")) ){
                    log.info("STEP 3.订单号["+payOrderInfo.getOrderSsn()+"]查询到终态为成功");
                    BthChkRsltInfo record=new BthChkRsltInfo();
                    ReflectionUtil.copyProperties(bthMerHangAcc,record);
                    record.setStlmSt(Constans.STLM_ST_00);
                    record.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                    record.setChkSt(Constans.CHK_STATE_00);
                    record.setChkRst("");
                    record.setCrtTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                    record.setLstUpdTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                    log.info("订单["+payOrderInfo.getOrderSsn()+"]录入对账结果表");
                    bthChkRsltInfoDao.insert(record);
                    log.info("订单["+payOrderInfo.getOrderSsn()+"]更新挂账表状态为挂账已处理");
                    bthMerHangAccDao.updateHangStByKey(bthMerHangAcc.getPagyPayTxnSsn() ,IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS), settleDate);

                    log.info("STEP 4.查询订单号["+payOrderInfo.getOrderSsn()+"]是否有原交易在挂账表中");
                    BthMerHangAcc acc = bthMerHangAccDao.selectByPrimaryKey(bthMerHangAcc.getOrigPagyTxnSsn());
                    if (IfspDataVerifyUtil.isNotBlank(acc)&& Constans.HANG_ST_00.equals(bthMerHangAcc.getHangSt())){
                        log.info("订单号["+payOrderInfo.getOrderSsn()+"]存在原交易在挂账表中");
                        BthChkRsltInfo record1=new BthChkRsltInfo();
                        ReflectionUtil.copyProperties(acc,record1);
                        record1.setStlmSt(Constans.STLM_ST_00);
                        record1.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                        record1.setChkSt(Constans.CHK_STATE_00);
                        record1.setChkRst("");
                        record1.setCrtTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                        record1.setLstUpdTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                        log.info("原交易["+acc.getOrderSsn()+"]插入对账结果表");
                        bthChkRsltInfoDao.insert(record1);
                        log.info("原交易["+acc.getOrderSsn()+"]更新挂账状态为挂账已处理");
                        bthMerHangAccDao.updateHangStByKey(bthMerHangAcc.getOrigPagyTxnSsn() ,IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS) ,settleDate);
                    }else {
                        log.info("订单号["+payOrderInfo.getOrderSsn()+"]没有原交易在挂账表中,不处理");
                    }

                // 查到终态失败 ,  改状态为 挂账已处理  ,再查看是否有原交易放在挂账表里, 如果有就将其录入对账结果表
                }else if ( "09".equals(result.get("orderStat"))){
                    log.info("STEP 3.订单号["+payOrderInfo.getOrderSsn()+"]查询到终态为失败,更新挂账状态为已处理...");
                    log.info("更新订单号["+payOrderInfo.getOrderSsn()+"]的挂账流水 , 将挂账状态改为 挂账已处理");
                    bthMerHangAccDao.updateHangStByKey(bthMerHangAcc.getPagyPayTxnSsn() ,IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS), settleDate);

                    log.info("STEP 4. 查询订单号["+payOrderInfo.getOrderSsn()+"]是否有原支付流水处于挂账状态 ");
                    BthMerHangAcc bthMerHangAcc1 = bthMerHangAccDao.selectByPrimaryKey(bthMerHangAcc.getOrigPagyTxnSsn());

                    if (IfspDataVerifyUtil.isNotBlank(bthMerHangAcc1)&& Constans.HANG_ST_00.equals(bthMerHangAcc.getHangSt())){
                        log.info("查到订单号["+payOrderInfo.getOrderSsn()+"]有原支付流水处于挂账状态");
                        BthChkRsltInfo record=new BthChkRsltInfo();
                        ReflectionUtil.copyProperties(bthMerHangAcc1,record);
                        record.setStlmSt(Constans.STLM_ST_00);
                        record.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                        record.setChkSt(Constans.CHK_STATE_00);
                        record.setChkRst("");
                        record.setCrtTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                        record.setLstUpdTm(IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                        log.info("原交易["+bthMerHangAcc1.getOrderSsn()+"]插入对账结果表");
                        bthChkRsltInfoDao.insert(record);
                        log.info("原交易["+bthMerHangAcc1.getOrderSsn()+"]更新挂账状态为挂账已处理");
                        bthMerHangAccDao.updateHangStByKey(bthMerHangAcc.getOrigPagyTxnSsn() ,IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS), settleDate);
                    }else {
                        log.info("查到订单号["+payOrderInfo.getOrderSsn()+"]没有原支付流水处于挂账状态 , 不做处理.");
                    }

                }else {
                    log.info("STEP 3.订单号["+payOrderInfo.getOrderSsn()+"]未查到终态 , 待明日去查询结果.");
                }

            }
            log.info("挂账表中状态为挂账中的记录处理完毕.");
        }
    }


	
	/**
	 * 剩余通道对账表未匹配流水
	 * @param settleDate
	 * @param pagySysNo
	 */
	private void systemVsWechat(String settleDate, String pagySysNo) {
		log.info("---------------------剩余通道对账表未匹配流水开始---------------------");
		List<BthPagyLocalInfo> bthLocalTraces=bthPagyLocalInfoDao.selectAliByDateAndStat(settleDate,Constans.CHK_STATE_00,pagySysNo);
		log.info("---------------------剩余通道对账表未匹配流水"+bthLocalTraces.size()+"条---------------------");
        for(BthPagyLocalInfo bthLocalTrace:bthLocalTraces){
            bthLocalTrace.setChkSt(Constans.CHK_STATE_01);
            bthLocalTrace.setChkRst(Constans.CHK_RST_02);
            bthLocalTrace.setDubiousFlag(Constans.DUBIOUS_FLAG_01);
            bthPagyLocalInfoDao.updateByPrimaryKeySelective(bthLocalTrace);
        }
	}
	
	/**
	 * 未对账流水对账
	 * @param settleDate
	 * @param pagySysNo
	 * @param list
     * @throws Exception
	 */
	private void wechatVsSystem(String settleDate, String pagySysNo, List<BthChkRsltInfo> list) throws Exception {
		log.info("---------------------未对账流水对账开始---------------------");
        // 对账成功日期
        String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
        log.info("chkSuccDt:"+chkSuccDt);
		SimpleDateFormat format = new SimpleDateFormat(IfspDateTime.YYYYMMDD);
        //本地待清算流水
        List<BthPagyLocalInfo> localList=new ArrayList<BthPagyLocalInfo>();
        //3.	根据清算日期，通道系统编号及状态（未对账）查询微信对账文件明细表和本地通道交易明细对账表
        List<BthWxFileDet> wechatList=bthWxFileDetDao.selectByDateAndStat(settleDate,Constans.CHK_STATE_00);
        log.info("---------------------微信待对账流水"+wechatList.size()+"条---------------------");
        //localList=bthPagyLocalInfoDao.selectAliByDateAndStat(settleDate,Constans.CHK_STATE_00,pagySysNo);
        
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pagySysNo", pagySysNo);
        param.put("settleDate", settleDate);
        param.put("state", Constans.CHK_STATE_00);
        localList=bthPagyLocalInfoDao.selectList("selectWxlocalInfoByDateAndStat",param);
        
        log.info("---------------------本地待对账流水"+localList.size()+"条---------------------");
        for(BthWxFileDet wechatRecord:wechatList){
            boolean exist_flag = false;
            for(BthPagyLocalInfo localWechatRecord:localList){
                //退货的按商户退款单号匹配
                String merOrderId=wechatRecord.getOrderNo();
                if(merOrderId.equals(localWechatRecord.getPagyPayTxnSsn())){
                    exist_flag=true;
                    boolean checkResult = true;                       
                    boolean amt=false;
                    boolean type=false;
                    boolean state=false;
                    BigDecimal wechatAmt=null;
                    String tradeSt="";
                    // 仅存在退款处理中的状态 ,其他都是成功
                    if("PROCESSING".equals(wechatRecord.getRefundSt())){
                        tradeSt="02";
                    }else {
                        tradeSt="00";
                    }

                    if("10".equals(wechatRecord.getOrderTp())){
                        //交易是支付
                        wechatAmt=wechatRecord.getOrderAmt();
                    }else{
                        //交易是是退货或撤销
                        wechatAmt=wechatRecord.getRefundAmt();
                    }
                    
                    /**比交易类型*/
                    //20181111修改，线上订单不校验订单类型
                    if(!wechatRecord.getOrderTp().equals(localWechatRecord.getTxnChlAction()) && wechatRecord.getSrcType() == null){
                    	// 插入对账差错表,更新两表状态已对账
                    	checkResult=false;
                    	type=true;
                    }              
                    /**比状态*/
                    if(!tradeSt.equals(localWechatRecord.getTradeSt())){
                        checkResult=false;
                        state=true;
                    }
                    /**比金额*/
                    if(!wechatAmt.toString().equals(localWechatRecord.getTxnAmt().toString())){
                    	checkResult=false;
                    	amt=true;
                    }
                    
                    if(checkResult){
                        /** 对账成功 */
                        //设置状态为00成功和对账描述为成功并插入通道对账结果表
                        //更新通道对账表和第三方对账表状态为对账成功                        
                        localWechatRecord.setChkSt(Constans.CHK_STATE_01);
                        localWechatRecord.setChkRst(Constans.CHK_RST_00);
//                        localWechatRecord.setChkDataDt(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYYMMDD));
                        bthPagyLocalInfoDao.updateByPrimaryKeySelective(localWechatRecord);
                        wechatRecord.setChkAcctSt(Constans.CHK_STATE_01);
                        wechatRecord.setChkRst(Constans.CHK_RST_00);
//                        wechatRecord.setChkDataDt(IfspDateTime.getYYYYMMDD());
                        bthWxFileDetDao.updateByPrimaryKeySelective(wechatRecord);
                        //插入对账结果结果表
                        BthChkRsltInfo record=new BthChkRsltInfo();
                        ReflectionUtil.copyProperties(localWechatRecord,record);
                        record.setStlmSt(Constans.STLM_ST_00);
                        record.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                        record.setChkSt(Constans.CHK_STATE_00);
                        record.setChkRst("");
                        record.setPagySysNo(Constans.WX_SYS_NO);
                        Map<String, Object> params = new HashMap<String, Object>();
                		params.put("pagyTxnSsn", localWechatRecord.getPagyTxnSsn());
                		PagyTxnInfo pagyTxnInfo = pagyTxnInfoDao.selectOne("selectPagyTxnInfoByPagyTxnSsn", params);
                		if(pagyTxnInfo==null){
                			throw new IfspBizException("9999", "根据通道内部订单号查询通道核心信息失败！");
                		}
                        record.setOrderSsn(pagyTxnInfo.getTxnReqSsn());
                        
                        record.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                        record.setTpamTxnFeeAmt(wechatRecord.getFeeAmt().longValue());

                        // todo 如果状态是处理中  , 我们需要挂起,  暂不录入对账结果表
                        if (Constans.TRADE_ST_02.equals(localWechatRecord.getTradeSt())){
                            list.add(record);
                        }else{
                            bthChkRsltInfoDao.insert(record);
                        }

                    }else{
                        /** 对账不平 */

                        // -------- 对账不平, 发现状态不一致进对账不平表 modify by ljy    modify_date : 20190422       start  -------
                        if (state){
                            initChkUnequalInfo(wechatRecord, localWechatRecord, wechatAmt, tradeSt, chkSuccDt);
                        }else {
                            // --------       modify end   --------------------------------------------------------------------------
                            //标识差错原因插入通道对账差错表，
                            //并更新通道流水表和第三方流水表对账状态为对账失败
                            BthPagyChkErrInfo bthBalErrRecord = new BthPagyChkErrInfo(localWechatRecord);

                            if(type==true&&state==false&&amt==false){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_100);
                                bthBalErrRecord.setErrDesc("交易类型不一致");
                            }

                            if(type==false&&state==false&&amt==true){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_001);
                                bthBalErrRecord.setErrDesc("金额不一致");
                            }

                            if(type==true&&state==false&&amt==true){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_101);
                                bthBalErrRecord.setErrDesc("交易类型金额均不一致");
                            }
                            bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                            bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                            bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                            bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                            bthPagyChkErrInfoDao.insert(bthBalErrRecord);

                        }
                        //设置本地对账流水表对账状态为已对账
                        localWechatRecord.setChkSt(Constans.CHK_STATE_01);
                        localWechatRecord.setChkRst(Constans.CHK_RST_01);
                        bthPagyLocalInfoDao.updateByPrimaryKeySelective(localWechatRecord);
                        wechatRecord.setChkAcctSt(Constans.CHK_STATE_01);
                        wechatRecord.setChkRst(Constans.CHK_RST_01);
                        bthWxFileDetDao.updateByPrimaryKeySelective(wechatRecord);
                    }
                    break;                    
                }                                                             
            } 
            //没有匹配到,设为可疑
            if(exist_flag == false){
                //微信可疑
            	wechatRecord.setChkAcctSt(Constans.CHK_STATE_01);
                wechatRecord.setChkRst(Constans.CHK_RST_02);
                wechatRecord.setDubiousFlag(Constans.DUBIOUS_FLAG_01);
                bthWxFileDetDao.updateByPrimaryKeySelective(wechatRecord);
            }                
        }
        
    
	}


	/**
	 * 通道系统可疑流水对账
	 * @param settleDate
	 * @param doubtDate
	 * @param pagySysNo
	 * @param list
     * @throws Exception
	 */
	private void systemDoubt(String settleDate, String doubtDate, String pagySysNo, List<BthChkRsltInfo> list) throws Exception {
		log.info("---------------------通道系统可疑流水对账开始---------------------");
        // 对账成功日期
        String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
        log.info("chkSuccDt:"+chkSuccDt);
		SimpleDateFormat format = new SimpleDateFormat(IfspDateTime.YYYYMMDD);
        List<BthPagyLocalInfo> resultList=new ArrayList<BthPagyLocalInfo>();
        //微信对账流水
        List<BthWxFileDet> wechatList=new ArrayList<BthWxFileDet>();
        Map<String,Object> msg = new HashMap<String,Object>();
        msg.put("settleDate", doubtDate);
        msg.put("pagySysNo", pagySysNo);
        msg.put("chkRst", Constans.CHK_RST_02);
        resultList=bthPagyLocalInfoDao.selectList("selectPagyDateAndChkstat",msg);
        log.info("---------------------通道可疑流水"+resultList.size()+"条---------------------");
        wechatList=bthWxFileDetDao.selectByDateAndStat(settleDate,Constans.CHK_STATE_00);
        log.info("---------------------微信对账流水"+wechatList.size()+"条---------------------");
       
        for(BthPagyLocalInfo systemDoubtRecord:resultList){
            boolean exist_flag = false;
            for(BthWxFileDet wechatRecord:wechatList){
                //匹配流水号看是否存在
            	//退货的按商户退款单号匹配
            	//退货的按商户退款单号匹配
            	String merOrderId=wechatRecord.getOrderNo();
            	
                if(systemDoubtRecord.getPagyPayTxnSsn().equals(merOrderId)){
                    exist_flag=true;
                    boolean checkResult = true;                        
                    boolean amt=false;
                    boolean type=false;
                    boolean state=false;
                    
                    BigDecimal wechatAmt=null;
                    String tradeSt="";

                    // 仅存在退款处理中的状态 ,其他都是成功
                    if("PROCESSING".equals(wechatRecord.getRefundSt())){
                        tradeSt="02";
                    }else {
                        tradeSt="00";
                    }

                    if("10".equals(wechatRecord.getOrderTp())){
                        //交易是支付
                        wechatAmt=wechatRecord.getOrderAmt();
                    }else{
                        //交易是是退货或撤销
                        wechatAmt=wechatRecord.getRefundAmt();
                    }
                    
                    /**比交易类型*/
                    if(!wechatRecord.getOrderTp().equals(systemDoubtRecord.getTxnChlAction())){
                    	checkResult=false;
                    	type=true;
                    }              
                    /**比状态*/
                    if(!tradeSt.equals(systemDoubtRecord.getTradeSt())){
                        checkResult=false;
                        state=true;
                    }
                    /**比金额*/
                    if(!wechatAmt.toString().equals(systemDoubtRecord.getTxnAmt().toString())){
                    	checkResult=false;
                    	amt=true;
                    }
                    
                    if(checkResult){
                    	/** 对账成功 */
                    	systemDoubtRecord.setChkSt(Constans.CHK_STATE_01);
                    	systemDoubtRecord.setChkRst(Constans.CHK_RST_00);
//                    	systemDoubtRecord.setChkDataDt(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYYMMDD));
                         bthPagyLocalInfoDao.updateByPrimaryKeySelective(systemDoubtRecord);
                         wechatRecord.setChkAcctSt(Constans.CHK_STATE_01);
                         wechatRecord.setChkRst(Constans.CHK_RST_00);
//                         wechatRecord.setChkDataDt(IfspDateTime.getYYYYMMDD());
                         bthWxFileDetDao.updateByPrimaryKeySelective(wechatRecord);
                         //插入对账结果结果表
                         BthChkRsltInfo record=new BthChkRsltInfo();
                         ReflectionUtil.copyProperties(systemDoubtRecord,record);
                         
                         record.setStlmSt(Constans.STLM_ST_00);
                         record.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                         record.setChkSt(Constans.CHK_STATE_00);
                         record.setChkRst("");
                         Map<String, Object> params = new HashMap<String, Object>();
                 		 params.put("pagyTxnSsn", systemDoubtRecord.getPagyTxnSsn());
                 		 PagyTxnInfo pagyTxnInfo = pagyTxnInfoDao.selectOne("selectPagyTxnInfoByPagyTxnSsn", params);
                 		 if(pagyTxnInfo==null){
                 			throw new IfspBizException("9999", "根据通道内部订单号查询通道核心信息失败！");
                 		 }
                         record.setOrderSsn(pagyTxnInfo.getTxnReqSsn());
                         record.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                       //解决重跑未删除对成功的对账结果表
                         record.setTpamTxnTm(wechatRecord.getChkDataDt());
                         record.setTpamTxnFeeAmt(wechatRecord.getFeeAmt().longValue());
                        // todo 如果状态是处理中  , 我们需要挂起,  暂不录入对账结果表
                        if (Constans.TRADE_ST_02.equals(systemDoubtRecord.getTradeSt())){
                            list.add(record);
                        }else {
                            bthChkRsltInfoDao.insert(record);
                        }

                    }else{
                        /** 对账不平 */
                        // -------- 对账不平, 发现状态不一致进对账不平表 modify by ljy    modify_date : 20190422       start  -------
                        if (state){
                            initChkUnequalInfo(wechatRecord, systemDoubtRecord, wechatAmt, tradeSt, chkSuccDt);
                        }else {
                            // --------       modify end   --------------------------------------------------------------------------

                            //标识原因且清算日期改为T-1插入通道对账差错表，
                            //删除通道对账结果该流水，
                            //并更新第三方对账表流水为对账失败
                            BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(systemDoubtRecord);
                            //添加差错标志
                            if(type==true&&state==false&&amt==false){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_100);
                                bthBalErrRecord.setErrDesc("交易类型不一致");
                            }

                            if(type==false&&state==false&&amt==true){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_001);
                                bthBalErrRecord.setErrDesc("金额不一致");
                            }

                            if(type==true&&state==false&&amt==true){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_101);
                                bthBalErrRecord.setErrDesc("交易类型金额均不一致");
                            }
                            bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                            bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                            bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                            bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                            bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                        }

                      //设置本地对账流水表对账状态为已对账
                        systemDoubtRecord.setChkSt(Constans.CHK_STATE_01);
                        systemDoubtRecord.setChkRst(Constans.CHK_RST_01);
                        bthPagyLocalInfoDao.updateByPrimaryKeySelective(systemDoubtRecord);
                        wechatRecord.setChkAcctSt(Constans.CHK_STATE_01);
                        wechatRecord.setChkRst(Constans.CHK_RST_01);
                        bthWxFileDetDao.updateByPrimaryKeySelective(wechatRecord);
                    }
                    break;
                }  
            }
            //连续两天都没有匹配到
            if(exist_flag == false){
                BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(systemDoubtRecord);
                bthBalErrRecord.setErrDesc("连续两天可疑");
                bthBalErrRecord.setErrTp(Constans.ERR_TP_01);
                bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                systemDoubtRecord.setChkSt(Constans.CHK_STATE_01);
                systemDoubtRecord.setChkRst(Constans.CHK_RST_03);
                bthPagyLocalInfoDao.updateByPrimaryKeySelective(systemDoubtRecord);
            }
        }
        
    
	}
	
	/**
	 * 微信可疑流水对账
	 * @param settleDate
	 * @param doubtDate
	 * @param pagySysNo
	 * @param list
     * @throws Exception
	 */
	private void wechatDoubt(String settleDate, String doubtDate, String pagySysNo, List<BthChkRsltInfo> list) throws Exception {
		log.info("---------------------微信可疑流水对账开始---------------------");
		// 对账成功日期
        String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
        log.info("chkSuccDt:"+chkSuccDt);
		SimpleDateFormat format = new SimpleDateFormat(IfspDateTime.YYYYMMDD);
        List<BthWxFileDet> wechatList=bthWxFileDetDao.selectByDateAndChkstat(doubtDate,Constans.CHK_RST_02);
        log.info("---------------------微信可疑流水"+wechatList.size()+"条---------------------");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pagySysNo", pagySysNo);
        param.put("settleDate", settleDate);
        param.put("state", Constans.CHK_STATE_00);
        List<BthPagyLocalInfo> localList=bthPagyLocalInfoDao.selectList("selectWxlocalInfoByDateAndStat",param);
        
        log.info("---------------------本地待对账流水"+localList.size()+"条---------------------");
        for(BthWxFileDet wechatDoubtRecord:wechatList){
            boolean exist_flag = false;
            for(BthPagyLocalInfo localWechatRecord:localList){
            	String merOrderId=wechatDoubtRecord.getOrderNo();
                //匹配交易流水号看是否存在
                if(merOrderId.equals(localWechatRecord.getPagyPayTxnSsn())){
                    exist_flag=true;
                    boolean checkResult = true; 
                    boolean amt=false;
                    boolean state=false;
                    boolean type=false;
                    BigDecimal wechatAmt=null;
                    String tradeSt="";
                    // 仅存在退款处理中的状态 ,其他都是成功
                    if("PROCESSING".equals(wechatDoubtRecord.getRefundSt())){
                        tradeSt="02";
                    }else {
                        tradeSt="00";
                    }


                    if("10".equals(wechatDoubtRecord.getOrderTp())){
                        //交易是支付
                        wechatAmt=wechatDoubtRecord.getOrderAmt();
                    }else{
                        //交易是是退货或撤销
                        wechatAmt=wechatDoubtRecord.getRefundAmt();
                    }
                    /**比交易类型*/
                    if(!wechatDoubtRecord.getOrderTp().equals(localWechatRecord.getTxnChlAction())){
                    	checkResult=false;
                    	type=true;
                    }
                  /**比状态*/
                  if(!tradeSt.equals(localWechatRecord.getTradeSt())){
                      checkResult=false;
                      state=true;
                  }
                  /**比金额*/
                  if(!wechatAmt.toString().equals(localWechatRecord.getTxnAmt().toString())){
                	  checkResult=false;                     
                	  amt=true;
                  }
                  
                  if(checkResult){
                      /** 对账成功 */
                      localWechatRecord.setChkSt(Constans.CHK_STATE_01);
                      localWechatRecord.setChkRst(Constans.CHK_RST_00);
                      bthPagyLocalInfoDao.updateByPrimaryKeySelective(localWechatRecord);
                      wechatDoubtRecord.setChkAcctSt(Constans.CHK_STATE_01);
                      wechatDoubtRecord.setChkRst(Constans.CHK_RST_00);
                      bthWxFileDetDao.updateByPrimaryKeySelective(wechatDoubtRecord);
                      //插入对账结果结果表
                      BthChkRsltInfo record=new BthChkRsltInfo();
                      ReflectionUtil.copyProperties(localWechatRecord,record);
                      record.setStlmSt(Constans.STLM_ST_00);
                      record.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                      record.setChkSt(Constans.CHK_STATE_00);
                      record.setChkRst("");
                      Map<String, Object> params = new HashMap<String, Object>();
              		  params.put("pagyTxnSsn", localWechatRecord.getPagyTxnSsn());
              		  PagyTxnInfo pagyTxnInfo = pagyTxnInfoDao.selectOne("selectPagyTxnInfoByPagyTxnSsn", params);
              		  if(pagyTxnInfo==null){
              			throw new IfspBizException("9999", "根据通道内部订单号查询通道核心信息失败！");
              		  }
                      record.setOrderSsn(pagyTxnInfo.getTxnReqSsn());
                      record.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                      record.setTpamTxnFeeAmt(wechatDoubtRecord.getFeeAmt().longValue());
                      // todo 如果状态是处理中  , 我们需要挂起,  暂时不录入对账结果表
                      if (Constans.TRADE_ST_02.equals(localWechatRecord.getTradeSt())){
                          list.add(record);
                      }else {
                          bthChkRsltInfoDao.insert(record);
                      }
                  }else{
                      // -------- 对账不平, 发现状态不一致进对账不平表 modify by ljy    modify_date : 20190422       start  -------
                      if (state){
                          initChkUnequalInfo(wechatDoubtRecord, localWechatRecord, wechatAmt, tradeSt, chkSuccDt);
                      }else {
                          // --------       modify end   --------------------------------------------------------------------------
                          BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(wechatDoubtRecord);
                          //添加差错标志
                          if(type==true&&state==false&&amt==false){
                              bthBalErrRecord.setErrTp(Constans.ERR_TP_100);
                              bthBalErrRecord.setErrDesc("交易类型不一致");
                          }

                          if(type==false&&state==false&&amt==true){
                              bthBalErrRecord.setErrTp(Constans.ERR_TP_001);
                              bthBalErrRecord.setErrDesc("金额不一致");
                          }
                          if(type==true&&state==false&&amt==true){
                              bthBalErrRecord.setErrTp(Constans.ERR_TP_101);
                              bthBalErrRecord.setErrDesc("交易类型金额均不一致");
                          }
                          bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                          bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                          bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                          bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                          bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                      }
                      //设置本地对账流水表对账状态为已对账
                      localWechatRecord.setChkSt(Constans.CHK_STATE_01);
                      localWechatRecord.setChkRst(Constans.CHK_RST_01);
                      bthPagyLocalInfoDao.updateByPrimaryKeySelective(localWechatRecord);
                      wechatDoubtRecord.setChkAcctSt(Constans.CHK_STATE_01);
                      wechatDoubtRecord.setChkRst(Constans.CHK_RST_01);
                      bthWxFileDetDao.updateByPrimaryKeySelective(wechatDoubtRecord);
                  } 
                  break;
              }  
            }
            //连续两天都没有匹配到
            if(exist_flag == false){
                BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(wechatDoubtRecord);
                bthBalErrRecord.setErrDesc("连续两天可疑");
                bthBalErrRecord.setErrTp(Constans.ERR_TP_02);
                bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                wechatDoubtRecord.setChkAcctSt(Constans.CHK_STATE_01);
                wechatDoubtRecord.setChkRst(Constans.CHK_RST_03);
                bthWxFileDetDao.updateByPrimaryKeySelective(wechatDoubtRecord);
            }                
        }
        
    
	}

    /**
     * 初始化通道对账不平记录表
     * @param wxRecord
     * @param localRecord
     * @param wechatAmt
     * @param tradeSt
     * @param chkUeqDate
     * @throws ParseException
     */
    private void initChkUnequalInfo(BthWxFileDet wxRecord, BthPagyLocalInfo localRecord, BigDecimal wechatAmt, String tradeSt, String chkUeqDate) throws ParseException {
        BthChkUnequalInfo unEqRecord = new BthChkUnequalInfo();
        unEqRecord.setPagyPayTxnSsn(localRecord.getPagyPayTxnSsn());
        unEqRecord.setPagyPayTxnTm(localRecord.getPagyPayTxnTm());
        unEqRecord.setPagySysNo(localRecord.getPagySysNo());
        unEqRecord.setTxnAmt(localRecord.getTxnAmt());
        unEqRecord.setTxnType(localRecord.getTxnChlAction());
        unEqRecord.setTxnStat(localRecord.getTradeSt());
        unEqRecord.setTpamTxnSsn(wxRecord.getOrderNoWx());
        unEqRecord.setTpamTxnTm(wxRecord.getTxnTm());
        unEqRecord.setTpamTxnAmt(Long.parseLong(wechatAmt.toString()));
        unEqRecord.setTpamTxnType(wxRecord.getOrderTp());
        unEqRecord.setTpamTxnStat(tradeSt);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pagyTxnSsn", localRecord.getPagyTxnSsn());
        PagyTxnInfo pagyTxnInfo = pagyTxnInfoDao.selectOne("selectPagyTxnInfoByPagyTxnSsn", param);
        if(IfspDataVerifyUtil.isBlank(pagyTxnInfo)||IfspDataVerifyUtil.isBlank(pagyTxnInfo.getTxnReqSsn())){
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "根据通道内部订单号查询通道核心信息失败！");
        }
        unEqRecord.setOrderSsn(pagyTxnInfo.getTxnReqSsn());

        unEqRecord.setProcSt(Constans.PROC_ST_01);
        unEqRecord.setProcDesc("");
        unEqRecord.setChkUeqDate(IfspDateTime.strToDate(chkUeqDate,IfspDateTime.YYYY_MM_DD));
        unEqRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
        unEqRecord.setLstUpdTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));

        bthChkUnequalInfoDao.insert(unEqRecord);
    }


}
