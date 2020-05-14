package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.request.GetOrderInfoRequest;
import com.scrcu.ebank.ebap.batch.dao.IbankBillLocalDao;
import com.scrcu.ebank.ebap.batch.service.GetLocalInfoService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;


/**
 * 抽取通道本地流水服务
 * @author ljy
 * @date 2019-05-10
 */
@Service("iBankLocalTxnService")
@Slf4j
public class IBankLocalTxnServiceImpl implements GetLocalInfoService {


    @Resource
    private IbankBillLocalDao ibankBillLocalDao;


    @Override
    public CommonResponse getLocalTxnInfo(GetOrderInfoRequest req) {


        //获取的订单数据的交易日期
        Date txnDate = getRecoDate(req.getSettleDate());
        //计算对账日期 = 交易日期 + 1
        Date recoDate = new DateTime(txnDate).plusDays(1).toDate();

        //清空数据
        clear(recoDate);
        /*
         * 抽取数据(表复制)
         */
        int count = ibankBillLocalDao.copy(txnDate, recoDate);
        int count2 = ibankBillLocalDao.copyReturn(txnDate, recoDate);
        log.info("本行数据抽取订单数量:" + (count+count2));
        return new CommonResponse();
    }


    /**
     * 根据对账日期清除本地流水表
     * @param recoDate
     */
    private void clear(Date recoDate) {
        int clearCount = ibankBillLocalDao.clear(recoDate);
        log.info("删除对账日下本行订单数量:" + clearCount);
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
