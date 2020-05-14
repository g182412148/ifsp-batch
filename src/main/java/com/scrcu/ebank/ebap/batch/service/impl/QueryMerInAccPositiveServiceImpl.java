package com.scrcu.ebank.ebap.batch.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.QueryMchtsMerInAccRequest;
import com.scrcu.ebank.ebap.batch.bean.response.QueryMerInAccPosiResponse;
import com.scrcu.ebank.ebap.batch.bean.vo.SubMchtInfoResultVo;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.msg.MchtMsg;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.PayOrderInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.QueryMerInAccPositiveService;
import com.scrcu.ebank.ebap.batch.soaclient.MchtCenterSoaClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author: ljy
 * @create: 2018-08-25 12:14
 */
@Service
@Slf4j
public class QueryMerInAccPositiveServiceImpl implements QueryMerInAccPositiveService {

    /**
     * 商户入账明细信息
     */
    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    @Resource
    private PayOrderInfoDao payOrderInfoDao;

    @Resource
    private PaySubOrderInfoDao paySubOrderInfoDao;

    @Resource
    private MchtCenterSoaClientService mchtCenterSoaClientService;

    @Override
    public QueryMerInAccPosiResponse queryMerInAccPositive(QueryMchtsMerInAccRequest request) {

        QueryMerInAccPosiResponse response = new QueryMerInAccPosiResponse();
        Map<String,Object> map = new HashMap<>();
        map.put("inAcctDate",request.getInAcctDate());
        map.put("merId",request.getMerId());
        // 汇总明细表
        BthMerInAccMchts info = bthMerInAccDtlDao.selectByMerTxn(map);

        // ------------------------------------------- 查询下级商户是否有返佣 start------------------------------------------

        // 根据下级商户号去查询 该入账日期有没有返佣金额
        List<BthMerInAccDtl> commAmts = bthMerInAccDtlDao.selectList("queryByMchtIdAndInaccDate", map);
        BigDecimal v = new BigDecimal(0);
        for (BthMerInAccDtl commAmt : commAmts) {
            v= v.add(IfspDataVerifyUtil.isBlank(commAmt.getCommissionAmt())? BigDecimal.ZERO:new BigDecimal(commAmt.getCommissionAmt()));
        }
        // ------------------------------------------- 查询下级商户是否有返佣 end--------------------------------------------

        BthMerInAccMchts info2 = null;
        if (IfspDataVerifyUtil.isBlank(info)&& v.compareTo(BigDecimal.ZERO) != 0){
            info2 = new BthMerInAccMchts();
            info2.setRecvCommissionAmt(v);
        }


        if (IfspDataVerifyUtil.isNotBlank(info)){
            // 设置下级向上级返佣金额
            info.setRecvCommissionAmt(v);

            BigDecimal mchtCouponAmt = new BigDecimal(0);
            BigDecimal bankCouponAmt = new BigDecimal(0);
            BigDecimal returnAmt = new BigDecimal(0);

            // 查询明细表
            List<BthMerInAccDtl> dtls = bthMerInAccDtlDao.selectList("selectByInAccDateMerId", map);
            for (BthMerInAccDtl dtl : dtls) {
                // 线上查询子订单表
                if (Constans.TXN_TYPE_ONLINE_PAY.equals(dtl.getTxnType())||Constans.TXN_TYPE_ONLINE_REFUND.equals(dtl.getTxnType())){
                    PaySubOrderInfo paySubOrderInfo = paySubOrderInfoDao.selectByPrimaryKey(dtl.getTxnSeqId());
                    bankCouponAmt = bankCouponAmt.add(new BigDecimal(IfspDataVerifyUtil.isBlank(paySubOrderInfo.getBankCouponAmt()) ? "0" : paySubOrderInfo.getBankCouponAmt()));
                    mchtCouponAmt = mchtCouponAmt.add(new BigDecimal(IfspDataVerifyUtil.isBlank(paySubOrderInfo.getMchtCouponAmt()) ? "0" : paySubOrderInfo.getMchtCouponAmt()));

                }else { // 线下查询订单表
                    PayOrderInfo payOrderInfo = payOrderInfoDao.queryByTxnSeqId(dtl.getTxnSeqId());
                    bankCouponAmt = bankCouponAmt.add(new BigDecimal(IfspDataVerifyUtil.isBlank(payOrderInfo.getBankCouponAmt()) ? "0" : payOrderInfo.getBankCouponAmt()));
                    mchtCouponAmt = mchtCouponAmt.add(new BigDecimal(IfspDataVerifyUtil.isBlank(payOrderInfo.getMchtCouponAmt()) ? "0" : payOrderInfo.getMchtCouponAmt()));
                }

                if (Constans.ORDER_TYPE_RETURN.equals(dtl.getOrderType())){
                    returnAmt = returnAmt.add(new BigDecimal(dtl.getTxnAmt()));
                }
            }

            info.setBankCouponAmt(bankCouponAmt.movePointLeft(2));
            info.setMchtCouponAmt(mchtCouponAmt.movePointLeft(2));
            info.setReturnAmt(returnAmt.movePointLeft(2));

            // 明细表关联订单(线下)
            List<BthMerInAccMchtsDtl> dtlList = bthMerInAccDtlDao.selectByMerTxnDtl(map);
            // 明细表关联子订单表订单(线上)
            List<BthMerInAccMchtsDtl> dtlList2 = bthMerInAccDtlDao.selectByMerTxnDtlOnline(map);
            dtlList.addAll(dtlList2);

            // 按照订单时间排序
            Collections.sort(dtlList, new Comparator<BthMerInAccMchtsDtl>() {
                @Override
                public int compare(BthMerInAccMchtsDtl o1, BthMerInAccMchtsDtl o2) {
                    return (int)(Long.valueOf(o1.getOrderTm())-Long.valueOf(o2.getOrderTm()));
                }
            });

            // 总页数
            Map<String ,Integer> p = new HashMap<>();
            p.put("totalPage",0);
            List<BthMerInAccMchtsDtl> pager = pager(request.getPagnParams().getPageSize(), request.getPagnParams().getPageNo(), dtlList , p);


            PagnResult pagnResult = new PagnResult();
            pagnResult.setPageNo(request.getPagnParams().getPageNo());
            pagnResult.setPageCount(p.get("totalPage"));
            pagnResult.setRecordTotal(dtlList.size());

            response.setPagnResult(pagnResult);
            response.setDtlList(pager);
            response.setBthMerInAccMchts(info);
        }else {

            if (info2!=null){
                response.setBthMerInAccMchts(info2);
            }

            PagnResult pagnResult = new PagnResult();
            pagnResult.setPageNo(0);
            pagnResult.setPageCount(0);
            pagnResult.setRecordTotal(0);
            response.setPagnResult(pagnResult);
            response.setDtlList(new ArrayList<>());

        }


        return response;
    }

