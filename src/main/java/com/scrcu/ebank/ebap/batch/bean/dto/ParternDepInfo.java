package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class ParternDepInfo extends ParternBaseInfo{
   
    private String depAcctNo;
    private String depAmt;
    private String depIntFlag;
    private String depIntRate;
    private String openClientId;
    private String cmName;
    private String cmPhone;
    private String guaranteeDeposit;

    public String getDepAcctNo() {
        return depAcctNo;
    }

    public void setDepAcctNo(String depAcctNo) {
        this.depAcctNo = depAcctNo;
    }

    public String getDepAmt() {
        return depAmt;
    }

    public void setDepAmt(String depAmt) {
        this.depAmt = depAmt;
    }

    public String getDepIntFlag() {
        return depIntFlag;
    }

    public void setDepIntFlag(String depIntFlag) {
        this.depIntFlag = depIntFlag;
    }

    public String getDepIntRate() {
        return depIntRate;
    }

    public void setDepIntRate(String depIntRate) {
        this.depIntRate = depIntRate;
    }

    public String getOpenClientId() {
        return openClientId;
    }

    public void setOpenClientId(String openClientId) {
        this.openClientId = openClientId;
    }

    public String getCmName() {
        return cmName;
    }

    public void setCmName(String cmName) {
        this.cmName = cmName;
    }

    public String getCmPhone() {
        return cmPhone;
    }

    public void setCmPhone(String cmPhone) {
        this.cmPhone = cmPhone;
    }

    public String getGuaranteeDeposit() {
        return guaranteeDeposit;
    }

    public void setGuaranteeDeposit(String guaranteeDeposit) {
        this.guaranteeDeposit = guaranteeDeposit;
    }
}
