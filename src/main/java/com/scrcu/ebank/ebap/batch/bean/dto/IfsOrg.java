package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class IfsOrg extends CommonDTO {
    private String brId;

    private String brName;

    private String brSimName;

    private String brLvl;

    private String brType;

    private String parBrId;

    private String brCuId;

    private String brFic;

    private String brPbocId;

    private String brLmName;

    private String brLmPhone;

    private String brTel;

    private String brZipCode;

    private String areaCode;

    private String brLmAddr;

    private String brState;

    private String corpFlag;

    private String updFlag;

    private Date crtDt;

    private String crtTlr;

    private Date updDt;

    private String updTlr;

    private String corpId;

    private String fax;

    public String getBrId() {
        return brId;
    }

    public void setBrId(String brId) {
        this.brId = brId == null ? null : brId.trim();
    }

    public String getBrName() {
        return brName;
    }

    public void setBrName(String brName) {
        this.brName = brName == null ? null : brName.trim();
    }

    public String getBrSimName() {
        return brSimName;
    }

    public void setBrSimName(String brSimName) {
        this.brSimName = brSimName == null ? null : brSimName.trim();
    }

    public String getBrLvl() {
        return brLvl;
    }

    public void setBrLvl(String brLvl) {
        this.brLvl = brLvl == null ? null : brLvl.trim();
    }

    public String getBrType() {
        return brType;
    }

    public void setBrType(String brType) {
        this.brType = brType == null ? null : brType.trim();
    }

    public String getParBrId() {
        return parBrId;
    }

    public void setParBrId(String parBrId) {
        this.parBrId = parBrId == null ? null : parBrId.trim();
    }

    public String getBrCuId() {
        return brCuId;
    }

    public void setBrCuId(String brCuId) {
        this.brCuId = brCuId == null ? null : brCuId.trim();
    }

    public String getBrFic() {
        return brFic;
    }

    public void setBrFic(String brFic) {
        this.brFic = brFic == null ? null : brFic.trim();
    }

    public String getBrPbocId() {
        return brPbocId;
    }

    public void setBrPbocId(String brPbocId) {
        this.brPbocId = brPbocId == null ? null : brPbocId.trim();
    }

    public String getBrLmName() {
        return brLmName;
    }

    public void setBrLmName(String brLmName) {
        this.brLmName = brLmName == null ? null : brLmName.trim();
    }

    public String getBrLmPhone() {
        return brLmPhone;
    }

    public void setBrLmPhone(String brLmPhone) {
        this.brLmPhone = brLmPhone == null ? null : brLmPhone.trim();
    }

    public String getBrTel() {
        return brTel;
    }

    public void setBrTel(String brTel) {
        this.brTel = brTel == null ? null : brTel.trim();
    }

    public String getBrZipCode() {
        return brZipCode;
    }

    public void setBrZipCode(String brZipCode) {
        this.brZipCode = brZipCode == null ? null : brZipCode.trim();
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode == null ? null : areaCode.trim();
    }

    public String getBrLmAddr() {
        return brLmAddr;
    }

    public void setBrLmAddr(String brLmAddr) {
        this.brLmAddr = brLmAddr == null ? null : brLmAddr.trim();
    }

    public String getBrState() {
        return brState;
    }

    public void setBrState(String brState) {
        this.brState = brState == null ? null : brState.trim();
    }

    public String getCorpFlag() {
        return corpFlag;
    }

    public void setCorpFlag(String corpFlag) {
        this.corpFlag = corpFlag == null ? null : corpFlag.trim();
    }

    public String getUpdFlag() {
        return updFlag;
    }

    public void setUpdFlag(String updFlag) {
        this.updFlag = updFlag == null ? null : updFlag.trim();
    }

    public Date getCrtDt() {
        return crtDt;
    }

    public void setCrtDt(Date crtDt) {
        this.crtDt = crtDt;
    }

    public String getCrtTlr() {
        return crtTlr;
    }

    public void setCrtTlr(String crtTlr) {
        this.crtTlr = crtTlr == null ? null : crtTlr.trim();
    }

    public Date getUpdDt() {
        return updDt;
    }

    public void setUpdDt(Date updDt) {
        this.updDt = updDt;
    }

    public String getUpdTlr() {
        return updTlr;
    }

    public void setUpdTlr(String updTlr) {
        this.updTlr = updTlr == null ? null : updTlr.trim();
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId == null ? null : corpId.trim();
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax == null ? null : fax.trim();
    }
}