package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 名称：〈银联商户服务类型〉<br>
 * 功能：〈银联商户同步状态〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月11日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public enum UpMchtSynStateDict {
    NOT_NEED("0","不需同步"),
    INIT("1","待同步"),
    SYN_IN("2","同步中"),
    FSYN_FIN("3","同步完成"),
    SYN_ERR("4","同步错误");


    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    UpMchtSynStateDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UpMchtSynStateDict get(String code) {
        UpMchtSynStateDict[] values = UpMchtSynStateDict.values();
        for (UpMchtSynStateDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
