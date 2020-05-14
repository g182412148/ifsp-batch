package com.scrcu.ebank.ebap.batch.common.dict;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;

/**
 * @author ljy
 * @date 2018-12-25 16:26
 */
public enum AntiFraudSwitchEnum {
    /**
     *  反欺诈 - 关
     */
    ANTI_FRAUD_SWITCH_OFF("0","关"),

    /**
     *  反欺诈 - 开
     */
    ANTI_FRAUD_SWITCH_ON("1","开");

    private String code;
    private String desc;

    AntiFraudSwitchEnum(String code,String desc ){
        this.code = code;
        this.desc = desc;

    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    public static AntiFraudSwitchEnum get(String code) {
        AntiFraudSwitchEnum[] values = AntiFraudSwitchEnum.values();
        for (AntiFraudSwitchEnum enumVo : values) {
            if (IfspDataVerifyUtil.equalsIgnoreCase(enumVo.getCode(), code)) {
                return enumVo;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        if (IfspDataVerifyUtil.isBlank(code)) {
            return "";
        }
        for (AntiFraudSwitchEnum enumVo : AntiFraudSwitchEnum.values()) {
            if (enumVo.getCode().equals(code)) {
                return enumVo.getDesc();
            }
        }
        return "";
    }

}
