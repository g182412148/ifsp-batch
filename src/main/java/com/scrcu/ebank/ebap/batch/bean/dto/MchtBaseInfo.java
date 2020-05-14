package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
public class MchtBaseInfo extends CommonDTO {
    private String mchtId;

    private String mchtName;

    private String mchtSimName;

    private String areaNo;

    private String mchtAddr;

    private String mchtType;

    private String mchtNat;

    private String platFlag;

    private String mchtSrc;

    private String mchtState;

    private String parMchId;

    private String cmNo;

    private String cmName;

    private String blNo;

    private String blType;

    private String blExpType;

    private Date blExpDate;

    private String mccGroupNo;

    private String mccNo;

    private String icpNo;

    private String webSiteAddr;

    private Date regDate;

    private String crtTlr;

    private Date crtTm;

    private String openClientId;

    private String updTlr;

    private Date updTm;

    private String mchtClass;

    private BigDecimal mchtLimitForCust;

    private String mchtDesc;

    private String rpsOrgNo;    //收单机构

    private String opOrgNo;    //运营机构，当为委托模式是存放商户运营商合作方法代码

    private String merOpType;  //0 - 直营，1 - 委托

    private String platPartnerCode;   //平台合作方代码

    private String mchtEnName;   //商户英文名称

    private String email;

    private String appName;

    private String chkFlag;   //是否需要对账文件: 0 - 不需要, 1 - 需要

    private String mchtOneClass;//商户一级分类
    private String mchtTwoClass;//商户二级分类

    private String addrLng;
    private String addrLat;
    private String addrCode;
    private String addrData;
    private String addrPrecise;
    private String addrConfidence;
    private String addrComprehension;
    private String addrCodeMsg;

    private String ownerName;//法人/负责人姓名
    private String ownerPhone;//法人/负责人手机号
    private String ownerCertType;//法人/负责人证件类型
    private String ownerCertNo;//法人/负责人证件编号
    private Date ownerCertEffDate;//法人/负责人证件生效时间
    private Date ownerCertExpDate;//法人/负责人证件失效时间
    private String ownerFrontCertPic;//法人/负责人证件正面图片
    private String ownerBackCertPic;//法人/负责人证件反面图片

    private String isChoiceness;//是否精选商户
    private String recommendReason;//推荐理由
    private String storePlace;//所在商圈

    private String ecifCustomerNo;//ECIf客户号

    private String orderTmMax;//最近交易时间

}