    @Override

    public QueryMerInAccPosiResponse queryMerInAccPositiveDown(QueryMchtsMerInAccRequest request) {
                QueryMerInAccPosiResponse response = new QueryMerInAccPosiResponse();
                Map<String,Object> map = new HashMap<>();
                map.put("inAcctDate",request.getInAcctDate());
                map.put("merId",request.getMerId());
                map.put("beginDate",request.getBeginDate());
                map.put("endDate",request.getEndDate());
                // 明细表关联订单(线下)
                List<BthMerInAccMchtsDtl> dtlList = bthMerInAccDtlDao.selectByMerTxnDtlDown(map);
                // 明细表关联子订单表订单(线上)
                List<BthMerInAccMchtsDtl> dtlList2 = bthMerInAccDtlDao.selectByMerTxnDtlOnlineDown(map);
                dtlList.addAll(dtlList2);

                // 按照订单时间排序
                Collections.sort(dtlList, new Comparator<BthMerInAccMchtsDtl>() {
                    @Override
                    public int compare(BthMerInAccMchtsDtl o1, BthMerInAccMchtsDtl o2) {
                        return (int) (Long.valueOf(o1.getOrderTm()) - Long.valueOf(o2.getOrderTm()));
                    }
                });
                response.setDtlList(dtlList);
        return response;
    }


    public  List<BthMerInAccMchtsDtl>  pager(int pageSize,int pageIndex,List list , Map<String ,Integer> p){
        //使用list 中的sublist方法分页
        List<BthMerInAccMchtsDtl> dataList = new ArrayList<>();

        // 每页显示多少条记录
        int currentPage; //当前第几页数据
        // 一共多少条记录
        int totalRecord = list.size();
        // 一共多少页
        int totalPage = 0;
        if (0 == pageSize) {
            totalPage = 1;
        }else {
            totalPage = totalRecord % pageSize;
            if (totalPage > 0) {
                totalPage = totalRecord / pageSize + 1;
            } else {
                totalPage = totalRecord / pageSize;
            }
        }


        System.out.println("总页数:" + totalPage);
        p.put("totalPage",totalPage);

        // 当前第几页数据
        currentPage = totalPage < pageIndex ? totalPage : pageIndex;

        // 起始索引
        int fromIndex = pageSize * (currentPage - 1);

        // 结束索引
        int toIndex = pageSize * currentPage > totalRecord ? totalRecord : pageSize * currentPage;
        try{
            dataList = list.subList(fromIndex, toIndex);
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return dataList;
    }


}
