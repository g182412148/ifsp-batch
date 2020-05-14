package com.scrcu.ebank.ebap.batch.bean.dto;

import java.util.Date;

public class SettleAccModTask {
    private String id;

    private String mchtId;

    private String settlAcctNameOld;

    private String settlAcctNoOld;

    private String settlAcctTypeOld;

    private String settlAcctOrgIdOld;

    private String settlAcctOrgNameOld;

    private String settlAcctName;

    private String settlAcctNo;

    private String settlAcctType;

    private String settlAcctOrgId;

    private String settlAcctOrgName;

    private Date creTm;

    private Date updTm;

    private String status;

    private String remark;

    public String getId() {
        return id;
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSettlAcctNameOld() {
        return settlAcctNameOld;
    }

    public void setSettlAcctNameOld(String settlAcctNameOld) {
        this.settlAcctNameOld = settlAcctNameOld;
    }

    public String getSettlAcctNoOld() {
        return settlAcctNoOld;
    }

    public void setSettlAcctNoOld(String settlAcctNoOld) {
        this.settlAcctNoOld = settlAcctNoOld;
    }

    public String getSettlAcctTypeOld() {
        return settlAcctTypeOld;
    }

    public void setSettlAcctTypeOld(String settlAcctTypeOld) {
        this.settlAcctTypeOld = settlAcctTypeOld;
    }

    public String getSettlAcctOrgIdOld() {
        return settlAcctOrgIdOld;
    }

    public void setSettlAcctOrgIdOld(String settlAcctOrgIdOld) {
        this.settlAcctOrgIdOld = settlAcctOrgIdOld;
    }

    public String getSettlAcctOrgNameOld() {
        return settlAcctOrgNameOld;
    }

    public void setSettlAcctOrgNameOld(String settlAcctOrgNameOld) {
        this.settlAcctOrgNameOld = settlAcctOrgNameOld;
    }

    public String getSettlAcctName() {
        return settlAcctName;
    }

    public void setSettlAcctName(String settlAcctName) {
        this.settlAcctName = settlAcctName;
    }

    public String getSettlAcctNo() {
        return settlAcctNo;
    }

    public void setSettlAcctNo(String settlAcctNo) {
        this.settlAcctNo = settlAcctNo;
    }

    public String getSettlAcctType() {
        return settlAcctType;
    }

    public void setSettlAcctType(String settlAcctType) {
        this.settlAcctType = settlAcctType;
    }

    public String getSettlAcctOrgId() {
        return settlAcctOrgId;
    }

    public void setSettlAcctOrgId(String settlAcctOrgId) {
        this.settlAcctOrgId = settlAcctOrgId;
    }

    public String getSettlAcctOrgName() {
        return settlAcctOrgName;
    }

    public void setSettlAcctOrgName(String settlAcctOrgName) {
        this.settlAcctOrgName = settlAcctOrgName;
    }

    public Date getCreTm() {
        return creTm;
    }

    public void setCreTm(Date creTm) {
        this.creTm = creTm;
    }

    public Date getUpdTm() {
        return updTm;
    }

    public void setUpdTm(Date updTm) {
        this.updTm = updTm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}