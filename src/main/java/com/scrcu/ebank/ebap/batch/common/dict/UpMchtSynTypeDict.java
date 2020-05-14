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
public enum UpMchtSynTypeDict {
    ADD("I","增加"),
    MOD("U","修改"),
    DEL("D","删除"),
    ADD_DEL("X","新增同步完成后删除");


    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    UpMchtSynTypeDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UpMchtSynTypeDict get(String code) {
        UpMchtSynTypeDict[] values = UpMchtSynTypeDict.values();
        for (UpMchtSynTypeDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
