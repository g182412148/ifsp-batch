package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.vo.CacheMchtContInfo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.SynchronizeAccService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class SynchronizeAccServiceImpl implements SynchronizeAccService {

    @Autowired
    private SettleAccModTaskDao settleAccModTaskDao;

    @Autowired
    private BthMerInAccDao bthMerInAccDao;

    @Autowired
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    @Autowired
    private BthSetCapitalDetailDao bthSetCapitalDetailDao;

    @Resource
    private MchtOrgRelDao mchtOrgRelDao;               // 商户组织关联信息

    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息

    private static BigDecimal ONE_HUNDRED = new BigDecimal(100);


    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public CommonResponse synchronizeSettleAcc() throws Exception {
        List<SettleAccModTask> toSynList = this.getDataList();
        Iterator<SettleAccModTask> iterator = toSynList.iterator();
//        List<BthMerInAcc> accUpList = new ArrayList<>();
//        List<BthMerInAcc> accInList = new ArrayList<>();
        while (iterator.hasNext())
        {

            List<BthMerInAcc> accUpList = new ArrayList<>();
            List<BthMerInAcc> accInList = new ArrayList<>();
            Set<String> outAcctNo = new HashSet<String>();
            SettleAccModTask modTask = iterator.next();
            if(Constans.SETTL_ACCT_TYPE_PLAT.equals(modTask.getSettlAcctTypeOld())&& Constans.SETTL_ACCT_TYPE_CROSS.equals(modTask.getSettlAcctType())){//本行转他行
               //查询出未结算的汇总数据
                List<BthMerInAcc> bthMerInAccList = this.getMerInacc(modTask);
                for(BthMerInAcc bthMerInAcc:bthMerInAccList){
                    if(IfspDataVerifyUtil.isBlank(bthMerInAcc.getOtherSetlFee())||(new BigDecimal(bthMerInAcc.getInAcctAmt()).compareTo(new BigDecimal(0) ))==0) {

                        //手续费
                        BigDecimal otherSettFee = calcMerFeeOtherSet(new BigDecimal(bthMerInAcc.getInAcctAmt()), bthMerInAcc.getChlMerId());
                        bthMerInAcc.setOtherSetlFee(otherSettFee.toString());
                        bthMerInAcc.setInAcctAmt(new BigDecimal(bthMerInAcc.getInAcctAmt()).subtract(otherSettFee).toString());
                        bthMerInAcc.setBrno("1");
                        accUpList.add(bthMerInAcc);
                        //查询收单机构号

                        MchtOrgRel mchtOrgRel = this.getMerOrgId(modTask);
//                bthMerInAcc.setInAcctNo(null);
//                bthMerInAcc.setInAcctName(null);
//                bthMerInAcc.setInAcctNoOrg(mchtOrgRel.getOrgId());
//                // 入账流水号
//                bthMerInAcc.setTxnSsn(IfspId.getUUID(32));
//                bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_FEE_2);
//                // 入账状态  1- 待入账  2- 入账成功  3- 入账失败
//                bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_PRE);
//                // 处理状态  0-未处理  1-处理中 2-处理成功 3-处理失败(参考清分表)
//                bthMerInAcc.setHandleState(Constans.HANDLE_STATE_PRE);
//                bthMerInAcc.setHandleMark("未处理");
//                bthMerInAcc.setLendFlag(Constans.LEND_FEE_INCOME);
//                bthMerInAcc.setInAcctAmt(otherSettFee.toString());
//                bthMerInAcc.setEntryType("18");
//                bthMerInAcc.setTxnAmt(null);
//                bthMerInAcc.setTxnCount(0);
//                bthMerInAcc.setFeeAmt(null);
//                bthMerInAcc.setBrno("0");

                        BthMerInAcc bthMerInAccFee = dealSumObj(bthMerInAcc, mchtOrgRel);
                        accInList.add(bthMerInAccFee);
                    }else{
                        bthMerInAcc.setBrno("1");
                        accUpList.add(bthMerInAcc);
                    }
                }

            }else if(Constans.SETTL_ACCT_TYPE_CROSS.equals(modTask.getSettlAcctTypeOld())&& Constans.SETTL_ACCT_TYPE_PLAT.equals(modTask.getSettlAcctType())){//他行转本行)

                //查询出未结算的汇总数据
                List<BthMerInAcc> bthMerInAccList = this.getMerInacc(modTask);
                for(BthMerInAcc bthMerInAcc:bthMerInAccList){
                    //bthMerInAcc.setInAcctAmt(new BigDecimal(bthMerInAcc.getInAcctAmt()).add(new BigDecimal(bthMerInAcc.getOtherSetlFee())).toString());
                    //bthMerInAcc.setOtherSetlFee(null);
                    bthMerInAcc.setBrno("0");
                    accUpList.add(bthMerInAcc);
                    outAcctNo.add(bthMerInAcc.getOutAcctNo());
                }
            }
            this.updateBthMerInAccBatch(accUpList);
            this.updateBthMerInAccbyOut(outAcctNo);
            this.insertBthMerInAcc(accInList);

            this.updateMerInAccDtl(modTask);
            this.updateCapitalDetail(modTask);
            this.updateMerInAcc(modTask);
            this.updateDealStatus(modTask);
        }

        //任务每天可重复执行，默认返回为成功
        CommonResponse response = new CommonResponse();
        response.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        response.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

        return response;
    }

    /**
     * 更新商户入账明细表中结算账号 ： 结算状态：失败 or
     * @param accModTask
     */
    public void updateMerInAccDtl(SettleAccModTask accModTask)
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("newAcctNo", accModTask.getSettlAcctNo());
        params.put("newAcctName",accModTask.getSettlAcctName());

        params.put("chlMerId", accModTask.getMchtId());
        params.put("stlStatus",Constans.SETTLE_STATUS_SUCCESS_CLEARING);   //已结算成功的不修改

        this.bthMerInAccDtlDao.update("updateMerInAccDtlStlAcctbyMerid",params);
    }

    /**
     * 更新清分表中结算账户信息
     * @param accModTask
     */
    public void  updateCapitalDetail(SettleAccModTask accModTask)
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("newAcctNo", accModTask.getSettlAcctNo());
        params.put("newAcctName",accModTask.getSettlAcctName());
        params.put("newAccType",accModTask.getSettlAcctType());   //新账户类型 0 - 本行 1-他行
        params.put("newAcctOrg",accModTask.getSettlAcctOrgId());  //开户机构（行）号

        params.put("merId", accModTask.getMchtId());
        params.put("dealResult",Constans.DEAL_RESULT_SUCCESS);   //已结算成功的不修改
        params.put("oldAccNo",accModTask.getSettlAcctNoOld());

        this.bthSetCapitalDetailDao.update("updateCapitalDetailStlAccByMerid",params);

    }

    /**
     * 修改商户入账汇总表结算账户信息
     * @param accModTask
     */
    public void updateMerInAcc(SettleAccModTask accModTask)
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("newAcctNo", accModTask.getSettlAcctNo());
        params.put("newAcctName",accModTask.getSettlAcctName());
        params.put("newAccType",accModTask.getSettlAcctType());   //新账户类型 0 - 本行 1-他行
        params.put("newAcctOrg",accModTask.getSettlAcctOrgId());  //开户机构（行）号

        params.put("merId", accModTask.getMchtId());
        params.put("handleState",Constans.HANDLE_STATE_IN);   //已结算成功的不修改
        params.put("oldAcctNo", accModTask.getSettlAcctNoOld());

        this.bthSetCapitalDetailDao.update("updateMerInAccStlAcctbyMerid",params);
    }

    /**
     * 更新“结算账号修改表处理状态”
     * @param accModTask
     */
    public void updateDealStatus(SettleAccModTask accModTask)
    {
        //更新状态为已处理
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id", accModTask.getId());
        params.put("status", Constans.ACCT_SYN_STATUS_DONE);   //未同步数据

        settleAccModTaskDao.update("updateStautsById",params);
    }

    /**
     * 查询“结算账号修改表”未处理数据
     * @return
     */
    public List<SettleAccModTask> getDataList()
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("status", Constans.ACCT_SYN_STATUS_UNDO);   //未同步数据

        List<SettleAccModTask> toSynList = settleAccModTaskDao.selectList("selectModifiedAccByStatus",params);

        return toSynList;
    }

    /**
     * 查询汇总数据
     * @return
     */
    public List<BthMerInAcc> getMerInacc(SettleAccModTask accModTask)
    {
        Map<String,Object> params = new HashMap<String,Object>();

        params.put("merId", accModTask.getMchtId());
        //params.put("handleState",Constans.HANDLE_STATE_IN);   //已结算成功的不修改
        params.put("oldAcctNo", accModTask.getSettlAcctNoOld());

        List<BthMerInAcc> bthMerInAccList = bthMerInAccDao.selectList("selectBthMerInAccByInAcctNO",params);

        return bthMerInAccList;
    }

    /**
     * 查询收单机构
     * @return
     */
    public MchtOrgRel getMerOrgId(SettleAccModTask accModTask)
    {
        Map<String,Object> params = new HashMap<String,Object>();

        params.put("merId", accModTask.getMchtId());

        MchtOrgRel mchtOrgRel = mchtOrgRelDao.selectOne("selectOrgByMchtId",params);

        return mchtOrgRel;
    }
    /**
     * 根据交易金额计算他行手续费
     *

     * @return
     */
    private BigDecimal calcMerFeeOtherSet (BigDecimal txnAmt, String merId)
    {
        BigDecimal merFee = new BigDecimal(0);
        txnAmt = txnAmt.multiply(ONE_HUNDRED);//转为分

        MchtContInfo merSettleInfo = getMerStlInfo(merId);
        if (merSettleInfo == null)
        {
            //没配置费率信息,不收手续费
            return merFee;
        }

        String rateCalcType = merSettleInfo.getOtherSettFeeType();
        if (Constans.COMM_TYPE_FIX_AMT.equals(rateCalcType))
        {
            //按固定金额返佣
            if (merSettleInfo.getOtherSettFee() != null)
            {
                merFee = merSettleInfo.getOtherSettFee().multiply(ONE_HUNDRED);   //参数单位为元
                if (txnAmt.compareTo(merFee) < 0)
                {
                    log.info(
                            "商户[" + merId + "]行内手续费[" + merFee + "]大于支付金额[" + txnAmt + "] , 手续费金额最多只收取订单金额, 即为 [" + txnAmt + "]分 .");
                    merFee = txnAmt;
                }
            }
        }
        else if (Constans.COMM_TYPE_BY_RATE.equals(rateCalcType))
        {
            //按比例返佣
            if (merSettleInfo.getOtherSettFee() != null)
            {
                merFee = merSettleInfo.getOtherSettFee().multiply(txnAmt).divide(ONE_HUNDRED);
                if (merSettleInfo.getMaxOtherSettFee() != null)
                {
                    //大于最大手续费
                    if (merFee.compareTo(merSettleInfo.getMaxOtherSettFee().multiply(ONE_HUNDRED)) == 1)
                    {
                        merFee = merSettleInfo.getMaxOtherSettFee().multiply(ONE_HUNDRED);
                    }
                }

                if (merSettleInfo.getMinOtherSettFee() != null)
                {
                    //小于最小手续费
                    if (merFee.compareTo(merSettleInfo.getMinOtherSettFee().multiply(ONE_HUNDRED)) == -1)
                    {
                        merFee = merSettleInfo.getMinOtherSettFee().multiply(ONE_HUNDRED);
                    }
                }

                //手续费不能大于交易金额
                if (txnAmt.compareTo(merFee) < 0)
                {
                    log.info(
                            "商户[" + merId + "]行内手续费[" + merFee + "]大于支付金额[" + txnAmt + "] , 手续费金额最多只收取订单金额, 即为 [" + txnAmt + "]分 .");
                    merFee = txnAmt;
                }
            }
        }


        //四舍五入，取整
        merFee = merFee.setScale(0, BigDecimal.ROUND_HALF_UP).divide(ONE_HUNDRED);

        return merFee;
    }

    private BthMerInAcc dealSumObj(BthMerInAcc bthMerInAccOrg, MchtOrgRel mchtOrgRel)
    {

        BthMerInAcc bthMerInAcc = new BthMerInAcc();
        // 本行卡
        bthMerInAcc.setBrno(Constans.SETTL_ACCT_TYPE_PLAT);
        // 清算日期
        bthMerInAcc.setDateStlm(bthMerInAccOrg.getDateStlm());
        // 入账状态  1- 待入账  2- 入账成功  3- 入账失败
        bthMerInAcc.setInAcctStat(Constans.IN_ACC_STAT_PRE);
        // 处理状态  0-未处理  1-处理中 2-处理成功 3-处理失败(参考清分表)
        bthMerInAcc.setHandleState(Constans.HANDLE_STATE_PRE);
        bthMerInAcc.setHandleMark("未处理");


        // 入账流水号
        bthMerInAcc.setTxnSsn(IfspId.getUUID(32));
        // 商户号
        bthMerInAcc.setChlMerId(bthMerInAccOrg.getChlMerId());
        // 商户名
        bthMerInAcc.setChlMerName(bthMerInAccOrg.getChlMerName());
        // 二级商户号
        bthMerInAcc.setChlSubMerId(bthMerInAccOrg.getChlSubMerId());
        // 二级商户名
        bthMerInAcc.setChlSubMerName(bthMerInAccOrg.getChlSubMerName());
        // 出账账户账号
        bthMerInAcc.setOutAcctNo(bthMerInAccOrg.getOutAcctNo());
        // 出账账户名称
        bthMerInAcc.setOutAcctName(bthMerInAccOrg.getOutAcctName());
        // 出账账户机构
        bthMerInAcc.setOutAcctNoOrg(bthMerInAccOrg.getOutAcctNoOrg());
        // 借方标记
        if (IfspDataVerifyUtil.isBlank(bthMerInAccOrg.getOutAcctNo()))
        {
            bthMerInAcc.setBorrowFlag(Constans.BORROW_FEE_TO_BE_ALLOCATED);
            if (Constans.ENTRY_TYPE_FEE_PAY_SD_ORG.equals(bthMerInAccOrg.getEntryType())
                    || Constans.ENTRY_TYPE_FEE_PAY_OPEN_ORG.equals(bthMerInAccOrg.getEntryType())
                    || Constans.ENTRY_TYPE_FEE_PAY_OPERATE_ORG.equals(bthMerInAccOrg.getEntryType()))
            {
                bthMerInAcc.setBorrowFlag(Constans.BORROW_FEE_EXPENSE);
            }
        }
        else
        {
            bthMerInAcc.setBorrowFlag(Constans.BORROW_ALLOCATED_ACCT);
        }


        // 入账账户账号
        bthMerInAcc.setInAcctNo(null);
        // 入账账户名称
        bthMerInAcc.setInAcctName(null);
        // 入账账户机构
        bthMerInAcc.setInAcctNoOrg(mchtOrgRel.getOrgId());
        // 贷方标记他行手续费
        bthMerInAcc.setLendFlag(Constans.LEND_FEE_INCOME);


        // 入账金额
        bthMerInAcc.setInAcctAmt(String.valueOf(bthMerInAccOrg.getOtherSetlFee()));

        // 设置入账类型
        bthMerInAcc.setInAcctType(Constans.IN_ACCT_TYPE_FEE_2);

        // 批次号
        bthMerInAcc.setBatchNo(bthMerInAccOrg.getBatchNo());
        //入账分录类型
        bthMerInAcc.setEntryType("18");

        return bthMerInAcc;
    }

    private  MchtContInfo getMerStlInfo(String mchtId) {

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

    /**
     *
     * @param accUpList
     */
    private void updateBthMerInAccBatch(List<BthMerInAcc> accUpList)
    {
        if(accUpList!=null&&accUpList.size()>0) {
            log.info("====================>>更新清分汇总表 start<<====================");

            // 汇总表MAP
            Map<String, Object> bthMerInAcc = new HashMap<>(5);
            // 查询参数
            bthMerInAcc.put("accUpList", accUpList);
            //capitalMap.put("merIdList", merIdList);
            bthSetCapitalDetailDao.update("updateBthMerInAccBatch", bthMerInAcc);
            log.info("====================>>更新清分汇总表 end<<====================");
        }
    }

    private void updateBthMerInAccbyOut(Set<String> accUpList)
    {
        if(accUpList!=null&&accUpList.size()>0){
        log.info("====================>>更新清分汇总表 start<<====================");

        // 汇总表MAP
        Map<String, Object> bthMerInAcc = new HashMap<>(5);
        // 查询参数
        bthMerInAcc.put("accUpList", accUpList.toArray());
        //capitalMap.put("merIdList", merIdList);
        bthSetCapitalDetailDao.update("updateBthMerInAccbyOut", bthMerInAcc);
        log.info("====================>>更新清分汇总表 end<<====================");
        }
    }

    private void insertBthMerInAcc(List<BthMerInAcc>  accList)
    {
        int count =0;
        if(accList!=null&&accList.size()>0)
        {
            count = bthMerInAccDao.insertBatch(accList);
        }
        log.info("=================批量插入汇总记录 end，入库数:【{}】=================", count);
    }

}






