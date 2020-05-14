package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 名称：〈法人标识〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019年06月25日 <br>
 * 作者：xiesl<br>
 * 说明：<br>\
 */
public enum CorpFlagDict {
    //是
    YES("1","是"),
    //否
    NO("0","否");
    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    CorpFlagDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static CorpFlagDict get(String code) {
        CorpFlagDict[] values =  CorpFlagDict.values();
        for (CorpFlagDict value : values) {
            if (StringUtils.equalsIgnoreCase(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }
}
