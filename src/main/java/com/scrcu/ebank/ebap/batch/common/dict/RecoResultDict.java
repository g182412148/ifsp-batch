package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 对账结果
 */
public enum RecoResultDict {

    IDENTICAL("00", "平账"),
    STATE_UI("01", "状态不一致"),
    AMT_UI("02", "金额不一致"),
    TYPE_UI("03", "类型不一致"),
    LOCAL_UA("11","本地单边"),
    OUTER_UA("12","三方单边");

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    RecoResultDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RecoResultDict get(String code) {
        RecoResultDict[] values = RecoResultDict.values();
        for (RecoResultDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
