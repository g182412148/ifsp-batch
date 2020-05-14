package com.scrcu.ebank.ebap.batch.bean.vo.ecif;

import lombok.Data;

import java.util.Date;

/**
 * <p>名称 : ECIF数据库查询结果对象 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/7/22 </p>
 */
@Data
public class ECIFMchtResultVo {
    /**
     * 商户号
     */
    private String mchtId;
    /**
     * 客户号
     */
    private String ecifCustomerNo;
    /**
     * 法人/负责人姓名
     */
    private String legalName;
    /**
     * 法人/负责人手机号
     */
    private String legalPhone;
    /**
     * 证件类型
     */
    private String legalCertType;
    /**
     * 证件编号
     */
    private String legalCertNo;
    /**
     * 证件有效期
     */
    private Date legalCertExpDate;
    /**
     * 商户种类
     */
    private String mchtNat;
    /**
     * 商户名称
     */
    private String mchtName;
    /**
     * 商户简称
     */
    private String mchtSimName;
    /**
     * 地区编号
     */
    private String areaNo;
    /**
     * 详细地址
     */
    private String mchtAddr;
    /**
     * 商户类别
     */
    private String mchtType;
    /**
     * 联系人名字
     */
    private String norName;
    /**
     * 联系人电话
     */
    private String norPhone;
    /**
     * 营业执照号码
     */
    private String blNo;
    /**
     * 营业执照有效期类型
     */
    private String blExpType;
    /**
     * 有效期至
     */
    private Date blExpDate;
}
