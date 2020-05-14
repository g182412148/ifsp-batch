package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class BthPagyChkInf extends CommonDTO {
    private String pagyNo;

    private Date chkDataDt;

    private String pagySysNo;

    private Date reqChkTm;

    private String getChkSt;

    private String getChkRspcode;

    private String getChkRspmsg;

    private String chkFilePath;

    private String chkFileImpSt;

    private Date chkFileWrtm;

    private Date chkAcctSttm;

    private String chkAcctSt;

    private String chkAcctMsg;

    private Date chkAcctEdtm;

    private String chkAcctRst;

    private String chkRmk;

    private Date crtTm;

    private Date lstUpdTm;

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public Date getChkDataDt() {
        return chkDataDt;
    }

    public void setChkDataDt(Date chkDataDt) {
        this.chkDataDt = chkDataDt;
    }

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public Date getReqChkTm() {
        return reqChkTm;
    }

    public void setReqChkTm(Date reqChkTm) {
        this.reqChkTm = reqChkTm;
    }

    public String getGetChkSt() {
        return getChkSt;
    }

    public void setGetChkSt(String getChkSt) {
        this.getChkSt = getChkSt == null ? null : getChkSt.trim();
    }

    public String getGetChkRspcode() {
        return getChkRspcode;
    }

    public void setGetChkRspcode(String getChkRspcode) {
        this.getChkRspcode = getChkRspcode == null ? null : getChkRspcode.trim();
    }

    public String getGetChkRspmsg() {
        return getChkRspmsg;
    }

    public void setGetChkRspmsg(String getChkRspmsg) {
        this.getChkRspmsg = getChkRspmsg == null ? null : getChkRspmsg.trim();
    }

    public String getChkFilePath() {
        return chkFilePath;
    }

    public void setChkFilePath(String chkFilePath) {
        this.chkFilePath = chkFilePath == null ? null : chkFilePath.trim();
    }

    public String getChkFileImpSt() {
        return chkFileImpSt;
    }

    public void setChkFileImpSt(String chkFileImpSt) {
        this.chkFileImpSt = chkFileImpSt == null ? null : chkFileImpSt.trim();
    }

    public Date getChkFileWrtm() {
        return chkFileWrtm;
    }

    public void setChkFileWrtm(Date chkFileWrtm) {
        this.chkFileWrtm = chkFileWrtm;
    }

    public Date getChkAcctSttm() {
        return chkAcctSttm;
    }

    public void setChkAcctSttm(Date chkAcctSttm) {
        this.chkAcctSttm = chkAcctSttm;
    }

    public String getChkAcctSt() {
        return chkAcctSt;
    }

    public void setChkAcctSt(String chkAcctSt) {
        this.chkAcctSt = chkAcctSt == null ? null : chkAcctSt.trim();
    }

    public String getChkAcctMsg() {
        return chkAcctMsg;
    }

    public void setChkAcctMsg(String chkAcctMsg) {
        this.chkAcctMsg = chkAcctMsg == null ? null : chkAcctMsg.trim();
    }

    public Date getChkAcctEdtm() {
        return chkAcctEdtm;
    }

    public void setChkAcctEdtm(Date chkAcctEdtm) {
        this.chkAcctEdtm = chkAcctEdtm;
    }

    public String getChkAcctRst() {
        return chkAcctRst;
    }

    public void setChkAcctRst(String chkAcctRst) {
        this.chkAcctRst = chkAcctRst == null ? null : chkAcctRst.trim();
    }

    public String getChkRmk() {
        return chkRmk;
    }

    public void setChkRmk(String chkRmk) {
        this.chkRmk = chkRmk == null ? null : chkRmk.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Date getLstUpdTm() {
        return lstUpdTm;
    }

    public void setLstUpdTm(Date lstUpdTm) {
        this.lstUpdTm = lstUpdTm;
    }
}