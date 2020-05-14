package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 可疑标志
 */
public enum DubisFlagDict {

    TRUE("1","可疑");

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    DubisFlagDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DubisFlagDict get(String code) {
        DubisFlagDict[] values = DubisFlagDict.values();
        for (DubisFlagDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
