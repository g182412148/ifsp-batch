package com.scrcu.ebank.ebap.batch.bean.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 渠道商户缓存限缓存信息
 * 功能：〈主键：chlId+chlMchtNo〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018-06-19 <br>
 * 作者：zhaodk <br>
 * 说明：<br>
 */
@Data
public class PagyChlMchtCacheInfo implements Serializable {
    /**
     * 渠道编号
     */
    private String chlNo;
    /**
     * 渠道名称
     */
    private String chlName;
    /**
     * 渠道接入方式：00=渠道直连接入；02=平台接入(平台接入时需要上送平台商户信息)；
     */
    private String chlAcsType;

    /**
     * 安保标识：00=启用(需要进行安保配置)；01=停用
     */
    private String securityFlag;
    /**
     * 渠道工作开始时间,格式：HH:MM:SS
     */
    private String chlWorkStartTm;

    /**
     * 渠道工作结束时间,格式：HH:MM:SS
     */
    private String chlWorkStopTm;
    /**
     * 启用日期
     */
    private String chlOpenDate;
    /**
     * 渠道清算模式：00：一清模式；01：二清模式；99：不清模式；
     */
    private String chlSetlModel;
    /**
     * 渠道商户号
     */
    private String chlMchtNo;
    /**
     * 商户名称
     */
    private String chlMchtName;
    /**
     * 通道是否使用渠道商户号(0=是(通道核心不生成新的渠道商户号直接使用渠道进件商户)；1=通道核心生成新的渠道商户号)
     */
    private String chlMchtCerFlag;
}
