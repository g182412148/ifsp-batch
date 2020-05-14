package com.scrcu.ebank.ebap.batch.bean.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StaffInfoVO {

    /** 员工编号 */
    private String staffId;

    /** 员工角色 */
    private String staffRole;

    /** 员工名称 */
    private String staffName;

    /** 证件类型 */
    private String certType;

    /** 证件号码 */
    private String certNo;

    /** 加密编号 */
    private String encryptId;

    /** 加密随机数 */
    private String encryptCode;

    /** 用户状态 */
    private String staffState;

    /** 普通密码 */
    private String loginPw;

    /** 用户是否允许登录 */
    private String allowLogin;

    /** 普通密码错误次数 */
    private int loginPwErrorCnt;

    /** 普通密码错误次数更新时间 */
    private Date loginPwErrorTm;

    /** 手势密码开启标识 */
    private String gestureFlag;

    /** 手势密码 */
    private String gestPw;

    /** 手势密码错误次数 */
    private int gestPwErrorCnt;

    /** 手势密码错误次数更新时间 */
    private Date gestPwErrorTm;

    /** 密码强制修改标志:0-不需要 1-需要 */
    private String forceUpdFlag;

    /** 最近一次登陆时间 */
    private Date lastLoginTm;

    /** 手机号 */
    private String staffPhone;

    /** 商户号 */
    private String mchtId;

    /** 商户状态 */
    private String mchtState;

    /** 商户简称 */
    private String mchtSimName;

    /** 商户地区码 */
    private String areaNo;

    /** 商户MCC码组别 */
    private String mccGroupNo;

    /** 商户MCC码 */
    private String mccNo;

    /** 收单机构编号 */
    private String orgId;

    /** 收单机构名称 */
    private String orgName;

}
