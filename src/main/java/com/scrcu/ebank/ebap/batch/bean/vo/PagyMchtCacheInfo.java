package com.scrcu.ebank.ebap.batch.bean.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 通道商户缓存限缓存信息
 * 功能：〈主键：pagyNo+chlId+chlMchtNo〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018-06-19 <br>
 * 作者：zhaodk <br>
 * 说明：<br>
 */
@Data
public class PagyMchtCacheInfo implements Serializable {
    /**
     * 通道编号(内部编号=4位系统编号+11序号)
     */
    private String pagyNo;
    /**
     * 渠道编号
     */
    private String chlNo;

    /**
     * 渠道商户号
     */
    private String chlMchtNo;
    /**
     * 通道商户编号(内部商户编号）
     */
    private String pagyMchtNo;


    /**
     * 第三方通道商户编号
     */
    private String tpamPagyMchtNo;

    /**
     * 第三方通道商户应用编号
     */
    private String tpamPagyMchtAppId;

    /**
     * 第三方通道商户应用版本号
     */
    private String tpamPagyMchtAppVersion;

    /**
     * 商户名称
     */
    private String pagyMchtNm;

    /**
     * mcc
     */
    private String tpamMccId;

    /**
     * 地区码
     */
    private String tpamMchtAddsCode;

}
