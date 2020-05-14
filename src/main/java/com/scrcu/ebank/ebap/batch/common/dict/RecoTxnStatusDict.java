package com.scrcu.ebank.ebap.batch.common.dict;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对账交易状态
 */
public enum RecoTxnStatusDict {

    SUCCESS("00", "成功"),
    FAIL("99", "失败"),
    PROCESSING("01", "处理中");

    /** 微信交易和对账交易类型的 映射关系 */
    private static Map<String, RecoTxnStatusDict> wxTxnMapping;
    //退款标志
    private static String refundFlag = "REFUND";
    //撤销标志
    private static String revokeFlag = "REVOKED";

    static{
        wxTxnMapping = new ConcurrentHashMap<>();
        wxTxnMapping.put("SUCCESS", SUCCESS);
        wxTxnMapping.put("FAIL", FAIL);
        wxTxnMapping.put("PROCESSING", PROCESSING);
    }

    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;

    RecoTxnStatusDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RecoTxnStatusDict get(String code) {
        RecoTxnStatusDict[] values = RecoTxnStatusDict.values();
        for (RecoTxnStatusDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }


    /**
     * 微信状态转换为对账状态
     * @param txnState
     * @param refundState
     * @return
     */
    public static RecoTxnStatusDict wxType2RecoType(String txnState, String refundState){
        //退款和撤销交易, 取退款状态
        if(StringUtils.equals(txnState, refundFlag) || StringUtils.equals(txnState, revokeFlag)){
            return wxTxnMapping.get(refundState);
        }else { //支付交易
            return wxTxnMapping.get(txnState);
        }
    }
}
