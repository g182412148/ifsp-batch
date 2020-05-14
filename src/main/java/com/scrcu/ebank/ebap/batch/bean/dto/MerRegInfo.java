package com.scrcu.ebank.ebap.batch.bean.dto;

import com.ruim.ifsp.utils.constant.IfspConstants;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class MerRegInfo extends CommonDTO {
    private static final long serialVersionUID = 1L;
    //导入标志
    private String opeFlag;
    //商户编号
    private String mchntCd;
    //商户服务类型
    private String mchntSvcTp;
    //商户中文名称
    private String mchntCnNm;
    //商户中文简称
    private String mchntCnAbbr;
    //收单机构代码
    private String acqInsIdCd;
    //国家代码
    private String cntryCd;
    //受理地区代码
    private String acqRegionCd;
    //交易商户类型
    private String mchntTp;
    //企业性质
    private String etpsAttr;
    //商户状态
    private String mchntSt;
    //营业证明文件类型
    private String netMchntSvcTp;
    //营业执照号码
    private String licNo;
    //商户经营地址
    private String bussAddr;
    //商户注册地址
    private String regAddr;
    //商户英文名称
    private String mchntEnNm;
    //法人代表姓名
    private String artifNm;
    //法人代表证件类型
    private String artifCertifTp;
    //法人代表证件号码
    private String artifCertifId;
    //商户联系人
    private String contactPersionNm;
    //商户联系人通讯地址
    private String commAddr;
    //商户联系人电话
    private String phone;
    //移动电话
    private String mobile;
    //商户拓展方式
    private String recnclTp;
    //收单外包服务机构
    private String principalNm;
    //商户开票开户银行名称
    private String cooking;
    //商户开票账号
    private String mchntIcp;
    //商户开票账户名称
    private String trafficLine;
    //是否申请优惠价格
    private String directAcqSettleIn;
    //商户现场注册标识码
    private String paySysSettleNo1;
    //特殊计费类型
    private String specDiscTp;
    //特殊计费档次
    private String specDiscLvl;
    //发卡银联分润算法
    private String allotAlgo;
    //贷记卡发卡银联分润算法
    private String allotCd;
    //商户计费算法
    private String mchntDiscDetIndex;
    //商户网址
    private String svcNetUrl;
    //网站或APP名称
    private String mchntWebSiteNm;
    //业务类型
    private String bussTp;
    //产品功能套餐
    private String prodFunc;
    //业务联系人邮箱
    private String bussCont1Email;
    //单笔限额
    private String singleAtLimit;
    //单笔限额说明
    private String singleAtLimitDesc;
    //单卡单日累计限额
    private String singleCardDayAtLimit;
    //单卡单日累计限额说明
    private String singleCardDayAtLimitDesc;
    //网络商户类型
    private String submchntIn;
    //ICP许可证编号
    private String svcInsNm;
    //总分店标志
    private String hdqrsBranchIn;
    //总店商户代码
    private String hdqrsMchntCd;
    //渠道接入商代码
    private String chnlMchntCd;
    //是否开通免密免签
    private String mccApplRule;
    //品牌
    private String masterPwd;
    //机构保留字段1
    private String insResv1;
    //机构保留字段2
    private String insResv2;
    //机构保留字段3
    private String insResv3;
    //机构保留字段4
    private String insResv4;
    //机构保留字段5
    private String insResv5;
    //机构保留字段6
    private String insResv6;
    //机构保留字段9
    private String insResv9;
    //机构保留字段10
    private String insResv10;
    //分公司保留字段1
    private String cupBranchResv1;

    public String getOpeFlag() {

        return opeFlag == null ? "" : opeFlag;
    }

    public void setOpeFlag(String opeFlag) {
        this.opeFlag = opeFlag;
    }

    public String getMchntCd() {

        return mchntCd == null ? "" : mchntCd;
    }

    public void setMchntCd(String mchntCd) {
        this.mchntCd = mchntCd;
    }

    public String getMchntSvcTp() {

        return mchntSvcTp == null ? "" : mchntSvcTp;
    }

    public void setMchntSvcTp(String mchntSvcTp) {
        this.mchntSvcTp = mchntSvcTp;
    }

    public String getMchntCnNm() {

        return mchntCnNm == null ? "" : mchntCnNm;
    }

    public void setMchntCnNm(String mchntCnNm) {
        this.mchntCnNm = mchntCnNm;
    }

    public String getMchntCnAbbr() {

        return mchntCnAbbr == null ? "" : mchntCnAbbr;
    }

    public void setMchntCnAbbr(String mchntCnAbbr) {
        this.mchntCnAbbr = mchntCnAbbr;
    }

    public String getAcqInsIdCd() {

        return acqInsIdCd == null ? "" : acqInsIdCd;
    }

    public void setAcqInsIdCd(String acqInsIdCd) {
        this.acqInsIdCd = acqInsIdCd;
    }

    public String getCntryCd() {

        return cntryCd == null ? "" : cntryCd;
    }

    public void setCntryCd(String cntryCd) {
        this.cntryCd = cntryCd;
    }

    public String getAcqRegionCd() {

        return acqRegionCd == null ? "" : acqRegionCd;
    }

    public void setAcqRegionCd(String acqRegionCd) {
        this.acqRegionCd = acqRegionCd;
    }

    public String getMchntTp() {

        return mchntTp == null ? "" : mchntTp;
    }

    public void setMchntTp(String mchntTp) {
        this.mchntTp = mchntTp;
    }

    public String getEtpsAttr() {

        return etpsAttr == null ? "" : etpsAttr;
    }

    public void setEtpsAttr(String etpsAttr) {
        this.etpsAttr = etpsAttr;
    }

    public String getMchntSt() {

        return mchntSt == null ? "" : mchntSt;
    }

    public void setMchntSt(String mchntSt) {
        this.mchntSt = mchntSt;
    }

    public String getNetMchntSvcTp() {

        return netMchntSvcTp == null ? "" : netMchntSvcTp;
    }

    public void setNetMchntSvcTp(String netMchntSvcTp) {
        this.netMchntSvcTp = netMchntSvcTp;
    }

    public String getLicNo() {

        return licNo == null ? "" : licNo;
    }

    public void setLicNo(String licNo) {
        this.licNo = licNo;
    }

    public String getBussAddr() {

        return bussAddr == null ? "" : bussAddr;
    }

    public void setBussAddr(String bussAddr) {
        this.bussAddr = bussAddr;
    }

    public String getRegAddr() {

        return regAddr == null ? "" : regAddr;
    }

    public void setRegAddr(String regAddr) {
        this.regAddr = regAddr;
    }

    public String getMchntEnNm() {

        return mchntEnNm == null ? "" : mchntEnNm;
    }

    public void setMchntEnNm(String mchntEnNm) {
        this.mchntEnNm = mchntEnNm;
    }

    public String getArtifNm() {

        return artifNm == null ? "" : artifNm;
    }

    public void setArtifNm(String artifNm) {
        this.artifNm = artifNm;
    }

    public String getArtifCertifTp() {

        return artifCertifTp == null ? "" : artifCertifTp;
    }

    public void setArtifCertifTp(String artifCertifTp) {
        this.artifCertifTp = artifCertifTp;
    }

    public String getArtifCertifId() {

        return artifCertifId == null ? "" : artifCertifId;
    }

    public void setArtifCertifId(String artifCertifId) {
        this.artifCertifId = artifCertifId;
    }

    public String getContactPersionNm() {

        return contactPersionNm == null ? "" : contactPersionNm;
    }

    public void setContactPersionNm(String contactPersionNm) {
        this.contactPersionNm = contactPersionNm;
    }

    public String getCommAddr() {

        return commAddr == null ? "" : commAddr;
    }

    public void setCommAddr(String commAddr) {
        this.commAddr = commAddr;
    }

    public String getPhone() {

        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {

        return mobile == null ? "" : mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRecnclTp() {

        return recnclTp == null ? "" : recnclTp;
    }

    public void setRecnclTp(String recnclTp) {
        this.recnclTp = recnclTp;
    }

    public String getPrincipalNm() {

        return principalNm == null ? "" : principalNm;
    }

    public void setPrincipalNm(String principalNm) {
        this.principalNm = principalNm;
    }

    public String getCooking() {

        return cooking == null ? "" : cooking;
    }

    public void setCooking(String cooking) {
        this.cooking = cooking;
    }

    public String getMchntIcp() {

        return mchntIcp == null ? "" : mchntIcp;
    }

    public void setMchntIcp(String mchntIcp) {
        this.mchntIcp = mchntIcp;
    }

    public String getTrafficLine() {

        return trafficLine == null ? "" : trafficLine;
    }

    public void setTrafficLine(String trafficLine) {
        this.trafficLine = trafficLine;
    }

    public String getDirectAcqSettleIn() {

        return directAcqSettleIn == null ? "" : directAcqSettleIn;
    }

    public void setDirectAcqSettleIn(String directAcqSettleIn) {
        this.directAcqSettleIn = directAcqSettleIn;
    }

    public String getPaySysSettleNo1() {

        return paySysSettleNo1 == null ? "" : paySysSettleNo1;
    }

    public void setPaySysSettleNo1(String paySysSettleNo1) {
        this.paySysSettleNo1 = paySysSettleNo1;
    }

    public String getSpecDiscTp() {

        return specDiscTp == null ? "" : specDiscTp;
    }

    public void setSpecDiscTp(String specDiscTp) {
        this.specDiscTp = specDiscTp;
    }

    public String getSpecDiscLvl() {

        return specDiscLvl == null ? "" : specDiscLvl;
    }

    public void setSpecDiscLvl(String specDiscLvl) {
        this.specDiscLvl = specDiscLvl;
    }

    public String getAllotAlgo() {

        return allotAlgo == null ? "" : allotAlgo;
    }

    public void setAllotAlgo(String allotAlgo) {
        this.allotAlgo = allotAlgo;
    }

    public String getAllotCd() {

        return allotCd == null ? "" : allotCd;
    }

    public void setAllotCd(String allotCd) {
        this.allotCd = allotCd;
    }

    public String getMchntDiscDetIndex() {

        return mchntDiscDetIndex == null ? "" : mchntDiscDetIndex;
    }

    public void setMchntDiscDetIndex(String mchntDiscDetIndex) {
        this.mchntDiscDetIndex = mchntDiscDetIndex;
    }

    public String getSvcNetUrl() {

        return svcNetUrl == null ? "" : svcNetUrl;
    }

    public void setSvcNetUrl(String svcNetUrl) {
        this.svcNetUrl = svcNetUrl;
    }

    public String getMchntWebSiteNm() {

        return mchntWebSiteNm == null ? "" : mchntWebSiteNm;
    }

    public void setMchntWebSiteNm(String mchntWebSiteNm) {
        this.mchntWebSiteNm = mchntWebSiteNm;
    }

    public String getBussTp() {

        return bussTp == null ? "" : bussTp;
    }

    public void setBussTp(String bussTp) {
        this.bussTp = bussTp;
    }

    public String getProdFunc() {

        return prodFunc == null ? "" : prodFunc;
    }

    public void setProdFunc(String prodFunc) {
        this.prodFunc = prodFunc;
    }

    public String getBussCont1Email() {

        return bussCont1Email == null ? "" : bussCont1Email;
    }

    public void setBussCont1Email(String bussCont1Email) {
        this.bussCont1Email = bussCont1Email;
    }

    public String getSingleAtLimit() {

        return singleAtLimit == null ? "" : singleAtLimit;
    }

    public void setSingleAtLimit(String singleAtLimit) {
        this.singleAtLimit = singleAtLimit;
    }

    public String getSingleAtLimitDesc() {

        return singleAtLimitDesc == null ? "" : singleAtLimitDesc;
    }

    public void setSingleAtLimitDesc(String singleAtLimitDesc) {
        this.singleAtLimitDesc = singleAtLimitDesc;
    }

    public String getSingleCardDayAtLimit() {

        return singleCardDayAtLimit == null ? "" : singleCardDayAtLimit;
    }

    public void setSingleCardDayAtLimit(String singleCardDayAtLimit) {
        this.singleCardDayAtLimit = singleCardDayAtLimit;
    }

    public String getSingleCardDayAtLimitDesc() {

        return singleCardDayAtLimitDesc == null ? "" : singleCardDayAtLimitDesc;
    }

    public void setSingleCardDayAtLimitDesc(String singleCardDayAtLimitDesc) {
        this.singleCardDayAtLimitDesc = singleCardDayAtLimitDesc;
    }

    public String getSubmchntIn() {

        return submchntIn == null ? "" : submchntIn;
    }

    public void setSubmchntIn(String submchntIn) {
        this.submchntIn = submchntIn;
    }

    public String getSvcInsNm() {

        return svcInsNm == null ? "" : svcInsNm;
    }

    public void setSvcInsNm(String svcInsNm) {
        this.svcInsNm = svcInsNm;
    }

    public String getHdqrsBranchIn() {

        return hdqrsBranchIn == null ? "" : hdqrsBranchIn;
    }

    public void setHdqrsBranchIn(String hdqrsBranchIn) {
        this.hdqrsBranchIn = hdqrsBranchIn;
    }

    public String getHdqrsMchntCd() {

        return hdqrsMchntCd == null ? "" : hdqrsMchntCd;
    }

    public void setHdqrsMchntCd(String hdqrsMchntCd) {
        this.hdqrsMchntCd = hdqrsMchntCd;
    }

    public String getChnlMchntCd() {

        return chnlMchntCd == null ? "" : chnlMchntCd;
    }

    public void setChnlMchntCd(String chnlMchntCd) {
        this.chnlMchntCd = chnlMchntCd;
    }

    public String getMccApplRule() {

        return mccApplRule == null ? "" : mccApplRule;
    }

    public void setMccApplRule(String mccApplRule) {
        this.mccApplRule = mccApplRule;
    }

    public String getMasterPwd() {

        return masterPwd == null ? "" : masterPwd;
    }

    public void setMasterPwd(String masterPwd) {
        this.masterPwd = masterPwd;
    }

    public String getInsResv1() {

        return insResv1 == null ? "" : insResv1;
    }

    public void setInsResv1(String insResv1) {
        this.insResv1 = insResv1;
    }

    public String getInsResv2() {

        return insResv2 == null ? "" : insResv2;
    }

    public void setInsResv2(String insResv2) {
        this.insResv2 = insResv2;
    }

    public String getInsResv3() {

        return insResv3 == null ? "" : insResv3;
    }

    public void setInsResv3(String insResv3) {
        this.insResv3 = insResv3;
    }

    public String getInsResv4() {

        return insResv4 == null ? "" : insResv4;
    }

    public void setInsResv4(String insResv4) {
        this.insResv4 = insResv4;
    }

    public String getInsResv5() {

        return insResv5 == null ? "" : insResv5;
    }

    public void setInsResv5(String insResv5) {
        this.insResv5 = insResv5;
    }

    public String getInsResv6() {

        return insResv6 == null ? "" : insResv6;
    }

    public void setInsResv6(String insResv6) {
        this.insResv6 = insResv6;
    }

    public String getInsResv9() {

        return insResv9 == null ? "" : insResv9;
    }

    public void setInsResv9(String insResv9) {
        this.insResv9 = insResv9;
    }

    public String getInsResv10() {

        return insResv10 == null ? "" : insResv10;
    }

    public void setInsResv10(String insResv10) {
        this.insResv10 = insResv10;
    }

    public String getCupBranchResv1() {

        return cupBranchResv1 == null ? "" : cupBranchResv1;
    }

    public void setCupBranchResv1(String cupBranchResv1) {
        this.cupBranchResv1 = cupBranchResv1;
    }

    public MerRegInfo() {
        super();
    }
    

    public String genRecordNew(){
        StringBuffer sb = new StringBuffer();
        return sb.append(getOpeFlag()).append(",")
                .append(getMchntCd()).append(",")
                .append(getMchntSvcTp()).append(",")
                .append(getMchntCnNm()).append(",")
                .append(getMchntCnAbbr()).append(",")
                .append(getAcqInsIdCd()).append(",")
                .append(getCntryCd()).append(",")
                .append(getAcqRegionCd()).append(",")
                .append(getMchntTp()).append(",")
                .append(getEtpsAttr()).append(",")
                .append(getMchntSt()).append(",")
                .append(getNetMchntSvcTp()).append(",")
                .append(getLicNo()).append(",")
                .append(getBussAddr()).append(",")
                .append(getRegAddr()).append(",")
                .append(getMchntEnNm()).append(",")
                .append(getArtifNm()).append(",")
                .append(getArtifCertifTp()).append(",")
                .append(getArtifCertifId()).append(",")
                .append(getContactPersionNm()).append(",")
                .append(getCommAddr()).append(",")
                .append(getPhone()).append(",")
                .append(getMobile()).append(",")
                .append(getRecnclTp()).append(",")
                .append(getPrincipalNm()).append(",")
                .append(getCooking()).append(",")
                .append(getMchntIcp()).append(",")
                .append(getTrafficLine()).append(",")
                .append(getDirectAcqSettleIn()).append(",")
                .append(getPaySysSettleNo1()).append(",")
                .append(getSpecDiscTp()).append(",")
                .append(getSpecDiscLvl()).append(",")
                .append(getAllotAlgo()).append(",")
                .append(getAllotCd()).append(",")
                .append(getMchntDiscDetIndex()).append(",")
                .append(getSvcNetUrl()).append(",")
                .append(getMchntWebSiteNm()).append(",")
                .append(getBussTp()).append(",")
                .append(getProdFunc()).append(",")
                .append(getBussCont1Email()).append(",")
                .append(getSingleAtLimit()).append(",")
                .append(getSingleAtLimitDesc()).append(",")
                .append(getSingleCardDayAtLimit()).append(",")
                .append(getSingleCardDayAtLimitDesc()).append(",")
                .append(getSubmchntIn()).append(",")
                .append(getSvcInsNm()).append(",")
                .append(getHdqrsBranchIn()).append(",")
                .append(getHdqrsMchntCd()).append(",")
                .append(getChnlMchntCd()).append(",")
                .append(getMccApplRule()).append(",")
                .append(getMasterPwd()).append(",")
                .append(getInsResv1()).append(",")
                .append(getInsResv2()).append(",")
                .append(getInsResv3()).append(",")
                .append(getInsResv4()).append(",")
                .append(getInsResv5()).append(",")
                .append(getInsResv6()).append(",")
                .append(getInsResv9()).append(",")
                .append(getInsResv10()).append(",")
                .append(getCupBranchResv1()).append("\r\n")
                .toString();
    }
    
}