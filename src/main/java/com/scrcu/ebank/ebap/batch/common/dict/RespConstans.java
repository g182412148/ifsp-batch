package com.scrcu.ebank.ebap.batch.common.dict;

/**
 * 响应码枚举类
 * @author
 */
public enum RespConstans {
    RESP_SUCCESS("0000", "交易成功"),
    RESP_INSERT_ERR("0001", "插入记录失败"),
    RESP_DEL_ERR("0002", "删除记录失败"),
    RESP_UPD_ERR("0003", "更新记录失败"),
    RESP_QUERY_ERR("0004", "查询记录失败"),
    RESP_ECIF_READY_UP_SUC("0005", "ECIF文件已上传成功"),
    RESP_AREA_NONE("006", "未找到相应地区码"),
    RESP_FAIL("9999", "交易失败");

    /** 枚举编号定义 */
    private String code;

    /** 枚举说明 */
    private String desc;

    RespConstans(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
