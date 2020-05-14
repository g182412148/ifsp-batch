package com.scrcu.ebank.ebap.batch.common.dict;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;

/**
 * 记账类: 是否支持重跑标志
 */
public enum ReRunFlagEnum {
    /**
     * 支持重跑
     */
    RE_RUN_FLAG_TRUE("00","支持重跑"),

    /**
     * 不支持重跑
     */
    RE_RUN_FLAG_FALSE("01", "不支持重跑");

    /**
     * 枚举编号定义
     */
    private String code;
    /**
     * 枚举说明
     */
    private String desc;

    ReRunFlagEnum(String code, String desc){
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

    public static ReRunFlagEnum get(String code) {
        ReRunFlagEnum[] values = ReRunFlagEnum.values();
        for (ReRunFlagEnum reRunFlagEnum : values) {
            if (IfspDataVerifyUtil.equalsIgnoreCase(reRunFlagEnum.getCode(), code)) {
                return reRunFlagEnum;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        if (IfspDataVerifyUtil.isBlank(code)) {
            return "";
        }
        for (ReRunFlagEnum reRunFlagEnum : ReRunFlagEnum.values()) {
            if (reRunFlagEnum.getCode().equals(code)) {
                return reRunFlagEnum.getDesc();
            }
        }
        return "";
    }
}
