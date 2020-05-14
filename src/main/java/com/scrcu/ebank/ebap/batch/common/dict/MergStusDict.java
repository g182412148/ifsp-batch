package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 名称：〈机构撤并状态〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019年06月25日 <br>
 * 作者：xiesl<br>
 * 说明：<br>\
 */
public enum MergStusDict {
    //是
    NOT_DONE("0","未执行"),
    EXECUTING("1","撤并中"),
    SUCCESS("2","撤并成功"),
    FAIL("3","撤并失败");
    //否

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    MergStusDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static MergStusDict get(String code) {
        MergStusDict[] values =  MergStusDict.values();
        for (MergStusDict value : values) {
            if (StringUtils.equalsIgnoreCase(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }
}
