package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.util.Date;

public class SpecialChoecenessInfo extends CommonDTO {
    private String specialId;

    private String specialName;

    private String specialClassify;

    private String specialDate;

    private String specialParCpt;

    private String specialExhSeq;

    private String specialPicId;

    private String specialState;

    private Date lastUptDate;

    public String getSpecialId() {
        return specialId;
    }

    public void setSpecialId(String specialId) {
        this.specialId = specialId == null ? null : specialId.trim();
    }

    public String getSpecialName() {
        return specialName;
    }

    public void setSpecialName(String specialName) {
        this.specialName = specialName == null ? null : specialName.trim();
    }

    public String getSpecialClassify() {
        return specialClassify;
    }

    public void setSpecialClassify(String specialClassify) {
        this.specialClassify = specialClassify == null ? null : specialClassify.trim();
    }

    public String getSpecialDate() {
        return specialDate;
    }

    public void setSpecialDate(String specialDate) {
        this.specialDate = specialDate == null ? null : specialDate.trim();
    }

    public String getSpecialParCpt() {
        return specialParCpt;
    }

    public void setSpecialParCpt(String specialParCpt) {
        this.specialParCpt = specialParCpt == null ? null : specialParCpt.trim();
    }

    public String getSpecialExhSeq() {
        return specialExhSeq;
    }

    public void setSpecialExhSeq(String specialExhSeq) {
        this.specialExhSeq = specialExhSeq == null ? null : specialExhSeq.trim();
    }

    public String getSpecialPicId() {
        return specialPicId;
    }

    public void setSpecialPicId(String specialPicId) {
        this.specialPicId = specialPicId == null ? null : specialPicId.trim();
    }

    public String getSpecialState() {
        return specialState;
    }

    public void setSpecialState(String specialState) {
        this.specialState = specialState == null ? null : specialState.trim();
    }

    public Date getLastUptDate() {
        return lastUptDate;
    }

    public void setLastUptDate(Date lastUptDate) {
        this.lastUptDate = lastUptDate;
    }
}