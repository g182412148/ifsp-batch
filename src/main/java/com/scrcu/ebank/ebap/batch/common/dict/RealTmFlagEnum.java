package com.scrcu.ebank.ebap.batch.common.dict;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;

/**
 * 记账类: 是否支持重跑标志
 */
public enum RealTmFlagEnum {
    /**
     * 实时结算
     */
    REAL_TM_TREU("00","实时结算"),

    /**
     * 非实时结算
     */
    REAL_TM_FALSE("01", "非实时结算");

    /**
     * 枚举编号定义
     */
    private String code;
    /**
     * 枚举说明
     */
    private String desc;

    RealTmFlagEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取枚举编号
     */
    public String getCode() {

        return code ;
    }

    /**
     * 获取枚举说明
     */
    public String getDesc() {

        return desc ;
    }

    public static RealTmFlagEnum get(String code) {
        RealTmFlagEnum[] values = RealTmFlagEnum.values();
        for (RealTmFlagEnum realTmFlagEnum : values) {
            if (IfspDataVerifyUtil.equalsIgnoreCase(realTmFlagEnum.getCode(), code)) {
                return realTmFlagEnum;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        if (IfspDataVerifyUtil.isBlank(code)) {
            return "";
        }
        for (RealTmFlagEnum realTmFlagEnum : RealTmFlagEnum.values()) {
            if (realTmFlagEnum.getCode().equals(code)) {
                return realTmFlagEnum.getDesc();
            }
        }
        return "";
    }
}
