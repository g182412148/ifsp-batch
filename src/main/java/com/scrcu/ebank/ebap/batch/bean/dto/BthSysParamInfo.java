package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class BthSysParamInfo extends CommonDTO {
    private String papamCode;

    private String paramInfo;

    private String paramDesc;

    private String crtTlr;

    private Date crtTm;

    private String lastUpdTlr;

    private Date lastUpdTm;

    public String getPapamCode() {
        return papamCode;
    }

    public void setPapamCode(String papamCode) {
        this.papamCode = papamCode == null ? null : papamCode.trim();
    }

    public String getParamInfo() {
        return paramInfo;
    }

    public void setParamInfo(String paramInfo) {
        this.paramInfo = paramInfo == null ? null : paramInfo.trim();
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc == null ? null : paramDesc.trim();
    }

    public String getCrtTlr() {
        return crtTlr;
    }

    public void setCrtTlr(String crtTlr) {
        this.crtTlr = crtTlr == null ? null : crtTlr.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public String getLastUpdTlr() {
        return lastUpdTlr;
    }

    public void setLastUpdTlr(String lastUpdTlr) {
        this.lastUpdTlr = lastUpdTlr == null ? null : lastUpdTlr.trim();
    }

    public Date getLastUpdTm() {
        return lastUpdTm;
    }

    public void setLastUpdTm(Date lastUpdTm) {
        this.lastUpdTm = lastUpdTm;
    }
}