package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 差错处理状态
 */
public enum ErroProcStateDict {

    INIT("00", "未处理"),
    FINISH("01", "已处理"),
    PUSH("02","已推送差错中心");


    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    ErroProcStateDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ErroProcStateDict get(String code) {
        ErroProcStateDict[] values = ErroProcStateDict.values();
        for (ErroProcStateDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
