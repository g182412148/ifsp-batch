package com.scrcu.ebank.ebap.batch.service.impl;/**
 * Created by Administrator on 2019-05-20.
 */

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.request.LocalKeepTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.request.LocalTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.response.LocalTxnInfoResponse;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.dao.KeepRecoInfoDao;
import com.scrcu.ebank.ebap.batch.service.LocalKeepAcctInfoService;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-05-20 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Service("localKeepAcctInfoService")
@Slf4j
public class LocalKeepAcctInfoServiceImp implements LocalKeepAcctInfoService {

    @Resource
    private KeepRecoInfoDao keepRecoInfoDao;

    @Override
    public LocalTxnInfoResponse getKeepAccInfo(LocalKeepTxnInfoRequest request) throws Exception {
        /** 初始化返回报文对象 */
        LocalTxnInfoResponse localUnionResponse = new LocalTxnInfoResponse();
        //获取的订单数据的交易日期
        Date txnDate = getRecoDate(request.getSettleDate());
        //计算对账日期 = 交易日期 + 1
        Date recoDate = new DateTime(txnDate).plusDays(1).toDate();
        //清空数据
        int clearCount = keepRecoInfoDao.clear(recoDate);
        log.info("清空数据{}条", clearCount);
        /*
         * 抽取数据(表复制)
         */
        int count = keepRecoInfoDao.copy(txnDate, recoDate);
        log.info("记账表数据抽取订单数量:" + count);
        localUnionResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        localUnionResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        return localUnionResponse;
    }

    /**
     * 获取对账日期
     * @param dateStr
     * @return
     */
    private Date getRecoDate(String dateStr){
        if(StringUtils.isBlank(dateStr)){
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期为空");
        }
        try{
            return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").toDate(); //todo 改成常数
        }catch (Exception e){
            log.error("对账日期格式错误: ", e);
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期格式错误");
        }
    }
}
