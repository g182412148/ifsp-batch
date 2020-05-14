package com.scrcu.ebank.ebap.batch.common.dict;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;

/**
 * 返回码枚举类
 */
public enum RespEnum {
    /**
     * respCode
     */
    RESP_CODE_PARAM("respCode", "响应码字段变量"),
    /**
     * respMsg
     */
    RESP_MSG_PARAM("respMsg", "响应码字段说明变量"),

    /**
     * 交易成功
     */
    RESP_SUCCESS("0000", "交易成功"),
    /**
     * 交易超时
     */
    RESP_TIMEOUT("0088", "交易超时"),
    /**
     * 交易超时
     */
    RESP_PROCESSED("0089", "记账失败,订单已处理"),
    /**
     * 交易失败
     */
    RESP_FAIL("9999", "交易失败"),
    /**
     * 服务应答为空
     */
    RESP_NULL("4399", "服务无应答");

    /**
     * 枚举编号定义
     */
    private String code;
    /**
     * 枚举说明
     */
    private String desc;

    RespEnum(String code, String desc){
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

    public static RespEnum get(String code) {
        RespEnum[] values = RespEnum.values();
        for (RespEnum respEnum : values) {
            if (IfspDataVerifyUtil.equalsIgnoreCase(respEnum.getCode(), code)) {
                return respEnum;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        if (IfspDataVerifyUtil.isBlank(code)) {
            return "";
        }
        for (RespEnum respEnum : RespEnum.values()) {
            if (respEnum.getCode().equals(code)) {
                return respEnum.getDesc();
            }
        }
        return "";
    }
}
