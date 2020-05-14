package com.scrcu.ebank.ebap.batch.service;

/**
 * @author: ljy
 * @create: 2018-09-04 22:57
 */
public interface AsyncNoticeTaskService {

    /**
     * 通知记账结果
     */
    void asyncNotice();

    /**
     * 根据订单号锁表,调用通知,更新状态
     * @param orderSsn
     */
    void lockByOrderSsn(String orderSsn);
}
