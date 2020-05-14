package com.scrcu.ebank.ebap.batch.common.dict;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;

public enum KeepAccRespEnum {
    /**
     * respCode
     */
    RESP_CODE_PARAM("respCode", "响应码字段变量"),
    /**
     * respMsg
     */
    RESP_MSG_PARAM("respMsg", "响应码字段说明变量"),
    /**
     * 未找到记账记录
     */
    RESP_NOTFIND("1001", "未找到对应的记账明细"),

    /**
     * 交易成功
     */
    RESP_SUCCESS("0000", "记账成功"),
    /**
     * 部分记账成功
     */
    RESP_PARTSUCC("0001", "部分记账成功"),
    /**
     * 全部失败
     */
    RESP_FAIL("0002", "全部失败"),
    /**
     * 异常失败
     */
    RESP_ERR("9999", "异常失败");

    /**
     * 枚举编号定义
     */
    private String code;
    /**
     * 枚举说明
     */
    private String desc;

    KeepAccRespEnum(String code, String desc){
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

    public static KeepAccRespEnum get(String code) {
        KeepAccRespEnum[] values = KeepAccRespEnum.values();
        for (KeepAccRespEnum respEnum : values) {
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
        for (KeepAccRespEnum respEnum : KeepAccRespEnum.values()) {
            if (respEnum.getCode().equals(code)) {
                return respEnum.getDesc();
            }
        }
        return "";
    }

}
