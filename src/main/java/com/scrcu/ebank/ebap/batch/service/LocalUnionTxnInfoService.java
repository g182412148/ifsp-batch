package com.scrcu.ebank.ebap.batch.service;/**
 * Created by Administrator on 2019-05-10.
 */

import com.scrcu.ebank.ebap.batch.bean.request.LocalTxnInfoRequest;
import com.scrcu.ebank.ebap.batch.bean.response.LocalTxnInfoResponse;

import java.text.ParseException;

/**
 * 名称：〈银联流水抽取〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-05-10 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
public interface LocalUnionTxnInfoService {
    /**
     * 银联二维码流水抽取
     * @param request
     * @return
     * @throws Exception
     */
    LocalTxnInfoResponse getUnionTxnInfo(LocalTxnInfoRequest request) throws Exception;

    /**
     * 银联全渠道流水抽取
     * @param request
     * @return
     */
    LocalTxnInfoResponse getTotalUnionTxn(LocalTxnInfoRequest request) throws ParseException;
}
