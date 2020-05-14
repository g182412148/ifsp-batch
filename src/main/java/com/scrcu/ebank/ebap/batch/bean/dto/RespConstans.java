package com.scrcu.ebank.ebap.batch.bean.dto;

/**
 * 响应码枚举类
 * @author louc
 */
public enum RespConstans {

    RESP_SUCCESS("0000", "交易成功"),
    RESP_MCHT_MINI_APPLY("0001", "小微商户已申请"),
    RESP_MCHT_COMMON_APPLY("0002", "普通商户已申请"),
    RESP_USER_NOT_EXIST("0003", "手机号不存在，请重新输入"),
    RESP_CANNOT_RESET_PWD("0004", "店员无法重置密码，需联系店长"),
    RESP_CERT_FALSE("0005", "手机号与证件号码不匹配"),
    RESP_MCHT_APPLY_CHECK("0006","手机号已存在对应的商户申请记录!"),
    RESP_SMS_CHECK("0007","短信验证失败!"),
    RESP_SMS_SEND("0008","短信发送响应失败!"),
    RESP_NOUSERID("0009","UserId不能为空"),
    RESP_MCHT_STAFF_INFO_QUERY("0010","用户信息没有查询到结果!"),
    RESP_MCHT_STAFF_INFO_ADD("0011","此电话号码已申请过店员!"),
    RESP_MCHT_STAFF_REL_NONE("0012","没有查询到当前用户的人员关联信息!"),
    RESP_MCHT_STAFF_MSG_NONE("0013","没有查询到当前用户类型的相关消息记录!"),
    RESP_CERT_TM_ERROR("0014", "证件生效日期不能大于失效时期"),
    RESP_CERT_EXP("0015", "证件已过期"),
    RESP_ENCRYPT_FAIL("0016","没有查询到用户信息!"),
    RESP_PWD_ERROR("0017","密码错误!"),
    RESP_UNKNOW_CERT_TYPE("0018", "未知的证件类型"),
    RESP_NONE_CUST("0019", "非本行客户"),
    RESP_NONE_BR_INFO("0020", "查无此机构"),
    RESP_NONE_BR_TYPE("0021", "无法获取该机构的机构类型"),
    RESP_NONE_CAEATE_MCHT("0022", "该机构下没有营业机构，无法新增商户"),
    RESP_NONE_PAGE("0023", "分页参数不能为空"),
    RESP_NONE_PAGE_SIZE("0024", "条数不能为空"),
    RESP_NONE_PAGE_NO("0025", "页码不能为空"),
    RESP_NO_BR_ID("0026", "机构号不能为空"),
    RESP_ILLEGAL_CHNL("0027", "请求渠道非法"),
    RESP_ERR_SMS_CODE("0028", "短信验证码校验失败"),
    RESP_NO_USER("0029", "用户不存在"),
    RESP_MCHT_ERST("0030", "商户状态异常"),
    RESP_USER_ERST("0031", "用户状态异常"),
    RESP_GEST_TMP_LOCK("0032", "手势密码临时锁定, 请使用普通密码登录或次日登录"),
    RESP_GEST_LOCK("0033", "手势密码永久锁定, 请使用普通密码登录"),
    RESP_COMM_TMP_LOCK("0034", "普通密码临时锁定, 请使用忘记密码或次日登录"),
    RESP_COMM_LOCK("0035", "普通密码永久锁定, 请使用忘记密码重置密码"),
    RESP_GEST_OFF("0036", "手势密码未开启"),
    RESP_ERR_PWD("0037", "密码错误"),
    RESP_ERR_ORIG_PWD("0038", "原密码错误"),
    RESP_NO_MCHT("0039", "查询不到该商户"),
    RESP_NO_EMP("0040", "查询不到该店员信息"),
    RESP_EMP_REST_ON("0041", "店员状态异常，不能启用"),
    RESP_EMP_REST_OFF("0042", "店员状态异常，不能停用"),
    RESP_NO_CONT("0043", "商户未签订合同"),
    RESP_CONT_REST("0044", "商户合同状态不正确"),
    RESP_NO_AUTH("0045", "商户无交易权限"),
    RESP_ERR_OPR("0046", "手势密码操作类型不正确"),
    RESP_AREA_NIL_BR("0047","所选地区没有对应的机构!"),
    RESP_PRINT_OFF("0048","未开启打印"),
    RESP_NO_RESULTFILE("3100","核心入账结果文件.ok不存在!"),
    RESP_CLEARING_RESULT("3101","对账结果为空!"),
    RESP_NO_MULTIBANKRESULT("3102","杭外核心入账结果文件.ok不存在!"),
    RESP_NO_TRANSACTION_RECORDS("3103","所选日期暂无交易记录"),
    RESP_QUERYDATE_GT_CURRENTDATE("3104","查询日期大于系统时间"),
    RESP_QUERYDATE_LT_CURRENTDATEONEYEAR("3105","查询日期不能小于当前时间的一年"),
    RESP_MCHT_YESTERDAY_CURRENTDATEONEYEAR("0000","商户当天没有交易"),
    RESP_MCHTSIZE_TOOLONG("3107","商户号长度超过32位"),
    RESP_STARTTIME_GT_ENDTIME("3108","开始时间不能大于结束时间"),
    RESP_ENDTIME_GT_CURRENTTIMEYESTERDAY("3109","查询结束日期大于系统时间前一天"),
    RESP_QUERYTIME_GT_ONEMONTH("3110","开始时间和结束时间的间隔不能大于一个月"),
    RESP_ACCOUNT_NORESULTFILE("3111","入账结果文件不存在!"),
    RESP_NO_SUBBRANCH("3112","没有分店!"),
    RESP_NO_WEEK_DEAL("3113","当前时间段没有数据"),
    RESP_NO_RESULTFILES("0000","当前没有入账结果文件"),

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
