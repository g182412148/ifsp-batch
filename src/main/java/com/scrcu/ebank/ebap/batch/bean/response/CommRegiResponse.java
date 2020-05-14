package com.scrcu.ebank.ebap.batch.bean.response;

import org.hibernate.validator.constraints.NotBlank;

import com.scrcu.ebank.ebap.batch.bean.dto.PagnResult;
import com.scrcu.ebank.ebap.msg.common.response.ScrcuCommonResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 名称：〈公共响应报文〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月01日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommRegiResponse extends ScrcuCommonResponse {
    private static final long serialVersionUID = 1L;


    /**
     * 平台流水号
     */
    private String respSsn;
    /**
     * 平台流水时间
     */
    private String respTm;
    /**
     * 请求流水号
     */
    @NotBlank
    private String reqSsn;
    /**
     * 请求时间
     */
    @NotBlank
    private String reqTm;

    //分页结果
    private PagnResult pagnResult;

}

