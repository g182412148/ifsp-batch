package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class ChoCptMap extends CommonDTO {
    private String cptId;

    private String choId;

    private String isJoin;

    private Date createTime;

    private Date updataTime;

    public String getCptId() {
        return cptId;
    }

    public void setCptId(String cptId) {
        this.cptId = cptId == null ? null : cptId.trim();
    }

    public String getChoId() {
        return choId;
    }

    public void setChoId(String choId) {
        this.choId = choId == null ? null : choId.trim();
    }

    public String getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(String isJoin) {
        this.isJoin = isJoin == null ? null : isJoin.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdataTime() {
        return updataTime;
    }

    public void setUpdataTime(Date updataTime) {
        this.updataTime = updataTime;
    }
}