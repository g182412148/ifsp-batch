package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PayOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;

import java.util.List;

/**
 * Created by Administrator on 2019-06-19.
 */
public interface PreClearingOldService {

    void dataGathering(BthChkRsltInfo chkSuccOrd);

    void dataGathering(List<BthChkRsltInfo> bthChkRsltInfoList);

    void calMerFee4SubOrder(PayOrderInfo orderInfo,PaySubOrderInfo subOrder,BthChkRsltInfo chkSuccOrd);
}
