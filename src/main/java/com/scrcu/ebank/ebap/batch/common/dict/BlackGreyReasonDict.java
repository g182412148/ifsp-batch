package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

public enum BlackGreyReasonDict {
    REASON_1("1","证件将到期"),
    REASON_2("2","连续3个月无交易"),
    REASON_3("3","证件到期"),
    REASON_4("4","连续1年无交易"),
    REASON_5("5","交易异常"),
    REASON_6("6","风险商户"),
    REASON_7("7","银联黑名单"),
    REASON_8("8","其他");

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    BlackGreyReasonDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static BlackGreyReasonDict get(String code) {
        BlackGreyReasonDict[] values = BlackGreyReasonDict.values();
        for (BlackGreyReasonDict blackGreyReasonDict : values) {
            if (StringUtils.equalsIgnoreCase(blackGreyReasonDict.getCode(), code)) {
                return blackGreyReasonDict;
            }
        }
        return null;
    }
}
