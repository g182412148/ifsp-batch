package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 名称：〈〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月11日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public enum PlatFlagDict {
    //0-按笔
    NOPLAT("0","非平台"),
    //01-
    PLAT("1","平台");


    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    PlatFlagDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PlatFlagDict get(String code) {
        PlatFlagDict[] values = PlatFlagDict.values();
        for (PlatFlagDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
