package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 名称：〈商户黑灰名单类型字典〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月04日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public enum MchtListTypeDict {
    //00：灰名单01：黑名单
    BLACK("01","黑名单"),
    //使用
    GREY("00","灰名单");

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    MchtListTypeDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static MchtListTypeDict get(String code) {
        MchtListTypeDict[] values = MchtListTypeDict.values();
        for (MchtListTypeDict value : values) {
            if (StringUtils.equalsIgnoreCase(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }
}
