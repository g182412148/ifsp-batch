package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;

public class BthBatchAccountFile extends CommonDTO {
    private String id;

    private String accDate;

    private String accFileName;

    private String bkFileName;

    private String fileCount;

    private BigDecimal fileAmt;

    private String fileType;

    private String dealStatus;

    private String genFileClass;

    private String dealFileClass;

    private String batchNo;

    private String reserved1;

    private String reserved2;

    private String reserved3;

    private BigDecimal reserved4;

    private String createDate;

    private String updateDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAccDate() {
        return accDate;
    }

    public void setAccDate(String accDate) {
        this.accDate = accDate == null ? null : accDate.trim();
    }

    public String getAccFileName() {
        return accFileName;
    }

    public void setAccFileName(String accFileName) {
        this.accFileName = accFileName == null ? null : accFileName.trim();
    }

    public String getBkFileName() {
        return bkFileName;
    }

    public void setBkFileName(String bkFileName) {
        this.bkFileName = bkFileName == null ? null : bkFileName.trim();
    }

    public String getFileCount() {
        return fileCount;
    }

    public void setFileCount(String fileCount) {
        this.fileCount = fileCount == null ? null : fileCount.trim();
    }

    public BigDecimal getFileAmt() {
        return fileAmt;
    }

    public void setFileAmt(BigDecimal fileAmt) {
        this.fileAmt = fileAmt;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType == null ? null : fileType.trim();
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus == null ? null : dealStatus.trim();
    }

    public String getGenFileClass() {
        return genFileClass;
    }

    public void setGenFileClass(String genFileClass) {
        this.genFileClass = genFileClass == null ? null : genFileClass.trim();
    }

    public String getDealFileClass() {
        return dealFileClass;
    }

    public void setDealFileClass(String dealFileClass) {
        this.dealFileClass = dealFileClass == null ? null : dealFileClass.trim();
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo == null ? null : batchNo.trim();
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1 == null ? null : reserved1.trim();
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2 == null ? null : reserved2.trim();
    }

    public String getReserved3() {
        return reserved3;
    }

    public void setReserved3(String reserved3) {
        this.reserved3 = reserved3 == null ? null : reserved3.trim();
    }

    public BigDecimal getReserved4() {
        return reserved4;
    }

    public void setReserved4(BigDecimal reserved4) {
        this.reserved4 = reserved4;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate == null ? null : createDate.trim();
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate == null ? null : updateDate.trim();
    }
}