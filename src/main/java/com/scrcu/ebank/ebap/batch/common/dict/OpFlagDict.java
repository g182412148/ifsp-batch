package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 名称：〈〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019年09月05日 <br>
 * 作者：xiesl <br>
 * 说明：<br>
 */
public enum OpFlagDict {

    REPE_MERG("5","执行撤并");


    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    OpFlagDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OpFlagDict get(String code) {
        OpFlagDict[] values = OpFlagDict.values();
        for (OpFlagDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
