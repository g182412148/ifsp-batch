package com.scrcu.ebank.ebap.batch.bean.vo;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 名称：〈〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月12日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public class DictVo {
    //字典类型
    @NotBlank(message = "字典类型不能为空")
    private String ditcType;
    //字典名称
    @NotBlank(message = "字典名称不能为空")
    private String dicName;
    //字典编号
    @NotBlank(message = "字典编号不能为空")
    private String dictNo;
    //字典值
    @NotBlank(message = "字典值不能为空")
    private String dictValue;

    public String getDitcType() {
        return ditcType;
    }

    public void setDitcType(String ditcType) {
        this.ditcType = ditcType;
    }

    public String getDicName() {
        return dicName;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName;
    }

    public String getDictNo() {
        return dictNo;
    }

    public void setDictNo(String dictNo) {
        this.dictNo = dictNo;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }
}
