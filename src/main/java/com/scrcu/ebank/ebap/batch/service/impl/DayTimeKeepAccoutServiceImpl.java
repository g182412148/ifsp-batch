package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.DateUtil;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.DayTimeKeepAcctVo;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.request.DayTimeKeepAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OnceKeepAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.response.KeepAccResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.ReRunFlagEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RealTmFlagEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.common.utils.UUIDCreator;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.service.DayTimeKeepAccoutService;
import com.scrcu.ebank.ebap.batch.service.FeeCalcService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 日间订单初始化记账表任务
 *
 * @author ljy
 */
@Slf4j
@Service
public class DayTimeKeepAccoutServiceImpl implements DayTimeKeepAccoutService {

    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    @Resource
    private KeepAccSoaService keepAccSoaService;

    @Resource
    private FeeCalcService feeCalcService;

    @Override
    public KeepAccResponse daytimeKeepAccount(DayTimeKeepAcctRequest request) {

        KeepAccResponse response = new KeepAccResponse();

        // 记账信息
        List<DayTimeKeepAcctVo> keepAcctList = request.getKeepAcctList();

        // 异步记账 (场景 : 正交易、本代他反交易), 记账表初始化记录, 状态为 00 - 待记账 , 等待定时任务扫描去记账 , 同步返回订单中心受理成功
        if (Constans.KEEP_ACCT_FLAG_ASYNC.equals(request.getIsSync())) {
            return getAsyncRst(request, response, keepAcctList);
            // 同步记账 ,记账表初始化记录 , 状态为 01 - 记账中
        }else if(Constans.KEEP_ACCT_FLAG_SYNC.equals(request.getIsSync())) {
            return getSyncRst(response, keepAcctList);
        }else {
            log.info("====================>>记账标志["+request.getIsSync()+"]无法识别,受理失败<<====================");
            response.setRespCode(RespEnum.RESP_FAIL.getCode());
            response.setRespMsg("无法识别记账标志");
            return response;
        }


    }

