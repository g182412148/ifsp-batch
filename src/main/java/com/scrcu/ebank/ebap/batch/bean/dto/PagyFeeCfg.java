package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class PagyFeeCfg extends CommonDTO {
    private String cfgId;

    private String pagyNo;

    private String startTm;

    private String endTm;

    private String cfgDesc;

    private String cfgTm;

    private String cfgTlrNo;

    private String lstUpdTm;

    private String lstUpdTlr;

    public String getCfgId() {
        return cfgId;
    }

    public void setCfgId(String cfgId) {
        this.cfgId = cfgId == null ? null : cfgId.trim();
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getStartTm() {
        return startTm;
    }

    public void setStartTm(String startTm) {
        this.startTm = startTm == null ? null : startTm.trim();
    }

    public String getEndTm() {
        return endTm;
    }

    public void setEndTm(String endTm) {
        this.endTm = endTm == null ? null : endTm.trim();
    }

    public String getCfgDesc() {
        return cfgDesc;
    }

    public void setCfgDesc(String cfgDesc) {
        this.cfgDesc = cfgDesc == null ? null : cfgDesc.trim();
    }

    public String getCfgTm() {
        return cfgTm;
    }

    public void setCfgTm(String cfgTm) {
        this.cfgTm = cfgTm == null ? null : cfgTm.trim();
    }

    public String getCfgTlrNo() {
        return cfgTlrNo;
    }

    public void setCfgTlrNo(String cfgTlrNo) {
        this.cfgTlrNo = cfgTlrNo == null ? null : cfgTlrNo.trim();
    }

    public String getLstUpdTm() {
        return lstUpdTm;
    }

    public void setLstUpdTm(String lstUpdTm) {
        this.lstUpdTm = lstUpdTm == null ? null : lstUpdTm.trim();
    }

    public String getLstUpdTlr() {
        return lstUpdTlr;
    }

    public void setLstUpdTlr(String lstUpdTlr) {
        this.lstUpdTlr = lstUpdTlr == null ? null : lstUpdTlr.trim();
    }
}