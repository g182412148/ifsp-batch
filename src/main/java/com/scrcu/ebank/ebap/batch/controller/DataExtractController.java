package com.scrcu.ebank.ebap.batch.controller;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.*;
import com.scrcu.ebank.ebap.batch.dao.TpamAtWechatTxnInfoDao;
import com.scrcu.ebank.ebap.batch.dao.test.WxBillLocalDao;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.Date;

@Controller
public class DataExtractController {

    Logger log = IfspLoggerFactory.getLogger(DataExtractController.class);

    @Resource
    private WxBillLocalDao localBillDao;
    @Resource
    private TpamAtWechatTxnInfoDao localRecordDao;

    @SOA("002.GetWxAtTxnInfo")
    @Explain(name = "微信抽数")
    public CommonResponse wechat(AcctContrastRequest req){
        //获取的订单数据的交易日期
        Date txnDate = getRecoDate(req.getSettleDate());
        //计算对账日期 = 交易日期 + 1
        Date recoDate = new DateTime(txnDate).plusDays(1).toDate();
        //清空数据
        int clearCount = clear(recoDate);
        log.info("清空历史微信抽取订单数量:" + clearCount);
        /*
         * 抽取数据(表复制)
         */
        int count = localBillDao.copy(txnDate, recoDate);
        log.info("微信数据抽取订单数量:" + count);


        /**
         * 在这里提前将后续批量所涉及到的本地缓存清除
         */
        CacheMchtBaseInfo.clearCache();
        CacheMchtContInfo.clearCache();
        CacheMchtGainsInfo.clearCache();
        CacheMchtOrgRel.clearCache();
        CacheServiceGainsInfo.clearCache();
        CacheServiceBaseInfo.clearCache();
        return new CommonResponse();
    }


    private int clear(Date recoDate) {
        return localBillDao.clear(recoDate);
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
