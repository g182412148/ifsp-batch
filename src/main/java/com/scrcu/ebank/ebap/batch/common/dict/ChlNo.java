package com.scrcu.ebank.ebap.batch.common.dict;/*
 * Copyright (C), 2015-2018, 上海睿民互联网科技有限公司
 * Package com.scrcu.ebank.ebap.order.common.dict
 * Author:   shiyw
 * Date:     2018/6/11 下午4:33
 * Description: //模块目的、功能描述
 * History: //修改记录
 *===============================================================================================
 *   author：          time：                             version：           desc：
 *   shiyw             2018/6/11下午4:33                1.0
 *===============================================================================================
 */


import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;

public enum ChlNo {

    WECHAR("01", "微信"),
    ALIPAY("02", "支付宝"),
    UNIONPAY("08", "银联"),
    HUIPAY_PERSON("26", "惠支付(个人版)"),
    HUIPAY("04", "惠支付(商户版)"),
    SX_E("07", "蜀信e"),
    ONLINE("10", "线上渠道"),
    CONSOLE("51", "内管"),
    ALL ("50", "全部"),
    INNER("99","订单系统自身"),
    BATCH("09","批量系统");


    private String code;
    private String desc;

    ChlNo(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public static String getDescByCode(String code) {
        if (IfspDataVerifyUtil.isBlank(code)) {
            return "";
        }
        for (ChlNo chlNo : ChlNo.values()) {
            if (chlNo.getCode().equals(code)) {
                return chlNo.getDesc();
            }
        }
        return "";
    }

    // 根据value返回枚举类型,主要在switch中使用
    public static ChlNo getByValue(String  value) {
        if (IfspDataVerifyUtil.isBlank(value)) {
            return null;
        }
        for (ChlNo chlNo : values()) {
            if (chlNo.getCode().equals(value)) {
                return chlNo;
            }
        }
        return null;
    }




}
