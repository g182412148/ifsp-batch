package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public interface ErrInfoService {

    /**
     * 获取前一天的差错  插入差错详情表
     * @return
     */
    CommonResponse getYestodayErrInfo();

    /**
     * 推送差错文件
     * @return
     */
    CommonResponse pushErrFile();

    /**
     * 差错记账
     * @return
     */
    CommonResponse errAcc();

}
