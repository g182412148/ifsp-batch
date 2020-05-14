package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.util.Date;

/**
 * 名称：〈〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019年07月07日 <br>
 * 作者：zhangbin <br>
 * 说明：<br>
 */
@Data
public class MchtBlackGreyInfo {
    private String id;
    /**
     * 商户号
     */
    private String mchtId;

    /**
     * 纳入原因
     * 多个原因用','分隔
     * 1-证件将到期
     * 2-连续3个月无交易
     * 3-证件到期
     * 4-连续1年无交易
     * 5-交易异常
     * 6-风险商户
     * 7-银联黑名单
     * 8-其他
     */
    private String reason;


    /**
     * 商户名单类型
     * 00：灰名单,01：黑名单
     */
    private String mchtListType;

    /**
     * 创建柜员
     */
    private String crtTlr;

    /**
     * 创建时间
     */
    private Date crtTm;

    /**
     * 更新柜员
     */
    private String lastUpdTlr;

    /**
     * 更新时间
     */
    private Date lastUpdTm;


    private String reasonDesc;
    private String status;
}
