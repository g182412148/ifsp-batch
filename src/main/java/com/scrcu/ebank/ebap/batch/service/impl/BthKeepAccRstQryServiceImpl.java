package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccVo;
import com.scrcu.ebank.ebap.batch.bean.request.KeepAccQryRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BthKeepAccResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.KeepAccRespEnum;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.service.BthKeepAccRstQryService;
import com.scrcu.ebank.ebap.batch.service.CommKeepAccService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BthKeepAccRstQryServiceImpl implements BthKeepAccRstQryService {


    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    @Resource
    private CommKeepAccService commKeepAccService;

    @Override
    public BthKeepAccResponse bthKeepAccountRstQry(KeepAccQryRequest request) {

        // 1. 根据订单号、订单时间查询记账明细
        List<KeepAccInfo> keepAccInfoList ;
        // 判断批次号是否不为空 , 不为空根据订单批次号查询记账信息
        if (IfspDataVerifyUtil.isNotBlank(request.getVerNo())){
            keepAccInfoList = keepAccInfoDao.queryByOrderSsnAndTmAndVerNo(request.getOrderSsn(),request.getOrderTm(),request.getVerNo());
        // 为空全部查询
        }else {
            keepAccInfoList = keepAccInfoDao.queryByOrderSsnAndTm(request.getOrderSsn(),request.getOrderTm());
        }
        if (IfspDataVerifyUtil.isEmptyList(keepAccInfoList)){
            // 未找到记账明细
            return new BthKeepAccResponse(KeepAccRespEnum.RESP_NOTFIND.getCode(),KeepAccRespEnum.RESP_NOTFIND.getDesc());
        }else {
            // 超时标志
            boolean timeOutFlag = false;
            // 失败标志
            boolean errorFlag = false;

            List<KeepAccVo> accVoList = new ArrayList<>();
            for (KeepAccInfo keepAccInfo : keepAccInfoList) {
                switch (keepAccInfo.getState()){
                    // 失败分为未发送记账与记账失败两种
                    case Constans.KEEP_ACCOUNT_STAT_PRE:
                    case Constans.KEEP_ACCOUNT_STAT_FAIL:
                        errorFlag = true;
                        break;
                    case Constans.KEEP_ACCOUNT_STAT_TIMEOUT:
                        // 1.调用通道查询交易最终状态
                        Map<String, String> respMap = commKeepAccService.qrcKeepAccRst(keepAccInfo.getCoreSsn());
                        // 根据查询结果更新记账表状态 , 如果为空说明未查到结果
                        if (IfspDataVerifyUtil.isNotEmptyMap(respMap)){
                            // 更新记账表状态
                            updKeepAccVo(keepAccInfo.getCoreSsn(),respMap);
                            // 查询成功&&记账状态为失败
                            if (Constans.KEEP_ACCOUNT_STAT_FAIL.equals(respMap.get("state"))){
                                errorFlag = true;
                            }
                            // 设置正确状态
                            keepAccInfo.setState(respMap.get("state"));
                            // 未查到结果
                        }else {
                            log.error("无法知悉记账明细最终状态!!");
                            timeOutFlag = true;

                        }
                        break;
                }

                accVoList.add(new KeepAccVo(keepAccInfo,null));
            }

            return buildRespInfo(accVoList,errorFlag,timeOutFlag);
        }



    }

    /**
     * 根据状态更新记账表
     * @param coreSsn
     * @param respMap
     */
    private void updKeepAccVo(String coreSsn, Map<String, String> respMap) {
        KeepAccInfo upd = new KeepAccInfo();
        upd.setCoreSsn(coreSsn);
        upd.setState(respMap.get("state"));
        upd.setPagyRespCode(respMap.get("respCode"));
        upd.setPagyRespMsg(respMap.get("respMsg"));
        keepAccInfoDao.updateByPrimaryKeySelective(upd);
    }


    /**
     * 组装返回码
     * @param accVoList
     * @param errorFlag
     * @param timeOutFlag
     * @return
     */
    private BthKeepAccResponse buildRespInfo(List<KeepAccVo> accVoList, boolean errorFlag , boolean timeOutFlag) {
        BthKeepAccResponse response = new BthKeepAccResponse();
        response.setKeepAccData(accVoList);
        response.setErrorFlag(errorFlag?Constans.TRUE_FLAG:Constans.FALSE_FLAG);
        response.setTimeOutFlag(timeOutFlag?Constans.TRUE_FLAG:Constans.FALSE_FLAG);
        response.setRespCode(KeepAccRespEnum.RESP_SUCCESS.getCode());
        response.setRespMsg(KeepAccRespEnum.RESP_SUCCESS.getDesc());
        return response;
    }
}
