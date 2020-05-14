package com.scrcu.ebank.ebap.batch.bean.dto;

public class ReturnFileInfo {
    private String mchtId;
    private String dataType;
    private String blNo;
    private String blNo_result;
    private String ownerCertNo;
    private String ownerName;
    private String Name_result;

    public String getMchtId() {
        return mchtId;
    }

    public String getDataType() {
        return dataType;
    }

    public String getBlNo() {
        return blNo;
    }

    public String getBlNo_result() {
        return blNo_result;
    }

    public String getOwnerCertNo() {
        return ownerCertNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getName_result() {
        return Name_result;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setBlNo(String blNo) {
        this.blNo = blNo;
    }

    public void setBlNo_result(String blNo_result) {
        this.blNo_result = blNo_result;
    }

    public void setOwnerCertNo(String ownerCertNo) {
        this.ownerCertNo = ownerCertNo;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setName_result(String name_result) {
        Name_result = name_result;
    }
}
