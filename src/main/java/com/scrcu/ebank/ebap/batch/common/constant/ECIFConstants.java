package com.scrcu.ebank.ebap.batch.common.constant;

/**
 * <p>名称 : ECIF字符常量 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/1 </p>
 */
public interface ECIFConstants {

    // 拼接字符
    String UNION = "|@|";
    // 换行符
    String NEW_LINE = "\r\n";

    // 商户性质 00-小微商户
    String MINI_MCHT_NAT = "00";
    // 商户性质 01-普通商户
    String NORMAL_MCHT_NAT = "01";
    // 负责人角色
    String LEGAL_ROLE = "01";
    // 联系人角色
    String CONTACT_ROLE = "02";

    // 上传文件路径
    String UP_FILE_PATH = "/home/ecif/upload/";
    // 上传文件路径
    String DOWN_FILE_PATH = "/home/ecif/download/";

}
