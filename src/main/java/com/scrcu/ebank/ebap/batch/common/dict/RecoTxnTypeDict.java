package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对账交易类型
 */
public enum RecoTxnTypeDict {

    PAY("00", "支付"),
    REFUND("10", "退款"),
    REVOKE("20", "撤销或冲正"),
    CANCEL("30", "冲正撤销");

    /** 微信交易和对账交易类型的 映射关系 */
    private static Map<String, RecoTxnTypeDict> wxTxnMapping;

    /** 支付宝交易和对账交易类型的 映射关系 */
    private static Map<String, RecoTxnTypeDict> aliTxnMapping;


    static{
        wxTxnMapping = new ConcurrentHashMap<>();
        wxTxnMapping.put("REFUND", REFUND);
        wxTxnMapping.put("REVOKED", REVOKE);
        aliTxnMapping = new ConcurrentHashMap<>();
        aliTxnMapping.put("交易",PAY);
        aliTxnMapping.put("退款",REFUND);
    }

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    RecoTxnTypeDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RecoTxnTypeDict get(String code) {
        RecoTxnTypeDict[] values = RecoTxnTypeDict.values();
        for (RecoTxnTypeDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }

    /**
     * 微信交易类型 转换为 对账交易类型
     * @param wxTxnType
     * @return
     */
    public static RecoTxnTypeDict wxType2RecoType(String wxTxnType){
        RecoTxnTypeDict recoTxnType = wxTxnMapping.get(wxTxnType);
        if(recoTxnType == null){
            return RecoTxnTypeDict.PAY;
        }else {
            return recoTxnType;
        }
    }

    /**
     * 支付宝交易类型 转换为 对账交易类型
     * @param busiTp
     * @return
     */
    public static RecoTxnTypeDict aliType2RecoType(String busiTp) {
        return aliTxnMapping.get(busiTp);
    }
}
