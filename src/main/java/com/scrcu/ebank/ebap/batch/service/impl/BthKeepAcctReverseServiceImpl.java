package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.request.KeepAccRevRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OnceRevKeepAccRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BthKeepAccResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.KeepAccRespEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.service.BthKeepAcctReverseService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量冲正服务
 * @author ljy
 */
@Service
@Slf4j
public class BthKeepAcctReverseServiceImpl implements BthKeepAcctReverseService {

    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    @Resource
    private KeepAccSoaService keepAccSoaService;

    /**
     * 批量冲正受理服务
     * @param request
     * @return
     */
    @Override
    public BthKeepAccResponse bthKeepAccountReverse(KeepAccRevRequest request) {

        List<KeepAccInfo> keepAccInfoList ;
        // 判断批次号是否传 , 不传默认找批次最大的进行冲正
        if (IfspDataVerifyUtil.isBlank(request.getVerNo())){
            // STEP1 根据订单号、订单时间去查询记账表记账成功的明细
            keepAccInfoList = keepAccInfoDao.queryByOrderSsnTmAndMaxVerNoSucc(request.getOrderSsn(),request.getOrderTm());
        }else {
            // STEP1 根据订单号、订单时间与批次号去查询记账表记账成功的明细
            keepAccInfoList = keepAccInfoDao.queryByOrderSsnTmAndVerNoSucc(request.getOrderSsn(),request.getOrderTm(),request.getVerNo());
        }

        if (IfspDataVerifyUtil.isEmptyList(keepAccInfoList)){
            // 未找到记账明细直接返回
            return new BthKeepAccResponse(KeepAccRespEnum.RESP_NOTFIND.getCode(),KeepAccRespEnum.RESP_NOTFIND.getDesc());
        }else {
            // 判断是否已有冲正流水入库, 整理出要冲正的流水
            List<KeepAccInfo> resvList  = new ArrayList<>();
            for (KeepAccInfo keepAccInfo : keepAccInfoList) {
                if (IfspDataVerifyUtil.isBlank(keepAccInfoDao.queryByOrigCoreSsn(keepAccInfo.getCoreSsn()))){
                    resvList.add(keepAccInfo);
                }else {
                    log.info("订单[{}]记账核心流水号[{}]已存在冲正流水,跳过处理...",keepAccInfo.getOrderSsn(),keepAccInfo.getCoreSsn());
                }
            }
            // STEP2 根据明细构造反向流水插入记账表
            BthKeepAcctReverseService reverseService = (BthKeepAcctReverseService)IfspSpringContextUtils.getInstance().getBean("bthKeepAcctReverseServiceImpl");
            reverseService.saveKeepAccInfo(resvList);
            // STEP3 返回响应成功
            return new BthKeepAccResponse(KeepAccRespEnum.RESP_SUCCESS.getCode(),"冲正受理成功");
        }
    }


    /**
     * 冲正流水入库
     * @param keepAccInfoList 原记账流水
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public void saveKeepAccInfo(List<KeepAccInfo> keepAccInfoList) {
        for (KeepAccInfo keepAccInfo : keepAccInfoList) {
            KeepAccInfo reverseRec = new KeepAccInfo(keepAccInfo);
            keepAccInfoDao.insert(reverseRec);
        }
    }


    /**
     * 调用本行通道单笔冲正服务
     * @param request 请求
     * @return resp
     */
    @Override
    public BthKeepAccResponse onceKeepAccountReverse(OnceRevKeepAccRequest request) {
        SoaResults soaResults = keepAccSoaService.ibankRevkeepAcc(initParam(request));
        // 返回成功 , 更新原交易状态为已冲正 , 更新本交易为成功 , 未返回成功 , 更新冲正明细尝试次数+1
        if (IfspDataVerifyUtil.equals(soaResults.getRespCode(),RespEnum.RESP_SUCCESS.getCode())){
            BthKeepAcctReverseService reverseService = (BthKeepAcctReverseService)IfspSpringContextUtils.getInstance().getBean("bthKeepAcctReverseServiceImpl");
            reverseService.updateData(request,soaResults.getRespCode(),soaResults.getRespMsg());
        }else {
            // 调用接口超时
            if (IfspDataVerifyUtil.equals(soaResults.getRespCode(),RespEnum.RESP_TIMEOUT.getCode())
                    // 接口显式返回超时
                    ||IfspDataVerifyUtil.equals(soaResults.getRespCode(),IfspRespCodeEnum.RESP_0021.getCode())){
                // 更新冲正明细尝试次数+1 ,状态为超时
                keepAccInfoDao.updateRetryCount(request.getPagyPayTxnSsn(),Constans.KEEP_ACCOUNT_STAT_TIMEOUT,soaResults.getRespCode(),soaResults.getRespMsg());
                // 更新冲正明细尝试次数+1 ,状态为失败
            }else {
                keepAccInfoDao.updateRetryCount(request.getPagyPayTxnSsn(),Constans.KEEP_ACCOUNT_STAT_FAIL,soaResults.getRespCode(),soaResults.getRespMsg());
            }
        }
        return new BthKeepAccResponse(soaResults.getRespCode(),soaResults.getRespMsg());
    }



    /**
     * 更新原交易状态与本冲正交易状态
     * @param request 请求
     * @param respCode
     * @param respMsg
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public void updateData(OnceRevKeepAccRequest request, String respCode, String respMsg) {
        // 更新原交易为已冲正
        KeepAccInfo origReco = new KeepAccInfo();
        origReco.setCoreSsn(request.getOrigPagyPayTxnSsn());
        origReco.setState(Constans.KEEP_ACCOUNT_STAT_REVERSE);
        keepAccInfoDao.updateByPrimaryKeySelective(origReco);

        // 更新本交易为成功
        KeepAccInfo reco = new KeepAccInfo();
        reco.setCoreSsn(request.getPagyPayTxnSsn());
        reco.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
        reco.setPagyRespCode(respCode);
        reco.setPagyRespMsg(respMsg);
        keepAccInfoDao.updateByPrimaryKeySelective(reco);
    }


    /**
     * 初始化参数
     * @param request 单笔冲正服务请求
     * @return 本行通道冲正服务请求参数
     */
    private SoaParams initParam(OnceRevKeepAccRequest request) {
        SoaParams param = new SoaParams();
        param.put("pagyPayTxnSsn",request.getPagyPayTxnSsn());
        param.put("pagyPayTxnTm",request.getPagyPayTxnTm());
        param.put("origPagyPayTxnSsn",request.getOrigPagyPayTxnSsn());
        param.put("erasPayInd",request.getErasPayInd());
        return param;
    }


}
