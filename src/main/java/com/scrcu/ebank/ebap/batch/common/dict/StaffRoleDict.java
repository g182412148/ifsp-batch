package com.scrcu.ebank.ebap.batch.common.dict;
import org.apache.commons.lang.StringUtils;

/**
 * 名称：〈员工角色〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月04日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public enum StaffRoleDict {
    //法人/负责人
    LEP("01","法人/负责人"),
    //联系人
    CNA("02","联系人"),
    //店长
    MNG("03","店长"),
    //店员
    ASS("04","店员");
    /** 字典值  */
    private String code;
    /** 字典描述 */
    private String desc;
    StaffRoleDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
    public static StaffRoleDict get(String code) {
        StaffRoleDict[] values = StaffRoleDict.values();
        for (StaffRoleDict ePCC_ACCT_TP_CD : values) {
            if (StringUtils.equalsIgnoreCase(ePCC_ACCT_TP_CD.getCode(), code)) {
                return ePCC_ACCT_TP_CD;
            }
        }
        return null;
    }
}