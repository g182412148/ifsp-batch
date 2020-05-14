package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 微信交易状态
 */
public enum WxTxnStatusDict {

    SUCCESS("0000", "成功"),
    LOCAL_HANDLING("0002", "处理中稍后发起查询"),
    OUTER_HANDLING("0011", "第三方处理中"),
    TIMEOUT("0020", "超时"),
    FAIL(null, "失败");

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    WxTxnStatusDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static WxTxnStatusDict get(String code) {
        WxTxnStatusDict[] values = WxTxnStatusDict.values();
        for (WxTxnStatusDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return FAIL;
    }
}
