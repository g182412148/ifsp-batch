package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

public enum AcctTypeId {
    /** 账户类型 */
    TYPE_0 ("000","全账户"),
    /** 银联卡账户 */
    TYPE_1 ("001","银联卡账户"),
    /** 支付宝账户 */
    TYPE_2 ("002","支付宝账户"),
    /** 微信账户 */
    TYPE_3 ("003","微信账户"),
    /** 本行卡账户 */
    TYPE_4 ("004","本行卡账户"),
    UNION_1 ("1","线下银联"),
    UNION_2("2","线上银联");

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    AcctTypeId(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    public static AcctTypeId get(String code) {
        AcctTypeId[] values = AcctTypeId.values();
        for (AcctTypeId value : values) {
            if (StringUtils.equalsIgnoreCase(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }
}
