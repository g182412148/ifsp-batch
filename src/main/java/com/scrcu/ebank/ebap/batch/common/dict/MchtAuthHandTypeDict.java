package com.scrcu.ebank.ebap.batch.common.dict;

/**
 * <p>名称 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : zhangb </p>
 * <p>日期 : 2019-09-02  18:41 </p>
 */

import org.apache.commons.lang.StringUtils;

/**
 * 商户实名认证处理类型
 *
 */
public enum MchtAuthHandTypeDict {

    APPLY ("0","实名认证申请"),
    APPLY_QUERY ("1","实名认证申请查询"),
    AUTH_QUERY ("2","实名认证授权查询");

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    MchtAuthHandTypeDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    public static MchtAuthHandTypeDict get(String code) {
        MchtAuthHandTypeDict[] values = MchtAuthHandTypeDict.values();
        for (MchtAuthHandTypeDict value : values) {
            if (StringUtils.equalsIgnoreCase(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }
}