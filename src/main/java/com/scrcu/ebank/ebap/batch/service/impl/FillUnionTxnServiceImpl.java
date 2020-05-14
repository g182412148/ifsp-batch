package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.ReflectionUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.FillUnionTxnService;
import com.scrcu.ebank.ebap.batch.soaclient.FillUnionTxnSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class FillUnionTxnServiceImpl implements FillUnionTxnService {

    @Resource
    private BthUnionFileDetDao bthUnionFileDetDao;

    @Resource
    private BthPagyChkErrInfoDao bthPagyChkErrInfoDao;

    @Resource
    private BthChkRsltInfoDao bthChkRsltInfoDao;

    @Resource
    private FillUnionTxnSoaService fillUnionTxnSoaService;

    @Resource
    private PayOrderInfoDao payOrderInfoDao;

    @Resource
    private KeepAccInfoDao keepAccInfoDao;


    @Resource
    private TpamCnuniopayQrcTxnInfoDao tpamCnuniopayQrcTxnInfoDao;

    @Resource
    private BthChkUnequalInfoDao bthChkUnequalInfoDao;


    @Override
    public CommonResponse fillUnionTxn(BatchRequest request) throws ParseException {

        CommonResponse response = new CommonResponse();
        if (IfspDataVerifyUtil.isBlank( request.getSettleDate())){
            response.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            response.setRespMsg("请传日期!!!");
            return response;
        }

        // 入差错保存时间
        String chkErrDt = request.getSettleDate();
        // 入对账不平表保存时间
        String chkUeqDt = IfspDateTime.plusTime(request.getSettleDate(), "yyyyMMdd", IfspTimeUnit.DAY, 1);


        List<Map<String,String>> infoList = new ArrayList<>();

        boolean flag = false;

        //*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-[  差错表的是需要补单的(01-主扫 , 丢单)  ]-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-
        // 根据日期查询差错表数据 ,拿到需要补单的记录
        List<BthPagyChkErrInfo> errInfos = getBthPagyChkErrInfos(chkErrDt);
        if (IfspDataVerifyUtil.isNotEmptyList(errInfos)){
            for (BthPagyChkErrInfo errInfo : errInfos) {
                // 根据差错数据查询对账文件表
                BthUnionFileDet bthUnionFileDet = getBthUnionFileDet(errInfo.getPagyPayTxnSsn());

                // 查不到对账文件表数据,跳过
                if (IfspDataVerifyUtil.isBlank(bthUnionFileDet)){
                    continue;
                }

                // 初始化调补单接口参数
                SoaParams soaParams = initParam(bthUnionFileDet,errInfo.getPagyTxnTm(),"01");
                // 调起补单接口
                SoaResults soaResults = fillUnionTxnSoaService.fillUnionTxn(soaParams);

                // 补单接口响应成功
                if (IfspRespCodeEnum.RESP_SUCCESS.getCode().equals(soaResults.getRespCode())) {
                    Map<String , String> recMap = new HashMap<>();
                    // 通道流水号
                    recMap.put("pagyPayTxnSsn",String.valueOf(soaResults.get("respSsn")));
                    // 银联手续费
                    recMap.put("tpamTxnFeeAmt",bthUnionFileDet.getCustomerFee());
                    // 差错表主键
                    recMap.put("chkErrSsn",errInfo.getChkErrSsn());

                    // 补单还是更新状态的标志
                    recMap.put("txnType","01");

                    infoList.add(recMap);
                    flag = true;
                }
            }
        }
        //*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-*-*_*-*-

        // 进对账不平表 , 则是需要更新通道状态的
        List<BthChkUnequalInfo> chkUnequalInfos = getBthChkUnequalInfos(chkUeqDt);
        if (IfspDataVerifyUtil.isNotEmptyList(chkUnequalInfos)){
            for (BthChkUnequalInfo chkUnequalInfo : chkUnequalInfos) {
                // 根据差错数据查询对账文件表
                BthUnionFileDet bthUnionFileDet = getBthUnionFileDet(chkUnequalInfo.getTpamTxnSsn());

                // 查不到对账文件表数据,跳过
                if (IfspDataVerifyUtil.isBlank(bthUnionFileDet)){
                    continue;
                }

                // 初始化调补单接口参数
                SoaParams soaParams = initParam(bthUnionFileDet,chkUnequalInfo.getPagyPayTxnTm(),"02");
                // 调起补单接口
                SoaResults soaResults = fillUnionTxnSoaService.fillUnionTxn(soaParams);

                // 补单接口响应成功
                if (IfspRespCodeEnum.RESP_SUCCESS.getCode().equals(soaResults.getRespCode())) {
                    Map<String , String> recMap = new HashMap<>();
                    // 通道流水号
                    recMap.put("pagyPayTxnSsn",String.valueOf(soaResults.get("respSsn")));
                    // 银联手续费
                    recMap.put("tpamTxnFeeAmt",bthUnionFileDet.getCustomerFee());
                    // 补单还是更新状态的标志
                    recMap.put("txnType","02");

                    infoList.add(recMap);
                    flag = true;
                }

            }
        }

        if (IfspDataVerifyUtil.isEmptyList(errInfos)&& IfspDataVerifyUtil.isEmptyList(chkUnequalInfos)){
            response.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
            response.setRespMsg("当日没有待补单的银联流水!!!");
            return response;
        }

        if (!flag){
            response.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            response.setRespMsg("补单失败!!!");
            return response;
        }



        //  通道调订单中心补单是需要时间的, 这里睡眠30s   ,确保订单已经生成   ,  已经去清算中心记账(只有生成订单以及记账才能给商户结算)
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            log.error("睡眠异常........");
        }


        // 查询订单 , 确认订单成功 ,则录入对账结果表
        for (Map<String, String> stringObjectMap : infoList) {
            // 得到订单
            PayOrderInfo payOrderInfo = getPayOrderInfo(stringObjectMap.get("pagyPayTxnSsn"));
            // 订单为空跳过
            if (IfspDataVerifyUtil.isBlank(payOrderInfo)){
                continue;
            }else {
                // 查询是否记账成功
                List<KeepAccInfo> keepAccInfos = keepAccInfoDao.selectByOrderSsn(payOrderInfo.getOrderSsn());
                // 订单未记账也跳过
                if (IfspDataVerifyUtil.isEmptyList(keepAccInfos)){
                    continue;
                }
            }

            // 录入对账结果表, 对账成功日期为第二天 , 通道号为 911
            initBthChkRsltInfo(stringObjectMap.get("pagyPayTxnSsn"),stringObjectMap.get("tpamTxnFeeAmt"),payOrderInfo.getOrderSsn());
            // 根据txnType判断是更新差错还是更新对账不平表

            if("01".equals(stringObjectMap.get("txnType"))){
                //更新差错状态
                bthPagyChkErrInfoDao.updByChkErrSsn(stringObjectMap.get("chkErrSsn"));
            }else if("02".equals(stringObjectMap.get("txnType"))) {
                //更新对账不平表
                bthChkUnequalInfoDao.updChkUnequalInfoProcStByPagyPayTxnSsn(stringObjectMap.get("pagyPayTxnSsn"));
            }else {
                log.error("没有此类型txnType: [{}]!!!!",stringObjectMap.get("txnType"));

            }
        }
        return response;
    }

    /**
     * 查询该日进入对账不平表的数据
     * @param chkUeqDt
     * @return
     */
    private List<BthChkUnequalInfo> getBthChkUnequalInfos(String chkUeqDt)
    {
        Map<String,Object> map = new HashMap<>();
        map.put("pagySysNo","607");
        map.put("chkUeqDate",chkUeqDt);
        return bthChkUnequalInfoDao.selectList("selectByPagySysNoAndChkUeqDt", map);
    }


    /**
     * 根据清算主键查询银联对账文件表
     * @param tpamSettleKey
     * @return
     */
    private BthUnionFileDet getBthUnionFileDet(String tpamSettleKey)
    {
        Map<String , Object> map = new HashMap<>();
        map.put("tpamSettleKey",tpamSettleKey);

        return bthUnionFileDetDao.selectOne("queryByTpamSettleKey", map);
    }

    /**
     * 初始化对账结果表
     * @param pagyPayTxnSsn
     * @param tpamTxnFeeAmt
     * @param orderSsn
     * @throws ParseException
     */
    private void initBthChkRsltInfo(String pagyPayTxnSsn, String tpamTxnFeeAmt, String orderSsn) throws ParseException
    {
        // 根据通道流水查询银联二维码流水表
        TpamCnuniopayQrcTxnInfo upacpTxnInfo = tpamCnuniopayQrcTxnInfoDao.selectByPrimaryKey(pagyPayTxnSsn);
        BthChkRsltInfo record = new BthChkRsltInfo();
        ReflectionUtil.copyProperties(upacpTxnInfo,record);
        // 银联手续费金额
        record.setTpamTxnFeeAmt(Long.parseLong(tpamTxnFeeAmt));
        // 订单号
        record.setOrderSsn(orderSsn);
        String today = IfspDateTime.getYYYYMMDD();
//        String tomorrow = IfspDateTime.plusTime(today, IfspDateTime.YYYYMMDD, IfspTimeUnit.DAY, 1);
//        // 对账成功日期设为当前时间的第二天 ,保证明天能处理
//        record.setChkSuccDt(IfspDateTime.strToDate(tomorrow, IfspDateTime.YYYY_MM_DD));
        record.setChkSuccDt(IfspDateTime.strToDate(today, IfspDateTime.YYYY_MM_DD));
        record.setChkSt(Constans.CHK_STATE_00);
        record.setChkRst("");
        record.setStlmSt(Constans.STLM_ST_00);
        record.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
        // 有值即表示不能删除 ,防止跑对账时自动删除
        record.setRemainFlag("fill");
        // 先插一遍订单是否已存在
        BthChkRsltInfo bthChkRsltInfo = bthChkRsltInfoDao.selectByPrimaryKey(upacpTxnInfo.getPagyPayTxnSsn());
        if (IfspDataVerifyUtil.isBlank(bthChkRsltInfo)){
            bthChkRsltInfoDao.insert(record);
        }else {
            log.error("订单[{}]已入对账结果表,无需再补!!!",orderSsn);
        }


    }


    /**
     * 根据通道流水查询订单
     * @param pagyPayTxnSsn
     * @return
     */
    private PayOrderInfo getPayOrderInfo(String pagyPayTxnSsn)
    {
        return payOrderInfoDao.selectByPagyTxnSsn(pagyPayTxnSsn);

    }



    /**
     * 得到待补单的数据
     * @param chkErrDt
     * @return
     */
    private List<BthPagyChkErrInfo> getBthPagyChkErrInfos(String chkErrDt)
    {
        Map<String , Object> map = new HashMap<>();
        map.put("chkErrDt",chkErrDt);
        return bthPagyChkErrInfoDao.selectList("queryUnionTxnByChkErrDtAndProcSt",map);
    }


    /**
     * 初始化银联补单参数
     *
     * @param bthUnionFileDet
     * @param pagyTxnTm
     * @return paramMap
     */
    private SoaParams initParam(BthUnionFileDet bthUnionFileDet, Date pagyTxnTm, String txnType)
    {
        SoaParams paramMap = new SoaParams();
        // 商户订单号
        paramMap.put("orderNo",bthUnionFileDet.getOrderId());
        // 不传(订单时间)
//        paramMap.put("orderTime","");
        // 交易金额
        paramMap.put("txnAmt",bthUnionFileDet.getTransAmt());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 清算日
        paramMap.put("settleDate",sdf.format(pagyTxnTm));
        // 清算主键
        paramMap.put("settleKey",bthUnionFileDet.getProxyInsCode()+bthUnionFileDet.getSendInsCode()+bthUnionFileDet.getTraceNum()+bthUnionFileDet.getTransDate());
        // 通道商户号
        paramMap.put("tpamMchtNo",bthUnionFileDet.getRecCardCode());
        // 卡号
        paramMap.put("acctNo",bthUnionFileDet.getAcctNo());
        // 交易类型，01-主扫（需要补单），02-被扫（更新状态）
        paramMap.put("txnType",txnType);
        // 不传(卡属性)
//        paramMap.put("cardAttr","");

        return paramMap;
    }
}
