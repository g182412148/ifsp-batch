package com.scrcu.ebank.ebap.batch.service.impl;

import com.github.pagehelper.Page;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMerInAccDtlRequest;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMerInAccRequest;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccDtlResponse;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccResponse;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.service.QueryMerInAccRstService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 商户入账汇总与详情查看
 * @author ljy
 */
@Slf4j
@Service
public class QueryMerInAccRstServiceImpl implements QueryMerInAccRstService {


    /**
     商户入账汇总信息
     */
    @Resource
    private BthMerInAccDao bthMerInAccDao;

    /**
     * 商户入账明细信息
     */
    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    /**
     * 合同表
     */
    @Resource
    private MchtContInfoDao mchtContInfoDao;

    /**
     * 商户入账列表查看
     * @param request
     * @return
     */
    @Override
    public QueryMerInAccResponse queryMerInAcc(QueryMerInAccRequest request) {
        QueryMerInAccResponse response = new QueryMerInAccResponse();

        // 根据请求参数查询商户入账汇总表
        Map<String,Object> params = new HashMap<>();
        params.put("brId",request.getBrId());
        params.put("stlmDate",request.getStlmDate());
        params.put("startDate",request.getStartDate());
        params.put("endDate",request.getEndDate());
        params.put("merId",request.getMerId());
        params.put("merSimpleName", IfspDataVerifyUtil.isBlank(request.getMerSimpleName())?null:"%"+request.getMerSimpleName()+"%");
        params.put("setlAcctNo",request.getSetlAcctNo());
        params.put("inAcctStat",request.getInAcctStat());
        params.put("txnSsn",request.getTxnSsn());
        List<BthMerInAccSumInfo> list = bthMerInAccDao.selectMerInAccSumInfo(params,request.getPagnParams());
        Page<BthMerInAccSumInfo> queryPage = (Page<BthMerInAccSumInfo>) list;
        PagnResult pagnResult = new PagnResult();
        pagnResult.setPageNo(queryPage.getPageNum());
        pagnResult.setPageCount(queryPage.getPages());
        pagnResult.setRecordTotal(queryPage.getTotal());
        response.setPagnResult(pagnResult);
        response.setSumList(list);
        return response;
    }

    /**
     * 商户入账详情查看
     * @param request
     * @return
     */
    @Override
    public QueryMerInAccDtlResponse queryMerInAccDtl(QueryMerInAccDtlRequest request) {
        QueryMerInAccDtlResponse response = new QueryMerInAccDtlResponse();


        // 根据批次号查询汇总表信息
        BthMerInAccSumInfoExtend acc = bthMerInAccDao.selectByTxnSsn(request.getTxnSsn());
        if (IfspDataVerifyUtil.isBlank(acc)){
            response.setRespCode("9999");
            response.setRespMsg("入账汇总数据不存在!!!");
            return response;
        }



        // 根据批次号查询商户明细信息 , 关联订单表得到订单时间
        List<BthMerInAccDtlInfo> dtlList = bthMerInAccDtlDao.selectByBatchNo(acc.getMerId() , acc.getBatchNo(),request.getPagnParams());
        if (IfspDataVerifyUtil.isEmptyList(dtlList)){
            response.setRespCode("9999");
            response.setRespMsg("入账明细数据不存在!!!");
            return response;
        }
        Page<BthMerInAccDtlInfo> queryPage = (Page<BthMerInAccDtlInfo>) dtlList;


        // 查询商户结算账户类型  合同表
        Map<String, Object> map = new HashMap<>();
        map.put("mchtId",acc.getMerId());
        MchtContInfo contInfo = mchtContInfoDao.selectOne("cont_selectByMchtId", map);
        if (IfspDataVerifyUtil.isBlank(contInfo.getSettlAcctType())) {
            response.setRespCode("9999");
            response.setRespMsg("商户结算账户类型不存在!!!");
            return response;
        }

        // 银行出资营销金额
        BigDecimal bankCouponSumAmt= BigDecimal.ZERO;
        // 商户出资营销金额
        BigDecimal mchtCouponSumAmt = BigDecimal.ZERO;
        // 营销总额
        BigDecimal sumCouponAmt ;
        // 汇总银行营销总额
        Iterator<BthMerInAccDtlInfo> iterator = dtlList.iterator();
        while (iterator.hasNext()){
            BthMerInAccDtlInfo next = iterator.next();
            bankCouponSumAmt = bankCouponSumAmt.add(IfspDataVerifyUtil.isBlank(next.getBankCouponAmt())? BigDecimal.ZERO: next.getBankCouponAmt());
            mchtCouponSumAmt = mchtCouponSumAmt.add(IfspDataVerifyUtil.isBlank(next.getMchtCouponAmt())? BigDecimal.ZERO: next.getMchtCouponAmt());
        }
        sumCouponAmt = bankCouponSumAmt.add(mchtCouponSumAmt);

        acc.setStlmAcctType(contInfo.getSettlAcctType());
        acc.setSumCouponAmt(sumCouponAmt);
        acc.setSumBankCouponAmt(bankCouponSumAmt);
        acc.setSumMchtCouponAmt(mchtCouponSumAmt);

        response.setRecordTotal(queryPage.getTotal());
        response.setBthMerInAccSumInfoExtend(acc);
        response.setDtlList(dtlList);
        return response;
    }
}
