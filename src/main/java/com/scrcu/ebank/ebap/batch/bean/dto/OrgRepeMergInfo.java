package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class OrgRepeMergInfo extends CommonDTO {
    private String repeMergId;

    private String repeOrg;

    private String mergOrg;

    private String mergStus;

    private Date mergDt;

    private Date craDt;

    private Date updDt;

    public String getRepeMergId() {
        return repeMergId;
    }

    public void setRepeMergId(String repeMergId) {
        this.repeMergId = repeMergId == null ? null : repeMergId.trim();
    }

    public String getRepeOrg() {
        return repeOrg;
    }

    public void setRepeOrg(String repeOrg) {
        this.repeOrg = repeOrg == null ? null : repeOrg.trim();
    }

    public String getMergOrg() {
        return mergOrg;
    }

    public void setMergOrg(String mergOrg) {
        this.mergOrg = mergOrg == null ? null : mergOrg.trim();
    }

    public String getMergStus() {
        return mergStus;
    }

    public void setMergStus(String mergStus) {
        this.mergStus = mergStus == null ? null : mergStus.trim();
    }

    public Date getMergDt() {
        return mergDt;
    }

    public void setMergDt(Date mergDt) {
        this.mergDt = mergDt;
    }

    public Date getCraDt() {
        return craDt;
    }

    public void setCraDt(Date craDt) {
        this.craDt = craDt;
    }

    public Date getUpdDt() {
        return updDt;
    }

    public void setUpdDt(Date updDt) {
        this.updDt = updDt;
    }
}