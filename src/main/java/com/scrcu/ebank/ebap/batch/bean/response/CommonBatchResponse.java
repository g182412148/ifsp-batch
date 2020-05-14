package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.Data;

/**
 * <p>名称 : 商户中心公共响应报文类 </p>
 * <p>方法 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/6/7  20:12 </p>
 */
@Data
public class CommonBatchResponse extends CommonResponse {

    /**
     * 响应流水号
     */
    private String respSsn;

    /**
     * 响应时间
     */
    private String respDate;


}
