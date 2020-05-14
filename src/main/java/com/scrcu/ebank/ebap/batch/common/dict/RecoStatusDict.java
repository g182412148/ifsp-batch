package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 对账状态
 */
public enum RecoStatusDict {

    READY("0", "未对账"),
    DUBIOUS("2", "可疑账(在途)"),
    FINISH("1", "已对账"),
    SKIP("3", "无需参与对账");

    /**
     * 字典值
     */
    private String code;
    /**
     * 字典描述
     */
    private String desc;

    RecoStatusDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RecoStatusDict get(String code) {
        RecoStatusDict[] values = RecoStatusDict.values();
        for (RecoStatusDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
