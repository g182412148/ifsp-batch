package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MchtExtInfo extends MchtBaseInfo{

    /** 地区编号 */
    private String pagyCtCode;

    private String ownerName;//法人/负责人姓名
    private String ownerPhone;//法人/负责人手机号
    private String ownerCertType;//法人/负责人证件类型
    private String ownerCertNo;//法人/负责人证件编号
    private Date ownerCertEffDate;//法人/负责人证件生效时间
    private Date ownerCertExpDate;//法人/负责人证件失效时间
    private String ownerFrontCertPic;//法人/负责人证件正面图片
    private String ownerBackCertPic;//法人/负责人证件反面图片

}
