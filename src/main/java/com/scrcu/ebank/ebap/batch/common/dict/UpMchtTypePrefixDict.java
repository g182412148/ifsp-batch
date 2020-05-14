package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

/**
 * 名称：〈银联商户服务类型〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月11日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public enum UpMchtTypePrefixDict {
    UPACP_MCHT_TYPE("966","银联全渠道"),
    UPACP_MCHT_SVC_TP("03","银联全渠道-03-互联网"),
    UPQRC_MCHT_TYPE("988","银联二维码普通商户"),
    UPQRC_MCHT_SVC_TP("00","00-传统POS商户");//扫码支付使用这个






    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    UpMchtTypePrefixDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UpMchtTypePrefixDict get(String code) {
        UpMchtTypePrefixDict[] values = UpMchtTypePrefixDict.values();
        for (UpMchtTypePrefixDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}
