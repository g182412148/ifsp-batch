package com.scrcu.ebank.ebap.batch.bean.vo;

import lombok.Data;


/**
 * <p>名称 : 商户分店查询-分店信息vo </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/8 </p>
 */
@Data
public class SubMchtInfoResultVo {
    /**
     * 商户号
     */
    private String mchtId;
    /**
     * 商户简称
     */
    private String mchtSimName;
    /**
     * 所属地区名称
     */
    private String AreaName;
    /**
     * 联系人姓名
     */
    private String staffName;
    /**
     * 联系人手机号
     */
    private String staffPhone;
    /**
     * 商户状态
     */
    private String mchtState;
    /**
     * 注册日期
     */
    private String regDate;
    /**
     * 结算账户账号
     */
    private String settlAcctNo;
    /**
     * 结算账户户名
     */
    private String settlAcctName;

}