    @Override
    public KeepAccResponse onceKeepAccount(OnceKeepAcctRequest request) {
        //响应报文
        KeepAccResponse response = new KeepAccResponse();
        String HDTellrSeqNum = null;
        Map<String ,Object> info = new HashMap<>(5);
        info.put("subOrderSsn",request.getKeepAccInfo().getSubOrderSsn());
        info.put("realTmFlag" ,request.getKeepAccInfo().getRealTmFlag());
        info.put("feeAmt",request.getKeepAccInfo().getFeeAmt());
        info.put("commissionAmt",request.getKeepAccInfo().getCommissionAmt());
        //1.更新记账状态（加状态条件）
        KeepAccInfo keepAccInfoUpd = new KeepAccInfo();
        keepAccInfoUpd.setCoreSsn(request.getKeepAccInfo().getCoreSsn());
        keepAccInfoUpd.setState(Constans.KEEP_ACCOUNT_STAT_IN);
        //金额为0时更新为成功，直接返回成功
        if(request.getKeepAccInfo().getTransAmt()<=0){
            keepAccInfoUpd.setState(Constans.KEEP_ACCOUNT_STAT_PRE);
            int keepCount = keepAccInfoDao.updateByPrimaryKeyAndSt(keepAccInfoUpd);
            //订单已处理直接返回
            if(keepCount<=0){
                response.setRespCode(RespEnum.RESP_PROCESSED.getCode());
                response.setRespMsg(RespEnum.RESP_PROCESSED.getDesc());
                return response;
            }else{
                response.setRespCode(RespEnum.RESP_SUCCESS.getCode());
                response.setRespMsg("记账成功");
                response.setRespData(info);
                return response;
            }
        }else{
            int keepCount = keepAccInfoDao.updateByPrimaryKeyAndSt(keepAccInfoUpd);
            //订单已处理直接返回
            if(keepCount<=0){
                response.setRespCode(RespEnum.RESP_PROCESSED.getCode());
                response.setRespMsg(RespEnum.RESP_PROCESSED.getDesc());
                return response;
            }
        }
//        Map<String ,Object> respInfo = new HashMap<>();
        SoaParams params = new SoaParams();
        // 组装参数 , keepAccInfo初始化核心流水
        initParam(params, request.getKeepAccInfo());
        //2.调用通道记账接口
        SoaResults results = keepAccSoaService.keepAcc(params);
        HDTellrSeqNum =  IfspDataVerifyUtil.isBlank(results.get("hDTellrSeqNum")) ? "" :  String.valueOf(results.get("hDTellrSeqNum"));

        info.put("hDTellrSeqNum",HDTellrSeqNum);
        response.setRespData(info);
        //2.1处理响应结果，更新状态
        if (RespEnum.RESP_SUCCESS.getCode().equals(results.getRespCode())) {
            log.info("===================>>本行通道受理记账成功<<===========================");
            keepAccInfoUpd.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
            keepAccInfoUpd.setPagyRespCode(results.getRespCode());
            keepAccInfoUpd.setPagyRespMsg(results.getRespMsg());
            keepAccInfoUpd.setReserved2(HDTellrSeqNum);
            keepAccInfoDao.updateByPrimaryKeySelective(keepAccInfoUpd);
        } else if (RespEnum.RESP_TIMEOUT.getCode().equals(results.getRespCode())){
            log.warn("===================>>调用本行通道记账接口超时<<===========================");
            // 记为超时 , 待日终改为记账失败
            keepAccInfoUpd.setState(Constans.KEEP_ACCOUNT_STAT_TIMEOUT);
            keepAccInfoUpd.setPagyRespCode(results.getRespCode());
            keepAccInfoUpd.setPagyRespMsg(results.getRespMsg());
            keepAccInfoUpd.setReserved2(HDTellrSeqNum);
            keepAccInfoDao.updateByPrimaryKeySelective(keepAccInfoUpd);
            response.setRespCode(results.getRespCode());
            response.setRespMsg(results.getRespMsg());
            return response;

        } else {
            log.info("========================>>本行通道调用成功,记账失败,返回码[" + results.getRespCode() + "],返回信息[" + results.getRespMsg() + "]<<====================");
            keepAccInfoUpd.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);
            keepAccInfoUpd.setPagyRespCode(results.getRespCode());
            keepAccInfoUpd.setPagyRespMsg(results.getRespMsg());
            keepAccInfoUpd.setReserved2(HDTellrSeqNum);
            keepAccInfoDao.updateByPrimaryKeySelective(keepAccInfoUpd);
            response.setRespCode(results.getRespCode());
            response.setRespMsg(results.getRespMsg());
            return response;
        }
        //设置响应结果
        response.setRespData(info);
        response.setRespCode(RespEnum.RESP_SUCCESS.getCode());
        response.setRespMsg("记账成功");
        return response;
    }


    /**
     * 同步记账
     * @param response
     * @param keepAcctList
     * @return
     */
    private KeepAccResponse getSyncRst(KeepAccResponse response, List<DayTimeKeepAcctVo> keepAcctList) {
        log.info("====================>>初始化记账表开始<<====================");
        // 按照序号排序入账   order by seq 升序
        Collections.sort(keepAcctList,new Comparator<DayTimeKeepAcctVo>() {
            @Override
            public int compare(DayTimeKeepAcctVo o1, DayTimeKeepAcctVo o2) {
                return Integer.valueOf(o1.getSeq()) -Integer.valueOf(o2.getSeq());
            }
        });

        Iterator<DayTimeKeepAcctVo> iterator = keepAcctList.iterator();

        List<Map<String ,Object>> respInfo = new ArrayList<>();

        String HDTellrSeqNum = null;
        while (iterator.hasNext()){
            DayTimeKeepAcctVo keepAccVo = iterator.next();

            // 记账表登记记账明细,状态记为记账中
            KeepAccInfo keepAccInfo = initKeepInfo(keepAccVo, Constans.KEEP_ACCOUNT_STAT_IN);
            // 异步还是同步
            keepAccInfo.setIsSync(Constans.KEEP_ACCT_FLAG_SYNC);

            // 插入时判断唯一索引
            keepAccInfoDao.insertIgnoreExist(keepAccInfo);

            // 如果记账金额为0 ,  认为成功不去记账
            if (0 == keepAccInfo.getTransAmt()){
                log.info("===================>>记账发生额为0   ,  置为记账成功<<===========================");
                keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                keepAccInfoDao.updateByPrimaryKeySelective(keepAccInfo);
            }else {
                SoaParams params = new SoaParams();
                // 组装参数 , keepAccInfo初始化核心流水
                initParam(params, keepAccInfo);

                SoaResults results = keepAccSoaService.keepAcc(params);
                HDTellrSeqNum =  IfspDataVerifyUtil.isBlank(results.get("hDTellrSeqNum")) ? "" :  String.valueOf(results.get("hDTellrSeqNum"));

                Map<String ,Object> info = new HashMap<>(5);
                info.put("subOrderSsn",keepAccInfo.getSubOrderSsn());
                info.put("realTmFlag" ,keepAccInfo.getRealTmFlag());
                info.put("feeAmt",keepAccInfo.getFeeAmt());
                info.put("commissionAmt",keepAccInfo.getCommissionAmt());
                info.put("HDTellrSeqNum",HDTellrSeqNum);
                respInfo.add(info);


                if (RespEnum.RESP_SUCCESS.getCode().equals(results.getRespCode())) {
                    log.info("===================>>本行通道受理记账成功<<===========================");
                    keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                    keepAccInfo.setPagyRespCode(results.getRespCode());
                    keepAccInfo.setPagyRespMsg(results.getRespMsg());
                    keepAccInfo.setReserved2(HDTellrSeqNum);
                    keepAccInfoDao.updateByPrimaryKeySelective(keepAccInfo);
                } else if (RespEnum.RESP_TIMEOUT.getCode().equals(results.getRespCode())){
                    log.warn("===================>>调用本行通道记账接口超时<<===========================");
                    // 记为超时 , 待日终改为记账失败
                    keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_TIMEOUT);
                    keepAccInfo.setPagyRespCode(results.getRespCode());
                    keepAccInfo.setPagyRespMsg(results.getRespMsg());
                    keepAccInfo.setReserved2(HDTellrSeqNum);
                    keepAccInfoDao.updateByPrimaryKeySelective(keepAccInfo);
                    response.setRespInfo(respInfo);
                    response.setRespCode(results.getRespCode());
                    response.setRespMsg(results.getRespMsg());
                    return response;

                } else {
                    log.info("========================>>本行通道调用成功,记账失败,返回码[" + results.getRespCode() + "],返回信息[" + results.getRespMsg() + "]<<====================");
                    keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);
                    keepAccInfo.setPagyRespCode(results.getRespCode());
                    keepAccInfo.setPagyRespMsg(results.getRespMsg());
                    keepAccInfo.setReserved2(HDTellrSeqNum);
                    keepAccInfoDao.updateByPrimaryKeySelective(keepAccInfo);
                    response.setRespInfo(respInfo);
                    response.setRespCode(results.getRespCode());
                    response.setRespMsg(results.getRespMsg());
                    return response;
                }
            }

        }

        response.setRespInfo(respInfo);
        response.setRespCode(RespEnum.RESP_SUCCESS.getCode());
        response.setRespMsg("同步记账成功");
        return response;
    }

    /**
     * 异步记账
     * @param request
     * @param response
     * @param keepAcctList
     * @return
     */
    private KeepAccResponse getAsyncRst(DayTimeKeepAcctRequest request, KeepAccResponse response, List<DayTimeKeepAcctVo> keepAcctList) {
        // 批次号
        String batchNo = String.valueOf(UUIDCreator.randomUUID());

        log.info("====================>>初始化记账表开始<<====================");
        // 待记账条数记账
        short size = (short) keepAcctList.size();
        Iterator<DayTimeKeepAcctVo> iterator = keepAcctList.iterator();
        while (iterator.hasNext()){
            DayTimeKeepAcctVo next = iterator.next();
            //  记账表登记记账明细,状态记为待记账
            KeepAccInfo keepAccInfo = initKeepInfo(next,Constans.KEEP_ACCOUNT_STAT_PRE);
            // 异步还是同步
            keepAccInfo.setIsSync(Constans.KEEP_ACCT_FLAG_ASYNC);
            // 保存该批次记账笔数
            keepAccInfo.setBatchNum(size);
            // 保存批次号
            keepAccInfo.setReserved1(batchNo);
            // 插入时判断唯一索引
            keepAccInfoDao.insertIgnoreExist(keepAccInfo);
        }

        //  同步返回订单中心受理结果
        response.setRespCode(RespEnum.RESP_SUCCESS.getCode());
        response.setRespMsg("异步记账受理成功");
        log.info("====================>>初始化记账表结束<<====================");
        return response;
    }


    /**
     * 初始化记账
     * @param vo
     * @return
     */
    public KeepAccInfo initKeepInfo(DayTimeKeepAcctVo vo,String state)  {
        KeepAccInfo keepAccInfo = new KeepAccInfo();
        // 核心流水
        keepAccInfo.setCoreSsn(IfspId.getUUID(20));
        // 订单号
        keepAccInfo.setOrderSsn(vo.getOrderSsn());
        // 订单时间
        keepAccInfo.setOrderTm(vo.getOrderTm());
        // 借方账户
        keepAccInfo.setOutAccNo(vo.getOutAccNo());
        // 借方账户名
        keepAccInfo.setOutAccNoName(vo.getOutAccName());
        // 借方账户类型
        keepAccInfo.setOutAccType(vo.getOutAccType());
        // 贷方账户
        keepAccInfo.setInAccNo(vo.getInAccNo());
        // 贷方商户名
        keepAccInfo.setInAccNoName(vo.getInAccName());
        // 贷方账户类型
        keepAccInfo.setInAccType(vo.getInAccType());
        // 划账序号
        keepAccInfo.setKeepAccSeq(Short.valueOf(vo.getSeq()));
        // 记账时间
        keepAccInfo.setKeepAccTime(DateUtil.getYYYYMMDDHHMMSS());
        // 记账类型
        keepAccInfo.setKeepAccType(Constans.KEEP_ACC_TYPE_OTHER);
        // 记账状态
        keepAccInfo.setState(state);
        // 交易描述
        keepAccInfo.setTxnDesc(vo.getTxnDesc());
        // 交易币种
        keepAccInfo.setTxnCcyType(vo.getTxnCcyType());
        // 商户所在机构
        keepAccInfo.setProxyOrg(vo.getProxyOrg());
        // 子订单号
        keepAccInfo.setSubOrderSsn(vo.getSubOrderSsn());
        // 是否支持重跑 默认支持
        if (IfspDataVerifyUtil.isBlank(vo.getRerunFlag())){
            keepAccInfo.setRerunFlag(ReRunFlagEnum.RE_RUN_FLAG_TRUE.getCode());
        }else {
            keepAccInfo.setRerunFlag(vo.getRerunFlag());
        }
        // 是否实时计算手续费 默认非实时计算
        if (IfspDataVerifyUtil.isBlank(vo.getRealTmFlag())){
            keepAccInfo.setRealTmFlag(RealTmFlagEnum.REAL_TM_FALSE.getCode());
        }else {
            keepAccInfo.setRealTmFlag(vo.getRealTmFlag());
        }

        Map<String, Long> map = calcAmt(vo);


        // 先转换为BigDecimal金额 , 为了支持传的金额 带有 .0 后缀
        BigDecimal stlAmt = IfspDataVerifyUtil.isBlank(map.get("stlAmt")) ?  BigDecimal.ZERO :new BigDecimal(map.get("stlAmt"));
        BigDecimal merFee = IfspDataVerifyUtil.isBlank(map.get("merFee")) ?  BigDecimal.ZERO :new BigDecimal(map.get("merFee"));
        BigDecimal commissionFee = IfspDataVerifyUtil.isBlank(map.get("commissionFee")) ? BigDecimal.ZERO :new BigDecimal(map.get("commissionFee"));

        keepAccInfo.setTransAmt(stlAmt.longValue());
        keepAccInfo.setFeeAmt(merFee);
        keepAccInfo.setCommissionAmt(commissionFee);
        // 记账摘要
        keepAccInfo.setMemo(vo.getMemo());

        // 唯一索引
        keepAccInfo.setUniqueSsn(vo.getUniqueSsn());

        // 批次号默认设置 0 (新同步记账接口有用)
        keepAccInfo.setVerNo("0");

        return keepAccInfo;
    }

    /**
     * 计算金额
     * @param vo
     * @return
     */
    private Map<String, Long> calcAmt(DayTimeKeepAcctVo vo) {
        Map<String, Long> map = new HashMap<>();
        if (RealTmFlagEnum.REAL_TM_TREU.getCode().equals(vo.getRealTmFlag())){
            // 判断是线上还是线下 (子订单不为空说明是线上)
            if(IfspDataVerifyUtil.isNotBlank(vo.getSubOrderSsn())){
                map = feeCalcService.calcMerFee4SubOrder(vo.getSubOrderSsn());
            }else {
                map = feeCalcService.calcMerFee4Order(vo.getOrderSsn());
            }

        }else {
            map.put("stlAmt",Long.valueOf(vo.getTransAmt()));
            map.put("merFee",0L);
            map.put("commissionFee",0L);
        }
        return map;
    }


    /**
     * 组装报文
     * @param params
     * @param info
     */
    public static void initParam(SoaParams params, KeepAccInfo info) {
        //通道支付请求流水号
        params.put("pagyPayTxnSsn", info.getCoreSsn());
        //通道支付请求流水时间
        params.put("pagyPayTxnTm", IfspDateTime.getYYYYMMDDHHMMSS());
        //通道支付订单号
        params.put("pagyTxnSsn", info.getOrderSsn());
        //通道订单时间
        params.put("pagyTxnTm", info.getOrderTm());
        //借方账号
        params.put("dtAcctNo", info.getOutAccNo());
        //借方账户名称 (非必输)
        params.put("dtAcctNm", info.getOutAccNoName());
        //贷方账号
        params.put("ctAcctNo", info.getInAccNo());
        //贷方账户名称 (非必输)
        params.put("ctAcctNm", info.getInAccNoName());
        //交易描述
        params.put("txnDesc", info.getTxnDesc());
        //支付金额
        params.put("txnAmt", info.getTransAmt());
        //币种
        params.put("txnCcyType", Constans.CCY_TYPE);
        //商户所在机构 (非必输)
        params.put("proxyOrg", info.getProxyOrg());
        // 摘要
        if (IfspDataVerifyUtil.isNotBlank(info.getMemo())){
            params.put("memo",info.getMemo());
        }
    }



}
