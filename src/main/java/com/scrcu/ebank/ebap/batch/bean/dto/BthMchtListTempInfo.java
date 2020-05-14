package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

import java.util.Date;

public class BthMchtListTempInfo extends CommonDTO {
    private String mchtId;

    private String chkDate;

    private String status;

    /**
     * 创建时间
     */
    private Date crtTm;

    /**
     * 更新时间
     */
    private Date lastUpdTm;
    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId == null ? null : mchtId.trim();
    }

    public String getChkDate() {
        return chkDate;
    }

    public void setChkDate(String chkDate) {
        this.chkDate = chkDate == null ? null : chkDate.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Date getLastUpdTm() {
        return lastUpdTm;
    }

    public void setLastUpdTm(Date lastUpdTm) {
        this.lastUpdTm = lastUpdTm;
    }
}