package com.scrcu.ebank.ebap.batch.bean.dto;

import java.util.Date;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class OrderTplInfo extends CommonDTO {
    private String tplSsn;

    private String orderSsn;

    private String subOrderSsn;

    private String subMerNo;

    private String tplType;

    private String tplId;

    private String tplOrderSsn;

    private String tplOrderAmt;

    private Date createTime;

    private Long refoundAmount;

    private Date updateTime;

    public String getTplSsn() {
        return tplSsn;
    }

    public void setTplSsn(String tplSsn) {
        this.tplSsn = tplSsn == null ? null : tplSsn.trim();
    }

    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn == null ? null : orderSsn.trim();
    }

    public String getSubOrderSsn() {
        return subOrderSsn;
    }

    public void setSubOrderSsn(String subOrderSsn) {
        this.subOrderSsn = subOrderSsn == null ? null : subOrderSsn.trim();
    }

    public String getSubMerNo() {
        return subMerNo;
    }

    public void setSubMerNo(String subMerNo) {
        this.subMerNo = subMerNo == null ? null : subMerNo.trim();
    }

    public String getTplType() {
        return tplType;
    }

    public void setTplType(String tplType) {
        this.tplType = tplType == null ? null : tplType.trim();
    }

    public String getTplId() {
        return tplId;
    }

    public void setTplId(String tplId) {
        this.tplId = tplId == null ? null : tplId.trim();
    }

    public String getTplOrderSsn() {
        return tplOrderSsn;
    }

    public void setTplOrderSsn(String tplOrderSsn) {
        this.tplOrderSsn = tplOrderSsn == null ? null : tplOrderSsn.trim();
    }

    public String getTplOrderAmt() {
        return tplOrderAmt;
    }

    public void setTplOrderAmt(String tplOrderAmt) {
        this.tplOrderAmt = tplOrderAmt == null ? null : tplOrderAmt.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getRefoundAmount() {
        return refoundAmount;
    }

    public void setRefoundAmount(Long refoundAmount) {
        this.refoundAmount = refoundAmount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}