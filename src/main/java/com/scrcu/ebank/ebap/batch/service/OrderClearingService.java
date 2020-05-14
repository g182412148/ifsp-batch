package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface OrderClearingService 
{
    /**
     * 本行订单清分
     * @param request
     * @return
     */
    CommonResponse coreChannelClearing(BatchRequest request) throws Exception;
    
    /**
     * 微信订单清分
     * @param request
     * @return
     */
    CommonResponse wxChannelClearing(BatchRequest request) throws Exception;
    
    /**
     * 支付宝订单清分
     * @param request
     * @return
     */
    CommonResponse aliChannelClearing(BatchRequest request) throws Exception;
    
    /**
     * 银联订单清分
     * @param request
     * @return
     */
    CommonResponse unionpayChannelClearing(BatchRequest request) throws Exception;
}
